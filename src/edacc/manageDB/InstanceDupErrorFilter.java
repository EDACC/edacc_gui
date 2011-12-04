/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import edacc.model.Instance;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;

/**
 *
 * @author rretz
 */
public class InstanceDupErrorFilter extends RowFilter<InstanceDupErrorTableModel, Integer> {

    private InstanceDupErrorTableModel model;
    private int selectedInstanceId;

    public InstanceDupErrorFilter(InstanceDupErrorTableModel model) {
        this.model = model;
        selectedInstanceId = -1;
    }

    @Override
    public boolean include(Entry<? extends InstanceDupErrorTableModel, ? extends Integer> entry) {
        try {
            Instance instance = model.getRelatedErrorInstance((Integer) entry.getIdentifier());
            if (instance.getId() == selectedInstanceId) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(InstanceDupErrorFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public void setSelectedInstance(int id){
        selectedInstanceId = id;
    }
}
