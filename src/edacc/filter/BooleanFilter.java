/*
 * BooleanFilter.java
 *
 * Created on 12.10.2010, 09:42:14
 */

package edacc.filter;

import javax.swing.ButtonGroup;

/**
 * This class implements a boolean filter.
 * @author simon
 */
public class BooleanFilter extends javax.swing.JPanel implements FilterInterface {
    private ButtonGroup btnGroup;
    private boolean valTrue;
    private boolean valFalse;
    /** Creates new form BooleanFilter */
    public BooleanFilter(String name) {
        initComponents();
        btnGroup = new ButtonGroup();
        lblName.setText(name);
        btnGroup.add(radioTrue);
        btnGroup.add(radioFalse);
        radioTrue.setSelected(true);
        valTrue = true;
        valFalse = true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        radioTrue = new javax.swing.JRadioButton();
        radioFalse = new javax.swing.JRadioButton();
        lblName = new javax.swing.JLabel();

        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(198, 20));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(BooleanFilter.class);
        radioTrue.setText(resourceMap.getString("radioTrue.text")); // NOI18N
        radioTrue.setName("radioTrue"); // NOI18N
        radioTrue.setPreferredSize(new java.awt.Dimension(47, 20));

        radioFalse.setText(resourceMap.getString("radioFalse.text")); // NOI18N
        radioFalse.setName("radioFalse"); // NOI18N
        radioFalse.setPreferredSize(new java.awt.Dimension(51, 20));

        lblName.setText(resourceMap.getString("lblName.text")); // NOI18N
        lblName.setName("lblName"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblName)
                .addGap(66, 66, 66)
                .addComponent(radioTrue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioFalse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(radioFalse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(radioTrue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblName))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblName;
    private javax.swing.JRadioButton radioFalse;
    private javax.swing.JRadioButton radioTrue;
    // End of variables declaration//GEN-END:variables

    @Override
    public boolean include(Object value) {
        if (value instanceof Boolean) {
            return valTrue && (Boolean) value || valFalse && !(Boolean) value;
        }
        return true;
    }


    public static boolean accept(Class<?> clazz) {
        return clazz == Boolean.class;
    }

    @Override
    public void apply() {
        valTrue = radioTrue.isSelected();
        valFalse = radioFalse.isSelected();
    }

    @Override
    public void undo() {
        radioTrue.setSelected(valTrue);
        radioFalse.setSelected(valFalse);
    }
}
