package edacc.experiment;

import edacc.model.ExperimentResultStatus;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;

/**
 *
 * @author simon
 */
public class ExperimentResultsBrowserTableModelRowFilter extends RowFilter<ExperimentResultsBrowserTableModel, Integer> {

    private String instanceName = null;
    private String solverName = null;
    private ExperimentResultStatus statusCode = null;

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public void setSolverName(String solverName) {
        this.solverName = solverName;
    }

    public void setStatus(ExperimentResultStatus statusCode) {
        this.statusCode = statusCode;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getSolverName() {
        return solverName;
    }

    public ExperimentResultStatus getStatus() {
        return statusCode;
    }

    @Override
    public boolean include(Entry<? extends ExperimentResultsBrowserTableModel, ? extends Integer> entry) {
        return (entry != null)
                && (instanceName == null || instanceName.equals(entry.getModel().getInstance(entry.getIdentifier()).getName()))
                && (solverName == null || solverName.equals(entry.getModel().getSolver(entry.getIdentifier()).getName()))
                && (statusCode == null || statusCode.equals(entry.getModel().getStatus(entry.getIdentifier())));
    }
}
