/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.gridqueues;

import edacc.model.GridQueue;
import edacc.model.GridQueueDAO;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

/**
 * At the moment only support of one queue!
 * This class will change a lot when adding support of multiple queues!
 * @author dgall
 */
public class GridQueuesController {

    private static GridQueuesController instance;

    private File tmpPBSScript;
    private GridQueue chosenQueue;

    private GridQueuesController() {
        
    }

    public static GridQueuesController getInstance() {
        if (instance == null)
            instance = new GridQueuesController();
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
        if (tmpPBSScript == null)
            throw new Exception("You must specify a generic PBS script!");
        q.setGenericPBSScript(tmpPBSScript);
        GridQueueDAO.save(q);
        tmpPBSScript = null;
    }

    /**
     * Saves a given modified GridQueue to the Database and cache.
     * In contrast to createNewGridQueue it throws no exception, if no pbs
     * script is specified (see addPBSScript).
     * @param q
     * @throws SQLException
     * @throws FileNotFoundException
     */
    public void saveEditedGridQueue(GridQueue q) throws SQLException, FileNotFoundException {
        GridQueueDAO.save(q);
        tmpPBSScript = null;
    }
    
    public void addPBSScript(File f) {
        tmpPBSScript = f;
    }

    public void setChosenQueue(GridQueue q) {
        this.chosenQueue = q;
    }

    public GridQueue getChosenQueue() {
        return chosenQueue;
    }
}
