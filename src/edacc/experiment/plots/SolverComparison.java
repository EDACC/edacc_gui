package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentDAO;
import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultDAO;
import edacc.model.SolverConfiguration;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import org.rosuda.JRI.Rengine;

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

/**
 *
 * @author simon
 */
public class SolverComparison implements PlotInterface {

    private Dependency[] dependencies;
    private JComboBox combo1, combo2;
    private JTextField txtMaxValue, txtRun;
    private ExperimentController expController;

    public SolverComparison(ExperimentController expController) {
        this.expController = expController;
        combo1 = new JComboBox();
        combo2 = new JComboBox();
        txtMaxValue = new JTextField();
        txtRun = new JTextField("0");
        dependencies = new Dependency[]{
                    new Dependency("First solver", combo1),
                    new Dependency("Second solver", combo2),
                    new Dependency("Max x/y-value (sec)", txtMaxValue),
                    new Dependency("Plot for run", txtRun)
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
        if (combo1.getSelectedItem() == null || combo2.getSelectedItem() == null) {
            throw new DependencyException("You have to select two solvers.");
        }
        double maxValue;
        int run;
        try {
            maxValue = Double.parseDouble(txtMaxValue.getText());
        } catch (NumberFormatException ex) {
            throw new DependencyException("Expected double value for max value.");
        }
        try {
            run = Integer.parseInt(txtRun.getText());
        } catch (NumberFormatException ex) {
            throw new DependencyException("Expected integer value for run.");
        }

        SolverConfiguration xSolverConfig = (SolverConfiguration) combo1.getSelectedItem();
        SolverConfiguration ySolverConfig = (SolverConfiguration) combo2.getSelectedItem();
        Vector<ExperimentResult> xResults = ExperimentResultDAO.getAllBySolverConfigurationAndStatus(xSolverConfig, 1);
        Vector<ExperimentResult> yResults = ExperimentResultDAO.getAllBySolverConfigurationAndStatus(ySolverConfig, 1);
        HashMap<RunInstance, ExperimentResult> hashMap = new HashMap<RunInstance, ExperimentResult>();
        for (ExperimentResult erx : xResults) {
            if (erx.getRun() == run) {
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
        txtMaxValue.setText("" + expController.getActiveExperiment().getTimeOut());
        txtRun.setText("0");
        combo1.removeAllItems();
        combo2.removeAllItems();
        for (SolverConfiguration solConfig : ExperimentDAO.getSolverConfigurationsInExperiment(expController.getActiveExperiment())) {
            combo1.addItem(solConfig);
            combo2.addItem(solConfig);
        }
    }
}
