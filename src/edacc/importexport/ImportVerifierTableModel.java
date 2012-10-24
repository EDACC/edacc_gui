package edacc.importexport;

import edacc.experiment.ThreadSafeDefaultTableModel;
import edacc.model.Verifier;
import javax.swing.JComboBox;

/**
 *
 * @author simon
 */
public class ImportVerifierTableModel extends ThreadSafeDefaultTableModel {

    private static final String[] columnNames = {"Name", "Parameters", "Action", "Name for new Verifier"};
    private JComboBox[] combos;
    private Verifier[] verifiers;
    private String[] names;

    public void setData(Verifier[] verifiers, JComboBox[] combos) {
        this.combos = combos;
        this.verifiers = verifiers;
        this.names = new String[verifiers.length];
        for (int i = 0; i < verifiers.length; i++) {
            names[i] = verifiers[i].getName();
        }
        fireTableDataChanged();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return String.class;
        } else if (columnIndex == 1) {
            return String.class;
        } else if (columnIndex == 2) {
            return JComboBox.class;
        } else if (columnIndex == 3) {
            return String.class;
        }
        return null;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return verifiers == null ? 0 : verifiers.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return verifiers[row].getName();
        } else if (column == 1) {
            return edacc.manageDB.Util.getVerifierParameterString(verifiers[row]);
        } else if (column == 2) {
            return combos[row];
        } else if (column == 3) {
            if (combos[row].getSelectedItem() instanceof String) {
                return names[row];
            } else {
                return "";
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 2 || column == 3 && (combos[row].getSelectedItem() instanceof String);
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        if (column == 2) {
            fireTableRowsUpdated(row, row);
        } else if (column == 3) {
            names[row] = (String) aValue;
            this.fireTableRowsUpdated(row, row);
        }
    }

    public Verifier getVerifierAt(int row) {
        return verifiers[row];
    }

    public JComboBox getComboBoxAt(int row) {
        return combos[row];
    }

    public String getVerifierNameAt(int row) {
        if (combos[row].getSelectedItem() instanceof String) {
            return names[row];
        } else {
            return null;
        }
    }
}
