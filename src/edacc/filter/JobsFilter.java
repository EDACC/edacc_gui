package edacc.filter;

import edacc.experiment.ExperimentResultsBrowserTableModel;
import javax.swing.JTable;
import javax.swing.RowFilter.Entry;

/**
 *
 * @author simon
 */
public class JobsFilter extends Filter {

    private ExperimentResultsBrowserTableModel model;

    public JobsFilter(java.awt.Frame parent, boolean modal, JTable table, boolean autoUpdateFilterTypes) {
        super(parent, modal, table, autoUpdateFilterTypes);
        if (!(table.getModel() instanceof ExperimentResultsBrowserTableModel)) {
            throw new IllegalArgumentException("Expected ExperimentResultBrowserTableModel.");
        }
        model = (ExperimentResultsBrowserTableModel) table.getModel();
    }

    @Override
    public Object getValueAt(int row, int col) {
        return model.getRealValueAt(row, col);
    }


    @Override
    public void updateFilterTypes() {
        String[] columnNames = model.getAllColumnNames();
        Class<?>[] columnClasses = new Class<?>[columnNames.length];
        for (int i = 0; i < columnClasses.length; i++) {
            columnClasses[i] = model.getRealColumnClass(i);
        }
        super.updateFilterTypes(columnClasses, columnNames);
    }
}