package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentDAO;
import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultStatus;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.SolverConfiguration;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public class ScatterOnePropertyTwoSolvers extends Plot {

    private Dependency[] dependencies;
    private JComboBox combo1, combo2, comboProperty, comboRun;
    private JTextField txtMaxValue;
    private InstanceSelector instanceSelector;
    private ScaleSelector scaleSelector;
    private String plotTitle;

    public ScatterOnePropertyTwoSolvers(ExperimentController expController) {
        super(expController);
        combo1 = new JComboBox();
        combo2 = new JComboBox();
        comboProperty = new JComboBox();
        final ActionListener loadMaxValue = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    loadMaxValue();
                } catch (SQLException ex) {
                    txtMaxValue.setText("0");
                }
            }
        };
        combo1.addActionListener(loadMaxValue);
        combo2.addActionListener(loadMaxValue);
        txtMaxValue = new JTextField();
        comboRun = new JComboBox();
        comboRun.addActionListener(loadMaxValue);
        instanceSelector = new InstanceSelector();
        scaleSelector = new ScaleSelector();
        dependencies = new Dependency[]{
                    new Dependency("First solver", combo1),
                    new Dependency("Second solver", combo2),
                    new Dependency("Property", comboProperty),
                    new Dependency("Instances", instanceSelector),
                    new Dependency("Plot for run", comboRun),
                    new Dependency("Max x/y-value (sec)", txtMaxValue),
                    new Dependency("Axes scale", scaleSelector)
                };
    }

    @Override
    public Dependency[] getDependencies() {
        return dependencies;
    }

    @Override
    public void plot(final Rengine re, Vector<PointInformation> pointInformations) throws SQLException, DependencyException {
        super.plot(re, pointInformations);
        if (comboProperty.getItemCount() == 0) {
            throw new DependencyException("You have to select a property.");
        }

        if (!(combo1.getSelectedItem() instanceof SolverConfiguration) || !(combo2.getSelectedItem() instanceof SolverConfiguration)) {
            throw new DependencyException("You have to select two solvers.");
        }
        int run = -4;
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
        Vector<Instance> instances = instanceSelector.getSelectedInstances();
        if (instances == null || instances.size() == 0) {
            throw new DependencyException("You have to select instances in order to plot.");
        }

        double maxValue;
        try {
            maxValue = Double.parseDouble(txtMaxValue.getText());
        } catch (NumberFormatException ex) {
            throw new DependencyException("Expected double value for max value.");
        }

        SolverConfiguration xSolverConfig = (SolverConfiguration) combo1.getSelectedItem();
        SolverConfiguration ySolverConfig = (SolverConfiguration) combo2.getSelectedItem();
        plotTitle = xSolverConfig.getName() + " vs. " + ySolverConfig.getName() + " (" + expController.getActiveExperiment().getName() + ")";
        Vector<Float> xsVec = new Vector<Float>();
        Vector<Float> ysVec = new Vector<Float>();

        for (Instance instance : instances) {
            try {
                if (run == ALLRUNS) {
                    Vector<ExperimentResult> xRes = getResults(xSolverConfig.getId(), instance.getId());
                    Vector<ExperimentResult> yRes = getResults(ySolverConfig.getId(), instance.getId());
                    if (xRes.size() != yRes.size()) {
                        continue;
                    }
                    for (int i = 0; i < xRes.size(); i++) {
                        if (xRes.get(i).getStatus() == ExperimentResultStatus.SUCCESSFUL && yRes.get(i).getStatus() == ExperimentResultStatus.SUCCESSFUL) {
                            xsVec.add(xRes.get(i).getResultTime());
                            ysVec.add(yRes.get(i).getResultTime());
                            pointInformations.add(new PointInformation(new double[]{0, 0}, "<html>" +
                                    xSolverConfig.toString() + ": " + (double) Math.round(xRes.get(i).getResultTime() * 100) / 100 + " sec<br>" +
                                    ySolverConfig.toString() + ": " + (double) Math.round(yRes.get(i).getResultTime() * 100) / 100 + " sec<br>" +
                                    "Run: " + xRes.get(i).getRun() + "<br>" +
                                    "Instance: " + instance.getName()));
                        }
                    }
                } else {
                    float xsTime = getResultValue(xSolverConfig.getId(), instance.getId(), run).floatValue();
                    float ysTime = getResultValue(ySolverConfig.getId(), instance.getId(), run).floatValue();
                    xsVec.add(xsTime);
                    ysVec.add(ysTime);
                    pointInformations.add(new PointInformation(new double[]{0, 0}, "<html>" +
                            xSolverConfig.toString() + ": " + (double) Math.round(xsTime * 100) / 100 + " sec<br>" +
                            ySolverConfig.toString() + ": " + (double) Math.round(ysTime * 100) / 100 + " sec<br>" +
                            "Instance: " + instance.getName()));
                }
            } catch (Exception e) {
            }
        }
        if (maxValue == 0.) {
            maxValue = 0.01;
        }
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
        if (scaleSelector.isXScaleLog() || scaleSelector.isYScaleLog()) {
            re.assign("limits", new double[]{minValue, maxValue});
        } else {
            re.assign("limits", new double[]{0, maxValue});
        }

        // set margin
        re.eval("par(mar=c(2, 5, 5, 5) + 0.1, oma=c(0,0,1,2) )");
        
        String log = "";
        if (scaleSelector.isYScaleLog()) {
            log += "y";
        }
        if (scaleSelector.isXScaleLog()) {
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
        
        Vector<double[]> points = getPoints(rengine, xs, ys);
        int k = 0;
        for (double[] point : points) {
            pointInformations.get(k).getPoint()[0] = point[0];
            pointInformations.get(k).getPoint()[1] = point[1];
            k++;
        }
    }

    @Override
    public void loadDefaultValues() throws Exception {
        loadMaxValue();
        comboRun.removeAllItems();
        combo1.removeAllItems();
        combo2.removeAllItems();
        comboProperty.removeAllItems();
        for (SolverConfiguration solConfig : ExperimentDAO.getSolverConfigurationsInExperiment(expController.getActiveExperiment())) {
            combo1.addItem(solConfig);
            combo2.addItem(solConfig);
        }
        comboRun.addItem("all runs - average");
        comboRun.addItem("all runs - median");
        comboRun.addItem("all runs");
        for (Integer i = 0; i < expController.getActiveExperiment().getNumRuns(); i++) {
            comboRun.addItem(i);
        }
        comboProperty.addItem("CPU-Time");
        Vector<Instance> instances = new Vector<Instance>();
        instances.addAll(InstanceDAO.getAllByExperimentId(expController.getActiveExperiment().getId()));
        instanceSelector.setInstances(instances);
        instanceSelector.btnSelectAll();
    }

    private void loadMaxValue() throws SQLException {
        double maxValue;
        if (!(combo1.getSelectedItem() instanceof SolverConfiguration) || !(combo2.getSelectedItem() instanceof SolverConfiguration) || !(comboRun.getSelectedItem() instanceof Integer)) {
            maxValue = expController.getActiveExperiment().getCPUTimeLimit();
        } else {
            int run = (Integer) comboRun.getSelectedItem();
            maxValue = expController.getMaxCalculationTimeForSolverConfiguration((SolverConfiguration) combo1.getSelectedItem(), 1, run);
            double tmp = expController.getMaxCalculationTimeForSolverConfiguration((SolverConfiguration) combo2.getSelectedItem(), 1, run);
            if (tmp > maxValue) {
                maxValue = tmp;
            }
            maxValue *= 1.1;
        }
        if (maxValue == 0.) {
            maxValue = expController.getActiveExperiment().getCPUTimeLimit();
        }
        txtMaxValue.setText("" + Math.round(maxValue));
    }

    @Override
    public String getTitle() {
        return plotTitle;
    }

    @Override
    public String toString() {
        return "Scatter plot - One result property of two solvers";
    }
}
