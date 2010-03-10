package edacc;

/**
 * Implement this interface to use task events. Needed for Tasks.
 * @author simon
 * @see edacc.model.Tasks
 */
public interface EDACCTaskEvents {
    /**
     * Called if a task finished successfully.
     * @param id The id of the task
     * @param result The result of the called method
     */
    public void onTaskSuccessful(int id, Object result);
    /**
     * Called when a task has been started
     * @param id The id of the task
     */
    public void onTaskStart(int id);
    /**
     * Called if a task failed.
     * @param id The id of the task
     * @param e The exception
     */
    public void onTaskFailed(int id, Throwable e);
}
