/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.gridqueues;

import edacc.model.GridQueue;
import edacc.model.GridQueueDAO;
import edacc.model.NoConnectionToDBException;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.AbstractListModel;

/**
 *
 * @author dgall
 */
public class QueueListModel extends AbstractListModel {

    private Vector<GridQueue> queues;

    public QueueListModel() throws NoConnectionToDBException, SQLException {
        queues = GridQueueDAO.getAll();
        fireContentsChanged(queues, 0, queues.size() - 1);
    }

    public int getSize() {
        return queues.size();
    }

    public Object getElementAt(int index) {
        return queues.get(index);
    }

    public void addQueue(GridQueue q) {
        queues.add(q);
        fireContentsChanged(queues, 0, queues.size() - 1);
    }

    public void removeQueue(GridQueue q) {
        queues.remove(q);
        fireContentsChanged(queues, 0, queues.size() - 1);
    }

    public void removeQueue(int index) {
        if (index >= queues.size())
            return;
        queues.remove(index);
        fireContentsChanged(queues, 0, queues.size() - 1);
    }

    public void refreshQueues() throws NoConnectionToDBException, SQLException {
        queues = GridQueueDAO.getAll();
        fireContentsChanged(queues, 0, queues.size() - 1);
    }

}
