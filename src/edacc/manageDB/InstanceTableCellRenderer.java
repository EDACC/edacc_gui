/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.NoConnectionToDBException;
import java.awt.Color;
import java.awt.Component;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author rretz
 */
public class InstanceTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
         Component cell = super.getTableCellRendererComponent
           (table, value, isSelected, hasFocus, row, column);
         Instance instance = (Instance) ((InstanceTableModel)table.getModel()).getValueAt(row, 5);
        try {
            if (InstanceDAO.IsInAnyExperiment(instance.getId())) {
                cell.setBackground(Color.orange);
            }
            else cell.setBackground(Color.white);
        } catch (NoConnectionToDBException ex) {
            Logger.getLogger(InstanceTableCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(InstanceTableCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
         return cell;

    }

}
