package edacc;

import edacc.experiment.ClientTableModel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author simon
 */
public class EDACCExperimentModeClientCellRenderer extends DefaultTableCellRenderer {

    private JCheckBox checkbox;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof Boolean) {
            if (checkbox == null) {
                checkbox = new JCheckBox();
            }
            checkbox.setBackground(c.getBackground());
            checkbox.setForeground(c.getForeground());
            checkbox.setSelected((Boolean) value);
            checkbox.repaint();
            c = checkbox;
        }
        if (!isSelected) {
            if (((ClientTableModel) table.getModel()).getClientAt(table.convertRowIndexToModel(row)).isDead()) {
                c.setBackground(Color.RED);
            } else {
                c.setBackground(Color.WHITE);
            }
        }
        return c;
    }
}
