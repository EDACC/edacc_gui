/*
 * EDACCExtendedWarning.java
 * Creates an extended Warning Dialog that can be used like the normal JOptionPane Dialog, but has an additional Componenet,
 * where informtion can be dispalyed.
 * Default additional display-component is a JTextArea.
 * The dialog is always modal.
 *
 * Created on 18.06.2010, 12:34:58
 */
package edacc;

import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.*;

/**
 *
 * @author balint
 */
public class EDACCExtendedWarning extends javax.swing.JDialog {

    /**Which Buttons should appear in the Dialog,
     * only OK (for Warnings)*/
    public static final int OK_OPTIONS = 0;
    /** Ok and Cancel Buttons are displayed */
    public static final int OK_CANCEL_OPTIONS = 1;
    //
    // Return values.
    //
    /** Return value form class method if CANCEL is chosen. */
    public static final int RET_CANCEL_OPTION = 1;
    /** Return value form class method if OK is chosen. */
    public static final int RET_OK_OPTION = 0;
    /** Return value from class method if user closes window without selecting anything*/
    public static final int RET_CLOSE_OPTION = -1;

    /** 
     * Creates a new form EDACCExtendedWarning with title : @param title and disaplyes either the text ot the component scrollMe in a JscrollPane
     * @param parent - the parent frame
     * @param title - the title of the dialog
     * @param message - is the warning message - a short summary
     * @param text - is the extended text to be displayed in a JTextArea
     * @param scrollMe - is the Component to be scrolled
     *
     * @return one of EDACCExtendedWarning.RET_CANCEL_OPTION, EDACCExtendedWarning.RET_OK_OPTION, EDACCExtendedWarning.RET_CLOSEDL_OPTION
     */
    private EDACCExtendedWarning(int type, java.awt.Frame parent, String title, String message, String extendedText, Component extendedMessageComponent) {
        super(parent, true);
        initComponents();
        this.setTitle(title);
        this.textAreaMessages.setEditable(false);
        this.lbWarningMessage.setText(message);
        this.lbWarningMessage.setHorizontalAlignment(SwingConstants.CENTER);
        this.lbWarningMessage.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
        if (extendedText.equalsIgnoreCase("")) {
            this.scrollPaneMessages.setViewportView(extendedMessageComponent);
        } else {
            this.textAreaMessages.setText(extendedText);
        }
        FlowLayout fl=(FlowLayout) this.jPanel1.getLayout();
        fl.setHgap(20);
        if (type==OK_OPTIONS)
            this.jPanel1.remove(this.cancelButton);
        this.jPanel1.setLayout(fl);
        this.setSize(640, 480);
        this.setAlwaysOnTop(true);
    }


    /** @return the return status of this dialog - one of RET_OK_OPTION, RET_CANCEL_OPTION or RET_CLOSED_OPTION*/
    private int getReturnStatus() {
        return returnStatus;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbWarningMessage = new javax.swing.JLabel();
        scrollPaneMessages = new javax.swing.JScrollPane();
        textAreaMessages = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(EDACCExtendedWarning.class);
        lbWarningMessage.setText(resourceMap.getString("lbWarningMessage.text")); // NOI18N
        lbWarningMessage.setName("lbWarningMessage"); // NOI18N

        scrollPaneMessages.setName("scrollPaneMessages"); // NOI18N

        textAreaMessages.setColumns(20);
        textAreaMessages.setRows(5);
        textAreaMessages.setName("textAreaMessages"); // NOI18N
        scrollPaneMessages.setViewportView(textAreaMessages);

        jPanel1.setName("jPanel1"); // NOI18N

        okButton.setText(resourceMap.getString("okButton.text")); // NOI18N
        okButton.setName("okButton"); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanel1.add(okButton);

        cancelButton.setText(resourceMap.getString("cancelButton.text")); // NOI18N
        cancelButton.setName("cancelButton"); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel1.add(cancelButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbWarningMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                    .addComponent(scrollPaneMessages, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbWarningMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneMessages, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doClose(RET_OK_OPTION);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL_OPTION);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CLOSE_OPTION);
    }//GEN-LAST:event_closeDialog

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
     * Displays a new form EDACCExtendedWarning dialog with :
     * @param type  one of OK_OPTIONS or OK_CANCEL_OPTIONS
     * @param parent  the parent frame
     * @param message  is the warning message - a short summary
     * @param title  the title of the dialog
     * @param extendedText  is the extended text to be displayed in a JTextArea
     *
     * @return one of:
     * EDACCExtendedWarning.RET_CANCEL_OPTION, EDACCExtendedWarning.RET_OK_OPTION, EDACCExtendedWarning.RET_CLOSE_OPTION
     */
    public static int showMessageDialog(int type,java.awt.Frame parent,  String message, String title,String extendedText) {
        return showMessageDialog(type,parent, title, message, extendedText, null);
    }

    /**
     * Displays a new form EDACCExtendedWarning with title : "Warning!"
     * @param type  one of OK_OPTIONS or OK_CANCEL_OPTIONS
     * @param parent  the parent frame
     * @param message  is the warning message - a short summary
     * @param extendedText  is the extended text to be displayed in a JTextArea
     *
     * @return one of:
     * EDACCExtendedWarning.RET_CANCEL_OPTION, EDACCExtendedWarning.RET_OK_OPTION, EDACCExtendedWarning.RET_CLOSE_OPTION
     */
    public static int showMessageDialog(int type,java.awt.Frame parent, String message, String extendedText) {
        return showMessageDialog(type,parent, "Warning!", message, extendedText, null);
    }

    /**
     * Creates a new form EDACCExtendedWarning with title : "Warning!"
     * @param type  one of OK_OPTIONS or OK_CANCEL_OPTIONS
     * @param parent  the parent frame
     * @param message is the warning message - a short summary
     * @param extendedMessageComponent is the Component where the extended content will be dispalyed inside a JScrollPane
     *
     * @return one of:
     * EDACCExtendedWarning.RET_CANCEL_OPTION, EDACCExtendedWarning.RET_OK_OPTION, EDACCExtendedWarning.RET_CLOSE_OPTION
     */
    public static int showMessageDialog(int type,java.awt.Frame parent, String message, Component extendedMessageComponent) {
        return showMessageDialog(type,parent, "Warning!", message, "", extendedMessageComponent);
    }

    /**
     * Creates a new form EDACCExtendedWarning with title : title
     * @param type  one of OK_OPTIONS or OK_CANCEL_OPTIONS
     * @param parent  the parent frame
    * @param message  is the warning message - a short summary
     * @param title  the title of the dialog
     * @param extendedMessageComponent is the Component where the extended content will be dispalyed inside a JScrollPane
     *
     * @return one of:
     * EDACCExtendedWarning.RET_CANCEL_OPTION, EDACCExtendedWarning.RET_OK_OPTION, EDACCExtendedWarning.RET_CLOSE_OPTION
     */
    public static int showMessageDialog(int type,java.awt.Frame parent,  String message, String title,Component extendedMessageComponent) {
        return showMessageDialog(type,parent, title, message, "", extendedMessageComponent);
    }

    private static int showMessageDialog(int type,java.awt.Frame parent, String title, String message, String extendedText, Component extendedMessageComponent) {
        EDACCExtendedWarning warning = new EDACCExtendedWarning(type,parent, title, message, extendedText, extendedMessageComponent);
        warning.setLocationRelativeTo(parent);
        warning.setVisible(true);
        warning.dispose();
        return warning.getReturnStatus();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbWarningMessage;
    private javax.swing.JButton okButton;
    private javax.swing.JScrollPane scrollPaneMessages;
    private javax.swing.JTextArea textAreaMessages;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL_OPTION;
}
