package edacc.experiment;

import edacc.model.StatusCode;
import javax.swing.DefaultRowSorter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author simon
 */
public class ResultsBrowserTableRowSorter extends TableRowSorter<AbstractTableModel> {

    private ExperimentResultsBrowserTableModel jobsTableModel;

    public ResultsBrowserTableRowSorter(ExperimentResultsBrowserTableModel jobsTableModel) {
        super(jobsTableModel);
        this.jobsTableModel = jobsTableModel;
    }

    @Override
    public void setModel(AbstractTableModel model) {
        super.setModel(new DefaultTableModel() {

            @Override
            public int getRowCount() {
                return jobsTableModel == null ? 0 : jobsTableModel.getRowCount();
            }

            @Override
            public Object getValueAt(int row, int column) {
                return jobsTableModel.getValueAt(row, column);
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == ExperimentResultsBrowserTableModel.COL_RUNTIME) {
                    return Integer.class;
                }
                return jobsTableModel.getColumnClass(columnIndex);
            }

            @Override
            public int getColumnCount() {
                return jobsTableModel == null ? 0 : jobsTableModel.getColumnCount();
            }
        });
        setModelWrapper(new ExperimentResultsBrowserModelWrapper<AbstractTableModel>(getModelWrapper()));
    }

    @Override
    public void toggleSortOrder(int column) {
        synchronized (getModel()) {
            super.toggleSortOrder(column);
        }
    }

    class ExperimentResultsBrowserModelWrapper<M extends TableModel> extends DefaultRowSorter.ModelWrapper<M, Integer> {

        private DefaultRowSorter.ModelWrapper<M, Integer> delegate;

        public ExperimentResultsBrowserModelWrapper(DefaultRowSorter.ModelWrapper<M, Integer> delegate) {
            this.delegate = delegate;
        }

        @Override
        public M getModel() {
            return delegate.getModel();
        }

        @Override
        public int getColumnCount() {
            return delegate.getColumnCount();
        }

        @Override
        public int getRowCount() {
            return delegate.getRowCount();
        }

        @Override
        public Object getValueAt(int row, int col) {
            ExperimentResultsBrowserTableModel model = jobsTableModel;
            if (col == ExperimentResultsBrowserTableModel.COL_STATUS) {
                return "" + (char) (model.getStatus(row).getStatusCode() + 68);
            } else if (col == ExperimentResultsBrowserTableModel.COL_RUNTIME) {
                if (model.getExperimentResult(row).getStatus().equals(StatusCode.RUNNING)) {
                    return model.getExperimentResult(row).getRunningTime();
                } else {
                    return -1;
                }
            }
            return model.getValueAt(row, col);
        }

        @Override
        public Integer getIdentifier(int row) {
            return delegate.getIdentifier(row);
        }
    }
}