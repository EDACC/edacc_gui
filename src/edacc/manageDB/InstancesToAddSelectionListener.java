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
public class InstancesToAddSelectionListener implements ListSelectionListener {

    AddInstanceErrorController controller;

    public InstancesToAddSelectionListener(AddInstanceErrorController controller) {
        this.controller = controller;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (controller.getSelectedToAddRowCount() != 1) {
            controller.noneFilter();
            controller.mulipleSelectionBtnShow(true);
        } else {
            if (controller.isSelected()) {
                controller.updateFilter(); 
                controller.mulipleSelectionBtnShow(false);
            }    
            
        }

    }
}
