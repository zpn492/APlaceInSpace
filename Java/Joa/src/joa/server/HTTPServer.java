package joa.server;

import joa.tubes.Sock;
import joa.tubes.Tube;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Class HTTPServer
 * Handles incoming socket connections & creates a HTTPHandler,
 * which will receives the client request, and respond accordingly
 * 
 * This class contains static fields and function such as:
 * 
 * http reponse codes as:
 * 404 - File Not Found
 * 206 - Parital Content
 * 200 - Ok(file found)
 * 
 * http headers as:
 * Accept-Ranges
 * Content-Lenght
 * Content-Range
 * Connecion
 * Content-Type
 * 
 * MIMEs as:
 * text/html
 * video/mp4
 * audio/mp3
 * text/javascript
 * application/pdf
 * 
 * Chunksize of 0.1 MB (104857 bytes)
 * Where chunksize is for every HTTPHandler
 * the maxium amount of bytes, a file is divided into,
 * where every chunk is loaded into the memory and afterwards sent through the socket.
 * where the HTTPHandler will keep the socket open until every chunk is sent,
 * such as the entire file is received by the client, 
 * or the client has broken the connection.
 * 
 * Example:
 * HttpServer httpserver = new HttpServer(80, "php");  // Create server
 * httpserver.accept(httpserver.new HTTPEntryPoint()); // Listen for requests
 * httpserver.stop(); // Stop listening, this will stop running requests
 * 
 * @version 26.03.2015
 * @author Joa
 */
public class HTTPServer extends ServerController<ServerSocket> {
    public HTTPServer(int port, String indexFileType) {
        ServerSocket s = Sock.serverSocket(port);
        server(s);
        switch(indexFileType) {
            case "html":
                indexFile = "index.html";
                break;
            case "htm":
                indexFile = "index.htm";
            case "php":
                indexFile = "index.php";
                break;
            default:
                indexFile = "index.html";
                break;
        }
    }
    
    public class HTTPEntryPoint extends joa.server.EntryPoint {
        @Override
        public boolean handle() {
            try {
                if(HTTPHandlers < 10) {
                    java.net.Socket s = server().accept();
                    new HTTPHandler(s).start();
                }
            }
            catch(IOException e) {
                System.out.println("BrandtServer.HTTPServer " +e.getMessage());
            }
            return true; // Continue to listen for clients
        }
    }
    
    /**
     * InnerClass HTTPHandler
     * Intialized for every http request received from clients
     * 
     * @version 11.11.2015
     * @author Joa
     */
    private class HTTPHandler extends Thread {

        public HTTPHandler(java.net.Socket s) {
            client = new Tube(s);
            socket = s;
            HTTPServer.HTTPHandlers++;
            headers = new HashMap<>();
            phpResponse = "";
        }

        @Override
        public void run() {
            // Handle Client Request
            String line = client.read();        
            // tokens[0] = GET|POST
            // tokens[1] = FILE
            // tokens[2] = HTTP/1.1|HTTP/1.0
            String[] tokens = line.split(" ");
            
            // Get all headers
            String[] header;
            while(true) {
                // Debug print
                if(HTTPServer.debug) System.out.println(line);

                if(line.equals("") || line.equals("\n"))
                    break;
                line = client.read();
                // header[0] : key
                // header[1] : value
                header = line.split(": ");
                if(header.length == 2)
                    headers.put(header[0], header[1]);
            }

            /* Replace dummy space ... */
            tokens[1] = tokens[1].replaceAll("%20", " ");
            
            // Separate query
            String get = "";
            if(tokens[1].contains("?")) {
                // Get Query
                get = tokens[1].substring(tokens[1].indexOf("?")+1);
                // File request
                tokens[1] = tokens[1].substring(0, tokens[1].indexOf("?"));
            }
            
            tokens[1] = tokens[1].equals("/") ? "/"+HTTPServer.indexFile : tokens[1];

            // Find content type
            String type = tokens[1].substring(tokens[1].indexOf(".")+1);

            final String filepath = HTTPServer.folder + tokens[1];
            final String getQuery = get;
            
            // Debug print
            if(HTTPServer.debug) System.out.println("Get: " + get + " File: "+filepath);
            
            // Retrieve file
            arc = new joa.archives.Archive(filepath);
            
            // Interpret php file
            if(type.equals("php") && arc.isInitialized()) {
                php = true;
                // Create list of arguments
                List<String> args = new ArrayList<String>() {
                    { 
                        add((!getQuery.equals("") ? HTTPServer.phpCgiPath : HTTPServer.phpPath)); 
                        add((!getQuery.equals("") ? "-f" : ""));
                        add(filepath);
                        add((!getQuery.equals("") ? getQuery : ""));
                    }
                };
                // Initialize a process for interpreting the .php file
                Process p = joa.tubes.SysProcess.createProcess(args);
                joa.tubes.Tube cp = new joa.tubes.Tube(p);
                
                phpResponse = "";
                while( (line = cp.read()) != null) {
                    phpResponse += line;
                }
                // Close bufferedreader
                cp.close();
                // Kill the process
                p.destroy();
            }

            // Indlæs postdata
            // Do something

            // Set Range information, if Any
            if(headers.containsKey("Range"))
                setRangeIfAny();

            // HTTP Response 404 Page Not Found
            if(!arc.isInitialized()) {
                arc = new joa.archives.Archive("pagenotfound.html");
                range = false;
                client.write(HTTPServer.httpResponse404());
                client.write(HTTPServer.headerContentLenght(""+arc.size()));
            }
            // HTTP Response 206 Partial Content
            else if(range) {
                client.write(HTTPServer.httpResponse206());
                client.write(HTTPServer.headerContentLenght(""+partialsize));
                client.write(HTTPServer.headerContentRange(from+"-"+to+"/"+(arc.size()+1)));
            }
            // HTTP Response 200 OK
            else {
                client.write(HTTPServer.httpResponse200());
                client.write(HTTPServer.headerContentLenght(""+(
                        php ? phpResponse.length() : arc.size())));
            }
            type = types.containsKey(type) ? types.get(type) : "text/html";

            // Connection close
            client.write(HTTPServer.headerConnection());

            client.write(HTTPServer.headerContentType(type));

            if(range)
                client.write(HTTPServer.headerAcceptRanges());

            client.write(HTTPServer.newline());

            // Send den tilhørende fil
            if(!php) {
                int read = range ? from : 0;

                long length = range ? partialsize-1 : arc.size();

                int buf = CHUNKSIZE;   
                int now = read+(int)length;
                while(read < now) {
                    buf = read+buf >= arc.size() ? (int)arc.size() - read : buf;
                    try {
                        socket.getOutputStream().write(arc.readPart(read, buf+1));
                        socket.getOutputStream().flush();
                        read += buf+1;
                    }
                    catch(IOException e) {
                        if(HTTPServer.debug) System.out.println("Socket write error: "+e.getMessage());
                        break;
                    }
                }        
            }
            else {
                client.write(phpResponse);
            }
            // Close file and bufferedreader &- write
            arc.close();
            client.close();
            // Close client socket
            try {
                socket.close();
            }
            catch(IOException e) {
                if(HTTPServer.debug) System.out.println("Client was forced to shutdown "+e.getMessage());
            }
            HTTPServer.HTTPHandlers--;
        }

        /**
         * from-to/partialsize
         */
        private void setRangeIfAny() {
                String[] header;
                // header[0] : from || empty
                // header[1] : to   || empty
                String temp = headers.get("Range");
                // bytes = from-to
                temp = temp.substring(temp.indexOf("=")+1);
                header = temp.split("-");
                // Set from to zero, if none is displayed
                from = header[0].isEmpty() ? 0 : Integer.parseInt(header[0]);
                to = header.length == 2 ? Integer.parseInt(header[1]) : (int)arc.size();
                partialsize = (to - from)+1;
                range = true;
        }
        
        private final Tube client;
        private final java.net.Socket socket;
        private final HashMap<String, String> headers;
        private boolean range = false, php = false;
        private int from = 0, to = 0, partialsize = 0;
        private joa.archives.Archive arc;
        private String phpResponse;
    }
    
    public static final String httpResponse200() { return "HTTP/1.1 200 OK\r\n"; }
    public static final String httpResponse206() { return "HTTP/1.1 206 Partial Content\r\n"; }
    public static final String httpResponse404() { return "HTTP/1.1 404 File Not Found\r\n"; }
    public static final String headerContentLenght(String l) { return "Content-Length: "+l+"\r\n"; }
    public static final String headerContentRange(String r) { return "Content-Range: bytes "+r+"\r\n"; }
    public static final String headerAcceptRanges() { return "Accept-Ranges: bytes\r\n"; }
    public static final String headerContentType(String t) { return "Content-Type: "+t+"\r\n"; }
    public static final String headerConnection() { return "Connection: close\r\n"; }
    public static final String newline() { return "\r\n"; }
    
    public void setDebugOn(boolean doDebug) { debug = doDebug; }
    
    public static String indexFile;
    public static boolean debug = false;
    public static final String phpPath = "php\\php.exe";
    public static final String phpCgiPath = "php\\php-cgi.exe";
    public static final String folder = "public_html";

    public static long HTTPHandlers = 0;

    // CHUNKSIZE, is the greatest part of a file, which
    // can be loaded into mememory for each http reqeust
    public static final int CHUNKSIZE = 104857;

    /**
     * Allowed Content Types
     */
    public static final Map<String, String> types = new HashMap<String, String>() 
        {
            {
                put("html", "text/html");
                put("png", "image/png");
                put("gif", "image/gif");
                put("jpeg", "image/jpeg");
                put("jpg", "image/jpeg");
                put("css", "text/css");
                put("mp3", "audio/mp3");
                put("js", "text/javascript");
                put("min", "text/javascript");
                put("pdf", "application/pdf");
                put("mp4", "video/mp4");
                put("ogg", "video/ogg");
            }
        };
}
