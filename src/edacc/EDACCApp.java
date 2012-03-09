/*
 * EDACCApp.java
 */
package edacc;

import edacc.experiment.Util;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class EDACCApp extends SingleFrameApplication {

    private static ErrorLogger logger;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        show(new EDACCView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of EDACCApp
     */
    public static EDACCApp getApplication() {
        return Application.getInstance(EDACCApp.class);
    }

    public static ErrorLogger getLogger() {
        return logger;
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        // clean Updater.class
        File updater = new File(edacc.experiment.Util.getPath() + System.getProperty("file.separator") + "Updater.class");
        if (updater.exists()) {
            updater.delete();
        }
        
        String logPath = edacc.experiment.Util.getPath() + System.getProperty("file.separator") + "edacc_errors.log";
        logger = new ErrorLogger(logPath);
        Thread.setDefaultUncaughtExceptionHandler(logger); // register for all threads
        EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue(); // register for swing stuff
        queue.push(logger);

        launch(EDACCApp.class, args);
    }
}
