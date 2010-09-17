/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EDACCManagePropertyValueTypesDialog.java
 *
 * Created on 13.09.2010, 14:28:26
 */

package edacc;

import edacc.model.NoConnectionToDBException;
import edacc.properties.PropertyValueTypeTableModel;
import edacc.properties.PropertyValueTypesController;
import edacc.satinstances.PropertyValueType;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author rretz
 */
public class EDACCManagePropertyValueTypesDialog extends javax.swing.JDialog {
    private PropertyValueTypesController controller;
    private PropertyValueTypeTableModel propValueTypeTableModel;
    private EDACCSelectPropertyValueTypeClassDialog selectValueType;
    /** Creates new form EDACCManagePropertyValueTypesDialog */
    public EDACCManagePropertyValueTypesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // initialize tablePropertyValueTypes
        propValueTypeTableModel = new PropertyValueTypeTableModel();
        tablePropertyValueTypes.setModel(propValueTypeTableModel);
        tablePropertyValueTypes.setRowSorter(new TableRowSorter<PropertyValueTypeTableModel>(propValueTypeTableModel));

        controller = new PropertyValueTypesController(this, tablePropertyValueTypes);
    }

     /**
     * Initialize and chargs the tableSolverPropertys with the corresponding items.
     */
    public void initialize() {
        try {
            controller.loadPropertyValueTypes();
        } catch (NoConnectionToDBException ex) {
            Logger.getLogger(EDACCManagePropertyValueTypesDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(EDACCManagePropertyValueTypesDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EDACCManagePropertyValueTypesDialog.class.getName()).log(Level.SEVERE, null, ex);
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

        jFileChooser1 = new javax.swing.JFileChooser();
        panelMain = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablePropertyValueTypes = new javax.swing.JTable();
        buttonDone = new javax.swing.JButton();
        buttonChooseClassFile = new javax.swing.JButton();
        buttonRemove = new javax.swing.JButton();

        jFileChooser1.setName("jFileChooser1"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCManagePropertyValueTypesDialog.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        panelMain.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelMain.border.title"))); // NOI18N
        panelMain.setName("panelMain"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tablePropertyValueTypes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablePropertyValueTypes.setName("tablePropertyValueTypes"); // NOI18N
        jScrollPane1.setViewportView(tablePropertyValueTypes);

        buttonDone.setText(resourceMap.getString("buttonDone.text")); // NOI18N
        buttonDone.setName("buttonDone"); // NOI18N
        buttonDone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDoneActionPerformed(evt);
            }
        });

        buttonChooseClassFile.setText(resourceMap.getString("buttonChooseClassFile.text")); // NOI18N
        buttonChooseClassFile.setName("buttonChooseClassFile"); // NOI18N
        buttonChooseClassFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseClassFileActionPerformed(evt);
            }
        });

        buttonRemove.setText(resourceMap.getString("buttonRemove.text")); // NOI18N
        buttonRemove.setName("buttonRemove"); // NOI18N
        buttonRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addComponent(buttonChooseClassFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonRemove)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 142, Short.MAX_VALUE)
                        .addComponent(buttonDone)))
                .addContainerGap())
        );

        panelMainLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonChooseClassFile, buttonDone, buttonRemove});

        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonChooseClassFile)
                    .addComponent(buttonDone)
                    .addComponent(buttonRemove))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelMainLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonChooseClassFile, buttonDone, buttonRemove});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonDoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDoneActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_buttonDoneActionPerformed

    private void buttonChooseClassFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseClassFileActionPerformed
        try {
            int returnVal = jFileChooser1.showOpenDialog(this);
            File file = jFileChooser1.getSelectedFile();
            JFrame mainFrame = EDACCApp.getApplication().getMainFrame();
            selectValueType = new EDACCSelectPropertyValueTypeClassDialog(mainFrame, true);
            selectValueType.setLocationRelativeTo(mainFrame);
            selectValueType.initialize(file);
            selectValueType.setVisible(true);
            controller.createNewPropertyValueType(file);
            controller.loadPropertyValueTypes();
        } catch (IOException ex) {
            Logger.getLogger(EDACCManagePropertyValueTypesDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoConnectionToDBException ex) {
            Logger.getLogger(EDACCManagePropertyValueTypesDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(EDACCManagePropertyValueTypesDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_buttonChooseClassFileActionPerformed

    private void buttonRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveActionPerformed
        controller.removePropertyValueType(tablePropertyValueTypes.convertRowIndexToModel(tablePropertyValueTypes.getSelectedRow()));
    }//GEN-LAST:event_buttonRemoveActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EDACCManagePropertyValueTypesDialog dialog = new EDACCManagePropertyValueTypesDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonChooseClassFile;
    private javax.swing.JButton buttonDone;
    private javax.swing.JButton buttonRemove;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelMain;
    private javax.swing.JTable tablePropertyValueTypes;
    // End of variables declaration//GEN-END:variables

}
