/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 *
 * @author rretz
 */
public class ManagePropertyDialogSourceListener implements ItemListener{
    private ManagePropertyController controller;
    
    public ManagePropertyDialogSourceListener(ManagePropertyController controller) {
        this.controller = controller;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        controller.propertySourceChanged();
    }

}
