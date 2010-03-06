/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.experiment;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;
import edacc.model.ExperimentResult;
import edacc.model.Instance;
import edacc.model.Solver;

/**
 *
 * @author daniel
 */
public class ExperimentResultsBrowserTableModel extends AbstractTableModel {
    protected Vector<ExperimentResult> jobs;
    private String[] columns = {"ID", "SolverConfig ID", "Instance ID", "Run", "Result File", "Time", "Seed"};

    public int getRowCount() {
        if (jobs == null) return 0;
        return jobs.size();
    }

    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Class getColumnClass(int col) {
        return getValueAt(0, col).getClass();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        ExperimentResult j = jobs.get(rowIndex);
        switch (columnIndex) {
            case 0: 
                return j.getId();
            case 1:
                return j.getSolverConfigId();
            case 2:
                return j.getInstanceId();
            case 3:
                return j.getRun();
            case 4:
                return j.getResultFileName();
            case 5:
                return j.getTime();
            case 6:
                return j.getSeed();
            case -1:
                return j.getStatus();
            default:
                return "";
        }
    }

}
