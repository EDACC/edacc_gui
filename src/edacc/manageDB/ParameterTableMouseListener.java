/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

/**
 *
 * @author dgall
 */
public class ParameterTableMouseListener extends MouseAdapter {

    private final JPopupMenu pm;

    public ParameterTableMouseListener(JPopupMenu pm) {
        this.pm = pm;
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        showPopupMenu(evt);
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        showPopupMenu(evt);
    }

    private void showPopupMenu(MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            pm.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }

}
