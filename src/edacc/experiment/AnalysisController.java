package edacc.experiment;

import edacc.experiment.plots.Plot;
import edacc.experiment.plots.PlotPanel;
import java.util.HashMap;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public class AnalysisController {
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
        getRengine();
        lastPlotPanel = plotPanel;
        re.eval("JavaGD()");
        plotPanel.setDeviceNumber(getCurrentDeviceNumber());
        plotPanels.put(plotPanel.getDeviceNumber(), plotPanel);
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
        return re.eval("dev.cur()").asInt();
    }

    public static void setCurrentDeviceNumber(int plotDevice) {
        if (re == null || !re.isAlive()) {
            throw new IllegalArgumentException("No rengine.");
        }
        re.eval("dev.set(which = " + plotDevice + ")");
    }

    public static double[] convertPoint(int dev, double[] point) {
        setCurrentDeviceNumber(dev);
        double[] res = new double[2];
        REXP xcoord = re.eval("grconvertX(" + point[0] + ", from = \"user\", to = \"device\")");
        REXP ycoord = re.eval("grconvertY(" + point[1] + ", from = \"user\", to = \"device\")");
        res[0] = xcoord.asDouble();
        res[1] = ycoord.asDouble();
        return res;
    }

    private static void moveDevice(int from, int to) {
        if (from == to) return;
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

    public static void closeDevice(int dev) {
        setCurrentDeviceNumber(dev);
        re.eval("dev.off()");
        plotPanels.remove(dev);

        // rename devices ... resulting dev list is 2, 3, 4, ...
        // this has to be done due to a bug in JavaGD(?)
        int[] devlist = re.eval("dev.list()").asIntArray();
        if (devlist == null || devlist.length == 0) return;
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

    public static int getDeviceCount() {
        if (re == null || !re.isAlive()) {
            throw new IllegalArgumentException("No rengine.");
        }
        int[] res = re.eval("dev.list()").asIntArray();
        return res == null?0:res.length;
    }

    public static void saveToPdf(PlotPanel pnl, String filename) {
        setCurrentDeviceNumber(pnl.getDeviceNumber());
        filename = filename.replace("\\", "\\\\");
        re.eval("dev.print(device = pdf, file = '" + filename + "')");
    }

    /**
     * Tries to set the class of the plot as the current plot type.
     * @param plot
     * @return false, iff setting the plot type failed.
     */
    public static boolean setSelectedPlotType(Plot plot) {
        return analysisPanel==null?false:analysisPanel.setSelectedPlot(plot);
    }
}
