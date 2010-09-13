/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.EDACCManagePropertyValueTypesDialog;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author rretz
 */
public class PropertyValueTypeTableSelectionListener implements ListSelectionListener{
    private JTable table;
    private PropertyValueTypesController controller;

    public PropertyValueTypeTableSelectionListener(JTable table, PropertyValueTypesController controller) {
        this.table = table;
        this.controller = controller;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
         if (e.getSource() == table.getSelectionModel() && table.getRowSelectionAllowed()) {
            if (table.getSelectedRow() != -1) {
                controller.showPropertyValueType(table.convertRowIndexToModel(table.getSelectedRow()));
            } else
                controller.showPropertyValueType(-1);
        }
    }

}
