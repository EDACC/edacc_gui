package edacc.manageDB;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author simon
 */
public class CostBinaryTableSelectionListener implements ListSelectionListener {
    private JTable table;
    private ManageDBCosts controller;

    public CostBinaryTableSelectionListener(JTable table, ManageDBCosts controller) {
        this.table = table;
        this.controller = controller;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == table.getSelectionModel() && table.getRowSelectionAllowed()) {
            controller.selectCostBinary(table.getSelectedRow() != -1);
        }
    }
    
}
