package edacc.model;

import edacc.EDACCApp;
import edacc.EDACCTaskEvents;
import edacc.EDACCTaskView;
import javax.swing.SwingUtilities;
import org.jdesktop.application.Task;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

/**
 *
 * @author simon
 */
public class Tasks extends org.jdesktop.application.Task<Void, Void> {

    private EDACCTaskEvents view;
    private String methodName;
    private Class[] signature;
    private Object[] parameters;
    private Object target;
    private static Task task;
    private static EDACCTaskView taskView;

    /**
     * Starts a new Task for a method specified in methodName and signature.
     * If there is currently a task running this methode does nothing.
     * @param methodName The name of the method to start
     * @param signature The signature of this method as a array of Class
     * @param parameters The parameters which sould be passed to this method as a array of Object
     * @param target The target object in which this method is declared and set as public.
     * @param view The corresponding view which implements EDACCTaskEvents to have control over this task.
     */
    public static void startTask(String methodName, Class[] signature, Object[] parameters, Object target, EDACCTaskEvents view, boolean withTaskView) {
        if (task != null) {
            return;
        }
        taskView = null;
        task = new Tasks(org.jdesktop.application.Application.getInstance(EDACCApp.class), methodName, signature, parameters, target, view);
        if (withTaskView) {
            taskView = new EDACCTaskView(EDACCApp.getApplication().getMainFrame(), true, (Tasks) task);
            taskView.setResizable(false);
            taskView.setLocationRelativeTo(EDACCApp.getApplication().getMainFrame());
            taskView.setTitle("Running..");
            taskView.setOperationName("Running..");
            taskView.setMessage("");
            taskView.setProgress(0.);
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (taskView.isDisplayable()) {
                            taskView.setVisible(true);
                        }
                    } catch (Exception _) {
                        // happens if the task view is already disposed, i.e. the task is finished.
                    }
                }
            });
        }
        ApplicationContext appC = Application.getInstance().getContext();
        appC.getTaskService().execute(task);
        appC.getTaskMonitor().setForegroundTask(task);
    }

    public static void startTask(String methodName, Class[] signature, Object[] parameters, Object target, EDACCTaskEvents view) {
        startTask(methodName, signature, parameters, target, view, true);
    }
    
    /**
     * Starts a task for a method without any parameters.
     * If there is currently a task running this methode does nothing.
     * @param methodName
     * @param target
     * @param view
     */
    public static void startTask(String methodName, Object target, EDACCTaskEvents view) {
        startTask(methodName, target, view, true);
    }

    public static void startTask(String methodName, Object target, EDACCTaskEvents view, boolean withTaskView) {
        startTask(methodName, new Class[]{}, new Object[]{}, target, view, withTaskView);
    }

    private Tasks(EDACCApp app, String methodName, Class[] signature, Object[] parameters, Object target, EDACCTaskEvents view) {
        super(app);
        this.methodName = methodName;
        this.signature = signature;
        this.parameters = parameters;
        this.target = target;
        this.view = view;
    }

    @Override
    protected Void doInBackground() {
        try {
            for (int i = 0; i < signature.length; i++) {
                if (signature[i] == edacc.model.Tasks.class) {
                    parameters[i] = this;
                }
            }
            view.onTaskStart(methodName);
            Object res = target.getClass().getDeclaredMethod(methodName, signature).invoke(target, parameters);
            if (taskView != null)
              taskView.dispose();
            view.onTaskSuccessful(methodName, res);
        } catch (java.lang.reflect.InvocationTargetException e) {
            if (taskView != null)
              taskView.dispose();
            view.onTaskFailed(methodName, e.getTargetException());
        } catch (Exception e) {
            System.out.println("This should not happen. Called a method which should not be called. Be sure that your method is declared as public. Exception as follows: " + e);
        }
        task = null;
        return null;
    }

    /**
     * Sets the percentage in the progress bar.
     * @param f 0 <= f <= 1
     */
    public void setTaskProgress(float f) {
        if (f < 0f) {
            f = 0f;
        } else if (f > 1.f) {
            f = 1.f;
        }
        this.setProgress(f);
        taskView.setProgress(f * 100);
    }

    /**
     * Sets the status text in the status bar.
     * @param s the status text to be set
     */
    public void setStatus(String s) {
        this.setMessage(s);
        taskView.setMessage(s);
    }

    /**
     * Sets the operation name of the task.
     * @param name the name of the operation
     */
    public void setOperationName(String name) {
        taskView.setOperationName(name);
    }

    /**
     * Returns the current task view.
     * @return the task view
     */
    public static EDACCTaskView getTaskView() {
        return taskView;
    }
}
