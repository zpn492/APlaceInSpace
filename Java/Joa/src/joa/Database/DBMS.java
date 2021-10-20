package joa.Database;


/**
 * This class is a common denominator, for all database management systems(DBMS).
 * Each DBMS like MYSQL, SqLite or MsSQL will extend this class and fill out the necessary fields.
 * 
 * @author Joa
 */
public class DBMS {
    /**
     * @return an ip e.g.: 172.66.21.20
     */
    public String host() { return this.host; }
    
    /**
     * @param host an ip e.g.: 172.66.21.20
     */
    public void host(String host) { this.host = host; }
    
    /**
     * @return the common port for the database management system 
     */
    public String port() { return this.port; }
    
    /**
     * @param port e.g. Microsoft SQL Server common port is: 1433
     */
    public void port(String port) { this.port = port; }
    
    public String username() { return this.username; }
    
    /**
     * @param username to connect to the host address
     */
    public void username(String username) { this.username = username; }
    
    public String password () { return this.password; }
    
    /**
     * @param password to connect to the host address
     */
    public void password(String password) { this.password = password; }
    
    public String driver () { return this.driver; }
    
    /**
     * The driver is normaly descriped in its respective JAR library
     * Its the bridge between the JAVA program and the database management system.
     * 
     * @param driver e.g.: a JDBC driver: net.sourceforge.jtds.jdbc.Driver 
     */
    public void driver(String driver) { this.driver = driver; }
    
    public String client() { return this.client;}
    
    /**
     * The client descripes the chosen database management system 
     * like Microsoft SQL Server.
     * @param client e.g.: jdbc:jtds:sqlserver
     */
    public void client(String client) { this.client = client; }
    
    public String database() { return this.database; }
    
    /**
     * The actual name of the database
     * @param database e.g.: SqLite: MyOwnDatabase.db
     */
    public void database(String database) { this.database = database; }
    
    private String host;
    private String port;
    private String username;
    private String password;
    private String driver;
    private String client;
    private String database;
}
