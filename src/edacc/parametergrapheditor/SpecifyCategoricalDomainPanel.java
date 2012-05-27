/*
 * SpecifyCategoricalDomainPanel.java
 *
 * Created on 03.07.2011, 18:27:38
 */
package edacc.parametergrapheditor;

import edacc.EDACCApp;
import edacc.parameterspace.Parameter;
import edacc.parameterspace.domain.CategoricalDomain;
import edacc.parameterspace.domain.Domain;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author simon
 */
public class SpecifyCategoricalDomainPanel extends javax.swing.JPanel implements IDomainPanel {

    DefaultListModel model;
    SpecifyDomainDialog main;
    /** Creates new form SpecifyCategoricalDomainPanel */
    public SpecifyCategoricalDomainPanel(SpecifyDomainDialog main) {
        initComponents();
        this.main = main;
        model = new DefaultListModel();
        model.clear();
        listCategories.setModel(model);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        listCategories = new javax.swing.JList();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnAddMultiple = new javax.swing.JButton();
        btnCopyToClipboard = new javax.swing.JButton();
        btnUseDomain = new javax.swing.JButton();

        setName("Form"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        listCategories.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listCategories.setName("listCategories"); // NOI18N
        jScrollPane1.setViewportView(listCategories);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(SpecifyCategoricalDomainPanel.class);
        btnAdd.setText(resourceMap.getString("btnAdd.text")); // NOI18N
        btnAdd.setName("btnAdd"); // NOI18N
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnRemove.setText(resourceMap.getString("btnRemove.text")); // NOI18N
        btnRemove.setName("btnRemove"); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        btnAddMultiple.setText(resourceMap.getString("btnAddMultiple.text")); // NOI18N
        btnAddMultiple.setName("btnAddMultiple"); // NOI18N
        btnAddMultiple.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddMultipleActionPerformed(evt);
            }
        });

        btnCopyToClipboard.setText(resourceMap.getString("btnCopyToClipboard.text")); // NOI18N
        btnCopyToClipboard.setName("btnCopyToClipboard"); // NOI18N
        btnCopyToClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyToClipboardActionPerformed(evt);
            }
        });

        btnUseDomain.setText(resourceMap.getString("btnUseDomain.text")); // NOI18N
        btnUseDomain.setName("btnUseDomain"); // NOI18N
        btnUseDomain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUseDomainActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnRemove)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(btnUseDomain)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCopyToClipboard)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddMultiple)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAdd)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnRemove)
                    .addComponent(btnAddMultiple)
                    .addComponent(btnCopyToClipboard)
                    .addComponent(btnUseDomain))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        String cat = JOptionPane.showInputDialog("Category:");
        if (cat != null && !"".equals(cat)) {
            if (!model.contains(cat)) {
                model.addElement(cat);
            }
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        for (Object o : listCategories.getSelectedValues()) {
            model.removeElement(o);
        }
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnAddMultipleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddMultipleActionPerformed
        MultipleCategoriesDialog dialog = new MultipleCategoriesDialog(EDACCApp.getApplication().getMainFrame(), true);
        dialog.setName("MultipleCategoriesDialog");
        EDACCApp.getApplication().show(dialog);
        List<String> categories;
        if ((categories = dialog.getCategories()) != null) {
            for (String s : categories) {
                if (!"".equals(s) && !model.contains(s)) {
                    model.addElement(s);
                }
            }
        }
    }//GEN-LAST:event_btnAddMultipleActionPerformed

    private void btnCopyToClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopyToClipboardActionPerformed
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < model.size(); i++) {
            sb.append((String) model.getElementAt(i)).append('\n');
        }
        StringSelection ss = new StringSelection(sb.toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }//GEN-LAST:event_btnCopyToClipboardActionPerformed

    private void btnUseDomainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUseDomainActionPerformed
        useDomain();
    }//GEN-LAST:event_btnUseDomainActionPerformed

    
    public void useDomain() {
        UseDomainDialog dialog = new UseDomainDialog(EDACCApp.getApplication().getMainFrame(), true, main.getParameters(), CategoricalDomain.class);
        dialog.setName("UseDomainDialog");
        EDACCApp.getApplication().show(dialog);
        Parameter p;
        if ((p = dialog.getSelectedItem()) != null && p.getDomain() instanceof CategoricalDomain) {
            this.setDomain(p.getDomain(), null);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddMultiple;
    private javax.swing.JButton btnCopyToClipboard;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnUseDomain;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList listCategories;
    // End of variables declaration//GEN-END:variables

    @Override
    public Domain getDomain() throws InvalidDomainException {
        if (model.isEmpty()) {
            throw new InvalidDomainException("You must specify at least one category for categorical domain.");
        }
        String[] categories = new String[model.size()];
        for (int i = 0; i < model.size(); i++) {
            categories[i] = (String) model.get(i);
        }
        return new CategoricalDomain(categories);
    }

    @Override
    public void setDomain(Domain orDomain, Domain andDomain) {
        if (!(orDomain instanceof CategoricalDomain) || andDomain != null) {
            return;
        }
        model.clear();
        for (String s : ((CategoricalDomain) orDomain).getCategories()) {
            model.addElement(s);
        }
    }
}
