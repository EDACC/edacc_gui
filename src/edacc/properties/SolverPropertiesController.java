/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.EDACCManageSolverPropertyDialog;
import edacc.model.NoConnectionToDBException;
import edacc.model.SolverProperty;
import edacc.model.SolverPropertyDAO;
import edacc.model.SolverPropertyNotInDBException;
import edacc.satinstances.PropertyValueType;
import edacc.satinstances.PropertyValueTypeManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 *
 * @author rretz
 */
public class SolverPropertiesController {
    private EDACCManageSolverPropertyDialog main;
    private JPanel panel;
    private JTable tableSolverProperty;

    public SolverPropertiesController(EDACCManageSolverPropertyDialog manage, JPanel panelManageResultProperty, JTable tableResultProperty) {
        this.main = manage;
        this.panel = panelManageResultProperty;
        this.tableSolverProperty = tableResultProperty;
    }


    public void showSolverPropertyEditField(boolean enable) {
        main.enableSolverPropertyEditField(enable);
    }

    public void NewSolverProperty() {
        showSolverPropertyEditField(true);
        clearSolverPropertyEditField();
    }

    private void clearSolverPropertyEditField() {
       main.clearSolverPropertyEditField();
    }

    public void SolverPropertyTypeChanged() {
        main.SolverPropertyTypeChanged();
    }

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

}
