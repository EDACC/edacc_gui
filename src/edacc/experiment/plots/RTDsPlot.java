package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentDAO;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import javax.swing.JComboBox;

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
        dependencies = new Dependency[] {
            new Dependency("Solvers", solverSelector),
            new Dependency("Instance", comboInstance)
        };
    }
    @Override
    public void loadDefaultValues() throws Exception {
        solverSelector.setSolverConfigurations(ExperimentDAO.getSolverConfigurationsInExperiment(expController.getActiveExperiment()));
                for (Instance i : InstanceDAO.getAllByExperimentId(expController.getActiveExperiment().getId())) {
            comboInstance.addItem(i);
        }
    }

    @Override
    public Dependency[] getDependencies() {
        return dependencies;
    }

    @Override
    public String getTitle() {
        return "Plot of the runtime distributions of solvers on an instance";
    }

    @Override
    public String toString() {
        return "Plot of the runtime distributions of solvers on an instance";
    }
}

