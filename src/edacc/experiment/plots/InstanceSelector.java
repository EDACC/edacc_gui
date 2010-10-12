package edacc.experiment.plots;

import edacc.EDACCApp;
import edacc.EDACCInstanceFilter;
import edacc.experiment.InstanceTableModel;
import edacc.experiment.InstanceTableModelRowFilter;
import edacc.filter.InstanceFilter;
import edacc.model.Instance;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author simon
 */
public class InstanceSelector extends JPanel {

    private JScrollPane scrollPane;
    private JTable table;
    private InstanceTableModel tableModel;
    private JButton btnFilter, btnSelectAll, btnDeselectAll, btnInvert;
    private EDACCInstanceFilter dialogFilter;
    private InstanceFilter rowFilter;
    private TableRowSorter<InstanceTableModel> sorter;
    private JLabel lblFilter;

    public InstanceSelector() {
        super(new GridBagLayout());
        Dimension dimensionButton = new Dimension(109, 25);
        tableModel = new InstanceTableModel();
        table = new JTable(tableModel);
        table.moveColumn(0, 1);
        sorter = new TableRowSorter<InstanceTableModel>(tableModel);
        table.setRowSorter(sorter);
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                rowFilter = new InstanceFilter(EDACCApp.getApplication().getMainFrame(), true, table);
                rowFilter.setFilterInstanceClasses(false);
            }
        });

        TableColumnModel colModel = table.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(20);
        colModel.getColumn(1).setPreferredWidth(1000);
        /*       colModel.getColumn(2).setPreferredWidth(15);
        colModel.getColumn(3).setPreferredWidth(15);
        colModel.getColumn(4).setPreferredWidth(15);
        colModel.getColumn(5).setPreferredWidth(15);*/
        scrollPane = new JScrollPane(table);
        lblFilter = new JLabel("");
        lblFilter.setForeground(Color.red);
        scrollPane.setMinimumSize(new Dimension(0, 250));
        scrollPane.setPreferredSize(new Dimension(0, 250));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(lblFilter, c);
        c.gridy = 1;
        add(scrollPane, c);
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.gridy = 2;
        c.gridwidth = 1;
        btnFilter = new JButton("Filter");
        btnFilter.setPreferredSize(dimensionButton);
        btnFilter.setMinimumSize(dimensionButton);
        add(btnFilter, c);
        btnSelectAll = new JButton("Select All");
        btnSelectAll.setPreferredSize(dimensionButton);
        btnSelectAll.setMinimumSize(dimensionButton);
        c.gridx = 1;
        add(btnSelectAll, c);
        btnDeselectAll = new JButton("Deselect All");
        btnDeselectAll.setPreferredSize(dimensionButton);
        btnDeselectAll.setMinimumSize(dimensionButton);
        c.gridx = 2;
        add(btnDeselectAll, c);
        btnInvert = new JButton("Invert Selection");
        btnInvert.setPreferredSize(dimensionButton);
        btnInvert.setMinimumSize(dimensionButton);
        c.gridx = 3;
        add(btnInvert, c);

        btnFilter.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                btnFilter();
            }
        });

        btnSelectAll.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                btnSelectAll();
            }
        });

        btnDeselectAll.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                btnDeselectAll();
            }
        });

        btnInvert.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                btnInvert();
            }
        });
    }

    public void btnFilter() {
        EDACCApp.getApplication().show(rowFilter);
        tableModel.fireTableDataChanged();
        if (rowFilter.hasFiltersApplied()) {
            lblFilter.setText("This list of instances has filters applied to it. Use the filter button below to modify.");
        } else {
            lblFilter.setText("");
        }
    }

    public void btnSelectAll() {
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setValueAt(true, i, table.convertColumnIndexToView(InstanceTableModel.COL_SELECTED));
        }
    }

    public void btnDeselectAll() {
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setValueAt(false, i, table.convertColumnIndexToView(InstanceTableModel.COL_SELECTED));
        }
    }

    public void btnInvert() {
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setValueAt(!((Boolean) table.getValueAt(i, table.convertColumnIndexToView(InstanceTableModel.COL_SELECTED))), i, table.convertColumnIndexToView(InstanceTableModel.COL_SELECTED));
        }
    }

    public void setInstances(ArrayList<Instance> instances) {
        try {
            tableModel.setInstances(instances);
        } catch (Exception e) {
        }
    }

    public ArrayList<Instance> getSelectedInstances() {
        return tableModel.getSelectedInstances();
    }
}
