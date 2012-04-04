package edacc.experiment;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author simon
 */
public class ThreadSafeDefaultTableModel extends DefaultTableModel {

    private boolean update = false;

    public synchronized void beginUpdate() {
        update = true;
    }

    public synchronized void endUpdate() {
        update = false;
        fireTableDataChanged();
    }

    @Override
    public void fireTableCellUpdated(final int row, final int column) {
        synchronized (this) {
            if (update) {
                return;
            }
        }
        if (SwingUtilities.isEventDispatchThread()) {
            super.fireTableCellUpdated(row, column);
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ThreadSafeDefaultTableModel.super.fireTableCellUpdated(row, column);
                }
            });
        }
    }

    @Override
    public void fireTableChanged(final TableModelEvent e) {
        synchronized (this) {
            if (update) {
                return;
            }
        }
        if (SwingUtilities.isEventDispatchThread()) {
            super.fireTableChanged(e);
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ThreadSafeDefaultTableModel.super.fireTableChanged(e);
                }
            });
        }
    }

    @Override
    public void fireTableDataChanged() {
        synchronized (this) {
            if (update) {
                return;
            }
        }
        if (SwingUtilities.isEventDispatchThread()) {
            super.fireTableDataChanged();
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ThreadSafeDefaultTableModel.super.fireTableDataChanged();
                }
            });
        }
    }

    @Override
    public void fireTableRowsDeleted(final int firstRow, final int lastRow) {
        synchronized (this) {
            if (update) {
                return;
            }
        }
        if (SwingUtilities.isEventDispatchThread()) {
            super.fireTableRowsDeleted(firstRow, lastRow);
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ThreadSafeDefaultTableModel.super.fireTableRowsDeleted(firstRow, lastRow);
                }
            });
        }
    }

    @Override
    public void fireTableRowsInserted(final int firstRow, final int lastRow) {
        synchronized (this) {
            if (update) {
                return;
            }
        }
        if (SwingUtilities.isEventDispatchThread()) {
            super.fireTableRowsInserted(firstRow, lastRow);
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ThreadSafeDefaultTableModel.super.fireTableRowsInserted(firstRow, lastRow);
                }
            });
        }
    }

    @Override
    public void fireTableRowsUpdated(final int firstRow, final int lastRow) {
        synchronized (this) {
            if (update) {
                return;
            }
        }
        if (SwingUtilities.isEventDispatchThread()) {
            super.fireTableRowsUpdated(firstRow, lastRow);
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ThreadSafeDefaultTableModel.super.fireTableRowsUpdated(firstRow, lastRow);
                }
            });
        }
    }

    @Override
    public void fireTableStructureChanged() {
        synchronized (this) {
            if (update) {
                return;
            }
        }
        if (SwingUtilities.isEventDispatchThread()) {
            super.fireTableStructureChanged();
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ThreadSafeDefaultTableModel.super.fireTableStructureChanged();
                }
            });
        }
    }
}
