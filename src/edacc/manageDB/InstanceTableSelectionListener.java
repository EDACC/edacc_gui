/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author rretz
 */
public class InstanceTableSelectionListener implements ListSelectionListener{
    JTable table;
    ManageDBInstances controller;

    public InstanceTableSelectionListener(JTable table, ManageDBInstances controller) {
        this.table = table;
        this.controller = controller;
    }
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == table.getSelectionModel() && table.getRowSelectionAllowed() && table.getSelectedRowCount() != 0)
            controller.showInstanceButtons(true);
        else
            controller.showInstanceButtons(false);
    }

}
