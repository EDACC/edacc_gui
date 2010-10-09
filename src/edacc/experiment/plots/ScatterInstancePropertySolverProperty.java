
package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentDAO;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.SolverConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public class ScatterInstancePropertySolverProperty extends Plot {
    private JComboBox comboInstanceProperty, comboSolverProperty, comboSolver, comboRun;
    private JTextField txtMaxValue;
    private InstanceSelector instanceSelector;
    private String plotTitle;
    public ScatterInstancePropertySolverProperty(ExperimentController expController) {
        super(expController);
        comboInstanceProperty = new JComboBox();
        comboSolverProperty = new JComboBox();
        comboSolver = new JComboBox();
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
        comboInstanceProperty.addActionListener(loadMaxValue);
        comboSolverProperty.addActionListener(loadMaxValue);
        txtMaxValue = new JTextField();
        comboRun = new JComboBox();
        comboRun.addActionListener(loadMaxValue);
        instanceSelector = new InstanceSelector();
       /* dependencies = new Dependency[]{
                    new Dependency("Instance property", comboInstanceProperty),
                    new Dependency("Solver property", comboSolverProperty),
                    new Dependency("Solver", comboSolver),
                    new Dependency("Instances", instanceSelector),
                    new Dependency("Plot for run", comboRun),
                    new Dependency("Max x/y-value (sec)", txtMaxValue)
                };*/
    }

    @Override
    public void plot(final Rengine re, ArrayList<PointInformation> pointInformations) throws Exception {
        initializeResults();
        if (comboInstanceProperty.getItemCount() == 0) {
            throw new DependencyException("You have to select two properties.");
        }

        if (!(comboSolver.getSelectedItem() instanceof SolverConfiguration)) {
            throw new DependencyException("You have to select a solver.");
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
        ArrayList<Instance> instances = instanceSelector.getSelectedInstances();
        if (instances == null || instances.isEmpty()) {
            throw new DependencyException("You have to select instances in order to plot.");
        }

        double maxValue;
        try {
            maxValue = Double.parseDouble(txtMaxValue.getText());
        } catch (NumberFormatException ex) {
            throw new DependencyException("Expected double value for max value.");
        }

        SolverConfiguration solverConfig = (SolverConfiguration) comboSolver.getSelectedItem();
       // plotTitle = xSolverConfig.getName() + " vs. " + ySolverConfig.getName() + " (" + expController.getActiveExperiment().getName() + ")";
        ArrayList<Float> xsVec = new ArrayList<Float>();
        ArrayList<Float> ysVec = new ArrayList<Float>();

       /* for (Instance instance : instances) {
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
        }*/

        double[] xs = new double[xsVec.size()];
        double[] ys = new double[ysVec.size()];
        for (int i = 0; i < xsVec.size(); i++) {
            xs[i] = xsVec.get(i);
            ys[i] = ysVec.get(i);
        }
        String xlabel = solverConfig.toString();
        String ylabel = solverConfig.toString();
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

        ArrayList<double[]> points = getPoints(re, xs, ys);
        int k = 0;
        for (double[] point : points) {
            pointInformations.add(new PointInformation(point, "<html>" +
                    xlabel + ": " + (double)Math.round(xs[k]*100)/100 + " sec<br>" +
                    ylabel + ": " + (double)Math.round(ys[k]*100)/100 +" sec<br>" +
                    "Instance: " + instances.get(k).getName()));
            k++;
        }
    }

/*    @Override
    public void loadDefaultValues() throws SQLException {
        loadMaxValue();
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
        ArrayList<Instance> instances = new ArrayList<Instance>();
        instances.addAll(InstanceDAO.getAllByExperimentId(expController.getActiveExperiment().getId()));
        instanceSelector.setInstances(instances);
        instanceSelector.btnSelectAll();
    }
*/
    private void loadMaxValue() throws SQLException {
       /* double maxValue;
        if (!(comboSolver.getSelectedItem() instanceof SolverConfiguration) || !(comboRun.getSelectedItem() instanceof Integer)) {
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
        txtMaxValue.setText("" + Math.round(maxValue));*/
    }

    @Override
    public String getPlotTitle() {
        return plotTitle;
    }

    public static String getTitle() {
        return "Scatter plot - Result property against instance property";
    }
}


