package BrandtTubes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.ArrayList;

/**
 * version 10-10-2015
 * @author Joa
 */
public class SysProcess {
    /**
     * Starts a process
     * @param prog e.g.: nodepad.exe or cmd.exe
     */
    public SysProcess(String prog) {
        List<String> arg = new ArrayList<>(); arg.add(prog);
        ownp = createProcess(arg);
        stdout = new BufferedWriter(new OutputStreamWriter(ownp.getOutputStream()));
        stdin = new BufferedReader(new InputStreamReader(ownp.getInputStream()));
    }
    
    /**
     * Constructor for a process which is already running 
     * @param p java.lang.process
     */
    public SysProcess(Process p) {
        ownp = p;
        stdout = new BufferedWriter(new OutputStreamWriter(ownp.getOutputStream()));
        stdin = new BufferedReader(new InputStreamReader(ownp.getInputStream()));
    }
    /**
     * Send a command to the process
     * @param com
     * @return false upon IOException
     */
    public boolean sendLine(String com) {
        try {
            stdout.write(com);
            stdout.newLine();
            stdout.flush();
        }
        catch(IOException e) {
            // Make log
            return false;
        }
        return true;
    }
    /**
     * Try to receive a line from the process
     * Remeber to call the close() before this method is called
     * @return null upon IOException or EOS(End Of Stream)
     */
    public String recvLine() {
        String line = null;
        try {
            line = stdin.readLine();
        }
        catch(IOException e) {
            // Make log
        }
        return line;
    } 
    /**
     * This method will send a exit command to the process writer
     * This should be done, before any printing, reading for stdin.
     */
    public void close() {
        sendLine("exit");
    }
    /**
     * Try to shutdown the process &
     * Close stdin and stdout.
     * 
     * @return false upon IOException
     */
    public boolean shutdown() {
        try {
            stdin.close();
            stdout.close();
            ownp.destroy();
        }
        catch(IOException e) {
            return false;
        }
        return true;
    }
    /**
     * 
     * @return this.process
     */
    public Process process() {
        return this.ownp;
    }
    
    /**
     * This method will return a running system process, or null on error
     * @param arg e.g.: {php\\php.exe, -f, page.php}
     * @return null on error
     */
    public static Process createProcess(List<String> arg) {
        Process p = null;
        try {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(arg);
            p = pb.start();
        }
        catch (IOException e) {
            // Make log
            System.out.println(e);
        }
        return p;
    }
    /**
     * The method will print the outputstream in the process
     * @param p an active process
     */
    public static void println(Process p) {
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while(true) {
            try {
                line = r.readLine();
                if (line == null) { break; }
            }
            catch(IOException e) {
                System.out.println(e);
            }
        }
        try {
            r.close();
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }
    /**
     * The ProcessHandler is an abstract class with a handle(Process) method
     * this method can be Overrided to fullfill a specific job
     * 
     * @param p process to be handled
     * @param ph ProcessHandler 
     */
    public static void processStream(Process p, ProcessHandler ph) {
        ph.handle(p);
    }
    /**
     * This is an abstract class with a handle(Process) method
     * this method can be Overrided to fullfill a specific job
     */
    public static abstract class ProcessHandler {
        public abstract void handle(Process p);
    }
    
    private final Process ownp;
    private final BufferedWriter stdout;
    private final BufferedReader stdin;
}

