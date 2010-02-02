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
 * @author dgall
 */
public class ParameterTableSelectionListener implements ListSelectionListener {

    private JTable table;
    private ManageDBParameters controller;

    public ParameterTableSelectionListener(JTable table, ManageDBParameters controller) {
        this.table = table;
        this.controller = controller;
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == table.getSelectionModel() && table.getRowSelectionAllowed()) {
            controller.showParameter(table.getSelectedRow());
        }
    }
}
