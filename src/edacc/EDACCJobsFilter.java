package edacc;

import edacc.model.ExperimentResultStatus;
import java.util.ArrayList;
import org.jdesktop.application.Action;

/**
 *
 * @author simon
 */
public class EDACCJobsFilter extends javax.swing.JDialog {
    private EDACCExperimentMode main;
    /** Creates new form EDACCJobsFilter */
    public EDACCJobsFilter(java.awt.Frame parent, boolean modal, EDACCExperimentMode main) {
        super(parent, modal);
        initComponents();
        this.main = main;
        ArrayList<String> instances = main.jobsTableModel.getInstances();
        ArrayList<String> solvers = main.jobsTableModel.getSolvers();
        ArrayList<ExperimentResultStatus> statusCodes = main.jobsTableModel.getStatusEnums();
        comboSolvers.removeAllItems();
        comboInstances.removeAllItems();
        comboStatusCodes.removeAllItems();
        comboSolvers.addItem("All");
        comboInstances.addItem("All");
        comboStatusCodes.addItem("All");
        for (String val : instances) {
            comboInstances.addItem(val);
        }
        for (String val : solvers) {
            comboSolvers.addItem(val);
        }
        for (ExperimentResultStatus val : statusCodes) {
            comboStatusCodes.addItem(val);
        }
        String selectedInstance = main.resultBrowserRowFilter.getInstanceName() == null ? "All" : main.resultBrowserRowFilter.getInstanceName();
        String selectedSolver = main.resultBrowserRowFilter.getSolverName() == null ? "All" : main.resultBrowserRowFilter.getSolverName();
        ExperimentResultStatus selectedStatusCode = main.resultBrowserRowFilter.getStatus();
        comboInstances.setSelectedItem(selectedInstance);
        comboSolvers.setSelectedItem(selectedSolver);
        if (selectedStatusCode == null) {
            comboStatusCodes.setSelectedItem("All");
        } else {
            comboStatusCodes.setSelectedItem(selectedStatusCode);
        }
        pack();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        comboSolvers = new javax.swing.JComboBox();
        btnAccept = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        comboStatusCodes = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        comboInstances = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCJobsFilter.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setModal(true);
        setName("Form"); // NOI18N

        comboSolvers.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboSolvers.setToolTipText(resourceMap.getString("comboSolvers.toolTipText")); // NOI18N
        comboSolvers.setName("comboSolvers"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getActionMap(EDACCJobsFilter.class, this);
        btnAccept.setAction(actionMap.get("btnAccept")); // NOI18N
        btnAccept.setText(resourceMap.getString("btnAccept.text")); // NOI18N
        btnAccept.setToolTipText(resourceMap.getString("btnAccept.toolTipText")); // NOI18N
        btnAccept.setName("btnAccept"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        comboStatusCodes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboStatusCodes.setToolTipText(resourceMap.getString("comboStatusCodes.toolTipText")); // NOI18N
        comboStatusCodes.setName("comboStatusCodes"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        comboInstances.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboInstances.setToolTipText(resourceMap.getString("comboInstances.toolTipText")); // NOI18N
        comboInstances.setName("comboInstances"); // NOI18N

        jButton1.setAction(actionMap.get("btnCancel")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setToolTipText(resourceMap.getString("jButton1.toolTipText")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(btnAccept, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboInstances, 0, 313, Short.MAX_VALUE)
                            .addComponent(comboStatusCodes, 0, 313, Short.MAX_VALUE)
                            .addComponent(comboSolvers, 0, 313, Short.MAX_VALUE)))
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(comboSolvers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(comboStatusCodes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(comboInstances, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(btnAccept))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void btnAccept() {
        String solver = null;
        String instance = null;
        ExperimentResultStatus statusCode = null;
        if (!"All".equals(comboSolvers.getSelectedItem())) {
            solver = (String) comboSolvers.getSelectedItem();
        }
        if (!"All".equals(comboInstances.getSelectedItem())) {
            instance = (String) comboInstances.getSelectedItem();
        }
        if (!"All".equals(comboStatusCodes.getSelectedItem())) {
            statusCode = (ExperimentResultStatus) comboStatusCodes.getSelectedItem();
        }
        main.resultBrowserRowFilter.setInstanceName(instance);
        main.resultBrowserRowFilter.setSolverName(solver);
        main.resultBrowserRowFilter.setStatus(statusCode);

        main.jobsTableModel.fireTableDataChanged();
        boolean filtersApplied = solver != null || instance != null || statusCode != null;
        if (filtersApplied) {
            main.setJobsFilterStatus("This list of jobs has filters applied to it. Use the filter button below to modify.");
        } else {
            main.setJobsFilterStatus("");
        }
        this.dispose();
    }

    @Action
    public void btnCancel() {
        this.dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAccept;
    private javax.swing.JComboBox comboInstances;
    private javax.swing.JComboBox comboSolvers;
    private javax.swing.JComboBox comboStatusCodes;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables

}
