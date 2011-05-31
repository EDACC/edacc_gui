package edacc.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Observable;

/**
 *
 * @author simon
 */
public class SolverConfigCache extends Observable {

    Experiment experiment;
    HashMap<Integer, SolverConfiguration> solverConfigs;

    public SolverConfigCache(Experiment experiment) {
        solverConfigs = new HashMap<Integer, SolverConfiguration>();
        this.experiment = experiment;
    }

    public synchronized ArrayList<SolverConfiguration> getAll() {
        ArrayList<SolverConfiguration> res = new ArrayList<SolverConfiguration>();
        res.addAll(solverConfigs.values());
        Collections.sort(res, new Comparator<SolverConfiguration>() {

            @Override
            public int compare(SolverConfiguration o1, SolverConfiguration o2) {
                return o1.getIdx() - o2.getIdx();
            }
        });
        return res;
    }

    public synchronized SolverConfiguration createSolverConfiguration(SolverBinaries solverBinary, int expId, int seed_group, String title, int idx) throws SQLException, Exception {
        SolverConfiguration sc = SolverConfigurationDAO.createSolverConfiguration(solverBinary, expId, seed_group, title, idx);
        solverConfigs.put(sc.getId(), sc);
        return sc;
    }

    public synchronized void markAsDeleted(SolverConfiguration sc) {
        sc.setDeleted();
        setChanged();
        notifyObservers(sc);
    }

    public synchronized void synchronize() {
        // TODO: implement when necessary
    }

    public synchronized void reload() throws SQLException {
        solverConfigs.clear();
        SolverConfigurationDAO.clearCache();
        for (SolverConfiguration sc : SolverConfigurationDAO.getSolverConfigurationByExperimentId(experiment.getId())) {
            solverConfigs.put(sc.getId(), sc);
        }
        setChanged();
        notifyObservers();
    }

    public synchronized void changeExperiment(Experiment experiment) throws SQLException {
        this.experiment = experiment;
        reload();
    }

    public synchronized boolean isModified() {
        for (SolverConfiguration sc : solverConfigs.values()) {
            if (!sc.isSaved()) {
                return true;
            }
        }
        return false;
    }

    public boolean isDeleted(SolverConfiguration solverConfiguration) {
        return solverConfiguration.isDeleted();
    }

    public ArrayList<ParameterInstance> getSolverConfigurationParameters(SolverConfiguration solverConfiguration) throws SQLException {
        return SolverConfigurationDAO.getSolverConfigurationParameters(solverConfiguration);
    }

    public synchronized ArrayList<SolverConfiguration> getAllDeleted() {
        ArrayList<SolverConfiguration> res = new ArrayList<SolverConfiguration>();
        for (SolverConfiguration sc : solverConfigs.values()) {
            if (sc.isDeleted()) {
                res.add(sc);
            }
        }
        return res;
    }

    /**
     * Equalises local data with database data so that the state of all
     * solver configurations is saved.
     * @throws SQLException
     */
    public synchronized void saveAll() throws SQLException {
        for (SolverConfiguration sc : getAll()) {
            SolverConfigurationDAO.save(sc);
            if (sc.isDeleted()) {
                solverConfigs.remove(sc.getId());
            }
        }
    }
}
