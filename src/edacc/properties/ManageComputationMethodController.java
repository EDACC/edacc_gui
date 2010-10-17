/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.EDACCManageComputationMethodDialog;
import edacc.manageDB.Util;
import edacc.model.ComputationMethod;
import edacc.model.ComputationMethodAlreadyExistsException;
import edacc.model.ComputationMethodDAO;
import edacc.model.ComputationMethodDoesNotExistException;
import edacc.model.NoComputationMethodBinarySpecifiedException;
import edacc.model.NoConnectionToDBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JPanel;

/**
 *
 * @author rretz
 */
public class ManageComputationMethodController {
    private ComputationMethodTableModel tableModel;
    private JPanel panelMain;
    private EDACCManageComputationMethodDialog main;
    private int editId = -1;

    public ManageComputationMethodController(EDACCManageComputationMethodDialog main, JPanel panelMain, ComputationMethodTableModel tableModel) {
        this.main = main;
        this.panelMain = panelMain;
        this.tableModel = tableModel;
    }

    /**
     * Adds all ComputationMethods to the table in the GUI.
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws ComputationMethodDoesNotExistException
     */
    public void loadComputationMethods() throws NoConnectionToDBException, SQLException, ComputationMethodDoesNotExistException {
        tableModel.clear();
        Vector<ComputationMethod> toLoad = ComputationMethodDAO.getAll();
        if(!toLoad.isEmpty()){
            tableModel.addComputationMethods(ComputationMethodDAO.getAll());   
        }
    }

    public void newComputationMethod() {
        editId = -1;
        main.clearInputFields();
        main.enableInputFields(true);
    }

    /**
     * Removes the ComputationMethod object at the given row of the tables from the cache and database.
     * @param rowIndex
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws SQLException
     * @throws ComputationMethodAlreadyExistsException
     * @throws NoComputationMethodBinarySpecifiedException
     * @throws FileNotFoundException
     */
    public void removeComputationMethod(int rowIndex) throws NoConnectionToDBException, SQLException, SQLException,
            ComputationMethodAlreadyExistsException, NoComputationMethodBinarySpecifiedException, FileNotFoundException {
        ComputationMethod toRemove = tableModel.getComputationMethod(rowIndex);
        toRemove.setDeleted();
        ComputationMethodDAO.save(toRemove);
    }

    public void saveComputationMethod(String name, String description, File selectedFile) throws NoConnectionToDBException, SQLException,
            ComputationMethodDoesNotExistException, ComputationMethodAlreadyExistsException, NoComputationMethodBinarySpecifiedException,
            FileNotFoundException, IOException, NoSuchAlgorithmException {
        if(editId == -1){
            ComputationMethod toSave = ComputationMethodDAO.getById(editId);
            toSave.setName(name);
            toSave.setDescription(description);
            ComputationMethodDAO.save(toSave);
        }else {
            ComputationMethodDAO.createComputationMethod(name, description, Util.calculateMD5(selectedFile), selectedFile);
        }
        loadComputationMethods();
    }

    void showComputationMethod(ComputationMethod computationMethod) {
        editId = computationMethod.getId();
        main.showComputationMethod(computationMethod);
        main.enableInputFields(false);
        main.enableEditInputFields(true);     
    }
}
