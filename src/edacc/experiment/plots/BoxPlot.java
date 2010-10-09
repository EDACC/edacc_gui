package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentResult;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.SolverConfiguration;
import edacc.model.SolverConfigurationDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author simon
 */
public class BoxPlot extends Plot {

    private static InstanceSelector instanceSelector;
    private static SolverConfigurationSelector solverConfigurationSelector;
    private String warning = null;
    private ArrayList<Instance> instances;
    private ArrayList<SolverConfiguration> solverConfigs;

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
        return new Dependency[]{
                    new Dependency("Solvers", solverConfigurationSelector),
                    new Dependency("Instances", instanceSelector)
                };
    }

    public static void loadDefaultValues(ExperimentController expController) throws Exception {
        ArrayList<Instance> _instances = new ArrayList<Instance>();
        _instances.addAll(InstanceDAO.getAllByExperimentId(expController.getActiveExperiment().getId()));
        instanceSelector.setInstances(_instances);
        instanceSelector.btnSelectAll();
        solverConfigurationSelector.setSolverConfigurations(SolverConfigurationDAO.getSolverConfigurationByExperimentId(expController.getActiveExperiment().getId()));
        solverConfigurationSelector.btnSelectAll();
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
        if (instances == null || solverConfigs == null) {
            instances = instanceSelector.getSelectedInstances();
            if (instances == null || instances.isEmpty()) {
                throw new DependencyException("You have to select instances in order to plot.");
            }
            solverConfigs = solverConfigurationSelector.getSelectedSolverConfigurations();
            if (solverConfigs.isEmpty()) {
                throw new DependencyException("You have to select solvers in order to plot.");
            }
        }
        initializeResults();
        int k = 0;
        ArrayList<String> warnings = new ArrayList<String>();
        String[] names = new String[solverConfigs.size()];
        for (SolverConfiguration sc : solverConfigs) {
            int penalties = 0;
            ArrayList<ExperimentResult> results = new ArrayList<ExperimentResult>();
            for (Instance i : instances) {
                results.addAll(getResults(sc.getId(), i.getId()));
            }
            double[] times = new double[expController.getActiveExperiment().getNumRuns() * instances.size()];
            for (int i = 0; i < results.size(); i++) {
                times[i] = results.get(i).getResultTime();
            }
            // penalty for not solving the instance.
            for (int i = results.size(); i < expController.getActiveExperiment().getNumRuns() * instances.size(); i++) {
                penalties++;
                times[i] = expController.getActiveExperiment().getCPUTimeLimit();
            }
            names[k] = sc.getName();
            engine.assign("res_" + (k++), times);
            if (penalties > 0) {
                warnings.add("Solver " + sc.getName() + " got " + penalties + " penalties.");
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
        engine.eval("mtext('CPU Time (s)', side=1,line=3, cex=1.2)");
        if (warnings.size() > 0) {
            warning = "<html>Some solvers got the penalty of " + expController.getActiveExperiment().getCPUTimeLimit() + "s for not solving some instances:<br>";
            for (String w : warnings) {
                warning += w + "<br>";
            }
            warning += "</html>";
        }
    }

    @Override
    public String getWarning() {
        return warning;
    }
}
