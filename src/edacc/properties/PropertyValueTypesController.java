/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.EDACCManagePropertyValueTypesDialog;
import edacc.model.NoConnectionToDBException;
import edacc.satinstances.PropertyValueType;
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
        ((PropertyValueTypeTableModel) tablePropertyValueTypes.getModel()).addPropertyValueTypes(new Vector<PropertyValueType>(PropertyValueTypeManager.getInstance().getAll()));
    }

    public void newPropertyValueType() {
        main.enablePropertyValueTypeInputFields(true);
    }

    void showPropertyValueType(int convertRowIndexToModel) {
        main.enablePropertyValueTypeInputFields(false);
        main.showPropertyValueType((PropertyValueType)tablePropertyValueTypes.getModel().getValueAt(1, convertRowIndexToModel));
    }


    public void createNewPropertyValueType(File file, String name) throws IOException, NoConnectionToDBException, SQLException {
        PropertyValueTypeManager.getInstance().createNewPropertyValueType(file, name);
    }


}
