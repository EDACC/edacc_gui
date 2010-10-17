package edacc.experiment;

import edacc.experiment.plots.Plot;
import edacc.experiment.plots.PlotPanel;
import java.awt.geom.Point2D;
import java.util.HashMap;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public class AnalysisController {

    public static final Object syncR = new Object();
    public static PlotPanel lastPlotPanel; // for the RPlotDevice .. see edacc.model.RPlotDevice.java
    public static AnalysisPanel analysisPanel;
    private static Rengine re;
    private static HashMap<Integer, PlotPanel> plotPanels = new HashMap<Integer, PlotPanel>();

    public static void checkForR() throws REngineInitializationException {
        try {
            System.loadLibrary("jri");
        } catch (Throwable e) {
            throw new REngineInitializationException(e.getMessage());
        }
    }

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

    public static Rengine getRengine() throws REngineInitializationException {
        try {
            if (re == null || !re.isAlive()) {
                if (!Rengine.versionCheck()) {
                    throw new REngineInitializationException("** Version mismatch - Java files don't match library version.");
                }
                re = new Rengine(new String[]{}, false, null);

                if (!re.waitForR()) {
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
     * @return
     */
    public static boolean setCurrentDeviceNumber(int plotDevice) {
        if (re == null || !re.isAlive()) {
            return false;
        }
        synchronized (syncR) {
            return re.eval("dev.set(which = " + plotDevice + ")").asInt() == plotDevice;
        }
    }

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

    public static void closeDevice(int dev) {
        synchronized (syncR) {
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

    public static void saveToPdf(PlotPanel pnl, String filename) {
        synchronized (syncR) {
            setCurrentDeviceNumber(pnl.getDeviceNumber());
            filename = filename.replace("\\", "\\\\");
            re.eval("dev.print(device = pdf, file = '" + filename + "')");
        }
    }

    public static void saveToEps(PlotPanel pnl, String filename) {
        synchronized (syncR) {
            setCurrentDeviceNumber(pnl.getDeviceNumber());
            filename = filename.replace("\\", "\\\\");
            re.eval("dev.print(device = eps, file = '" + filename + "')");
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
