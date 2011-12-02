/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import edacc.model.Instance;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author rretz
 */
public class AddInstanceErrorController {
    private InstanceDupErrorTableModel duplicateModel;
    private TableRowSorter<InstanceDupErrorTableModel> duplicateSorter;
    private InstanceErrorTableModel toAddModel;
    private TableRowSorter<InstanceErrorTableModel> toAddSorter;
    
    public AddInstanceErrorController(ArrayList<Instance> toAdd, HashMap<Instance, ArrayList<Instance>> duplicate) {
        duplicateModel = new InstanceDupErrorTableModel(duplicate);
        duplicateSorter = new TableRowSorter<InstanceDupErrorTableModel>();
        
        toAddModel = new InstanceErrorTableModel(toAdd);      
        toAddSorter = new TableRowSorter<InstanceErrorTableModel>();
    }

    public TableModel getToAddModel() {
        return toAddModel;
    }
    
    public TableRowSorter getToAddSorter(){
        return toAddSorter;
    }

    public TableModel getDuplicateModel() {
        return duplicateModel;
    }
    
    public TableRowSorter getDuplicateSorter(){
        return duplicateSorter;
    }
    
}
