package edacc.experiment.tabs.solver;

import edacc.model.Experiment;
import edacc.model.Parameter;
import edacc.model.ParameterDAO;
import edacc.model.ParameterInstance;
import edacc.model.ParameterInstanceDAO;
import edacc.model.Solver;
import edacc.model.SolverBinaries;
import edacc.model.SolverConfiguration;
import edacc.model.SolverDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * @author simon
 */
public class SolverConfigurationEntry {

    private SolverConfiguration solverConfig;
    private Solver solver;
    private String hint;
    private SolverBinaries solverBinary;
    private String name;
    private int seedGroup;
    private SolverConfigEntryTableModel tableModel;
    private Experiment experiment;

    public SolverConfigurationEntry(SolverConfiguration solverConfig, Experiment experiment) throws SQLException {
        this(SolverDAO.getById(solverConfig.getSolverBinary().getIdSolver()), experiment);

        this.solverConfig = solverConfig;
        hint = solverConfig.getHint();
        name = solverConfig.getName();
        seedGroup = solverConfig.getSeed_group();
        solverBinary = solverConfig.getSolverBinary();
        tableModel.setParameterInstances(ParameterInstanceDAO.getBySolverConfig(solverConfig));
    }

    public SolverConfigurationEntry(Solver solver, Experiment experiment) throws SQLException {
        tableModel = new SolverConfigEntryTableModel();
        this.solver = solver;
        this.experiment = experiment;
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        params.addAll(ParameterDAO.getParameterFromSolverId(solver.getId()));
        tableModel.setParameters(params);
        name = solver.getName();
        seedGroup = 0;
        if (!solver.getSolverBinaries().isEmpty()) {
            solverBinary = solver.getSolverBinaries().get(0);
        }
        hint = "";
    }

    public SolverConfigurationEntry(SolverConfigurationEntry other) throws SQLException {
        this(other.getSolver(), other.getExperiment());

        this.assign(other);
    }

    /**
     * Saves all new and modified parameter instances to the database.
     * @throws SQLException
     */
    public void saveParameterInstances() throws SQLException {
        ArrayList<ParameterInstance> parameterVector = new ArrayList<ParameterInstance>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((Boolean) tableModel.getValueAt(i, 0)) {
                Parameter p = (Parameter) tableModel.getValueAt(i, 5);
                ParameterInstance pi = (ParameterInstance) tableModel.getValueAt(i, 6);
                if (pi == null) {
                    pi = ParameterInstanceDAO.createParameterInstance(p.getId(), solverConfig, (String) tableModel.getValueAt(i, 2));
                    parameterVector.add(pi);
                }
                if (!pi.getValue().equals((String) tableModel.getValueAt(i, 3))) {
                    pi.setValue((String) tableModel.getValueAt(i, 3));
                    ParameterInstanceDAO.setModified(pi);
                    ParameterInstanceDAO.save(pi);
                }
            } else {
                ParameterInstance pi = (ParameterInstance) tableModel.getValueAt(i, 6);
                if (pi != null) {
                    ParameterInstanceDAO.setDeleted(pi);
                    ParameterInstanceDAO.save(pi);
                    tableModel.removeParameterInstance(pi);
                }
            }
        }
        if (parameterVector.size() > 0) {
            tableModel.setParameterInstances(parameterVector);
        }
    }

    /**
     * Checks for unsaved data, i.e. checks iff the seed group, the parameter instances or the idx have been changed.<br/>
     * If the seed group is not a valid integer it will be substituted and used as 0.
     * @param idx the idx to check the equality. If <code>idx == -1</code> the idx and name of the solver configuration will not be checked
     * @return <code>true</code>, if and only if data is unsaved, false otherwise
     */
    public boolean isModified() {
        if (solverConfig == null
                || solverConfig.getSeed_group() != seedGroup
                || !name.equals(solverConfig.getName())
                || solverConfig.getSolverBinary() != solverBinary
                || !hint.equals(solverConfig.getHint())) {
            return true;
        }
        return tableModel.isModified();
    }

    public boolean hasEmptyValues() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ("instance".equals((String) tableModel.getValueAt(i, 1))
                    || "seed".equals((String) tableModel.getValueAt(i, 1))) {
                continue;
            }
            if ((Boolean) tableModel.getValueAt(i, 0)) {
                if (tableModel.getParameters().get(i).getHasValue()) {
                    if ("".equals(tableModel.getValueAt(i, 3))) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeedGroup() {
        return seedGroup;
    }

    public void setSeedGroup(int seedGroup) {
        this.seedGroup = seedGroup;
    }

    public Solver getSolver() {
        return solver;
    }

    public void setSolver(Solver solver) {
        this.solver = solver;
    }

    public SolverBinaries getSolverBinary() {
        return solverBinary;
    }

    public void setSolverBinary(SolverBinaries solverBinary) {
        this.solverBinary = solverBinary;
    }

    public SolverConfiguration getSolverConfig() {
        return solverConfig;
    }

    public void setSolverConfig(SolverConfiguration solverConfig) {
        if (solverConfig == null) {
            tableModel.setParameterInstances(null);
        }
        this.solverConfig = solverConfig;
    }

    public SolverConfigEntryTableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(SolverConfigEntryTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public void assign(SolverConfigurationEntry other) {
        if (this.getSolver() != other.getSolver()) {
            return;
        }
        
        this.hint = other.hint;
        this.name = other.name;
        this.seedGroup = other.seedGroup;
        this.solverBinary = other.solverBinary;

        for (int i = 0; i < other.tableModel.getRowCount(); i++) {
            tableModel.setValueAt(other.tableModel.getValueAt(i, 3), i, 3);
            tableModel.setValueAt(other.tableModel.getValueAt(i, 0), i, 0);
        }
    }
}
