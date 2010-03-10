package edacc.model;

import edacc.EDACCApp;
import edacc.EDACCTaskEvents;
import org.jdesktop.application.Task;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

/**
 *
 * @author simon
 */
public class Tasks extends org.jdesktop.application.Task<Object, Void> {

    private int id;
    private EDACCTaskEvents view;
    private String methodName;
    private Class[] signature;
    private Object[] parameters;
    private Object target;
    private static Task task;

    /**
     * Starts a new Task for a method specified in methodName and signature.
     * @param methodName The name of the method to start
     * @param signature The signature of this method as a array of Class
     * @param parameters The parameters which sould be passed to this method as a array of Object
     * @param target The target object in which this method is declared and set as public.
     * @param view The corresponding view which implements EDACCTaskEvents to have control over this task.
     * @param id An id which is passed to the task events.
     * @throws AlreadyRunningTaskException
     */
    public static void startTask(String methodName, Class[] signature, Object[] parameters, Object target, EDACCTaskEvents view, int id) throws AlreadyRunningTaskException {
        if (task != null) {
            throw new AlreadyRunningTaskException();
        }
        task = new Tasks(org.jdesktop.application.Application.getInstance(EDACCApp.class), methodName, signature, parameters, target,view,id);
        ApplicationContext appC = Application.getInstance().getContext();
        appC.getTaskService().execute(task);
        appC.getTaskMonitor().setForegroundTask(task);
    }

    /**
     * Start a task for a method without any parameters.
     * @param methodName
     * @param target
     * @param view
     * @param id
     * @throws AlreadyRunningTaskException
     */
    public static void startTask(String methodName, Object target,EDACCTaskEvents view, int id) throws AlreadyRunningTaskException {
        startTask(methodName, new Class[] {}, new Object[] {}, target,view,id);
    }

    private Tasks(EDACCApp app,String methodName, Class[] signature, Object[] parameters,Object target, EDACCTaskEvents view, int id) {
        super(app);
        this.id = id;
        this.methodName = methodName;
        this.signature = signature;
        this.parameters = parameters;
        this.target = target;
        this.view = view;
    }

    @Override
    protected Object doInBackground() {
        try {
            this.setMessage("Initializing Task ..");
            for (int i = 0; i < signature.length; i++) {
                if (signature[i] == edacc.model.Tasks.class) {
                    parameters[i] = this;
                }
            }
            view.onTaskStart(id);
            this.setMessage("Task running");
            Object res = target.getClass().getDeclaredMethod(methodName, signature).invoke(target, parameters);
            view.onTaskSuccessful(id,res);
            this.setMessage("Task finished");
        } catch (java.lang.reflect.InvocationTargetException e) {
            view.onTaskFailed(id, e.getTargetException());
        } catch (Exception e) {
            System.out.println("This should not happen. Called a method which should not be called. Be sure that your method is declared as public. Exception as follows: " +e);
        }
        task = null;
        return null;
    }

    /**
     * Sets the percentage in the progress bar.
     * @param f
     */
    public void setTaskProgress(float f) {
        this.setProgress(f);
    }

    /**
     * Sets the status text in the status bar.
     * @param s the status text to be set
     */
    public void setStatus(String s) {
        this.setMessage(s);
    }
    
}
