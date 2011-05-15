package edacc.experiment;

import edacc.model.Client;
import edacc.model.GridQueueDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author simon
 */
public class ClientTableModel extends DefaultTableModel implements Observer {

    public static final int COL_COMPUTENODE = 0;
    public static final int COL_STATUS = 1;
    public static final int COL_CORES = 2;
    public static final int COL_THREADS = 3;
    public static final int COL_HYPERTHREADING = 4;
    public static final int COL_TURBOBOOST = 5;
    public static final int COL_CPUNAME = 6;
    public static final int COL_CACHESIZE = 7;
    public static final int COL_MEMORY = 8;
    public static final int COL_MEMORYFREE = 9;
    private static final String[] columns = {"Compute Node", "Status", "Cores", "Threads", "Hyperthreading", "Turboboost", "CPU Name", "Cache Size", "Memory", "Free Memory"};
    private ArrayList<Client> clients;

    public ClientTableModel() {
        clients = new ArrayList<Client>();
    }
    
    public void clearClients() {
        clients = new ArrayList<Client>();
        this.fireTableDataChanged();
    }

    public void addClient(Client client) {
        client.addObserver(this);
        clients.add(client);
        this.fireTableRowsInserted(clients.size() - 1, clients.size() - 1);
    }

    public Client getClientAt(int row) {
        return clients.get(row);
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public int getRowCount() {
        return clients == null ? 0 : clients.size();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        switch (column) {
            case COL_COMPUTENODE:
                try {
                    return clients.get(row).getGridQueueId() == 0 ? "none" : GridQueueDAO.getById(clients.get(row).getGridQueueId()).getName();
                } catch (SQLException ex) {
                    return "error";
                }
            case COL_STATUS:
                int computeCores = 0;
                for (Integer cores : clients.get(row).getComputingExperiments().values()) 
                    computeCores += cores;
                return "" + computeCores + " threads computing " + clients.get(row).getComputingExperiments().size() + " experiments";
            case COL_CORES:
                return clients.get(row).getNumCores();
            case COL_THREADS:
                return clients.get(row).getNumThreads();
            case COL_HYPERTHREADING:
                return clients.get(row).isHyperthreading();
            case COL_TURBOBOOST:
                return clients.get(row).isTurboboost();
            case COL_CPUNAME:
                return clients.get(row).getCpuname();
            case COL_CACHESIZE:
                return clients.get(row).getCacheSize();
            case COL_MEMORY:
                return clients.get(row).getMemory();
            case COL_MEMORYFREE:
                return clients.get(row).getMemoryFree();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return (getRowCount() > 0 ? (getValueAt(0, columnIndex) != null ? getValueAt(0, columnIndex).getClass() : null) : String.class);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Client) {
            Client c = (Client) arg;
            if (c.isDeleted()) {
                for (int i = 0; i < this.getRowCount(); i++) {
                    if (clients.get(i).getId() == c.getId()) {
                        clients.remove(i);
                        this.fireTableRowsDeleted(i, i);
                        break;
                    }
                }
            } else {
                for (int i = 0; i < this.getRowCount(); i++) {
                    if (clients.get(i).getId() == c.getId()) {
                        this.fireTableRowsUpdated(i, i);
                        break;
                    }
                }
            }
        }
    }
}
