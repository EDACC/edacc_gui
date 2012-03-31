package edacc.importexport;

import edacc.model.Verifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author simon
 */
public class VerifierFixedSelectionTableModel extends DefaultTableModel {

    public static final int COL_NAME = 0;
    public static final int COL_PARAMETERS = 1;
    public static final int COL_SELECTED = 2;
    private final String[] columns = {"Name", "Parameters", "Selected"};
    private List<Verifier> verifiers;
    private boolean[] selected;
    private boolean[] fixed;

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
        return verifiers == null ? 0 : verifiers.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        switch (column) {
            case COL_NAME:
                return verifiers.get(row).getName();
            case COL_PARAMETERS:
                return edacc.manageDB.Util.getVerifierParameterString(verifiers.get(row));
            case COL_SELECTED:
                return selected[row] || fixed[row];
            default:
                return "";
        }
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        if (column == COL_SELECTED) {
            selected[row] = (Boolean) aValue;
        }
    }
    
    

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == COL_SELECTED && !fixed[row]) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == COL_SELECTED) {
            return Boolean.class;
        }
        return String.class;
    }

    public void setVerifiers(List<Verifier> verifiers) {
        this.verifiers = verifiers;
        if (verifiers == null) {
            fixed = new boolean[0];
            selected = new boolean[0];
        } else {
            fixed = new boolean[verifiers.size()];
            selected = new boolean[verifiers.size()];
        }
        this.fireTableDataChanged();
    }

    public Verifier getVerifier(int rowIndex) {
        return verifiers.get(rowIndex);
    }

    public List<Verifier> getSelectedVerifiers() {
        List<Verifier> res = new LinkedList<Verifier>();
        for (int i = 0; i < getRowCount(); i++) {
            if (selected[i] || fixed[i]) {
                res.add(verifiers.get(i));
            }
        }
        return res;
    }

    public boolean isFixed(int row) {
        return fixed[row];
    }

    public void clearFixedVerifiers() {
        if (fixed.length == 0) {
            return ;
        }
        for (int i = 0; i < fixed.length; i++) {
            fixed[i] = false;
        }
        this.fireTableRowsUpdated(0, fixed.length - 1);
    }

    public void setVerifierFixed(int vid, boolean value) {
        for (int i = 0; i < fixed.length; i++) {
            if (verifiers.get(i).getId() == vid) {
                fixed[i] = value;
                this.fireTableRowsUpdated(i, i);
                break;
            }
        }
    }
}
