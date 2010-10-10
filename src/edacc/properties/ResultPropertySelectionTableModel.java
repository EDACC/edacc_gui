/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.model.SolverProperty;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author rretz
 */
public class ResultPropertySelectionTableModel extends AbstractTableModel{
    private Vector<SolverProperty> rows = new Vector<SolverProperty>();
    private Vector<Boolean> selected = new Vector<Boolean>();
    private String[] columns = {"Result property", "Selected"};

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 0:
                return rows.get(rowIndex).getName();
            case 1:
                return rows.get(rowIndex);
            default:
                return "";
        }
    }

    @Override
    public String getColumnName(int column){
        return columns[column];
    }

   @Override
    public Class getColumnClass(int column){
        if(column == 0 )
            return String.class;
        else
            return Boolean.class;
    }

    public SolverProperty getResultProperty(int rowIndex){
        return rows.get(rowIndex);
    }

    public void setSelected(boolean select, int rowIndex){
        selected.set(rowIndex, select);
        this.fireTableDataChanged();
    }
    
    public void setValueAt(Object value, int row, int col) {
        if(col == 3){
            selected.set(row, (Boolean) value);
            fireTableCellUpdated(row, col);
        }
        

    }

    public void addResultProperty(SolverProperty toAdd){
        this.rows.add(toAdd);
        this.selected.add(false);
    }

    public void addResultProperties(Vector<SolverProperty> toAdd){
        for(int i = 0; i < toAdd.size(); i++){
            addResultProperty(toAdd.get(i));
        }
        this.fireTableDataChanged();
    }
}
