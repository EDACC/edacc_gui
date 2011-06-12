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

    public TableColumnSelector(JTable table) {
        this.table = table;

        columns = new TableColumn[table.getColumnCount()];
        for (int i = 0; i < table.getColumnCount(); i++) {
            columns[i] = table.getColumnModel().getColumn(i);
        }
    }

    public void setVisible(Object identifier, boolean visible) {
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
    
    public String getColumnName(int idx) {
        return (String) columns[idx].getIdentifier();
    }
}
