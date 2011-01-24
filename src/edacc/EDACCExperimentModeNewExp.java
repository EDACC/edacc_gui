/*
 * EDACCExperimentModeNewSolver.java
 *
 * Created on 14.06.2010, 11:47:44
 */
package edacc;

import edacc.experiment.ExperimentController;
import edacc.experiment.ExperimentTableModel;
import edacc.experiment.SolverConfigurationTableModel;
import edacc.experiment.Util;
import edacc.model.Experiment;
import edacc.model.SolverConfiguration;
import edacc.model.SolverConfigurationDAO;
import edacc.model.SolverDAO;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author balint
 */
public class EDACCExperimentModeNewExp extends javax.swing.JDialog {

    public String expName;
    public String expDesc;
    public boolean canceled;
    private Experiment currentExperiment;
    private ExperimentTableModel experimentTableModel;
    private SolverConfigurationTableModel solverConfigTableModel;
    private ExperimentSolverConfigurationTableModel experimentSolverConfigTableModel;
    private ExperimentSolverConfigurationRowFilter experimentSolverConfigRowFilter;

    /** Creates new form EDACCExperimentModeNewExp */
    public EDACCExperimentModeNewExp(java.awt.Frame parent, boolean modal, ExperimentController expController) {
        super(parent, modal);
        experimentTableModel = new ExperimentTableModel();
        solverConfigTableModel = new SolverConfigurationTableModel();
        experimentSolverConfigTableModel = new ExperimentSolverConfigurationTableModel();
        initComponents();

        try {
            ArrayList<ExperimentSolverConfiguration> items = new ArrayList<ExperimentSolverConfiguration>();
            for (Experiment exp : expController.getExperiments()) {
                for (SolverConfiguration sc : SolverConfigurationDAO.getSolverConfigurationByExperimentId(exp.getId())) {
                    if (sc.getName() == null) {
                        SolverConfigurationDAO.updateName(sc);
                    }
                    items.add(new ExperimentSolverConfiguration(exp.getName(), sc.getName(), Util.getParameterString(SolverConfigurationDAO.getSolverConfigurationParameters(sc)), exp.getId(), sc.getId()));
                }
            }
            experimentSolverConfigTableModel.setItems(items);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: ERROR!
        }
        experimentSolverConfigRowFilter = new ExperimentSolverConfigurationRowFilter();
        ((TableRowSorter) tblExperimentSolverConfiguration.getRowSorter()).setRowFilter(experimentSolverConfigRowFilter);
        experimentTableModel.setExperiments(expController.getExperiments());
        tblExperiments.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                tblExperimentsSelectionModelValueChanged(e);
            }
        });

        pnlImport.setVisible(false);
        this.pack();
        this.txtExperimentName.requestFocus();
        this.getRootPane().setDefaultButton(this.btnCreateExperiment);
    }

    public EDACCExperimentModeNewExp(java.awt.Frame parent, boolean modal, String expName, String expDescription, ExperimentController expController) {
        this(parent, modal, expController);
        txtExperimentName.setText(expName);
        txtExperimentDescription.setText(expDescription);
        btnCreateExperiment.setText("Save");
        jScrollPaneExperiments.setVisible(false);
        lblImport.setVisible(false);
        chkImport.setVisible(false);
        this.pack();
        this.setTitle("Edit experiment");
    }

    /**
     * Returns a list of all selected solver configuration ids to import data from or the empty list if there isn't any.
     * @return
     */
    public ArrayList<Integer> getSelectedSolverConfigIds() {
        ArrayList<Integer> res = new ArrayList<Integer>();
        if (chkImport.isSelected()) {
            for (int i = 0; i < experimentSolverConfigTableModel.getRowCount(); i++) {
                ExperimentSolverConfiguration e = experimentSolverConfigTableModel.getItemAt(i);
                if (experimentSolverConfigRowFilter.contains(e)) {
                    res.add(e.solverConfigId);
                }
            }
        }
        return res;
    }

    public ArrayList<Boolean> getDuplicateListForSelectedSolverConfigs() {
        ArrayList<Boolean> res = new ArrayList<Boolean>();
        if (chkImport.isSelected()) {
            for (int i = 0; i < experimentSolverConfigTableModel.getRowCount(); i++) {
                ExperimentSolverConfiguration e = experimentSolverConfigTableModel.getItemAt(i);
                if (experimentSolverConfigRowFilter.contains(e)) {
                    res.add(e.duplicate);
                }
            }
        }
        return res;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblExperimentName = new javax.swing.JLabel();
        txtExperimentName = new javax.swing.JTextField();
        lblExperimentDescription = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtExperimentDescription = new javax.swing.JTextArea();
        btnCreateExperiment = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblImport = new javax.swing.JLabel();
        chkImport = new javax.swing.JCheckBox();
        pnlImport = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblExperimentSolverConfiguration = new javax.swing.JTable();
        splitExperimentsSolverConfigs = new javax.swing.JSplitPane();
        jScrollPaneExperiments = new javax.swing.JScrollPane();
        tblExperiments = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSolverConfigs = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCExperimentModeNewExp.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lblExperimentName.setText(resourceMap.getString("lblExperimentName.text")); // NOI18N
        lblExperimentName.setName("lblExperimentName"); // NOI18N

        txtExperimentName.setToolTipText(resourceMap.getString("txtExperimentName.toolTipText")); // NOI18N
        txtExperimentName.setName("txtExperimentName"); // NOI18N

        lblExperimentDescription.setText(resourceMap.getString("lblExperimentDescription.text")); // NOI18N
        lblExperimentDescription.setName("lblExperimentDescription"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txtExperimentDescription.setColumns(20);
        txtExperimentDescription.setRows(5);
        txtExperimentDescription.setToolTipText(resourceMap.getString("txtExperimentDescription.toolTipText")); // NOI18N
        txtExperimentDescription.setName("txtExperimentDescription"); // NOI18N
        jScrollPane1.setViewportView(txtExperimentDescription);

        btnCreateExperiment.setText(resourceMap.getString("btnCreateExperiment.text")); // NOI18N
        btnCreateExperiment.setToolTipText(resourceMap.getString("btnCreateExperiment.toolTipText")); // NOI18N
        btnCreateExperiment.setName("btnCreateExperiment"); // NOI18N
        btnCreateExperiment.setPreferredSize(new java.awt.Dimension(80, 25));
        btnCreateExperiment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateExperimentActionPerformed(evt);
            }
        });
        btnCreateExperiment.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnCreateExperimentKeyPressed(evt);
            }
        });

        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setToolTipText(resourceMap.getString("btnCancel.toolTipText")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.setPreferredSize(new java.awt.Dimension(80, 25));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        btnCancel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnCancelKeyPressed(evt);
            }
        });

        lblImport.setText(resourceMap.getString("lblImport.text")); // NOI18N
        lblImport.setName("lblImport"); // NOI18N

        chkImport.setText(resourceMap.getString("chkImport.text")); // NOI18N
        chkImport.setName("chkImport"); // NOI18N
        chkImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkImportActionPerformed(evt);
            }
        });

        pnlImport.setName("pnlImport"); // NOI18N
        pnlImport.setPreferredSize(new java.awt.Dimension(0, 0));

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        tblExperimentSolverConfiguration.setModel(experimentSolverConfigTableModel);
        tblExperimentSolverConfiguration.setName("tblExperimentSolverConfiguration"); // NOI18N
        tblExperimentSolverConfiguration.setRowSelectionAllowed(false);
        tblExperimentSolverConfiguration.setRowSorter(new TableRowSorter<ExperimentSolverConfigurationTableModel>(experimentSolverConfigTableModel));
        jScrollPane3.setViewportView(tblExperimentSolverConfiguration);

        splitExperimentsSolverConfigs.setDividerLocation(350);
        splitExperimentsSolverConfigs.setName("splitExperimentsSolverConfigs"); // NOI18N
        splitExperimentsSolverConfigs.setPreferredSize(new java.awt.Dimension(0, 404));

        jScrollPaneExperiments.setName("jScrollPaneExperiments"); // NOI18N

        tblExperiments.setModel(experimentTableModel);
        tblExperiments.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        tblExperiments.setName("tblExperiments"); // NOI18N
        tblExperiments.setRowSorter(new TableRowSorter<ExperimentTableModel>(experimentTableModel));
        tblExperiments.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblExperiments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblExperimentsMouseReleased(evt);
            }
        });
        tblExperiments.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tblExperimentsPropertyChange(evt);
            }
        });
        jScrollPaneExperiments.setViewportView(tblExperiments);

        splitExperimentsSolverConfigs.setLeftComponent(jScrollPaneExperiments);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblSolverConfigs.setModel(solverConfigTableModel);
        tblSolverConfigs.setName("tblSolverConfigs"); // NOI18N
        tblSolverConfigs.setRowSelectionAllowed(false);
        tblSolverConfigs.setRowSorter(new TableRowSorter<SolverConfigurationTableModel>(solverConfigTableModel));
        tblSolverConfigs.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tblSolverConfigsPropertyChange(evt);
            }
        });
        jScrollPane2.setViewportView(tblSolverConfigs);

        splitExperimentsSolverConfigs.setRightComponent(jScrollPane2);

        javax.swing.GroupLayout pnlImportLayout = new javax.swing.GroupLayout(pnlImport);
        pnlImport.setLayout(pnlImportLayout);
        pnlImportLayout.setHorizontalGroup(
            pnlImportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
            .addComponent(splitExperimentsSolverConfigs, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
        );
        pnlImportLayout.setVerticalGroup(
            pnlImportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImportLayout.createSequentialGroup()
                .addComponent(splitExperimentsSolverConfigs, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnCreateExperiment, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblExperimentDescription, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblExperimentName))
                            .addComponent(lblImport))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
                            .addComponent(txtExperimentName, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
                            .addComponent(chkImport)
                            .addComponent(pnlImport, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblExperimentName)
                    .addComponent(txtExperimentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblExperimentDescription)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkImport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlImport, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCreateExperiment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lblImport))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateExperimentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateExperimentActionPerformed
        this.expName = this.txtExperimentName.getText();
        this.expDesc = this.txtExperimentDescription.getText();
        this.canceled = false;
        this.setVisible(false);
    }//GEN-LAST:event_btnCreateExperimentActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.canceled = true;
        this.txtExperimentDescription.setText("");
        this.txtExperimentName.setText("");
        this.setVisible(false);
}//GEN-LAST:event_btnCancelActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        this.expName = "";
        this.expDesc = "";
    }//GEN-LAST:event_formWindowActivated

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        this.canceled = true;
    }//GEN-LAST:event_formWindowClosed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.canceled = true;
    }//GEN-LAST:event_formWindowClosing

    private void btnCreateExperimentKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnCreateExperimentKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) //create Experiment
        {
            this.btnCreateExperimentActionPerformed(null);
        }
    }//GEN-LAST:event_btnCreateExperimentKeyPressed

    private void btnCancelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnCancelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) //create Experiment
        {
            this.btnCancelActionPerformed(null);
        }
    }//GEN-LAST:event_btnCancelKeyPressed

    private void chkImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkImportActionPerformed
        int div = splitExperimentsSolverConfigs.getDividerLocation();
        pnlImport.setVisible(chkImport.isSelected());
        this.pack();
        splitExperimentsSolverConfigs.setDividerLocation(.5);
    }//GEN-LAST:event_chkImportActionPerformed

    private void tblExperimentsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblExperimentsMouseReleased
    }//GEN-LAST:event_tblExperimentsMouseReleased

    private void tblExperimentsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tblExperimentsPropertyChange
    }//GEN-LAST:event_tblExperimentsPropertyChange

    public void tblExperimentsSelectionModelValueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            try {
                int sel = -1;
                if ((sel = tblExperiments.convertRowIndexToModel(tblExperiments.getSelectedRow())) > -1) {
                    Experiment exp = experimentTableModel.getExperimentAt(sel);
                    ArrayList<SolverConfiguration> sc = SolverConfigurationDAO.getSolverConfigurationByExperimentId(exp.getId());
                    solverConfigTableModel.setSolverConfigurations(sc);
                    for (int i = 0; i < solverConfigTableModel.getRowCount(); i++) {
                        if (experimentSolverConfigRowFilter.contains(new ExperimentSolverConfiguration(null, null, null, exp.getId(), solverConfigTableModel.getSolverConfigurationAt(i).getId()))) {
                            solverConfigTableModel.setValueAt(true, i, SolverConfigurationTableModel.COL_SEL);
                        }
                    }
                    solverConfigTableModel.fireTableDataChanged();
                    currentExperiment = exp;
                }
            } catch (Exception ex) {
                // TODO ERROR!!
            }
        }
    }

    private void tblSolverConfigsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tblSolverConfigsPropertyChange
        if ("tableCellEditor".equals(evt.getPropertyName())) {
            for (int i = 0; i < solverConfigTableModel.getRowCount(); i++) {
                SolverConfiguration sc = solverConfigTableModel.getSolverConfigurationAt(i);
                if ((Boolean) solverConfigTableModel.getValueAt(i, SolverConfigurationTableModel.COL_SEL)) {
                    experimentSolverConfigRowFilter.addItem(new ExperimentSolverConfiguration(null, null, null, currentExperiment.getId(), sc.getId()));
                } else {
                    experimentSolverConfigRowFilter.removeItem(new ExperimentSolverConfiguration(null, null, null, currentExperiment.getId(), sc.getId()));
                }
            }
            experimentSolverConfigTableModel.fireTableDataChanged();
        }
    }//GEN-LAST:event_tblSolverConfigsPropertyChange
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCreateExperiment;
    private javax.swing.JCheckBox chkImport;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPaneExperiments;
    private javax.swing.JLabel lblExperimentDescription;
    private javax.swing.JLabel lblExperimentName;
    private javax.swing.JLabel lblImport;
    private javax.swing.JPanel pnlImport;
    private javax.swing.JSplitPane splitExperimentsSolverConfigs;
    private javax.swing.JTable tblExperimentSolverConfiguration;
    private javax.swing.JTable tblExperiments;
    private javax.swing.JTable tblSolverConfigs;
    private javax.swing.JTextArea txtExperimentDescription;
    private javax.swing.JTextField txtExperimentName;
    // End of variables declaration//GEN-END:variables

    class ExperimentSolverConfigurationTableModel extends DefaultTableModel {

        private final int COL_EXPERIMENT = 0;
        private final int COL_SOLVER = 1;
        private final int COL_PARAMS = 2;
        private final int COL_DUPLICATE = 3;
        private final String[] columns = {"Experiment", "Solver", "Parameters", "Duplicate"};
        private ArrayList<ExperimentSolverConfiguration> items;

        public void setItems(ArrayList<ExperimentSolverConfiguration> items) {
            this.items = items;
            this.fireTableDataChanged();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return getRowCount() == 0 ? String.class : getValueAt(0, columnIndex).getClass();
        }

        @Override
        public int getRowCount() {
            return items == null ? 0 : items.size();
        }

        @Override
        public Object getValueAt(int row, int column) {
            switch (column) {
                case COL_EXPERIMENT:
                    return items.get(row).experiment;
                case COL_SOLVER:
                    return items.get(row).solver;
                case COL_PARAMS:
                    return items.get(row).params;
                case COL_DUPLICATE:
                    return items.get(row).duplicate;
                default:
                    return "";
            }
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            if (column == COL_DUPLICATE) {
                items.get(row).duplicate = (Boolean) aValue;
            }
        }

        public ExperimentSolverConfiguration getItemAt(int row) {
            return items.get(row);
        }
    }

    class ExperimentSolverConfigurationRowFilter extends RowFilter<ExperimentSolverConfigurationTableModel, Integer> {

        private HashSet<ExperimentSolverConfiguration> items;

        public ExperimentSolverConfigurationRowFilter() {
            super();
            items = new HashSet<ExperimentSolverConfiguration>();
        }

        @Override
        public boolean include(Entry<? extends ExperimentSolverConfigurationTableModel, ? extends Integer> entry) {
            return items.contains(experimentSolverConfigTableModel.getItemAt(entry.getIdentifier()));
        }

        public void addItem(ExperimentSolverConfiguration item) {
            items.add(item);
        }

        public void removeItem(ExperimentSolverConfiguration item) {
            items.remove(item);
        }

        public void clear() {
            items.clear();
        }

        public boolean contains(ExperimentSolverConfiguration item) {
            return items.contains(item);
        }
    }

    class ExperimentSolverConfiguration {

        public String experiment;
        public String solver;
        public String params;
        public int experimentId;
        public int solverConfigId;
        public boolean duplicate;

        public ExperimentSolverConfiguration(String experiment, String solver, String params, int experimentId, int solverConfigId) {
            this.experiment = experiment;
            this.solver = solver;
            this.params = params;
            this.experimentId = experimentId;
            this.solverConfigId = solverConfigId;
            duplicate = false;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ExperimentSolverConfiguration other = (ExperimentSolverConfiguration) obj;
            if (this.experimentId != other.experimentId) {
                return false;
            }
            if (this.solverConfigId != other.solverConfigId) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + this.experimentId;
            hash = 59 * hash + this.solverConfigId;
            return hash;
        }
    }
}
