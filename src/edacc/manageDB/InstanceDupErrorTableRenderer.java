/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author rretz
 */
public class InstanceDupErrorTableRenderer extends DefaultTableCellRenderer {

    AddInstanceErrorController controller;
    InstanceDupErrorTableModel dupErrorModel;

    public InstanceDupErrorTableRenderer(AddInstanceErrorController controller, InstanceDupErrorTableModel dupErrorModel) {
        super();
        this.controller = controller;
        this.dupErrorModel = dupErrorModel;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);       
        JLabel test = new JLabel();
        if (dupErrorModel.dupName(table.convertRowIndexToModel(row), controller.getToAddSelectedInstance(), table.convertColumnIndexToModel(column))) {
            if (isSelected) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            } else {
                comp.setBackground(Color.red);
            }
        } else if (dupErrorModel.dupMd5(table.convertRowIndexToModel(row), controller.getToAddSelectedInstance(), table.convertColumnIndexToModel(column))) {
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
