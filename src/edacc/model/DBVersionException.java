package edacc.model;

/**
 *
 * @author simon
 */
public class DBVersionException extends Exception {

    public int currentVersion, localVersion;

    public DBVersionException(int currentVersion, int localVersion) {
        this.currentVersion = currentVersion;
        this.localVersion = localVersion;
    }
}