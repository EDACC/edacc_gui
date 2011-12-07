/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import edacc.EDACCAddInstanceErrorDialog;
import edacc.model.Instance;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JTable;
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

    public AddInstanceErrorController(HashMap<Instance, ArrayList<Instance>> duplicate, EDACCAddInstanceErrorDialog main) {
        duplicateModel = new InstanceDupErrorTableModel(duplicate);
        duplicateSorter = new TableRowSorter<InstanceDupErrorTableModel>();



        ArrayList<Instance> toAdd = new ArrayList<Instance>(duplicate.keySet());
        toAddModel = new InstanceErrorTableModel(toAdd);
        toAddSorter = new TableRowSorter<InstanceErrorTableModel>();

        this.main = main;
    }

    public InstanceErrorTableModel getToAddModel() {
        return toAddModel;
    }

    public TableRowSorter getToAddSorter() {
        return toAddSorter;
    }

    public InstanceDupErrorTableModel getDuplicateModel() {
        return duplicateModel;
    }

    public TableRowSorter getDuplicateSorter() {
        return duplicateSorter;
    }

    public InstanceDupErrorFilter getFilter() {
        return filter;
    }

    public void updateFilter() {
        filter.setSelectedInstance(toAddModel.getInstance(main.getSelectedToAddInstance()));
        main.sort();
        this.duplicateModel.fireTableDataChanged();
    }

    public void drop(ArrayList<Instance> allInstances) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void setFilter(InstanceDupErrorFilter rowFilter) {
        this.filter = rowFilter;
    }
}
