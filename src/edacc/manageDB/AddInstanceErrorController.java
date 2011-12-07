/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import edacc.EDACCAddInstanceErrorDialog;
import edacc.model.Instance;
import edacc.model.InstanceClass;
import edacc.model.InstanceDAO;
import edacc.model.InstanceHasInstanceClass;
import edacc.model.InstanceHasInstanceClassDAO;
import java.sql.SQLException;
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
    private HashMap<Instance, InstanceClass> instanceClasses;

    public AddInstanceErrorController(HashMap<Instance, ArrayList<Instance>> duplicate, EDACCAddInstanceErrorDialog main, HashMap<Instance, InstanceClass> instanceClasses) {
        duplicateModel = new InstanceDupErrorTableModel(duplicate);
        duplicateSorter = new TableRowSorter<InstanceDupErrorTableModel>();


        ArrayList<Instance> toAdd = new ArrayList<Instance>(duplicate.keySet());
        toAddModel = new InstanceErrorTableModel(toAdd);
        toAddSorter = new TableRowSorter<InstanceErrorTableModel>();

        this.instanceClasses = instanceClasses;
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

    public void setFilter(InstanceDupErrorFilter rowFilter) {
        this.filter = rowFilter;
    }

    /**
     * Removes the instances and their related duplicates from both tables.
     * @param rows Row number of the error causing instances to delete.
     */
    public void remove(int[] rows) {
        for (int row : rows) {
            duplicateModel.removeDups(toAddModel.getInstance(row));
            toAddModel.remove(row);
        }
    }

    public void add(int[] rows) {
        for (int row : rows) {
            Instance add = toAddModel.getInstance(row);
            InstanceDAO.createDuplicateInstance(add, instanceClasses.get(add));
            duplicateModel.removeDups(toAddModel.getInstance(row));
            toAddModel.remove(row);
        }
    }

    public void link(HashMap<Integer, Instance> selected) throws SQLException {
        ArrayList<Integer> ids = new ArrayList<Integer>(selected.keySet());
        for(int id : ids){
            Instance instance = InstanceDAO.getById(id);
            InstanceHasInstanceClassDAO.createInstanceHasInstance(instance, instanceClasses.get(selected.get(id)));
        }
        ArrayList<Instance> toRemove = new ArrayList<Instance> (selected.values());
        for(Instance remove: toRemove){
            duplicateModel.removeDups(remove);
            toAddModel.remove(remove);
        }
    }

    public boolean isSelected() {
        return main.isSelected();
    }
}
