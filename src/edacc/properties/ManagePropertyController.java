/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.properties;

import edacc.EDACCManagePropertyDialog;
import edacc.model.ComputationMethod;
import edacc.model.ComputationMethodAlreadyExistsException;
import edacc.model.ComputationMethodDAO;
import edacc.model.ComputationMethodDoesNotExistException;
import edacc.model.ComputationMethodSameNameAlreadyExists;
import edacc.model.NoComputationMethodBinarySpecifiedException;
import edacc.model.NoConnectionToDBException;
import edacc.model.Property;
import edacc.model.PropertyDAO;
import edacc.model.PropertyIsUsedException;
import edacc.model.PropertyNotInDBException;
import edacc.model.PropertyType;
import edacc.model.PropertyTypeDoesNotExistException;
import edacc.satinstances.PropertyValueType;
import edacc.satinstances.PropertyValueTypeManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 * Controller class of the EDACCManagePropertyDialog
 * @author rretz
 */
public class ManagePropertyController {

    private EDACCManagePropertyDialog main;
    private JPanel panel;
    private JTable tableSolverProperty;
    private int editId;

    /**
     * Constructor of the ManagePropertyController
     * @param manage the EDACCManagePropertyDialog to controll
     * @param panelManageResultProperty main panel of the EDACCManagerSolverPropertyDialog
     * @param tableResultProperty the table which contains the Overview over all Property objects.
     */
    public ManagePropertyController(EDACCManagePropertyDialog manage, JPanel panelManageResultProperty, JTable tableResultProperty) {
        this.main = manage;
        this.panel = panelManageResultProperty;
        this.tableSolverProperty = tableResultProperty;
        this.editId = -1;
    }

    /**
     * Clears and enables the input fields in the gui.
     */
    public void NewProperty() {
        clearPropertyEditField();
        showPropertyTypeSelection();
        this.editId = -1;
    }

    /**
     * Clears the input fields of the gui.
     */
    private void clearPropertyEditField() {
        main.clearPropertyEditField();
    }

    /**
     * Enables and disables some of the input fields of the gui controlled by the selected PropertySource
     */
    public void propertySourceChanged() {
        main.propertySourceChanged();
    }

    /**
     * 
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws SQLException
     * @throws PropertyNotInDBException
     * @throws PropertyTypeNotExistException
     * @throws IOException
     */
    public void loadProperties()
            throws NoConnectionToDBException, SQLException, SQLException, PropertyNotInDBException,
            PropertyTypeNotExistException, IOException, ComputationMethodDoesNotExistException {
        ((PropertyTableModel) this.tableSolverProperty.getModel()).clear();
        ((PropertyTableModel) this.tableSolverProperty.getModel()).addProperties(new Vector<Property>(PropertyDAO.getAll()));
    }

    public void loadPropertyValueTypes() throws IOException, NoConnectionToDBException, SQLException {
        Vector<PropertyValueType<?>> propValueTypes = PropertyValueTypeManager.getInstance().getAll();
        Vector<String> items = new Vector<String>();
        for (int i = 0; i < propValueTypes.size(); i++) {
            items.add(propValueTypes.get(i).getName());
        }
        main.setComboBoxPropertyValueTypesItems(items);
    }

    public void removeProperty(int convertRowIndexToModel) throws NoConnectionToDBException, SQLException, PropertyIsUsedException,
            PropertyTypeDoesNotExistException, IOException, PropertyNotInDBException, PropertyTypeNotExistException,
            ComputationMethodDoesNotExistException {
        Property toRemove = (Property) ((PropertyTableModel) tableSolverProperty.getModel()).getProperty(convertRowIndexToModel);
        PropertyDAO.remove(toRemove);
        ((PropertyTableModel) tableSolverProperty.getModel()).removeProperty(toRemove);
    }

    public void showProperty(int convertRowIndexToModel) {
        if (convertRowIndexToModel != -1) {
            Property toShow = (Property) ((PropertyTableModel) tableSolverProperty.getModel()).getProperty(convertRowIndexToModel);
            main.showProperty(toShow);
            this.editId = toShow.getId();
        } else {
            this.editId = -1;
        }
    }

    public void showPropertyTypeSelection() {
        main.showPropertyTypeSelection();
    }

    public void propertyTypeChanged() {
        main.propertyTypeChanged();
    }

    public void disablePropertyEditFields() {
        main.disablePropertyEditFields();
    }

    public void saveProperty(String name, String description, PropertyType type, String regExpression, ComputationMethod computationMethod,
            String computationMethodParameters, PropertySource source, PropertyValueType<?> valueType, boolean isMultipe)
            throws NoConnectionToDBException, SQLException, PropertyIsUsedException, PropertyTypeDoesNotExistException, IOException,
            PropertyNotInDBException, PropertyTypeNotExistException, ComputationMethodDoesNotExistException {
        Vector<String> regExpressions = new Vector<String>();
        if (editId != -1) {
            Property toEdit = PropertyDAO.getById(editId);
            toEdit.setName(name);
            toEdit.setDescription(description);
            PropertyDAO.save(toEdit);
        } else {
            // extract the single reg expressions from the regExp String
            String[] getRegExp = regExpression.split("\n");
            for (int i = 0; i < getRegExp.length; i++) {
                regExpressions.add(getRegExp[i]);
            }
            PropertyDAO.createProperty(name, regExpressions, description, type, valueType, source, isMultipe, computationMethod,
                    computationMethodParameters, name, isMultipe);
        }
        loadProperties();
        main.clearPropertyEditField();
    }

    public Vector<ComputationMethod> loadAllComputationMethods() throws NoConnectionToDBException, SQLException, ComputationMethodDoesNotExistException {
        return ComputationMethodDAO.getAll();
    }

    /**
     * Exports the Property Objects related to the given rows of the propertyTable of the view into the given path.
     * @param selectedRows
     * @param path 
     */
    public void exportProperty(int[] selectedRows, String path) {
        PropertyTableModel tableModel = (PropertyTableModel) tableSolverProperty.getModel();
        for (int i = 0; i < selectedRows.length; i++) {
            try {
                PropertyDAO.exportProperty(tableModel.getProperty(selectedRows[i]), path);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ManagePropertyController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ManagePropertyController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void importProperty(File[] files) {
        try {

                PropertyDAO.importProperty(files);
            
            loadProperties();
        } catch (ComputationMethodSameNameAlreadyExists ex) {
           JOptionPane.showMessageDialog(main,
                    "The name of a computation Method of an added property already exists, but the MD5 sums doesn't "
                   + " match.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ManagePropertyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ManagePropertyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManagePropertyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoConnectionToDBException ex) {
            Logger.getLogger(ManagePropertyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ManagePropertyController.class.getName()).log(Level.SEVERE, null, ex);    
        } catch (NoComputationMethodBinarySpecifiedException ex) {
            Logger.getLogger(ManagePropertyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyIsUsedException ex) {
            Logger.getLogger(ManagePropertyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyTypeDoesNotExistException ex) {
            Logger.getLogger(ManagePropertyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyNotInDBException ex) {
            Logger.getLogger(ManagePropertyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyTypeNotExistException ex) {
            Logger.getLogger(ManagePropertyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ComputationMethodDoesNotExistException ex) {
            Logger.getLogger(ManagePropertyController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
