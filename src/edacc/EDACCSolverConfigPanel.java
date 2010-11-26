/*
 * EDACCSolverConfigPanel.java
 *
 * Created on 30.12.2009, 21:31:12
 */
package edacc;

import edacc.model.Solver;
import edacc.model.SolverConfiguration;
import edacc.model.SolverConfigurationDAO;
import edacc.model.SolverDAO;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

import javax.swing.border.TitledBorder;

/**
 * A JPanel which serves as container for EDACCSolverConfigEntry objects.
 * @author simon
 * @see edacc.EDACCSolverConfigEntry
 */
public class EDACCSolverConfigPanel extends javax.swing.JPanel {

    private GridBagConstraints gridBagConstraints;
    private GridBagLayout layout;
    private EDACCExperimentMode parent;
    private boolean update;

    /** Creates new form EDACCSolverConfigPanel */
    public EDACCSolverConfigPanel() {
      //  initComponents();
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.anchor = GridBagConstraints.PAGE_START;

        this.parent = null;
        layout = new GridBagLayout();
        this.setLayout(layout);
        this.update = false;
    }

    public void setParent(EDACCExperimentMode parent) {
        this.parent = parent;
    }

    public void setTitles() {
        if (!update && parent != null) {
            parent.setTitles();
        }
    }

    private void doRepaint() {
        if (!update) {
            int oldId = -1;
            int count = 1;
            EDACCSolverConfigEntry old = null;
            for (int i = 0; i < this.getComponentCount(); i++) {
                EDACCSolverConfigEntry e = (EDACCSolverConfigEntry) this.getComponent(i);
                if (e.getSolverId() != oldId) {
                    if (old != null && count == 1) {
                        old.setTitleNumber(0);
                        setSolverConfigurationName(old);
                    }
                    oldId = e.getSolverId();
                    count = 1;
                } else {
                    count++;
                }
                e.setTitleNumber(count);
                setSolverConfigurationName(e);
                old = e;
            }
            if (old != null && count == 1) {
                old.setTitleNumber(0);
                setSolverConfigurationName(old);
            }
            this.repaint();
            this.revalidate();
            setTitles();
        }
    }

    /**
     * This will set the name of the solver configuration (in case it is not null) for the given Solver config entry.
     * @param e
     */
    public void setSolverConfigurationName(EDACCSolverConfigEntry e) {
        if (e.getSolverConfiguration() != null) {
            e.getSolverConfiguration().setName(((TitledBorder) e.getBorder()).getTitle());
        }
    }

    private int getIndex(int solverId) {
        int solverIndex = 0;
        int solverOrder[] = new int[parent.solTableModel.getRowCount()];
        for (int i = 0; i < parent.solTableModel.getRowCount(); i++) {
            solverOrder[i] = parent.solTableModel.getSolver(i).getId();
            if (solverId == parent.solTableModel.getSolver(i).getId()) {
                solverIndex = i;
            }
        }
        int currentIndex = 0;
        for (int i = 0; i < this.getComponents().length; i++) {
            EDACCSolverConfigEntry entry = (EDACCSolverConfigEntry) this.getComponents()[i];
            for (int j = currentIndex; j < solverOrder.length; j++) {
                if (entry.getSolverId() == solverOrder[j]) {
                    currentIndex = j;
                    break;
                }
            }
            if (currentIndex > solverIndex) {
                return i;
            }
        }
        return this.getComponentCount();
    }

    /**
     * Generates a new EDACCSolverConfigEntry for a solver.
     * @param o Solver
     */
    public void addSolver(Object o) {
        if (o instanceof Solver) {
            Solver solver = (Solver) o;
            try {
                EDACCSolverConfigEntry entry = new EDACCSolverConfigEntry(solver);
                entry.setParent(this);
                gridBagConstraints.gridy++;
                this.add(entry, getIndex(solver.getId()));
                setGridBagConstraints();
                parent.solTableModel.setSolverSelected(solver.getId(), true);
                doRepaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generates a new EDACCSolverConfigEntry for a solver configuration.
     * @param solverConfiguration
     * @throws SQLException
     */
    public void addSolverConfiguration(SolverConfiguration solverConfiguration) throws SQLException {
        EDACCSolverConfigEntry entry = new EDACCSolverConfigEntry(solverConfiguration);
        entry.setParent(this);
        this.add(entry, getIndex(solverConfiguration.getSolver_id()));
        parent.solTableModel.setSolverSelected(solverConfiguration.getSolver_id(), true);
        setGridBagConstraints();
        doRepaint();
    }

    /**
     * Copies an EDACCSolverConfigEntry and adds it after that entry.
     * @param entry
     * @throws SQLException
     */
    public void replicateEntry(EDACCSolverConfigEntry entry) throws SQLException {
        EDACCSolverConfigEntry repl = new EDACCSolverConfigEntry(SolverDAO.getById(entry.getSolverId()));
        repl.setParent(this);
        repl.assign(entry);
        int pos = 0;
        for (int i = 0; i < this.getComponentCount(); i++) {
            if (this.getComponent(i) == entry) {
                pos = i;
                break;
            }
        }
        this.add(repl, pos + 1);
        setGridBagConstraints();
        doRepaint();
    }

    private void setGridBagConstraints() {
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weighty = 1;
        for (int i = 0; i < this.getComponentCount(); i++) {
            gridBagConstraints.gridy++;
            gridBagConstraints.weighty *= 1000;
            layout.setConstraints(this.getComponent(i), gridBagConstraints);
        }
    }

    /**
     * Removes an EDACCSolverConfigEntry. If the solver configuration exists in the database
     * it is marked as deleted.
     * @param entry
     */
    public void removeEntry(EDACCSolverConfigEntry entry) {
        if (entry.getSolverConfiguration() != null) {
            SolverConfigurationDAO.removeSolverConfiguration(entry.getSolverConfiguration());
        }
        this.remove(entry);

        // if this was the last solver configuration for the corresponding solver
        // we will deselect the solver in the solvers table
        boolean lastSolver = true;
        for (Component c : this.getComponents()) {
            if (((EDACCSolverConfigEntry) c).getSolverId() == entry.getSolverId()) {
                lastSolver = false;
                break;
            }
        }
        if (lastSolver) {
            parent.solTableModel.setSolverSelected(entry.getSolverId(), false);
        }
        doRepaint();
    }

    /**
     * Removes every EDACCSolverConfigEntry which was generated with this solver.
     * @param o solver to be removed
     */
    public void removeSolver(Object o) {
        if (!(o instanceof Solver)) {
            return;
        }
        Solver solver = (Solver) o;
        Component[] c = this.getComponents();
        for (int i = c.length - 1; i >= 0; i--) {
            EDACCSolverConfigEntry e = (EDACCSolverConfigEntry) c[i];
            if (e.getSolverId() == solver.getId()) {
                this.removeEntry(e);
            }
        }
        doRepaint();
    }

    /**
     * Returns true if a EDACCSolverConfigEntry exists with this solverId
     * @param solverId
     * @return boolean
     */
    public boolean solverExists(int solverId) {
        for (int i = 0; i < this.getComponentCount(); i++) {
            if (((EDACCSolverConfigEntry) this.getComponent(i)).getSolverId() == solverId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prevents from updating the GUI until endUpdate() is called.
     */
    public void beginUpdate() {
        this.update = true;
    }

    /**
     * GUI will be updated after every single change.
     */
    public void endUpdate() {
        this.update = false;
        doRepaint();
    }

    /**
     * Checks for unsaved solver configurations
     * @return true, if and only if there is a unsaved solver configuration, false otherwise
     */
    public boolean isModified() {
        // checks for deleted entries
        if (SolverConfigurationDAO.isModified()) {
            return true;
        }
        // checks for changed entries
        for (Component comp : this.getComponents()) {
            if (comp instanceof EDACCSolverConfigEntry) {
                EDACCSolverConfigEntry entry = (EDACCSolverConfigEntry) comp;
                if (entry.isModified()) {
                    return true;
                }
            }
        }
        // ... unchanged
        return false;
    }

    @Override
    public void removeAll() {
        while (this.getComponents().length > 0) {
            removeEntry((EDACCSolverConfigEntry) this.getComponent(0));
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

        setName("Form"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
