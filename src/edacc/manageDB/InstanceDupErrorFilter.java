/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import edacc.model.Instance;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author rretz
 */
public class InstanceDupErrorFilter extends RowFilter<InstanceDupErrorTableModel, Integer> {

    private InstanceDupErrorTableModel model;
    private Instance selectedInstance ;
    private TableRowSorter< ? extends TableModel> rowSorter;
    private RowFilter<Object, Object> rowFilter;

    public InstanceDupErrorFilter(final InstanceDupErrorTableModel model) {
        this.model = model;
        selectedInstance = null;

       
    }

    public void setSelectedInstance(Instance selectedInstance) {
        this.selectedInstance= selectedInstance;
    }

    @Override
    public boolean include(Entry<? extends InstanceDupErrorTableModel, ? extends Integer> entry) {
        try {
                    ArrayList<Instance> instances = model.getRelatedErrorInstance((Integer) entry.getIdentifier());
                    if(instances == null)
                        return false;
                                
                    if (instances.contains(selectedInstance)) {
                        return true;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(InstanceDupErrorFilter.class.getName()).log(Level.SEVERE, null, ex);
                }
                return false;
    }
}
