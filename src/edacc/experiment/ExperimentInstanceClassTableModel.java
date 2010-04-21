/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.experiment;

import edacc.manageDB.InstanceClassTableModel;
import edacc.model.ExperimentHasInstance;
import edacc.model.ExperimentHasInstanceDAO;
import edacc.model.Instance;
import edacc.model.InstanceClass;
import edacc.model.InstanceDAO;
import edacc.model.NoConnectionToDBException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author rretz
 */
public class ExperimentInstanceClassTableModel extends AbstractTableModel{
     private String[] columns = {"Name", "Description", "Source", "Select"};
     protected Vector <InstanceClass> classes;
     protected Vector <Boolean> classSelect;
     protected JTable instanceTable;
     protected ExperimentController expController;


    public ExperimentInstanceClassTableModel(JTable instanceTable) {

    }

    public ExperimentInstanceClassTableModel(JTable tableInstances, ExperimentController expController) {
        this.classes = new Vector <InstanceClass>();
        this.classSelect = new Vector <Boolean>();
        this.instanceTable = tableInstances;
        this.expController = expController;
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

    @Override
    public Class getColumnClass(int column){
        if(column == 2 || column == 3) return Boolean.class;
        return String.class;
    }

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
    public boolean isCellEditable(int rowIndex, int columnIndex){
        if( columnIndex == 3) return true;
        return false;
    }

    public void addClass(InstanceClass instanceClass){
        this.classes.add(instanceClass);
        this.classSelect.add(false);
    }

    public void addClasses (Vector <InstanceClass> classes){
        for(int i = 0; i < classes.size(); i++){
            this.classSelect.add(false);
        }
        this.classes.addAll(classes);
    }

    public void setClasses (Vector <InstanceClass> classes){
        for(int i = 0; i < classes.size(); i++){
            this.classSelect.add(false);
        }
        this.classes = classes;
    }
    
    public void removeClass(int row){
        this.classes.remove(row);
        this.classSelect.remove(row);
    }

    public void setInstanceClassSelected(int row){
            setValueAt(true, row, 3 );
    }


    @Override
     public void setValueAt(Object value, int row, int col) {
        if(col == 3){
            classSelect.set(row, (Boolean) value);
            try {
                changeInstanceTable();
            } catch (NoConnectionToDBException ex) {
                Logger.getLogger(InstanceClassTableModel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(InstanceClassTableModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        fireTableCellUpdated(row, col);

    }

    public Vector<InstanceClass> getAllChoosen(){
        Vector<InstanceClass> choosen = new Vector<InstanceClass>();
        for(int i = 0; i < classes.size(); i++){
            if(classSelect.get(i)){
                choosen.add(classes.get(i));
            }
        }
        return choosen;
    }
    public void changeInstanceTable() throws NoConnectionToDBException, SQLException {
        ((InstanceTableModel)instanceTable.getModel()).clearTable();
        expController.removeAllInstancesFromVector();
        Vector<InstanceClass> choosen = getAllChoosen();
        if(!choosen.isEmpty()){
            int expId = expController.getActiveExperiment().getId();
            Vector<Instance> temp = new Vector<Instance> (InstanceDAO.getAllByInstanceClasses(getAllChoosen()));
            Vector<ExperimentHasInstance> vExpHasInst = ExperimentHasInstanceDAO.getExperimentHasInstanceByExperimentId(expId);
            Hashtable<Integer, ExperimentHasInstance> expHasInst = new Hashtable();

            for(int i = 0; i < vExpHasInst.size(); i++ ){
                expHasInst.put(vExpHasInst.get(i).getInstances_id(), vExpHasInst.get(i));
            }

            for(int i = 0; i < temp.size(); i++){
                if(expHasInst.containsKey(temp.get(i).getId())){                  
                     ((InstanceTableModel)instanceTable.getModel()).setInstancesAndExperimentHasInstance(temp.get(i), expHasInst.get(temp.get(i).getId()));
                }else ((InstanceTableModel)instanceTable.getModel()).setInstancesWithoutExp(temp.get(i));
            }
            expController.addInstancesToVector(new Vector<Instance>(temp));
        }
    }

    void setInstanceClassDeselected(int row) {
        setValueAt(false, row, 3 );
    }

}
