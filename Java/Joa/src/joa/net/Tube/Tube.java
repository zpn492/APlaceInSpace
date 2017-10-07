
package BrandtTubes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

/**
 * This class provides a standard for reading and writing tubes
 * through the java.io.BufferedReader og -Writer
 * 
 * @author Joa
 */
public class Tube {
    /**
     * Wraping a socket connected to a remote host into a
     * bufferedInputStream & -OutputStream
     * 
     * @param s initialized is set to false upon IOException
     */
    public Tube(Socket s) {
        this.initialized = true;
        try {
            this.br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        } 
        catch(IOException e) {
            System.out.println("BrandtTubes.Tube: "+e.getMessage());
            initialized = false;
        }
    }
    /**
     * Wrapping a java.lang.Process into a
     * bufferedInputStream & -OutputStream
     * 
     * @param s 
     */
    public Tube(Process s) {
        this.initialized = true;
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
    }
    /**
     * Try to read a line from the InputStream
     * 
     * @return null upon EOF or IOException
     */
    public String read() {
        String line = null;
        try {
            line = br.readLine();
        }
        catch(IOException e) {
            System.out.println("BrandtTubes.Tube: "+e.getMessage());
        }
        return line;
    }
    /**
     * Try to write a String message
     * 
     * @param str
     * @return false upon IOException
     */
    public boolean write(String str) {
        try {
            bw.write(str);
            bw.flush();
        }
        catch(IOException e) {
            System.out.println("BrandtTubes.Tube: "+e.getMessage());
            return false;
        }
        return true;
    }
    /**
     * Whether or not the tube is initialized
     * @return false upon constructor failure
     */
    public boolean isInitialized() {
        return this.initialized;
    }
    /**
     * 
     * @return false upon IOException
     */
    public boolean close() {
        boolean success = true;
        try {
            br.close();
            bw.close();
        }
        catch(IOException e) {
            System.out.println("BrandtTubes.Tube: "+e.getMessage());
            success = false;
        }
        return success;
    }
    
    private BufferedReader br;
    private BufferedWriter bw;
    private boolean initialized;
}
