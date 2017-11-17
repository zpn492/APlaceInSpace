package joa.workers;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
/**
 * This class is meant to handle various tasks in parrallel
 * 
 * Example:
 * Employee emp = new Employee();
 * 
 * public class nTask extends joa.workers.Task { 
 *  public nTask(String name, long start, long repeat) {}
 *  @Override
 *  public void begin() { // put int some magic }
 * }
 * 
 * emp.addTask(new nTask("someName", 0, 5000));
 * 
 * @author Joa
 */
public class Employee {
    private static long employees = 0;
    private final static ArrayList<String> OWNER = new ArrayList<>();
    private final static ArrayList<Task> TASKS = new ArrayList<>();
    public Employee(String name) {
        Employee.employees++;
        timer = new Timer();
        empName = name;
    }
    public Employee() {
        Employee.employees++;
        timer = new Timer();
        empName = "Employee"+System.currentTimeMillis();
    }
    public void setFree() {
        Employee.employees--;
        timer.cancel();
    }
    synchronized public void addTask(Task t) {
        String name = empName+System.currentTimeMillis();
        Employee.TASKS.add(t);
        Employee.OWNER.add(name);
        if(t.repeat() < 0)
            timer.schedule(new EmpTask(t, name), t.start()*1000);
        else
            timer.schedule(new EmpTask(t, name), t.start()*1000, t.repeat()*1000);
    }
    synchronized private void removeTask(Task t, String id) 
            throws IndexOutOfBoundsException { 
        Employee.TASKS.remove(t);
        Employee.OWNER.remove(id);
    }
    /**
     * Execute the TimerTask, and remove it from the global tasklist
     * A task is first removed when is finished
     */
    private class EmpTask extends TimerTask {
        public EmpTask(Task t, String id) { this.t = t; this.id = id; }
        @Override
        public void run() {
            t.begin();
            try {
                removeTask(t, id);
            }
            catch(IndexOutOfBoundsException e) {
                System.out.println(e);
            }
        }
        private final Task t;
        private final String id;
    }
    /**
     * @return the number of Employee objects
     */
    public long employees() { return Employee.employees; }
    /**
     * @return the total number of tasks for all Employee objects
     */
    public int numberOfTasks() { return Employee.TASKS.size(); }
    /**
     * Print all active tasks
     */
    public void println() {
        System.out.println("Owner . TaskId . Task");
        
        for(int i = 0; i < Employee.TASKS.size(); i++)
            System.out.println(empName + " - " + Employee.OWNER.get(i) + " - " + 
                    Employee.TASKS.get(i).name());
    }
    private final Timer timer;
    private final String empName;
}
