/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import edacc.EDACCAddInstanceErrorDialog;
import edacc.model.Instance;
import edacc.model.InstanceClass;
import edacc.model.InstanceDAO;
import edacc.model.InstanceHasInstanceClassDAO;
import edacc.model.Tasks;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        duplicateModel = new InstanceDupErrorTableModel(duplicate, this);
        duplicateSorter = new TableRowSorter<InstanceDupErrorTableModel>();


        ArrayList<Instance> toAdd = new ArrayList<Instance>(duplicate.keySet());
        toAddModel = new InstanceErrorTableModel(toAdd, this);
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
        }
        toAddModel.removeRows(rows);
    }

    public void add(int[] row) {
        Tasks.startTask("tryToAdd", new Class[]{row.getClass(), edacc.model.Tasks.class},
                new Object[]{row, null}, this, this.main);
    }

    /**
     * 
     * @param rows 
     */
    public void tryToAdd(int[] rows, Tasks task) {
        Tasks.getTaskView().setCancelable(true);
        task.setOperationName("Add instances");
        int count = 0;
        int all = rows.length;

        for (int row : rows) {
            Instance add = toAddModel.getInstance(row);
            duplicateModel.removeDups(add);
            InstanceDAO.createDuplicateInstance(add, instanceClasses.get(add));
            duplicateModel.checkNewAdded(add);
            task.setStatus(count + " of " + all + " instances added");
            task.setTaskProgress((float) count / (float) all);
            count++;
        }
        toAddModel.removeRows(rows);
        Tasks.getTaskView().setCancelable(false);
    }

    /**
     * Links the given tuple, removes them from both tables of the EDACCAddInstanceErrorDialog and 
     * updates the database.
     * @param selected @HashMap<@Integer, @ArrayList<@Instance>> of all links between error causing and duplicate 
     * instances.
     * @throws SQLException 
     */
    public void link(HashMap<Integer, ArrayList<Instance>> selected) throws SQLException {
        ArrayList<Integer> ids = new ArrayList<Integer>(selected.keySet());
        for (int id : ids) {
            Instance instance = InstanceDAO.getById(id);
            for (Instance inst : selected.get(id)) {
                InstanceHasInstanceClassDAO.createInstanceHasInstance(instance, instanceClasses.get(inst));
            }

        }
        ArrayList<ArrayList<Instance>> toRemove = new ArrayList<ArrayList<Instance>>(selected.values());
        for (ArrayList<Instance> removeList : toRemove) {
            for (Instance remove : removeList) {
                duplicateModel.removeDups(remove);
                toAddModel.remove(remove);
            }
        }
    }

    /**
     * 
     * @return True if one or more instances are selected in the jTableInstancesToAdd table.
     */
    public boolean isSelected() {
        return main.isSelected();
    }

    /**
     * 
     * @return The count of selected Instances of the jTableInstancesToAdd table.
     */
    public int getSelectedToAddRowCount() {
        return main.getSelectedToAddRowCount();
    }

    /**
     * Removes the filter on the jTableProblemCausing table.
     */
    public void noneFilter() {
        filter.setSelectedInstance(null);
        main.sort();
        this.duplicateModel.fireTableDataChanged();
    }

    /**
     * Enables or disables buttons of the EDACCInstanceErrorDialog, which cannot work on with
     * multiple selected instance in the jTableInstancesToAdd table.
     * @param b 
     */
    public void mulipleSelectionBtnShow(boolean b) {
        main.multipleSelecteBtnShow(b);
    }

    /**
     * 
     * @return Instance which is selected in the jTableInstancesToAdd table
     */
    public Instance getToAddSelectedInstance() {
        int tmp = main.getToAddSelectedInstance();
        if (tmp == -1) {
            return null;
        } else {
            return toAddModel.getInstance(tmp);
        }
    }

    /**
     * 
     * @return Arraylist<Instance> of instances which are selected in the jTableInstancesToAdd table.
     */
    public ArrayList<Instance> getToAddSelectedInstances() {
        ArrayList<Instance> ret = new ArrayList<Instance>();
        int[] tmp = main.getToAddSelectedInstances();
        for (int i : tmp) {
            ret.add(toAddModel.getInstance(i));
        }
        return ret;
    }

    /**
     * 
     * @param row
     * @return True if the instance at the given row of the jTableInstancesToAdd is linked with
     *  a duplicate instance which is already in the database.
     */
    public boolean isLinked(int row) {
        row = main.ToAddTableConvertRowToModel(row);
        Instance linked = toAddModel.getInstance(row);
        return duplicateModel.isLinked(linked);
    }

    public String getErrorType(Instance i) {
        String ret = "";

        ArrayList<Instance> list = duplicateModel.getDupInstances(i);
        for (Instance inst : list) {
            if (i.getMd5().equals(inst.getMd5())) {
                if (ret.equals("Name")) {
                    return "Both";
                }
                ret = "MD5";
            }
            if (i.getName().equals(inst.getName())) {
                if (ret.equals("MD5")) {
                    return "Both";
                }
                ret = "Name";
            }
        }
        return ret;

    }
}
