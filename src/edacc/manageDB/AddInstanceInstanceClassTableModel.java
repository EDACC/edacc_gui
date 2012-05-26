/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.model.InstanceClass;
import edacc.model.InstanceClassDAO;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author rretz
 */
public class AddInstanceInstanceClassTableModel extends AbstractTableModel {
    private String[] columns = {"Name", "Description"};
    protected Vector <InstanceClass> classes;

    public AddInstanceInstanceClassTableModel(){
        this.classes = new Vector <InstanceClass>();
    }

    public int getRowCount() {
        return classes.size();
    }

    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column){
        return columns[column];
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
         switch (columnIndex) {
            case 0:
        try {
            return InstanceClassDAO.getCompletePathOf(classes.get(rowIndex).getId());
        } catch (SQLException ex) {
            Logger.getLogger(AddInstanceInstanceClassTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
            case 1:
                return classes.get(rowIndex).getDescription();
            case 2:
                return classes.get(rowIndex);
            default:
                return "";
        }
    }

    public void addClasses(Vector<InstanceClass> classes){
        this.classes.addAll(classes);
    }

        public void setClasses(Vector<InstanceClass> classes){
            this.classes = classes;
        }


}
