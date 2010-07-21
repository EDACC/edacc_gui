package edacc.experiment.plots;

import edacc.EDACCApp;
import edacc.EDACCInstanceFilter;
import edacc.experiment.InstanceTableModel;
import edacc.experiment.InstanceTableModelRowFilter;
import edacc.model.Instance;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
    private InstanceTableModelRowFilter rowFilter;
    private TableRowSorter<InstanceTableModel> sorter;
    private JLabel lblFilter;

    public InstanceSelector() {
        super(new GridBagLayout());
        Dimension dimensionButton = new Dimension(109, 25);
        tableModel = new InstanceTableModel();
        table = new JTable(tableModel);
        rowFilter = new InstanceTableModelRowFilter();
        rowFilter.setFilterInstanceClasses(false);
        sorter = new TableRowSorter<InstanceTableModel>(tableModel);
        table.setRowSorter(sorter);
        sorter.setRowFilter(rowFilter);
        TableColumnModel colModel = table.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(350);
        colModel.getColumn(1).setPreferredWidth(15);
        colModel.getColumn(2).setPreferredWidth(15);
        colModel.getColumn(3).setPreferredWidth(15);
        colModel.getColumn(4).setPreferredWidth(15);
        colModel.getColumn(5).setPreferredWidth(15);
        scrollPane = new JScrollPane(table);
        lblFilter = new JLabel("");
        lblFilter.setForeground(Color.red);
        scrollPane.setMinimumSize(new Dimension(0,250));
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

            public void actionPerformed(ActionEvent e) {
                btnFilter();
            }
        });

        btnSelectAll.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                btnSelectAll();
            }
        });

        btnDeselectAll.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                btnDeselectAll();
            }
        });

        btnInvert.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                btnInvert();
            }
        });
    }

    public void btnFilter() {
        if (dialogFilter == null) {
            JFrame mainFrame = EDACCApp.getApplication().getMainFrame();
            dialogFilter = new EDACCInstanceFilter(mainFrame, true, rowFilter);
            dialogFilter.setLocationRelativeTo(mainFrame);
        }
        dialogFilter.loadValues();
        EDACCApp.getApplication().show(dialogFilter);
        tableModel.fireTableDataChanged();
        if (rowFilter.filter_name || rowFilter.filter_numAtoms ||
                rowFilter.filter_numClauses || rowFilter.filter_ratio ||
                rowFilter.filter_maxClauseLength) {
            lblFilter.setText("This list of instances has filters applied to it. Use the filter button below to modify.");
        } else {
            lblFilter.setText("");
        }
    }

    public void btnSelectAll() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (rowFilter.include((String) tableModel.getValueAt(i, 0),
                    (Integer) tableModel.getValueAt(i, 1),
                    (Integer) tableModel.getValueAt(i, 2),
                    (Float) tableModel.getValueAt(i, 3),
                    (Integer) tableModel.getValueAt(i, 4),
                    0)) {
                tableModel.setValueAt(true, i, 5);
            }
        }
    }

    public void btnDeselectAll() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (rowFilter.include((String) tableModel.getValueAt(i, 0),
                    (Integer) tableModel.getValueAt(i, 1),
                    (Integer) tableModel.getValueAt(i, 2),
                    (Float) tableModel.getValueAt(i, 3),
                    (Integer) tableModel.getValueAt(i, 4),
                    0)) {
                tableModel.setValueAt(false, i, 5);
            }
        }
    }

    public void btnInvert() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (rowFilter.include((String) tableModel.getValueAt(i, 0),
                    (Integer) tableModel.getValueAt(i, 1),
                    (Integer) tableModel.getValueAt(i, 2),
                    (Float) tableModel.getValueAt(i, 3),
                    (Integer) tableModel.getValueAt(i, 4),
                    0)) {
                tableModel.setValueAt(!((Boolean) tableModel.getValueAt(i, 5)), i, 5);
            }
        }
    }

    public void setInstances(Vector<Instance> instances) {
        tableModel.setInstances(instances);
    }

    public Vector<Instance> getSelectedInstances() {
        return tableModel.getSelectedInstances();
    }
}
