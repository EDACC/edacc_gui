package edacc.model;


/**
 *
 * @author simon
 */
public class AlreadyRunningTaskException extends Exception {
    public AlreadyRunningTaskException() {
        this("Application busy. Please wait until last task is completed.");
    }

    public AlreadyRunningTaskException(String msg) {
        super(msg);
    }
}
