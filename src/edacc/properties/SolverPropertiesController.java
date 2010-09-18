/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.EDACCManageSolverPropertyDialog;
import edacc.model.NoConnectionToDBException;
import edacc.model.SolverProperty;
import edacc.model.SolverPropertyDAO;
import edacc.model.SolverPropertyIsUsedException;
import edacc.model.SolverPropertyNotInDBException;
import edacc.satinstances.PropertyValueType;
import edacc.satinstances.PropertyValueTypeManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 * Controller class of the EDACCManageSolverDialog
 * @author rretz
 */
public class SolverPropertiesController {
    private EDACCManageSolverPropertyDialog main;
    private JPanel panel;
    private JTable tableSolverProperty;

    /**
     * Constructor of the SolverPropertiesController
     * @param manage the EDACCManageSolverPropertyDialog to controll
     * @param panelManageResultProperty main panel of the EDACCManagerSolverPropertyDialog
     * @param tableResultProperty the table which contains the Overview over all SolverProperty objects.
     */
    public SolverPropertiesController(EDACCManageSolverPropertyDialog manage, JPanel panelManageResultProperty, JTable tableResultProperty) {
        this.main = manage;
        this.panel = panelManageResultProperty;
        this.tableSolverProperty = tableResultProperty;
    }

    /**
     * Enables or disables all input fields of the GUI which are required for editing and creating new SolverProperties
     * @param enable true for enable, false for disable
     */
    public void showSolverPropertyEditField(boolean enable) {
        main.enableSolverPropertyEditField(enable);
    }

    /**
     * Clears and enables the input fields in the gui.
     */
    public void NewSolverProperty() {
        showSolverPropertyEditField(true);
        clearSolverPropertyEditField();
    }

    /**
     * Clears the input fields of the gui.
     */
    private void clearSolverPropertyEditField() {
       main.clearSolverPropertyEditField();
    }

    /**
     * Enables and disables some of the input fields of the gui controlled by the selected SolverPropertyType
     */
    public void SolverPropertyTypeChanged() {
        main.SolverPropertyTypeChanged();
    }

    /**
     * 
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws SQLException
     * @throws SolverPropertyNotInDBException
     * @throws SolverPropertyTypeNotExistException
     * @throws IOException
     */
    public void loadSolverProperties() 
            throws NoConnectionToDBException, SQLException, SQLException, SolverPropertyNotInDBException,
        SolverPropertyTypeNotExistException, IOException {
        ((SolverPropertyTableModel)this.tableSolverProperty.getModel()).clear();
        ((SolverPropertyTableModel)this.tableSolverProperty.getModel()).addResultProperties(new Vector<SolverProperty>(SolverPropertyDAO.getAll()));
    }

    public void loadPropertyValueTypes() throws IOException, NoConnectionToDBException, SQLException{
        Vector<PropertyValueType<?>> propValueTypes = PropertyValueTypeManager.getInstance().getAll();
        Vector<String> items = new Vector<String>();
        for(int i = 0; i < propValueTypes.size(); i++){
            items.add(propValueTypes.get(i).getName());
        }
        main.setComboBoxPropertyValueTypesItems(items);
    }

    public void removeSolverProperty(int convertRowIndexToModel) throws NoConnectionToDBException, SQLException, SolverPropertyIsUsedException {
        SolverProperty toRemove = (SolverProperty)((SolverPropertyTableModel)tableSolverProperty.getModel()).getValueAt(convertRowIndexToModel, 5);
        SolverPropertyDAO.remove(toRemove);
        ((SolverPropertyTableModel)tableSolverProperty.getModel()).removeSolverProperty(toRemove);
    }

}
