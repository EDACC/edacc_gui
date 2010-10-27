package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentResult;
import edacc.model.Instance;
import edacc.model.Property;
import edacc.model.SolverConfiguration;
import java.util.ArrayList;
import javax.swing.JComboBox;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public class BoxPlot extends Plot {

    private static InstanceSelector instanceSelector;
    private static SolverConfigurationSelector solverConfigurationSelector;
    private static JComboBox comboProperty;
    private String warning = null;
    private ArrayList<Instance> instances;
    private ArrayList<SolverConfiguration> solverConfigs;
    private Property property;

    public BoxPlot(ExperimentController expController) {
        super(expController);

    }

    public static Dependency[] getDependencies() {
        if (instanceSelector == null) {
            instanceSelector = new InstanceSelector();
        }
        if (solverConfigurationSelector == null) {
            solverConfigurationSelector = new SolverConfigurationSelector();
        }
        if (comboProperty == null) {
            comboProperty = new JComboBox();
        }
        return new Dependency[]{
                    new Dependency("Property", comboProperty),
                    new Dependency("Solvers", solverConfigurationSelector),
                    new Dependency("Instances", instanceSelector)
                };
    }

    public static void loadDefaultValues(ExperimentController expController) throws Exception {
        instanceSelector.setInstances(expController.getInstances());
        instanceSelector.btnSelectAll();
        solverConfigurationSelector.setSolverConfigurations(expController.getSolverConfigurations());
        solverConfigurationSelector.btnSelectAll();
        comboProperty.removeAllItems();
        for (Property p : expController.getResultProperties()) {
            comboProperty.addItem(p);
        }
    }

    public static String getTitle() {
        return "Box plot - Box plots of the runtimes of solvers on instances";
    }

    @Override
    public String getPlotTitle() {
        return "BoxPlot (" + expController.getActiveExperiment().getName() + ")";
    }

    @Override
    public void plot(Rengine engine, ArrayList<PointInformation> pointInformations) throws Exception {
        warning = null;
        if (instances == null || solverConfigs == null || property == null) {
            if (!(comboProperty.getSelectedItem() instanceof Property)) {
                throw new DependencyException("You have to select a property.");
            }
            property = (Property) comboProperty.getSelectedItem();
            instances = instanceSelector.getSelectedInstances();
            if (instances == null || instances.isEmpty()) {
                throw new DependencyException("You have to select instances in order to plot.");
            }
            solverConfigs = solverConfigurationSelector.getSelectedSolverConfigurations();
            if (solverConfigs.isEmpty()) {
                throw new DependencyException("You have to select solvers in order to plot.");
            }

        }
        expController.updateExperimentResults();
        int k = 0;
        ArrayList<String> warnings = new ArrayList<String>();
        String[] names = new String[solverConfigs.size()];
        for (SolverConfiguration sc : solverConfigs) {
            int noProp = 0;
            ArrayList<ExperimentResult> results = new ArrayList<ExperimentResult>();
            for (Instance i : instances) {
                results.addAll(expController.getResults(sc.getId(), i.getId()));
            }
            ArrayList<Double> values = new ArrayList<Double>();
            for (ExperimentResult er : results) {
                Double value = expController.getValue(er, property);
                if (value == null) {
                    noProp++;
                } else {
                    values.add(value);
                }
            }
            double[] times = new double[values.size()];
            for (int i = 0; i < values.size(); i++) {
                times[i] = values.get(i);
            }
            names[k] = sc.getName();
            engine.assign("res_" + (k++), times);
            if (noProp > 0) {
                warnings.add("Solver " + sc.getName() + " didn't solve or has no result property calculated for " + noProp + " runs.");
            }
        }

        String data = "";
        for (int j = 0; j < k; j++) {
            data += "res_" + j;
            if (j != k - 1) {
                data += ",";
            }
        }
        engine.assign("names", names);
        engine.eval("boxplot(main = 'Boxplot', " + data + ", names = names, horizontal = TRUE)");
        engine.eval("mtext('"+property.getName() +"', side=1,line=3, cex=1.2)");
        if (warnings.size() > 0) {
            warning = htmlHeader
                    + "<h2>Warning</h2>";
            for (String w : warnings) {
                warning += w + "<br>";
            }
            warning += htmlFooter;
        }
    }

    @Override
    public String getAdditionalInformations() {
        return warning;
    }

    @Override
    public void updateDependencies() {
        if (instances == null || solverConfigs == null || property == null) {
            return;
        }
        instanceSelector.setSelectedInstances(instances);
        solverConfigurationSelector.setSelectedSolverConfigurations(solverConfigs);
        comboProperty.setSelectedItem(property);
    }
}
