/*
 * EDACCParameterGraphEditor.java
 *
 * Created on 02.07.2011, 17:44:44
 */
package edacc.parametergrapheditor;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxMultiplicity;
import edacc.EDACCApp;
//import edacc.model.Parameter; //Todo generate the parameters from smac file
import edacc.model.ParameterDAO;
import edacc.model.ParameterGraphDAO;
import edacc.model.Solver;
import edacc.parameterspace.Parameter;
import edacc.parameterspace.domain.*;
import edacc.parameterspace.graph.AndNode;
import edacc.parameterspace.graph.Edge;
import edacc.parameterspace.graph.Node;
import edacc.parameterspace.graph.OrNode;
import edacc.parameterspace.graph.ParameterGraph;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBException;

/**
 *
 * @author simon
 */
public class ParameterGraphEditor extends javax.swing.JDialog {

    mxGraph graph;
    mxGraphComponent graphComponent;
    Solver solver;
    mxCell rootNode;
    Set<Parameter> parameters;
    ArrayList<String> solverParameters;

    /** Creates new form EDACCParameterGraphEditor */
    public ParameterGraphEditor(java.awt.Frame parent, boolean modal, Solver solver) throws SQLException, JAXBException {
        super(parent, modal);
        initComponents();
        this.solver = solver;
        solverParameters = new ArrayList<String>();
        for (edacc.model.Parameter param : ParameterDAO.getParameterFromSolverId(solver.getId())) {
            if (!edacc.experiment.Util.isMagicSolverParameter(param.getName())) {
                solverParameters.add(param.getName());
            }
        }

        graph = new mxGraph() {

            @Override
            public boolean isCellEditable(Object o) {
                return false;
            }

            @Override
            public String convertValueToString(Object o) {
                if (o instanceof mxCell) {
                    mxCell cell = (mxCell) o;
                    if (cell.isEdge()) {
                        if (cell.getValue() == null || cell.getValue() instanceof String) {
                            return "";
                        } else {
                            Edge edge = (Edge) cell.getValue();
                            if (edge.getGroup() == 0) {
                                return "";
                            } else {
                                return "" + edge.getGroup();
                            }
                        }
                    } else if (cell.isVertex()) {
                        if (cell.getValue() instanceof AndNode) {
                            AndNode node = (AndNode) cell.getValue();
                            if (node.getDomain() == null) {
                                return "AND";
                            }
                            return "AND\n" + domainToString(node.getDomain());
                        } else if (cell.getValue() instanceof OrNode) {
                            OrNode node = (OrNode) cell.getValue();
                            if (node == null || node.getParameter() == null) {
                                // TODO: fix
                                // Parameter is null while moving cell, don't know why
                                return "";
                            }
                            return "OR\n" + node.getParameter().getName() + "\n" + domainToString(node.getParameter().getDomain());
                        }
                    } else {
                        return super.convertValueToString(o);
                    }
                } else {
                    return super.convertValueToString(o);
                }
                return "None";
            }
        };

        graphComponent = new mxGraphComponent(graph);

        graphComponent.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                ParameterGraphEditor.this.evtKeyReleased(e);
            }
        });

        jScrollPane1.setViewportView(graphComponent);

        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Object o = graph.getSelectionCell();
                    if (o instanceof mxCell) {
                        mxCell cell = (mxCell) o;
                        if (cell.isEdge()) {
                            Edge edge = null;
                            if (cell.getValue() == null || cell.getValue() instanceof String) {
                                // will be changed when saved.
                                edge = new Edge(null, null, 0);
                            } else {
                                edge = (Edge) cell.getValue();
                            }
                            String group = JOptionPane.showInputDialog("Group:", edge.getGroup());
                            if (group != null) {
                                try {
                                    edge.setGroup(Integer.parseInt(group));
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(ParameterGraphEditor.this, "Invalid group: " + group, "Invalid Group", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            graph.getModel().setValue(cell, edge);
                        } else if (cell.isVertex()) {
                            if (cell != rootNode && cell.getValue() instanceof AndNode) {
                                editAndNode((AndNode) cell.getValue());
                                graph.updateCellSize(cell);
                                graph.refresh();
                            }
                        }
                    }
                }
            }
        });

        graphComponent.getGraph().getModel().addListener(null, new mxEventSource.mxIEventListener() {

            @Override
            public void invoke(Object o, mxEventObject eo) {
                if ("change".equals(eo.getName())) {
                    for (Object oj : eo.getProperties().values()) {
                        if (oj instanceof ArrayList) {
                            ArrayList list = (ArrayList) oj;
                            for (Object object : list) {
                                if (object instanceof mxGraphModel.mxChildChange) {
                                    mxGraphModel.mxChildChange ch = (mxGraphModel.mxChildChange) object;

                                    // parent is null if removed, we don't want to show a dialog when removing edges
                                    if (ch.getChild() instanceof mxCell && ch.getParent() != null) {
                                        mxCell child = (mxCell) ch.getChild();

                                        if (child.isEdge()) {
                                            if (child.getTarget() == null && child.getSource() != null) {
                                                Point mouse = MouseInfo.getPointerInfo().getLocation();
                                                Point graphLocation = graphComponent.getLocationOnScreen();

                                                try {
                                                    Parameter parameter = null;
                                                    Domain domain = null;
                                                    boolean cancelled = false;
                                                    if (child.getSource().getValue() instanceof AndNode) {
                                                        parameter = createOrNode();
                                                        cancelled = parameter == null;
                                                    } else {
                                                        domain = createAndNode((OrNode) child.getSource().getValue());
                                                        cancelled = domain == null;
                                                    }
                                                    graph.getModel().beginUpdate();
                                                    if (!cancelled) {
                                                        Object node = null;
                                                        if (parameter != null) {
                                                            node = new OrNode(parameter);
                                                        } else {
                                                            node = new AndNode(((OrNode) child.getSource().getValue()).getParameter(), domain);
                                                        }
                                                        mxCell vertex = (mxCell) graph.insertVertex(graph.getDefaultParent(), null, node, mouse.x - graphLocation.x, mouse.y - graphLocation.y, 80, 30, null);
                                                        graph.updateCellSize(vertex);
                                                        graph.insertEdge(graph.getDefaultParent(),
                                                                null, "",
                                                                child.getSource(),
                                                                vertex);
                                                        // child.setTarget((mxCell) graph.insertVertex(graph.getDefaultParent(), null, node, mouse.x - graphLocation.x, mouse.y - graphLocation.y, 80, 30, null));
                                                        // child.setParent(child.getTarget());
                                                        //  ((mxGraphModel) graph.getModel()).updateEdgeParents(graph.getDefaultParent());

                                                    }
                                                    graph.getModel().remove(child);
                                                    graph.getModel().endUpdate();
                                                    graph.setSelectionCell(null);
                                                    graph.refresh();

                                                } catch (Exception ex) {
                                                    EDACCApp.getLogger().logException(ex);
                                                    graph.getModel().remove(child);
                                                    graph.setSelectionCell(null);
                                                    graph.refresh();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        mxMultiplicity[] multiplicities = new mxMultiplicity[1];

        multiplicities[0] = new mxMultiplicity(true, null, null, null, 0,
                "n", null,
                "Can't connect nodes.",
                "", true) {

            @Override
            public String check(mxGraph mxgrph, Object edge, Object source, Object target, int sourceOut, int targetIn) {
                if (!(source instanceof mxCell) && !(target instanceof mxCell)) {
                    return "Can't connect nodes.";
                }
                mxCell s = (mxCell) source;
                mxCell t = (mxCell) target;
                if (s.getValue() instanceof AndNode && t.getValue() instanceof AndNode) {
                    return "Can't connect AND-node to AND-node.";
                }
                if (s.getValue() instanceof OrNode && t.getValue() instanceof OrNode) {
                    return "Can't connect OR-node to OR-node";
                }

                List<mxCell> roots = findRoots();
                boolean hasCycle = false;
                for (int i = 0; i < roots.size(); i++) {
                    hasCycle |= hasCycle(roots.get(i), new HashSet<String>(), new HashSet<String>(), (mxCell) target);
                    if (hasCycle) {
                        break;
                    }
                }
                if (hasCycle) {
                    return "Can't connect nodes. Graph cannot have cycles.";
                }
                return null;
            }
        };

        graph.setMultiplicities(multiplicities);
        graph.setMultigraph(false);
        //  graph.setAllowDanglingEdges(false);
        graph.setCellsDisconnectable(false);
        graph.setCellsDeletable(true);
        graph.setCellsCloneable(false);
        graph.setAllowNegativeCoordinates(false);

        ParameterGraph parameterGraph = ParameterGraphDAO.loadParameterGraph(solver);
        try {
            loadParameterGraph(parameterGraph);
        } catch (Exception ex) {
            EDACCApp.getLogger().logException(ex);
            JOptionPane.showMessageDialog(parent, "Could not load parameter graph!", "Error", JOptionPane.ERROR_MESSAGE);
            graph.removeCells(graph.getChildCells(graph.getDefaultParent()));
            // create root node:
            graph.updateCellSize(rootNode = (mxCell) graph.insertVertex(graph.getDefaultParent(), null, new AndNode(null, null), 20, 20, 80, 30));
            if (parameters == null) {
                parameters = new HashSet<Parameter>();
            }
            graph.getModel().endUpdate();
        }
    }

    private void loadParameterGraph(ParameterGraph parameterGraph) {
        graph.removeCells(graph.getChildCells(graph.getDefaultParent()));
        if (parameterGraph == null) {
            // create root node:
            graph.updateCellSize(rootNode = (mxCell) graph.insertVertex(graph.getDefaultParent(), null, new AndNode(null, null), 20, 20, 80, 30));
            parameters = new HashSet<Parameter>();
        } else {
            parameters = parameterGraph.parameters;
            if (parameters == null) {
                parameters = new HashSet<Parameter>();
            }
            graph.getModel().beginUpdate();
            HashMap<String, mxCell> mapNodeIdToCell = new HashMap<String, mxCell>();
            for (Node node : parameterGraph.nodes) {
                mxCell cell = (mxCell) graph.insertVertex(graph.getDefaultParent(), null, node, 20, 20, 80, 30);
                if (node == parameterGraph.startNode) {
                    rootNode = cell;
                }
                graph.updateCellSize(cell);
                mapNodeIdToCell.put(node.getId(), cell);
            }

            for (Edge edge : parameterGraph.edges) {
                graph.insertEdge(graph.getDefaultParent(), null, edge, mapNodeIdToCell.get(edge.getSource().getId()), mapNodeIdToCell.get(edge.getTarget().getId()));
            }

            graph.getModel().endUpdate();

            btnLayoutActionPerformed(null);
        }
    }

    private Parameter createOrNode() {
        CreateOrNodeDialog dialog = new CreateOrNodeDialog(new javax.swing.JFrame(), true, parameters);
        dialog.setLocationRelativeTo(ParameterGraphEditor.this);
        dialog.setVisible(true);
        Parameter parameter;
        if (!dialog.isCancelled()) {
            parameter = dialog.getParameter();
            return parameter;
        }
        return null;
    }

    private Domain createAndNode(OrNode node) {
        CreateAndNodeDialog dialog = new CreateAndNodeDialog(new javax.swing.JFrame(), true, node.getParameter(), node.getParameter().getDomain());
        dialog.setLocationRelativeTo(ParameterGraphEditor.this);
        dialog.setSize(new Dimension(800, 600));
        dialog.setVisible(true);
        Domain domain = null;
        while (!dialog.isCancelled() && domain == null) {
            try {
                domain = dialog.getDomain();
                break;
            } catch (InvalidDomainException ex) {
                EDACCApp.getLogger().logException(ex);
                JOptionPane.showMessageDialog(ParameterGraphEditor.this, ex.getMessage(), "Invalid Domain", JOptionPane.ERROR_MESSAGE);
                ((JDialog) dialog).setVisible(true);
            }
        }
        return domain;
    }

    private void editAndNode(AndNode node) {
        CreateAndNodeDialog dialog = new CreateAndNodeDialog(new javax.swing.JFrame(), true, node.getParameter(), node.getParameter().getDomain());
        try {
            dialog.loadValuesFromDomain(node.getParameter().getDomain(), node.getDomain());
        } catch (Exception ex) {
            // TODO: error?
            EDACCApp.getLogger().logException(ex);
            return;
        }
        dialog.setLocationRelativeTo(ParameterGraphEditor.this);
        dialog.setSize(new Dimension(800, 600));
        dialog.setVisible(true);
        Domain domain = null;
        while (!dialog.isCancelled() && domain == null) {
            try {
                domain = dialog.getDomain();
                break;
            } catch (InvalidDomainException ex) {
                EDACCApp.getLogger().logException(ex);
                JOptionPane.showMessageDialog(ParameterGraphEditor.this, ex.getMessage(), "Invalid Domain", JOptionPane.ERROR_MESSAGE);
                ((JDialog) dialog).setVisible(true);
            }
        }
        if (domain != null) {
            node.setDomain(domain);
        }
    }

    private String domainToString(Domain domain) {
        if (domain instanceof CategoricalDomain) {
            return "Categorical";
        } else if (domain instanceof FlagDomain) {
            FlagDomain flag = (FlagDomain) domain;
            if (flag.contains(FlagDomain.FLAGS.ON) && (flag.contains(FlagDomain.FLAGS.OFF))) {
                return "Flag [On, Off]";
            } else if (flag.contains(FlagDomain.FLAGS.ON)) {
                return "Flag [On]";
            } else {
                return "Flag [Off]";
            }
        } else if (domain instanceof IntegerDomain) {
            IntegerDomain integer = (IntegerDomain) domain;
            return "Integer [" + integer.getLow() + ", " + integer.getHigh() + "]";
        } else if (domain instanceof MixedDomain) {
            MixedDomain mixed = (MixedDomain) domain;
            String res = "";
            for (int d = 0; d < mixed.getDomains().size(); d++) {
                res += domainToString(mixed.getDomains().get(d));
                if (d != mixed.getDomains().size() - 1) {
                    res += "\n";
                }
            }
            return res;
        } else if (domain instanceof OptionalDomain) {
            return "Optional";
        } else if (domain instanceof OrdinalDomain) {
            return "Ordinal";
        } else if (domain instanceof RealDomain) {
            RealDomain real = (RealDomain) domain;
            return "Real [" + real.getLow() + ", " + real.getHigh() + "]";
        } else {
            return "Unknown";
        }
    }

    public boolean hasCycle(mxCell startNode, Set<String> gray, Set<String> black, mxCell defaultTarget) {
        if (gray.contains(startNode.getId())) {
            return true;
        } else if (!black.contains(startNode.getId())) {
            gray.add(startNode.getId());
            Object[] edges = graph.getOutgoingEdges(startNode);
            for (Object o : edges) {
                mxCell edge = (mxCell) o;
                mxCell target = (edge.getTarget() != null ? (mxCell) edge.getTarget() : defaultTarget);
                if (target != null && hasCycle(target, gray, black, defaultTarget)) {
                    return true;
                }
            }
            black.add(startNode.getId());
            gray.remove(startNode.getId());
        }
        return false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnLayout = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnParameters = new javax.swing.JButton();
        btnDefaultGraph = new javax.swing.JButton();
        btnSaveToFile = new javax.swing.JButton();
        btnLoadFromFile = new javax.swing.JButton();
        btnImportSMACStyleParams = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(ParameterGraphEditor.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        btnLayout.setText(resourceMap.getString("btnLayout.text")); // NOI18N
        btnLayout.setName("btnLayout"); // NOI18N
        btnLayout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLayoutActionPerformed(evt);
            }
        });

        btnSave.setText(resourceMap.getString("btnSave.text")); // NOI18N
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnParameters.setText(resourceMap.getString("btnParameters.text")); // NOI18N
        btnParameters.setName("btnParameters"); // NOI18N
        btnParameters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnParametersActionPerformed(evt);
            }
        });

        btnDefaultGraph.setText(resourceMap.getString("btnDefaultGraph.text")); // NOI18N
        btnDefaultGraph.setToolTipText(resourceMap.getString("btnDefaultGraph.toolTipText")); // NOI18N
        btnDefaultGraph.setName("btnDefaultGraph"); // NOI18N
        btnDefaultGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultGraphActionPerformed(evt);
            }
        });

        btnSaveToFile.setText(resourceMap.getString("btnSaveToFile.text")); // NOI18N
        btnSaveToFile.setName("btnSaveToFile"); // NOI18N
        btnSaveToFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveToFileActionPerformed(evt);
            }
        });

        btnLoadFromFile.setText(resourceMap.getString("btnLoadFromFile.text")); // NOI18N
        btnLoadFromFile.setName("btnLoadFromFile"); // NOI18N
        btnLoadFromFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadFromFileActionPerformed(evt);
            }
        });

        btnImportSMACStyleParams.setText(resourceMap.getString("btnImportSMACStyleParams.text")); // NOI18N
        btnImportSMACStyleParams.setName("btnImportSMACStyleParams"); // NOI18N
        btnImportSMACStyleParams.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportSMACStyleParamsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnLayout)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnParameters)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDefaultGraph)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSaveToFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLoadFromFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnImportSMACStyleParams)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSave)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLayout)
                    .addComponent(btnSave)
                    .addComponent(btnParameters)
                    .addComponent(btnDefaultGraph)
                    .addComponent(btnSaveToFile)
                    .addComponent(btnLoadFromFile)
                    .addComponent(btnImportSMACStyleParams))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 930, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLayoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLayoutActionPerformed
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.setInterRankCellSpacing(40);
        // layout.setFixRoots(true);
        Object cell = graph.getSelectionCell();

        if (cell == null
                || graph.getModel().getChildCount(cell) == 0) {
            cell = graph.getDefaultParent();
        }

        graph.getModel().beginUpdate();
        try {
            layout.execute(cell);
        } finally {
            mxMorphing morph = new mxMorphing(graphComponent, 20,
                    1.2, 20);

            morph.addListener(mxEvent.DONE, new mxIEventListener() {

                @Override
                public void invoke(Object sender, mxEventObject evt) {
                    graph.getModel().endUpdate();
                }
            });

            morph.startAnimation();
        }
    }//GEN-LAST:event_btnLayoutActionPerformed

    public List<mxCell> findRoots() {
        Object[] cells = graph.getChildCells(graph.getDefaultParent());
        ArrayList<mxCell> roots = new ArrayList<mxCell>();
        for (Object o : cells) {
            if (o instanceof mxCell) {
                mxCell cell = (mxCell) o;
                if (cell.isVertex() && graph.getIncomingEdges(cell).length == 0) {
                    roots.add(cell);
                }
            }
        }
        return roots;
    }

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        try {
            ParameterGraph parameterGraph = getParameterGraph();
            try {
                ParameterGraphDAO.saveParameterGraph(parameterGraph, solver);
                JOptionPane.showMessageDialog(this, "Saved.", "Information", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                EDACCApp.getLogger().logException(ex);
                JOptionPane.showMessageDialog(this, "Error while saving:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            EDACCApp.getLogger().logException(ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnParametersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnParametersActionPerformed
        SpecifyDomainDialog dialog = new SpecifyDomainDialog(EDACCApp.getApplication().getMainFrame(), true, solverParameters, parameters);
        dialog.setLocationRelativeTo(EDACCApp.getApplication().getMainFrame());
        EDACCApp.getApplication().show(dialog);
        if (!dialog.isCancelled()) {
            parameters = dialog.getParameters();
            HashMap<String, Parameter> paramMap = new HashMap<String, Parameter>();
            for (Parameter param : parameters) {
                paramMap.put(param.getName(), param);
            }
            graph.getModel().beginUpdate();
            Object[] cells = graph.getChildCells(graph.getDefaultParent());
            for (Object o : cells) {
                mxCell cell = (mxCell) o;
                if (cell.getValue() instanceof OrNode) {
                    String name = ((OrNode) cell.getValue()).getParameter().getName();
                    // TODO: what if not... invalid param (deleted param) -> delete node? set default domain?
                    if (paramMap.containsKey(name)) {
                        ((OrNode) cell.getValue()).setParameter(paramMap.get(name));
                        graph.getModel().setValue(cell, cell.getValue());
                        graph.updateCellSize(cell);
                    }
                }
            }
            graph.getModel().endUpdate();
        }
    }//GEN-LAST:event_btnParametersActionPerformed

    private void btnDefaultGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultGraphActionPerformed
        if (parameters == null || parameters.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No parameters specified yet.", "Parameters missing", JOptionPane.INFORMATION_MESSAGE);
        }
        graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
        Set<Node> nodes = new HashSet<Node>();
        List<Edge> edges = new LinkedList<Edge>();
        AndNode startNode = new AndNode(null, null);
        nodes.add(startNode);
        for (Parameter parameter : parameters) {
            OrNode orNode = new OrNode(parameter);
            AndNode andNode = new AndNode(parameter, parameter.getDomain());
            edges.add(new Edge(startNode, orNode, 0));
            edges.add(new Edge(orNode, andNode, 0));
            nodes.add(orNode);
            nodes.add(andNode);
        }
        renderGraph(nodes, edges, startNode);
    }

    private void renderGraph(Set<Node> nodes, List<Edge> edges, AndNode startNode) {
        ParameterGraph parameterGraph = new ParameterGraph(nodes, edges, parameters, startNode);

        graph.getModel().beginUpdate();
        HashMap<String, mxCell> mapNodeIdToCell = new HashMap<String, mxCell>();
        for (Node node : parameterGraph.nodes) {
            mxCell cell = (mxCell) graph.insertVertex(graph.getDefaultParent(), null, node, 20, 20, 80, 30);
            if (node == parameterGraph.startNode) {
                rootNode = cell;
            }
            graph.updateCellSize(cell);
            node.setId(cell.getId());
            mapNodeIdToCell.put(cell.getId(), cell);
        }

        for (Edge edge : parameterGraph.edges) {
            graph.insertEdge(graph.getDefaultParent(), null, edge, mapNodeIdToCell.get(edge.getSource().getId()), mapNodeIdToCell.get(edge.getTarget().getId()));
        }

        graph.getModel().endUpdate();

        btnLayoutActionPerformed(null);
    }//GEN-LAST:event_btnDefaultGraphActionPerformed

    private void btnSaveToFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveToFileActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getAbsolutePath().endsWith(".graph");
            }

            @Override
            public String getDescription() {
                return "EDACC Graph File (.graph)";
            }
        });
        fc.showSaveDialog(this);
        File f;

        if ((f = fc.getSelectedFile()) != null) {

            if (!f.getAbsolutePath().endsWith(".graph")) {
                f = new File(f.getAbsolutePath().concat(".graph"));
            }
            if (f.exists()) {
                int input = JOptionPane.showConfirmDialog(this, "File " + f.getAbsoluteFile() + " exists. Overwrite?", "File exists", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (input != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            ParameterGraph parameterGraph;
            try {
                parameterGraph = getParameterGraph();
            } catch (Exception ex) {
                EDACCApp.getLogger().logException(ex);
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                ParameterGraphDAO.saveParameterGraph(f, parameterGraph);
                JOptionPane.showMessageDialog(this, "Saved.", "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (JAXBException ex) {
                EDACCApp.getLogger().logException(ex);
                JOptionPane.showMessageDialog(this, "Error while generating XML file. Graph invalid?", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (FileNotFoundException ex) {
                EDACCApp.getLogger().logException(ex);
                JOptionPane.showMessageDialog(this, "File not found: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                EDACCApp.getLogger().logException(ex);
                JOptionPane.showMessageDialog(this, "Error while writing file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnSaveToFileActionPerformed

    private void btnLoadFromFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadFromFileActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getAbsolutePath().endsWith(".graph");
            }

            @Override
            public String getDescription() {
                return "EDACC Graph File (.graph)";
            }
        });
        fc.showOpenDialog(this);
        File f;
        if ((f = fc.getSelectedFile()) != null) {
            ParameterGraph parameterGraph;
            try {
                parameterGraph = ParameterGraphDAO.loadParameterGraph(f);
                loadParameterGraph(parameterGraph);
                JOptionPane.showMessageDialog(this, "Graph has been loaded.", "Loaded", JOptionPane.INFORMATION_MESSAGE);
            } catch (FileNotFoundException ex) {
                EDACCApp.getLogger().logException(ex);
                JOptionPane.showMessageDialog(this, "File not found: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (JAXBException ex) {
                EDACCApp.getLogger().logException(ex);
                JOptionPane.showMessageDialog(this, "Error while loading XML file. File invalid?", "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }//GEN-LAST:event_btnLoadFromFileActionPerformed

    private void btnImportSMACStyleParamsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportSMACStyleParamsActionPerformed
        JOptionPane.showMessageDialog(ParameterGraphEditor.this, "Conditional parameters can be imported only when their parent is of categorical type! Please check the graph after import!", "SMAC Specification import", JOptionPane.INFORMATION_MESSAGE);
        JFileChooser fc = new JFileChooser();
        int lineNr = 0;
        fc.showOpenDialog(this);
        HashSet<String> childParams = new HashSet<String>();
        HashSet<String> parentParams = new HashSet<String>();
        HashSet<String> includedParams = new HashSet<String>();
        StringBuilder message = new StringBuilder();
        class Conditional {

            HashSet<String> childrenParams;
            ArrayList<String> condParamWithValues;
            boolean included; //is true once this node has been added to the graph

            public boolean isIncluded() {
                return included;
            }

            public void setIncluded(boolean included) {
                this.included = included;
            }

            Conditional(String child, ArrayList<String> condparamv) {
                this.childrenParams = new HashSet<String>();
                this.childrenParams.add(child);
                this.condParamWithValues = condparamv;
                this.included = false;

            }

            Conditional(ArrayList<String> condparamv) {
                this.childrenParams = new HashSet<String>();
                this.condParamWithValues = condparamv;
                this.included = false;

            }

            public void addChild(String child) {
                this.childrenParams.add(child);
            }

            public HashSet<String> getChildren() {
                return new HashSet<String>(this.childrenParams);
            }

            @Override
            public String toString() {
                return "Conditional{" + "condParamWithValues=" + condParamWithValues + " childrenParams=" + childrenParams + '}';
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == null) {
                    return false;
                }
                if (getClass() != obj.getClass()) {
                    return false;
                }
                final Conditional other = (Conditional) obj;
                if (this.condParamWithValues != other.condParamWithValues && (this.condParamWithValues == null || !this.condParamWithValues.equals(other.condParamWithValues))) {
                    return false;
                }
                return true;
            }

            @Override
            public int hashCode() {
                int hash = 5;
                hash = 71 * hash + (this.condParamWithValues != null ? this.condParamWithValues.hashCode() : 0);
                return hash;
            }
        }
        HashSet<Conditional> conditionalParams = new HashSet<Conditional>();
        HashSet<String> allParamNames = new HashSet<String>();
        if (fc.getSelectedFile() != null) {
            try {
                parameters = new HashSet<Parameter>();
                //System.out.println("Found Paramters:");
                BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream(fc.getSelectedFile()), "UTF-8"));
                String line;
                while ((line = rdr.readLine()) != null) {
                    lineNr++;
                    line = line.trim();
                    if (line.startsWith("#") || line.isEmpty()) {
                        continue; //ignore empty lines and comments
                    }
                    /*possible lines
                     * categorical:
                     * <parameter_name> {<value 1>, ..., <value N>} [<default value>]
                     * continous integer or double (with possible log scale)
                     * <parameter_name> [<min value>, <max value>] [<default value>] [i] [l]
                     *  now we have only the conditionals parameters of the form
                     * <child name> | <parent name> in {<parent val1>, ..., <parent valK>}
                     */
                    int pos = line.indexOf(" "); //once the first white space has been found we have the
                    String paramName = line.substring(0, pos); //parameter name
                    String paramDefinition = line.substring(paramName.length()).trim(); //contains the definition of the paramter
                    //System.out.print(paramName + " : ");
                    if (paramDefinition.isEmpty()) {
                        message.append("Error at line: ");
                        message.append(lineNr);
                        message.append("the definition of the paramter is empty!\n");
                    }
                    if (paramDefinition.charAt(0) == '{') { //we have a categorical parameter
                        //System.out.print(" is categorical with values: ");
                        int endIx = paramDefinition.indexOf('}');
                        if (endIx == -1) {
                            message.append("Error at line");
                            message.append(lineNr);
                            break;
                        }
                        String[] vals = paramDefinition.substring(1, endIx).split(",");
                        for (int idxVals = 0; idxVals < vals.length; idxVals++) {
                            vals[idxVals] = vals[idxVals].trim();
                            //System.out.print(vals[idxVals] + ", ");
                        }
                        //System.out.println();
                        Parameter p = new Parameter(paramName, new CategoricalDomain(vals));
                        parameters.add(p);
                        allParamNames.add(p.getName());
                    } else if (paramDefinition.charAt(0) == '[') {
                        int endIx = paramDefinition.indexOf(']');
                        if (endIx == -1) {
                            message.append("Error at line");
                            message.append(lineNr);
                            break;
                        }
                        String range = paramDefinition.substring(1, endIx);

                        int endDefaultIx = paramDefinition.indexOf(']', endIx + 1);
                        boolean isInt = false;
                        if (paramDefinition.length() > endDefaultIx + 1) {
                            isInt = (paramDefinition.charAt(endDefaultIx + 1) == 'i');
                        }

                        Parameter p = null;
                        if (isInt) {
                            //System.out.println("is continous integer");
                            int low = Integer.valueOf(range.split(",")[0]);
                            //System.out.print(low + "..");
                            int high = Integer.valueOf(range.split(",")[1]);
                            //System.out.println(high);
                            p = new Parameter(paramName, new IntegerDomain(low, high));
                        } else {
                            //System.out.println("is continous real");
                            double low = Double.valueOf(range.split(",")[0]);
                            //System.out.print(low + "..");
                            double high = Double.valueOf(range.split(",")[1]);
                            //System.out.println(high);
                            p = new Parameter(paramName, new RealDomain(low, high));
                        }
                        parameters.add(p);
                        allParamNames.add(p.getName());
                    } else if (paramDefinition.charAt(0) == '|') { //we have a conditional parameter of the form:
                        //<child name> | <parent name> in {<parent val1>, ..., <parent valK>}
                        //add the child param to the list of conditioned parameters
                        //parse the parent parameter
                        paramDefinition = paramDefinition.substring(1, paramDefinition.length()).trim(); //skip the "| " part
                        int endIdx = paramDefinition.indexOf(' ');
                        if (endIdx == -1) {
                            message.append("Error at line");
                            message.append(lineNr);
                            break;
                        }
                        String parentParamName = paramDefinition.substring(0, endIdx);
                        //System.out.println("found parent parameter: " + parentParamName);
                        //Check whether the parent and child paramter are already in the paramter table
                        if (!allParamNames.contains(paramName)) {
                            message.append("Error at line: ");
                            message.append(lineNr);
                            message.append("! Conditional child parameter found that was not specified yet!\n");
//                            JOptionPane.showMessageDialog(this, "Error at line: " + lineNr + "! Conditional child parameter found that was not specified yet!", "Parameter file misformed!", JOptionPane.ERROR_MESSAGE);
                        }
                        if (!allParamNames.contains(parentParamName)) {
                            message.append("Error at line: ");
                            message.append(lineNr);
                            message.append("! Conditional parent parameter found that was not specified yet!\n");
                            //JOptionPane.showMessageDialog(this, "Error at line: " + lineNr + "! Conditional parent parameter found that was not specified yet!", "Parameter file misformed!", JOptionPane.ERROR_MESSAGE);
                        }
                        childParams.add(paramName);//add the child to the list of conditioned parameter
                        parentParams.add(parentParamName); //add the parent parameter name to the list
                        paramDefinition = paramDefinition.substring(endIdx + 1, paramDefinition.length()).trim();
                        if (!paramDefinition.startsWith("in")) {
                            message.append("Error at line: ");
                            message.append(lineNr);
                            message.append("! Conditional parameter: ");
                            message.append(paramName);
                            message.append(" but no \"in\" key found\n");
                            //JOptionPane.showMessageDialog(this, "Error at line: " + lineNr + "! Conditional parameter: " + paramName + " but no \"in\" key found!", "Parameter file misformed!", JOptionPane.ERROR_MESSAGE);
                        }

                        paramDefinition = paramDefinition.substring(2, paramDefinition.length()).trim();
                        ArrayList<String> parentWithValues = new ArrayList<String>();
                        parentWithValues.add(parentParamName); //first Element is the parent name
                        if (paramDefinition.charAt(0) == '{') {
                            int endIx = paramDefinition.indexOf('}');
                            if (endIx == -1) {
                                message.append("Error at line");
                                message.append(lineNr);
                                break;
                            }
                            String[] vals = paramDefinition.substring(1, endIx).split(",");
                            for (String value : vals) {
                                parentWithValues.add(value.trim());
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Error at line: " + lineNr + "! Conditional parameter: " + paramName + " but no conditional values defintion starting with \"{\"!", "Parameter file misformed!", JOptionPane.ERROR_MESSAGE);
                        }
                        if (conditionalParams.contains(new Conditional(parentWithValues))) {
                            for (Conditional cond : conditionalParams) {
                                if (cond.condParamWithValues.equals(parentWithValues)) {
                                    cond.addChild(paramName);
                                }
                            }
                        } else {
                            conditionalParams.add(new Conditional(paramName, parentWithValues));
                        }
                    }
                }
                if (parameters == null || parameters.isEmpty()) { //stop if no parameters are present
                    JOptionPane.showMessageDialog(this, "No parameters specified yet.", "Parameters missing", JOptionPane.INFORMATION_MESSAGE);
                }
                graph.removeCells(graph.getChildVertices(graph.getDefaultParent())); //clean the graph if a graph alreaday exists
                Set<Node> nodes = new HashSet<Node>();
                List<Edge> edges = new LinkedList<Edge>();
                AndNode startNode = new AndNode(null, null); //generate the start AND node
                nodes.add(startNode);
                //a case splitting has to be done here
                //parent child case
                // 0        0   1
                // 0        1   2   add with full domain while adding the parents
                // 1        0   3
                // 1        1   4
                for (Parameter parameter : parameters) {
                    //if the parameter is not conditioned and it does not occur as a parent or if it is a parent but has real/integer domain
                    //we add it with its full domain
                    //case 1
                    if (!childParams.contains(parameter.getName())) {
                        if (!parentParams.contains(parameter.getName()) || (parameter.getDomain().getName().equalsIgnoreCase(RealDomain.name)) || (parameter.getDomain().getName().equalsIgnoreCase(IntegerDomain.name))) {
                            OrNode orNode = new OrNode(parameter);
                            AndNode andNode = new AndNode(parameter, parameter.getDomain());
                            edges.add(new Edge(startNode, orNode, 0));
                            edges.add(new Edge(orNode, andNode, 0));
                            nodes.add(orNode);
                            nodes.add(andNode);
                        }
                    }
                }
                //pass through all conditional parameters until they have been all inserted in the graph
                //it is possible that the conditionalParams are passed multiple times until the parents have been included
                boolean allInserted = false; //all parameters insterted in the graph?
                int iteration = 0;
                while (!allInserted) {
                    iteration++;
                    //search for a node that was not added to the graph
                    Conditional currentCond = null;
                    Parameter currentParameter = null;
                    boolean found = false;
                    for (Conditional conditional : conditionalParams) {
                        if (!conditional.isIncluded()) {
                            found = true;
                            currentCond = conditional;
                            for (Parameter parameter : parameters) {
                                if (parameter.getName().equals(conditional.condParamWithValues.get(0))) {
                                    currentParameter = parameter;
                                    break;
                                }
                            }
                            System.out.println("Found Paramter: " + currentParameter.getName() + " with conditional: " + currentCond.condParamWithValues);
                            //if the parameter is a parent add it with domain splitting
                            //case 3
                            if (parentParams.contains(currentParameter.getName())) {
                                //add the node as an or node!
                                OrNode orNode = new OrNode(currentParameter);

                                boolean parentCreated = false;
                                if (childParams.contains(currentParameter.getName())) {
                                    //we have to find the parent node with its domain where to draw the edge
                                    for (Conditional condi : conditionalParams) {
                                        if (condi.getChildren().contains(currentParameter.getName())) {
                                            String parent = condi.condParamWithValues.get(0);
                                            HashSet<String> vals = new HashSet<String>();
                                            for (int idx = 1; idx < condi.condParamWithValues.size(); idx++) {//skip the parent name
                                                vals.add(condi.condParamWithValues.get(idx));//Add the values to vals and to parentValues
                                            }
                                            CategoricalDomain cat = new CategoricalDomain(vals);
                                            for (Parameter par : parameters) {
                                                if (par.getName().equals(parent)) {
                                                    AndNode parentDomainNode = new AndNode(par, cat);//domain and node
                                                    parentDomainNode.setId(vals.toString());
                                                    if (!nodes.contains(parentDomainNode)) {
                                                        System.out.println("We have a problem, the parent: " + parentDomainNode.toString() + "  was not created yet!");
                                                        break;
                                                    } else {
                                                        for (Node nod : nodes) {
                                                            if (nod.equals(parentDomainNode)) {
                                                                nodes.add(orNode);
                                                                currentCond.setIncluded(true);
                                                                edges.add(new Edge(nod, orNode, 0));
                                                                parentCreated = true;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                    }
                                    if (!parentCreated) //skip this parameter until parents created
                                    {
                                        System.out.println("Skipped parameter: " + currentParameter.getName());
                                        continue;
                                    }
                                } else {

                                    nodes.add(orNode);
                                    edges.add(new Edge(startNode, orNode, 0));
                                    System.out.println("Inserted parameter: " + currentParameter.getName());
                                    currentCond.setIncluded(true);
                                }
                                //currentCond.setIncluded(true);
                                CategoricalDomain cat = null;
                                HashSet<String> parentValues = new HashSet<String>();
                                //go throgh all conditionals and get the domains of the condition and create as and nodes.
                                //collect all values of conditionals and the rest of the values are added as extra and node
                                //System.out.println("searching for parent parameter :" + parameter.getName());
                                for (Conditional condi : conditionalParams) {
                                    if (condi.condParamWithValues.get(0).equals(currentParameter.getName())) {
                                        //get conditional values
                                        HashSet<String> vals = new HashSet<String>();
                                        //System.out.println("Found specification: " + condi.condParamWithValues.toString());
                                        for (int idx = 1; idx < condi.condParamWithValues.size(); idx++) {//skip the parent name
                                            vals.add(condi.condParamWithValues.get(idx));//Add the values to vals and to parentValues
                                            parentValues.add(condi.condParamWithValues.get(idx));
                                        }
                                        cat = new CategoricalDomain(vals);
                                        AndNode andNode = new AndNode(currentParameter, cat);//domain and node
                                        andNode.setId(vals.toString());
                                        edges.add(new Edge(orNode, andNode, 0));
                                        nodes.add(andNode);
                                        //go through the list of children and add them if they are not parents
                                        for (String child : condi.childrenParams) {
                                            for (Parameter paramChild : parameters) {
                                                if (!parentParams.contains(paramChild.getName())) { //if parent skip adding it because we have to add it as a parent and do a domain splitting
                                                    if (paramChild.getName().equals(child)) {
                                                        OrNode orNode2 = new OrNode(paramChild);
                                                       /*if (nodes.contains(orNode2)) {
                                                        for (Node node : nodes) {
                                                        if (node.equals(orNode2)) {
                                                        System.err.println("ornode already in the graph");
                                                        edges.add(new Edge(andNode, node, 0));
                                                        break;
                                                        }
                                                        }
                                                        } else {*/ //case 2
                                                        edges.add(new Edge(andNode, orNode2, 0));
                                                        if (!parentParams.contains(child)) { //only if the child is not a parent it can be added with full domain
                                                            AndNode andNode2 = new AndNode(paramChild, paramChild.getDomain());
                                                            andNode2.setId(paramChild.getDomain().toString());
                                                            edges.add(new Edge(orNode2, andNode2, 0));
                                                            nodes.add(orNode2);
                                                            nodes.add(andNode2);
                                                        }
                                                        //}
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                //check which values of the parent where not contained in and nodes of children and add this values as an and node
                                Set<String> allValues = ((CategoricalDomain) (currentParameter.getDomain())).getCategories();
                                // System.out.println("Parmeter " + parameter.getName() + " has categories" + allValues.toString());
                                HashSet<String> valuesToAdd = new HashSet<String>();

                                for (String value : allValues) {
                                    if (!parentValues.contains(value)) {
                                        valuesToAdd.add(value);
                                    }
                                }
                                //System.out.println("Mising values: " + valuesToAdd);
                                if (!valuesToAdd.isEmpty()) {
                                    CategoricalDomain d = new CategoricalDomain(valuesToAdd);
                                    // System.out.println("Missing Domain" + d.toString());
                                    AndNode restAndNode = new AndNode(currentParameter, d);
                                    //System.out.println("And Node : "+restAndNode);
                                    restAndNode.setId(valuesToAdd.toString());
                                    nodes.add(restAndNode);
                                    edges.add(new Edge(orNode, restAndNode, 0));

                                }
                            }
                        }
                    }
                    if (!found) {
                        allInserted = true;
                    }

                }
                System.out.println("number of iterations needed to create the graph: "+ iteration);
                renderGraph(nodes, edges, startNode);

            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ParameterGraphEditor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ParameterGraphEditor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ParameterGraphEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (message.length() != 0) {
                JOptionPane.showMessageDialog(ParameterGraphEditor.this, message.toString(), "Errors in parameter specification!", JOptionPane.INFORMATION_MESSAGE);
            }

            JOptionPane.showMessageDialog(ParameterGraphEditor.this, "Parameter specification imported and graph generated", "SMAC parameter import", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnImportSMACStyleParamsActionPerformed

    public void evtKeyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            for (Object cell : graph.getSelectionCells()) {
                // can't remove root node
                if (cell != rootNode) {
                    graph.removeCells(new Object[]{cell});
                }
            }
            e.consume();
        }
    }

    public boolean buildParameterGraph(Set<Node> nodes, List<Edge> edges, mxCell currentCell) {
        if (currentCell.isVertex()) {
            Node node = (Node) currentCell.getValue();
            node.setId(currentCell.getId());
            nodes.add(node);
            if (node instanceof OrNode) {
                if (graph.getOutgoingEdges(currentCell).length == 0) {
                    return false;
                }
            }
            for (Object o : graph.getOutgoingEdges(currentCell)) {
                mxCell cell = (mxCell) o;
                if (cell.getValue() == null || cell.getValue() instanceof String) {
                    edges.add(new Edge(node, (Node) cell.getTarget().getValue(), 0));
                } else {
                    Edge edge = (Edge) cell.getValue();
                    edge.setSource(node);
                    edge.setTarget((Node) cell.getTarget().getValue());
                    edges.add(edge);
                }
                if (!buildParameterGraph(nodes, edges, (mxCell) cell.getTarget())) {
                    return false;
                }
            }
        }
        return true;
    }

    private ParameterGraph getParameterGraph() throws Exception {
        List<mxCell> roots = findRoots();

        if (roots.size() != 1) {
            throw new Exception("Found " + roots.size() + " roots.");
        }
        mxCell root = (mxCell) roots.get(0);
        if (!(root.getValue() instanceof AndNode) || root != rootNode) {
            // should never happen
            throw new Exception("Invalid root node.");
        }
        Set<Node> nodes = new HashSet<Node>();
        List<Edge> edges = new LinkedList<Edge>();
        if (!buildParameterGraph(nodes, edges, root)) {
            // happens if not all leafs are AND nodes
            throw new Exception("Invalid graph.");
        }
        return new ParameterGraph(nodes, edges, parameters, (AndNode) root.getValue());
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDefaultGraph;
    private javax.swing.JButton btnImportSMACStyleParams;
    private javax.swing.JButton btnLayout;
    private javax.swing.JButton btnLoadFromFile;
    private javax.swing.JButton btnParameters;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSaveToFile;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
