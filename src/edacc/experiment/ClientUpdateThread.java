package edacc.experiment;

import edacc.model.Client;
import edacc.model.ClientDAO;
import java.util.HashSet;
import java.util.List;
import javax.swing.SwingWorker;

/**
 *
 * @author simon
 */
public class ClientUpdateThread extends SwingWorker<Void, Client> {

    private ClientTableModel model;
    private HashSet<Integer> ids;

    public ClientUpdateThread(ClientTableModel model) {
        super();
        ids = new HashSet<Integer>();
        this.model = model;
        for (int row = 0; row < model.getRowCount(); row++) {
            ids.add(model.getClientAt(row).getId());
        }
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    protected Void doInBackground() throws Exception {
        while (!this.isCancelled()) {
            for (Client c : ClientDAO.getClients()) {
                if (!ids.contains(c.getId())) {
                    publish(c);
                    ids.add(c.getId());
                }
            }
            Thread.sleep(5000);
        }
        return null;
    }

    @Override
    protected void process(List<Client> chunks) {
        for (Client c : chunks) {
            model.addClient(c);
        }
    }
}
