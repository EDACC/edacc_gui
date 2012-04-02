/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.experiment.tabs.solver.gui;

import edacc.EDACCApp;
import edacc.EDACCExperimentMode;
import edacc.experiment.ExperimentController;
import edacc.experiment.tabs.solver.EDACCSolverConfigEntryListener;
import edacc.experiment.tabs.solver.SolverConfigEntryTableModel;
import edacc.experiment.tabs.solver.SolverConfigurationEntry;
import edacc.experiment.tabs.solver.SolverConfigurationEntryModel;
import edacc.experiment.tabs.solver.SolverConfigurationEntryModelListener;
import edacc.model.Solver;
import edacc.model.SolverBinaries;
import edacc.util.Pair;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;

/**
 *
 * @author simon
 */
public class EDACCSolverConfigComponent extends JComponent implements SolverConfigurationEntryModelListener, EDACCSolverConfigEntryListener {

    private static final int entryHeight = 265;
    private static final int maxSolverConfigEntries = 50;
    private static final int labelSpace = 5;
    private static final int entrySpace = 10;
    private int size;
    public EDACCExperimentMode parent;
    private boolean update;
    protected SolverConfigurationEntryModel model;
    private LinkedList<SolverLabel> labels;
    private LinkedList<EntryAdded> temporaryEntries;
    private ExperimentController expController;
    private RowFilter<SolverConfigurationEntryModel, Pair<Solver, Integer>> rowFilter;

    public EDACCSolverConfigComponent(ExperimentController expController) {
        this.expController = expController;
        this.rowFilter = null;
        this.model = expController.getSolverConfigurationEntryModel();
        model.addSolverConfigurationEntryModelListener(this);
        this.update = false;
        labels = new LinkedList<SolverLabel>();
        temporaryEntries = new LinkedList<EntryAdded>();
        size = 0;
    }

    public void setParent(EDACCExperimentMode parent) {
        this.parent = parent;
    }

    public SolverConfigurationEntryModel getModel() {
        return model;
    }

    public void setTitles() {
        if (!update && parent != null) {
            parent.setTitles();
        }
    }

    private void doRepaint() {
        if (!update) {
            this.repaint();
            setTitles();
        }
    }

    protected void replicateEntry(EDACCSolverConfigEntry other) {
        int index = model.getSolverIndex(other.getModel());
        try {
            SolverConfigurationEntry entry = new SolverConfigurationEntry(other.getModel());
            model.insert(entry, entry.getSolver(), index + 1);
        } catch (SQLException ex) {
            // TODO: error!
        }
        model.fireDataChanged();
    }

    protected void removeEntry(EDACCSolverConfigEntry entry, boolean markAsDeleted) {
        model.removeSolverConfigurationEntry(entry.getModel());
        if (entry.getSolverConfiguration() != null) {
            expController.solverConfigCache.markAsDeleted(entry.getSolverConfiguration());
        }
        model.fireDataChanged();
    }

    @Override
    public Dimension getPreferredSize() {
        if (model == null || labels.isEmpty()) {
            return new Dimension(0, 0);
        }

        return new Dimension(0, size * (entryHeight + entrySpace) + labels.size() * (labels.get(0).label.getPreferredSize().height + labelSpace));
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(0,0);
    }
    
    

    @Override
    public void paint(Graphics g) {
        //  this.removeAll();
        Rectangle rect = g.getClipBounds();
        int y = 10;
        if (labels.size() > 0) {
            int labelHeight = labels.get(0).label.getPreferredSize().height;
            for (SolverLabel sl : labels) {
                int height = labelHeight + labelSpace + sl.entries.size() * (entryHeight + entrySpace);
                Rectangle slrect = new Rectangle(0, y, this.getWidth(), height);
                // System.out.println(slrect + " intersects " + rect + "? " + slrect.intersects(rect));
                if (!slrect.intersects(rect)) {
                    y += height;
                    continue;
                }
                if (y >= rect.y - 1000 && y <= rect.y + rect.height + 1000) {
                    sl.label.setBounds(10, y, sl.label.getPreferredSize().width, labelHeight);
                    this.add(sl.label);
                }
                y += labelHeight + labelSpace;


                int start = Math.max((rect.y - 1000 - y) / (entryHeight + entrySpace), 0);
                y += start * (entryHeight + entrySpace);
                //  System.out.println("START = " + start);
                for (int i = start; i < sl.entries.size(); i++) {
                    if (y <= rect.y + rect.height + 1000) {
                        if (sl.entries.get(i).entry == null) {
                            sl.entries.get(i).entry = new EDACCSolverConfigEntry(sl.entries.get(i).model, this);
                            temporaryEntries.add(sl.entries.get(i));
                        }
                        sl.entries.get(i).entry.setBounds(10, y, this.getWidth() - 20, entryHeight);
                        if (!sl.entries.get(i).added) {
                            this.add(sl.entries.get(i).entry);
                            sl.entries.get(i).added = true;
                            temporaryEntries.remove(sl.entries.get(i));
                            temporaryEntries.add(sl.entries.get(i));
                        }
                        sl.entries.get(i).entry.invalidate();
                        sl.entries.get(i).entry.revalidate();
                        sl.entries.get(i).entry.repaint();
                    } else {
                        //      System.out.println("END = " + i);
                        break;
                    }
                    y += entryHeight + entrySpace;
                }
            }
        }

        while (temporaryEntries.size() > maxSolverConfigEntries) {
            EntryAdded entry = temporaryEntries.poll();
            if (entry.added) {
                this.remove(entry.entry);
                entry.added = false;
            }
            entry.entry = null;
        }
        super.paint(g);
    }

    @Override
    public void onDataChanged() {
        labels.clear();
        temporaryEntries.clear();
        this.removeAll();

        List<Solver> solvers = new ArrayList<Solver>();
        solvers.addAll(model.getSolvers());
        Collections.sort(solvers, new Comparator<Solver>() {

            @Override
            public int compare(Solver o1, Solver o2) {
                return o1.getId() - o2.getId();
            }
        });

        // TODO: don't select solvers here. Parent should do this.
        for (int i = 0; i < parent.solTableModel.getRowCount(); i++) {
            parent.solTableModel.setSolverSelected(parent.solTableModel.getSolver(i).getId(), false);
        }
        size = 0;

        for (Solver s : solvers) {
            parent.solTableModel.setSolverSelected(s.getId(), true);
            List<EntryAdded> entries = new LinkedList<EntryAdded>();
            JLabel label = new JLabel(s.toString());
            labels.add(new SolverLabel(s, label, entries));
            for (int i = 0; i < model.getSize(labels.get(labels.size() - 1).solver); i++) {
                final Solver solver = s;
                final int index = i;
                if (rowFilter == null || rowFilter.include(new Entry<SolverConfigurationEntryModel, Pair<Solver, Integer>>() {

                    @Override
                    public SolverConfigurationEntryModel getModel() {
                        return model;
                    }

                    @Override
                    public int getValueCount() {
                        return model.getColumnCount();
                    }

                    @Override
                    public Object getValue(int i) {
                        return model.getValueAt(new Pair<Solver, Integer>(solver, index), i);
                    }

                    @Override
                    public Pair<Solver, Integer> getIdentifier() {
                        return new Pair<Solver, Integer>(solver, index);
                    }
                })) {
                    entries.add(new EntryAdded(model.getEntry(s, i), null));
                    size++;
                }
            }
        }
        this.invalidate();
        this.revalidate();
        doRepaint();
    }

    @Override
    public void onEntryChanged(SolverConfigurationEntry entry) {
        for (int i = 0; i < temporaryEntries.size(); i++) {
            if (temporaryEntries.get(i).entry.getModel() == entry) {
                temporaryEntries.get(i).entry.update();
                break;
            }
        }
        doRepaint();
    }

    public Rectangle getBoundsOf(SolverConfigurationEntry entry) {

        int y = 0;
        if (labels.size() == 0) {
            return null;
        }
        int labelHeight = labels.get(0).label.getPreferredSize().height;
        for (SolverLabel sl : labels) {
            y += labelHeight + labelSpace;
            for (int i = 0; i < sl.entries.size(); i++) {
                if (sl.entries.get(i).model == entry) {
                    return new Rectangle(10, y, this.getWidth() - 20, entryHeight);
                }
                y += entryHeight + entrySpace;
            }
        }
        return null;
    }

    @Override
    public void onNameChanged(EDACCSolverConfigEntry entry, String oldName, String newName) {
        notifyEntryChanged(entry);
    }

    @Override
    public void onSeedGroupChanged(EDACCSolverConfigEntry entry, int oldSeedGroup, int newSeedGroup) {
        notifyEntryChanged(entry);
    }

    @Override
    public void onHintChanged(EDACCSolverConfigEntry entry, String oldHint, String newHint) {
        notifyEntryChanged(entry);
    }

    @Override
    public void onSolverBinaryChanged(EDACCSolverConfigEntry entry, SolverBinaries oldSolverBinary, SolverBinaries newSolverBinary) {
        notifyEntryChanged(entry);
    }

    @Override
    public void onParametersChanged(EDACCSolverConfigEntry entry) {
        notifyEntryChanged(entry);
    }

    @Override
    public void onReplicateRequest(EDACCSolverConfigEntry entry) {
        replicateEntry(entry);
    }

    @Override
    public void onRemoveRequest(EDACCSolverConfigEntry entry) {
        removeEntry(entry, true);
    }

    private void notifyEntryChanged(EDACCSolverConfigEntry entry) {
        model.fireEntryChanged(entry.getModel());
        setTitles();
    }

    public void setFilter(RowFilter<SolverConfigurationEntryModel, Pair<Solver, Integer>> rowFilter) {
        this.rowFilter = rowFilter;
        onDataChanged();
    }

    public int getRowCount() {
        return size;
    }

    @Override
    public void onMassReplicationRequest(EDACCSolverConfigEntry entry) {
        EDACCSolverConfigReplicateUsingFiles replicator = new EDACCSolverConfigReplicateUsingFiles(EDACCApp.getApplication().getMainFrame(), true, entry.getModel());
        replicator.setName("EDACCSolverConfigReplicateUsingFiles");
        replicator.setLocationRelativeTo(EDACCApp.getApplication().getMainFrame());
        EDACCApp.getApplication().show(replicator);
        File dir;
        int index = model.getSolverIndex(entry.getModel());

        if ((dir = replicator.getChosenFolder()) != null) {
            boolean multipleSC = replicator.parseMultipleSolverConfigurations();
            String solverConfigName = replicator.getSolverConfigurationName();
            Pattern regexGroupsFromFilename = Pattern.compile(replicator.getRegexGroupsFromFilename());
            Pattern regexGroupsFromLines = Pattern.compile(replicator.getRegexGroupsFromLines());
            File[] files = dir.listFiles();
            SolverConfigEntryTableModel replModel = replicator.getModel();
            Pattern[] patterns = new Pattern[replModel.getRowCount()];
            String[] values = new String[replModel.getRowCount()];
            for (int i = 0; i < replModel.getRowCount(); i++) {
                if ((Boolean) replModel.getValueAt(i, 0)) {
                    // is selected
                    if ((Boolean) replModel.getValueAt(i, 6)) {
                        // is regex
                        patterns[i] = Pattern.compile((String) replModel.getValueAt(i, 3));
                    }
                }
            }
            for (File file : files) {
                if (!file.isFile()) {
                    continue;
                }
                for (int i = 0; i < replModel.getRowCount(); i++) {
                    if ((Boolean) replModel.getValueAt(i, 0)) {
                        if (!(Boolean) replModel.getValueAt(i, 6)) {
                            values[i] = (String) replModel.getValueAt(i, 3);
                        } else {
                            values[i] = null;
                        }
                    }
                }
                BufferedReader input = null;

                try {
                    input = new BufferedReader(new FileReader(file));
                    String line;
                    ArrayList<String> lineGroups = new ArrayList<String>();
                    while ((line = input.readLine()) != null) {
                        if (multipleSC) {
                            lineGroups.clear();
                        }
                        for (int i = 0; i < replModel.getRowCount(); i++) {
                            if (patterns[i] != null) {
                                Matcher m = patterns[i].matcher(line);
                                if (m.matches()) {
                                    if (m.groupCount() > 0) {
                                        if (values[i] != null || m.groupCount() > 1) {
                                            // TODO: WARNING
                                        } else {
                                            values[i] = m.group(1);
                                        }
                                    }
                                }
                            }

                        }
                        Matcher m = regexGroupsFromLines.matcher(line);
                        if (m.matches()) {
                            for (int k = 1; k <= m.groupCount(); k++) {
                                lineGroups.add(m.group(k));
                            }
                        }
                        if (multipleSC) {
                            SolverConfigurationEntry newEntry = new SolverConfigurationEntry(entry.getModel().getSolver(), entry.getModel().getExperiment());
                            m = regexGroupsFromFilename.matcher(file.getName());
                            ArrayList<String> filenameGroups = new ArrayList<String>();
                            if (m.matches()) {
                                for (int i = 1; i <= m.groupCount(); i++) {
                                    filenameGroups.add(m.group(i));
                                }
                            }
                            String name = solverConfigName;
                            for (int i = 0; i < lineGroups.size(); i++) {
                                name = name.replaceAll("\\$l\\[" + (i + 1) + "\\]", lineGroups.get(i));
                            }
                            for (int i = 0; i < filenameGroups.size(); i++) {
                                name = name.replaceAll("\\$f\\[" + (i + 1) + "\\]", filenameGroups.get(i));
                            }
                            newEntry.setName(name);
                            newEntry.setSeedGroup(entry.getModel().getSeedGroup());
                            for (int i = 0; i < replModel.getRowCount(); i++) {
                                if (values[i] != null) {
                                    newEntry.getTableModel().setValueAt(values[i], i, 3);
                                    newEntry.getTableModel().setValueAt(true, i, 0);
                                    if (patterns[i] != null) {
                                        values[i] = null;
                                    }
                                }
                            }
                            model.insert(newEntry, newEntry.getSolver(), ++index);
                        }
                    }
                    if (!multipleSC) {
                        SolverConfigurationEntry newEntry = new SolverConfigurationEntry(entry.getModel().getSolver(), entry.getModel().getExperiment());
                        ArrayList<String> filenameGroups = new ArrayList<String>();
                        Matcher m = regexGroupsFromFilename.matcher(file.getName());
                        if (m.matches()) {
                            for (int i = 1; i <= m.groupCount(); i++) {
                                filenameGroups.add(m.group(i));
                            }
                        }
                        String name = solverConfigName;
                        for (int i = 0; i < lineGroups.size(); i++) {
                            name = name.replaceAll("\\$l\\[" + (i + 1) + "\\]", lineGroups.get(i));
                        }
                        for (int i = 0; i < filenameGroups.size(); i++) {
                            name = name.replaceAll("\\$f\\[" + (i + 1) + "\\]", filenameGroups.get(i));
                        }
                        newEntry.setName(name);
                        newEntry.setSeedGroup(entry.getModel().getSeedGroup());
                        for (int i = 0; i < replModel.getRowCount(); i++) {
                            if (values[i] != null) {
                                newEntry.getTableModel().setValueAt(values[i], i, 3);
                                newEntry.getTableModel().setValueAt(true, i, 0);
                            }
                        }
                        model.insert(newEntry, newEntry.getSolver(), ++index);
                    }
                } catch (FileNotFoundException ex) {
                    // TODO: error
                } catch (IOException ex) {
                    // TODO: error
                } catch (SQLException ex) {
                    // TODO: error
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException ex) {
                        }
                    }
                }
            }
            model.fireDataChanged();
        }
    }

    private class SolverLabel {

        Solver solver;
        JLabel label;
        List<EntryAdded> entries;

        public SolverLabel(Solver solver, JLabel label, List<EntryAdded> entries) {
            this.solver = solver;
            this.label = label;
            this.entries = entries;
        }
    }

    private class EntryAdded {

        SolverConfigurationEntry model;
        EDACCSolverConfigEntry entry;
        boolean added;

        public EntryAdded(SolverConfigurationEntry model, EDACCSolverConfigEntry entry) {
            this.model = model;
            this.added = false;
            this.entry = entry;
        }
    }
}
