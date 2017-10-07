
package BrandtTubes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.UnknownHostException;
/**
 * This class provides static methods to initialize un- and bound remote
 * & listener sockets
 * 
 * @author Joa
 */
public class Sock {    
    /**
     * Provides a ServerSocket, which is listening on port
     * 
     * @param port the which the serverSocket should listen on
     * @return null upon IOException
     */
    public static ServerSocket serverSocket(int port) {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
        }
        catch(IOException e) {
            System.out.println("BrandtTubes.Sock: " + e.getMessage());
        }
        return ss;
    }
    /**
     * Provides a stream socket, connected to a remote host
     * 
     * @param host an ip address for the remote host e.g: 127.0.0.1
     * @param port
     * @return null upon UnknownHost- or IOException
     */
    public static Socket socket(String host, int port) {
        Socket s = null;
        try {
            s = new Socket(host, port);
        }
        catch(UnknownHostException e) {
            System.out.println("BrandtTubes.Sock: "+e);
        }
        catch(IOException e) {
            System.out.println("BrandtTubes.Sock: "+e);
        }
        return s;
    }
    /**
     * Provides a stream socket, connected to a remote host
     * 
     * @param adr an ip address for the remote host e.g: 127.0.0.1
     * @param port
     * @return null upon UnknownHost- or IOException
     */
    public static Socket socket(InetAddress adr, int port) {
        Socket s = null;
        try {
            s = new Socket(adr, port);
        }
        catch(UnknownHostException e) {
            System.out.println("BrandtTubes.Sock: "+e);
        }
        catch(IOException e) {
            System.out.println("BrandtTubes.Sock: "+e);
        }
        return s;
    }
    /**
     * This returns a DatagramSocket, which is bound to a random free port
     * or null, if none is avalible
     * 
     * @return null upon SocketException
     */
    public static DatagramSocket datagramSocket() {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket();
        }
        catch(SocketException e) {
            // Make log
            System.out.println("BrandtTubes.Sock: " +e.getMessage());
        }
        return ds;
    } 
    /**
     * @param port indicate the port which the socket should bind to
     * @return null upon SocketException
     */
    public static DatagramSocket datagramSocket(int port) {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket(port);
        }
        catch(SocketException e) {
            // Make log
            System.out.println("BrandtTubes.Sock: " +e.getMessage());
        }
        return ds;
    }
    /**
     * This method returns an ip address for the domain name
     * Find the ip as String by: whois("www.whois.dk").getHostAddress()
     * 
     * @param domain e.g.: www.whois.dk
     * @return null upon UnknownHostException
     */
    public static InetAddress whois(String domain) {
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(domain);
        }
        catch(UnknownHostException e) {
            System.out.println("BrandtTubes.Sock: " +e.getMessage());
        }
        return ip;
    }
}
