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
public class InstancesToAddSelectionListener implements ListSelectionListener{

    AddInstanceErrorController controller;
    
    public InstancesToAddSelectionListener(AddInstanceErrorController controller) {
        this.controller = controller;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        controller.updateFilter();
    }
    
}
