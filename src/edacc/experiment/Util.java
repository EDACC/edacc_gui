package edacc.experiment;

import edacc.model.Parameter;
import edacc.model.ParameterDAO;
import edacc.model.ParameterInstance;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        // iterate over all not filtered cells and get the length from each one.
        // the maximum of the lengths of all cells within a column col will be in width[col]
        if (table.getRowCount() == 0) {
            for (int i = 0; i < table.getColumnCount(); i++) {
                width[i] = table.getWidth() / table.getColumnCount();
            }
        } else {
            for (int col = 0; col < table.getColumnCount(); col++) {
                // set the width to the columns title width
                if (table.getColumnModel().getColumn(col).getHeaderRenderer() != null) {
                    width[col] = table.getColumnModel().getColumn(col).getHeaderRenderer().getTableCellRendererComponent(
                            table,
                            table.getColumnModel().getColumn(col).getHeaderValue(),
                            false,
                            false,
                            0,
                            0).getPreferredSize().width;
                } else {
                    width[col] = table.getDefaultRenderer(String.class).getTableCellRendererComponent(
                            table,
                            table.getColumnModel().getColumn(col).getHeaderValue(),
                            false,
                            false,
                            0,
                            0).getPreferredSize().width;
                }

                for (int row = 0; row < table.getRowCount(); row++) {
                    // get the component which represents the value and determine its witdth
                    int len = table.getCellRenderer(row, col).getTableCellRendererComponent(table, table.getValueAt(row, col), false, true, row, col).getPreferredSize().width;
                    if (len > width[col]) {
                        width[col] = len;
                    }
                }
                width[col] += table.getIntercellSpacing().width;
                colsum += width[col];
            }
        }
        // get the weight for a pixel
        double proz = (double) tableWidth / (double) colsum;
        for (int col = 0; col < table.getColumnCount(); col++) {
            // multiplicate the width of a column with the weight
            table.getColumnModel().getColumn(col).setPreferredWidth((int) Math.round(proz * width[col]));
        }
    }

    /**
     * Transforms the parameter instances in params to a string.
     * @param params the parameter instances
     * @return null if an error occurred
     */
    public static String getParameterString(ArrayList<ParameterInstance> params) {
        try {
            if (params == null) {
                return "";
            }
            String paramString = "";
            Collections.sort(params, new Comparator<ParameterInstance>() {

                @Override
                public int compare(ParameterInstance o1, ParameterInstance o2) {
                    try {
                        Parameter sp1 = ParameterDAO.getById(o1.getParameter_id());

                        Parameter sp2 = ParameterDAO.getById(o2.getParameter_id());
                        if (sp1.getOrder() > sp2.getOrder()) {
                            return 1;
                        } else if (sp1.getOrder() == sp2.getOrder()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    } catch (SQLException ex) {
                        return 0;
                    }
                }
            });
            for (ParameterInstance param : params) {
                Parameter solverParameter = ParameterDAO.getById(param.getParameter_id());
                if (solverParameter.getHasValue()) {
                    String value;
                    if ("instance".equals(solverParameter.getName().toLowerCase())) {
                        value = "<instance>";
                    } else if ("seed".equals(solverParameter.getName().toLowerCase())) {
                        value = "<seed>";
                    } else {
                        value = param.getValue();
                    }
                    paramString += solverParameter.getPrefix() == null ? value : (solverParameter.getPrefix() + " ") + value;
                } else {
                    paramString += solverParameter.getPrefix() == null ? "" : (solverParameter.getPrefix() + " ");
                }

                if (params.get(params.size() - 1) != param) {
                    paramString += " ";
                }
            }
            return paramString;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the empty string or any string containing a non-negative number or -1.
     * @param text
     * @return the number text
     */
    public static String getNumberText(String text) {
        String res = "";
        if (text.length() > 0) {
            if (text.charAt(0) == '-') {
                if (text.length() > 1) {
                    if (text.charAt(1) == '1') {
                        return "-1";
                    } else {
                        return "-";
                    }
                } else {
                    return "-";
                }
            }
        }
        int begin;
        for (begin = 0; begin < text.length() && text.charAt(begin) == '0'; begin++);
        if (begin > 0 && begin == text.length()) {
            return "0";
        }
        for (int i = begin; i < text.length(); i++) {
            if (text.charAt(i) >= '0' && text.charAt(i) <= '9') {
                res += text.charAt(i);
            }
        }
        return res;
    }
}
