
package BrandtWorkers;

import java.util.TimerTask;

/**
 * This class, has moved the delayedStart & -Repeat from the 
 * java.util.Timer class to be set already at the java.util.TimerTask
 * 
 * @author Joa
 */
public abstract class Task extends TimerTask {
    public Task(String name, long start, long repeat) {
        this.name = name;
        this.start = start < 0 ? 0 : start;
        this.repeat = repeat < 0 ? 0 : repeat;
    } 
    public Task(String name, long start) {
        this.name = name;
        this.start = start < 0 ? 0 : start;
        this.repeat = -1;
    }
    public Task(String name) {
        this.name = name;
        this.start = 0;
        this.repeat = -1;
    }
    public abstract void begin();
    @Override
    public void run() {
        begin();
    } 
    public String name() { return this.name; }
    public long start() { return this.start; }
    public long repeat() { return this.repeat; }
    private final String name;
    private final long start;
    private final long repeat;
}
