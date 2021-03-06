/*
 * SelectCatgoricalDomainPanel.java
 *
 * Created on 04.07.2011, 11:00:22
 */
package edacc.parametergrapheditor;

import edacc.parameterspace.domain.CategoricalDomain;
import edacc.parameterspace.domain.Domain;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author simon
 */
public class SelectCategoricalDomainPanel extends javax.swing.JPanel implements IDomainPanel {

    CategoricalDomainTableModel model;

    public SelectCategoricalDomainPanel(List<String> categories) {
        initComponents();
        model = new CategoricalDomainTableModel(categories);
        tblCategories.setModel(model);        
    }
    
    /** Creates new form SelectCatgoricalDomainPanel */
    public SelectCategoricalDomainPanel(CategoricalDomain domain) {
        LinkedList<String> list = new LinkedList<String>();
        for (String s : domain.getCategories()) {
            list.add(s);
        }
        initComponents();
        model = new CategoricalDomainTableModel(list);
        tblCategories.setModel(model);         
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
        tblCategories = new javax.swing.JTable();
        btnSelectAll = new javax.swing.JButton();
        btnDeselectAll = new javax.swing.JButton();
        btnInvertSelection = new javax.swing.JButton();

        setName("Form"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblCategories.setAutoCreateRowSorter(true);
        tblCategories.setModel(new javax.swing.table.DefaultTableModel(
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
        tblCategories.setName("tblCategories"); // NOI18N
        jScrollPane1.setViewportView(tblCategories);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(SelectCategoricalDomainPanel.class);
        btnSelectAll.setText(resourceMap.getString("btnSelectAll.text")); // NOI18N
        btnSelectAll.setName("btnSelectAll"); // NOI18N
        btnSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectAllActionPerformed(evt);
            }
        });

        btnDeselectAll.setText(resourceMap.getString("btnDeselectAll.text")); // NOI18N
        btnDeselectAll.setName("btnDeselectAll"); // NOI18N
        btnDeselectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeselectAllActionPerformed(evt);
            }
        });

        btnInvertSelection.setText(resourceMap.getString("btnInvertSelection.text")); // NOI18N
        btnInvertSelection.setName("btnInvertSelection"); // NOI18N
        btnInvertSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInvertSelectionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSelectAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDeselectAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnInvertSelection)
                .addContainerGap(368, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 717, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnDeselectAll, btnInvertSelection, btnSelectAll});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelectAll)
                    .addComponent(btnDeselectAll)
                    .addComponent(btnInvertSelection)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectAllActionPerformed
        for (int row = 0; row < tblCategories.getRowCount(); row++) {
            model.setSelected(tblCategories.convertRowIndexToModel(row), true);
        }
        model.fireTableDataChanged();
    }//GEN-LAST:event_btnSelectAllActionPerformed

    private void btnDeselectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeselectAllActionPerformed
        for (int row = 0; row < tblCategories.getRowCount(); row++) {
            model.setSelected(tblCategories.convertRowIndexToModel(row), false);
        }
        model.fireTableDataChanged();
    }//GEN-LAST:event_btnDeselectAllActionPerformed

    private void btnInvertSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInvertSelectionActionPerformed
        for (int row = 0; row < tblCategories.getRowCount(); row++) {
            model.setSelected(tblCategories.convertRowIndexToModel(row), !model.isSelected(tblCategories.convertRowIndexToModel(row)));
        }
        model.fireTableDataChanged();
    }//GEN-LAST:event_btnInvertSelectionActionPerformed

    protected List<String> getSelectedCategories() {
        List<String> res = new LinkedList<String>();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.isSelected(i)) {
                res.add(model.getCategoryAt(i));
            }
        }
        return res;
    }
    
    protected void setSelectedCategories(Set<String> catSet) {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setSelected(i, catSet.contains(model.getCategoryAt(i)));
        }
        model.fireTableDataChanged();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeselectAll;
    private javax.swing.JButton btnInvertSelection;
    private javax.swing.JButton btnSelectAll;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCategories;
    // End of variables declaration//GEN-END:variables

    @Override
    public Domain getDomain() throws InvalidDomainException {
        List<String> list = getSelectedCategories();
        if (list.isEmpty()) {
            throw new InvalidDomainException("You must select at least one category for categorical domain.");
        }
        return new CategoricalDomain(list.toArray(new String[0]));
    }

    @Override
    public void setDomain(Domain orDomain, Domain andDomain) throws InvalidDomainException {
        if (!(orDomain instanceof CategoricalDomain) || !(andDomain instanceof CategoricalDomain)) {
            throw new InvalidDomainException("Got domains " + orDomain.getName() + "/" + andDomain.getName() + ", expected categorical/categorical domains.");
        }
        setSelectedCategories(((CategoricalDomain) andDomain).getCategories());
    }
    
    private class CategoricalDomainTableModel extends DefaultTableModel {

        private final String[] columns = {"Category", "Selected"};
        private String[] categories;
        private boolean[] selected;

        public CategoricalDomainTableModel(List<String> categories) {
            this.categories = categories.toArray(new String[0]);
            this.selected = new boolean[categories.size()];
            for (int i = 0; i < selected.length; i++) {
                selected[i] = false;
            }
        }

        public boolean isSelected(int rowIndex) {
            return selected[rowIndex];
        }

        public void setSelected(int rowIndex, boolean sel) {
            selected[rowIndex] = sel;
        }

        public String getCategoryAt(int rowIndex) {
            return categories[rowIndex];
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public int getRowCount() {
            return categories == null ? 0 : categories.length;
        }

        @Override
        public Object getValueAt(int row, int column) {
            if (column == 0) {
                return categories[row];
            } else if (column == 1) {
                return selected[row];
            } else {
                return "";
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return String.class;
            } else if (columnIndex == 1) {
                return Boolean.class;
            } else {
                return String.class;
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 1 ? true : false;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            if (column == 1) {
                selected[row] = (Boolean) aValue;
            }
        }
    }
}
