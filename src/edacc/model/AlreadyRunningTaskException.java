package edacc.model;


/**
 *
 * @author simon
 */
public class AlreadyRunningTaskException extends Exception {
    public AlreadyRunningTaskException() {
        this("Application busy. Please wait until current task has been completed.");
    }

    public AlreadyRunningTaskException(String msg) {
        super(msg);
    }
}
