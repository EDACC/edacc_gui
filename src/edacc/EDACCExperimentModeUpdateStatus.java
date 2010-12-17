/*
 * EDACCExperimentModeUpdateStatus.java
 *
 * Created on 17.12.2010, 16:16:07
 */
package edacc;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentResultStatus;
import edacc.model.TaskRunnable;
import edacc.model.Tasks;
import java.sql.SQLException;
import org.jdesktop.application.Action;

/**
 *
 * @author simon
 */
public class EDACCExperimentModeUpdateStatus extends javax.swing.JDialog {

    private ExperimentController expController;

    /** Creates new form EDACCExperimentModeUpdateStatus */
    public EDACCExperimentModeUpdateStatus(java.awt.Frame parent, boolean modal, ExperimentController expController) {
        super(parent, modal);
        initComponents();
        this.expController = expController;
        comboStatus.removeAllItems();
        for (ExperimentResultStatus status : ExperimentResultStatus.constants) {
            comboStatus.addItem(status);
        }
        comboStatus.addItem("custom");

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        comboStatus = new javax.swing.JComboBox();
        txtStatus = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCExperimentModeUpdateStatus.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        comboStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboStatus.setName("comboStatus"); // NOI18N
        comboStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboStatusActionPerformed(evt);
            }
        });

        txtStatus.setText(resourceMap.getString("txtStatus.text")); // NOI18N
        txtStatus.setName("txtStatus"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getActionMap(EDACCExperimentModeUpdateStatus.class, this);
        jButton1.setAction(actionMap.get("btnApply")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.setPreferredSize(new java.awt.Dimension(67, 23));

        jButton2.setAction(actionMap.get("btnDismiss")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(comboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void comboStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboStatusActionPerformed
        if (comboStatus.getSelectedItem() instanceof ExperimentResultStatus) {
            txtStatus.setText("" + ((ExperimentResultStatus) comboStatus.getSelectedItem()).getValue());
            txtStatus.setEnabled(false);
        } else {
            txtStatus.setEnabled(true);
        }
    }//GEN-LAST:event_comboStatusActionPerformed

    @Action
    public void btnApply() {
        ExperimentResultStatus status = null;
        if (comboStatus.getSelectedItem() instanceof ExperimentResultStatus) {
            status = (ExperimentResultStatus) comboStatus.getSelectedItem();
        } else {
            try {
                status = ExperimentResultStatus.getExperimentResultStatus(Integer.parseInt(txtStatus.getText()));
            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Invalid status code.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        int userInput = javax.swing.JOptionPane.showConfirmDialog(this, "Do you really want to update the status of the currently visible jobs?", "Status Update", javax.swing.JOptionPane.YES_NO_OPTION);
        if (userInput == javax.swing.JOptionPane.YES_OPTION) {
            final ExperimentResultStatus fstatus = status;
            Tasks.startTask(new TaskRunnable() {

                @Override
                public void run(Tasks task) {
                    try {
                        expController.setStatus(fstatus);
                    } catch (SQLException ex) {
                        javax.swing.JOptionPane.showMessageDialog(Tasks.getTaskView(), ex.getMessage(), "Database Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
        setVisible(false);
    }

    @Action
    public void btnDismiss() {
        setVisible(false);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comboStatus;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField txtStatus;
    // End of variables declaration//GEN-END:variables
}
