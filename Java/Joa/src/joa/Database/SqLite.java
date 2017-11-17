// Remember to credit: https://bitbucket.org/xerial/sqlite-jdbc
// Licens: http://www.gnu.org/licenses/old-licenses/gpl-2.0.html

package joa.Database;

/**
 *
 * @author Joa
 */
public class SqLite extends DBMS {
    /**
     * @param databasePath e.g.: MyOwnDatabase.db
     */
    public SqLite(String databasePath) {
        super();
        
        host("");
        database(databasePath);
        username("");
        password("");
        
        driver("org.sqlite.JDBC");
        client("jdbc:sqlite:");
        port("");
    }
}
