package edacc.importexport;

import edacc.experiment.ThreadSafeDefaultTableModel;
import edacc.model.Solver;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JComboBox;

/**
 *
 * @author simon
 */
public class ImportSolverTableModel extends ThreadSafeDefaultTableModel {
    
    private static final String[] columnNames = {"Name", "Parameters", "Action", "Name for new Solver"};
    private JComboBox[] combos;
    private Solver[] solvers;
    private String[] names;
    private HashMap<String, Integer> nameMap;
    
    public ImportSolverTableModel(HashSet<String> invalidNames) {
        nameMap = new HashMap<String, Integer>();
        for (String s : invalidNames) {
            nameMap.put(s, 1);
        }
    }
    
    public void setData(Solver[] solvers, JComboBox[] combos) {
        this.combos = combos;
        this.solvers = solvers;
        this.names = new String[solvers.length];
        for (int i = 0; i< solvers.length; i++) {
            names[i] = solvers[i].getName();
            if (combos[i].getSelectedItem() instanceof String) {
                Integer c = nameMap.get(names[i]);
                if (c == null)
                    c = 0;
                c++;
                nameMap.put(names[i], c);
            }
        }
        this.fireTableDataChanged();
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
        return solvers == null ? 0 : solvers.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return solvers[row].getName();
        } else if (column == 1) {
            return "";
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
            Integer c = nameMap.get(names[row]);
            if (c == null) {
                c = 0;
            }
            if (combos[row].getSelectedItem() instanceof String) {
                nameMap.put(names[row], c+1);
            } else {
                if (c <= 1) {
                    nameMap.remove(names[row]);
                } else {
                    nameMap.put(names[row], c-1);
                }
            }
            
            
        } else if (column == 3) {
            Integer c = nameMap.get(names[row]);
            if (c == null || c <= 1) {
                nameMap.remove(names[row]);
            } else {
                nameMap.put(names[row], c-1);
            }
            names[row] = (String) aValue;
            c = nameMap.get(names[row]);
            if (c == null) {
                c = 0;
            }
            c++;
            nameMap.put(names[row], c);
            this.fireTableRowsUpdated(row, row);
        }
    }
    
    public Solver getSolverAt(int row) {
        return solvers[row];
    }
    
    public JComboBox getComboBoxAt(int row) {
        return combos[row];
    }

    public String getSolverNameAt(int row) {
        if (combos[row].getSelectedItem() instanceof String) {
            return names[row];
        } else {
            return null;
        }
    }
    
    public HashMap<String, Integer> getNameMap() {
        return nameMap;
    }
}
