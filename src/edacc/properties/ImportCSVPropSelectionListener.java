/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.properties;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author rretz
 */
public class ImportCSVPropSelectionListener implements ListSelectionListener {

    private ImportPropertyCSVController controller;

    public ImportCSVPropSelectionListener(ImportPropertyCSVController controller) {
        this.controller = controller;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
       controller.refreshSysPropTable();
    }
}
