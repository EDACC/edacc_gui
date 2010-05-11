/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.experiment;

import edacc.model.Instance;
import edacc.model.InstanceClass;
import edacc.model.InstanceDAO;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author rretz
 */
public class ExperimentInstanceClassTableModel extends AbstractTableModel {

    private String[] columns = {"Name", "Description", "Source", "Select"};
    protected Vector<InstanceClass> classes;
    protected Vector<Boolean> classSelect;
    protected JTable instanceTable;
    protected ExperimentController expController;
    private boolean update;

    public ExperimentInstanceClassTableModel(JTable tableInstances, ExperimentController expController) {
        this.classes = new Vector<InstanceClass>();
        this.classSelect = new Vector<Boolean>();
        this.instanceTable = tableInstances;
        this.expController = expController;
        this.update = false;
    }

    @Override
    public int getRowCount() {
        return classes.size();
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
        if (column == 2 || column == 3) {
            return Boolean.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return classes.get(rowIndex).getName();
            case 1:
                return classes.get(rowIndex).getDescription();
            case 2:
                return classes.get(rowIndex).isSource();
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

    public void addClass(InstanceClass instanceClass) {
        this.classes.add(instanceClass);
        this.classSelect.add(false);
    }

    public void addClasses(Vector<InstanceClass> classes) {
        for (int i = 0; i < classes.size(); i++) {
            this.classSelect.add(false);
        }
        this.classes.addAll(classes);
    }

    public void setClasses(Vector<InstanceClass> classes) {
        for (int i = 0; i < classes.size(); i++) {
            this.classSelect.add(false);
        }
        this.classes = classes;
    }

    public void removeClass(int row) {
        this.classes.remove(row);
        this.classSelect.remove(row);
    }

    public void beginUpdate() {
        update = true;
    }

    public void endUpdate() {
        update = false;
        try {
            Vector<InstanceClass> selected = getAllChoosen();
            if (!selected.isEmpty()) {
                Vector<Instance> instances = new Vector<Instance>(InstanceDAO.getAllByInstanceClasses(selected));
                ((InstanceTableModel) instanceTable.getModel()).setInstances(instances);
            } else {
                ((InstanceTableModel) instanceTable.getModel()).setInstances(new Vector<Instance>());
            }

        } catch (Exception ex) {
        }
        this.fireTableDataChanged();
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 3) {
            classSelect.set(row, (Boolean) value);
            if (!update) {
                try {
                    Vector<InstanceClass> selected = getAllChoosen();
                    if (!selected.isEmpty()) {
                        Vector<Instance> instances = new Vector<Instance>(InstanceDAO.getAllByInstanceClasses(selected));
                        ((InstanceTableModel) instanceTable.getModel()).setInstances(instances);
                        this.fireTableDataChanged();
                    } else {
                        ((InstanceTableModel) instanceTable.getModel()).setInstances(new Vector<Instance>());
                        this.fireTableDataChanged();
                    }

                } catch (Exception ex) {
                }
            }
        }
        if (!update) {
            fireTableCellUpdated(row, col);
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
