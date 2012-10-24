/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.gridqueues;

import edacc.model.Experiment;
import edacc.model.ExperimentHasGridQueue;
import edacc.model.ExperimentHasGridQueueDAO;
import edacc.model.GridQueue;
import edacc.model.GridQueueDAO;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;

/**
 * @author dgall
 */
public class GridQueuesController extends Observable {

    private static GridQueuesController instance;

    private GridQueuesController() {
    }

    public static GridQueuesController getInstance() {
        if (instance == null) {
            instance = new GridQueuesController();
        }
        return instance;
    }

    /**
     * Saves a given new GridQueue to the Database and cache.
     * It throws an exception, if no pbs script is specified (see addPBSScript).
     * @param q
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws Exception
     */
    public void createNewGridQueue(GridQueue q) throws SQLException, FileNotFoundException, Exception {
        GridQueueDAO.save(q);
    }

    public ArrayList<GridQueue> getChosenQueuesByExperiment(Experiment exp) throws SQLException {
        ArrayList<GridQueue> res = new ArrayList<GridQueue>();
        ArrayList<ExperimentHasGridQueue> ehgqs = ExperimentHasGridQueueDAO.getExperimentHasGridQueueByExperiment(exp);
        for (ExperimentHasGridQueue ehgq : ehgqs) {
            res.add(GridQueueDAO.getById(ehgq.getIdGridQueue()));
        }
        return res;
    }

    public void gridQueueSelectionChanged() {
        setChanged();
        notifyObservers();
    }
}
