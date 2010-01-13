/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import edacc.manageDB.InstanceParser.*;
import edacc.EDACCManageDBMode;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowFilter;

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

    /**
     * Will open a jFilechooser to select a file or directory to add all containing
     * instance files into the "instance table" of the MangeDBMode.
     * @param 
     */
    public void addInstances() {
        try {
            int returnVal = jFileChooserManageDBInstance.showOpenDialog(panelManageDBInstances);
            File ret = jFileChooserManageDBInstance.getSelectedFile();
            RecursiveFileScanner InstanceScanner = new RecursiveFileScanner("cnf");
            Vector<File> instanceFiles = InstanceScanner.searchFileExtension(ret);
            main.instanceTableModel.addInstances(buildTempInstances(instanceFiles));
            if (instanceFiles.isEmpty()) {
                JOptionPane.showMessageDialog(panelManageDBInstances,
                        "No Instances have been found.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (NoSuchAlgorithmException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "A problem with the MD5-algorithm has occured.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (InstanceException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "Choosen file or directory not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
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
    public void removeInstances(int[] rows) {
        Vector<Instance> rem = new Vector<Instance>();
        for (int i = 0; i < rows.length; i++) {
            rem.add((Instance) main.instanceTableModel.getValueAt(i, 5));
        }
        main.instanceTableModel.removeInstances(rem);
    }

    /**
     * Saves all instances from the instanceTableModel into the Database
     */
    public void saveInstances() throws SQLException, FileNotFoundException {
        for (Instance i : main.instanceTableModel.getInstances()) {
            InstanceDAO.saveTempInstance(i);
        }
    }

    private Vector<Instance> buildTempInstances(Vector<File> instanceFiles)
            throws InstanceException, FileNotFoundException, NullPointerException, IOException, NoSuchAlgorithmException {
        Vector<Instance> instances = new Vector<Instance>();
        for (int i = 0; i < instanceFiles.size(); i++) {
            InstanceParser tempInstance = new InstanceParser(instanceFiles.get(i).getAbsolutePath());
            Instance temp = InstanceDAO.createInstanceTemp();
            temp.setFile(instanceFiles.get(i));
            temp.setName(tempInstance.name);
            temp.setNumAtoms(tempInstance.n);
            temp.setNumClauses(tempInstance.m);
            temp.setRatio(tempInstance.r);
            temp.setMaxClauseLength(tempInstance.k);
            temp.setMd5(calculateMD5(instanceFiles.get(i)));
            instances.add(temp);
        }
        return instances;
    }

    /**
     * Creates filters with the given values and adds them to the sorter of EDACCManageDBMode
     * @param name
     * @param numOfAtoms
     * @param numOfClauses
     * @param ratio
     * @param maxClauseLength
     */
    public void newFilter(String name, String numOfAtoms, String numOfClauses, String ratio, String maxClauseLength) {
        Vector<RowFilter<Object, Object>> filters = new Vector<RowFilter<Object, Object>>();
        try {
            filters.add(RowFilter.regexFilter(name, 0));
            filters.add(RowFilter.regexFilter(numOfAtoms, 1));
            filters.add(RowFilter.regexFilter(numOfClauses, 2));
            filters.add(RowFilter.regexFilter(ratio, 3));
            filters.add(RowFilter.regexFilter(maxClauseLength, 4));
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
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
        MessageDigest digest = MessageDigest.getInstance("MD5");
        InputStream is = new FileInputStream(file);
        byte[] buffer = new byte[8192];
        int read = 0;
        while ((read = is.read(buffer)) > 0) {
            digest.update(buffer, 0, read);
        }
        byte[] md5sum = digest.digest();
        BigInteger bigInt = new BigInteger(1, md5sum);
        return bigInt.toString(16);
    }
}
