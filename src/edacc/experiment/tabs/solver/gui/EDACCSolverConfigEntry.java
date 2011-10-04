/*
 * EDACCSolverConfigEntry.java
 *
 * Created on 30.12.2009, 20:11:16
 */
package edacc.experiment.tabs.solver.gui;

import edacc.EDACCApp;
import edacc.JTableTooltipInformation;
import edacc.experiment.tabs.solver.EDACCSolverConfigEntryListener;
import edacc.experiment.tabs.solver.SolverConfigurationEntry;
import edacc.model.SolverBinaries;
import edacc.model.SolverConfiguration;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.sql.SQLException;
import java.util.LinkedList;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import org.jdesktop.application.Action;

/**
 * A simple JPanel which represents a Solver Configuration in the GUI.
 * @author simon
 * @see edacc.EDACCSolverConfigPanel
 */
public class EDACCSolverConfigEntry extends javax.swing.JPanel {

    private TitledBorder border;
    private boolean updateTableColumnWidth;
    private SolverConfigurationEntry model;
    private EDACCSolverConfigEntryListener listener;

    protected EDACCSolverConfigEntry(final SolverConfigurationEntry model, EDACCSolverConfigEntryListener listener) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null.");
        }
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null.");
        }
        
        this.model = model;
        initComponents();
        if (!model.getExperiment().isConfigurationExp()) {
            jLabel2.setVisible(false);
            txtHint.setVisible(false);
        }
        this.border = new TitledBorder("");
        this.setBorder(border);

        for (SolverBinaries sb : model.getSolver().getSolverBinaries()) {
            comboSolverBinaries.addItem(sb);
        }

        parameterTable.setRowSelectionAllowed(false);
        parameterTable.setCellSelectionEnabled(false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JLabel lbl = (JLabel) comp;
                lbl.setEnabled((Boolean) model.getTableModel().getValueAt(row, 0));
                return comp;
            }
        };

        DefaultTableCellRenderer booleanRenderer = new DefaultTableCellRenderer() {

            protected JCheckBox checkBox;

            {
                checkBox = new JCheckBox();
                checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                checkBox.setBackground(Color.white);
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                checkBox.setBackground(comp.getBackground());
                checkBox.setEnabled(!model.getTableModel().getParameters().get(row).isMandatory());
                checkBox.setSelected((Boolean) value);
                return checkBox;
            }
        };

        parameterTable.setDefaultRenderer(String.class, renderer);
        parameterTable.setDefaultRenderer(Integer.class, renderer);
        parameterTable.setDefaultRenderer(Boolean.class, booleanRenderer);

        updateTableColumnWidth = true;

        model.getTableModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                fireParametersChanged();
            }
        });
        update();
        this.listener = listener;
    }
    
    public void update() {
        txtSeedGroup.setText("" + model.getSeedGroup());
        txtHint.setText(model.getHint());
        border.setTitle(model.getName());
        comboSolverBinaries.setSelectedItem(model.getSolverBinary());
    }

    /**
     * Creates a new form EDACCSolverConfigEntry. Uses the solver to fill
     * the parameter table with the standard values.
     * @param solver
     * @throws SQLException
     */
    /* public EDACCSolverConfigEntry(Solver solver, int num, Experiment experiment) throws SQLException {
    this(solver, num > 1 ? solver.getName() + " (" + num + ")" : solver.getName(), experiment);
    }*/
    public SolverBinaries getSolverBinary() {
        return (SolverBinaries) comboSolverBinaries.getSelectedItem();
    }

    public SolverConfigurationEntry getModel() {
        return model;
    }

    @Override
    public void paint(Graphics g) {
        if (updateTableColumnWidth) {
            edacc.experiment.Util.updateTableColumnWidth(parameterTable);
            updateTableColumnWidth = false;
        }
        super.paint(g);
    }

    public String getTitle() {
        return border.getTitle();
    }

    public int getSolverId() {
        return model.getSolver().getId();
    }

    public SolverConfiguration getSolverConfiguration() {
        return model.getSolverConfig();
    }

    public javax.swing.JTextField getSeedGroup() {
        return txtSeedGroup;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnReplicate = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        parameterTable = parameterTable = new JTableTooltipInformation();
        lblSeedGroup = new javax.swing.JLabel();
        txtSeedGroup = new javax.swing.JTextField();
        btnEditName = new javax.swing.JButton();
        btnMassReplication = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        comboSolverBinaries = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        txtHint = new javax.swing.JTextField();

        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCSolverConfigEntry.class);
        btnReplicate.setText(resourceMap.getString("btnReplicate.text")); // NOI18N
        btnReplicate.setToolTipText(resourceMap.getString("btnReplicate.toolTipText")); // NOI18N
        btnReplicate.setActionCommand(resourceMap.getString("btnReplicate.actionCommand")); // NOI18N
        btnReplicate.setName("btnReplicate"); // NOI18N
        btnReplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReplicateActionPerformed(evt);
            }
        });

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getActionMap(EDACCSolverConfigEntry.class, this);
        btnRemove.setAction(actionMap.get("btnRemove")); // NOI18N
        btnRemove.setText(resourceMap.getString("btnRemove.text")); // NOI18N
        btnRemove.setToolTipText(resourceMap.getString("btnRemove.toolTipText")); // NOI18N
        btnRemove.setMaximumSize(new java.awt.Dimension(81, 23));
        btnRemove.setMinimumSize(new java.awt.Dimension(81, 23));
        btnRemove.setName("btnRemove"); // NOI18N
        btnRemove.setPreferredSize(new java.awt.Dimension(90, 23));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        parameterTable.setModel(model.getTableModel());
        parameterTable.setName("solverParameters"); // NOI18N
        jScrollPane2.setViewportView(parameterTable);

        lblSeedGroup.setText(resourceMap.getString("lblSeedGroup.text")); // NOI18N
        lblSeedGroup.setName("lblSeedGroup"); // NOI18N

        txtSeedGroup.setText(resourceMap.getString("txtSeedGroup.text")); // NOI18N
        txtSeedGroup.setToolTipText(resourceMap.getString("txtSeedGroup.toolTipText")); // NOI18N
        txtSeedGroup.setName("txtSeedGroup"); // NOI18N
        txtSeedGroup.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSeedGroupFocusLost(evt);
            }
        });
        txtSeedGroup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSeedGroupKeyReleased(evt);
            }
        });

        btnEditName.setText(resourceMap.getString("btnEditName.text")); // NOI18N
        btnEditName.setName("btnEditName"); // NOI18N
        btnEditName.setPreferredSize(new java.awt.Dimension(90, 23));
        btnEditName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditNameActionPerformed(evt);
            }
        });

        btnMassReplication.setText(resourceMap.getString("btnMassReplication.text")); // NOI18N
        btnMassReplication.setName("btnMassReplication"); // NOI18N
        btnMassReplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMassReplicationActionPerformed(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        comboSolverBinaries.setName("comboSolverBinaries"); // NOI18N
        comboSolverBinaries.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboSolverBinariesActionPerformed(evt);
            }
        });

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtHint.setText(resourceMap.getString("txtHint.text")); // NOI18N
        txtHint.setName("txtHint"); // NOI18N
        txtHint.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtHintFocusLost(evt);
            }
        });
        txtHint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtHintKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnReplicate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSeedGroup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSeedGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMassReplication)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                        .addComponent(btnEditName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtHint, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                            .addComponent(comboSolverBinaries, 0, 437, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboSolverBinaries, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtHint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReplicate)
                    .addComponent(lblSeedGroup)
                    .addComponent(txtSeedGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMassReplication)
                    .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtSeedGroupKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSeedGroupKeyReleased
        String res = "";
        String tmp = txtSeedGroup.getText();
        for (int i = 0; i < tmp.length(); i++) {
            if (tmp.charAt(i) >= '0' && tmp.charAt(i) <= '9') {
                res += tmp.charAt(i);
            }
        }
        txtSeedGroup.setText(res);
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtSeedGroup.transferFocus();
        }

    }//GEN-LAST:event_txtSeedGroupKeyReleased

    private void txtSeedGroupFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSeedGroupFocusLost
        int oldSeedGroup = model.getSeedGroup();
        try {
            model.setSeedGroup(Integer.parseInt(txtSeedGroup.getText()));
            fireSeedGroupChanged(oldSeedGroup, model.getSeedGroup());
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_txtSeedGroupFocusLost

    private void btnEditNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditNameActionPerformed
        EDACCSolverConfigEntryEditName editNameDialog = new EDACCSolverConfigEntryEditName(EDACCApp.getApplication().getMainFrame(), true, border.getTitle());
        EDACCApp.getApplication().show(editNameDialog);
        String newName = editNameDialog.getNameText();
        String oldName = model.getName();
        border.setTitle(newName);
        model.setName(newName);
        this.invalidate();
        this.revalidate();
        this.repaint();
        fireNameChanged(oldName, newName);
    }//GEN-LAST:event_btnEditNameActionPerformed

    private void btnMassReplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMassReplicationActionPerformed
        // TODO: fix!
        /*  EDACCSolverConfigReplicateUsingFiles replicator = new EDACCSolverConfigReplicateUsingFiles(EDACCApp.getApplication().getMainFrame(), true, this);
        replicator.setLocationRelativeTo(EDACCApp.getApplication().getMainFrame());
        EDACCApp.getApplication().show(replicator);
        File dir;
        if ((dir = replicator.getChosenFolder()) != null) {
        File[] files = dir.listFiles();
        SolverConfigEntryTableModel model = replicator.getModel();
        Pattern[] patterns = new Pattern[model.getRowCount()];
        String[] values = new String[model.getRowCount()];
        for (int i = 0; i < model.getRowCount(); i++) {
        if ((Boolean) model.getValueAt(i, 0)) {
        // is selected
        if ((Boolean) model.getValueAt(i, 6)) {
        // is regex
        patterns[i] = Pattern.compile((String) model.getValueAt(i, 3));
        }
        }
        }
        for (File file : files) {
        if (!file.isFile()) {
        continue;
        }
        for (int i = 0; i < model.getRowCount(); i++) {
        if ((Boolean) model.getValueAt(i, 0)) {
        if (!(Boolean) model.getValueAt(i, 6)) {
        values[i] = (String) model.getValueAt(i, 3);
        } else {
        values[i] = null;
        }
        }
        }
        BufferedReader input = null;
        
        try {
        input = new BufferedReader(new FileReader(file));
        String line;
        while ((line = input.readLine()) != null) {
        for (int i = 0; i < model.getRowCount(); i++) {
        if (patterns[i] != null) {
        Matcher m = patterns[i].matcher(line);
        if (m.matches()) {
        if (m.groupCount() > 0) {
        if (values[i] != null || m.groupCount() > 1) {
        // TODO: WARNING
        } else {
        values[i] = m.group(1);
        }
        }
        }
        }
        }
        }
        
        if (solver == null) {
        solver = SolverDAO.getById(this.getSolverId());
        }
        EDACCSolverConfigEntry entry = new EDACCSolverConfigEntry(solver, ((TitledBorder) this.getBorder()).getTitle() + "-" + file.getName(), experiment);
        entry.txtSeedGroup.setText(getSeedGroup().getText());
        for (int i = 0; i < model.getRowCount(); i++) {
        if (values[i] != null) {
        entry.solverConfigEntryTableModel.setValueAt(values[i], i, 3);
        entry.solverConfigEntryTableModel.setValueAt(true, i, 0);
        }
        }
        entry.setParent(parent);
        parent.addEntryAfterEntry(entry, this);
        } catch (FileNotFoundException ex) {
        // TODO: error
        } catch (IOException ex) {
        // TODO: error
        } catch (SQLException ex) {
        // TODO: error
        } finally {
        if (input != null) {
        try {
        input.close();
        } catch (IOException ex) {
        }
        }
        }
        }
        }*/
    }//GEN-LAST:event_btnMassReplicationActionPerformed

    private void comboSolverBinariesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboSolverBinariesActionPerformed
        // we check if listener != null because when creating this object, this 
        // method will be called for every solver binary added to this combo box
        if (comboSolverBinaries.getSelectedItem() instanceof SolverBinaries && listener != null) {
            SolverBinaries oldSolverBinary = model.getSolverBinary();
            model.setSolverBinary((SolverBinaries) comboSolverBinaries.getSelectedItem());
            fireSolverBinaryChanged(oldSolverBinary, model.getSolverBinary());
        }
        
    }//GEN-LAST:event_comboSolverBinariesActionPerformed

    private void txtHintFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtHintFocusLost
        String oldHint = model.getHint();
        model.setHint(txtHint.getText());
        fireHintChanged(oldHint, model.getHint());
    }//GEN-LAST:event_txtHintFocusLost

    private void txtHintKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHintKeyReleased
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtHint.transferFocus();
        }
    }//GEN-LAST:event_txtHintKeyReleased

    private void btnReplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReplicateActionPerformed
        fireReplicateRequest();
    }//GEN-LAST:event_btnReplicateActionPerformed

    @Action
    public void btnRemove() {
        fireRemoveRequest();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditName;
    private javax.swing.JButton btnMassReplication;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnReplicate;
    private javax.swing.JComboBox comboSolverBinaries;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblSeedGroup;
    private javax.swing.JTable parameterTable;
    private javax.swing.JTextField txtHint;
    private javax.swing.JTextField txtSeedGroup;
    // End of variables declaration//GEN-END:variables

    public void removeButtons() {
        btnReplicate.setVisible(false);
        btnRemove.setVisible(false);
        btnMassReplication.setVisible(false);
        btnEditName.setVisible(false);
    }

    public String getHint() {
        return txtHint.getText();
    }

    public void fireNameChanged(String oldName, String newName) {
        listener.onNameChanged(this, oldName, newName);
    }

    public void fireHintChanged(String oldHint, String newHint) {
        listener.onHintChanged(this, oldHint, newHint);
    }

    public void fireSeedGroupChanged(int oldSeedGroup, int newSeedGroup) {
        listener.onSeedGroupChanged(this, oldSeedGroup, newSeedGroup);
    }

    public void fireParametersChanged() {
        listener.onParametersChanged(this);
    }
    
    public void fireSolverBinaryChanged(SolverBinaries oldSolverBinary, SolverBinaries newSolverBinary) {
        listener.onSolverBinaryChanged(this, oldSolverBinary, newSolverBinary);
    }
    
    public void fireReplicateRequest() {
        listener.onReplicateRequest(this);
    }
    
    public void fireRemoveRequest() {
        listener.onRemoveRequest(this);
    }
}
