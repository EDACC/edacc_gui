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
public class ComputationMethodTableSelectionListener implements ListSelectionListener {
    private JTable tableComputationMethod;
    private ManageComputationMethodController controller;

    public ComputationMethodTableSelectionListener(JTable tableComputationMethod, ManageComputationMethodController controller) {
        this.tableComputationMethod = tableComputationMethod;
        this.controller = controller;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(tableComputationMethod.getSelectedRow() != -1){
            int rowIndex = tableComputationMethod.convertRowIndexToModel(tableComputationMethod.getSelectedRow());
            controller.showComputationMethod(((ComputationMethodTableModel) tableComputationMethod.getModel()).getComputationMethod(rowIndex));
        }
    }

}
