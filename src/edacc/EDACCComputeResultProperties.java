/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EDACCComputeResultProperties.java
 *
 * Created on 10.10.2010, 15:50:17
 */

package edacc;

import edacc.events.TaskEvents;
import edacc.model.Experiment;
import edacc.model.Property;
import edacc.model.Tasks;
import edacc.properties.ComputePropertiesController;
import edacc.properties.PropertySelectionTableModel;
import java.awt.Component;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author rretz
 */
public class EDACCComputeResultProperties extends javax.swing.JDialog implements TaskEvents{
    ComputePropertiesController controller;
    PropertySelectionTableModel tableModel;
    Experiment exp;
    /** Creates new form EDACCComputeResultProperties */
    public EDACCComputeResultProperties(java.awt.Frame parent, boolean modal, Experiment exp) {
        super(parent, modal);
        initComponents();
        this.exp = exp;
        controller = new ComputePropertiesController(this, tableSelectResultProperties);

        // initate the result property table
        tableModel = new PropertySelectionTableModel();
        tableSelectResultProperties.setModel(tableModel);
       
       initalize();
    }

    public EDACCComputeResultProperties(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelMain = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableSelectResultProperties = new javax.swing.JTable();
        checkBoxReCompute = new javax.swing.JCheckBox();
        buttonCompute = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCComputeResultProperties.class);
        panelMain.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelMain.border.title"))); // NOI18N
        panelMain.setName("panelMain"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableSelectResultProperties.setModel(new javax.swing.table.DefaultTableModel(
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
        tableSelectResultProperties.setName("tableSelectResultProperties"); // NOI18N
        jScrollPane1.setViewportView(tableSelectResultProperties);

        checkBoxReCompute.setText(resourceMap.getString("checkBoxReCompute.text")); // NOI18N
        checkBoxReCompute.setName("checkBoxReCompute"); // NOI18N

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxReCompute)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkBoxReCompute))
        );

        buttonCompute.setText(resourceMap.getString("buttonCompute.text")); // NOI18N
        buttonCompute.setName("buttonCompute"); // NOI18N
        buttonCompute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonComputeActionPerformed(evt);
            }
        });

        buttonCancel.setText(resourceMap.getString("buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonCompute)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 271, Short.MAX_VALUE)
                        .addComponent(buttonCancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCompute)
                    .addComponent(buttonCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonComputeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonComputeActionPerformed
        Vector<Property> toCalculate = tableModel.getAllChoosen();
        if(toCalculate.isEmpty()){
            JOptionPane.showMessageDialog(this,
                "No result property selected.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }else {
            Tasks.startTask("computProperties", new Class[]{Vector.class, boolean.class, edacc.model.Experiment.class, edacc.model.Tasks.class}, new Object[]{toCalculate, this.checkBoxReCompute.isSelected(), exp, null}, controller, EDACCComputeResultProperties.this);
            //controller.computProperties(toCalculate, this.checkBoxReCompute.isSelected(), exp);
        }
    }//GEN-LAST:event_buttonComputeActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EDACCComputeResultProperties dialog = new EDACCComputeResultProperties(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonCompute;
    private javax.swing.JCheckBox checkBoxReCompute;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelMain;
    private javax.swing.JTable tableSelectResultProperties;
    // End of variables declaration//GEN-END:variables

    private void initalize() {
        controller.loadResultProperties();
    }

    @Override
    public void onTaskSuccessful(String methodName, Object result) {
    }

    @Override
    public void onTaskStart(String methodName) {
    }

    @Override
    public void onTaskFailed(String methodName, Throwable e) {
        e.printStackTrace();
    }


}
