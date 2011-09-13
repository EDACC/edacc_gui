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
public class SolverBinariesTableSelectionListener implements ListSelectionListener {
    private JTable table;
    private ManageDBSolvers controller;

    public SolverBinariesTableSelectionListener(JTable table, ManageDBSolvers controller) {
        this.table = table;
        this.controller = controller;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == table.getSelectionModel() && table.getRowSelectionAllowed()) {
            controller.selectSolverBinary(table.getSelectedRow() != -1);
        }
    }
}
