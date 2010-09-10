package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentDAO;
import edacc.model.ExperimentResult;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.SolverConfiguration;
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
public class SolverComparison extends Plot {

    private static int AVERAGE = -2;
    private static int MEDIAN = -1;
    private Dependency[] dependencies;
    private JComboBox combo1, combo2, comboRun;
    private JTextField txtMaxValue;
    private InstanceSelector instanceSelector;
    private String plotTitle;
    public SolverComparison(ExperimentController expController) {
        super(expController);
        combo1 = new JComboBox();
        combo2 = new JComboBox();
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
        dependencies = new Dependency[]{
                    new Dependency("First solver", combo1),
                    new Dependency("Second solver", combo2),
                    new Dependency("Instances", instanceSelector),
                    new Dependency("Plot for run", comboRun),
                    new Dependency("Max x/y-value (sec)", txtMaxValue)
                };
    }

    @Override
    public Dependency[] getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return "Comparison between two solvers";
    }

    @Override
    public void plot(final Rengine re, Vector<PointInformation> pointInformations) throws SQLException, DependencyException {
        super.plot(re, pointInformations);
        if (!(combo1.getSelectedItem() instanceof SolverConfiguration) || !(combo2.getSelectedItem() instanceof SolverConfiguration)) {
            throw new DependencyException("You have to select two solvers.");
        }
        int run = -3;
        if ("average".equals(comboRun.getSelectedItem())) {
            run = AVERAGE;
        } else if ("median".equals(comboRun.getSelectedItem())) {
            run = MEDIAN;
        } else if (comboRun.getSelectedItem() instanceof Integer) {
            run = (Integer) comboRun.getSelectedItem();
        }
        if (run == -3) {
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
            if (run == AVERAGE) {
                Vector<ExperimentResult> resultsX = getResults(xSolverConfig.getId(), instance.getId());
                Vector<ExperimentResult> resultsY = getResults(ySolverConfig.getId(), instance.getId());
                for (int j = resultsX.size() - 1; j >= 0; j--) {
                    if (resultsX.get(j).getStatus() != 1) {
                        resultsX.remove(j);
                    }
                }
                for (int j = resultsY.size() - 1; j >= 0; j--) {
                    if (resultsY.get(j).getStatus() != 1) {
                        resultsY.remove(j);
                    }
                }
                if (resultsX.size() == 0 || resultsY.size() == 0) {
                    continue;
                }
                xsVec.add(new Float(super.getAverageTime(resultsX)));
                ysVec.add(new Float(super.getAverageTime(resultsY)));
            } else if (run == MEDIAN) {
                Vector<ExperimentResult> resultsX = getResults(xSolverConfig.getId(), instance.getId());
                Vector<ExperimentResult> resultsY = getResults(ySolverConfig.getId(), instance.getId());
                for (int j = resultsX.size() - 1; j >= 0; j--) {
                    if (resultsX.get(j).getStatus() != 1) {
                        resultsX.remove(j);
                    }
                }
                for (int j = resultsY.size() - 1; j >= 0; j--) {
                    if (resultsY.get(j).getStatus() != 1) {
                        resultsY.remove(j);
                    }
                }
                if (resultsX.size() == 0 || resultsY.size() == 0) {
                    continue;
                }
                xsVec.add(new Float(super.getMedianTime(resultsX)));
                ysVec.add(new Float(super.getMedianTime(resultsY)));
            } else {
                ExperimentResult xRes = super.getResult(xSolverConfig.getId(), instance.getId(), run);
                ExperimentResult yRes = super.getResult(xSolverConfig.getId(), instance.getId(), run);
                if (xRes.getStatus() == yRes.getStatus() && xRes.getStatus() != 1) {
                    continue;
                }
                xsVec.add(xRes.getTime());
                ysVec.add(yRes.getTime());
            }
        }

        double[] xs = new double[xsVec.size()];
        double[] ys = new double[ysVec.size()];
        for (int i = 0; i < xsVec.size(); i++) {
            xs[i] = xsVec.get(i);
            ys[i] = ysVec.get(i);
        }
        String xlabel = xSolverConfig.toString();
        String ylabel = ySolverConfig.toString();
        String title = xlabel + " vs " + ylabel;
        re.assign("xs", xs);
        re.assign("ys", ys);
        re.assign("maxValue", new double[]{0, maxValue});
        // set margin
        re.eval("par(mar=c(3,3,9,6))");
        
        re.eval("plot(xs, ys, type='p', col='red', las = 1, xlim=c(0," + maxValue + "), ylim=c(0," + maxValue + "), xaxs='i', yaxs='i',xlab='',ylab='',pch=3, tck=0.015, cex.axis=1.2, cex.main=1.5)");
        re.eval("axis(side=4, tck=0.015, las=1, cex.axis=1.2, cex.main=1.5)");
        re.eval("axis(side=3, tck=0.015, las=1, cex.axis=1.2, cex.main=1.5)");
        re.eval("mtext('" + ylabel + "', side=4, line=3, cex=1.2)");
        re.eval("mtext('" + xlabel + "', side=3, padj=0, line=3, cex=1.2)");
        re.eval("mtext('" + title + "', padj=-1.7, side=3, line=3, cex=1.7)");
        re.eval("par(new=1)");
        re.eval("plot(maxValue, maxValue, type='l', col='black', lty=2, xlim=c(0," + maxValue + "), ylim=c(0," + maxValue + "), xaxs='i', yaxs='i',xaxt='n',yaxt='n', xlab='', ylab='')");

        Vector<double[]> points = getPoints(rengine, xs, ys);
        int k = 0;
        for (double[] point : points) {
            pointInformations.add(new PointInformation(point, "<html>" + 
                    xlabel + ": " + (double)Math.round(xs[k]*100)/100 + " sec<br>" +
                    ylabel + ": " + (double)Math.round(ys[k]*100)/100 +" sec<br>" +
                    "Instance: " + instances.get(k).getName()));
            k++;
        }
    }

    @Override
    public void loadDefaultValues() throws SQLException {
        loadMaxValue();
        comboRun.removeAllItems();
        combo1.removeAllItems();
        combo2.removeAllItems();
        for (SolverConfiguration solConfig : ExperimentDAO.getSolverConfigurationsInExperiment(expController.getActiveExperiment())) {
            combo1.addItem(solConfig);
            combo2.addItem(solConfig);
        }
        comboRun.addItem("average");
        comboRun.addItem("median");
        for (Integer i = 0; i < expController.getActiveExperiment().getNumRuns(); i++) {
            comboRun.addItem(i);
        }
        Vector<Instance> instances = new Vector<Instance>();
        instances.addAll(InstanceDAO.getAllByExperimentId(expController.getActiveExperiment().getId()));
        instanceSelector.setInstances(instances);
        instanceSelector.btnSelectAll();
    }

    private void loadMaxValue() throws SQLException {
        double maxValue;
        if (!(combo1.getSelectedItem() instanceof SolverConfiguration) || !(combo2.getSelectedItem() instanceof SolverConfiguration) || !(comboRun.getSelectedItem() instanceof Integer)) {
            maxValue = expController.getActiveExperiment().getTimeOut();
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
            maxValue = expController.getActiveExperiment().getTimeOut();
        }
        txtMaxValue.setText("" + Math.round(maxValue));
    }

    @Override
    public String getTitle() {
        return plotTitle;
    }
}