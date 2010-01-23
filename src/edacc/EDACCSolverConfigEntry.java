/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
import edacc.model.SolverDAO;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.border.TitledBorder;
import org.jdesktop.application.Action;

/**
 *
 * @author simon
 */
public class EDACCSolverConfigEntry extends javax.swing.JPanel {

    private SolverConfigEntryTableModel solverConfigEntryTableModel;
    private String title;
    private TitledBorder border;
    private SolverConfiguration solverConfiguration;
    private Solver solver;
    private EDACCSolverConfigPanel parent;

    /**
     * Creates a new form EDACCSolverConfigEntry. Uses a solver configuration
     * to fill the parameter table.
     * @param solverConfiguration
     * @throws SQLException
     */
    public EDACCSolverConfigEntry(SolverConfiguration solverConfiguration) throws SQLException {
        this(SolverDAO.getById(solverConfiguration.getSolver_id()));
        this.solverConfiguration = solverConfiguration;
        solverConfigEntryTableModel.setParameterInstances(SolverConfigurationDAO.getSolverConfigurationParameters(solverConfiguration));
    }

    /**
     * Creates a new form EDACCSolverConfigEntry. Uses the solver to fill
     * the parameter table with the standard values.
     * @param solver
     * @throws SQLException
     */
    public EDACCSolverConfigEntry(Solver solver) throws SQLException {
        solverConfigEntryTableModel = new SolverConfigEntryTableModel();
        initComponents();
        this.solver = solver;
        this.title = solver.getName();
        this.border = new TitledBorder(title);
        this.setBorder(border);
        solverConfigEntryTableModel.setParameters(ParameterDAO.getParameterFromSolverId(solver.getId()));
        this.solverConfiguration = null;
    }

    public void setTitleNumber(int number) {
        if (number == 1) {
            border.setTitle(title);
        } else {
            border.setTitle(title + " (" + number + ")");
        }
    }
    
    /**
     * Assigns all parameter values/selections from entry.
     * @param entry
     */
    public void assign(EDACCSolverConfigEntry entry) {
        for (int i = 0; i < entry.solverConfigEntryTableModel.getRowCount(); i++) {
            solverConfigEntryTableModel.setValueAt(entry.solverConfigEntryTableModel.getValueAt(i, 2), i, 2);
            solverConfigEntryTableModel.setValueAt(entry.solverConfigEntryTableModel.getValueAt(i, 4), i, 4);
        }
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

    public void setSolverConfiguration(SolverConfiguration solverConfiguration) {
        this.solverConfiguration = solverConfiguration;
    }

    public void setParent(EDACCSolverConfigPanel parent) {
        this.parent = parent;
    }

    /**
     * Saves all new and modified parameter instances to the database.
     * @throws SQLException
     */
    public void saveParameterInstances() throws SQLException {
        Vector<ParameterInstance> parameterVector = new Vector<ParameterInstance>();
        for (int i = 0; i < solverConfigEntryTableModel.getRowCount(); i++) {
            if ((Boolean) solverConfigEntryTableModel.getValueAt(i, 4)) {
                Parameter p = (Parameter) solverConfigEntryTableModel.getValueAt(i, 5);
                ParameterInstance pi = (ParameterInstance) solverConfigEntryTableModel.getValueAt(i, 6);
                if (pi == null) {
                    pi = ParameterInstanceDAO.createParameterInstance(p.getId(), solverConfiguration.getId(), (String) solverConfigEntryTableModel.getValueAt(i, 2));
                    parameterVector.add(pi);
                }
                if (!pi.getValue().equals((String) solverConfigEntryTableModel.getValueAt(i, 2))) {
                    pi.setValue((String) solverConfigEntryTableModel.getValueAt(i, 2));
                    ParameterInstanceDAO.setModified(pi);
                    ParameterInstanceDAO.save(pi);
                }
            }
        }
        if (parameterVector.size() > 0)
            solverConfigEntryTableModel.setParameterInstances(parameterVector);
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

        setName("Form"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getActionMap(EDACCSolverConfigEntry.class, this);
        btnReplicate.setAction(actionMap.get("btnReplicate")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCSolverConfigEntry.class);
        btnReplicate.setText(resourceMap.getString("btnReplicate.text")); // NOI18N
        btnReplicate.setActionCommand(resourceMap.getString("btnReplicate.actionCommand")); // NOI18N
        btnReplicate.setName("btnReplicate"); // NOI18N

        btnRemove.setAction(actionMap.get("btnRemove")); // NOI18N
        btnRemove.setText(resourceMap.getString("btnRemove.text")); // NOI18N
        btnRemove.setName("btnRemove"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        parameterTable.setModel(solverConfigEntryTableModel);
        parameterTable.setName("solverParameters"); // NOI18N
        jScrollPane2.setViewportView(parameterTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnReplicate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 323, Short.MAX_VALUE)
                        .addComponent(btnRemove))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRemove)
                    .addComponent(btnReplicate))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void btnReplicate() {
        try {
            parent.replicateEntry(this);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Action
    public void btnRemove() {
        parent.removeEntry(this);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnReplicate;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable parameterTable;
    // End of variables declaration//GEN-END:variables
}
