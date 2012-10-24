package edacc.events;

/**
 * Implement this interface to use task events.
 * @author simon
 * @see edacc.model.Tasks
 */
public interface TaskEvents {
    /**
     * Called if a task finished successfully.
     * @param methodName the name of the method which finished successfully
     * @param result The result of the called method
     */
    public void onTaskSuccessful(String methodName, Object result);
    /**
     * Called when a task has been started
     * @param methodName the name of the method which started
     */
    public void onTaskStart(String methodName);
    /**
     * Called if a task failed.
     * @param methodName the name of the method which failed
     * @param e The exception
     */
    public void onTaskFailed(String methodName, Throwable e);
}
