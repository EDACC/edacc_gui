package edacc.experiment;

import edacc.experiment.plots.Plot;
import edacc.experiment.plots.PlotPanel;
import java.awt.geom.Point2D;
import java.util.HashMap;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 * This class implements some functions for the connection between java and r.
 * @author simon
 */
public class AnalysisController {

    public static final Object syncR = new Object();
    public static PlotPanel lastPlotPanel; // for the RPlotDevice .. see edacc.model.RPlotDevice.java
    public static AnalysisPanel analysisPanel;
    private static Rengine re;
    private static HashMap<Integer, PlotPanel> plotPanels = new HashMap<Integer, PlotPanel>();

    /**
     * Basically this looks if the jri-library can be loaded.
     * @throws REngineInitializationException if the library could not be loaded.
     */
    public static void checkForR() throws REngineInitializationException {
        try {
            System.loadLibrary("jri");
        } catch (Throwable e) {
            throw new REngineInitializationException(e.getMessage());
        }
    }

    /**
     * Creates a new device and sets the device number of the plot.
     * @param plotPanel the plot panel for which we need a device
     * @return the r engine
     * @throws REngineInitializationException if creating a new r engine failed
     */
    public static Rengine getREngine(PlotPanel plotPanel) throws REngineInitializationException {
        synchronized (syncR) {
            getRengine();
            lastPlotPanel = plotPanel;
            re.eval("JavaGD()");
            plotPanel.setDeviceNumber(getCurrentDeviceNumber());
            plotPanels.put(plotPanel.getDeviceNumber(), plotPanel);
        }
        return re;
    }

    /**
     * Returns a r engine. If there is no living r engine, it will try to create a new one.
     * @return the r engine
     * @throws REngineInitializationException if creating a new r engine failed.
     */
    public static Rengine getRengine() throws REngineInitializationException {
        try {
            if (re == null || !re.isAlive()) {
                if (!Rengine.versionCheck()) {
                    throw new REngineInitializationException("** Version mismatch - Java files don't match library version.");
                }
                re = new Rengine(new String[]{"--vanilla"}, false, null);

                if (!re.waitForR()) {
                    re = null;
                    throw new REngineInitializationException("Cannot load R.");
                }
                if (re.eval("library(JavaGD)") == null) {
                    re.end();
                    re = null;
                    throw new REngineInitializationException("Did not find JavaGD.");
                }
                re.eval("Sys.putenv('JAVAGD_CLASS_NAME'='edacc/model/RPlotDevice')");
            }
        } catch (Exception ex) {
            throw new REngineInitializationException(ex.getMessage());
        }
        return re;
    }

    /**
     * Returns the current device of the r engine.
     * @return the device id
     */
    public static int getCurrentDeviceNumber() {
        if (re == null || !re.isAlive()) {
            throw new IllegalArgumentException("No rengine.");
        }
        synchronized (syncR) {
            return re.eval("dev.cur()").asInt();
        }
    }

    /**
     * Tries to set the device number plotDevice. If it fails it returns false.
     * @param plotDevice
     * @return false, if there is no r engine, true otherwise
     */
    public static boolean setCurrentDeviceNumber(int plotDevice) {
        if (re == null || !re.isAlive()) {
            return false;
        }
        synchronized (syncR) {
            return re.eval("dev.set(which = " + plotDevice + ")").asInt() == plotDevice;
        }
    }

    /**
     * Converts the given point from user coordinates to device coordinates on the specified device.
     * @param dev the device
     * @param point the point
     * @return a new point with the converted coordinates
     */
    public static Point2D convertPoint(int dev, Point2D point) {
        REXP xcoord;
        REXP ycoord;
        synchronized (syncR) {
            setCurrentDeviceNumber(dev);
            xcoord = re.eval("grconvertX(" + point.getX() + ", from = \"user\", to = \"device\")");
            ycoord = re.eval("grconvertY(" + point.getY() + ", from = \"user\", to = \"device\")");
        }
        return new Point2D.Double(xcoord.asDouble(), ycoord.asDouble());

    }

    private static void moveDevice(int from, int to) {
        synchronized (syncR) {
            if (from == to) {
                return;
            }
            PlotPanel pnl = plotPanels.get(from);
            plotPanels.put(to, pnl);
            plotPanels.remove(from);
            setCurrentDeviceNumber(from);
            re.eval("dev.copy(device = JavaGD, " + to + ")");
            setCurrentDeviceNumber(from);
            re.eval("dev.off()");
            pnl.gdc.setDeviceNumber(to);
            pnl.gdc.initRefresh();
        }
    }

    /**
     * Closes the R device with that id.
     * @param dev
     */
    public static void closeDevice(int dev) {
        synchronized (syncR) {
            // close the device and remove the panel from the hashmap
            setCurrentDeviceNumber(dev);
            re.eval("dev.off()");
            plotPanels.remove(dev);

            // rename devices ... resulting dev list is 2, 3, 4, ...
            // this has to be done due to a bug in JavaGD(?)
            int[] devlist = re.eval("dev.list()").asIntArray();
            if (devlist == null || devlist.length == 0) {
                return;
            }
            int cur = 2;
            int idx = 0;
            while (idx < devlist.length) {
                if (devlist[idx] != cur) {
                    plotPanels.get(devlist[idx]).setDeviceNumber(cur);
                    moveDevice(devlist[idx], cur);
                }
                cur++;
                idx++;
            }
        }
    }

    /**
     * Returns the number of devices in the R engine.
     * It calls `dev.list()` and returns the length.
     * Throws an exception if there is no living R engine.
     * @return
     */
    public static int getDeviceCount() {
        if (re == null || !re.isAlive()) {
            throw new IllegalArgumentException("No rengine.");
        }
        int[] res;
        synchronized (syncR) {
            res = re.eval("dev.list()").asIntArray();
        }
        return res == null ? 0 : res.length;
    }

    /**
     * Saves the image of the panel to a filename in the pdf format using R.
     * @param pnl the panel
     * @param filename the filename
     */
    public static void saveToPdf(PlotPanel pnl, String filename) {
        synchronized (syncR) {
            setCurrentDeviceNumber(pnl.getDeviceNumber());
            filename = filename.replace("\\", "\\\\");
            re.eval("dev.print(device = pdf, file = '" + filename + "')");
        }
    }

    /**
     * Saves the image of the panel to a filename in the eps format using R.
     * @param pnl the panel
     * @param filename the filename
     */
    public static void saveToEps(PlotPanel pnl, String filename) {
        synchronized (syncR) {
            setCurrentDeviceNumber(pnl.getDeviceNumber());
            filename = filename.replace("\\", "\\\\");
            re.eval("setEPS()");
            re.eval("dev.print(device = postscript, file = '"+filename+"')");
            // TODO: revert setEPS()
        }
    }

    /**
     * Tries to set the class of the plot as the current plot type.
     * @param plot
     * @return false, iff setting the plot type failed.
     */
    public static boolean setSelectedPlotType(Plot plot) {
        return analysisPanel == null ? false : analysisPanel.setSelectedPlot(plot);
    }
}
