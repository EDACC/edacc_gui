package edacc.experiment;

import edacc.EDACCExperimentMode;
import edacc.EDACCSolverConfigEntry;
import edacc.EDACCSolverConfigPanel;
import edacc.model.Experiment;
import edacc.model.ExperimentDAO;
import edacc.model.ExperimentHasInstance;
import edacc.model.ExperimentHasInstanceDAO;
import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultDAO;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.Solver;
import edacc.model.SolverConfiguration;
import edacc.model.SolverConfigurationDAO;
import edacc.model.SolverDAO;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Random;

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
    private static Random rnd =new Random(System.currentTimeMillis());

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
     * @throws SQLException
     */
    public void initialize() throws SQLException {
        Vector<Experiment> v = new Vector<Experiment>();
        v.addAll(ExperimentDAO.getAll());
        experiments = v;
        main.expTableModel.setExperiments(experiments);

        Vector<Instance> vi = new Vector<Instance>();
        vi.addAll(InstanceDAO.getAll());
        instances = vi;
        main.insTableModel.setInstances(instances);
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
        main.solverConfigPanel.beginUpdate();
        for (int i = 0; i < vss.size(); i++) {
            main.solverConfigPanel.addSolverConfiguration(vss.get(i));
            for (int k = 0; k < main.solTableModel.getRowCount(); k++) {
                if (((Solver) main.solTableModel.getValueAt(k, 5)).getId() == vss.get(i).getSolver_id()) {
                    main.solTableModel.setValueAt(true, k, 4);
                }
            }
        }
        main.solverConfigPanel.endUpdate();

        main.insTableModel.setExperimentHasInstances(ExperimentHasInstanceDAO.getExperimentHasInstanceByExperimentId(activeExperiment.getId()));
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
            int seed_group = 0;
            try {
                seed_group = Integer.valueOf(entry.getSeedGroup().getText());
            }
            catch (NumberFormatException e) {
                seed_group = 0;
                entry.getSeedGroup().setText("0");
                javax.swing.JOptionPane.showMessageDialog(null, "Seed groups have to be integers, defaulted to 0", "Expected integer for seed groups", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
            if (entry.getSolverConfiguration() == null) {
                entry.setSolverConfiguration(SolverConfigurationDAO.createSolverConfiguration(entry.getSolverId(), activeExperiment.getId(), seed_group));
            }
            else {
                entry.getSolverConfiguration().setSeed_group(seed_group);
                entry.getSolverConfiguration().setModified();
            }
            entry.saveParameterInstances();
        }
        SolverConfigurationDAO.saveAll();
    }

    public void saveExperimentHasInstances() throws SQLException {
        for (int i = 0; i < main.insTableModel.getRowCount(); i++) {
            if ((Boolean) main.insTableModel.getValueAt(i, 5)) {
                if ((ExperimentHasInstance) main.insTableModel.getValueAt(i, 6) == null) {
                    main.insTableModel.setExperimentHasInstance(ExperimentHasInstanceDAO.createExperimentHasInstance(activeExperiment.getId(), ((Instance) main.insTableModel.getValueAt(i, 7)).getId()), i);
                }
            } else {
                ExperimentHasInstance ei = (ExperimentHasInstance) main.insTableModel.getValueAt(i, 6);
                if (ei != null) {
                    ExperimentHasInstanceDAO.removeExperimentHasInstance(ei);
                    main.insTableModel.setExperimentHasInstance(null, i);
                }
            }
        }
    }


    /**
     * method used for auto seed generation
     * @return
     */
    private int generateSeed(int max) {
        return rnd.nextInt(max+1);
    }

    /**
     * generates the ExperimentResults (jobs) in the database for the currently active experiment
     * This is the cartesian product of the set of solver configs and the set of the selected instances
     * Doesn't overwrite existing jobs
     * @throws SQLException
     * @param numRuns
     * @param timeout
     * @param generateSeeds
     * @param maxSeed
     * @return number of jobs added to the experiment results table
     * @throws SQLException
     */
    public int generateJobs(int numRuns, int timeout, boolean generateSeeds, int maxSeed, boolean linkSeeds) throws SQLException {
        activeExperiment.setAutoGeneratedSeeds(generateSeeds);
        activeExperiment.setNumRuns(numRuns);
        activeExperiment.setTimeOut(timeout);
        ExperimentDAO.setModified(activeExperiment);
        ExperimentDAO.save(activeExperiment);
        
        // get instances of this experiment
        LinkedList<Instance> listInstances = InstanceDAO.getAllByExperimentId(activeExperiment.getId());

        // get solver configurations of this experiment
        Vector<SolverConfiguration> vsc = SolverConfigurationDAO.getSolverConfigurationByExperimentId(activeExperiment.getId());
        
        int seed = 0;
        Hashtable<Integer, Integer> linked_seeds = new Hashtable<Integer, Integer>();
        if (generateSeeds) {
            if (linkSeeds) {
                for (SolverConfiguration c: vsc) {
                    if (!linked_seeds.containsKey(new Integer(c.getSeed_group()))) {
                        linked_seeds.put(new Integer(c.getSeed_group()), new Integer(generateSeed(maxSeed)));
                    }
                }
            }
            else {
                seed = generateSeed(maxSeed);
            }
        }

        int experiments_added = 0;

        Vector<ExperimentResult> experiment_results = new Vector<ExperimentResult>();

        // cartesian product
        for (Instance i: listInstances) {
            for (SolverConfiguration c: vsc) {
                for (int run = 0; run < numRuns; ++run) {
                    if (ExperimentResultDAO.jobExists(run, c.getId(), i.getId(), activeExperiment.getId()) == false) { // skip jobs that already exist
                        if (generateSeeds && linkSeeds) {
                            experiment_results.add(ExperimentResultDAO.createExperimentResult(run, 0, linked_seeds.get(new Integer(c.getSeed_group())), "", 0, 0, c.getId(), activeExperiment.getId(), i.getId()));
                        }
                        else {
                            experiment_results.add(ExperimentResultDAO.createExperimentResult(run, 0, seed, "", 0, 0, c.getId(), activeExperiment.getId(), i.getId()));
                        }
                        experiments_added++;
                    }
                }
                
            }
        }
        ExperimentResultDAO.batchSave(experiment_results);

        return experiments_added;
    }

    /**
     * returns the number of jobs in the database for the given experiment
     * @return
     */
    public int getNumJobs() {
        try {
            return ExperimentResultDAO.getCountByExperimentId(activeExperiment.getId());
        }
        catch (Exception e) {
            return 0;
        }
    }
}
