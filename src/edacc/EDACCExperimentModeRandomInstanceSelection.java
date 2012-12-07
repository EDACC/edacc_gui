/*
 * EDACCExperimentModeRandomInstanceSelection.java
 *
 * Created on 17.09.2010, 16:54:10
 */
package edacc;

import edacc.experiment.Util;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author simon
 */
public class EDACCExperimentModeRandomInstanceSelection extends javax.swing.JDialog {

    private EDACCExperimentMode expMode;

    /** Creates new form EDACCExperimentModeRandomInstanceSelection */
    public EDACCExperimentModeRandomInstanceSelection(java.awt.Frame parent, boolean modal, final EDACCExperimentMode expMode) {
        super(parent, modal);
        this.expMode = expMode;
        initComponents();
        txtCount.selectAll();
        txtCount.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnSelectActionPerformed(null);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    btnCancelActionPerformed(null);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    txtCount.setText(Util.getNumberText(txtCount.getText()));
                }
            }
        });
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
        btnCancel = new javax.swing.JButton();
        btnSelect = new javax.swing.JButton();
        txtCount = new javax.swing.JTextField();
        chkSelectedInstances = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCExperimentModeRandomInstanceSelection.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        setResizable(false);

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getActionMap(EDACCExperimentModeRandomInstanceSelection.class, this);
        btnCancel.setAction(actionMap.get("btnCancel")); // NOI18N
        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.setPreferredSize(new java.awt.Dimension(80, 25));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnSelect.setAction(actionMap.get("btnSelect")); // NOI18N
        btnSelect.setText(resourceMap.getString("btnSelect.text")); // NOI18N
        btnSelect.setActionCommand(resourceMap.getString("btnSelect.actionCommand")); // NOI18N
        btnSelect.setName("btnSelect"); // NOI18N
        btnSelect.setPreferredSize(new java.awt.Dimension(80, 25));
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });

        txtCount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCount.setText(resourceMap.getString("txtCount.text")); // NOI18N
        txtCount.setName("txtCount"); // NOI18N

        chkSelectedInstances.setText(resourceMap.getString("chkSelectedInstances.text")); // NOI18N
        chkSelectedInstances.setName("chkSelectedInstances"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkSelectedInstances, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                            .addComponent(txtCount, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkSelectedInstances)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        try {
            expMode.randomInstanceSelection(Integer.parseInt(txtCount.getText()), chkSelectedInstances.isSelected());
            this.dispose();
        } catch (NumberFormatException ex) {
            txtCount.selectAll();
            javax.swing.JOptionPane.showMessageDialog(null, "Count has to be an integer.", "Random Instance Selection", javax.swing.JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            txtCount.selectAll();
            javax.swing.JOptionPane.showMessageDialog(null, ex.getMessage(), "Random Instance Selection", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
}//GEN-LAST:event_btnSelectActionPerformed

private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
}//GEN-LAST:event_btnCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSelect;
    private javax.swing.JCheckBox chkSelectedInstances;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField txtCount;
    // End of variables declaration//GEN-END:variables
}
