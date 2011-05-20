/*
 * EDACCSolverConfigEntry.java
 *
 * Created on 30.12.2009, 20:11:16
 */
package edacc;

import edacc.experiment.SolverConfigEntryTableModel;
import edacc.model.Parameter;
import edacc.model.ParameterDAO;
import edacc.model.ParameterInstance;
import edacc.model.ParameterInstanceDAO;
import edacc.model.Solver;
import edacc.model.SolverBinaries;
import edacc.model.SolverBinariesDAO;
import edacc.model.SolverConfiguration;
import edacc.model.SolverConfigurationDAO;
import edacc.model.SolverDAO;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    private SolverConfigEntryTableModel solverConfigEntryTableModel;
    private TitledBorder border;
    protected SolverConfiguration solverConfiguration;
    protected Solver solver;
    private EDACCSolverConfigPanelSolver parent;
    private boolean updateTableColumnWidth;

    private EDACCSolverConfigEntry(int solverId) throws SQLException {
        solverConfigEntryTableModel = new SolverConfigEntryTableModel();
        initComponents();
        this.border = new TitledBorder("");
        this.setBorder(border);
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        params.addAll(ParameterDAO.getParameterFromSolverId(solverId));
        solverConfigEntryTableModel.setParameters(params);

        ArrayList<SolverBinaries> solverBinaries = new ArrayList<SolverBinaries>();
        solverBinaries.addAll(SolverBinariesDAO.getBinariesOfSolver(SolverDAO.getById(solverId)));
        for (SolverBinaries sb : solverBinaries) {
            comboSolverBinaries.addItem(sb);
        }

        solverConfigEntryTableModel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                if (parent != null) {
                    parent.setTitles();
                }
            }
        });
        parameterTable.setRowSelectionAllowed(false);
        parameterTable.setCellSelectionEnabled(false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JLabel lbl = (JLabel) comp;
                lbl.setEnabled((Boolean) solverConfigEntryTableModel.getValueAt(row, 0));
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
                checkBox.setEnabled(!solverConfigEntryTableModel.getParameters().get(row).isMandatory());
                checkBox.setSelected((Boolean) value);
                return checkBox;
            }
        };

        parameterTable.setDefaultRenderer(String.class, renderer);
        parameterTable.setDefaultRenderer(Integer.class, renderer);
        parameterTable.setDefaultRenderer(Boolean.class, booleanRenderer);
        updateTableColumnWidth = true;
    }

    /**
     * Creates a new form EDACCSolverConfigEntry. Uses a solver configuration
     * to fill the parameter table.
     * @param solverConfiguration
     * @throws SQLException
     */
    public EDACCSolverConfigEntry(SolverConfiguration solverConfiguration) throws SQLException {
        this(solverConfiguration.getSolverBinary().getIdSolver());
        this.solverConfiguration = solverConfiguration;
        
        solverConfigEntryTableModel.setParameterInstances(SolverConfigurationDAO.getSolverConfigurationParameters(solverConfiguration));
        txtSeedGroup.setText(String.valueOf(solverConfiguration.getSeed_group()));
        border.setTitle(solverConfiguration.getName());
        comboSolverBinaries.setSelectedItem(solverConfiguration.getSolverBinary());
    }

    /**
     * Creates a new form EDACCSolverConfigEntry. Uses the solver to fill
     * the parameter table with the standard values.
     * @param solver
     * @throws SQLException
     */
    public EDACCSolverConfigEntry(Solver solver, int num) throws SQLException {
        this(solver, num > 1 ? solver.getName() + " (" + num + ")" : solver.getName());
    }

    public SolverBinaries getSolverBinary() {
        return (SolverBinaries) comboSolverBinaries.getSelectedItem();
    }

    public EDACCSolverConfigEntry(Solver solver, String name) throws SQLException {
        this(solver.getId());
        this.solver = solver;
        border.setTitle(name);
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

    /**
     * Assigns all parameter values/selections from entry.
     * @param entry
     */
    public void assign(EDACCSolverConfigEntry entry) {
        txtSeedGroup.setText(entry.getSeedGroup().getText());
        for (int i = 0; i < entry.solverConfigEntryTableModel.getRowCount(); i++) {
            solverConfigEntryTableModel.setValueAt(entry.solverConfigEntryTableModel.getValueAt(i, 3), i, 3);
            solverConfigEntryTableModel.setValueAt(entry.solverConfigEntryTableModel.getValueAt(i, 0), i, 0);
        }
        updateTableColumnWidth = true;
    }

    public int getSolverId() {
        if (solver != null) {
            return solver.getId();
        }
        if (solverConfiguration != null) {
            return solverConfiguration.getSolverBinary().getIdSolver();
        }
        return -1;
    }

    public SolverConfiguration getSolverConfiguration() {
        return solverConfiguration;
    }

    /*
     * Updates the solver configuration if there is currently no solver configuration
     * specified and sets its name.
     */
    public void setSolverConfiguration(SolverConfiguration solverConfiguration) {
        if (this.solverConfiguration == null) {
            this.solverConfiguration = solverConfiguration;
            solverConfiguration.setName(border.getTitle());
        }
    }

    public void setParent(EDACCSolverConfigPanelSolver parent) {
        this.parent = parent;
    }

    public javax.swing.JTextField getSeedGroup() {
        return txtSeedGroup;
    }

    /**
     * Saves all new and modified parameter instances to the database.
     * @throws SQLException
     */
    public void saveParameterInstances() throws SQLException {
        ArrayList<ParameterInstance> parameterVector = new ArrayList<ParameterInstance>();
        for (int i = 0; i < solverConfigEntryTableModel.getRowCount(); i++) {
            if ((Boolean) solverConfigEntryTableModel.getValueAt(i, 0)) {
                Parameter p = (Parameter) solverConfigEntryTableModel.getValueAt(i, 5);
                ParameterInstance pi = (ParameterInstance) solverConfigEntryTableModel.getValueAt(i, 6);
                if (pi == null) {
                    pi = ParameterInstanceDAO.createParameterInstance(p.getId(), solverConfiguration.getId(), (String) solverConfigEntryTableModel.getValueAt(i, 2));
                    parameterVector.add(pi);
                }
                if (!pi.getValue().equals((String) solverConfigEntryTableModel.getValueAt(i, 3))) {
                    pi.setValue((String) solverConfigEntryTableModel.getValueAt(i, 3));
                    ParameterInstanceDAO.setModified(pi);
                    ParameterInstanceDAO.save(pi);
                }
            } else {
                ParameterInstance pi = (ParameterInstance) solverConfigEntryTableModel.getValueAt(i, 6);
                if (pi != null) {
                    ParameterInstanceDAO.setDeleted(pi);
                    ParameterInstanceDAO.save(pi);
                    solverConfigEntryTableModel.removeParameterInstance(pi);
                }
            }
        }
        if (parameterVector.size() > 0) {
            solverConfigEntryTableModel.setParameterInstances(parameterVector);
        }
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
        parameterTable = new javax.swing.JTable();
        lblSeedGroup = new javax.swing.JLabel();
        txtSeedGroup = new javax.swing.JTextField();
        btnEditName = new javax.swing.JButton();
        btnMassReplication = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        comboSolverBinaries = new javax.swing.JComboBox();

        setName("Form"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getActionMap(EDACCSolverConfigEntry.class, this);
        btnReplicate.setAction(actionMap.get("btnReplicate")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCSolverConfigEntry.class);
        btnReplicate.setText(resourceMap.getString("btnReplicate.text")); // NOI18N
        btnReplicate.setToolTipText(resourceMap.getString("btnReplicate.toolTipText")); // NOI18N
        btnReplicate.setActionCommand(resourceMap.getString("btnReplicate.actionCommand")); // NOI18N
        btnReplicate.setName("btnReplicate"); // NOI18N
        btnReplicate.setPreferredSize(new java.awt.Dimension(77, 25));

        btnRemove.setAction(actionMap.get("btnRemove")); // NOI18N
        btnRemove.setText(resourceMap.getString("btnRemove.text")); // NOI18N
        btnRemove.setToolTipText(resourceMap.getString("btnRemove.toolTipText")); // NOI18N
        btnRemove.setMaximumSize(new java.awt.Dimension(81, 23));
        btnRemove.setMinimumSize(new java.awt.Dimension(81, 23));
        btnRemove.setName("btnRemove"); // NOI18N
        btnRemove.setPreferredSize(new java.awt.Dimension(95, 25));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        parameterTable.setModel(solverConfigEntryTableModel);
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
        btnEditName.setPreferredSize(new java.awt.Dimension(95, 25));
        btnEditName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditNameActionPerformed(evt);
            }
        });

        btnMassReplication.setText(resourceMap.getString("btnMassReplication.text")); // NOI18N
        btnMassReplication.setName("btnMassReplication"); // NOI18N
        btnMassReplication.setPreferredSize(new java.awt.Dimension(111, 25));
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnReplicate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSeedGroup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSeedGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMassReplication, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEditName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboSolverBinaries, 0, 437, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboSolverBinaries, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReplicate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSeedGroup)
                    .addComponent(txtSeedGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMassReplication, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        parent.setTitles();
    }//GEN-LAST:event_txtSeedGroupFocusLost

    private void btnEditNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditNameActionPerformed
        EDACCSolverConfigEntryEditName editNameDialog = new EDACCSolverConfigEntryEditName(EDACCApp.getApplication().getMainFrame(), true, border.getTitle());
        EDACCApp.getApplication().show(editNameDialog);
        String newName = editNameDialog.getNameText();
        border.setTitle(newName);
        parent.setTitles();
    }//GEN-LAST:event_btnEditNameActionPerformed

    private void btnMassReplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMassReplicationActionPerformed
        EDACCSolverConfigReplicateUsingFiles replicator = new EDACCSolverConfigReplicateUsingFiles(EDACCApp.getApplication().getMainFrame(), true, this);
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
                    EDACCSolverConfigEntry entry = new EDACCSolverConfigEntry(solver, ((TitledBorder) this.getBorder()).getTitle() + "-" + file.getName());
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
        }
    }//GEN-LAST:event_btnMassReplicationActionPerformed

    private void comboSolverBinariesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboSolverBinariesActionPerformed
        if (parent != null)
            parent.setTitles();
    }//GEN-LAST:event_comboSolverBinariesActionPerformed
    @Action
    public void btnReplicate() {
        try {
            parent.replicateEntry(this);
        } catch (SQLException ex) {
        }
    }

    @Action
    public void btnRemove() {
        parent.removeEntry(this);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditName;
    private javax.swing.JButton btnMassReplication;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnReplicate;
    private javax.swing.JComboBox comboSolverBinaries;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblSeedGroup;
    private javax.swing.JTable parameterTable;
    private javax.swing.JTextField txtSeedGroup;
    // End of variables declaration//GEN-END:variables

    public ArrayList<Parameter> getParameters() {
        return solverConfigEntryTableModel.getParameters();
    }

    public ArrayList<ParameterInstance> getParameterInstances() {
        return solverConfigEntryTableModel.getParameterInstances();
    }

    public SolverConfigEntryTableModel getModel() {
        return solverConfigEntryTableModel;
    }

    public void removeButtons() {
        btnReplicate.setVisible(false);
        btnRemove.setVisible(false);
        btnMassReplication.setVisible(false);
        btnEditName.setVisible(false);
    }

    /**
     * Checks for unsaved data, i.e. checks iff the seed group, the parameter instances or the idx have been changed.<br/>
     * If the seed group is not a valid integer it will be substituted and used as 0.
     * @param idx the idx to check the equality. If <code>idx == -1</code> the idx and name of the solver configuration will not be checked
     * @return <code>true</code>, if and only if data is unsaved, false otherwise
     */
    public boolean isModified(int idx) {
        int seedGroup = 0;
        try {
            seedGroup = Integer.parseInt(txtSeedGroup.getText());
        } catch (NumberFormatException _) {
            txtSeedGroup.setText("0");
        }

        if (solverConfiguration == null || solverConfiguration.getSeed_group() != seedGroup || (idx != -1 && (!border.getTitle().equals(solverConfiguration.getName()) || solverConfiguration.getIdx() != idx)) || solverConfiguration.getSolverBinary() != this.getSolverBinary()) {
            return true;
        }
        return solverConfigEntryTableModel.isModified();
    }
    
    public boolean hasEmptyValues() {
        for (int i = 0; i < solverConfigEntryTableModel.getRowCount(); i++) {
            if ("instance".equals((String) solverConfigEntryTableModel.getValueAt(i, 1))
                    || "seed".equals((String) solverConfigEntryTableModel.getValueAt(i, 1))) {
                continue;
            }
            if ((Boolean) solverConfigEntryTableModel.getValueAt(i, 0)) {
                if (solverConfigEntryTableModel.getParameters().get(i).getHasValue()) {
                    if ("".equals(solverConfigEntryTableModel.getValueAt(i, 3))) {
                        return true;
                    }
                }
                    
            } 
        }
        return false;
    }
    
}
