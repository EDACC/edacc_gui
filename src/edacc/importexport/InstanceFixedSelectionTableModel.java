package edacc.importexport;

import edacc.experiment.InstanceTableModel;
import edacc.model.Instance;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author simon
 */
public class InstanceFixedSelectionTableModel extends InstanceTableModel {
        private boolean[] fixed;

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col == InstanceTableModel.COL_SELECTED && fixed[row])
            return false;
        return super.isCellEditable(row, col);
    }
    
    public boolean isSelected(int row) {
        if (fixed[row])
            return true;
        return (Boolean) super.getValueAt(row, InstanceTableModel.COL_SELECTED);
    }

    @Override
    public void setInstances(List<Instance> instances, boolean filterInstanceClassIds, boolean updateProperties) {
        fixed = new boolean[instances.size()];
        for (int i = 0; i < fixed.length; i++)
            fixed[i] = false;
        super.setInstances(instances, filterInstanceClassIds, updateProperties);
    }
    
    public void setInstanceFixed(int iid, boolean value) {
        for (int i = 0; i < fixed.length; i++)
            if (super.getInstanceAt(i).getId() == iid) {
                fixed[i] = value;
                this.fireTableRowsUpdated(i, i);
                break;
            }
    }

    public void clearFixedInstances() {
        for (int i = 0; i < fixed.length; i++)
            fixed[i] = false;
        this.fireTableRowsUpdated(0, fixed.length-1);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == InstanceTableModel.COL_SELECTED && fixed[rowIndex]) {
            return true;
        }
        return super.getValueAt(rowIndex, columnIndex);
    }
    
    @Override
    public List<Instance> getSelectedInstances() {
        List<Instance> res = new ArrayList<Instance>();
        for (int row = 0; row < super.getRowCount(); row++) {
            if (isSelected(row))
                res.add(super.getInstanceAt(row));
        }
        return res;
    }
    
    public boolean isFixed(int row) {
        return fixed[row];
    }
}
