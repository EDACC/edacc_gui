package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentDAO;
import edacc.model.ExperimentResult;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.Property;
import edacc.model.SolverConfiguration;
import java.util.ArrayList;
import javax.swing.JComboBox;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public class KernelDensityPlot extends Plot {

    private static JComboBox comboSolver, comboInstance, comboProperty;
    private SolverConfiguration sc;
    private Instance instance;
    private Property property;
    private String title;

    public KernelDensityPlot(ExperimentController expController) {
        super(expController);
    }

    public static Dependency[] getDependencies() {
        if (comboSolver == null) {
            comboSolver = new JComboBox();
        }
        if (comboInstance == null) {
            comboInstance = new JComboBox();
        }
        if (comboProperty == null) {
            comboProperty = new JComboBox();
        }
        return new Dependency[]{
                    new Dependency("Solver", comboSolver),
                    new Dependency("Instance", comboInstance),
                    new Dependency("Property", comboProperty)
                };
    }

    public static void loadDefaultValues(ExperimentController expController) throws Exception {
        comboSolver.removeAllItems();
        comboInstance.removeAllItems();
        comboProperty.removeAllItems();
        for (SolverConfiguration sc : ExperimentDAO.getSolverConfigurationsInExperiment(expController.getActiveExperiment())) {
            comboSolver.addItem(sc);
        }
        for (Instance i : InstanceDAO.getAllByExperimentId(expController.getActiveExperiment().getId())) {
            comboInstance.addItem(i);
        }
        for (Property p : getResultProperties()) {
            comboProperty.addItem(p);
        }
    }

    @Override
    public void plot(Rengine engine, ArrayList<PointInformation> pointInformations) throws Exception {

        if (sc == null || instance == null || property == null) {
            if (!(comboSolver.getSelectedItem() instanceof SolverConfiguration)) {
                throw new DependencyException("You have to select a solver.");
            }
            if (!(comboInstance.getSelectedItem() instanceof Instance)) {
                throw new DependencyException("You have to select an instance.");
            }
            if (!(comboProperty.getSelectedItem() instanceof Property)) {
                throw new DependencyException("You have to select a property");
            }
            if (engine.eval("library('np')") == null) {
                throw new DependencyException("Did not find np library which is needed for this plot. "
                        + "Please install the np library in r by typing install.packages('np').");
            }

            sc = (SolverConfiguration) comboSolver.getSelectedItem();
            instance = (Instance) comboInstance.getSelectedItem();
            property = (Property) comboProperty.getSelectedItem();
        }
        expController.updateExperimentResults();
        title = "Kernel density estimation for " + sc.getName() + " on " + instance.getName() + " for property "+ property.getName() + expController.getActiveExperiment().getName() + ")";

        ArrayList<Double> values = new ArrayList<Double>();
        for (ExperimentResult er : expController.getResults(sc.getId(), instance.getId())) {
            Double value = expController.getValue(er, property);
            if (value == null) {
                // TODO: warning
            } else {
                values.add(value);
            }
        }

        double[] results = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            results[i] = values.get(i);
        }
        engine.assign("results", results);
        // TODO: CONSTANT!!
        engine.eval("plot(npudens(results),main='', xaxt='n', yaxt='n',xlab='', ylab='', xaxs='i', yaxs='i', las=1)");

        // plot labels and axes
        engine.eval("mtext('Nonparametric kernel density estimation',padj=1, side=3, line=3, cex=1.7)"); // plot title
    }

    public static String getTitle() {
        return "Kernel density estimation for a solver on an instance";
    }

    @Override
    public String getPlotTitle() {
        return title;
    }

    @Override
    public void updateDependencies() {
        if (sc == null || instance == null || property == null) {
            return;
        }
        comboSolver.setSelectedItem(sc);
        comboInstance.setSelectedItem(instance);
        comboProperty.setSelectedItem(property);
    }
}
