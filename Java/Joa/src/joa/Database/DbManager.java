package joa.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
/**
 * All the methods is synchronized such that multiple threads can't insert 
 * content into a database at the same time.
 * 
 * E.g.:
 * 
 * DbManager db = new DbManager(new DBMS.SqLite("MyOwnDB.db"));
 * 
 * db.openConnection();
 * 
 * db.extract("SELECT * FROM Person");
 * 
 * db.closeConnection();
 * 
 * @author Joa
 */
public class DbManager {
    /**
     * @param dbManagementSys
     */
    public DbManager(DBMS dbManagementSys) {
        this.running = false;
        this.dbms = dbManagementSys;
    }
    
    /**
     * This method should be called after openConnection()
     * 
     * @param query takes an sql statement and executes it
     * @return resultSet with the result of the sql query
     */
    public ResultSet extract(String query) {
        this.running = true;
        ResultSet rs = null;
        try {
            this.statement = this.connection.createStatement();
            this.resultset = this.statement.executeQuery(query);
            rs = this.resultset;
        } catch (SQLException e) {
            // Make a log
            System.out.println("DbManager.extract: "+e.getMessage());
        } finally {
            this.running = false;
        }
        return rs;
    }
    
    /**
     * This method should be called after openConnection()
     * @param query
     * @param newValue
     * @return a boolean indicating wether or not the insert statement was a success
     */
    synchronized public boolean update(String query, Object[] newValue) {
        this.running = true;
        boolean success = true;
        
        try {
            this.preparedstatement = connection.prepareStatement(query);
            
            for(int i = 0; i < newValue.length; i++) {
                if(newValue[i] instanceof String)
                    this.preparedstatement.setString(i+1, (String)newValue[i]);                    
                if(newValue[i] instanceof Boolean)
                    this.preparedstatement.setBoolean(i+1, ((Boolean)newValue[i]).booleanValue());
                if(newValue[i] instanceof Integer)
                    this.preparedstatement.setInt(i+1, ((Integer)newValue[i]).intValue());
            }
                
            
            this.preparedstatement.executeUpdate();            
        } catch (SQLException e) {
            System.out.println("DbManager.insert: "+e.getMessage());
            success = false;
        } finally {
            this.running = false;
        }
        return success;
    }
    
    
    synchronized public boolean insert(String query) {
        this.running = true;
        boolean success = true;
                
        try {
            statement = connection.createStatement();
            
            String[] tokens = query.split("\n");
            for(String s1 : tokens) {
              statement.addBatch(s1.substring(0, s1.length()-2)+",'"+new Timestamp(System.currentTimeMillis())+"');");
            }
            
            statement.executeBatch();
            
        } catch (SQLException e) {
            System.out.println("DbManager.insert: "+e.getMessage());
            success = false;
        } finally {
            this.running = false;
        }
        return success;
    }
    /**
     * This method should be called after openConnection()
     * 
     * @param attr an array of attributes
     * @param tableName name of the table where the attributes should be inserted
     * @return a boolean indicating wether or not the insert statement was a success
     */
    synchronized public boolean insert(String[] attr, String tableName) {
        this.running = true;
        boolean success = true;
        
        String arguments = "";
        for(int i = 0; i < attr.length; i++) {
            if(i == attr.length-1) arguments += "?";
            else if(attr.length == 0) arguments += "?";
            else arguments += "?, ";
        }
        
        try {
            this.preparedstatement = connection.prepareStatement("INSERT INTO " + 
                    tableName + " values(" + arguments + ");");
            
            for(int i = 0; i < attr.length; i++)
                this.preparedstatement.setString(i+1, attr[i]);
            
            this.preparedstatement.addBatch();
            this.preparedstatement.executeBatch();
            
        } catch (SQLException e) {
            System.out.println("DbManager.insert: "+e.getMessage());
            success = false;
        } finally {
            this.running = false;
        }
        return success;
    }
    
    synchronized public boolean insert(String username, String password) {
        this.running = true;
        boolean success = true;
                
        try {
            statement = connection.createStatement();
            String sql = "INSERT INTO users " + 
                    " values(NULL, NULL, '::1', '" + username + "', '" + username + "', NULL, NULL, NULL, NULL, '" + 
                    password + "', NULL, NULL, NULL, NULL, NULL, 0, NULL);";
            
            statement.executeUpdate(sql);
            
        } catch (SQLException e) {
            System.out.println("DbManager.insert: "+e.getMessage());
            success = false;
        } finally {
            this.running = false;
        }
        return success;
    }
    
    synchronized public boolean insert(String userid, String spset, int i) {
        this.running = true;
        boolean success = true;
                
        try {
            statement = connection.createStatement();
            String sql = "INSERT INTO vt_link (User_Uid, QuestionSet_Id) " + 
                    " values(" + userid + "," + spset + ");";
            
            statement.executeUpdate(sql);
            
        } catch (SQLException e) {
            System.out.println("DbManager.insert: "+e.getMessage());
            success = false;
        } finally {
            this.running = false;
        }
        return success;
    }
    
    synchronized public boolean delete(String query) {
        return true;
    }
    
    /**
     * @return a boolean indicating wether or not the connection was opened
     */
    public boolean openConnection() {
        // Tru to access driver
        try {
            Class.forName(this.dbms.driver());
        } catch (ClassNotFoundException e) {
            // Make a log
            System.out.println("DbManager.openConnection: "+e.getMessage());
            return false;
        }
        // Try to connect
        try {
            String conn = this.dbms.client() + (!this.dbms.host().equals("") ? "://" 
                    + this.dbms.host() + ":" : "") + 
                    (!this.dbms.port().equals("") ? this.dbms.port() + "/" : "") +
                            this.dbms.database();
            this.connection = DriverManager.
                    getConnection(conn, this.dbms.username(), this.dbms.password());
        } catch (SQLException e) {
            // Make a log
            System.out.println("DbManager.openConnection: "+e.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * @return a boolean indicating wether or not the connection was a closed
     */
    public boolean closeConnection() {
        try {
            if(this.resultset != null)  this.resultset.close();
            if(this.statement != null)  this.statement.close(); 
            if(this.connection != null) this.connection.close();
            return true;
        } catch (SQLException e) {
            // Make a log
            System.out.println("DbManager.closeConnection: "+e.getMessage());
            return false;
        }
    }
    
    /**
     * @return a boolean indicating wether or not a statement is executing
     */
    public boolean isRunning() {
        return this.running;
    }
    
    private boolean running;
    
    private final DBMS dbms;
    
    private PreparedStatement preparedstatement;
    private ResultSet resultset;
    private Statement statement;
    private Connection connection;
}
