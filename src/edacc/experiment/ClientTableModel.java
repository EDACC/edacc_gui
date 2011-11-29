package edacc.experiment;

import edacc.model.Client;
import edacc.model.GridQueueDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author simon
 */
public class ClientTableModel extends ThreadSafeDefaultTableModel implements Observer {

    /** The index of the compute node column */
    public static final int COL_COMPUTENODE = 0;
    /** The index of the status column */
    public static final int COL_STATUS = 1;
    /** The index of the wait time column */
    public static final int COL_WAITTIME = 2;
    /** The index of the cores column */
    public static final int COL_CORES = 3;
    /** The index of the threads column */
    public static final int COL_THREADS = 4;
    /** The index of the hyperthreading column */
    public static final int COL_HYPERTHREADING = 5;
    /** The index of the turboboost column */
    public static final int COL_TURBOBOOST = 6;
    /** The index of the cpu name column */
    public static final int COL_CPUNAME = 7;
    /** The index of the cache size column */
    public static final int COL_CACHESIZE = 8;
    /** The index of the memory column */
    public static final int COL_MEMORY = 9;
    /** The index of the free memory column */
    public static final int COL_MEMORYFREE = 10;
    private static final String[] columns = {"Compute Node", "Status", "Wait Time", "Cores", "Threads", "Hyperthreading", "Turboboost", "CPU Name", "Cache Size", "Memory", "Free Memory"};
    private ArrayList<Client> clients;

    /** Creates the client table model */
    public ClientTableModel() {
        clients = new ArrayList<Client>();
    }

    /** Clears the clients */
    public void clearClients() {
        clients = new ArrayList<Client>();

        fireTableDataChanged();
    }

    /**
     * Adds a client.
     * @param client the client to be added 
     */
    public void addClient(Client client) {
        client.addObserver(this);
        clients.add(client);
        fireTableRowsInserted(clients.size() - 1, clients.size() - 1);
    }

    /**
     * Returns the client represented by <code>row</code>
     * @param row the row index for which a client should be returned
     * @return 
     */
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
                for (Integer cores : clients.get(row).getComputingExperiments().values()) {
                    computeCores += cores;
                }
                String timeleftStr = "";
                int timeleft;
                if ((timeleft = clients.get(row).getTimeleft()) > 0) {
                    int seconds = timeleft % 60;
                    timeleft /= 60;
                    int minutes = timeleft % 60;
                    timeleft /= 60;
                    int hours = timeleft % 24;
                    timeleft /= 24;
                    int days = timeleft;
                    if (days > 0) {
                        timeleftStr = "" + days + "d" + hours + "h" + minutes + "m" + seconds + "s";
                    } else if (hours > 0) {
                        timeleftStr = hours + "h" + minutes + "m" + seconds + "s";
                    } else if (minutes > 0) {
                        timeleftStr = minutes + "m" + seconds + "s";
                    } else if (seconds > 0) {
                        timeleftStr = seconds + "s";
                    }
                }
                return "" + computeCores + " threads computing " + clients.get(row).getComputingExperiments().size() + " experiments." + (!timeleftStr.equals("") ? " Expected timeleft: " + timeleftStr : "");
            case COL_WAITTIME:
                return "" + clients.get(row).getCurrent_wait_time() + " sec / " + clients.get(row).getWait_time() + " sec";
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
                return new edacc.experiment.Util.ValueUnit(clients.get(row).getMemory());
            case COL_MEMORYFREE:
                return new edacc.experiment.Util.ValueUnit(clients.get(row).getMemoryFree());
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == COL_MEMORY || columnIndex == COL_MEMORYFREE) {
            return edacc.experiment.Util.ValueUnit.class;
        }
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
