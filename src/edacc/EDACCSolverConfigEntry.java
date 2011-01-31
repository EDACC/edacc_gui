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
import edacc.model.SolverConfiguration;
import edacc.model.SolverConfigurationDAO;
import java.awt.Component;
import java.awt.Graphics;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTable;
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
    private SolverConfiguration solverConfiguration;
    private Solver solver;
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

        parameterTable.setDefaultRenderer(String.class, renderer);
        parameterTable.setDefaultRenderer(Integer.class, renderer);
        updateTableColumnWidth = true;
    }

    /**
     * Creates a new form EDACCSolverConfigEntry. Uses a solver configuration
     * to fill the parameter table.
     * @param solverConfiguration
     * @throws SQLException
     */
    public EDACCSolverConfigEntry(SolverConfiguration solverConfiguration) throws SQLException {
        this(solverConfiguration.getSolver_id());
        this.solverConfiguration = solverConfiguration;
        solverConfigEntryTableModel.setParameterInstances(SolverConfigurationDAO.getSolverConfigurationParameters(solverConfiguration));
        txtSeedGroup.setText(String.valueOf(solverConfiguration.getSeed_group()));
        border.setTitle(solverConfiguration.getName());
    }

    /**
     * Creates a new form EDACCSolverConfigEntry. Uses the solver to fill
     * the parameter table with the standard values.
     * @param solver
     * @throws SQLException
     */
    public EDACCSolverConfigEntry(Solver solver, int num) throws SQLException {
        this(solver.getId());
        this.solver = solver;
        if (num > 1) {
            border.setTitle(solver.getName() + " (" + num + ")");
        } else {
            border.setTitle(solver.getName());
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (updateTableColumnWidth) {
            edacc.experiment.Util.updateTableColumnWidth(parameterTable);
            updateTableColumnWidth = false;
        }
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
            return solverConfiguration.getSolver_id();
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

        setName("Form"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getActionMap(EDACCSolverConfigEntry.class, this);
        btnReplicate.setAction(actionMap.get("btnReplicate")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCSolverConfigEntry.class);
        btnReplicate.setText(resourceMap.getString("btnReplicate.text")); // NOI18N
        btnReplicate.setToolTipText(resourceMap.getString("btnReplicate.toolTipText")); // NOI18N
        btnReplicate.setActionCommand(resourceMap.getString("btnReplicate.actionCommand")); // NOI18N
        btnReplicate.setName("btnReplicate"); // NOI18N

        btnRemove.setAction(actionMap.get("btnRemove")); // NOI18N
        btnRemove.setText(resourceMap.getString("btnRemove.text")); // NOI18N
        btnRemove.setToolTipText(resourceMap.getString("btnRemove.toolTipText")); // NOI18N
        btnRemove.setName("btnRemove"); // NOI18N
        btnRemove.setPreferredSize(new java.awt.Dimension(81, 23));

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
        btnEditName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnReplicate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSeedGroup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSeedGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                        .addComponent(btnEditName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReplicate)
                    .addComponent(lblSeedGroup)
                    .addComponent(txtSeedGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditName))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnReplicate;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblSeedGroup;
    private javax.swing.JTable parameterTable;
    private javax.swing.JTextField txtSeedGroup;
    // End of variables declaration//GEN-END:variables

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

        if (solverConfiguration == null || solverConfiguration.getSeed_group() != seedGroup || (idx != -1 && (!border.getTitle().equals(solverConfiguration.getName()) || solverConfiguration.getIdx() != idx))) {
            return true;
        }
        return solverConfigEntryTableModel.isModified();
    }
}
