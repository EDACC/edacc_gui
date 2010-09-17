package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentDAO;
import edacc.model.ExperimentResult;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.SolverConfiguration;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JComboBox;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public class RTDPlot extends Plot {

    private Dependency[] dependencies;
    private JComboBox combo1, combo2, comboInstance;

    public RTDPlot(ExperimentController expController) {
        super(expController);
        combo1 = new JComboBox();
        combo2 = new JComboBox();
        comboInstance = new JComboBox();
        dependencies = new Dependency[]{
                    new Dependency("First solver", combo1),
                    new Dependency("Second solver", combo2),
                    new Dependency("Instance", comboInstance)
                };
    }

    @Override
    public void loadDefaultValues() throws Exception {
        combo1.removeAllItems();
        combo2.removeAllItems();
        comboInstance.removeAllItems();
        for (SolverConfiguration solConfig : ExperimentDAO.getSolverConfigurationsInExperiment(expController.getActiveExperiment())) {
            combo1.addItem(solConfig);
            combo2.addItem(solConfig);
        }

        for (Instance i : InstanceDAO.getAllByExperimentId(expController.getActiveExperiment().getId())) {
            comboInstance.addItem(i);
        }
    }

    @Override
    public Dependency[] getDependencies() {
        return dependencies;
    }

    @Override
    public void plot(Rengine engine, Vector<PointInformation> pointInformations) throws SQLException, DependencyException {
        super.plot(engine, pointInformations);

        if (!(combo1.getSelectedItem() instanceof SolverConfiguration) || !(combo2.getSelectedItem() instanceof SolverConfiguration)) {
            throw new DependencyException("You have to select two solvers.");
        }
        if (!(comboInstance.getSelectedItem() instanceof Instance)) {
            throw new DependencyException("You have to select an instance.");
        }

        SolverConfiguration sc1 = (SolverConfiguration) combo1.getSelectedItem();
        SolverConfiguration sc2 = (SolverConfiguration) combo2.getSelectedItem();
        Instance instance = (Instance) comboInstance.getSelectedItem();

        Vector<ExperimentResult> results1 = getResults(sc1.getId(), instance.getId());
        Vector<ExperimentResult> results2 = getResults(sc2.getId(), instance.getId());

        double max_x = 0;
        double[] resultsDouble1 = new double[results1.size()];
        double[] resultsDouble2 = new double[results2.size()];
        for (int i = 0; i < results1.size(); i++) {
            double tmp = results1.get(i).getTime();
            resultsDouble1[i] = tmp;
            if (tmp > max_x) {
                max_x = tmp;
            }
        }
        for (int i = 0; i < results2.size(); i++) {
            double tmp = results2.get(i).getTime();
            resultsDouble2[i] = tmp;
            if (tmp > max_x) {
                max_x = tmp;
            }
        }


        engine.assign("results1", resultsDouble1);
        engine.assign("results2", resultsDouble2);
        engine.assign("legendNames", new String[] {sc1.toString(), sc2.toString()});
        // plot without data to create the frame
        engine.eval("plot(c(), c(), type='p', col='red', las=1, xlim=c(0," + max_x + ") , ylim=c(0,1), xaxs='i', yaxs='i', xlab='', ylab='', cex.main=1.5)");
        engine.eval("par(new=1)");

        // plot the two distributions
        engine.eval("plot(ecdf(results1)," +
                "main=''," +
                "xlab='', ylab='', xaxs='i', yaxs='i', las=1, col='red'," +
                "xlim=c(0.0," + max_x + "), ylim=c(0.0,1.0))");
        engine.eval("par(new=1)");
        engine.eval("plot(ecdf(results2)," +
                "main=''," +
                "xlab='', ylab='', xaxs='i', yaxs='i', las=1, col='blue'," +
                "xlim=c(0.0," + max_x + "), ylim=c(0.0,1.0))");

        // plot labels and axes
        engine.eval("mtext('CPU Time (s)', side=1, line=3, cex=1.2)");                      // bottom axis label
        engine.eval("mtext('P(solve within x seconds)', side=2, padj=0, line=3, cex=1.2)"); // left axis label
        engine.eval("mtext('RTD Comparison', padj=1, side=3, line=3, cex=1.7)");            // plot title

        // plot legend
        engine.eval("legend("+(max_x - (max_x * 0.35))+", 0.2,"+
        "legend=legendNames,"+
        "col=c('red', 'blue'),"+
        "pch=c(0,1), lty=1)");
    }

    @Override
    public String getTitle() {
        return "RTD Comparison of two solvers on an instance";
    }

    @Override
    public String toString() {
        return "RTD Comparison of two solvers on an instance";
    }
}
