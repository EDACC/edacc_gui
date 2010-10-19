package edacc.experiment;

import javax.swing.JTable;

/**
 * Static class with some useful(!) utility methods.
 * @author simon
 */
public class Util {

    /**
     * Updates the width of each column according to the table size and the data in the cells.
     * @param table
     */
    public static void updateTableColumnWidth(JTable table) {
        int tableWidth = table.getWidth();
        int colsum = 0;
        int width[] = new int[table.getColumnCount()];
        for (int col = 0; col < table.getColumnCount(); col++) {
            width[col] = table.getFontMetrics(table.getFont()).stringWidth(table.getColumnName(col));
            for (int row = 0; row < table.getRowCount(); row++) {
                if (table.getValueAt(row, col) == null) {
                    continue;
                }
                String s = table.getValueAt(row, col).toString();
                int len = table.getFontMetrics(table.getFont()).stringWidth(s);
                if (len > width[col]) {
                    width[col] = len;
                }
            }
            colsum += width[col];
        }
        double proz = (double) tableWidth / (double) colsum;
        for (int col = 0; col < table.getColumnCount(); col++) {
            table.getColumnModel().getColumn(col).setPreferredWidth((int) (proz * width[col]));
        }
    }
}
