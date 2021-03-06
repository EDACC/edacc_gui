/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EDACCVerifierBinaryDialog.java
 *
 * Created on Mar 14, 2012, 1:02:28 AM
 */
package edacc;

import edacc.manageDB.VerifierBinaryListModel;
import java.io.File;

/**
 *
 * @author simon
 */
public class EDACCVerifierBinaryDialog extends javax.swing.JDialog {

    private File[] files;
    private boolean cancelled;
    private String runCommand;
    private String runPath;

    /** Creates new form EDACCVerifierBinaryDialog */
    public EDACCVerifierBinaryDialog(java.awt.Frame parent, boolean modal, File[] files) {
        super(parent, modal);
        this.files = files;
        initComponents();
        cancelled = true;
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
        jScrollPane1 = new javax.swing.JScrollPane();
        listFiles = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        txtRunCommand = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        lblGridCommand = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        btnAddBinary = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCVerifierBinaryDialog.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        listFiles.setModel(new VerifierBinaryListModel(files));
        listFiles.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listFiles.setName("listFiles"); // NOI18N
        listFiles.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listFilesValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(listFiles);

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtRunCommand.setText(resourceMap.getString("txtRunCommand.text")); // NOI18N
        txtRunCommand.setName("txtRunCommand"); // NOI18N
        txtRunCommand.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtRunCommandKeyReleased(evt);
            }
        });

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        lblGridCommand.setText(resourceMap.getString("lblGridCommand.text")); // NOI18N
        lblGridCommand.setName("lblGridCommand"); // NOI18N

        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnAddBinary.setText(resourceMap.getString("btnAddBinary.text")); // NOI18N
        btnAddBinary.setName("btnAddBinary"); // NOI18N
        btnAddBinary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddBinaryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(lblGridCommand)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 201, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnCancel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addComponent(btnAddBinary))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                    .addComponent(txtRunCommand, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAddBinary, btnCancel});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtRunCommand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lblGridCommand))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddBinary)
                    .addComponent(btnCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void updateRunCommandLine() {
        runCommand = txtRunCommand.getText();
        runPath = (String) listFiles.getModel().getElementAt(listFiles.getSelectedIndex());
        if (!"".equals(runCommand)) {
            lblGridCommand.setText(runCommand + " " + runPath);
        } else {
            lblGridCommand.setText(runPath);
        }
    }

    public String getRunCommand() {
        return runCommand;
    }

    public String getRunPath() {
        return runPath;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }

    private void listFilesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listFilesValueChanged
        updateRunCommandLine();
    }//GEN-LAST:event_listFilesValueChanged

    private void txtRunCommandKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRunCommandKeyReleased
        updateRunCommandLine();
    }//GEN-LAST:event_txtRunCommandKeyReleased

    private void btnAddBinaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBinaryActionPerformed
        cancelled = false;
        setVisible(false);
    }//GEN-LAST:event_btnAddBinaryActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddBinary;
    private javax.swing.JButton btnCancel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblGridCommand;
    private javax.swing.JList listFiles;
    private javax.swing.JTextField txtRunCommand;
    // End of variables declaration//GEN-END:variables
}
