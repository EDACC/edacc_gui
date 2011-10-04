package edacc.experiment.tabs.solver;

import edacc.model.Solver;
import edacc.model.SolverConfiguration;
import edacc.util.Pair;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author simon
 */
public class SolverConfigurationEntryModel {

    private HashMap<Solver, LinkedList<SolverConfigurationEntry>> mapSolverEntryList;
    private LinkedList<SolverConfigurationEntryModelListener> listeners;
    private int size;

    public SolverConfigurationEntryModel() {
        size = 0;
        mapSolverEntryList = new HashMap<Solver, LinkedList<SolverConfigurationEntry>>();
        listeners = new LinkedList<SolverConfigurationEntryModelListener>();
    }

    public void addSolverConfigurationEntryModelListener(SolverConfigurationEntryModelListener listener) {
        this.listeners.add(listener);
    }

    public void removeSolverConfigurationEntryModelListener(SolverConfigurationEntryModelListener listener) {
        this.listeners.remove(listener);
    }

    public void insert(SolverConfigurationEntry entry, Solver solver, int index) {
        LinkedList<SolverConfigurationEntry> list = mapSolverEntryList.get(solver);
        if (list == null) {
            list = new LinkedList<SolverConfigurationEntry>();
            mapSolverEntryList.put(solver, list);
        }
        list.add(index, entry);
        size++;
    }

    public SolverConfigurationEntry getEntry(Solver solver, int index) {
        return mapSolverEntryList.get(solver).get(index);
    }

    public void add(SolverConfigurationEntry entry) {
        LinkedList<SolverConfigurationEntry> list = mapSolverEntryList.get(entry.getSolver());
        if (list == null) {
            list = new LinkedList<SolverConfigurationEntry>();
            mapSolverEntryList.put(entry.getSolver(), list);
        }
        list.add(entry);
        size++;
    }

    public void fireDataChanged() {
        for (SolverConfigurationEntryModelListener listener : listeners) {
            listener.onDataChanged();
        }
    }

    public void fireEntryChanged(SolverConfigurationEntry entry) {
        for (SolverConfigurationEntryModelListener listener : listeners) {
            listener.onEntryChanged(entry);
        }
    }

    public int getSize() {
        return size;
    }

    public List<Solver> getSolvers() {
        LinkedList<Solver> solvers = new LinkedList<Solver>();
        solvers.addAll(mapSolverEntryList.keySet());
        return solvers;
    }

    public int getSize(Solver solver) {
        List<SolverConfigurationEntry> list = mapSolverEntryList.get(solver);
        return list == null ? 0 : list.size();
    }

    public boolean isModified() {
        for (List<SolverConfigurationEntry> entries : mapSolverEntryList.values()) {
            for (SolverConfigurationEntry entry : entries) {
                if (entry.isModified()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean solverExists(Solver o) {
        return mapSolverEntryList.get(o) != null;
    }

    public void removeSolver(Solver o) {
        if (solverExists(o)) {
            size -= mapSolverEntryList.get(o).size();
            mapSolverEntryList.remove(o);
        }
    }

    public void removeSolverConfigurationEntry(SolverConfigurationEntry entry) {
        List<SolverConfigurationEntry> tmp = mapSolverEntryList.get(entry.getSolver());
        if (tmp != null) {
            if (tmp.contains(entry)) {
                tmp.remove(entry);
                size--;
            }
            if (tmp.isEmpty()) {
                mapSolverEntryList.remove(entry.getSolver());
            }
        }
    }

    public void clear() {
        mapSolverEntryList.clear();
        size = 0;
    }

    public int getSolverIndex(SolverConfigurationEntry entry) {
        List<SolverConfigurationEntry> tmp = mapSolverEntryList.get(entry.getSolver());
        if (tmp == null) {
            return -1;
        }
        return tmp.indexOf(entry);
    }

    public List<SolverConfigurationEntry> getEntries(Solver solver) {
        List<SolverConfigurationEntry> entries = new LinkedList<SolverConfigurationEntry>();
        List<SolverConfigurationEntry> tmp = mapSolverEntryList.get(solver);
        if (tmp != null) {
            entries.addAll(tmp);
        }
        return entries;
    }

    /**
     * Returns an <code>ArrayList</code> of all modified solver configurations and solver configurations for which the seed group has been changed. <br><br>
     * <b>Note:</b> Here a modified solver configuration doesn't mean a new/deleted solver configuration.
     * @return ArrayList of all modified solver configurations
     */
    public ArrayList<SolverConfiguration> getModifiedSolverConfigurations() {
        ArrayList<SolverConfiguration> res = new ArrayList<SolverConfiguration>();
        for (Solver s : getSolvers()) {
            for (SolverConfigurationEntry entry : getEntries(s)) {
                if (entry.getSolverConfig() != null && entry.isModified()) {
                    res.add(entry.getSolverConfig());
                }
            }
        }
        return res;
    }

    public Object getValueAt(Pair<Solver, Integer> p, int col) {
        SolverConfigurationEntry entry = getEntry(p.getFirst(), p.getSecond());
        switch (col) {
            case 0:
                return entry.getName();
            case 1:
                return entry.getSolver().toString();
            case 2:
                return entry.getHint();
            case 3:
                return entry.getSolverBinary().getBinaryName();
            case 4:
                return entry.getSeedGroup();
            default:
                return "";

        }
    }
    
    public int getColumnCount() {
        return 5;
    }
    
    public Class getColumnClass(int col) {
        switch (col) {
            case 4:
                return Integer.class;
            default:
                return String.class;
        }
    }

    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "Name";
            case 1:
                return "Solver";
            case 2:
                return "Hint";
            case 3:
                return "Solver Binary";
            case 4:
                return "Seed Group";
            default:
                return "";
        }
    }
}
