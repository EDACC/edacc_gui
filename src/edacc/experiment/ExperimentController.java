package edacc.experiment;

import edacc.EDACCExperimentMode;
import edacc.EDACCSolverConfigEntry;
import edacc.EDACCSolverConfigPanel;
import edacc.model.Experiment;
import edacc.model.ExperimentDAO;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.Solver;
import edacc.model.SolverConfiguration;
import edacc.model.SolverConfigurationDAO;
import edacc.model.SolverDAO;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Vector;

/**
 *
 * @author daniel
 */
public class ExperimentController {

    EDACCExperimentMode main;
    EDACCSolverConfigPanel solverConfigPanel;
    private Experiment activeExperiment;
    private Vector<Experiment> experiments;
    private Vector<Instance> instances;
    private Vector<Solver> solvers;

    /**
     * Creates a new experiment Controller
     * @param experimentMode
     * @param solverConfigPanel
     */
    public ExperimentController(EDACCExperimentMode experimentMode, EDACCSolverConfigPanel solverConfigPanel) {
        this.main = experimentMode;
        this.solverConfigPanel = solverConfigPanel;
    }

    /**
     * Initializes the experiment controller. Loads the experiments and the instances.
     */
    public void initialize() {
        try {
            Vector<Experiment> v = new Vector<Experiment>();
            v.addAll(ExperimentDAO.getAll());
            experiments = v;
            main.expTableModel.setExperiments(experiments);

            Vector<Instance> vi = new Vector<Instance>();
            vi.addAll(InstanceDAO.getAll());
            instances = vi;
            main.insTableModel.setInstances(instances);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads an experiment, the solvers and the solver configurations.
     * @param id
     * @throws SQLException
     */
    public void loadExperiment(int id) throws SQLException {
        if (activeExperiment != null) {
            // TODO! messagedlg,..
            solverConfigPanel.removeAll();
        }
        activeExperiment = ExperimentDAO.getById(id);
        Vector<Solver> vs = new Vector<Solver>();
        vs.addAll(SolverDAO.getAll());
        solvers = vs;
        main.solTableModel.setSolvers(solvers);

        Vector<SolverConfiguration> vss = SolverConfigurationDAO.getSolverConfigurationByExperimentId(id);
        for (int i = 0; i < vss.size(); i++) {
            main.solverConfigPanel.addSolverConfiguration(vss.get(i));
            for (int k = 0; k < main.solTableModel.getRowCount(); k++) {
                if (((Solver)main.solTableModel.getValueAt(k, 5)).getId() == vss.get(i).getSolver_id()) {
                    main.solTableModel.setValueAt(true, k, 4);
                }
            }
        }
        main.afterExperimentLoaded();
    }

    /**
     * Removes an experiment form the db.
     * @param id
     * @return
     * @throws SQLException
     */
    public void removeExperiment(int id) throws SQLException {
        Experiment e = ExperimentDAO.getById(id);
        if (e.equals(activeExperiment)) {
            unloadExperiment();
        }
        ExperimentDAO.removeExperiment(e);
        initialize();
    }

    public Experiment getActiveExperiment() {
        return activeExperiment;
    }

    public void unloadExperiment() {
        activeExperiment = null;
        main.afterExperimentUnloaded();
    }

    public void createExperiment(String name, Date date, String description) throws SQLException {
        ExperimentDAO.createExperiment(name, date, description);
        initialize();
    }

    /**
     * Saves all solver configurations with parameter instances in the solver
     * config panel.
     * @throws SQLException
     */
    public void saveSolverConfigurations() throws SQLException {
        for (int i = 0; i < solverConfigPanel.getComponentCount(); i++) {
            EDACCSolverConfigEntry entry = (EDACCSolverConfigEntry) solverConfigPanel.getComponent(i);
            if (entry.getSolverConfiguration() == null) {
                entry.setSolverConfiguration(SolverConfigurationDAO.createSolverConfiguration(entry.getSolverId(), activeExperiment.getId()));
            }
            entry.saveParameterInstances();
        }
        SolverConfigurationDAO.saveAll();
    }
}
