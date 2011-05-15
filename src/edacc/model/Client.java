package edacc.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import javax.swing.SwingUtilities;

/**
 *
 * @author simon
 */
public class Client extends BaseModel implements IntegerPKModel {

    private int idClient;
    private int numCores;
    private int numThreads;
    private boolean hyperthreading;
    private boolean turboboost;
    private String cpuname;
    private int cacheSize;
    private String cpuflags;
    private long memory;
    private long memoryFree;
    private String cpuinfo;
    private String meminfo;
    private String message;
    private int gridQueueId;
    private Timestamp lastReport;
    private boolean dead;
    private HashMap<Experiment, Integer> computingExperiments;
    private Observable observable;

    public Client(ResultSet rs) throws SQLException {
        this.idClient = rs.getInt("idClient");
        this.numCores = rs.getInt("numCores");
        this.numThreads = rs.getInt("numThreads");
        this.hyperthreading = rs.getBoolean("hyperthreading");
        this.turboboost = rs.getBoolean("turboboost");
        this.cpuname = rs.getString("CPUName");
        this.cacheSize = rs.getInt("cacheSize");
        this.cpuflags = rs.getString("cpuflags");
        this.memory = rs.getLong("memory");
        this.memoryFree = rs.getLong("memoryFree");
        this.cpuinfo = rs.getString("cpuinfo");
        this.meminfo = rs.getString("meminfo");
        this.message = rs.getString("message");
        this.gridQueueId = rs.getInt("gridQueue_idgridQueue");
        this.lastReport = rs.getTimestamp("lastReport");
        this.dead = rs.getBoolean("dead");
        computingExperiments = new HashMap<Experiment, Integer>();
        observable = new Observable() {

            @Override
            public void notifyObservers() {
                this.setChanged();
                super.notifyObservers();
            }

            @Override
            public void notifyObservers(Object arg) {
                this.setChanged();
                super.notifyObservers(arg);
            }
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Client other = (Client) obj;
        if (this.idClient != other.idClient) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + this.idClient;
        return hash;
    }

    protected void notifyObservers() {
        if (SwingUtilities.isEventDispatchThread()) {
            observable.notifyObservers(this);
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    observable.notifyObservers(Client.this);
                }
            });
        }
    }

    public void addObserver(Observer o) {
        observable.addObserver(o);
    }

    public void deleteObserver(Observer o) {
        observable.deleteObserver(o);
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public String getCpuflags() {
        return cpuflags;
    }

    public String getCpuinfo() {
        return cpuinfo;
    }

    public String getCpuname() {
        return cpuname;
    }

    public int getGridQueueId() {
        return gridQueueId;
    }

    public boolean isHyperthreading() {
        return hyperthreading;
    }

    public Timestamp getLastReport() {
        return lastReport;
    }

    public String getMeminfo() {
        return meminfo;
    }

    public long getMemory() {
        return memory;
    }

    public long getMemoryFree() {
        return memoryFree;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if (this.message == null || !this.message.equals(message)) {
            this.message = message;
            this.setModified();
        }
    }

    public int getNumCores() {
        return numCores;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public boolean isTurboboost() {
        return turboboost;
    }

    @Override
    public int getId() {
        return idClient;
    }

    @Override
    public boolean isDeleted() {
        return super.isDeleted();
    }

    public boolean isDead() {
        return dead;
    }

    protected void setDead(boolean dead) {
        if (this.dead != dead) {
            this.dead = dead;
            this.setModified();
        }
    }

    public HashMap<Experiment, Integer> getComputingExperiments() {
        return computingExperiments;
    }

    protected void setComputingExperiments(HashMap<Experiment, Integer> computingExperiments) {
        if (!this.computingExperiments.equals(computingExperiments)) {
            if (!this.isNew()) {
                this.setModified();
            }
        }
        this.computingExperiments = computingExperiments;
    }
}
