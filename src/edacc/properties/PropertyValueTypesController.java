/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.EDACCManagePropertyValueTypesDialog;
import edacc.EDACCSelectPropertyValueTypeClassDialog;
import edacc.model.NoConnectionToDBException;
import edacc.satinstances.PropertyValueType;
import edacc.satinstances.PropertyValueTypeInPropertyException;
import edacc.satinstances.PropertyValueTypeManager;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JTable;

/**
 *
 * @author rretz
 */
public class PropertyValueTypesController {
    private EDACCManagePropertyValueTypesDialog main;
    private JTable tablePropertyValueTypes;
    private EDACCSelectPropertyValueTypeClassDialog selectValueType;


     public PropertyValueTypesController(EDACCManagePropertyValueTypesDialog main, JTable tablePropertyValueTypes) {
        this.main = main;
        this.tablePropertyValueTypes = tablePropertyValueTypes;
    }

     /**
      * Clears the PropertyValueType table and adds all PropertyValueType objects from the database into the table.
      * @throws NoConnectionToDBException
      * @throws SQLException
      * @throws IOException
      */
    public void loadPropertyValueTypes() throws NoConnectionToDBException, SQLException, IOException {
        ((PropertyValueTypeTableModel) tablePropertyValueTypes.getModel()).clearTable();
        Vector<PropertyValueType<?>> tmp = PropertyValueTypeManager.getInstance().getAll();
        ((PropertyValueTypeTableModel) tablePropertyValueTypes.getModel()).addPropertyValueTypes(tmp);
    }

    public void createNewPropertyValueType(File file) throws IOException, NoConnectionToDBException, SQLException {
        PropertyValueTypeManager.getInstance().readNameFromJarFile(file);

    }

    public void removePropertyValueType(int rowIndex) throws IOException, NoConnectionToDBException, SQLException, PropertyValueTypeInPropertyException {
        PropertyValueType<?> toRemove = (PropertyValueType)((PropertyValueTypeTableModel)tablePropertyValueTypes.getModel()).getValueAt(rowIndex, 2);
        PropertyValueTypeManager.getInstance().remove(toRemove);
        ((PropertyValueTypeTableModel)tablePropertyValueTypes.getModel()).remove(toRemove);
    }


}
