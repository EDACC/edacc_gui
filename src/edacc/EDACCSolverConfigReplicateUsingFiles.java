/*
 * EDACCSolverConfigReplicateUsingFile.java
 *
 * Created on 16.02.2011, 12:25:58
 */
package edacc;

import edacc.experiment.SolverConfigEntryTableModel;
import edacc.model.Parameter;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;

/**
 *
 * @author simon
 */
public class EDACCSolverConfigReplicateUsingFiles extends javax.swing.JDialog {

    private EDACCSolverConfigEntry entry;
    private JFileChooser fileChooser;
    private File chosenFolder;
    private ParameterTableModel model;

    /** Creates new form EDACCSolverConfigReplicateUsingFile */
    public EDACCSolverConfigReplicateUsingFiles(java.awt.Frame parent, boolean modal, EDACCSolverConfigEntry entry) {
        super(parent, modal);
        initComponents();
        this.entry = entry;
        fileChooser = new JFileChooser();
        txtDirectory.setText(fileChooser.getCurrentDirectory().getAbsolutePath());
        chosenFolder = null;
        model = (ParameterTableModel) tblParameters.getModel();
        model.setParameters(entry.getParameters());
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(entry.getModel().getValueAt(i, 3), i, 3);
            model.setValueAt(entry.getModel().getValueAt(i, 0), i, 0);
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

        jPanel1 = new javax.swing.JPanel();
        txtDirectory = new javax.swing.JTextField();
        btnChooseDirectory = new javax.swing.JButton();
        pnlParameters = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblParameters = new javax.swing.JTable();
        btnReplicate = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCSolverConfigReplicateUsingFiles.class);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        txtDirectory.setText(resourceMap.getString("txtDirectory.text")); // NOI18N
        txtDirectory.setName("txtDirectory"); // NOI18N

        btnChooseDirectory.setText(resourceMap.getString("btnChooseDirectory.text")); // NOI18N
        btnChooseDirectory.setName("btnChooseDirectory"); // NOI18N
        btnChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseDirectoryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(txtDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnChooseDirectory))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnChooseDirectory))
        );

        pnlParameters.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("pnlParameters.border.title"))); // NOI18N
        pnlParameters.setName("pnlParameters"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblParameters.setModel(new ParameterTableModel());
        tblParameters.setName("tblParameters"); // NOI18N
        jScrollPane1.setViewportView(tblParameters);

        javax.swing.GroupLayout pnlParametersLayout = new javax.swing.GroupLayout(pnlParameters);
        pnlParameters.setLayout(pnlParametersLayout);
        pnlParametersLayout.setHorizontalGroup(
            pnlParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
        );
        pnlParametersLayout.setVerticalGroup(
            pnlParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        btnReplicate.setText(resourceMap.getString("btnReplicate.text")); // NOI18N
        btnReplicate.setName("btnReplicate"); // NOI18N
        btnReplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReplicateActionPerformed(evt);
            }
        });

        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlParameters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(239, Short.MAX_VALUE)
                .addComponent(btnCancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReplicate)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlParameters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReplicate)
                    .addComponent(btnCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed
    private void btnChooseDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseDirectoryActionPerformed
        File curDir = new File(txtDirectory.getText());


        if (curDir.exists()) {
            fileChooser.setCurrentDirectory(curDir);


        }
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


        if (fileChooser.showDialog(this, "Choose") == JFileChooser.APPROVE_OPTION) {
            txtDirectory.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_btnChooseDirectoryActionPerformed

    private void btnReplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReplicateActionPerformed
        File dir = new File(txtDirectory.getText());
        if (!dir.exists()) {
            // TODO: ERROR
        } else {
            chosenFolder = dir;
            setVisible(false);
        }
    }//GEN-LAST:event_btnReplicateActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnChooseDirectory;
    private javax.swing.JButton btnReplicate;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlParameters;
    private javax.swing.JTable tblParameters;
    private javax.swing.JTextField txtDirectory;
    // End of variables declaration//GEN-END:variables

    /**
     * Returns the chosen folder. That folder exists or is null if the operation has been cancelled.
     * @return
     */
    public File getChosenFolder() {
        return chosenFolder;
    }

    public SolverConfigEntryTableModel getModel() {
        return model;
    }

    private class ParameterTableModel extends SolverConfigEntryTableModel {

        private boolean[] useRegex;

        @Override
        public void setParameters(ArrayList<Parameter> parameters) {
            super.setParameters(parameters);
            useRegex = new boolean[parameters.size()];
        }

        @Override
        public int getColumnCount() {
            return super.getColumnCount() + 1;
        }

        @Override
        public String getColumnName(int col) {
            if (col < super.getColumnCount()) {
                return super.getColumnName(col);
            } else {
                return "use regex";
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex < super.getColumnCount()) {
                return super.getValueAt(rowIndex, columnIndex);
            } else {
                return useRegex[rowIndex];
            }
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            if (col < super.getColumnCount()) {
                super.setValueAt(value, row, col);
            } else {
                useRegex[row] = (Boolean) value;
                this.fireTableRowsUpdated(row, row);
            }
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            if (col < super.getColumnCount()) {
                return super.isCellEditable(row, col);
            } else {
                return true;
            }
        }
    }
}
