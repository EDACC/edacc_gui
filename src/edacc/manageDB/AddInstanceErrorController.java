/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import edacc.EDACCAddInstanceErrorDialog;
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
    private EDACCAddInstanceErrorDialog main;
    private InstanceDupErrorFilter filter;
    
    public AddInstanceErrorController(ArrayList<Instance> toAdd, HashMap<Instance, ArrayList<Instance>> duplicate, EDACCAddInstanceErrorDialog main) {
        duplicateModel = new InstanceDupErrorTableModel(duplicate);
        duplicateSorter = new TableRowSorter<InstanceDupErrorTableModel>();
        
        toAddModel = new InstanceErrorTableModel(toAdd);      
        toAddSorter = new TableRowSorter<InstanceErrorTableModel>();
        
        filter = new InstanceDupErrorFilter(duplicateModel);
        
        this.main = main;
    }

    public InstanceErrorTableModel getToAddModel() {
        return toAddModel;
    }
    
    public TableRowSorter getToAddSorter(){
        return toAddSorter;
    }

    public InstanceDupErrorTableModel getDuplicateModel() {
        return duplicateModel;
    }
    
    public TableRowSorter getDuplicateSorter(){
        return duplicateSorter;
    }
    
    public InstanceDupErrorFilter getFilter(){
        return filter;
    }

    public void updateFilter() {       
        filter.setSelectedInstance(toAddModel.getInstance(main.getSelectedToAddInstance()).getId());
    }
    
}
