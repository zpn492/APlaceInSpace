package joa.archives;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import joa.Database.DBMS;
import joa.Database.DbManager;


/**
 * E.g.:
FileLoader fl = new FileLoader();
ArrayList<DbManager> dbs = fl.reader("config.txt");

dbs.get(0).openConnection();
java.sql.ResultSet rs = dbs.get(0).extract("select * from Job");
try {
while(rs.next())
    System.out.println(rs.getString(1));
}
catch(Exception e) {
    System.out.println(e);
}
dbs.get(0).closeConnection();
 */

/**
 * FileFormat:
 * % comment
 * config:nameOfValue:value
 * #{type:server:databse:username:password}
 * #{type:#:#:#:database}
 * 
 * % Types in {SqLite, MsSql, MySql}
 * @author Joa
 */
public class ConfigLoader {
    
    public ConfigLoader() {
        this.configValues = new HashMap<>(); 
    }
    
    /**
     * @param filePath Path to file which should be read
     * @return an array of DbManagers - null upon error
     */
    public ArrayList<DbManager> reader(String filePath) {
        ArrayList<DbManager> managers = new ArrayList<>();
        try {
            BufferedReader re = new BufferedReader(new FileReader(filePath));
            
            String line = re.readLine();
            DBMS dbms = null;
            while(line != null) {
                if(line.contains("%")) {
                    line = re.readLine();
                    continue;
                }
                if(line.startsWith("#")) {
                   managers.add(new DbManager(parseDb(line)));
                }
                else if(line.startsWith("config:")) {
                    String[] tokens = line.split(":");
                    String name = tokens[1];
                    String value = line.substring(line.indexOf(tokens[1])+tokens[1].length()+1);
                    this.configValues.put(name, value);
                }
                
                // do something with line
                line = re.readLine();
            }
            re.close();
            return managers;
        }
        catch(FileNotFoundException e) {
            // The specified file could not be found
            System.out.println(e);
        }
        catch(IOException e) {
            // Something went wrong with reader or closing
            System.out.println(e);
        }
        return null;
    }
    
    private DBMS parseDb(String args) {
        DBMS dbms;
        
        /**
         * tokens[0] = type
         * tokens[1] = server
         * tokens[2] = database
         * tokens[3] = username
         * tokens[4] = password
         */
        String[] tokens = args.substring(2, args.length()-1).split(":");
        
        try {
            dbms = (DBMS) createObject("OhrtDatabase."+tokens[0]);
            dbms.host(parseArg(tokens[1]));
            dbms.database(parseArg(tokens[2]));
            dbms.username(parseArg(tokens[3]));
            dbms.password(tokens[4]);
            return dbms;
        }
        catch(Exception e)  {
            System.out.println("Database type not supported: "+tokens[0]);
            return null;
        }
        
    }
        
    private String parseArg(String arg) {
        return arg.equals("#") ? "" : arg;
    }
    
    private Object createObject(String className) {
      Object object = null;
      try {
          Class classDefinition = Class.forName(className);
          object = classDefinition.newInstance();
      } catch (InstantiationException e) {
          System.out.println(e);
      } catch (IllegalAccessException e) {
          System.out.println(e);
      } catch (ClassNotFoundException e) {
          System.out.println(e);
      }
      return object;
   }
    
   private String schedulerquery;
   private String updateProcessSuccess;
   private String updateProcessFailure;
   private HashMap<String, String> configValues;
}
