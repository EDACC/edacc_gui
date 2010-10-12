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
    private boolean[] visibility = new boolean[0];

    public JobsFilter(java.awt.Frame parent, boolean modal, JTable table) {
        super(parent, modal, table);
        if (!(table.getModel() instanceof ExperimentResultsBrowserTableModel)) {
            throw new IllegalArgumentException("Expected ExperimentResultBrowserTableModel.");
        }
        model = (ExperimentResultsBrowserTableModel) table.getModel();
    }

    @Override
    protected void updateLayout() {
        // make all columns visible, this will let the filter recognize every column
        boolean[] old = model.getColumnVisibility();
        if (visibility.length != old.length) {
            visibility = new boolean[old.length];
            for (int i = 0; i < visibility.length; i++) {
                visibility[i] = true;
            }
        }
        model.setColumnVisibility(visibility, false);
        super.updateLayout();
        // revert
        model.setColumnVisibility(old, false);
    }

    @Override
    public boolean include(Entry<? extends Object, ? extends Object> entry) {
        // make all columns visible, this will let the filter recognize every column
        boolean[] old = model.getColumnVisibility();
        if (visibility.length != old.length) {
            visibility = new boolean[old.length];
            for (int i = 0; i < visibility.length; i++) {
                visibility[i] = true;
            }
        }
        model.setColumnVisibility(visibility, false);
        boolean res = super.include(entry);
        // revert
        model.setColumnVisibility(old, false);
        return res;
    }
}
