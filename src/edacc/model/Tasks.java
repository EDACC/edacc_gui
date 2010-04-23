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
     * @param methodName The name of the method to start
     * @param signature The signature of this method as a array of Class
     * @param parameters The parameters which sould be passed to this method as a array of Object
     * @param target The target object in which this method is declared and set as public.
     * @param view The corresponding view which implements EDACCTaskEvents to have control over this task.
     * @param id An id which is passed to the task events.
     * @throws AlreadyRunningTaskException
     */
    public static void startTask(String methodName, Class[] signature, Object[] parameters, Object target, EDACCTaskEvents view) {
        if (task != null) {
            return;
        }
        task = new Tasks(org.jdesktop.application.Application.getInstance(EDACCApp.class), methodName, signature, parameters, target, view);
        taskView = new EDACCTaskView(EDACCApp.getApplication().getMainFrame(), true);
        taskView.setResizable(false);
        taskView.setLocationRelativeTo(EDACCApp.getApplication().getMainFrame());
        taskView.setTitle("Running..");
        taskView.setMessage("");
        taskView.setProgress(0.);
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    taskView.setVisible(true);
                } catch (Exception e) {
                }
            }
        });
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
    public static void startTask(String methodName, Object target, EDACCTaskEvents view) {
        startTask(methodName, new Class[]{}, new Object[]{}, target, view);
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
            taskView.dispose();
            view.onTaskSuccessful(methodName, res);
        } catch (java.lang.reflect.InvocationTargetException e) {
            taskView.dispose();
            view.onTaskFailed(methodName, e.getTargetException());
        } catch (Exception e) {
            System.out.println("This should not happen. Called a method which should not be called. Be sure that your method is declared as public. Exception as follows: " + e);
        }
        task = null;

        //taskView = null;
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
}
