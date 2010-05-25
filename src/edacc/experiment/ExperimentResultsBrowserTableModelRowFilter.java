/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.experiment;

import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;

/**
 *
 * @author simon
 */
public class ExperimentResultsBrowserTableModelRowFilter extends RowFilter<ExperimentResultsBrowserTableModel, Integer> {

    private String instanceName = null;
    private String solverName = null;
    private Integer statusCode = null;

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public void setSolverName(String solverName) {
        this.solverName = solverName;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getSolverName() {
        return solverName;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    @Override
    public boolean include(Entry<? extends ExperimentResultsBrowserTableModel, ? extends Integer> entry) {
        return (entry != null)
                && (instanceName == null || instanceName.equals(entry.getModel().getInstance(entry.getIdentifier()).getName()))
                && (solverName == null || solverName.equals(entry.getModel().getSolver(entry.getIdentifier()).getName()))
                && (statusCode == null || statusCode.equals(entry.getModel().getStatusCode(entry.getIdentifier())));
    }

}
