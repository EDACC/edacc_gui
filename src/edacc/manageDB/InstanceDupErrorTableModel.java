/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import edacc.experiment.ThreadSafeDefaultTableModel;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import javax.swing.table.DefaultTableModel;

/**
 * TableModel created for the Table with the duplicate instances which caused an error by adding a new 
 * instance into the database.
 * Used in the EDACCAddInstanceErrorDialog.
 * @author rretz
 */
public class InstanceDupErrorTableModel extends DefaultTableModel {

    private AddInstanceErrorController controller;
    private ArrayList<Instance> instances = new ArrayList<Instance>();
    private String[] columns = {"Name", "MD5", "Link"};
    // <id of instance to link, error causing instance to link to>
    private HashMap<Integer, Instance> toLink = new HashMap<Integer, Instance>();
    // <duplicate Instance, error causing instance>
    private HashMap<Instance, Instance> relatedInstances = new HashMap<Instance, Instance>();
    // <error causing instance, all related duplicate instances>
    private HashMap<Instance, ArrayList<Instance>> backRelation;

    InstanceDupErrorTableModel(HashMap<Instance, ArrayList<Instance>> duplicate, AddInstanceErrorController controller) {
        this.controller = controller;
        Set<Instance> keys = duplicate.keySet();
        for (Instance causedInstance : keys) {
            ArrayList<Instance> tmp = duplicate.get(causedInstance);
            for (Instance dupInstance : tmp) {

                relatedInstances.put(dupInstance, causedInstance);
            }
        }
        this.instances = new ArrayList<Instance>(relatedInstances.keySet());

        this.backRelation = duplicate;
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (col) {
            case 0:
                return instances.get(row).getName();
            case 1:
                return instances.get(row).getMd5();
            case 2:
                if (toLink.containsKey(instances.get(row).getId())) {
                    return true;
                } else {
                    return false;
                }
            default:
                return null;
        }
    }

    @Override
    public int getRowCount() {
        return instances == null ? 0 : instances.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Class getColumnClass(int col) {
        if (instances.isEmpty()) {
            return String.class;
        } else {
            return getValueAt(0, col).getClass();
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 3;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 3) {
            Boolean check = true;
            ArrayList<Instance> toCheck = backRelation.get(relatedInstances.get(instances.get(row)));
            for (Instance tmp : toCheck) {
                if (toLink.containsKey(tmp)) {
                    check = false;
                }
            }

            if (check) {
                if ((Boolean) value) {
                    toLink.put(instances.get(row).getId(), relatedInstances.get(instances.get(row)));
                } else {
                    toLink.remove(instances.get(row).getId());
                }
            }

        }
        this.fireTableCellUpdated(row, col);
    }

    /**
     * Removes all duplicate Instances from this table which are related with the given Instances.
     * @param removeDups 
     */
    public void remove(ArrayList<Instance> removeDups) {
        for (Instance causedInstance : removeDups) {
            ArrayList<Instance> tmp = backRelation.get(causedInstance);
            for (Instance dupInstance : tmp) {
                relatedInstances.remove(dupInstance);
                toLink.remove(dupInstance.getId());
                instances.remove(dupInstance);
            }
            backRelation.remove(causedInstance);
        }

    }

    /**
     * Returns the error causing instance object which is related to the given duplicate Instance
     */
    public Instance getRelatedErrorInstance(int id) throws SQLException {       
        return relatedInstances.get(instances.get(id));
    }

    /**
     * Removes all duplicate instances from the Model
     * @param instance 
     */
    public void removeDups(Instance instance) {
        Set<Instance> keySet = backRelation.keySet();
        ArrayList<Instance> toRemove = backRelation.get(instance);
        for(Instance remove : toRemove){
            instances.remove(remove);
            relatedInstances.remove(remove);    
            toLink.remove(remove.getId());
        }
        backRelation.remove(instance);
        
    }
    
    /**
     * 
     * @return 
     */
    public HashMap<Integer, Instance> getSelected(){
        return toLink;
    }
            
}
