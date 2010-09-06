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
    ManageSolverProperties controller;

    public SolverPropertyTableSelectionListener(JTable table, ManageSolverProperties controller) {
        this.table = table;
        this.controller = controller;
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == table.getSelectionModel() && table.getRowSelectionAllowed() && table.getSelectedRowCount() != 0)
            controller.showSolverPropertyEditField(true);
        else
            controller.showSolverPropertyEditField(false);
    }

}
