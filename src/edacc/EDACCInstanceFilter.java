package edacc;

import edacc.experiment.InstanceTableModel;
import java.util.HashSet;
import javax.swing.JTable;
import javax.swing.RowFilter.Entry;

/**
 * This is a Filter with the extension of using instance classes to filter also.
 * @author simon
 */
public class EDACCInstanceFilter extends EDACCFilter {

    private HashSet<Integer> instanceClassIds;
    private boolean filterInstanceClasses;
    private InstanceTableModel model;

    /**
     * Throws IllegalArgumentException if the Table Model of the table is not an instance of InstanceTableModel.
     * @param parent
     * @param modal
     * @param table
     * @param autoUpdateFilterTypes
     */
    public EDACCInstanceFilter(java.awt.Frame parent, boolean modal, JTable table, boolean autoUpdateFilterTypes) {
        super(parent, modal, table, autoUpdateFilterTypes);
        if (!(table.getModel() instanceof InstanceTableModel)) {
            throw new IllegalArgumentException("Expected instance table model.");
        }
        model = (InstanceTableModel) table.getModel();
        instanceClassIds = new HashSet<Integer>();
        filterInstanceClasses = true;
    }

    @Override
    public boolean include(Entry<? extends Object, ? extends Object> entry) {
        //int instanceClassId = model.getInstanceAt((Integer) entry.getIdentifier()).getInstanceClass().getId();
        int instanceClassId = -10;
        if (filterInstanceClasses && !instanceClassIds.contains(instanceClassId)) {
            return false;
        }
        return super.include(entry);
    }

    @Override
    public void clearFilters() {
        super.clearFilters();
        instanceClassIds.clear();
    }

    /**
     * Add an instance class id to be included to the fillter.
     * @param id
     */
    public void addInstanceClassId(int id) {
        instanceClassIds.add(id);
    }

    /**
     * Remove an instance class id to be excluded from the filter.
     * @param id
     */
    public void removeInstanceClassId(int id) {
        instanceClassIds.remove(id);
    }

    /**
     * Remove all instance class ids.
     */
    public void clearInstanceClassIds() {
        instanceClassIds.clear();
    }

    /**
     * If this is set to true class ids will also be filtered.
     * @param filterInstanceClasses
     */
    public void setFilterInstanceClasses(boolean filterInstanceClasses) {
        this.filterInstanceClasses = filterInstanceClasses;
    }
}
