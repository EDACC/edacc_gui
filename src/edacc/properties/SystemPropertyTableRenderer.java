/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.properties;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author rretz
 */
public class SystemPropertyTableRenderer extends DefaultTableCellRenderer {

    private ImportPropertyCSVController controller;

    public SystemPropertyTableRenderer(ImportPropertyCSVController controller) {
        super();
        this.controller = controller;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (controller.propAlreadyChoosen(table.convertRowIndexToModel(row))) {
            if (isSelected) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            } else {
                comp.setBackground(Color.red);
            }
        } else {
            if (isSelected) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            } else {
                setBackground(Color.white);
            }
        }
        return this;
    }
}
