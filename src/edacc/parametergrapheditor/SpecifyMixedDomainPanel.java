/*
 * SpecifyMixedDomainPanel.java
 *
 * Created on 03.07.2011, 18:27:59
 */
package edacc.parametergrapheditor;

import edacc.parameterspace.domain.Domain;
import edacc.parameterspace.domain.MixedDomain;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.JComboBox;

/**
 *
 * @author simon
 */
public class SpecifyMixedDomainPanel extends javax.swing.JPanel implements IDomainPanel {

    GridBagConstraints c;
    GridBagLayout layout;

    /** Creates new form SpecifyMixedDomainPanel */
    public SpecifyMixedDomainPanel() {
        initComponents();
        layout = new GridBagLayout();
        setLayout(layout);
        c = new GridBagConstraints();
        SpecifyDomainPanel panel = new SpecifyDomainPanel();
        panel.comboDomain.removeItem(MixedDomain.name);
        panel.comboDomain.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                comboDomainActionPerformed(e);
            }
        });
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 0.5;
        c.weighty = .5;
        c.anchor = GridBagConstraints.PAGE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(panel, c);
        setGridBagConstraints();
        invalidate();
        revalidate();
        repaint();
    }

    private void setGridBagConstraints() {
        c.gridy = 0;
        c.weighty = 1;
        for (int i = 0; i < this.getComponentCount(); i++) {
            c.gridy++;
            c.weighty *= 1000;
            layout.setConstraints(this.getComponent(i), c);
        }
    }

    private void comboDomainActionPerformed(ActionEvent e) {
        boolean found = false;
        for (int i = this.getComponentCount() - 1; i >= 0; i--) {
            SpecifyDomainPanel panel = (SpecifyDomainPanel) getComponent(i);
            if ("".equals(panel.comboDomain.getSelectedItem())) {
                if (found) {
                    this.remove(i);
                }
                found = true;
            }
        }
        if (!found) {
            SpecifyDomainPanel panel = new SpecifyDomainPanel();
            panel.comboDomain.removeItem(MixedDomain.name);
            panel.comboDomain.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    comboDomainActionPerformed(e);
                }
            });
            add(panel, c);
        }
        setGridBagConstraints();
        invalidate();
        revalidate();
        repaint();
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

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(SpecifyMixedDomainPanel.class);
        setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("Form.border.title"))); // NOI18N
        setName("Form"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 26, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public Domain getDomain() throws InvalidDomainException {
        if (getComponentCount() == 1) {
            throw new InvalidDomainException("You must add at least one domain to a mixed domain.");
        }
        LinkedList<Domain> domains = new LinkedList<Domain>();

        // last domain is an invalid domain (-1)
        for (int i = 0; i < getComponentCount() - 1; i++) {
            domains.add(((IDomainPanel) getComponent(i)).getDomain());
        }
        return new MixedDomain(domains);
    }

    @Override
    public void setDomain(Domain domain) {
        if (!(domain instanceof MixedDomain)) {
            return;
        }
        for (Domain d : ((MixedDomain) domain).getDomains()) {
            for (int i = getComponentCount() - 1; i >= 0; i--) {
                if (getComponent(i) instanceof SpecifyDomainPanel && "".equals(((SpecifyDomainPanel) getComponent(i)).comboDomain.getSelectedItem())) {
                    ((SpecifyDomainPanel) getComponent(i)).comboDomain.setSelectedItem(d.getName());
                    ((SpecifyDomainPanel) getComponent(i)).setDomain(d);
                    break;
                }
            }
        }
    }
}
