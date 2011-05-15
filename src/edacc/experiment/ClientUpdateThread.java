package edacc.experiment;

import edacc.model.Client;
import edacc.model.ClientDAO;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 *
 * @author simon
 */
public class ClientUpdateThread extends SwingWorker<Void, Client> {

    private ClientTableModel model;
    private HashSet<Integer> ids;

    public ClientUpdateThread(final ClientTableModel model) {
        super();
        ids = new HashSet<Integer>();
        this.model = model;
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        model.clearClients();
                    }
                    
                });
            } catch (Exception ex) {
            }
        } else {
            model.clearClients();
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
