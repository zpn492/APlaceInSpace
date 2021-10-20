// Remember to credit: http://jtds.sourceforge.net/license.html
// Licens: http://www.gnu.org/licenses/old-licenses/gpl-2.0.html

package joa.Database;
/**
 *
 * @author Joa
 */
public class MsSql extends DBMS {
    /**
     * @param host an ip e.g.: 172.66.21.20
     * @param database e.g.: MyOwnDatabase
     * @param username to connect to the host address
     * @param password to connect to the host address
     */
    public MsSql(String host, String database, String username, String password) {
        super();
        
        host(host);
        database(database);
        username(username);
        password(password);
        
        driver("net.sourceforge.jtds.jdbc.Driver");
        client("jdbc:jtds:sqlserver");
        port("1433");
    }
}
