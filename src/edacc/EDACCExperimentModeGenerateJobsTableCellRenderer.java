package edacc;

import edacc.experiment.GenerateJobsTableModel;
import edacc.experiment.Util;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author simon
 */
public class EDACCExperimentModeGenerateJobsTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
            GenerateJobsTableModel model = (GenerateJobsTableModel) table.getModel();
            int rowModel = table.convertRowIndexToModel(row);
            int colModel = table.convertColumnIndexToModel(column);
            int numRuns = model.getNumRuns(model.getInstance(rowModel), model.getSolverConfiguration(colModel));
            int savedNumRuns = model.getSavedNumRuns(model.getInstance(rowModel), model.getSolverConfiguration(colModel));
            if (numRuns > savedNumRuns) {
                c.setBackground(Util.COLOR_GENERATEJOBSTABLE_UNSAVED_BIGGER);
            } else if (numRuns < savedNumRuns) {
                c.setBackground(Util.COLOR_GENERATEJOBSTABLE_UNSAVED_LOWER);
            } else {
                c.setBackground(Color.white);
            }
        }
        return c;
    }
    
}
