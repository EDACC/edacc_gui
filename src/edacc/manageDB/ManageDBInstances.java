/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import edacc.EDACCAddInstanceToInstanceClass;
import edacc.EDACCApp;
import edacc.EDACCCreateEditInstanceClassDialog;
import edacc.EDACCExtendedWarning;
import edacc.EDACCManageDBInstanceFilter;
import edacc.manageDB.InstanceParser.*;
import edacc.EDACCManageDBMode;
import edacc.model.ComputationMethodDoesNotExistException;
import edacc.model.DatabaseConnector;
import edacc.model.InstanceClassAlreadyInDBException;
import edacc.model.InstanceNotInDBException;
import edacc.model.Instance;
import edacc.model.InstanceAlreadyInDBException;
import edacc.model.InstanceClass;
import edacc.model.InstanceClassDAO;
import edacc.model.InstanceClassMustBeSourceException;

import edacc.model.InstanceDAO;
import edacc.model.InstanceHasInstanceClass;
import edacc.model.InstanceHasInstanceClassDAO;
import edacc.model.InstanceIsInExperimentException;
import edacc.model.InstanceSourceClassHasInstance;
import edacc.model.MD5CheckFailedException;
import edacc.model.NoConnectionToDBException;
import edacc.model.Property;
import edacc.model.PropertyNotInDBException;
import edacc.model.Tasks;
import edacc.properties.PropertyComputationController;
import edacc.properties.PropertyTypeNotExistException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author rretz
 */
public class ManageDBInstances implements Observer {

    EDACCManageDBMode main;
    JPanel panelManageDBInstances;
    JFileChooser jFileChooserManageDBInstance;
    JFileChooser jFileChooserManageDBExportInstance;
    JTable tableInstances;
    Lock lock = new ReentrantLock();
    Condition condition;

    public ManageDBInstances(EDACCManageDBMode main, JPanel panelManageDBInstances,
            JFileChooser jFileChooserManageDBInstance, JFileChooser jFileChooserManageDBExportInstance,
            JTable tableInstances) {
        this.main = main;
        this.panelManageDBInstances = panelManageDBInstances;
        this.jFileChooserManageDBInstance = jFileChooserManageDBInstance;
        this.jFileChooserManageDBExportInstance = jFileChooserManageDBExportInstance;
        this.tableInstances = tableInstances;
        DatabaseConnector.getInstance().addObserver(this);
    }

    /**
     * Load all instances from the database into the instancetable
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    public void loadInstances() throws NoConnectionToDBException, SQLException, InstanceClassMustBeSourceException, IOException, PropertyNotInDBException, PropertyTypeNotExistException, ComputationMethodDoesNotExistException {
        main.instanceTableModel.instances.clear();
        main.instanceTableModel.addInstances(new Vector<Instance>(InstanceDAO.getAll()));
        main.instanceTableModel.fireTableDataChanged();
    }

    public void loadInstanceClasses() throws SQLException {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) InstanceClassDAO.getAllAsTree();
        main.instanceClassTreeModel.setRoot(root);
        main.getInstanceClassTree().setRootVisible(false);
        //main.instanceClassTreeModel.nodesWereInserted(((DefaultMutableTreeNode)main.instanceClassTreeModel.getRoot()), new int[] {((DefaultMutableTreeNode)main.instanceClassTreeModel.getRoot()).getIndex(child)});

        /* main.instanceClassTableModel.classes.clear();
        main.instanceClassTableModel.classSelect.clear();
        main.instanceClassTableModel.addClasses(new Vector<InstanceClass>(InstanceClassDAO.getAll()));*/
    }

    /**
     * Will open a jFilechooser to select a file or directory to add all containing
     * instance files into the "instance table" of the MangeDBMode.
     */
    public void addInstances(InstanceClass input, File ret, Tasks task, String fileExtension, Boolean compress, Boolean autoClass) throws InstanceException {
        try {

            RecursiveFileScanner InstanceScanner = new RecursiveFileScanner(fileExtension);
            Vector<File> instanceFiles = InstanceScanner.searchFileExtension(ret);
            task.setOperationName("Adding Instances");
            if (input.getName().equals("")) {
                Vector<Instance> instances;
                instances = buildInstancesAutogenerateClass(instanceFiles, ret, task, compress);
                main.instanceTableModel.addInstances(instances);
                loadInstanceClasses();
            } else {
                Vector<Instance> instances;
                if(autoClass){
                    instances  = buildInstancesGivenClassAutogenerate(instanceFiles, (InstanceClass) input, ret, task, compress);
                }else{
                    instances = buildInstancesGivenClass(instanceFiles, (InstanceClass) input, task, compress);
                }

               
                main.instanceTableModel.addInstances(instances);
                loadInstanceClasses();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ManageDBInstances.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstanceClassAlreadyInDBException ex) {
            Logger.getLogger(ManageDBInstances.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ManageDBInstances.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            Logger.getLogger(ManageDBInstances.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ManageDBInstances.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ManageDBInstances.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeInstances(int[] rows) throws NoConnectionToDBException, SQLException {
        Vector<Instance> toRemove = new Vector<Instance>();

        for (int i = 0; i < rows.length; i++) {
            toRemove.add((Instance) main.instanceTableModel.getInstance(tableInstances.convertRowIndexToModel(rows[i])));
        }
        InstanceTableModel tableModel = new InstanceTableModel();
        tableModel.addInstances(toRemove);
        //EDACCExtWarningErrorDialog removeInstances = new EDACCExtWarningErrorDialog(mainFrame, true, true, tableModel,
        //        "Do you really won't to remove the listed instances?");
        //removeInstances.setLocationRelativeTo(mainFrame);
        //EDACCApp.getApplication().show(removeInstances);
        //if(EDACCExtendedWarning.){
//            Tasks.startTask("TryToRemoveInstances", new Class[]{Vector.class, edacc.model.Tasks.class}, new Object[]{toRemove,  null}, this, this.main);
        //      }
        /*
         * Vector<Instance> toRemove = new Vector<Instance>();

        for (int i = 0; i < rows.length; i++) {
        toRemove.add((Instance )main.instanceTableModel.getValueAt(tableInstances.convertRowIndexToModel(rows[i]), 1));
        }
        InstanceTableModel tableModel = new InstanceTableModel();
        tableModel.addInstances(toRemove);
        JFrame mainFrame = EDACCApp.getApplication().getMainFrame();
        EDACCExtWarningErrorDialog removeInstances = new EDACCExtWarningErrorDialog(mainFrame, true, true, tableModel,
        "Do you really won't to remove the listed instances?");
        removeInstances.setLocationRelativeTo(mainFrame);
        EDACCApp.getApplication().show(removeInstances); */
        if (EDACCExtendedWarning.showMessageDialog(EDACCExtendedWarning.OK_CANCEL_OPTIONS,
                EDACCApp.getApplication().getMainFrame(),
                "Do you really want to remove the listed instances?",
                new JTable(tableModel))
                == EDACCExtendedWarning.RET_OK_OPTION) {
            Tasks.startTask("TryToRemoveInstances", new Class[]{Vector.class, edacc.model.Tasks.class}, new Object[]{toRemove, null}, this, this.main);
        }

    }

    /**
     * Remove the given rows from the instanceTableModel
     * @param rows the rows which have to be deleted
     */
    public void TryToRemoveInstances(Vector<Instance> toRemove, Tasks task) throws NoConnectionToDBException, SQLException {
        task.setOperationName("Removing instances");
        Vector<Instance> rem = new Vector<Instance>();
        Vector<Instance> notRem = new Vector<Instance>();
        for (int i = 0; i < toRemove.size(); i++) {
            Instance ins = toRemove.get(i);
            try {
                InstanceDAO.delete(ins);
                rem.add(ins);
                task.setStatus(i + " of " + toRemove.size() + " instances removed");
                task.setTaskProgress((float) i / (float) toRemove.size());
            } catch (InstanceIsInExperimentException ex) {
                notRem.add(ins);
            }
        }
        main.instanceTableModel.removeInstances(rem);
        if (!notRem.isEmpty()) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "Some of the selected instances couldn't be removed, because they are containing to a experiment.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Removes all instances from the instancetable
     */
    public void removeAllInstancesFromTable() {
        main.instanceTableModel.clearTable();

    }

    /**
     * Removes the Filter of the given JTable
     */
    public void removeFilter(JTable table) {
        table.setRowSorter(null);
    }

    /**
     *
     * @param file
     * @return md5sum of the given File
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public String calculateMD5(File file) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        return Util.calculateMD5(file);
    }

    /**
     * Writes the selected instances binarys into the choosen Directory.
     * @param rows rows of the selected instances
     * @throws IOException
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws InstanceNotInDBException
     */
    public void exportInstances(int[] rows, String path, Tasks task) throws IOException, NoConnectionToDBException, SQLException,
            InstanceNotInDBException, FileNotFoundException, MD5CheckFailedException,
            NoSuchAlgorithmException {
        task.setOperationName("Exporting instances");

        Instance temp;
        Vector<Instance> md5Error = new Vector<Instance>();
        for (int i = 0; i < rows.length; i++) {
            temp = (Instance) main.instanceTableModel.getInstance(tableInstances.convertRowIndexToModel(rows[i]));

            File f = new File(path + System.getProperty("file.separator") + temp.getName());
            if (!f.exists()) {
                InstanceDAO.getBinaryFileOfInstance(temp, f);
            }
            String md5File = Util.calculateMD5(f);
            task.setStatus(i + " of " + rows.length + " instances are exported");
            task.setTaskProgress((float) i / (float) rows.length);
            if (!md5File.equals(temp.getMd5())) {
                md5Error.add(temp);
                f.delete();
            }
        }

        if (!md5Error.isEmpty()) {
            InstanceTableModel tableModel = new InstanceTableModel();
            tableModel.addInstances(md5Error);
            EDACCExtendedWarning.showMessageDialog(EDACCExtendedWarning.OK_OPTIONS, EDACCApp.getApplication().getMainFrame(), "Following instances couldn't be written. Because the MD5checksum wasn't valid.", new JTable(tableModel));

//            JFrame mainFrame = EDACCApp.getApplication().getMainFrame();
//            EDACCExtWarningErrorDialog removeInstances = new EDACCExtWarningErrorDialog(mainFrame, true, false, tableModel,
//                    "Following instances couldn't be written. Because the MD5checksum wasn't valid.");
//            removeInstances.setLocationRelativeTo(mainFrame);
//            EDACCApp.getApplication().show(removeInstances);
        }
    }

    /**
     * Sets all checkboxes of the instanceclass table true.
     */
    public void SelectAllInstanceClass(Tasks task) {
        /*   for(int i = 0; i < main.instanceClassTableModel.getRowCount(); i++){
        task.setStatus(i + " of " + main.instanceClassTableModel.getRowCount() + " instance classes are loaded.");
        task.setTaskProgress((float)i/(float)main.instanceClassTableModel.getRowCount());
        main.instanceClassTableModel.seSelected(i);
        }
        try {
        main.instanceClassTableModel.changeInstanceTable();
        } catch (NoConnectionToDBException ex) {
        Logger.getLogger(ManageDBInstances.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
        Logger.getLogger(ManageDBInstances.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    public void DeselectAllInstanceClass() {
        /* main.instanceClassTableModel.DeselectAll();
        main.instanceTableModel.clearTable();
        main.instanceClassTableModel.fireTableDataChanged();*/
    }

    /**
     * Removes the given instance classes.
     * @param choosen The instance classes to remove.
     * @throws SQLException
     * @throws NoConnectionToDBException
     * @throws InstanceSourceClassHasInstance if one of the selected classes are a source class which has a refernce to an Instance.
     */
    public void RemoveInstanceClass(DefaultMutableTreeNode node) throws SQLException, NoConnectionToDBException, InstanceSourceClassHasInstance, InstanceIsInExperimentException {
        Vector<InstanceClass> toRemove = new Vector<InstanceClass>();
        toRemove = getAllToEnd(node);
        AddInstanceInstanceClassTableModel tableModel = new AddInstanceInstanceClassTableModel();
        tableModel.addClasses(new Vector<InstanceClass>(toRemove));
        if (EDACCExtendedWarning.showMessageDialog(EDACCExtendedWarning.OK_CANCEL_OPTIONS,
                EDACCApp.getApplication().getMainFrame(),
                "Do you really won't to remove the listed instance classes?",
                new JTable(tableModel))
                == EDACCExtendedWarning.RET_OK_OPTION) {
            Vector<Instance> lastRelated = InstanceClassDAO.checkIfEmpty(toRemove);
            if (toRemove.isEmpty()) {
                Vector<InstanceClass> errors = new Vector<InstanceClass>();
                for (int i = 0; i < toRemove.size(); i++) {
                    try {
                        InstanceClassDAO.delete(toRemove.get(i));
                    } catch (InstanceSourceClassHasInstance ex) {
                        errors.add(toRemove.get(i));
                    }
                }
                loadInstanceClasses();
                if (!errors.isEmpty()) {
                    tableModel = new AddInstanceInstanceClassTableModel();
                    tableModel.addClasses(errors);
                    EDACCExtendedWarning.showMessageDialog(EDACCExtendedWarning.OK_OPTIONS,
                            EDACCApp.getApplication().getMainFrame(),
                            "A Problem occured by removing the following instance classes.  \n ",
                            new JTable(tableModel));
                }
            } else {
                EDACCExtendedWarning.showMessageDialog(EDACCExtendedWarning.OK_OPTIONS,
                        EDACCApp.getApplication().getMainFrame(),
                        "A Problem occured by removing the following instance classes.  \n "
                        + "The selected instance class or one of his Childrens are an source class \n "
                        + "and is not Empty. Please remove all Instances from the source class before \n "
                        + "before deleting them.",
                        new JTable(tableModel));
            }
        }
    }

    /**
     * Opens a EDACCCreateEditInstanceClassDialog to create a new instance class.
     */
    public void addInstanceClasses() {
        JFrame mainFrame = EDACCApp.getApplication().getMainFrame();
        EDACCCreateEditInstanceClassDialog dialog = new EDACCCreateEditInstanceClassDialog(mainFrame, true, main.getInstanceClassTree());
        dialog.setLocationRelativeTo(mainFrame);
        EDACCApp.getApplication().show(dialog);
    }

    /**
     * Builds the instances of the given files with the instance source class which was choosen by the
     * User.
     * @param instanceFiles
     * @param instanceClass
     * @return Vector<Instance> of the instances of the from files.
     * @throws FileNotFoundException
     * @throws NullPointerException
     * @throws NullPointerException
     * @throws IOException
     * @throws InstanceException
     * @throws NoSuchAlgorithmException
     * @throws SQLException
     */
    public Vector<Instance> buildInstancesGivenClass(Vector<File> instanceFiles, InstanceClass instanceClass, Tasks task, boolean compressBinary)
            throws FileNotFoundException, NullPointerException, NullPointerException, IOException,
            NoSuchAlgorithmException, SQLException {

        Vector<Instance> instances = new Vector<Instance>();
        String duplicatesDB = "";
        Vector<String> errorsDB = new Vector<String>();
        Vector<String> errorsAdd = new Vector<String>();
        StringBuilder instanceErrors = new StringBuilder("");
        int errCount = 0;
        int done = 0;

        task.setTaskProgress((float) 0 / (float) instanceFiles.size());
        for (int i = 0; i < instanceFiles.size(); i++) {
            try {
                String md5 = calculateMD5(instanceFiles.get(i));
                String fileName = instanceFiles.get(i).getName();
                Instance temp = InstanceDAO.createInstance(instanceFiles.get(i), fileName, md5);
                instances.add(temp);
                InstanceDAO.save(temp, compressBinary, instanceClass);
            } catch (InstanceAlreadyInDBException ex) {
                errorsDB.add(instanceFiles.get(i).getAbsolutePath());
            }
            task.setTaskProgress((float) i / (float) instanceFiles.size());
            task.setStatus("Added " + i + " instances of " + instanceFiles.size());
        }


        String instanceErrs = instanceErrors.toString();
        if (!errorsAdd.isEmpty()) {
            FileNameTableModel tmp = new FileNameTableModel();
            tmp.setAll(errorsAdd);
            EDACCExtendedWarning.showMessageDialog(
                    EDACCExtendedWarning.OK_OPTIONS, EDACCApp.getApplication().getMainFrame(),
                    "By adding the following instances an error occured.",
                    new JTable(tmp));
        }

        if (!errorsDB.isEmpty()) {
            FileNameTableModel tmp = new FileNameTableModel();
            tmp.setAll(errorsDB);
            EDACCExtendedWarning.showMessageDialog(
                    EDACCExtendedWarning.OK_OPTIONS, EDACCApp.getApplication().getMainFrame(),
                    "The following instances are already in the database. (Equal name or md5 hash)",
                    new JTable(tmp));
        }

        return instances;
    }

    /**
     * Builds the instances of the given files and autogenerates the instance source classes.
     * If a autogenerated instance source class already exists, the existing is used.
     * @param instanceFiles
     * @param ret
     * @return Vector<Instance> with all generated instances.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NullPointerException
     * @throws InstanceException
     * @throws SQLException
     */
    public Vector<Instance> buildInstancesAutogenerateClass(Vector<File> instanceFiles, File ret, Tasks task, boolean compressBinary) throws FileNotFoundException, IOException, NoSuchAlgorithmException, NullPointerException, SQLException, InstanceClassAlreadyInDBException, InstanceException {

        if(instanceFiles.isEmpty()){
            throw new InstanceException();
        }
        Vector<Instance> instances = new Vector<Instance>();
        Vector<String> errorsDB = new Vector<String>();
        Vector<String> errorsAdd = new Vector<String>();
        InstanceClass instanceClass;
        task.setTaskProgress((float) 0 / (float) instanceFiles.size());

        //Creates all InstanceClasses in the directory and structures them as a tree.
        DefaultMutableTreeNode nodes = InstanceClassDAO.createInstanceClassFromDirectory(ret);

        //Create the instances and assigne them to their InstanceClass
        for (int i = 0; i < instanceFiles.size(); i++) {
            try {
                String md5 = calculateMD5(instanceFiles.get(i));               
                try {
                    //Get all InstanceClasses of the Instance and assigne it to the corresponding InstanceClass
                    InstanceParser tempInstance = new InstanceParser(instanceFiles.get(i).getAbsolutePath());
                    String rawPath = instanceFiles.get(i).getAbsolutePath().substring(ret.getParent().length() + 1, instanceFiles.get(i).getParent().length());
                    String[] possibleInstanceClasses = rawPath.split("\\\\|/");                                     
                    if (possibleInstanceClasses.length != 1) {
                        instanceClass = getInstanceClassFromTree(possibleInstanceClasses, nodes, 0);
                    } else {
                        instanceClass = (InstanceClass) ((DefaultMutableTreeNode) nodes.getRoot()).getUserObject();
                    }
                    Instance temp = InstanceDAO.createInstance(instanceFiles.get(i), tempInstance.name, md5);
                    instances.add(temp);
                    InstanceDAO.save(temp, compressBinary, instanceClass);
                } catch (InstanceException e) {
                    errorsAdd.add(instanceFiles.get(i).getAbsolutePath());
                }

            } catch (InstanceAlreadyInDBException ex) {
                errorsDB.add(instanceFiles.get(i).getAbsolutePath());
            }
            task.setTaskProgress((float) i / (float) instanceFiles.size());
            task.setStatus("Added " + i + " instances of " + instanceFiles.size());
        }

        if (!errorsAdd.isEmpty()) {
            FileNameTableModel tmp = new FileNameTableModel();
            tmp.setAll(errorsAdd);
            EDACCExtendedWarning.showMessageDialog(
                    EDACCExtendedWarning.OK_OPTIONS, EDACCApp.getApplication().getMainFrame(),
                    "By adding the following instances an error occured.",
                    new JTable(tmp));
        }

        if (!errorsDB.isEmpty()) {
            FileNameTableModel tmp = new FileNameTableModel();
            tmp.setAll(errorsDB);
            EDACCExtendedWarning.showMessageDialog(
                    EDACCExtendedWarning.OK_OPTIONS, EDACCApp.getApplication().getMainFrame(),
                    "The following instances are already in the database. (Equal name or md5 hash)",
                    new JTable(tmp));
        }
        return instances;

    }

       public Vector<Instance> buildInstancesGivenClassAutogenerate(Vector<File> instanceFiles, InstanceClass input, File ret, Tasks task, boolean compressBinary) throws FileNotFoundException, IOException, NoSuchAlgorithmException, NullPointerException, SQLException, InstanceClassAlreadyInDBException, InstanceException {

        if(instanceFiles.isEmpty()){
            throw new InstanceException();
        }

        Vector<Instance> instances = new Vector<Instance>();
        Vector<String> errorsDB = new Vector<String>();
        Vector<String> errorsAdd = new Vector<String>();
        InstanceClass instanceClass;
        task.setTaskProgress((float) 0 / (float) instanceFiles.size());

        //Creates all InstanceClasses in the directory and structures them as a tree. The given InstanceClass is the root of this tree.
        DefaultMutableTreeNode nodes = InstanceClassDAO.createInstanceClassFromDirectory(ret, input);

        //Create the instances and assigne them to their InstanceClass
        for (int i = 0; i < instanceFiles.size(); i++) {
            try {
                String md5 = calculateMD5(instanceFiles.get(i));
                try {
                    //Get all InstanceClasses of the Instance and assigne it to the corresponding InstanceClass
                    InstanceParser tempInstance = new InstanceParser(instanceFiles.get(i).getAbsolutePath());
                    String rawPath = instanceFiles.get(i).getAbsolutePath().substring(ret.getParent().length() + 1, instanceFiles.get(i).getParent().length());
                    String[] possibleInstanceClasses = rawPath.split("\\\\|/");
                    if (possibleInstanceClasses.length != 1) {
                        instanceClass = getInstanceClassFromTree(possibleInstanceClasses, nodes, 0);
                    } else {
                        instanceClass = (InstanceClass) ((DefaultMutableTreeNode) nodes.getRoot()).getUserObject();
                    }
                    Instance temp = InstanceDAO.createInstance(instanceFiles.get(i), tempInstance.name, md5);
                    instances.add(temp);
                    InstanceDAO.save(temp, compressBinary, instanceClass);
                } catch (InstanceException e) {
                    errorsAdd.add(instanceFiles.get(i).getAbsolutePath());
                }

            } catch (InstanceAlreadyInDBException ex) {
                errorsDB.add(instanceFiles.get(i).getAbsolutePath());
            }
            task.setTaskProgress((float) i / (float) instanceFiles.size());
            task.setStatus("Added " + i + " instances of " + instanceFiles.size());
        }

        if (!errorsAdd.isEmpty()) {
            FileNameTableModel tmp = new FileNameTableModel();
            tmp.setAll(errorsAdd);
            EDACCExtendedWarning.showMessageDialog(
                    EDACCExtendedWarning.OK_OPTIONS, EDACCApp.getApplication().getMainFrame(),
                    "By adding the following instances an error occured.",
                    new JTable(tmp));
        }

        if (!errorsDB.isEmpty()) {
            FileNameTableModel tmp = new FileNameTableModel();
            tmp.setAll(errorsDB);
            EDACCExtendedWarning.showMessageDialog(
                    EDACCExtendedWarning.OK_OPTIONS, EDACCApp.getApplication().getMainFrame(),
                    "The following instances are already in the database. (Equal name or md5 hash)",
                    new JTable(tmp));
        }
        return instances;

    }

    private InstanceClass getInstanceClassFromTree(String[] possibleInstanceClasses, DefaultMutableTreeNode node, int i) throws SQLException, InstanceClassAlreadyInDBException {
        i++;
        if (i == possibleInstanceClasses.length - 1) {

            for (int j = 0; j < node.getChildCount(); j++) {
                InstanceClass tmp = (InstanceClass) ((DefaultMutableTreeNode) node.getChildAt(j)).getUserObject();
                if (tmp.getName().equals(possibleInstanceClasses[i])) {
                    return tmp;
                }
            }
            DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(InstanceClassDAO.createInstanceClass(possibleInstanceClasses[(i)], "Autogenerated instance class", (InstanceClass) node.getUserObject()));
            node.add(tmp);
            return (InstanceClass) tmp.getUserObject();


        } else {
            for (int j = 0; j < node.getChildCount(); j++) {
                InstanceClass tmp = (InstanceClass) ((DefaultMutableTreeNode) node.getChildAt(j)).getUserObject();
                if (tmp.getName().equals(possibleInstanceClasses[i])) {
                    return getInstanceClassFromTree(possibleInstanceClasses, (DefaultMutableTreeNode) node.getChildAt(j), i);
                }
            }
            DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(InstanceClassDAO.createInstanceClass(possibleInstanceClasses[(i)], "Autogenerated instance class", (InstanceClass) node.getUserObject()));
            node.add(tmp);
            return getInstanceClassFromTree(possibleInstanceClasses, (DefaultMutableTreeNode) tmp, i);

        }
    }

    private DefaultMutableTreeNode autoGenerateInstanceClasses(Vector<File> files, File root) throws SQLException, InstanceClassAlreadyInDBException {
        DefaultMutableTreeNode node = null;
        for (int i = 0; i < files.size(); i++) {
            String rawPath = files.get(i).getAbsolutePath().substring(root.getParent().length() + 1, files.get(i).getParent().length());
            String[] possibleInstanceClasses = rawPath.split("\\\\|/");
            if (node == null) {
                node = new DefaultMutableTreeNode(InstanceClassDAO.createInstanceClass(possibleInstanceClasses[i], "Autogenerated instance source class", null));
            }
            for (int j = 1; j < possibleInstanceClasses.length; j++) {
                addInstanceClassToTree(node, j, possibleInstanceClasses[j]);
            }
        }
        return node;
    }

    private void addInstanceClassToTree(DefaultMutableTreeNode node, int j, String name) throws SQLException, InstanceClassAlreadyInDBException {
        j--;
        if (j == 0) {
            for (int i = 0; i < node.getChildCount(); i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
                if (((InstanceClass) child.getUserObject()).getName().equals(name)) {
                    return;
                }
            }
            node.add(new DefaultMutableTreeNode(InstanceClassDAO.createInstanceClass(name, "Autogenerated instance source class", (InstanceClass) node.getUserObject())));
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                addInstanceClassToTree((DefaultMutableTreeNode) node.getChildAt(i), j, name);
            }
        }
    }

    /**
     * Adds the selected instances to a new instance user class or changes their instance source class.
     * This is decided by the user in the EDACCAddInstanceToInstanceClass Dialog.
     * @param selectedRows The rows of the selected instances
     */
    public void addInstancesToClass(int[] selectedRows) throws IOException {
        if (tableInstances.getSelectedRows().length == 0) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "No instances selected.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } else {

            try {
                JFrame mainFrame = EDACCApp.getApplication().getMainFrame();
                EDACCAddInstanceToInstanceClass addInstanceToClass = new EDACCAddInstanceToInstanceClass(mainFrame, true);
                addInstanceToClass.setLocationRelativeTo(mainFrame);
                EDACCApp.getApplication().show(addInstanceToClass);
                InstanceClass input = addInstanceToClass.getInput();
                Vector<Instance> toChange = new Vector<Instance>();
                for (int i = 0; i < selectedRows.length; i++) {
                    toChange.add((Instance) main.instanceTableModel.getInstance(selectedRows[i]));
                }
                if (input != null) {
                    for (int i = 0; i < selectedRows.length; i++) {
                        InstanceHasInstanceClassDAO.createInstanceHasInstance(toChange.get(i), input);
                    }
                    loadInstanceClasses();
                }           
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
            }
        }
    }

    /**
     * Removes the choosen instances from the selected instance classes in the instanceClasstable.
     * If one of the selected instance classes is a source class a error message will occur.
     * @param selectedRowsInstance the instances to remove
     * @param selectedRowsInstanceClass rows of the InstanceClasses from which the selected instances have to be removed
     */
    public void RemoveInstanceFromInstanceClass(int[] selectedRowsInstance, int[] selectedRowsInstanceClass) {
        /*  // check if a instance is selected, if not notify the user
        if(tableInstances.getSelectedRows().length == 0){
        JOptionPane.showMessageDialog(panelManageDBInstances,
        "No instances selected.",
        "Warning",
        JOptionPane.WARNING_MESSAGE);
        }else{
        Boolean isSource = false;

        try {
        Vector<Instance> toRemove = new Vector<Instance>();
        for(int i = 0; i < selectedRowsInstance.length; i++){
        toRemove.add((Instance) main.instanceTableModel.getInstance(tableInstances.convertRowIndexToModel(selectedRowsInstance[i])));
        }
        // check if the user really want to remove the instances from the instace classes
        InstanceTableModel tableModel = new InstanceTableModel();
        tableModel.addInstances(toRemove);

        //                JFrame mainFrame = EDACCApp.getApplication().getMainFrame();
        //                EDACCExtWarningErrorDialog removeInstances = new EDACCExtWarningErrorDialog(mainFrame, true, true, tableModel,
        //                    "Do you really won't to remove the the listed instances from the selected instance classes?");
        //                removeInstances.setLocationRelativeTo(mainFrame);
        //                EDACCApp.getApplication().show(removeInstances);

        // remove the instances from the instace classes
        //if(removeInstances.isAccept()){
        if(EDACCExtendedWarning.showMessageDialog(EDACCExtendedWarning.OK_CANCEL_OPTIONS,
        EDACCApp.getApplication().getMainFrame(),
        "Do you really won't to remove  the listed instances from the selected instance classes?",
        new JTable(tableModel))==EDACCExtendedWarning.RET_OK_OPTION){
        for(int i = 0; i < toRemove.size(); i++){
        for(int j = 0; j < selectedRowsInstanceClass.length; j++){
        InstanceClass tempInstanceClass = (InstanceClass) main.instanceClassTableModel.getValueAt(selectedRowsInstanceClass[j], 4);
        if(tempInstanceClass.isSource()){
        isSource = true;
        }else{
        InstanceHasInstanceClass rem = InstanceHasInstanceClassDAO.getInstanceHasInstanceClass(tempInstanceClass, toRemove.get(i));
        if (rem != null) {
        InstanceHasInstanceClassDAO.removeInstanceHasInstanceClass(rem);
        }
        }
        }
        }
        }

        if(isSource){
        JOptionPane.showMessageDialog(panelManageDBInstances,
        "Some of the choosen instance classes are source classes, " +
        "the selected instances couldn't removed from them.",
        "Error",
        JOptionPane.ERROR_MESSAGE);
        }
        main.instanceClassTableModel.changeInstanceTable();

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
        }
        }*/
    }

    /**
     * Shows the dialog to configure a filter and add it to the instance table. If less than 2 instances are in the
     * instance table, no filter can be added. Selects the first entry in the instance table after adding the filter.
     */
    public void addFilter() {

        if (main.instanceFilter == null) {
            JFrame mainFrame = EDACCApp.getApplication().getMainFrame();
            main.instanceFilter = new EDACCManageDBInstanceFilter(mainFrame, true);
            main.instanceFilter.setLocationRelativeTo(mainFrame);
        }
        EDACCApp.getApplication().show(main.instanceFilter);

        Vector<RowFilter<Object, Object>> filters = main.instanceFilter.getFilter();
        if (filters != null) {
            if (filters.isEmpty()) {
                removeFilter(tableInstances);
                main.setFilterStatus("");
                if (tableInstances.getRowCount() != 0) {
                    tableInstances.addRowSelectionInterval(0, 0);
                }
            } else {
                main.sorter.setRowFilter(RowFilter.andFilter(filters));
                tableInstances.setRowSorter(main.sorter);
                main.instanceTableModel.fireTableDataChanged();
                if (tableInstances.getRowCount() != 0) {
                    tableInstances.addRowSelectionInterval(0, 0);
                }
                main.setFilterStatus("This list of instances has filters applied to it. Use the filter button below to modify.");
            }
        }
    }

    public void onTaskStart(String methodName) {
    }

    public void onTaskFailed(String methodName, Throwable e) {
    }

    public void onTaskSuccessful(String methodName, Object result) {
        if (methodName.equals("TryToRemoveInstances")) {
            main.instanceTableModel.fireTableDataChanged();
        }
    }

    /**
     * Shows the dialog to edit an instance class of the selected instance class
     * @param instanceClassTableModel Table model of the instance classes of the ManageDBMode
     * @param convertRowIndexToModel the row of the selected instance class
     */
    public void EditInstanceClass() {
        JFrame mainFrame = EDACCApp.getApplication().getMainFrame();
        EDACCCreateEditInstanceClassDialog dialog = new EDACCCreateEditInstanceClassDialog(mainFrame, true, main.getInstanceClassTree());
        dialog.setLocationRelativeTo(mainFrame);
        EDACCApp.getApplication().show(dialog);
    }

    public void update(Observable o, Object arg) {
        /* this.main.instanceTableModel.clearTable();
        this.main.instanceClassTableModel.clearTable();*/
    }

    public void showInstanceClassButtons(boolean enable) {
        main.showInstanceClassButtons(enable);
    }

    void showInstanceButtons(boolean enable) {
        main.showInstanceButtons(enable);
    }

    /**
     *
     * @param parent String which represents the path of root
     * @param searchDepth int which represents the depth to which the root path has to be cut
     * @return path cut down to the given search depth or the maximum search depth (depth of the layer of the instance file).
     */
    private String CutToSearchDepth(String parent, File file, int searchDepth) {
        String tmpString = file.getAbsolutePath().substring(parent.length() + 1);
        char[] tmp = tmpString.toCharArray();
        int count = 0;
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i] == System.getProperty("file.separator").toCharArray()[0]) {
                count++;
                String tmpS = tmpString.substring(0, i);
                if (searchDepth == count || !tmpS.contains("" + System.getProperty("file.separator").toCharArray()[0])) {
                    return tmpS;
                }
            }
        }
        return tmpString;
    }

    /**
     *
     * @param selectedRows
     */
    public void showInstanceInfoDialog(int[] selectedRows) {
        try {
            // Get the intersection of the instance classes of the selected Instances
            Vector<Instance> instances = new Vector<Instance>();
            for (int i = 0; i < selectedRows.length; i++) {
                instances.add((Instance) ((InstanceTableModel) tableInstances.getModel()).getInstance(tableInstances.convertRowIndexToModel(selectedRows[i])));
            }
            Vector<InstanceClass> instanceClasses = InstanceHasInstanceClassDAO.getIntersectionOfInstances(instances);
            AddInstanceInstanceClassTableModel tableModel = new AddInstanceInstanceClassTableModel();
            tableModel.addClasses(instanceClasses);
            EDACCExtendedWarning.showMessageDialog(EDACCExtendedWarning.OK_OPTIONS, EDACCApp.getApplication().getMainFrame(), "The selected instances belong to the following common instance classes.", new JTable(tableModel));

        } catch (NoConnectionToDBException ex) {
            Logger.getLogger(ManageDBInstances.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ManageDBInstances.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void computeProperties(Vector<Instance> instances, Vector<Property> properties, Tasks task) {
        System.out.println(instances.size() + " instances, " + properties.size() + " properties.");

        lock.lock();
        Condition condition = lock.newCondition();
        PropertyComputationController comCon = new PropertyComputationController(instances, properties, task, lock);
        Thread compute = new Thread(comCon);
        try {
            compute.start();
            condition.await();
        } catch (InterruptedException e) {
        } finally {
            lock.unlock();
        }
        task.cancel(true);
    }

    public void changeInstanceTable() {
        ((InstanceTableModel) tableInstances.getModel()).clearTable();
        TreePath[] selected = main.getInstanceClassTree().getSelectionPaths();
        Vector<InstanceClass> choosen = new Vector<InstanceClass>();
        if(selected == null)
            return;
        for (int i = 0; i < selected.length; i++) {
            choosen.addAll(getAllToEnd((DefaultMutableTreeNode) (selected[i].getLastPathComponent())));
            /*  DefaultMutableTreeNode[] nodes = getPath()

            Enumeration<DefaultMutableTreeNode> tmp = nodes.children();
            while(tmp.hasMoreElements()){
            choosen.add((InstanceClass) tmp.nextElement().getUserObject());
            }
            choosen.add((InstanceClass) nodes.getUserObject());*/
        }
        if (!choosen.isEmpty()) {
            try {
                LinkedList<Instance> test = InstanceDAO.getAllByInstanceClasses(choosen);
                ((InstanceTableModel) tableInstances.getModel()).addInstances(new Vector<Instance>(test));
            } catch (NoConnectionToDBException ex) {
                Logger.getLogger(ManageDBInstances.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(ManageDBInstances.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ((InstanceTableModel) tableInstances.getModel()).fireTableDataChanged();
    }

    private Vector<InstanceClass> getAllToEnd(DefaultMutableTreeNode root) {
        Vector<InstanceClass> ret = new Vector<InstanceClass>();
        for (int i = 0; i < root.getChildCount(); i++) {
            ret.addAll(getAllToEnd((DefaultMutableTreeNode) root.getChildAt(i)));
        }
        ret.add((InstanceClass) root.getUserObject());
        return ret;
    }

    public void RemoveInstanceFromInstanceClass(int[] selectedRows, TreePath[] selected) {
        if (tableInstances.getSelectedRows().length == 0) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "No instances selected.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } else if (selected == null) {
            JOptionPane.showMessageDialog(panelManageDBInstances,
                    "No instance class selected.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            Boolean isSource = false;

            try {
                Vector<Instance> toRemove = new Vector<Instance>();
                for (int i = 0; i < selectedRows.length; i++) {
                    toRemove.add((Instance) main.instanceTableModel.getInstance(tableInstances.convertRowIndexToModel(selectedRows[i])));
                }
                // check if the user really want to remove the instances from the instace classes
                InstanceTableModel tableModel = new InstanceTableModel();
                tableModel.addInstances(toRemove);


                if (EDACCExtendedWarning.showMessageDialog(EDACCExtendedWarning.OK_CANCEL_OPTIONS,
                        EDACCApp.getApplication().getMainFrame(),
                        "Do you really won't to remove  the listed instances from the selected instance classes? \n" +
                        "If the instances are deleted from all their instance classes, the instances are deleted too.",
                        new JTable(tableModel)) == EDACCExtendedWarning.RET_OK_OPTION) {
                    Vector<InstanceClass> choosen = new Vector<InstanceClass>();
                    for (int j = 0; j < selected.length; j++) {
                        choosen.addAll(getAllToEnd((DefaultMutableTreeNode) (selected[j].getLastPathComponent())));
                    }

                    for (int i = 0; i < toRemove.size(); i++) {
                        for (int j = 0; j < choosen.size(); j++) {
                            InstanceClass tempInstanceClass = choosen.get(j);
                            InstanceHasInstanceClass rem = InstanceHasInstanceClassDAO.getInstanceHasInstanceClass(tempInstanceClass, toRemove.get(i));
                            if (rem != null) {
                                InstanceHasInstanceClassDAO.removeInstanceHasInstanceClass(rem);
                            }
                        }
                    }
                }

                if (isSource) {
                    JOptionPane.showMessageDialog(panelManageDBInstances,
                            "Some of the choosen instance classes are source classes, "
                            + "the selected instances couldn't removed from them.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                loadInstanceClasses();

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
            }
        }
    }

    public void exportInstanceClass(DefaultMutableTreeNode selected, String path, Tasks task) throws NoConnectionToDBException, SQLException, FileNotFoundException, IOException, NoSuchAlgorithmException, InstanceNotInDBException {
        task.setOperationName("Exporting instance classes");
        InstanceClass root = (InstanceClass) selected.getUserObject();
        Vector<InstanceClass> tmp = new Vector<InstanceClass>();
        Vector<Instance> md5Error = new Vector<Instance>();
        tmp.add(root);
        Vector<Instance> toExport = new Vector<Instance>(InstanceDAO.getAllByInstanceClasses(tmp));

        File dir = new File(path + System.getProperty("file.separator") + root.getName());
        if (!dir.isDirectory()) {
            dir.mkdir();
        }

        //Creates all files of the Instances related to the the InstanceClass
        for (int i = 0; i < toExport.size(); i++) {
            task.setStatus(i + " of " + toExport.size() + " instances from the class " + root.getName());
            task.setTaskProgress((float) i / (float) toExport.size());
            File f = new File(dir.getAbsolutePath() + System.getProperty("file.separator")
                    + toExport.get(i).getName());
            if (!f.exists()) {
                InstanceDAO.getBinaryFileOfInstance(toExport.get(i), f);
            }
            String md5File = Util.calculateMD5(f);
            // If the file is corrupted delete it
            if (!md5File.equals(toExport.get(i).getMd5())) {
                md5Error.add(toExport.get(i));
                f.delete();
            }
        }
        task.setStatus(toExport.size() + " of " + toExport.size() + " instances from the class " + root.getName());
        task.setTaskProgress(1);
        //Creates all files of the Instances related to the the childs of the InstanceClass
        for (int i = 0; i < selected.getChildCount(); i++) {
            md5Error.addAll(exportInstanceClasses((DefaultMutableTreeNode) selected.getChildAt(i), dir.getAbsolutePath(), task));
        }

        if (!md5Error.isEmpty()) {
            InstanceTableModel tableModel = new InstanceTableModel();
            tableModel.addInstances(md5Error);
            EDACCExtendedWarning.showMessageDialog(EDACCExtendedWarning.OK_OPTIONS, EDACCApp.getApplication().getMainFrame(), "Following instances couldn't be written. Because the MD5checksum wasn't valid.", new JTable(tableModel));
        }
    }

    private Vector<Instance> exportInstanceClasses(DefaultMutableTreeNode selected, String path, Tasks task) throws NoConnectionToDBException, SQLException, FileNotFoundException, IOException, NoSuchAlgorithmException, InstanceNotInDBException {
        task.setOperationName("Exporting instance classes");
        InstanceClass root = (InstanceClass) selected.getUserObject();
        Vector<InstanceClass> tmp = new Vector<InstanceClass>();
        Vector<Instance> md5Error = new Vector<Instance>();
        tmp.add(root);
        Vector<Instance> toExport = new Vector<Instance>(InstanceDAO.getAllByInstanceClasses(tmp));

        File dir = new File(path + System.getProperty("file.separator") + root.getName());
        if (!dir.isDirectory()) {
            dir.mkdir();
        }

        //Creates all files of the Instances related to the the InstanceClass
        for (int i = 0; i < toExport.size(); i++) {
            task.setStatus(i + " of " + toExport.size() + " instances from the class " + root.getName());
            task.setTaskProgress((float) i / (float) toExport.size());
            File f = new File(dir.getAbsolutePath() + System.getProperty("file.separator")
                    + toExport.get(i).getName());
            if (!f.exists()) {
                InstanceDAO.getBinaryFileOfInstance(toExport.get(i), f);
            }
            String md5File = Util.calculateMD5(f);
            // If the file is corrupted delete it
            if (!md5File.equals(toExport.get(i).getMd5())) {
                md5Error.add(toExport.get(i));
                f.delete();
            }
        }

        task.setStatus(toExport.size() + " of " + toExport.size() + " instances from the class " + root.getName());
        task.setTaskProgress(1);
        //Creates all files of the Instances related to the the childs of the InstanceClass
        for (int i = 0; i < selected.getChildCount(); i++) {
            md5Error.addAll(exportInstanceClasses((DefaultMutableTreeNode) selected.getChildAt(i), dir.getAbsolutePath(), task));
        }
        return md5Error;
    }
}
