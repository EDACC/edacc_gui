package edacc.gridqueues;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentHasGridQueue;
import edacc.model.ExperimentHasGridQueueDAO;
import edacc.model.GridQueue;
import edacc.model.GridQueueDAO;
import edacc.model.NoConnectionToDBException;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.JCheckBox;

/**
 *
 * @author dgall
 */
public class QueueListModel extends AbstractListModel {

    public Vector<JCheckBox> checkBoxes;
    private Vector<GridQueue> queues;
    private ExperimentController expController;

    public QueueListModel(ExperimentController expController) throws NoConnectionToDBException, SQLException {
        this.expController = expController;
        refreshQueues();
    }

    @Override
    public int getSize() {
        return queues.size();
    }

    @Override
    public Object getElementAt(int index) {
        return queues.get(index);
    }

    public void refreshQueues() throws NoConnectionToDBException, SQLException {

        queues = GridQueueDAO.getAll();
        checkBoxes = new Vector<JCheckBox>();
        for (GridQueue q : queues) {
            JCheckBox checkBox = new JCheckBox(q.getName());
            checkBoxes.add(checkBox);
            if (expController != null) {
                Vector<ExperimentHasGridQueue> ehgqs = ExperimentHasGridQueueDAO.getExperimentHasGridQueueByExperiment(expController.getActiveExperiment());
                for (ExperimentHasGridQueue ehgq : ehgqs) {
                    if (ehgq.getIdGridQueue() == q.getId()) {
                        checkBox.setSelected(true);
                        break;
                    }
                }
            }
        }
        fireContentsChanged(queues, 0, queues.size() - 1);
    }

    public Vector<GridQueue> getSelectedGridQueues() {
        Vector<GridQueue> res = new Vector<GridQueue>();
        for (int i = 0; i < queues.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                res.add(queues.get(i));
            }
        }
        return res;
    }
}
