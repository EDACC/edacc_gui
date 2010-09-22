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
    private final Color royalBlue = new Color(4 * 16 + 1, 6 * 16 + 9, 14 * 16 + 1);
    public EDACCExperimentModeJobsCellRenderer() {
        super();
        this.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        row = table.convertRowIndexToModel(row);
        Integer status = ((ExperimentResultsBrowserTableModel) table.getModel()).getStatus(row).getValue();
        if (status == null) {
            return null;
        }

        if (status < -1)
                comp.setBackground(Color.red);
        else if (status == -1)
            comp.setBackground(royalBlue);
        else if (status == 0)
            comp.setBackground(Color.orange);
        else comp.setBackground(Color.green);

        return comp;
    }
}
