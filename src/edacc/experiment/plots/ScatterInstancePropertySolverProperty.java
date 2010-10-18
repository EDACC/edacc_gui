package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentDAO;
import edacc.model.ExperimentResult;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.InstanceProperty;
import edacc.model.SolverConfiguration;
import edacc.model.Property;
import java.awt.geom.Point2D;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JComboBox;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public class ScatterInstancePropertySolverProperty extends Plot {

    private static JComboBox comboInstanceProperty, comboSolverProperty, comboSolver, comboRun;
    private static ScaleSelector scaleSelector;
    private static InstanceSelector instanceSelector;
    private String infos, plotTitle;
    private SolverConfiguration solverConfig;
    private ArrayList<Instance> instances;
    private Property solverProperty;
    private InstanceProperty instanceProperty;
    private Integer run;
    private Boolean xlog, ylog;

    public ScatterInstancePropertySolverProperty(ExperimentController expController) {
        super(expController);
    }

    public static Dependency[] getDependencies() {
        if (comboInstanceProperty == null) {
            comboInstanceProperty = new JComboBox();
        }
        if (comboSolverProperty == null) {
            comboSolverProperty = new JComboBox();
        }
        if (comboSolver == null) {
            comboSolver = new JComboBox();
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
                    new Dependency("Solver", comboSolver),
                    new Dependency("Instance property", comboInstanceProperty),
                    new Dependency("Solver property", comboSolverProperty),
                    new Dependency("Instances", instanceSelector),
                    new Dependency("Plot for run", comboRun),
                    new Dependency("Axes scale", scaleSelector)
                };
    }

    public static void loadDefaultValues(ExperimentController expController) throws SQLException, Exception {
        comboRun.removeAllItems();
        comboInstanceProperty.removeAllItems();
        comboSolverProperty.removeAllItems();
        comboSolver.removeAllItems();
        for (SolverConfiguration solConfig : ExperimentDAO.getSolverConfigurationsInExperiment(expController.getActiveExperiment())) {
            comboSolver.addItem(solConfig);
        }
        comboRun.addItem("all runs - average");
        comboRun.addItem("all runs - median");
        comboRun.addItem("all runs");
        for (Integer i = 0; i < expController.getActiveExperiment().getNumRuns(); i++) {
            comboRun.addItem(i);
        }
        for (Property sProp : getSolverProperties()) {
            comboSolverProperty.addItem(sProp);
        }
        for (InstanceProperty iProp : getInstanceProperties()) {
            comboInstanceProperty.addItem(iProp);
        }
        ArrayList<Instance> instances = new ArrayList<Instance>();
        instances.addAll(InstanceDAO.getAllByExperimentId(expController.getActiveExperiment().getId()));
        instanceSelector.setInstances(instances);
        instanceSelector.btnSelectAll();
    }

    @Override
    public void plot(final Rengine re, ArrayList<PointInformation> pointInformations) throws Exception {

        if (run == null || solverConfig == null || instanceProperty == null || solverProperty == null || instances == null || xlog == null || ylog == null) {
            if (!(comboSolverProperty.getSelectedItem() instanceof Property)) {
                throw new DependencyException("You have to select a solver property.");
            }
            if (!(comboInstanceProperty.getSelectedItem() instanceof InstanceProperty)) {
                throw new DependencyException("You have to select an instance property.");
            }
            if (!(comboSolver.getSelectedItem() instanceof SolverConfiguration)) {
                throw new DependencyException("You have to select a solver.");
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

            solverConfig = (SolverConfiguration) comboSolver.getSelectedItem();
            instanceProperty = (InstanceProperty) comboInstanceProperty.getSelectedItem();
            solverProperty = (Property) comboSolverProperty.getSelectedItem();
            xlog = scaleSelector.isXScaleLog();
            ylog = scaleSelector.isYScaleLog();
        }
        initialize(expController);
        infos = null;
        double ymax = 0.;
        double xmax = 0.;
        plotTitle = solverConfig.getName() + ": " + instanceProperty + " vs. " + solverProperty + " (" + expController.getActiveExperiment().getName() + ")";
        ArrayList<Double> xsVec = new ArrayList<Double>();
        ArrayList<Double> ysVec = new ArrayList<Double>();

        int xNoResult = 0, yNoResult = 0;
        int xNoProp = 0, yNoProp = 0;
        for (Instance instance : instances) {
            if (run == ALLRUNS) {
                ArrayList<ExperimentResult> ysResults = getResults(solverConfig.getId(), instance.getId());
                for (ExperimentResult yres : ysResults) {
                    Double xsValue = this.getValue(instance, instanceProperty);
                    Double ysValue = getValue(yres, solverProperty);
                    if (xsValue == null || ysValue == null) {
                        if (xsValue == null) {
                            xNoProp++;
                        }
                        if (ysValue == null) {
                            yNoProp++;
                        }
                        continue;
                    }
                    if (xsValue > xmax) {
                        xmax = xsValue;
                    }
                    if (ysValue > ymax) {
                        ymax = ysValue;
                    }
                    // add the values and set the point information; the right point coordinates will be set later
                    xsVec.add(xsValue);
                    ysVec.add(ysValue);
                    pointInformations.add(new PointInformation(new Point2D.Double(), "<html>"
                            + instance.getName() + " (" + instanceProperty + "): " + (double) Math.round(xsValue * 100) / 100 + "<br>"
                            + solverConfig.toString() + " (" + solverProperty + "): " + (double) Math.round(ysValue * 100) / 100 + "<br>"
                            + "Run: " + yres.getRun() + "<br>"
                            + "Instance: " + instance.getName()
                            + "</html>"));
                }
            } else if (run == MEDIAN || run == AVERAGE) {
                // get the x/y results and calculate the count of the not verified jobs
                ArrayList<ExperimentResult> ysResults = getResults(solverConfig.getId(), instance.getId());
                Double xsValue = getValue(instance, instanceProperty);
                Double ysValue;
                if (run == MEDIAN) {
                    ysValue = getMedian(ysResults, solverProperty);
                } else {
                    ysValue = getAverage(ysResults, solverProperty);
                }
                if (xsValue == null || ysValue == null) {
                    continue;
                }
                if (xsValue > xmax) {
                    xmax = xsValue;
                }
                if (ysValue > ymax) {
                    ymax = ysValue;
                }
                // add the values and specify the point information
                xsVec.add(xsValue);
                ysVec.add(ysValue);
                pointInformations.add(new PointInformation(new Point2D.Double(), "<html>"
                        + instance.getName() + " (" + instanceProperty + "): " + (double) Math.round(xsValue * 100) / 100 + "<br>"
                        + solverConfig.toString() + " (" + solverProperty + "): " + (double) Math.round(ysValue * 100) / 100 + "<br>"
                        + "Instance: " + instance.getName()
                        + "</html>"));
            } else {
                // get the x/y results, this is for a particular run
                ExperimentResult ysResult = getResult(solverConfig.getId(), instance.getId(), run);

                if (ysResult == null) {
                    yNoResult++;
                    continue;
                }

                Double xsValue = getValue(instance, instanceProperty);
                Double ysValue = getValue(ysResult, solverProperty);
                if (xsValue == null || ysValue == null) {
                    if (xsValue == null) {
                        xNoProp++;
                    }
                    if (ysValue == null) {
                        yNoProp++;
                    }
                    continue;
                }
                if (xsValue > xmax) {
                    xmax = xsValue;
                }
                if (ysValue > ymax) {
                    ymax = ysValue;
                }
                // add the values and specify the point information
                xsVec.add(xsValue);
                ysVec.add(ysValue);
                pointInformations.add(new PointInformation(new Point2D.Double(), "<html>"
                        + instance.getName() + " (" + instanceProperty + "): " + (double) Math.round(xsValue * 100) / 100 + "<br>"
                        + solverConfig.toString() + " (" + solverProperty + "): " + (double) Math.round(ysValue * 100) / 100 + "<br>"
                        + "Instance: " + instance.getName()
                        + "</html>"));
            }
        }
        if (xmax == 0.) {
            xmax = 0.01;
        }
        if (ymax == 0.) {
            ymax = 0.01;
        }
        xmax *= 1.01;
        ymax *= 1.01;
        // min value is needed for logarithmic scale -> cannot be 0
        double xmin = xmax;
        double ymin = ymax;
        double[] xs = new double[xsVec.size()];
        double[] ys = new double[ysVec.size()];
        for (int i = 0; i < xsVec.size(); i++) {
            xs[i] = xsVec.get(i);
            ys[i] = ysVec.get(i);
            if (xs[i] > 0. && xs[i] < xmin) {
                xmin = xs[i];
            }
            if (ys[i] > 0. && ys[i] < ymin) {
                ymin = ys[i];
            }
        }

        String xlabel = instanceProperty.toString();
        String ylabel = solverProperty.toString();
        String title = solverConfig.toString();
        re.assign("xs", xs);
        re.assign("ys", ys);
        if (xlog || ylog) {
            re.assign("x_limits", new double[]{xmin, xmax});
            re.assign("y_limits", new double[]{ymin, ymax});
        } else {
            re.assign("x_limits", new double[]{0, xmax});
            re.assign("y_limits", new double[]{0, ymax});
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
        re.eval("plot(xs, ys, log='" + log + "', type='p', col='red', las = 1, xlim=x_limits, ylim=y_limits, xaxs='i', yaxs='i',xlab='',ylab='',pch=3, tck=0.015, cex.axis=1.2, cex.main=1.5)");
        re.eval("axis(side=4, tck=0.015, las=1, cex.axis=1.2, cex.main=1.5)");
        re.eval("axis(side=3, tck=0.015, las=1, cex.axis=1.2, cex.main=1.5)");
        re.eval("mtext('" + ylabel + "', outer=TRUE, side=4, line=0, cex=1.2)");
        re.eval("mtext('" + xlabel + "', side=3, padj=0, line=2, cex=1.2)");
        re.eval("mtext('" + title + "', side=3, line=4, cex=1.7)");
        re.eval("par(new=1)");
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
        ArrayList<Point2D> points = getPoints(re, xs, ys);
        int k = 0;
        for (Point2D point : points) {
            pointInformations.get(k++).getPoint().setLocation(point);
        }
    }

    @Override
    public String getPlotTitle() {
        return plotTitle;
    }

    public static String getTitle() {
        return "Scatter plot - Result property against instance property";
    }

    @Override
    public String getAdditionalInformations() {
        return infos;
    }

    @Override
    public void updateDependencies() {
        if (solverConfig == null || instances == null || solverProperty == null || instanceProperty == null || run == null || xlog == null || ylog == null) {
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
        comboSolver.setSelectedItem(solverConfig);
        instanceSelector.setSelectedInstances(instances);
        comboSolverProperty.setSelectedItem(solverProperty);
        comboInstanceProperty.setSelectedItem(instanceProperty);
        scaleSelector.setXScaleLog(xlog);
        scaleSelector.setYScaleLog(ylog);
    }
}
