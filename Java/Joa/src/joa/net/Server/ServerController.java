package BrandtServer;

/**
 * @author Joa
 */
public abstract class ServerController<T> {
    /**
     * <p> Start listning for clients <br /> 
     * The EntryPoint should decide the program for the client </p>
     * 
     * @param ep EntryPoint
     */
    public void accept(BrandtServer.EntryPoint ep) {
        if(this.ep != null) return;
        this.ep = ep;
        this.ep.start();
    } 
    /**
     * <p> Close server & stop listening for new clients </p>
     */
    public void stop() {
        //if(this.server != null) this.server.close();
        if(this.ep != null) this.ep.close();
    }
    /**
     * @return status for the server thread
     */
    public boolean isRunning() {
        if(this.ep != null) return this.ep.isRunning();
        else return false;
    }
    public T server() {
        if(this.server != null) return this.server;
        else return null;
    }
    public void server(T server) {
        this.server = server;
    }
    public BrandtServer.EntryPoint entryPoint() {
        if(this.ep != null) return this.ep;
        else return null;
    }
    private T server;
    private BrandtServer.EntryPoint ep = null;
}
