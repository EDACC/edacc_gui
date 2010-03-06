/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author daniel
 */
public class EDACCExperimentModeJobsCellRenderer extends DefaultTableCellRenderer {
    public EDACCExperimentModeJobsCellRenderer() {
        super();
        this.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        int status = ((Integer)table.getModel().getValueAt(row, -1)).intValue();
        switch (status) {
            case -2: // error
                comp.setBackground(Color.red); break;
            case -1: // waiting
                comp.setBackground(Color.blue); break;
            case 0: // running
                comp.setBackground(Color.orange); break;
            case 1: // finished
            case 2:
            case 3:
                comp.setBackground(Color.green); break;
            default:
                comp.setBackground(null);
        }
        //((JComponent)comp).setOpaque(true);
        //comp.setForeground(Color.red);
        return comp;
    }
}
