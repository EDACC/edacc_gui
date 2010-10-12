/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.experiment;

import edacc.filter.InstanceFilter;
import edacc.model.Instance;
import edacc.model.InstanceClass;
import edacc.model.InstanceDAO;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author rretz
 */
public class ExperimentInstanceClassTableModel extends AbstractTableModel {

    private String[] columns = {"Name", "Description", "Source", "Show"};
    protected Vector<InstanceClass> classes;
    protected Vector<Boolean> classSelect;
    protected InstanceFilter filter;
    protected InstanceTableModel model;
    protected ExperimentController expController;
    private boolean update;

    public ExperimentInstanceClassTableModel(InstanceTableModel model, InstanceFilter filter, ExperimentController expController) {
        this.expController = expController;
        this.filter = filter;
        this.model = model;
        this.update = false;
    }

    @Override
    public int getRowCount() {
        return classes==null?0:classes.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Class getColumnClass(int column) {
        if (getRowCount() == 0) {
            return String.class;
        } else {
            return getValueAt(0, column).getClass();
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return classes.get(rowIndex).getName();
            case 1:
                return classes.get(rowIndex).getDescription();
            case 2:
                return classes.get(rowIndex).isSource() ? "\u2713" : "";
            case 3:
                return classSelect.get(rowIndex);
            case 4:
                return classes.get(rowIndex);
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 3) {
            return true;
        }
        return false;
    }

    public void setClasses(Vector<InstanceClass> classes) {
        if (classes != null) {
            classSelect = new Vector<Boolean>();
            for (int i = 0; i < classes.size(); i++) {
                this.classSelect.add(false);
            }
        }
        this.classes = classes;
        filter.clearInstanceClassIds();
    }

    public void beginUpdate() {
        update = true;
    }

    public void endUpdate() {
        update = false;
        this.fireTableDataChanged();
        model.fireTableDataChanged();
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 3) {
            boolean selected = (Boolean) value;
            
            classSelect.set(row, selected);
            if (selected) {

                filter.addInstanceClassId(classes.get(row).getId());
            } else {
                filter.removeInstanceClassId(classes.get(row).getId());
            }
        }
        if (!update) {
            fireTableCellUpdated(row, col);
            model.fireTableDataChanged();
        }

    }

    public Vector<InstanceClass> getAllChoosen() {
        Vector<InstanceClass> choosen = new Vector<InstanceClass>();
        for (int i = 0; i < classes.size(); i++) {
            if (classSelect.get(i)) {
                choosen.add(classes.get(i));
            }
        }
        return choosen;
    }

    public void setInstanceClassSelected(int row) {
        setValueAt(true, row, 3);
    }

    public void setInstanceClassDeselected(int row) {
        setValueAt(false, row, 3);
    }
}
