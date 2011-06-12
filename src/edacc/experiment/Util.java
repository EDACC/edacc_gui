package edacc.experiment;

import edacc.EDACCApp;
import edacc.model.InstanceClass;
import edacc.model.Parameter;
import edacc.model.ParameterDAO;
import edacc.model.ParameterInstance;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Static class with some useful(!) utility methods.
 * @author simon
 */
public class Util {

    public static final Color COLOR_ERROR = new Color(0xed1c24);
    public static final Color COLOR_GENERATEJOBSTABLE_UNSAVED_BIGGER = Color.green;
    public static final Color COLOR_GENERATEJOBSTABLE_UNSAVED_LOWER = COLOR_ERROR;

    /**
     * Updates the width of each column according to the table size and the data in the cells.
     * @param table
     */
    public static void updateTableColumnWidth(JTable table) {
        int tableWidth = table.getWidth();
        if (table.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF) {
            tableWidth = Integer.MAX_VALUE;
        }
        int colsum = 0;
        int width[] = new int[table.getColumnCount()];
        int minwidth[] = new int[table.getColumnCount()];
        int minwidthsum = 0;
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
                            0).getPreferredSize().width + 10;
                } else {
                    width[col] = table.getDefaultRenderer(String.class).getTableCellRendererComponent(
                            table,
                            table.getColumnModel().getColumn(col).getHeaderValue(),
                            false,
                            false,
                            0,
                            0).getPreferredSize().width + 10;
                }
                minwidth[col] = width[col];
                minwidthsum += width[col];
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
        for (int col = 0; col < table.getColumnCount(); col++) {
            double proz;
            // get the weight for a pixel
            if (table.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF) {
                proz = 1.;
            } else {

                proz = (double) tableWidth / (double) colsum;
            }
            // multiplicate the width of a column with the weight
            int w = (int) Math.round(proz * width[col]);
            if (w < minwidth[col]) {
                w = minwidth[col];
            }
            if (tableWidth - w < minwidthsum - minwidth[col]) {
                w = tableWidth - minwidthsum;
            }
            table.getColumnModel().getColumn(col).setPreferredWidth(w);
            minwidthsum -= minwidth[col];
            tableWidth -= w;
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
                    paramString += solverParameter.getPrefix() == null ? value : (solverParameter.getPrefix() + (solverParameter.getSpace() ? " " : "")) + value;
                } else {
                    paramString += solverParameter.getPrefix() == null ? "" : (solverParameter.getPrefix());
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
    @SuppressWarnings("empty-statement")
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

    public static ArrayList<Integer> getInstanceClassIdsFromPath(DefaultMutableTreeNode root) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        for (int i = 0; i < root.getChildCount(); i++) {
            res.addAll(getInstanceClassIdsFromPath((DefaultMutableTreeNode) root.getChildAt(i)));
        }
        res.add(((InstanceClass) root.getUserObject()).getId());
        return res;
    }

    public static String getPath() {
        File f = new File(EDACCApp.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        if (f.isDirectory()) {
            return f.getPath();
        } else {
            return f.getParent();
        }
    }

    public static boolean verifyNumber_geq(JTextField field, Integer number) {
        boolean res;
        try {
            res = Integer.parseInt(field.getText()) >= number;
        } catch (Exception ex) {
            res = false;
        }
        JTextField tmp = new JTextField();
        Border defaultBorder = tmp.getBorder();
        if (!res) {
            // Mark as error

            // default thickness
            int thickness = 1;
            if (tmp.getBorder() instanceof LineBorder) {
                // if the look and feel uses some line border then get the thickness from it
                thickness = ((LineBorder) tmp.getBorder()).getThickness();
            }
            // red line border with the hopefully same thickness as the old border
            Border errorBorder = BorderFactory.createLineBorder(new Color(255, 0, 0), thickness);
            // this is a hack: create a border inside the red border so that the distance from text to our new border seems to be the same as with the original border
            Border insideBorder = BorderFactory.createLineBorder(new Color(255, 238, 238), tmp.getBorder().getBorderInsets(tmp).left - thickness); // light red, color of the JTextField Background
            field.setBackground(new Color(255, 238, 238));
            field.setBorder(BorderFactory.createCompoundBorder(errorBorder, insideBorder));
        } else {
            // this is easier .. set the default border from look&feel
            field.setBackground(UIManager.getColor("TextField.background"));
            field.setBorder(defaultBorder);
        }
        return res;
    }

    /**
     * Replaces all illegal characters by '_' of filename and returns the result.
     * @param filename
     * @return
     */
    public static String getFilename(String filename) {
        final char[] ILLEGAL_CHARACTERS = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':', ' '};
        String res = "";
        for (char c : filename.toCharArray()) {
            boolean illegal = false;
            for (char i : ILLEGAL_CHARACTERS) {
                if (c == i) {
                    illegal = true;
                }
            }
            if (illegal) {
                res += "_";
            } else {
                res += c;
            }
        }
        return res;
    }

    /**
     * If this table has a editable boolean column, the user can press space to toggle the selection of the selected rows for the column.
     * @param table the table to add the key listener
     * @param column the column which has values of boolean class and is editable
     */
    public static void addSpaceSelection(final JTable table, final int column) {
        table.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    TableModel model = table.getModel();
                    for (int row : table.getSelectedRows()) {
                        int rowModel = table.convertRowIndexToModel(row);
                        model.setValueAt(!(Boolean) model.getValueAt(rowModel, column), rowModel, column);
                    }
                    e.consume();
                }
            }
        });
    }
}
