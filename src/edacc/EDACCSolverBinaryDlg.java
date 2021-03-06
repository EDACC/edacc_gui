/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EDACCSolverBinaryDlg.java
 *
 * Created on 09.05.2011, 19:26:38
 */

package edacc;

import edacc.manageDB.ManageDBSolvers;
import edacc.manageDB.SolverBinariesListModel;
import edacc.model.SolverBinaries;
import java.awt.Frame;
import javax.swing.JOptionPane;

/**
 *
 * @author dgall
 */
public class EDACCSolverBinaryDlg extends javax.swing.JDialog {

    public enum DialogMode { CREATE_MODE, EDIT_MODE };
    
    private SolverBinaries solverBin;
    private SolverBinaries workingCopy;
    private ManageDBSolvers controller;
    private DialogMode mode;
    

    /** Creates new form EDACCSolverBinaryDlg */
    public EDACCSolverBinaryDlg(Frame parent, SolverBinaries solverBin, ManageDBSolvers controller, DialogMode mode) {
        super(parent, true);
        this.solverBin = solverBin;
        this.workingCopy = new SolverBinaries(solverBin);
        this.controller = controller;
        this.mode = mode;
        setLocationRelativeTo(parent);
        initComponents();
        lbRunPath.setSelectedIndex(0);
        showSolverBin();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lBinaryName = new javax.swing.JLabel();
        lRunCommand = new javax.swing.JLabel();
        lRunPath = new javax.swing.JLabel();
        tfBinaryName = new javax.swing.JTextField();
        tfRunCommand = new javax.swing.JTextField();
        lCommand = new javax.swing.JLabel();
        lCommandPreview = new javax.swing.JLabel();
        bAddBinary = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();
        lCaption = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lbRunPath = new javax.swing.JList();
        lVersion = new javax.swing.JLabel();
        tfVersion = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCSolverBinaryDlg.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setModal(true);
        setName("Form"); // NOI18N

        lBinaryName.setText(resourceMap.getString("lBinaryName.text")); // NOI18N
        lBinaryName.setName("lBinaryName"); // NOI18N

        lRunCommand.setText(resourceMap.getString("lRunCommand.text")); // NOI18N
        lRunCommand.setName("lRunCommand"); // NOI18N

        lRunPath.setText(resourceMap.getString("lRunPath.text")); // NOI18N
        lRunPath.setName("lRunPath"); // NOI18N

        tfBinaryName.setText(resourceMap.getString("tfBinaryName.text")); // NOI18N
        tfBinaryName.setName("tfBinaryName"); // NOI18N

        tfRunCommand.setText(resourceMap.getString("tfRunCommand.text")); // NOI18N
        tfRunCommand.setName("tfRunCommand"); // NOI18N
        tfRunCommand.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfRunCommandChanged(evt);
            }
        });

        lCommand.setText(resourceMap.getString("lCommand.text")); // NOI18N
        lCommand.setName("lCommand"); // NOI18N

        lCommandPreview.setText(resourceMap.getString("lCommandPreview.text")); // NOI18N
        lCommandPreview.setName("lCommandPreview"); // NOI18N

        bAddBinary.setText(resourceMap.getString("bAddBinary.text")); // NOI18N
        bAddBinary.setName("bAddBinary"); // NOI18N
        bAddBinary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBinary(evt);
            }
        });

        bCancel.setText(resourceMap.getString("bCancel.text")); // NOI18N
        bCancel.setName("bCancel"); // NOI18N
        bCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCancelActionPerformed(evt);
            }
        });

        lCaption.setFont(resourceMap.getFont("lCaption.font")); // NOI18N
        lCaption.setText(resourceMap.getString("lCaption.text")); // NOI18N
        lCaption.setName("lCaption"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        lbRunPath.setModel(new SolverBinariesListModel(solverBin));
        lbRunPath.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lbRunPath.setName("lbRunPath"); // NOI18N
        lbRunPath.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lbRuntPathChanged(evt);
            }
        });
        jScrollPane1.setViewportView(lbRunPath);

        lVersion.setText(resourceMap.getString("lVersion.text")); // NOI18N
        lVersion.setName("lVersion"); // NOI18N

        tfVersion.setText(resourceMap.getString("tfVersion.text")); // NOI18N
        tfVersion.setName("tfVersion"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lCaption)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 395, Short.MAX_VALUE)
                        .addComponent(bAddBinary))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lRunCommand)
                            .addComponent(lBinaryName)
                            .addComponent(lRunPath)
                            .addComponent(lCommand)
                            .addComponent(lVersion))
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tfVersion, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                            .addComponent(tfBinaryName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                            .addComponent(lCommandPreview, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                            .addComponent(tfRunCommand, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lCaption)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfBinaryName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lBinaryName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lRunPath)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfRunCommand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lRunCommand))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lCommandPreview)
                    .addComponent(lCommand, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lVersion)
                    .addComponent(tfVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bCancel)
                    .addComponent(bAddBinary))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addBinary(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBinary
        solverBin.setBinaryName(tfBinaryName.getText());
        if (tfRunCommand.getText().equals(""))
            solverBin.setRunCommand(null);
        else
            solverBin.setRunCommand(tfRunCommand.getText());
        solverBin.setRunPath((String) lbRunPath.getModel().getElementAt(lbRunPath.getSelectedIndex()));
        solverBin.setVersion(tfVersion.getText());
        try {
            if (mode == DialogMode.CREATE_MODE) {
                // in create mode: add the binary to the solver, in edit mode, do nothing
                controller.addSolverBinary(solverBin);
            }
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occured while adding Solver Binary: \n" + ex.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addBinary

    private void tfRunCommandChanged(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfRunCommandChanged
        updateRunCommandLine();
    }//GEN-LAST:event_tfRunCommandChanged

    private void lbRuntPathChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lbRuntPathChanged
        updateRunCommandLine();
    }//GEN-LAST:event_lbRuntPathChanged

    private void bCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCancelActionPerformed
        if (mode == DialogMode.EDIT_MODE) {
            // reset object to working copy (status before editing)
            solverBin.setAll(workingCopy);
        }
        this.dispose();
    }//GEN-LAST:event_bCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAddBinary;
    private javax.swing.JButton bCancel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lBinaryName;
    private javax.swing.JLabel lCaption;
    private javax.swing.JLabel lCommand;
    private javax.swing.JLabel lCommandPreview;
    private javax.swing.JLabel lRunCommand;
    private javax.swing.JLabel lRunPath;
    private javax.swing.JLabel lVersion;
    private javax.swing.JList lbRunPath;
    private javax.swing.JTextField tfBinaryName;
    private javax.swing.JTextField tfRunCommand;
    private javax.swing.JTextField tfVersion;
    // End of variables declaration//GEN-END:variables

    private void updateRunCommandLine() {
        String runCommand = tfRunCommand.getText();
        String runPath = (String) lbRunPath.getModel().getElementAt(lbRunPath.getSelectedIndex());
        if (runCommand != null)
            lCommandPreview.setText(runCommand + " ." + runPath);
        else
            lCommandPreview.setText(runPath);
    }

    private void showSolverBin() {
        if (solverBin == null)
            return;
        tfBinaryName.setText(solverBin.getBinaryName());
        tfRunCommand.setText(solverBin.getRunCommand());
        tfVersion.setText(solverBin.getVersion());
        int selectedIndex = ((SolverBinariesListModel) lbRunPath.getModel()).getIndexOf(solverBin.getRunPath());
        // if an error occured select first index
        if (selectedIndex < 0)
            selectedIndex = 0;
        lbRunPath.setSelectedIndex(selectedIndex);
        if (mode == DialogMode.CREATE_MODE) {
            this.setTitle("Add Solver Binary");
            bAddBinary.setText("Add Binary");
        } else if (mode == DialogMode.EDIT_MODE) {
            this.setTitle("Edit Solver Binary");
            bAddBinary.setText("Edit Binary");
        }
    }

}
