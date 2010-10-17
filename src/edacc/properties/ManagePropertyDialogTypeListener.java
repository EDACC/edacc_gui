/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;

/**
 *
 * @author rretz
 */
public class ManagePropertyDialogTypeListener implements ItemListener{
    private ManagePropertyController controller;
    private JComboBox comboBoxPropertyType;

    public ManagePropertyDialogTypeListener(ManagePropertyController controller, JComboBox comboBoxPropertyType) {
        this.controller = controller;
        this.comboBoxPropertyType = comboBoxPropertyType;

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        controller.propertyTypeChanged();
    }


}
