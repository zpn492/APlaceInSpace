/**
 * A new begining
 */


public class Joa {

    public static void main(String[] args) {
        // Create server
        joa.server.HTTPServer http = new joa.server.HTTPServer(80, "php");
        
        // Start accepting requests
        http.accept((joa.server.EntryPoint)http.new HTTPEntryPoint());
    }
    
}
