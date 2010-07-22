/*
 * EDACCTaskView.java
 *
 * Created on 19.04.2010, 18:07:20
 */
package edacc;

import edacc.model.Tasks;
import javax.swing.SwingUtilities;
import org.jdesktop.application.Action;

/**
 *
 * @author simon
 */
public class EDACCTaskView extends javax.swing.JDialog {

    private Tasks task;

    /** Creates new form EDACCTaskView */
    public EDACCTaskView(java.awt.Frame parent, boolean modal, Tasks task) {
        super(parent, modal);
        initComponents();
        this.task = task;
        btnCancel.setVisible(false);
        progressBar.setMaximum(1000);
        progressBar.setIndeterminate(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        progressBar = new javax.swing.JProgressBar();
        lblMessage = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        lblOperationName = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setName("Form"); // NOI18N
        setResizable(false);

        progressBar.setName("progressBar"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCTaskView.class);
        lblMessage.setText(resourceMap.getString("lblMessage.text")); // NOI18N
        lblMessage.setMaximumSize(new java.awt.Dimension(32767, 14));
        lblMessage.setName("lblMessage"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getActionMap(EDACCTaskView.class, this);
        btnCancel.setAction(actionMap.get("btnCancel")); // NOI18N
        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setToolTipText(resourceMap.getString("btnCancel.toolTipText")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N

        lblOperationName.setText(resourceMap.getString("lblOperationName.text")); // NOI18N
        lblOperationName.setMaximumSize(new java.awt.Dimension(32767, 14));
        lblOperationName.setName("lblOperationName"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
                    .addComponent(lblOperationName, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblOperationName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancel)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblOperationName;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables

    public void setMessage(String description) {
        lblMessage.setText(description);
    }

    public void setOperationName(String name) {
        lblOperationName.setText(name);
    }

    public void setProgress(final double progress) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                if (progress == 0.) {
                    progressBar.setIndeterminate(true);
                } else {
                    if (progressBar.isIndeterminate()) {
                        progressBar.setIndeterminate(false);
                    }
                    progressBar.setValue((int) (progress * 10));
                }

            }
        });


    }

    public void setCancelable(boolean cancelable) {
        if (cancelable) {
            btnCancel.setVisible(true);
        } else {
            btnCancel.setVisible(false);
        }
    }

    @Action
    public void btnCancel() {
        btnCancel.setEnabled(false);
        task.cancel(false);
    }
}
