// Remember to credit: http://dev.mysql.com/downloads/connector/j/
// Licens: http://www.gnu.org/licenses/old-licenses/gpl-2.0.html

package joa.Database;
/**
 *
 * @author Joa
 */
public class MySql extends DBMS {
    /**
     * @param host an ip e.g.: 172.66.21.20
     * @param database e.g.: MyOwnDatabase
     * @param username to connect to the host address
     * @param password to connect to the host address
     */
    public MySql(String host, String database, String username, String password) {
        super();
        
        host(host);
        database(database);
        username(username);
        password(password);
        
        driver("com.mysql.jdbc.Driver");
        client("jdbc:mysql");
        port("3306");
    }
    
    public MySql() {
        super();
        
        driver("com.mysql.jdbc.Driver");
        client("jdbc:mysql");
        port("3306");
    }
}
