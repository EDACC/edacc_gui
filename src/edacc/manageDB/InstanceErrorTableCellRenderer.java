/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author rretz
 */
public class InstanceErrorTableCellRenderer extends DefaultTableCellRenderer {

    AddInstanceErrorController controller;

    public InstanceErrorTableCellRenderer(AddInstanceErrorController controller) {
        super();
        this.controller = controller;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (controller.isLinked(row)) {
             if (isSelected) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            } else {
                setBackground(Color.green);
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
