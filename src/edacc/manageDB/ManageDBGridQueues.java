/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.model.GridQueue;
import edacc.model.GridQueueDAO;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Vector;

/**
 * At the moment only support of one queue!
 * This class will change a lot when adding support of multiple queues!
 * @author dgall
 */
public class ManageDBGridQueues {

    private static ManageDBGridQueues instance;

    private int defaultQueueId;

    private File tmpPBSScript;

    private ManageDBGridQueues() throws SQLException {
        GridQueue defaultQueue = getDefaultQueue();
        if (defaultQueue != null)
            defaultQueueId = defaultQueue.getId();
        else
            defaultQueueId = -1;
    }

    public static ManageDBGridQueues getInstance() throws SQLException {
        if (instance == null)
            instance = new ManageDBGridQueues();
        return instance;
    }

    public void saveGridSettings(String name, String location, int numNodes, int numCPUs, int walltime, int availNodes, int maxJobsQueue, String description) throws SQLException, FileNotFoundException {
        GridQueue q = GridQueueDAO.getById(defaultQueueId);
        if (q == null)
            q = new GridQueue();

        q.setName(name);
        q.setLocation(location);
        q.setNumNodes(numNodes);
        q.setNumCPUs(numCPUs);
        q.setWalltime(walltime);
        q.setAvailNodes(availNodes);
        q.setMaxJobsQueue(maxJobsQueue);
        q.setDescription(description);
        if (tmpPBSScript != null)
            q.setGenericPBSScript(tmpPBSScript);
        GridQueueDAO.save(q);
    }

    public void addPBSScript(File f) {
        tmpPBSScript = f;
    }

    public GridQueue getDefaultQueue() throws SQLException {
        Vector<GridQueue> queues = GridQueueDAO.getAll();
        if (queues.size() == 0)
            return null;
        return queues.get(queues.size() - 1);
    }
}
