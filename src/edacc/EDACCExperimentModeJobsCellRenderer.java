package edacc;

import edacc.experiment.ExperimentResultsBrowserTableModel;
import edacc.experiment.Util;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author daniel
 */
public class EDACCExperimentModeJobsCellRenderer extends DefaultTableCellRenderer {
    public int markRow = -1;
    public int markCol = -1;

    public EDACCExperimentModeJobsCellRenderer() {
        super();
        this.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        if (value == null) {
            int column = col;
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
            comp.setBackground(Util.COLOR_JOBBROWSER_ERROR);
        } else if (status == -1) {
            comp.setBackground(Util.COLOR_JOBBROWSER_WAITING);
        } else if (status == 0) {
            comp.setBackground(Util.COLOR_JOBBROWSER_RUNNING);
        } else {
            comp.setBackground(Util.COLOR_JOBBROWSER_FINISHED);
        }
        if (row == markRow && col == markCol) {
            comp.setBackground(Color.GRAY);
        }
        return comp;
    }
}
