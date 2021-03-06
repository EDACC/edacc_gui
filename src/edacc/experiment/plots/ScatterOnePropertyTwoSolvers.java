package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentResult;
import edacc.model.Instance;
import edacc.model.SolverConfiguration;
import edacc.model.Property;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.swing.JComboBox;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public class ScatterOnePropertyTwoSolvers extends Plot {

    private static JComboBox combo1, combo2, comboRun, comboProperty;
    private static InstanceSelector instanceSelector;
    private static ScaleSelector scaleSelector;
    private String infos, plotTitle;
    private List<Instance> instances;
    private SolverConfiguration xSolverConfig, ySolverConfig;
    private Property property;
    private Integer run;
    private Boolean xlog, ylog;

    public ScatterOnePropertyTwoSolvers(ExperimentController expController) {
        super(expController);
    }

    public static Dependency[] getDependencies() {
        if (combo1 == null) {
            combo1 = new JComboBox();
        }
        if (combo2 == null) {
            combo2 = new JComboBox();
        }
        if (comboProperty == null) {
            comboProperty = new JComboBox();
        }
        if (comboRun == null) {
            comboRun = new JComboBox();
        }
        if (instanceSelector == null) {
            instanceSelector = new InstanceSelector();
        }
        if (scaleSelector == null) {
            scaleSelector = new ScaleSelector();
        }
        return new Dependency[]{
                    new Dependency("First solver", combo1),
                    new Dependency("Second solver", combo2),
                    new Dependency("Property", comboProperty),
                    new Dependency("Instances", instanceSelector),
                    new Dependency("Plot for run", comboRun),
                    new Dependency("Axes scale", scaleSelector)
                };
    }

    public static void loadDefaultValues(ExperimentController expController) throws Exception {
        comboRun.removeAllItems();
        combo1.removeAllItems();
        combo2.removeAllItems();
        comboProperty.removeAllItems();
        for (SolverConfiguration solConfig : expController.getSolverConfigurations()) {
            combo1.addItem(solConfig);
            combo2.addItem(solConfig);
        }
        comboRun.addItem("all runs - average");
        comboRun.addItem("all runs - median");
        comboRun.addItem("all runs");
        for (Integer i = 0; i <= expController.getMaxRun(); i++) {
            comboRun.addItem(i);
        }
        for (Property sp : expController.getResultProperties()) {
            comboProperty.addItem(sp);
        }
        instanceSelector.setInstances(expController.getInstances());
        instanceSelector.btnSelectAll();
    }

    @Override
    public void plot(final Rengine re, ArrayList<PointInformation> pointInformations) throws Exception {

        if (run == null || xSolverConfig == null || ySolverConfig == null || property == null || instances == null || xlog == null || ylog == null) {
            if (!(comboProperty.getSelectedItem() instanceof Property)) {
                throw new DependencyException("You have to select a property.");
            }

            if (!(combo1.getSelectedItem() instanceof SolverConfiguration) || !(combo2.getSelectedItem() instanceof SolverConfiguration)) {
                throw new DependencyException("You have to select two solvers.");
            }
            run = -4;
            if ("all runs - average".equals(comboRun.getSelectedItem())) {
                run = AVERAGE;
            } else if ("all runs - median".equals(comboRun.getSelectedItem())) {
                run = MEDIAN;
            } else if ("all runs".equals(comboRun.getSelectedItem())) {
                run = ALLRUNS;
            } else if (comboRun.getSelectedItem() instanceof Integer) {
                run = (Integer) comboRun.getSelectedItem();
            }
            if (run == -4) {
                throw new DependencyException("You have to select a run.");
            }
            instances = instanceSelector.getSelectedInstances();
            if (instances == null || instances.isEmpty()) {
                throw new DependencyException("You have to select instances in order to plot.");
            }

            xSolverConfig = (SolverConfiguration) combo1.getSelectedItem();
            ySolverConfig = (SolverConfiguration) combo2.getSelectedItem();
            property = (Property) comboProperty.getSelectedItem();
            xlog = scaleSelector.isXScaleLog();
            ylog = scaleSelector.isYScaleLog();
        }

        expController.getExperimentResults().updateExperimentResults();
        infos = null;
        double maxValue = 0.;
        plotTitle = xSolverConfig.getName() + " vs. " + ySolverConfig.getName() + " (" + expController.getActiveExperiment().getName() + ")";
        ArrayList<Double> xsVec = new ArrayList<Double>();
        ArrayList<Double> ysVec = new ArrayList<Double>();

        int xNoResult = 0, yNoResult = 0;
        int xNoProp = 0, yNoProp = 0;
        for (Instance instance : instances) {
            if (run == ALLRUNS) {
                // get all x results in a hashmap
                ArrayList<ExperimentResult> xsTmp = expController.getExperimentResults().getResults(xSolverConfig.getId(), instance.getId());
                HashMap<Integer, ExperimentResult> xsResults = new HashMap<Integer, ExperimentResult>();
                for (ExperimentResult xres : xsTmp) {
                    xsResults.put(xres.getRun(), xres);
                }
                // get the y results
                ArrayList<ExperimentResult> ysResults = expController.getExperimentResults().getResults(ySolverConfig.getId(), instance.getId());


                for (ExperimentResult yres : ysResults) {
                    // look the x result for the y result up
                    ExperimentResult xres = xsResults.get(yres.getRun());
                    // remove the x result from the hashmap -> then we can calculate how many y results are not verified as successful
                    xsResults.remove(yres.getRun());
                    // x result is not verified as successful
                    if (xres == null) {
                        xNoResult++;
                        continue;
                    }
                    // get the values; if the properties are currently not calculated this will result in null
                    Double xsValue = expController.getValue(xres, property);
                    Double ysValue = expController.getValue(yres, property);
                    if (xsValue == null || ysValue == null) {
                        if (xsValue == null) {
                            xNoProp++;
                        }
                        if (ysValue == null) {
                            yNoProp++;
                        }
                        continue;
                    }
                    if (xsValue > maxValue) {
                        maxValue = xsValue;
                    }
                    if (ysValue > maxValue) {
                        maxValue = ysValue;
                    }
                    // add the values and set the point information; the right point coordinates will be set later
                    xsVec.add(xsValue);
                    ysVec.add(ysValue);
                    pointInformations.add(new PointInformation(new Point2D.Double(xsValue, ysValue), "<html>"
                            + xSolverConfig.toString() + ": " + (double) Math.round(xsValue * 100) / 100 + "<br>"
                            + ySolverConfig.toString() + ": " + (double) Math.round(ysValue * 100) / 100 + "<br>"
                            + "Run: " + xres.getRun() + "<br>"
                            + "Instance: " + instance.getName()
                            + "</html>"));
                }
                // for these we don't have a y result, therefore they are not verified as successful
                yNoResult += xsResults.size();
            } else if (run == MEDIAN || run == AVERAGE) {
                // get the x/y results and calculate the count of the not verified jobs
                ArrayList<ExperimentResult> xsResults = expController.getExperimentResults().getResults(xSolverConfig.getId(), instance.getId());
                ArrayList<ExperimentResult> ysResults = expController.getExperimentResults().getResults(ySolverConfig.getId(), instance.getId());

                HashSet<Integer> tmp = new HashSet<Integer>();
                for (ExperimentResult res : xsResults) {
                    tmp.add(res.getRun());
                }

                for (ExperimentResult res : ysResults) {
                    if (!tmp.contains(res.getRun())) {
                        xNoResult++;
                    } else {
                        tmp.remove(res.getRun());
                    }
                }

                yNoResult += tmp.size();
                // get the values if the ArrayLists contain data
                Double xsValue;
                Double ysValue;
                if (run == MEDIAN) {
                    xsValue = getMedian(xsResults, property);
                    ysValue = getMedian(ysResults, property);
                } else {
                    xsValue = getAverage(xsResults, property);
                    ysValue = getAverage(ysResults, property);
                }
                if (xsValue == null || ysValue == null) {
                    continue;
                }
                if (xsValue > maxValue) {
                    maxValue = xsValue;
                }
                if (ysValue > maxValue) {
                    maxValue = ysValue;
                }
                // add the values and specify the point information
                xsVec.add(xsValue);
                ysVec.add(ysValue);
                pointInformations.add(new PointInformation(new Point2D.Double(xsValue, ysValue), "<html>"
                        + xSolverConfig.toString() + ": " + (double) Math.round(xsValue * 100) / 100 + "<br>"
                        + ySolverConfig.toString() + ": " + (double) Math.round(ysValue * 100) / 100 + "<br>"
                        + "Instance: " + instance.getName()
                        + "</html>"));
            } else {
                // get the x/y results, this is for a particular run
                ExperimentResult xsResult = expController.getExperimentResults().getResult(xSolverConfig.getId(), instance.getId(), run);
                ExperimentResult ysResult = expController.getExperimentResults().getResult(ySolverConfig.getId(), instance.getId(), run);

                if (xsResult == null || ysResult == null) {
                    if (xsResult == null) {
                        xNoResult++;
                    }
                    if (ysResult == null) {
                        yNoResult++;
                    }
                    continue;
                }

                Double xsValue = expController.getValue(xsResult, property);
                Double ysValue = expController.getValue(ysResult, property);
                if (xsValue == null || ysValue == null) {
                    if (xsValue == null) {
                        xNoProp++;
                    }
                    if (ysValue == null) {
                        yNoProp++;
                    }
                    continue;
                }
                if (xsValue > maxValue) {
                    maxValue = xsValue;
                }
                if (ysValue > maxValue) {
                    maxValue = ysValue;
                }
                // add the values and specify the point information
                xsVec.add(xsValue);
                ysVec.add(ysValue);
                pointInformations.add(new PointInformation(new Point2D.Double(xsValue, ysValue), "<html>"
                        + xSolverConfig.toString() + ": " + (double) Math.round(xsValue * 100) / 100 + "<br>"
                        + ySolverConfig.toString() + ": " + (double) Math.round(ysValue * 100) / 100 + "<br>"
                        + "Instance: " + instance.getName()
                        + "</html>"));
            }
        }
        if (maxValue == 0.) {
            maxValue = 0.01;
        }
        maxValue *= 1.01;
        // min value is needed for logarithmic scale -> cannot be 0
        double minValue = maxValue;
        double[] xs = new double[xsVec.size()];
        double[] ys = new double[ysVec.size()];
        for (int i = 0; i < xsVec.size(); i++) {
            xs[i] = xsVec.get(i);
            ys[i] = ysVec.get(i);
            if (xs[i] > 0. && xs[i] < minValue) {
                minValue = xs[i];
            }
            if (ys[i] > 0. && ys[i] < minValue) {
                minValue = ys[i];
            }
        }

        String xlabel = xSolverConfig.toString();
        String ylabel = ySolverConfig.toString();
        String title = xlabel + " vs " + ylabel;
        re.assign("xs", xs);
        re.assign("ys", ys);
        if (xlog || ylog) {
            re.assign("limits", new double[]{minValue, maxValue});
        } else {
            re.assign("limits", new double[]{0, maxValue});
        }

        // set margin
        re.eval("par(mar=c(2, 5, 5, 5) + 0.1, oma=c(0,0,1,2) )");

        String log = "";
        if (ylog) {
            log += "y";
        }
        if (xlog) {
            log += "x";
        }
        re.eval("plot(xs, ys, log='" + log + "', type='p', col='red', las = 1, xlim=limits, ylim=limits, xaxs='i', yaxs='i',xlab='',ylab='',pch=3, tck=0.015, cex.axis=1.2, cex.main=1.5)");
        re.eval("axis(side=4, tck=0.015, las=1, cex.axis=1.2, cex.main=1.5)");
        re.eval("axis(side=3, tck=0.015, las=1, cex.axis=1.2, cex.main=1.5)");
        re.eval("mtext('" + ylabel + "', outer=TRUE, side=4, line=0, cex=1.2)");
        re.eval("mtext('" + xlabel + "', side=3, padj=0, line=2, cex=1.2)");
        re.eval("mtext('" + title + "', side=3, line=4, cex=1.7)");
        re.eval("par(new=1)");

        // plot diagonal line
        if (!log.equals("")) {
            re.eval("x <- seq(0.01," + maxValue + ")");
            re.eval("plot(x, x, log='" + log + "', type='l', col='black', lty=2, xlim=limits, ylim=limits, xaxs='i', yaxs='i',xaxt='n',yaxt='n', xlab='', ylab='')");
        } else {
            re.eval("plot(limits, limits, type='l', col='black', lty=2, xlim=limits, ylim=limits, xaxs='i', yaxs='i',xaxt='n',yaxt='n', xlab='', ylab='')");
        }

        double[] spearman = Statistics.spearmanCorrelation(re, "xs", "ys");
        infos = htmlHeader;
        if (spearman != null) {
            infos += "<h2>Spearman Rank correlation coefficient</h2>"
                    + "Correlation Coefficient: " + spearman[1] + "<br>"
                    + "p-value: " + spearman[0] + "<br>";
        } else {
        }
        double[] pearson = Statistics.pearsonCorrelation(re, "xs", "ys");
        if (pearson != null) {
            infos += "<h2>Pearson product-moment correlation coefficient</h2>"
                    + "Correlation Coefficient: " + pearson[1] + "<br>"
                    + "p-value: " + pearson[0] + "<br>";
        } else {
        }
        if (xNoResult > 0 || yNoResult > 0 || xNoProp > 0 || yNoProp > 0) {
            infos += "<h2>Warning</h2>";
            infos += "Some points could not be calculated or might be inaccurate (for median or average):<br>";
            if (xNoResult > 0) {
                infos += xSolverConfig.getName() + " was not successfully verified on " + xNoResult + " runs.<br>";
            }
            if (xNoProp > 0) {
                infos += "" + xNoProp + " properties are not calculated for " + xSolverConfig.getName() + "<br>";
            }
            if (yNoResult > 0) {
                infos += ySolverConfig.getName() + " was not successfully verified on " + yNoResult + " runs.<br>";
            }
            if (yNoProp > 0) {
                infos += "" + yNoProp + " properties are not calculated for " + ySolverConfig.getName() + "<br>";
            }
        }

        infos += htmlFooter;
    }

    @Override
    public String getPlotTitle() {
        return plotTitle;
    }

    public static String getTitle() {
        return "Scatter plot - One result property of two solvers";
    }

    @Override
    public String getAdditionalInformations() {
        return infos;
    }

    @Override
    public void updateDependencies() {
        if (xSolverConfig == null || ySolverConfig == null || instances == null || property == null || run == null || xlog == null || ylog == null) {
            return;
        }
        if (run == AVERAGE) {
            comboRun.setSelectedItem(AVERAGE_TEXT);
        } else if (run == MEDIAN) {
            comboRun.setSelectedItem(MEDIAN_TEXT);
        } else if (run == ALLRUNS) {
            comboRun.setSelectedItem(ALLRUNS);
        } else {
            comboRun.setSelectedItem(run);
        }
        combo1.setSelectedItem(xSolverConfig);
        combo2.setSelectedItem(ySolverConfig);
        instanceSelector.setSelectedInstances(instances);
        comboProperty.setSelectedItem(property);
        scaleSelector.setXScaleLog(xlog);
        scaleSelector.setYScaleLog(ylog);
    }
}
