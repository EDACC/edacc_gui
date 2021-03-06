/*
 * ArgumentPanel.java
 *
 * Created on 13.10.2010, 22:54:03
 */

package edacc.filter;

import edacc.EDACCFilter;
import javax.swing.JPanel;
import org.jdesktop.application.Action;

/**
 * This is a container for an FilterInterface.
 * @author simon
 */
public class ArgumentPanel extends javax.swing.JPanel {
    private EDACCFilter filter;
    private FilterInterface filterInterface;
    private int argNum, column;
    /** Creates new form ArgumentPanel */
    public ArgumentPanel(EDACCFilter filter, FilterInterface filterInterface, int argNum, int column) {
        initComponents();
        if (!(filterInterface instanceof JPanel)) {
            throw new IllegalArgumentException();
        }
        this.filter = filter;
        this.argNum = argNum;
        this.column = column;
        this.filterInterface = filterInterface;
        lblArgNum.setText("$" + argNum + ":");
        pnlArgument.setLayout(new java.awt.BorderLayout());
        pnlArgument.add((JPanel) filterInterface, java.awt.BorderLayout.CENTER);
    }

    public int getArgNum() {
        return argNum;
    }

    public int getColumn() {
        return column;
    }

    public FilterInterface getFilterInterface() {
        return filterInterface;
    }

    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblArgNum = new javax.swing.JLabel();
        pnlArgument = new javax.swing.JPanel();
        btnRemove = new javax.swing.JButton();

        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(ArgumentPanel.class);
        lblArgNum.setText(resourceMap.getString("lblArgNum.text")); // NOI18N
        lblArgNum.setName("lblArgNum"); // NOI18N
        lblArgNum.setPreferredSize(new java.awt.Dimension(34, 19));

        pnlArgument.setName("pnlArgument"); // NOI18N

        javax.swing.GroupLayout pnlArgumentLayout = new javax.swing.GroupLayout(pnlArgument);
        pnlArgument.setLayout(pnlArgumentLayout);
        pnlArgumentLayout.setHorizontalGroup(
            pnlArgumentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 75, Short.MAX_VALUE)
        );
        pnlArgumentLayout.setVerticalGroup(
            pnlArgumentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getActionMap(ArgumentPanel.class, this);
        btnRemove.setAction(actionMap.get("btnRemove")); // NOI18N
        btnRemove.setText(resourceMap.getString("btnRemove.text")); // NOI18N
        btnRemove.setName("btnRemove"); // NOI18N
        btnRemove.setPreferredSize(new java.awt.Dimension(71, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblArgNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlArgument, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlArgument, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblArgNum, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
            .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void btnRemove() {
        filter.remove(this);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRemove;
    private javax.swing.JLabel lblArgNum;
    private javax.swing.JPanel pnlArgument;
    // End of variables declaration//GEN-END:variables

}
