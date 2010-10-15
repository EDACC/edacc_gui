/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author rretz
 */
public class SolverPropertyTableSelectionListener implements ListSelectionListener{
    JTable table;
    ManagePropertyController controller;

    public SolverPropertyTableSelectionListener(JTable table, ManagePropertyController controller) {
        this.table = table;
        this.controller = controller;
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == table.getSelectionModel() && table.getRowSelectionAllowed()) {
            if (table.getSelectedRow() != -1) {
                controller.showProperty(table.convertRowIndexToModel(table.getSelectedRow()));
            } else
                controller.showSolverPropertyEditField(false);
            }
    }


}
