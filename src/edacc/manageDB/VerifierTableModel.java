package edacc.manageDB;

import edacc.experiment.ThreadSafeDefaultTableModel;
import edacc.model.Verifier;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author simon
 */
public class VerifierTableModel extends ThreadSafeDefaultTableModel {
    public static final int COL_NAME = 0;
    public static final int COL_PARAMETERS = 1;
    private final String[] columns = {"Name", "Parameters"};
    private List<Verifier> verifiers;

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
                return "tbd";
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }
    
    public void setVerifiers(List<Verifier> verifiers) {
        this.verifiers = verifiers;
        this.fireTableDataChanged();
    }

    void addVerifier(Verifier verifier) {
        if (verifiers == null) {
            verifiers = new ArrayList<Verifier>();
        }
        verifiers.add(verifier);
        fireTableRowsInserted(this.getRowCount()-1, this.getRowCount()-1);
    }

    public Verifier getVerifier(int rowIndex) {
        return verifiers.get(rowIndex);
    }
    
}
