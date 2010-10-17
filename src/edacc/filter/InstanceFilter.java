package edacc.filter;

import edacc.experiment.InstanceTableModel;
import java.util.HashSet;
import javax.swing.JTable;
import javax.swing.RowFilter.Entry;

/**
 *
 * @author simon
 */
public class InstanceFilter extends Filter {

    private HashSet<Integer> instanceClassIds;
    private boolean filterInstanceClasses;
    private InstanceTableModel model;

    public InstanceFilter(java.awt.Frame parent, boolean modal, JTable table, boolean autoUpdateFilterTypes) {
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
        int instanceClassId = model.getInstanceAt((Integer) entry.getIdentifier()).getInstanceClass().getId();
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

    public void addInstanceClassId(int id) {
        instanceClassIds.add(id);
    }

    public void removeInstanceClassId(int id) {
        instanceClassIds.remove(id);
    }

    public void clearInstanceClassIds() {
        instanceClassIds.clear();
    }

    public void setFilterInstanceClasses(boolean filterInstanceClasses) {
        this.filterInstanceClasses = filterInstanceClasses;
    }
}
