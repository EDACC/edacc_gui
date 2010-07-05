package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentDAO;
import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultDAO;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.SolverConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public class SolverComparison extends Plot {

    private Dependency[] dependencies;
    private JComboBox combo1, combo2, comboRun;
    private JTextField txtMaxValue;
    private InstanceSelector instanceSelector;
    public SolverComparison(ExperimentController expController) {
        super(expController);
        combo1 = new JComboBox();
        combo2 = new JComboBox();
        final ActionListener loadMaxValue = new ActionListener() {

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

    public Dependency[] getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return "Comparison between two solvers";
    }

    public void plot(final Rengine re) throws SQLException, DependencyException {
        if (!(combo1.getSelectedItem() instanceof SolverConfiguration) || !(combo2.getSelectedItem() instanceof SolverConfiguration)) {
            throw new DependencyException("You have to select two solvers.");
        }
        if (!(comboRun.getSelectedItem() instanceof Integer)) {
            throw new DependencyException("You have to select a run.");
        }
        Vector<Instance> instances = instanceSelector.getSelectedInstances();
        if (instances == null || instances.size() == 0) {
            throw new DependencyException("You have to select instances in order to plot.");
        }
        HashSet<Integer> selectedInstanceIds = new HashSet<Integer>();
        for (Instance i: instances) {
            selectedInstanceIds.add(i.getId());
        }
        double maxValue;
        try {
            maxValue = Double.parseDouble(txtMaxValue.getText());
        } catch (NumberFormatException ex) {
            throw new DependencyException("Expected double value for max value.");
        }
        int run = (Integer) comboRun.getSelectedItem();

        SolverConfiguration xSolverConfig = (SolverConfiguration) combo1.getSelectedItem();
        SolverConfiguration ySolverConfig = (SolverConfiguration) combo2.getSelectedItem();
        Vector<ExperimentResult> xResults = ExperimentResultDAO.getAllBySolverConfigurationAndStatus(xSolverConfig, 1);
        Vector<ExperimentResult> yResults = ExperimentResultDAO.getAllBySolverConfigurationAndStatus(ySolverConfig, 1);
        HashMap<RunInstance, ExperimentResult> hashMap = new HashMap<RunInstance, ExperimentResult>();
        for (ExperimentResult erx : xResults) {
            if (erx.getRun() == run && selectedInstanceIds.contains(erx.getInstanceId())) {
                hashMap.put(new RunInstance(erx.getRun(), erx.getInstanceId()), erx);
            }
        }
        Vector<Float> xsVec = new Vector<Float>();
        Vector<Float> ysVec = new Vector<Float>();
        for (ExperimentResult ery : yResults) {
            ExperimentResult erx = hashMap.get(new RunInstance(ery.getRun(), ery.getInstanceId()));
            if (erx != null) {
                xsVec.add(erx.getTime());
                ysVec.add(ery.getTime());
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
        re.assign("marValues", new double[]{3, 3, 10, 6});
        re.eval("par(mar=marValues)");
        re.assign("maxValue", new double[]{0, maxValue});
        re.eval("plot(maxValue, maxValue, type='l', col='black', lty=2, xlim=c(0," + maxValue + "), ylim=c(0," + maxValue + "), xaxs='i', yaxs='i',xaxt='n',yaxt='n', xlab='', ylab='')");
        re.eval("par(new=1)");
        re.eval("plot(xs, ys, type='p', col='red', las = 1, xlim=c(0," + maxValue + "), ylim=c(0," + maxValue + "), xaxs='i', yaxs='i',xlab='',ylab='',pch=3, tck=0.015, cex.axis=1.2, cex.main=1.5)");
        re.eval("axis(side=4, tck=0.015, las=1, cex.axis=1.2, cex.main=1.5)");
        re.eval("axis(side=3, tck=0.015, las=1, cex.axis=1.2, cex.main=1.5)");
        re.eval("mtext('" + ylabel + "', side=4, line=3, cex=1.2)");
        re.eval("mtext('" + xlabel + "', side=3, padj=0, line=3, cex=1.2)");
        re.eval("mtext('" + title + "', padj=-1.7, side=3, line=3, cex=1.7)");
    }

    public void loadDefaultValues() throws SQLException {
        loadMaxValue();
        comboRun.removeAllItems();
        combo1.removeAllItems();
        combo2.removeAllItems();
        for (SolverConfiguration solConfig : ExperimentDAO.getSolverConfigurationsInExperiment(expController.getActiveExperiment())) {
            combo1.addItem(solConfig);
            combo2.addItem(solConfig);
        }
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
}

class RunInstance {

    public int run;
    public int instanceId;

    public RunInstance(int run, int instanceId) {
        this.run = run;
        this.instanceId = instanceId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RunInstance other = (RunInstance) obj;
        if (this.run != other.run) {
            return false;
        }
        if (this.instanceId != other.instanceId) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.run;
        hash = 23 * hash + this.instanceId;
        return hash;
    }
}
