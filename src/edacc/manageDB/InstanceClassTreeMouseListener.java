/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

/**
 * MouseListener of the tree of instance classes in the ManageDBMode.
 * Opens a JPopMenu, if the PopupTrieer has been used, at the Postion of the cursor of the mouse.
 * @author rretz
 */
public class InstanceClassTreeMouseListener extends MouseAdapter {

    private final JPopupMenu jPM;

    public InstanceClassTreeMouseListener(JPopupMenu jPMInstanceTree) {
        this.jPM = jPMInstanceTree;
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            jPM.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            jPM.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }
}
