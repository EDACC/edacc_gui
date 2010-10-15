/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.EDACCManagePropertyDialog;
import edacc.model.ComputationMethodDoesNotExistException;
import edacc.model.NoConnectionToDBException;
import edacc.model.Property;
import edacc.model.PropertyDAO;
import edacc.model.PropertyIsUsedException;
import edacc.model.PropertyNotInDBException;
import edacc.model.PropertyTypeDoesNotExistException;
import edacc.satinstances.PropertyValueType;
import edacc.satinstances.PropertyValueTypeManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;
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
       main.clearSolverPropertyEditField();
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
        ((PropertyTableModel)this.tableSolverProperty.getModel()).clear();
        ((PropertyTableModel)this.tableSolverProperty.getModel()).addResultProperties(new Vector<Property>(PropertyDAO.getAll()));
    }

    public void loadPropertyValueTypes() throws IOException, NoConnectionToDBException, SQLException{
        Vector<PropertyValueType<?>> propValueTypes = PropertyValueTypeManager.getInstance().getAll();
        Vector<String> items = new Vector<String>();
        for(int i = 0; i < propValueTypes.size(); i++){
            items.add(propValueTypes.get(i).getName());
        }
        main.setComboBoxPropertyValueTypesItems(items);
    }

    public void removeProperty(int convertRowIndexToModel) throws NoConnectionToDBException, SQLException, PropertyIsUsedException,
            PropertyTypeDoesNotExistException, IOException, PropertyNotInDBException, PropertyTypeNotExistException {
        Property toRemove = (Property)((PropertyTableModel)tableSolverProperty.getModel()).getValueAt(convertRowIndexToModel, 5);
        PropertyDAO.remove(toRemove);
        ((PropertyTableModel)tableSolverProperty.getModel()).removeProperty(toRemove);
    }

    public void showProperty(int convertRowIndexToModel) {
        if(convertRowIndexToModel != -1){
                Property toShow = (Property) ((PropertyTableModel)tableSolverProperty.getModel()).getProperty(convertRowIndexToModel);
                main.showProperty(toShow);
                this.editId = toShow.getId();
        }else 
            this.editId = -1;
    }

    public void saveSolverProperty(String name, String prefix, String description, PropertySource propType, String parameter)
            throws NoConnectionToDBException, SQLException, PropertyNotInDBException, PropertyTypeNotExistException, IOException, 
            PropertyIsUsedException, PropertyTypeDoesNotExistException, ComputationMethodDoesNotExistException{

        if(editId != -1){
            Property toEdit = PropertyDAO.getById(editId);
            toEdit.setName(name);
            toEdit.setRegularExpression(prefix);
            toEdit.setDescription(description);
            PropertyDAO.save(toEdit);
        }else {
        //   PropertyDAO.createProperty(name, prefix, description, propType, parameter);
        }
        loadProperties();
        main.clearSolverPropertyEditField();
    }

     public void saveSolverProperty(String name, String prefix, String description, PropertySource propType, String valueType, boolean isMultiple)
            throws NoConnectionToDBException, SQLException, PropertyNotInDBException, PropertyTypeNotExistException, IOException, 
            PropertyIsUsedException, ComputationMethodDoesNotExistException{

        if(editId != -1){
            Property toEdit = PropertyDAO.getById(editId);
            toEdit.setName(name);
            toEdit.setRegularExpression(prefix);
            toEdit.setDescription(description);
       //     PropertyDAO.save(toEdit);
        }else {
          //  PropertyDAO.createResultProperty(name, prefix, description, PropertyValueTypeManager.getInstance().getPropertyValueTypeByName(valueType), propType, isMultiple);
        }
        loadProperties();
        main.clearSolverPropertyEditField();
    }

    public void showPropertyTypeSelection() {
        main.showPropertyTypeSelection();
    }

    public void propertyTypeChanged() {
        main.propertyTypeChanged();
    }

    void disablePropertyEditFields() {
        main.disablePropertyEditFields();
    }
      

}
