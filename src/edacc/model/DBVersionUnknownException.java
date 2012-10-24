package edacc.model;

/**
 *
 * @author simon
 */
public class DBVersionUnknownException extends Exception {
    public int localVersion;
    
    public DBVersionUnknownException(int localVersion) {
        this.localVersion = localVersion;
    }
}
