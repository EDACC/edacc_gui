/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.experiment;

import java.sql.SQLException;
import javax.swing.table.AbstractTableModel;
import java.util.Vector;
import edacc.model.ExperimentResult;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.Parameter;
import edacc.model.ParameterDAO;
import edacc.model.ParameterInstance;
import edacc.model.ParameterInstanceDAO;
import edacc.model.Solver;
import edacc.model.SolverConfiguration;
import edacc.model.SolverConfigurationDAO;
import edacc.model.SolverDAO;
import java.util.HashMap;

/**
 *
 * @author daniel
 */
public class ExperimentResultsBrowserTableModel extends AbstractTableModel {

    private Vector<ExperimentResult> jobs;
    private String[] columns = {"ID", "Solver", "Parameters", "Instance", "Run", "Result File", "Time", "Seed", "Status code"};
    private boolean[] visible = {true, true, true, true, true, true, true, true, true};
    private HashMap<Integer, Vector<ParameterInstance>> parameterInstances;

    public void setJobs(Vector<ExperimentResult> jobs) throws SQLException {
        this.jobs = jobs;
        parameterInstances = new HashMap<Integer, Vector<ParameterInstance>>();
    }

    private int getIndexForColumn(int col) {
        for (int i = 0; i < visible.length; i++) {
            if (visible[i]) {
                col--;
            }
            if (col == -1) {
                return i;
            }
        }
        return 0;
    }

    public String[] getAllColumnNames() {
        return columns;
    }

    @Override
    public int getRowCount() {
        if (jobs == null) {
            return 0;
        }
        return jobs.size();
    }

    @Override
    public int getColumnCount() {
        int res = 0;
        for (int i = 0; i < visible.length; i++) {
            if (visible[i]) {
                res++;
            }
        }
        return res;
    }

    @Override
    public String getColumnName(int col) {
        return columns[getIndexForColumn(col)];
    }

    @Override
    public Class getColumnClass(int col) {
        return getValueAt(0, getIndexForColumn(col)).getClass();
    }

    public void setColumnVisibility(boolean[] visible) {
        this.visible = visible;
        this.fireTableStructureChanged();
    }

    public boolean[] getColumnVisibility() {
        return visible;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ExperimentResult j = jobs.get(rowIndex);

        try {
            Instance instance = InstanceDAO.getById(j.getInstanceId());
            SolverConfiguration sc = SolverConfigurationDAO.getSolverConfigurationById(j.getSolverConfigId());
            Vector<ParameterInstance> params = parameterInstances.get(sc.getId());
            if (params == null) {
                params = ParameterInstanceDAO.getBySolverConfigId(sc.getId());
                parameterInstances.put(sc.getId(), params);
            }
            String paramString = "";

                for (ParameterInstance param : params) {
                    Parameter solverParameter = ParameterDAO.getById(param.getParameter_id());
                    paramString += solverParameter.getName() + " = " + param.getValue();
                    if (params.lastElement() != param) {
                        paramString += ", ";
                    }
                }
            Solver solver = SolverDAO.getById(sc.getSolver_id());
            String instanceName = instance.getName();
            String solverName = solver.getName();
            if (columnIndex != -1) {
                columnIndex = getIndexForColumn(columnIndex);
            }
            switch (columnIndex) {
                case 0:
                    return j.getId();
                case 1:
                    return solverName;
                case 2:
                    return paramString;
                case 3:
                    return instanceName;
                case 4:
                    return j.getRun();
                case 5:
                    return j.getResultFileName();
                case 6:
                    return j.getTime();
                case 7:
                    return j.getSeed();
                case 8:
                    return j.getStatus();
                case -1:
                    return j.getStatus();
                default:
                    return "";
            }
        } catch (SQLException e) {
            return "Error while loading";
        }
    }
}
