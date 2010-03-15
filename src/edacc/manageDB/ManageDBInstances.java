/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import com.mysql.jdbc.Blob;
import edacc.manageDB.InstanceParser.*;
import edacc.EDACCManageDBMode;
import edacc.model.InstaceNotInDBException;
import edacc.model.Instance;
import edacc.model.InstanceAlreadyInDBException;
import edacc.model.InstanceDAO;
import edacc.model.InstanceIsInExperimentException;
import edacc.model.NoConnectionToDBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    JFileChooser jFileChooserManageDBExportInstance;

    public ManageDBInstances(EDACCManageDBMode main, JPanel panelManageDBInstances, JFileChooser jFileChooserManageDBInstance,  JFileChooser jFileChooserManageDBExportInstance) {
        this.main = main;
        this.panelManageDBInstances = panelManageDBInstances;
        this.jFileChooserManageDBInstance = jFileChooserManageDBInstance;
        this.jFileChooserManageDBExportInstance = jFileChooserManageDBExportInstance;
    }
    /**
     * Load all instances from the database into the instancetable
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
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
            Vector<Instance> instances = buildInstances(instanceFiles);
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
     * Remove the given rows from the instanceTableModel
     * @param rows the rows which have to be deleted
     */
    public void removeInstances(int[] rows) throws NoConnectionToDBException, SQLException {
        Vector<Instance> rem = new Vector<Instance>();
        Vector<Instance> notRem = new Vector<Instance>();
        for (int i = 0; i < rows.length; i++) {
            Instance ins = (Instance) main.instanceTableModel.getValueAt(rows[i], 5);
            try {
                InstanceDAO.delete(ins);
                rem.add(ins);
            } catch (InstanceIsInExperimentException ex) {
                notRem.add(ins);
            }
        }
        main.instanceTableModel.removeInstances(rem);
        if(!notRem.isEmpty()){
            JOptionPane.showMessageDialog(panelManageDBInstances,
             "Some of the selected instances couldn't be removed, because they are containing to a experiment.",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Removes all instances from the instancetable
     */
    public void removeAllInstancesFromTable(){
        main.instanceTableModel.clearTable();

    }

    private Vector<Instance> buildInstances(Vector<File> instanceFiles)
            throws InstanceException, FileNotFoundException, NullPointerException, IOException, NoSuchAlgorithmException, NoConnectionToDBException, SQLException {
        Vector<Instance> instances = new Vector<Instance>();
        String duplicatesDB = "";
        for (int i = 0; i < instanceFiles.size(); i++) {
            try {
                String md5 = calculateMD5(instanceFiles.get(i));
                InstanceParser tempInstance = new InstanceParser(instanceFiles.get(i).getAbsolutePath());
                Instance temp = InstanceDAO.createInstance(instanceFiles.get(i), tempInstance.name, tempInstance.n,
                        tempInstance.m, tempInstance.r, tempInstance.k, md5);{
                instances.add(temp);
                InstanceDAO.save(temp);
                }
            } catch (InstanceAlreadyInDBException ex) {
                duplicatesDB += "\n " + instanceFiles.get(i).getAbsolutePath();
            }
            
        }
        if(!duplicatesDB.equals("")){
             JOptionPane.showMessageDialog(panelManageDBInstances,
                    "The following instances are already in the database: " + duplicatesDB,
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
    /**
     * Writes the selected instances binarys into the choosen Directory.
     * @param rows rows of the selected instances
     * @throws IOException
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws InstaceNotInDBException
     */
    public void exportInstances(int[] rows) throws IOException, NoConnectionToDBException, SQLException, InstaceNotInDBException{
        int returnVal = jFileChooserManageDBExportInstance.showOpenDialog(panelManageDBInstances);
        String path = jFileChooserManageDBExportInstance.getSelectedFile().getAbsolutePath();
        Instance temp;
        for(int i = 0; i < rows.length; i++){
           temp =    (Instance) main.instanceTableModel.getValueAt(rows[i], 5);
           FileOutputStream output = new FileOutputStream(path + System.getProperty("file.separator") + temp.getName());
           Blob blob = (Blob) InstanceDAO.getBinary(temp.getId());
           InputStream input = blob.getBinaryStream();
           byte[] buffer = new byte[1];
           while (input.read(buffer) > 0) {
                output.write(buffer);
           }
           input.close();
           output.close();
        }
    }

    public void SelectAllInstanceClass() {
        for(int i = 0; i < main.instanceClassTableModel.getRowCount(); i++){
            main.instanceClassTableModel.setSelected(i);
        }
    }
}
