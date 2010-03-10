/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import edacc.manageDB.InstanceParser.*;
import edacc.EDACCManageDBMode;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.NoConnectionToDBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowFilter.ComparisonType;

/**
 *
 * @author rretz
 */
public class ManageDBInstances {

    EDACCManageDBMode main;
    JPanel panelManageDBInstances;
    JFileChooser jFileChooserManageDBInstance;

    public ManageDBInstances(EDACCManageDBMode main, JPanel panelManageDBInstances, JFileChooser jFileChooserManageDBInstance) {
        this.main = main;
        this.panelManageDBInstances = panelManageDBInstances;
        this.jFileChooserManageDBInstance = jFileChooserManageDBInstance;
    }

    public void loadInstances() throws NoConnectionToDBException, SQLException {
        main.instanceTableModel.instances.clear();
        main.instanceTableModel.addInstances(new Vector<Instance>(InstanceDAO.getAll()));
        main.instanceTableModel.fireTableDataChanged();
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
            Vector<Instance> instances = buildTempInstances(instanceFiles);
            if (instanceFiles.isEmpty()) {
                JOptionPane.showMessageDialog(panelManageDBInstances,
                        "No Instances have been found.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
            }
            main.instanceTableModel.addInstances(instances);
        } catch (NoConnectionToDBException ex) {
           JOptionPane.showMessageDialog(panelManageDBInstances,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "There is a Problem with the database: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NoSuchAlgorithmException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "A problem with the MD5-algorithm has occured: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (InstanceException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "Chosen file or directory not found: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "Error reading chosen file or directory: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Delete the given rows from the instanceTableModel
     * @param rows the rows which have to be deleted
     */
    public void removeInstances(int[] rows) throws NoConnectionToDBException, SQLException {
        Vector<Instance> rem = new Vector<Instance>();
        for (int i = 0; i < rows.length; i++) {
            Instance ins = (Instance) main.instanceTableModel.getValueAt(rows[i], 5);
            rem.add(ins);
            InstanceDAO.delete(ins);
        }
        main.instanceTableModel.removeInstances(rem);
    }

    public void removeAllInstances(){
        main.instanceTableModel.clearTable();

    }

    /**
     * Saves all instances from the instanceTableModel into the Database
     * @throws SQLException
     * @throws FileNotFoundException
     */
    public void saveInstances() throws  FileNotFoundException, NoInstancesToSaveException, SQLException {
        if(!main.instanceTableModel.isEmpty()){
            Vector<Instance> saved = new Vector<Instance>();
            for (Instance i : main.instanceTableModel.getInstances()) {
                try {
                    InstanceDAO.saveTempInstance(i);
                    saved.addElement(i);
                } catch (SQLException ex) {
                    main.instanceTableModel.removeInstances(saved);
                    throw new SQLException(ex.getMessage());
                }
            }   
        }else{
            throw new NoInstancesToSaveException();
        }
    }

    private Vector<Instance> buildTempInstances(Vector<File> instanceFiles)
            throws InstanceException, FileNotFoundException, NullPointerException, IOException, NoSuchAlgorithmException, NoConnectionToDBException, SQLException {
        Vector<Instance> instances = new Vector<Instance>();
        String duplicates = "";
        for (int i = 0; i < instanceFiles.size(); i++) {
            String md5 = calculateMD5(instanceFiles.get(i));
            InstanceParser tempInstance = new InstanceParser(instanceFiles.get(i).getAbsolutePath());
            Instance temp = InstanceDAO.createInstanceTemp(md5);
            if(temp != null){
                temp.setFile(instanceFiles.get(i));
                temp.setName(tempInstance.name);
                temp.setNumAtoms(tempInstance.n);
                temp.setNumClauses(tempInstance.m);
                temp.setRatio(tempInstance.r);
                temp.setMaxClauseLength(tempInstance.k);
                temp.setMd5(md5);
                instances.add(temp);
            }else{
                duplicates +="; " + instanceFiles.get(i).getAbsolutePath();
            }
            
        }
        if(!duplicates.equals("")){
             JOptionPane.showMessageDialog(panelManageDBInstances,
                    "The following instances are already in the database: " + duplicates,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return instances;
    }

/**
 * Creates and add a andFilter with the given values to the sorter of EDACCManageDBMode
 * @param name
 * @param numOfAtomsMin
 * @param numOfAtomsMax
 * @param numOfClausesMin
 * @param numOfClausesMax
 * @param ratioMin
 * @param ratioMax
 * @param maxClauseLengthMin
 * @param maxClauseLengthMax
 */
    public void newFilter(String name, String numOfAtomsMin, String numOfAtomsMax, String numOfClausesMin,
            String numOfClausesMax, String ratioMin, String ratioMax, String maxClauseLengthMin,
            String maxClauseLengthMax) {
        Vector<RowFilter<Object, Object>> filters = new Vector<RowFilter<Object, Object>>();
        filters.add(RowFilter.regexFilter(name, 0));
        if(!numOfAtomsMin.isEmpty())filters.add(RowFilter.numberFilter(ComparisonType.AFTER, Integer.parseInt(numOfAtomsMin), 1));
        if(!numOfAtomsMax.isEmpty()) filters.add(RowFilter.numberFilter(ComparisonType.BEFORE, Integer.parseInt(numOfAtomsMax), 1));
        if(!numOfClausesMin.isEmpty())filters.add(RowFilter.numberFilter(ComparisonType.AFTER, Integer.parseInt(numOfClausesMin), 2));
        if(!numOfClausesMax.isEmpty())filters.add(RowFilter.numberFilter(ComparisonType.BEFORE, Integer.parseInt(numOfClausesMax), 2));
        if(!ratioMin.isEmpty())filters.add(RowFilter.numberFilter(ComparisonType.AFTER, Float.parseFloat(ratioMin), 3));
        if(!ratioMax.isEmpty())filters.add(RowFilter.numberFilter(ComparisonType.BEFORE, Float.parseFloat(ratioMax), 3));
        if(!maxClauseLengthMin.isEmpty())filters.add(RowFilter.numberFilter(ComparisonType.AFTER, Integer.parseInt(maxClauseLengthMin), 4));
        if(!maxClauseLengthMax.isEmpty())filters.add(RowFilter.numberFilter(ComparisonType.BEFORE, Integer.parseInt(maxClauseLengthMax), 4));
        RowFilter<Object, Object> filter = RowFilter.andFilter(filters);
        main.sorter.setRowFilter(filter);
    }

    /**
     * Removes the Filter of the given JTable
     */
    public void removeFilter(JTable table) {
        table.setRowSorter(null);
    }

    public String calculateMD5(File file) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        return Util.calculateMD5(file);
    }
}
