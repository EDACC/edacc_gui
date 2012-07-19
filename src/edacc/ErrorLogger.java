package edacc;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ErrorLogger extends EventQueue implements Thread.UncaughtExceptionHandler {
    public static boolean DEBUG = true; // DEBUG = true => log exceptions to file and to stdout
    File log = null;
    Writer writer = null;

    public ErrorLogger(String logPath) {
        try {
            log = new File(logPath);
            writer = new BufferedWriter(new FileWriter(log, true)); // true for append
        } catch (IOException ex) {
            System.err.println("Can't open edacc_errors.log file.");
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace(System.out);
        logException(e);
    }

    public void logException(Throwable e) {
        //e.printStackTrace();
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        logError(sw.toString());
    }

    public void logError(String error) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            writer.write(dateFormat.format(date) + ":\n");
            writer.write(error);
            writer.write("\n");
            writer.write("=========================================\n\n");
            writer.flush();
        } catch (Exception ex) {
            System.err.println("Can't write exception to the edacc_errors.log file.");
        }
    }

    @Override
    protected void dispatchEvent(AWTEvent newEvent) {
        try {
            super.dispatchEvent(newEvent);
        } catch (Throwable t) {
            logException(t);
            if (DEBUG) {
                t.printStackTrace(System.out);
            }
        }
    }
}
