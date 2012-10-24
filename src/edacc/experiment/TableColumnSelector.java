package edacc.experiment;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 *
 * @author simon
 */
public class TableColumnSelector {

    private JTable table;
    private TableColumn[] columns;

    /**
     * Creates a new table column selector for a <code>JTable</code>.
     * @param table the table to be used
     */
    public TableColumnSelector(JTable table) {
        this.table = table;

        columns = new TableColumn[table.getColumnCount()];
        for (int i = 0; i < table.getColumnCount(); i++) {
            columns[i] = table.getColumnModel().getColumn(i);
        }
    }

    private void setVisible(Object identifier, boolean visible) {
        try {
            if (!visible) {
                table.removeColumn(table.getColumn(identifier));
            } else {
                int targetCol = 0;
                for (TableColumn col : columns) {
                    try {
                        table.getColumn(col.getIdentifier());
                        targetCol++;
                    } catch (Exception e) {
                    }
                    if (col.getIdentifier().equals(identifier)) {
                        table.addColumn(col);
                        break;
                    }
                }
                table.moveColumn(table.getColumnCount()-1, targetCol);
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Returns the current visibility of the columns.
     * @return visibility as boolean array
     */
    public boolean[] getColumnVisibility() {
        boolean[] res = new boolean[columns.length];
        for (int i = 0; i < columns.length; i++) {
            try {
                if (table.getColumn(columns[i].getIdentifier()) != null) {
                    res[i] = true;
                }
            } catch (Exception e) {
                res[i] = false;
            }
        }
        return res;
    }

    /**
     * Sets the visibility of the row.<br/>
     * <br/>
     * Throws an <code>IllegalArgumentException</code> if <code>visiblity.length != columns.length</code> where columns are the initial tables columns.
     * @param visibility the visibility of the columns
     */
    public void setColumnVisiblity(boolean[] visibility) {
        if (visibility.length != columns.length) {
            throw new IllegalArgumentException("Lengths differ: " + visibility.length + " != " + columns.length);
        }
        for (int i = 0; i < visibility.length; i++) {
            if (!visibility[i]) {
                setVisible(columns[i].getIdentifier(), false);
            } else {
                try {
                    table.getColumn(columns[i].getIdentifier());
                } catch (Exception e) {
                    setVisible(columns[i].getIdentifier(), true);
                }
            }
        }
    }
    
    /**
     * Returns the name of the column at <code>idx</code>
     * @param idx the column index
     * @return the name of the column
     */
    public String getColumnName(int idx) {
        return (String) columns[idx].getIdentifier();
    }
}
