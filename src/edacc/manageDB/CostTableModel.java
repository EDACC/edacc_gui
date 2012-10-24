package edacc.manageDB;

import edacc.model.Cost;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author simon
 */
public class CostTableModel extends DefaultTableModel {

    public static final int COL_NAME = 0;
    private static final String[] columns = {"Name"};
    private ArrayList<Cost> costs;

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        Cost cost = costs.get(row);
        switch (column) {
            case COL_NAME:
                return cost.getName();
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == COL_NAME;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        switch (column) {
            case COL_NAME:
                costs.get(row).setName((String) aValue);
                break;
        }
    }

    @Override
    public int getRowCount() {
        return costs == null ? 0 : costs.size();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }
    
    public void setCosts(List<Cost> costs) {
        if (costs == null) {
            this.costs = null;
        } else {
            this.costs = new ArrayList<Cost>();
            this.costs.addAll(costs);
        }
        this.fireTableDataChanged();
    }

    public void addCost(Cost cost) {
        costs.add(cost);
        this.fireTableRowsInserted(costs.size()-1, costs.size()-1);
    }

    public Cost getCost(int row) {
        return costs.get(row);
    }

    public void removeCost(int row) {
        if (costs.get(row).isModified() || costs.get(row).isSaved()) {
            costs.get(row).setDeleted();
        }
        costs.remove(row);
    }
    
    public List<Cost> getCosts() {
        return costs;
    }
}
