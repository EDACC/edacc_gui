/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EDACCAddInstanceErrorDialog.java
 *
 * Created on 14-Nov-2011, 15:18:24
 */
package edacc;

import edacc.events.TaskEvents;
import edacc.manageDB.InstanceDupErrorTableRenderer;
import edacc.manageDB.AddInstanceErrorController;
import edacc.manageDB.InstanceDupErrorFilter;
import edacc.manageDB.InstanceDupErrorTableModel;
import edacc.manageDB.InstanceErrorTableCellRenderer;
import edacc.manageDB.InstanceErrorTableModel;
import edacc.manageDB.InstancesToAddSelectionListener;
import edacc.model.Instance;
import edacc.model.InstanceClass;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author rretz
 */
public class EDACCAddInstanceErrorDialog extends javax.swing.JDialog  implements TaskEvents{

    private AddInstanceErrorController controller;
    private InstanceDupErrorFilter dupErrorFilter;
    private InstanceDupErrorFilter filter;
    private InstanceDupErrorTableModel dupErrorModel;
    private InstanceErrorTableModel toAddModel;
    private TableRowSorter rowSorter;

    /** Creates new form EDACCAddInstanceErrorDialog */
    public EDACCAddInstanceErrorDialog(java.awt.Frame parent, boolean modal) {
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

        jPnlToolBtns = new javax.swing.JPanel();
        jBtnAdd = new javax.swing.JButton();
        jBtnLink = new javax.swing.JButton();
        jBtnDrop = new javax.swing.JButton();
        jBtnDone = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPnlInstancesToAdd = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableInstancesToAdd = new javax.swing.JTable();
        jPnlProblemCausing = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableProblemCausing = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(600, 450));
        setName("Form"); // NOI18N

        jPnlToolBtns.setName("jPnlToolBtns"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCAddInstanceErrorDialog.class);
        jBtnAdd.setText(resourceMap.getString("jBtnAdd.text")); // NOI18N
        jBtnAdd.setName("jBtnAdd"); // NOI18N
        jBtnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnAddActionPerformed(evt);
            }
        });

        jBtnLink.setText(resourceMap.getString("jBtnLink.text")); // NOI18N
        jBtnLink.setName("jBtnLink"); // NOI18N
        jBtnLink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLinkActionPerformed(evt);
            }
        });

        jBtnDrop.setText(resourceMap.getString("jBtnDrop.text")); // NOI18N
        jBtnDrop.setName("jBtnDrop"); // NOI18N
        jBtnDrop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnDropActionPerformed(evt);
            }
        });

        jBtnDone.setText(resourceMap.getString("jBtnDone.text")); // NOI18N
        jBtnDone.setName("jBtnDone"); // NOI18N
        jBtnDone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnDoneActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPnlToolBtnsLayout = new javax.swing.GroupLayout(jPnlToolBtns);
        jPnlToolBtns.setLayout(jPnlToolBtnsLayout);
        jPnlToolBtnsLayout.setHorizontalGroup(
            jPnlToolBtnsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlToolBtnsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jBtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jBtnLink, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jBtnDrop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 182, Short.MAX_VALUE)
                .addComponent(jBtnDone, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPnlToolBtnsLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBtnAdd, jBtnDrop, jBtnLink});

        jPnlToolBtnsLayout.setVerticalGroup(
            jPnlToolBtnsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlToolBtnsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPnlToolBtnsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBtnAdd)
                    .addComponent(jBtnLink)
                    .addComponent(jBtnDrop)
                    .addComponent(jBtnDone))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPnlToolBtnsLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBtnAdd, jBtnDrop, jBtnLink});

        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setName("jSplitPane1"); // NOI18N
        jSplitPane1.setPreferredSize(new java.awt.Dimension(600, 518));
        jSplitPane1.setRequestFocusEnabled(false);

        jPnlInstancesToAdd.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPnlInstancesToAdd.border.title"))); // NOI18N
        jPnlInstancesToAdd.setName("jPnlInstancesToAdd"); // NOI18N
        jPnlInstancesToAdd.setPreferredSize(new java.awt.Dimension(300, 516));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTableInstancesToAdd.setModel(new javax.swing.table.DefaultTableModel(
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
        jTableInstancesToAdd.setName("jTableInstancesToAdd"); // NOI18N
        jScrollPane1.setViewportView(jTableInstancesToAdd);

        javax.swing.GroupLayout jPnlInstancesToAddLayout = new javax.swing.GroupLayout(jPnlInstancesToAdd);
        jPnlInstancesToAdd.setLayout(jPnlInstancesToAddLayout);
        jPnlInstancesToAddLayout.setHorizontalGroup(
            jPnlInstancesToAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 237, Short.MAX_VALUE)
            .addGroup(jPnlInstancesToAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPnlInstancesToAddLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPnlInstancesToAddLayout.setVerticalGroup(
            jPnlInstancesToAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 298, Short.MAX_VALUE)
            .addGroup(jPnlInstancesToAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPnlInstancesToAddLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jSplitPane1.setLeftComponent(jPnlInstancesToAdd);

        jPnlProblemCausing.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPnlProblemCausing.border.title"))); // NOI18N
        jPnlProblemCausing.setName("jPnlProblemCausing"); // NOI18N
        jPnlProblemCausing.setPreferredSize(new java.awt.Dimension(400, 516));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTableProblemCausing.setModel(new javax.swing.table.DefaultTableModel(
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
        jTableProblemCausing.setName("jTableProblemCausing"); // NOI18N
        jScrollPane2.setViewportView(jTableProblemCausing);

        javax.swing.GroupLayout jPnlProblemCausingLayout = new javax.swing.GroupLayout(jPnlProblemCausing);
        jPnlProblemCausing.setLayout(jPnlProblemCausingLayout);
        jPnlProblemCausingLayout.setHorizontalGroup(
            jPnlProblemCausingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlProblemCausingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPnlProblemCausingLayout.setVerticalGroup(
            jPnlProblemCausingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlProblemCausingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPnlProblemCausing);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPnlToolBtns, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPnlToolBtns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBtnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnAddActionPerformed
        int[] rows = jTableInstancesToAdd.getSelectedRows();
        jTableInstancesToAdd.clearSelection();
        for (int i = 0; i < rows.length; i++) {
            rows[i] = jTableInstancesToAdd.convertRowIndexToModel(rows[i]);
        }
        controller.add(rows);
        //controller.remove(rows);
        dupErrorModel.fireTableDataChanged();
        toAddModel.fireTableDataChanged();
    }//GEN-LAST:event_jBtnAddActionPerformed

    private void jBtnDoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnDoneActionPerformed
        this.dispose();
    }//GEN-LAST:event_jBtnDoneActionPerformed

    private void jBtnDropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnDropActionPerformed
        int[] rows = jTableInstancesToAdd.getSelectedRows();      
        jTableInstancesToAdd.clearSelection();
        for (int i = 0; i < rows.length; i++) {
            rows[i] = jTableInstancesToAdd.convertRowIndexToModel(rows[i]);
        }
        controller.remove(rows);
        dupErrorModel.fireTableDataChanged();
        toAddModel.fireTableDataChanged();
    }//GEN-LAST:event_jBtnDropActionPerformed

    private void jBtnLinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLinkActionPerformed
        jTableInstancesToAdd.clearSelection();
        try {
            controller.link(dupErrorModel.getSelected());
        } catch (SQLException ex) {
            Logger.getLogger(EDACCAddInstanceErrorDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        dupErrorModel.fireTableDataChanged();
        toAddModel.fireTableDataChanged();

    }//GEN-LAST:event_jBtnLinkActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                EDACCAddInstanceErrorDialog dialog = new EDACCAddInstanceErrorDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    public void initialize(HashMap<Instance, ArrayList<Instance>> duplicate, HashMap<Instance, InstanceClass> instanceClasses) {
        controller = new AddInstanceErrorController(duplicate, this, instanceClasses);


        // initialize the ProblemCausing (Duplicate Instance) table

        dupErrorModel = controller.getDuplicateModel();
        jTableProblemCausing.setModel(dupErrorModel);

        jTableProblemCausing.setRowSorter(new TableRowSorter<InstanceDupErrorTableModel>(dupErrorModel));
        rowSorter = (TableRowSorter<? extends InstanceDupErrorTableModel>) jTableProblemCausing.getRowSorter();
        InstanceDupErrorFilter rowFilter = new InstanceDupErrorFilter(dupErrorModel);
        rowSorter.setRowFilter(rowFilter);
        controller.setFilter(rowFilter);
        InstanceDupErrorTableRenderer duprenderer = new InstanceDupErrorTableRenderer(controller, dupErrorModel);
        jTableProblemCausing.setDefaultRenderer(String.class, duprenderer);


        //jTableProblemCausing.setRowSorter(controller.getDuplicateSorter());

        // initialize the InstanceToAdd table
        this.toAddModel = controller.getToAddModel();
        jTableInstancesToAdd.setModel(toAddModel);
        jTableInstancesToAdd.setRowSorter(new TableRowSorter<InstanceErrorTableModel>(toAddModel));
        InstanceErrorTableCellRenderer renderer = new InstanceErrorTableCellRenderer(controller);
        jTableInstancesToAdd.setDefaultRenderer(String.class, renderer);
        jTableInstancesToAdd.getSelectionModel().addListSelectionListener(new InstancesToAddSelectionListener(controller));

        this.dupErrorModel.fireTableDataChanged();
        this.toAddModel.fireTableDataChanged();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnAdd;
    private javax.swing.JButton jBtnDone;
    private javax.swing.JButton jBtnDrop;
    private javax.swing.JButton jBtnLink;
    private javax.swing.JPanel jPnlInstancesToAdd;
    private javax.swing.JPanel jPnlProblemCausing;
    private javax.swing.JPanel jPnlToolBtns;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTableInstancesToAdd;
    private javax.swing.JTable jTableProblemCausing;
    // End of variables declaration//GEN-END:variables

    public int getSelectedToAddInstance() {
        return jTableInstancesToAdd.convertRowIndexToModel(jTableInstancesToAdd.getSelectedRow());
    }

    public void sort() {
        this.rowSorter.sort();
    }

    public boolean isSelected() {
        return jTableInstancesToAdd.getSelectedRowCount() != 0;
    }

    public void multipleSelecteBtnShow(boolean b) {
        if(isSelected()){
            this.jBtnLink.setEnabled(!b);
            this.jBtnAdd.setEnabled(true);
            this.jBtnDrop.setEnabled(true);
        }else {
            this.jBtnLink.setEnabled(false);
            this.jBtnAdd.setEnabled(false);
            this.jBtnDrop.setEnabled(false);
        }
        
    }

    public int getSelectedToAddRowCount() {
        return this.jTableInstancesToAdd.getSelectedRowCount();
    }

    public int getToAddSelectedInstance() {
        if(jTableInstancesToAdd.getSelectedRowCount() == 1){
            return jTableInstancesToAdd.convertRowIndexToModel(this.jTableInstancesToAdd.getSelectedRow());
        } else
            return -1;
       
    }

    public int[] getToAddSelectedInstances() {
        int[] convertedRows = this.jTableInstancesToAdd.getSelectedRows();
        for(int i = 0; i < convertedRows.length;i++ ){
            convertedRows[i] = jTableInstancesToAdd.convertRowIndexToModel(convertedRows[i]);
        }
        return convertedRows;
    }

    public int ToAddTableConvertRowToModel(int row) {
        return jTableInstancesToAdd.convertRowIndexToModel(row);
    }

    @Override
    public void onTaskSuccessful(String methodName, Object result) {
        toAddModel.fireTableDataChanged();
        dupErrorModel.fireTableDataChanged();
    }

    @Override
    public void onTaskStart(String methodName) {
    }

    @Override
    public void onTaskFailed(String methodName, Throwable e) {
        try {
            throw e;
        } catch (Throwable ex) {
            Logger.getLogger(EDACCAddInstanceErrorDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
