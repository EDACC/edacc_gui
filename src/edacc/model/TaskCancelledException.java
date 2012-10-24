package edacc.model;

/**
 *
 * @author simon
 */
public class TaskCancelledException extends Exception {
    public TaskCancelledException() {
        this("Cancelled.");
    }
    public TaskCancelledException(String msg) {
        super (msg);
    }
}
