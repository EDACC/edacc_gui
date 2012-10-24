/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.EDACCSelectPropertyValueTypeClassDialog;
import edacc.model.NoConnectionToDBException;
import edacc.satinstances.PropertyValueType;
import edacc.satinstances.PropertyValueTypeAlreadyExistsException;
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
public class SelectPropertyValueTypeClassController {
    private EDACCSelectPropertyValueTypeClassDialog main;
    private JTable tablePropertyValueTypes;

     public SelectPropertyValueTypeClassController(EDACCSelectPropertyValueTypeClassDialog main, JTable tablePropertyValueTypes) {
        this.main = main;
        this.tablePropertyValueTypes = tablePropertyValueTypes;
    }

    public void addPropertyValueTypes(int[] selectedRows, File file) throws IOException, NoConnectionToDBException, SQLException, PropertyValueTypeAlreadyExistsException {
        Vector<String> toAdd = new Vector<String>();
        for(int i = 0; i < selectedRows.length; i++){
            toAdd.add((String)((PropertyValueTypeSelectionModel)tablePropertyValueTypes.getModel()).getValueAt(selectedRows[i], 0));
        }
        PropertyValueTypeManager.getInstance().addPropertyValueTypes(toAdd, file);
       
    }

    public Vector<String> readPropertyValueTypesFromFile(File file) throws SQLException, NoConnectionToDBException, IOException{
        return  PropertyValueTypeManager.getInstance().readNameFromJarFile(file);
    }

}
