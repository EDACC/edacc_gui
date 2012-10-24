package edacc.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import javax.swing.SwingUtilities;

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
        return res;
    }

    public synchronized SolverConfiguration createSolverConfiguration(SolverBinaries solverBinary, int expId, int seed_group, String title, String hint) throws SQLException, Exception {
        SolverConfiguration sc = SolverConfigurationDAO.createSolverConfiguration(solverBinary, expId, seed_group, title, hint);
        solverConfigs.put(sc.getId(), sc);
        return sc;
    }

    public synchronized void markAsDeleted(final SolverConfiguration sc) {
        sc.setDeleted();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                setChanged();
                notifyObservers(sc);
            }
        });
    }
    
    public synchronized SolverConfiguration getSolverConfigurationById(Integer id) {
        return solverConfigs.get(id);
    }

    public synchronized void synchronize() throws SQLException {
        HashSet<Integer> ids = new HashSet<Integer>();
        for (final SolverConfiguration sc : SolverConfigurationDAO.getSolverConfigurationByExperimentId(experiment.getId())) {
            
            if (solverConfigs.get(sc.getId()) == null) {
                solverConfigs.put(sc.getId(), sc);
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        setChanged();
                        notifyObservers(sc);
                    }
                });

            }
            ids.add(sc.getId());
        }
        LinkedList<SolverConfiguration> scs = new LinkedList<SolverConfiguration>();
        scs.addAll(solverConfigs.values());
        for (SolverConfiguration sc : scs) {
            if (!ids.contains(sc.getId())) {
                markAsDeleted(sc);
                solverConfigs.remove(sc.getId());
            }
        }
    }

    public synchronized void reload() throws SQLException {
        solverConfigs.clear();
        SolverConfigurationDAO.clearCache();
        for (SolverConfiguration sc : SolverConfigurationDAO.getSolverConfigurationByExperimentId(experiment.getId())) {
            solverConfigs.put(sc.getId(), sc);
        }
        ArrayList<SolverConfiguration> tmp = new ArrayList<SolverConfiguration>();
        for (SolverConfiguration sc : solverConfigs.values()) {
            tmp.add(sc);
        }
        ParameterInstanceDAO.cacheParameterInstances(tmp);
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                setChanged();
                notifyObservers();
            }
        });
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
        SolverConfigurationDAO.saveAll(getAll());
        for (SolverConfiguration sc : getAll()) {
            if (sc.isDeleted()) {
                solverConfigs.remove(sc.getId());
            }
        }
    }

    public void createAll(List<SolverConfiguration> solverConfigurations) throws SQLException {
        for (SolverConfiguration sc : solverConfigurations)
            if (!sc.isNew()) {
                throw new IllegalArgumentException("Can't create existing solver configurations");
            }
        SolverConfigurationDAO.saveAll(solverConfigurations);
        for (SolverConfiguration sc : solverConfigurations) {
            solverConfigs.put(sc.getId(), sc);
        }
    }
}
