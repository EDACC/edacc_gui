package edacc;

import edacc.experiment.ExperimentResultsBrowserTableModel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author daniel
 */
public class EDACCExperimentModeJobsCellRenderer extends DefaultTableCellRenderer {

    private final Color royalBlue = new Color(4 * 16 + 1, 6 * 16 + 9, 14 * 16 + 1);
    private final Color green = new Color(0 * 16 + 0, 12 * 16 + 12, 3 * 16 + 3);
    public int markRow = -1;
    public int markCol = -1;

    public EDACCExperimentModeJobsCellRenderer() {
        super();
        this.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        if (value == null) {
            int column = ((ExperimentResultsBrowserTableModel) table.getModel()).getIndexForColumn(col);
            if (column >= ExperimentResultsBrowserTableModel.COL_PROPERTY) {
                value = "not yet calculated";
            } else if (column == ExperimentResultsBrowserTableModel.COL_RUNTIME) {
                value = "not running";
            }
        }
        final Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        row = table.convertRowIndexToModel(row);
        Integer status = ((ExperimentResultsBrowserTableModel) table.getModel()).getStatus(row).getStatusCode();
        if (status == null) {
            return null;
        }

        if (status < -1) {
            comp.setBackground(Color.red);
        } else if (status == -1) {
            comp.setBackground(royalBlue);
        } else if (status == 0) {
            comp.setBackground(Color.orange);
        } else {
            comp.setBackground(green);
        }
        if (row == markRow && col == markCol) {
            comp.setBackground(Color.GRAY);
        }
        return comp;
    }
}
