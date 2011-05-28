/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EDACCCreateEditInstanceClassDialog.java
 *
 * Created on 19.03.2010, 14:55:29
 */

package edacc;

import edacc.model.InstanceClass;
import edacc.model.InstanceClassAlreadyInDBException;
import edacc.model.InstanceClassDAO;
import edacc.model.InstanceDAO;
import edacc.model.NoConnectionToDBException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author rretz
 */
public class EDACCCreateEditInstanceClassDialog extends javax.swing.JDialog {
     JTree tree;
     InstanceClass instanceClass;
     InstanceClass parentClass;
     EDACCSelectParentInstanceClassDialog selectParent;

     /**
      * Creates new form EDACCCreateEditInstanceClassDialog
      * @param parent
      * @param modal
      * @param treeModel
      */
    public EDACCCreateEditInstanceClassDialog(java.awt.Frame parent, boolean modal, JTree tree) {
        super(parent, modal);
        initComponents();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
        if(node == null){
            this.jButtonEdit.setVisible(false);
            this.jButtonCreate.setEnabled(true);
            this.setTitle("Create a new instance class");
        } else{
            this.jButtonCreate.setVisible(false);
            this.jButtonEdit.setEnabled(true);
            this.instanceClass = (InstanceClass)node.getUserObject();
            this.setTitle("Edit instance class: "+this.instanceClass.getName());
            this.jButtonEdit.setVisible(true);
            this.jTextFieldName.setText(instanceClass.getName());
            this.jTextArea1.setText(instanceClass.getDescription());
            this.jButtonSelectParent.setEnabled(true);
            DefaultMutableTreeNode checkParent =  (DefaultMutableTreeNode)node.getParent();
            if((checkParent != null) && (!checkParent.isRoot())){
                this.parentClass = (InstanceClass) checkParent.getUserObject();
                try {
                    this.jLabelSelectedParent.setText(InstanceClassDAO.getCompletePathOf(parentClass.getId()));
                } catch (SQLException ex) {
                    Logger.getLogger(EDACCCreateEditInstanceClassDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
           
        }
            

        this.tree = tree;
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
        jButtonCancel = new javax.swing.JButton();
        jButtonCreate = new javax.swing.JButton();
        jButtonEdit = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabelName = new javax.swing.JLabel();
        jTextFieldName = new javax.swing.JTextField();
        jLabelDescription = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButtonSelectParent = new javax.swing.JButton();
        jLabelSelectedParent = new javax.swing.JLabel();
        jLabelParent = new javax.swing.JLabel();
        jButtonRemoveParent = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCCreateEditInstanceClassDialog.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(350, 300));
        setName("Form"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jButtonCancel.setText(resourceMap.getString("jButtonCancel.text")); // NOI18N
        jButtonCancel.setName("jButtonCancel"); // NOI18N
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jButtonCreate.setText(resourceMap.getString("jButtonCreate.text")); // NOI18N
        jButtonCreate.setName("jButtonCreate"); // NOI18N
        jButtonCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateActionPerformed(evt);
            }
        });

        jButtonEdit.setText(resourceMap.getString("jButtonEdit.text")); // NOI18N
        jButtonEdit.setName("jButtonEdit"); // NOI18N
        jButtonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonCancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 204, Short.MAX_VALUE)
                .addComponent(jButtonEdit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCreate)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonCancel, jButtonCreate, jButtonEdit});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCreate)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonEdit)))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonCancel, jButtonCreate, jButtonEdit});

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N

        jLabelName.setText(resourceMap.getString("jLabelName.text")); // NOI18N
        jLabelName.setName("jLabelName"); // NOI18N

        jTextFieldName.setText(resourceMap.getString("jTextFieldName.text")); // NOI18N
        jTextFieldName.setName("jTextFieldName"); // NOI18N

        jLabelDescription.setText(resourceMap.getString("jLabelDescription.text")); // NOI18N
        jLabelDescription.setName("jLabelDescription"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setColumns(1);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(4);
        jTextArea1.setTabSize(1);
        jTextArea1.setMaximumSize(new java.awt.Dimension(100, 76));
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        jButtonSelectParent.setText(resourceMap.getString("jButtonSelectParent.text")); // NOI18N
        jButtonSelectParent.setName("jButtonSelectParent"); // NOI18N
        jButtonSelectParent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectParentActionPerformed(evt);
            }
        });

        jLabelSelectedParent.setText(resourceMap.getString("jLabelSelectedParent.text")); // NOI18N
        jLabelSelectedParent.setName("jLabelSelectedParent"); // NOI18N

        jLabelParent.setText(resourceMap.getString("jLabelParent.text")); // NOI18N
        jLabelParent.setName("jLabelParent"); // NOI18N

        jButtonRemoveParent.setText(resourceMap.getString("jButtonRemoveParent.text")); // NOI18N
        jButtonRemoveParent.setName("jButtonRemoveParent"); // NOI18N
        jButtonRemoveParent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveParentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelName, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelParent))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButtonSelectParent)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                        .addComponent(jLabelSelectedParent, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonRemoveParent, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                    .addComponent(jTextFieldName, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonRemoveParent, jButtonSelectParent});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelName)
                    .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelDescription)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelParent)
                    .addComponent(jButtonRemoveParent)
                    .addComponent(jButtonSelectParent, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSelectedParent, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonRemoveParent, jButtonSelectParent});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.jTextArea1.setText("");
        this.jTextFieldName.setText("");
        this.jLabelSelectedParent.setText("");
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateActionPerformed
       if(jTextFieldName.getText().isEmpty()){
        JOptionPane.showMessageDialog(this,
                    "Please enter a instace class name." ,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);       
       }else{
           
                try {
                    InstanceClass ret;
                    String description = "";
                    if(!jTextArea1.getText().isEmpty())
                        description = jTextArea1.getText();                   
                    ret = InstanceClassDAO.createInstanceClass(jTextFieldName.getText(), description, parentClass);
                   // tableModel.addClass(ret);
                    this.jTextArea1.setText("");
                    this.jTextFieldName.setText("");
                    this.dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                    "There is a Problem with the database: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                } catch (InstanceClassAlreadyInDBException ex) {
                    JOptionPane.showMessageDialog(this,
                    "Instance class is already in the system.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                }
          
       }
    }//GEN-LAST:event_jButtonCreateActionPerformed

    private void jButtonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditActionPerformed
          if(jTextFieldName.getText().isEmpty()){
            JOptionPane.showMessageDialog(this,
                        "Please enter a instance class name." ,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
        }else {
            try {
                instanceClass.setName(jTextFieldName.getText());
                instanceClass.setDescription(jTextArea1.getText());
                InstanceClassDAO.save(instanceClass, parentClass);
                this.dispose();
            } catch (SQLException ex) {
                 JOptionPane.showMessageDialog(this,
                    "There is a Problem with the database: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButtonEditActionPerformed

    private void jButtonSelectParentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectParentActionPerformed

           if(!jButtonEdit.isVisible()){
                selectParent = new EDACCSelectParentInstanceClassDialog(EDACCApp.getApplication().getMainFrame(), true, (DefaultTreeModel) tree.getModel());
            }else{
                selectParent = new EDACCSelectParentInstanceClassDialog(EDACCApp.getApplication().getMainFrame(), true, (DefaultTreeModel) tree.getModel());
            }

            selectParent.setLocationRelativeTo(this);
            selectParent.setAlwaysOnTop(true);
            selectParent.initialize();
            selectParent.setVisible(true);
            InstanceClass tmpParent = parentClass;
            parentClass = selectParent.getInstanceClassParent();
            if (!jButtonEdit.isVisible()) {
                if (parentClass != null) {
                try {
                    jLabelSelectedParent.setText(InstanceClassDAO.getCompletePathOf(parentClass.getId()));
                } catch (SQLException ex) {
                    Logger.getLogger(EDACCCreateEditInstanceClassDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
                } else {
                    parentClass = tmpParent;
                }
            } else {
                if (parentClass == null) {
                    parentClass = tmpParent;
                } else {
                try {
                    jLabelSelectedParent.setText(InstanceClassDAO.getCompletePathOf(parentClass.getId()));
                } catch (SQLException ex) {
                    Logger.getLogger(EDACCCreateEditInstanceClassDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
;
                }
            }
    }//GEN-LAST:event_jButtonSelectParentActionPerformed

    private void jButtonRemoveParentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveParentActionPerformed
        this.parentClass = null;
        this.jLabelSelectedParent.setText("");
    }//GEN-LAST:event_jButtonRemoveParentActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonCreate;
    private javax.swing.JButton jButtonEdit;
    private javax.swing.JButton jButtonRemoveParent;
    private javax.swing.JButton jButtonSelectParent;
    private javax.swing.JLabel jLabelDescription;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JLabel jLabelParent;
    private javax.swing.JLabel jLabelSelectedParent;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextFieldName;
    // End of variables declaration//GEN-END:variables

}
