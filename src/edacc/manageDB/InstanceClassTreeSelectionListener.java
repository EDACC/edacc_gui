/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 *
 * @author rretz
 */
public class InstanceClassTreeSelectionListener implements TreeSelectionListener{
    ManageDBInstances controller;
    JTree tree;

    public InstanceClassTreeSelectionListener(ManageDBInstances controller, JTree tree){
        this.controller = controller;
        this.tree = tree;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
            if(tree.getSelectionCount() == 1){
                controller.showInstanceClassButtons(true);
            }
            else{
                controller.showInstanceClassButtons(false);
            }
            controller.changeInstanceTable();
    }

}
