/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.EDACCSelectPropertyValueTypeClassDialog;
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

    public void addPropertyValueTypes(int[] selectedRows) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
