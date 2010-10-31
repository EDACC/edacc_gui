package edacc;

import edacc.experiment.ExperimentResultsBrowserTableModel;
import javax.swing.JTable;

/**
 * This filter can be used for the ExperimentResultsBrowserTableModel. It supports invisible columns.
 * @author simon
 */
public class EDACCJobsFilter extends EDACCFilter {

    private ExperimentResultsBrowserTableModel model;

    /**
     * Throws an IllegalArgumentException if the model of the table is not an instance of ExperimentResultsBrowserTableModel.
     * @param parent
     * @param modal
     * @param table
     * @param autoUpdateFilterTypes
     */
    public EDACCJobsFilter(java.awt.Frame parent, boolean modal, JTable table, boolean autoUpdateFilterTypes) {
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
