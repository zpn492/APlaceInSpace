package joa.server;

/**
 * <p> This class should be used as standard
 * for an accepting thread in a server </p>
 * 
 * <p> Example: <br />
 * public class HttpEntryPoint extends EntryPoint {
 *  @Override
 *  public boolean handleRun() {
 *      Accept socket s
 *      Read/Write
 *      Close s
 *      return this.isRunning() // loop while true
 *  }
 * }
 * @author Joa
 */
public abstract class EntryPoint extends Thread implements Runnable {
    /**
     * <p> Do your magic inside this method </p>
     * @return false for thread exit
     */
    public abstract boolean handle();
    /**
     * @return thread status
     */
    public boolean isRunning() {
        return this.running;
    }
    /** 
     * <p> stop thread </p>
     */
    public void close() {
        this.running = false;
    }
    /**
     * <p> Main loop, this should not be changed </p>
     */
    @Override
    public void run() {
        while(this.running) {
            if(!handle())
                break;
        }
        this.running = false;
    }
    // Thread status
    private boolean running = true;
}
