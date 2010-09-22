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
public class KernelDensityPlot extends Plot {

    private JComboBox comboSolver, comboInstance;
    private Dependency[] dependencies;
    private String title;

    public KernelDensityPlot(ExperimentController expController) {
        super(expController);
        comboSolver = new JComboBox();
        comboInstance = new JComboBox();
        dependencies = new Dependency[]{
                    new Dependency("Solver", comboSolver),
                    new Dependency("Instance", comboInstance)
                };
    }

    @Override
    public void loadDefaultValues() throws Exception {
        comboSolver.removeAllItems();
        comboInstance.removeAllItems();
        for (SolverConfiguration sc : ExperimentDAO.getSolverConfigurationsInExperiment(expController.getActiveExperiment())) {
            comboSolver.addItem(sc);
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
        if (!(comboSolver.getSelectedItem() instanceof SolverConfiguration)) {
            throw new DependencyException("You have to select a solver.");
        }
        if (!(comboInstance.getSelectedItem() instanceof Instance)) {
            throw new DependencyException("You have to select an instance.");
        }
        if (engine.eval("library('np')") == null) {
            throw new DependencyException("Did not find np library which is needed for this plot. " +
                    "Please install the np library in r by typing install.packages('np').");
        }
        SolverConfiguration sc = (SolverConfiguration) comboSolver.getSelectedItem();
        Instance instance = (Instance) comboInstance.getSelectedItem();

        title = "Kernel density estimation for " + sc.getName() + " on " + instance.getName() + " (" + expController.getActiveExperiment().getName() + ")";

        Vector<ExperimentResult> expResults = getResults(sc.getId(), instance.getId());
        double[] results = new double[expResults.size()];
        for (int i = 0; i < expResults.size(); i++) {
            results[i] = expResults.get(i).getResultTime();
        }
        
        engine.assign("results", results);
        engine.eval("plot(npudens(results),main='', xaxt='n', yaxt='n',xlab='', ylab='', xaxs='i', yaxs='i', las=1)");

        // plot labels and axes
        engine.eval("mtext('Nonparametric kernel density estimation',padj=1, side=3, line=3, cex=1.7)"); // plot title
    }

    @Override
    public String toString() {
        return "Kernel density estimation for a solver on an instance";
    }

    @Override
    public String getTitle() {
        return title;
    }
}
