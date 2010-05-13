/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc;

import edacc.experiment.ExperimentResultsBrowserTableModel;
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
        row = table.convertRowIndexToModel(row);
        Integer status = ((ExperimentResultsBrowserTableModel) table.getModel()).getStatusCode(row);
        if (status == null) {
            return null;
        }
        switch (status) {
            case -2: // error
                comp.setBackground(Color.red);
                break;
            case -1: // waiting
                // comp.setBackground(new Color(4*16+1,6*16+9,14*16+1));// Color.getColor("4169e1"));
                //  comp.setBackground(new Color(10*16+13,13*16+8,14*16+6)); // add8e6 - light blue
                // comp.setBackground(new Color(4*16+6,8*16+2,11*16+4)); // 4682B4 - steelblue
                comp.setBackground(new Color(4 * 16 + 1, 6 * 16 + 9, 14 * 16 + 1)); // 4169E1 - royal blue
                //  comp.setBackground(Color.blue);
                break;
            case 0: // running
                comp.setBackground(Color.orange);
                break;
            case 1: // finished
            case 2:
            case 3:
                comp.setBackground(Color.green);
                break;
            default:
                comp.setBackground(null);
        }
        //((JComponent)comp).setOpaque(true);
        //comp.setForeground(Color.red);
        return comp;
    }
}
