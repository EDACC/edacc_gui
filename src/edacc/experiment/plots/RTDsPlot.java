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
import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public class RTDsPlot extends Plot {

    private Dependency[] dependencies;
    private SolverConfigurationSelector solverSelector;
    private JComboBox comboInstance;

    public RTDsPlot(ExperimentController expController) {
        super(expController);
        solverSelector = new SolverConfigurationSelector();
        comboInstance = new JComboBox();
        dependencies = new Dependency[]{
                    new Dependency("Solvers", solverSelector),
                    new Dependency("Instance", comboInstance)
                };
    }

    @Override
    public void loadDefaultValues() throws Exception {
        solverSelector.setSolverConfigurations(ExperimentDAO.getSolverConfigurationsInExperiment(expController.getActiveExperiment()));
        solverSelector.btnSelectAll();
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
        if (solverSelector.getSelectedSolverConfigurations().size() == 0) {
            throw new DependencyException("You have to select solvers in order to plot.");
        }

        if (!(comboInstance.getSelectedItem() instanceof Instance)) {
            throw new DependencyException("You have to select an instance.");
        }

        Vector<SolverConfiguration> scs = solverSelector.getSelectedSolverConfigurations();
        Instance instance = (Instance) comboInstance.getSelectedItem();
        double max_x = 0.;

        Vector<double[]> results = new Vector<double[]>();
        String[] legendNames = new String[scs.size()];
        String[] legendColors = new String[scs.size()];
        int k = 0;
        for (SolverConfiguration sc : scs) {
            Vector<ExperimentResult> res = getResults(sc.getId(), instance.getId());
            double[] tmp = new double[res.size()];
            for (int i = 0; i < res.size(); i++) {
                tmp[i] = res.get(i).getResultTime();
                if (tmp[i] > max_x) {
                    max_x = tmp[i];
                }
            }
            results.add(tmp);
            legendNames[k] = sc.getName();
            legendColors[k] = colors[k % colors.length];
            k++;
        }
        // plot without data to create the frame
        engine.eval("plot(c(), c(), type='p', col='red', las=1, xlim=c(0," + max_x + ") , ylim=c(-0.05,1.05), xaxs='i', yaxs='i', xlab='', ylab='', cex.main=1.5)");
        engine.eval("par(new=1)");

        // plot the distributions
        for (int i = 0; i < results.size(); i++) {
            engine.assign("results", results.get(i));
            engine.eval("plot(ecdf(results)," +
                    "main='', col='" + legendColors[i] + "', pch=" + i + "," +
                    "xlab='', ylab='', xaxs='i', yaxs='i', las=1," +
                    "xaxt='n', yaxt='n'," +
                    "xlim=c(0.0," + max_x + "), ylim=c(-0.05, 1.05))");
            engine.eval("par(new=1)");
        }
        // plot labels and axes
        engine.eval("mtext('CPU Time (s)', side=1, line=3, cex=1.2)");                      // bottom axis label
        engine.eval("mtext('P(solve within x seconds)', side=2, padj=0, line=3, cex=1.2)"); // left axis label
        engine.eval("mtext('Runtime Distributions', padj=1, side=3, line=3, cex=1.7)");     // plot title

        // plot legend
        engine.assign("legendNames", legendNames);
        engine.assign("legendColors", legendColors);
        engine.eval("legend('bottomright', inset=.01," +
                "legend=legendNames," +
                "col=legendColors," +
                "pch=c(0," + scs.size() + "), lty=1)");
    }

    @Override
    public String getTitle() {
        return "RTDs ("+expController.getActiveExperiment().getName()+")";
    }

    @Override
    public String toString() {
        return "Plot of the runtime distributions of solvers on an instance";
    }
}

