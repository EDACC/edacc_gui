package edacc;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

/**
 *
 * @author simon
 */
public class JTableTooltipInformation extends JTable {

    private String defaultToolTip;

    public JTableTooltipInformation() {
        super();
        defaultToolTip = "";
    }

    public JTableTooltipInformation(TableModel tableModel) {
        super(tableModel);
        defaultToolTip = "";
    }

    @Override
    public void setToolTipText(String text) {
        defaultToolTip = text;
    }

    @Override
    public String getToolTipText() {
                String text = "<html>";
        if (defaultToolTip != null && !"".equals(defaultToolTip)) {
            text += defaultToolTip + "<br/>";
        }
        int rows = JTableTooltipInformation.this.getSelectedRowCount();
        text += rows + " / " + JTableTooltipInformation.this.getRowCount() + " selected</html>";
        return text;
    }    
}
