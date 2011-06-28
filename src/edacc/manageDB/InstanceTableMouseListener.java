/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

/**
 * MouseListener of the table of instances in the ManageDBMode.
 * Opens a JPopMenu, if the PopupTrieer has been used, at the Postion of the cursor of the mouse.
 * @author rretz
 */
public class InstanceTableMouseListener extends MouseAdapter {

    private final JPopupMenu jPM;

    public InstanceTableMouseListener(JPopupMenu jPM) {
        this.jPM = jPM;
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
