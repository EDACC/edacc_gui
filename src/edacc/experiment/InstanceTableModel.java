package edacc.experiment;

import edacc.model.DatabaseConnector;
import edacc.model.ExperimentHasInstance;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.InstanceHasInstanceClassDAO;
import edacc.model.InstanceHasProperty;
import edacc.model.Property;
import edacc.model.PropertyDAO;
import edacc.satinstances.ConvertException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.table.AbstractTableModel;
import java.util.Vector;

/**
 *
 * @author daniel, simon
 */
public class InstanceTableModel extends AbstractTableModel {

    /** The index of the name column */
    public static final int COL_NAME = 0;
    /** The index of the benchmark type column */
    public static final int COL_BENCHTYPE = 1;
    /** The index of the selected column */
    public static final int COL_SELECTED = 2;
    /** The index of the first property column */
    public static final int COL_PROP = 3;
    private String[] columns = {"Name", "Benchmark Type", "Selected"};
    ArrayList<Property> properties;
    private ArrayList<Instance> instances;
    private Vector<ExperimentHasInstance> experimentHasInstances;
    private HashMap<Integer, ExperimentHasInstance> selectedInstances;
    private Vector<ExperimentHasInstance> savedExperimentInstances;
    private String[] benchmarkTypes;
    private HashMap<Instance, LinkedList<Integer>> instanceClassIds;

    /**
     * Sets the instances for this model. Also updates the properties if <code>updateProperties</code> is true. 
     * @param instances the instances to be set
     * @param filterInstanceClassIds if true, reload instance class ids from the db (used for edacc.EDACCInstanceFilter)
     * @param updateProperties if true, properties are updated from the db
     * @see edacc.EDACCInstanceFilter
     */
    public void setInstances(ArrayList<Instance> instances, boolean filterInstanceClassIds, boolean updateProperties) {
        boolean isCompetition;

        try {
            isCompetition = DatabaseConnector.getInstance().isCompetitionDB();
        } catch (Exception e) {
            isCompetition = false;
        }
        updateProperties(updateProperties || properties == null);
        this.instances = instances;
        experimentHasInstances = new Vector<ExperimentHasInstance>();
        experimentHasInstances.setSize(instances.size());
        if (instances == null) {
            benchmarkTypes = null;
        } else if (isCompetition) {
            benchmarkTypes = new String[instances.size()];
            try {
                HashMap<Integer, String> types = InstanceDAO.getBenchmarkTypes();
                for (int i = 0; i < instances.size(); i++) {
                    benchmarkTypes[i] = types.get(instances.get(i).getId());

                }
            } catch (Exception e) {
            }

        }
        if (filterInstanceClassIds) {
            instanceClassIds = new HashMap<Instance, LinkedList<Integer>>();
            for (Instance i : instances) {
                instanceClassIds.put(i, new LinkedList<Integer>());
            }
            try {
                InstanceHasInstanceClassDAO.fillInstanceClassIds(instanceClassIds);
            } catch (SQLException ex) {
                // TODO: error
            }
        }
        fireTableDataChanged();
    }

    /**
     * Returns the instance class ids for the instance represented by this row
     * @param rowIndex the row index
     * @return <code>LinkedList</code> of class ids
     */
    public LinkedList<Integer> getInstanceClassIdsForRow(int rowIndex) {
        if (instanceClassIds == null) {
            return null;
        }
        LinkedList<Integer> res = instanceClassIds.get(instances.get(rowIndex));
        return res == null ? new LinkedList<Integer>() : res;
    }

    private void updateProperties(boolean updateProperties) {
        if (updateProperties) {
            properties = new ArrayList<Property>();
        }
        // TODO: fix!
        try {
            if (updateProperties) {
                properties.addAll(PropertyDAO.getAllInstanceProperties());
            }
            if (properties.size() > 0) {
                columns = java.util.Arrays.copyOf(columns, COL_PROP + properties.size());
                for (int i = COL_PROP; i < columns.length; i++) {
                    columns[i] = properties.get(i - COL_PROP).getName();
                }
            }
        } catch (Exception e) {
            if (edacc.ErrorLogger.DEBUG) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Deselects all selected instances and selects all instances which are specified
     * by the given parameter experimentHasInstances.
     * @param experimentHasInstances
     */
    public void setExperimentHasInstances(Vector<ExperimentHasInstance> experimentHasInstances) {
        this.savedExperimentInstances = experimentHasInstances;
        selectedInstances.clear();
        for (ExperimentHasInstance ehi : experimentHasInstances) {
            selectedInstances.put(ehi.getInstances_id(), ehi);
        }
        for (int i = 0; i < this.experimentHasInstances.size(); i++) {
            this.experimentHasInstances.set(i, null);
        }
        for (int i = 0; i < experimentHasInstances.size(); i++) {
            for (int j = 0; j < this.experimentHasInstances.size(); j++) {
                if (instances.get(j).getId() == experimentHasInstances.get(i).getInstances_id()) {
                    this.experimentHasInstances.set(j, experimentHasInstances.get(i));
                    break;
                }
            }
        }
    }

    /**
     * Loads the last save state.
     */
    public void undo() {
        setExperimentHasInstances(this.savedExperimentInstances);
        this.fireTableDataChanged();
    }

    /**
     * Returns a vector with all instance ids for which there is no
     * corresponding ExperimentHasInstance.
     * @return Vector of the new instance ids
     */
    public Vector<Integer> getNewInstanceIds() {
        Vector<Integer> res = new Vector<Integer>();
        for (Integer instanceId : selectedInstances.keySet()) {
            ExperimentHasInstance ehi = selectedInstances.get(instanceId);
            if (ehi == null) {
                res.add(instanceId);
            }
        }
        return res;
    }

    /**
     * Returns the instance represented by this row
     * @param rowIndex the row index
     * @return the instance
     */
    public Instance getInstanceAt(int rowIndex) {
        return instances.get(rowIndex);
    }

    /**
     * Returns a vector with all deselected ExperimentHasInstance objects which
     * were specified by setExperimentHasInstances()
     * @return arraylist of the ExperimentHasInstance objects
     */
    public ArrayList<ExperimentHasInstance> getDeletedExperimentHasInstances() {
        ArrayList<ExperimentHasInstance> res = new ArrayList<ExperimentHasInstance>();
        if (savedExperimentInstances == null) {
            return res;
        }
        for (ExperimentHasInstance ehi : savedExperimentInstances) {
            if (!selectedInstances.containsKey(ehi.getInstances_id())) {
                res.add(ehi);
            }
        }
        return res;
    }

    /**
     * Checks if some data is modified
     * @return true, if some data is modified
     */
    public boolean isModified() {
        return getDeletedExperimentHasInstances().size() > 0 || getNewInstanceIds().size() > 0;
    }

    /**
     * Updates the instance selection according to the specified <code>ExperimentHasInstance</code>
     * @param e the ExperimentHasInstance
     * @param row the row index
     */
    public void setExperimentHasInstance(ExperimentHasInstance e, int row) {
        this.experimentHasInstances.set(row, e);
    }

    /** Creates a new instance table model */
    public InstanceTableModel() {
        this.instances = new ArrayList<Instance>();
        this.experimentHasInstances = new Vector<ExperimentHasInstance>();
        selectedInstances = new HashMap<Integer, ExperimentHasInstance>();
    }

    /**
     * Returns all selected instances
     * @return <code>ArrayList</code> of selected instances
     */
    public ArrayList<Instance> getSelectedInstances() {
        ArrayList<Instance> res = new ArrayList<Instance>();
        for (Instance instance : instances) {
            if (selectedInstances.containsKey(instance.getId())) {
                res.add(instance);
            }
        }
        return res;
    }

    @Override
    public int getRowCount() {
        return instances.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Class getColumnClass(int col) {
        if (this.getRowCount() == 0) {
            return String.class;
        } else {
            if (col >= COL_PROP) {
                int propertyIdx = col - COL_PROP;
                if (propertyIdx < properties.size()) {
                    return properties.get(propertyIdx).getPropertyValueType().getJavaType();
                } else {
                    return String.class;
                }
            }
            return getValueAt(0, col).getClass();
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == COL_SELECTED;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == COL_SELECTED) {
            if ((Boolean) value) {
                selectedInstances.put(instances.get(row).getId(), experimentHasInstances.get(row));
            } else {
                selectedInstances.remove(instances.get(row).getId());
            }
        }
        this.fireTableCellUpdated(row, col);
    }

    /**
     * Returns the <code>ExperimentHasInstance</code> represented by this row
     * @param rowIndex the row index
     * @return 
     */
    public ExperimentHasInstance getExperimentHasInstance(int rowIndex) {
        return experimentHasInstances.get(rowIndex);
    }

    /**
     * Returns the instance represented by this row
     * @param rowIndex the row index
     * @return the instnace
     */
    public Instance getInstance(int rowIndex) {
        return instances.get(rowIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == COL_NAME) {
            return instances.get(rowIndex).getName();
        } else if (columnIndex == COL_BENCHTYPE) {
            return (benchmarkTypes == null || benchmarkTypes[rowIndex] == null) ? "" : benchmarkTypes[rowIndex];
        } else if (columnIndex == COL_SELECTED) {
            return selectedInstances.containsKey(instances.get(rowIndex).getId());
        } else {
            int propertyIdx = columnIndex - COL_PROP;
            if (properties.size() <= propertyIdx || instances.get(rowIndex).getPropertyValues() == null) {
                return null;
            }
            InstanceHasProperty ip = instances.get(rowIndex).getPropertyValues().get(properties.get(propertyIdx).getId());
            if (ip == null || ip.getValue() == null) {
                return null;
            }
            try {
                return properties.get(propertyIdx).getPropertyValueType().getJavaTypeRepresentation(ip.getValue());
            } catch (ConvertException ex) {
                return null;
            }
        }
    }

    /**
     * Returns the default visibility for all known columns including the benchmark type column, also if this is not a competition db.
     * @return array of boolean representing the visibility of the columns
     */
    public boolean[] getDefaultVisibility() {
        boolean[] res = new boolean[columns.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = false;
        }
        res[0] = true;
        res[1] = true;
        res[2] = true;
        return res;
    }  
}
