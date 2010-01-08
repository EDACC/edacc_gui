/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.EDACCManageDBMode;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author rretz
 */
public class ManageDBInstances {
    EDACCManageDBMode main;
    JPanel panelManageDBInstances;
    JFileChooser jFileChooserManageDBInstance;

    public ManageDBInstances(EDACCManageDBMode main, JPanel panelManageDBInstances, JFileChooser jFileChooserManageDBInstance){
        this.main = main;
        this.panelManageDBInstances = panelManageDBInstances;
        this.jFileChooserManageDBInstance = jFileChooserManageDBInstance;
    }


    /**
     * Will open a jFilechooser to select a file or directory to add all containing
     * instance files into the "instance table" of the MangeDBMode.
     * @param 
     */
    public void addInstances(){
        try {
            int returnVal = jFileChooserManageDBInstance.showOpenDialog(panelManageDBInstances);
            File ret = jFileChooserManageDBInstance.getSelectedFile();
            RecursiveFileScanner InstanceScanner = new RecursiveFileScanner("cnf");
            Vector<File> instanceFiles = InstanceScanner.searchFileExtension(ret);
            main.instanceTableModel.addInstances(buildTempInstances(instanceFiles));
            if (instanceFiles.isEmpty()) {
                JOptionPane.showMessageDialog(panelManageDBInstances,
                "No solvers have been found.",
                "Error",
                JOptionPane.WARNING_MESSAGE);
            }
        }catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
            "Choosen file or directory not found.",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        }catch (IOException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
            "Error reading choosen file or directory.",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Delete the given rows from the instanceTableModel
     * @param rows the rows which have to be deleted
     */
    public void removeInstances(int[] rows){
        Vector<Instance> rem = new Vector<Instance>();
        for(int i = 0; i < rows.length; i++){
            rem.add((Instance)main.instanceTableModel.getValueAt(i, 5));
        }
        main.instanceTableModel.removeInstances(rem);
    }

    /**
     * Saves all instances from the instanceTableModel into the Database
     */
    public void saveInstances() throws SQLException{
        Instance[] toSave =  (Instance[]) main.instanceTableModel.getInstances().toArray();
        for(int i = 0; i < toSave.length; i++){
            InstanceDAO.saveTempInstance(toSave[i]);
        }
    }

    private Vector<Instance> buildTempInstances(Vector<File> instanceFiles){
        Vector<Instance> instances = new Vector<Instance>();
        while(!instanceFiles.isEmpty()){
            Instance temp = InstanceDAO.createInstanceTemp();
             // TODO! set all Attributes
            instances.add(temp);
        }
        return instances;
    }
    /**
     * Creates filters with the given values for given JTable
     * @param tableInstances
     * @param name
     * @param numOfAtoms
     * @param numOfClause
     * @param ratio
     * @param maxClauseLength
     */
    public void filter(JTable tableInstances, String name, String numOfAtoms, String numOfClause , String ratio, String maxClauseLength) {


        if(name != null){
            TableRowSorter sorter = new TableRowSorter();
            sorter.setRowFilter( RowFilter.regexFilter(name, 0) );
            tableInstances.setRowSorter(sorter);
        }
        if(numOfAtoms != null){
            TableRowSorter sorter = new TableRowSorter();
            sorter.setRowFilter( RowFilter.regexFilter(numOfAtoms, 1) );
            tableInstances.setRowSorter(sorter);
        }
        if(numOfClause != null){
            TableRowSorter sorter = new TableRowSorter();
            sorter.setRowFilter( RowFilter.regexFilter(numOfClause, 2));
            tableInstances.setRowSorter(sorter);
        }
        if(ratio != null){
            TableRowSorter sorter = new TableRowSorter();
            sorter.setRowFilter( RowFilter.regexFilter(ratio, 3) );
            tableInstances.setRowSorter(sorter);
        }
        if(maxClauseLength != null){
            TableRowSorter sorter = new TableRowSorter();
            sorter.setRowFilter( RowFilter.regexFilter(maxClauseLength, 4) );
            tableInstances.setRowSorter(sorter);
        }
    }

}
