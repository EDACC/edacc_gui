package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultDAO;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import org.rosuda.JRI.Rengine;

/**
 * The abstract plot class. Plot classes have to extend it.
 * @author simon
 */
public abstract class Plot {
    protected ExperimentController expController;
    protected Rengine rengine;
    private HashMap<ResultIdentifier, ExperimentResult> resultMap;

    protected Plot(ExperimentController expController) {
        this.expController = expController;
    }

    private void initializeResults() throws SQLException {
        if (resultMap == null) {
            resultMap = new HashMap<ResultIdentifier, ExperimentResult>();
        } else {
            resultMap.clear();
        }
        Vector<ExperimentResult> results = ExperimentResultDAO.getAllByExperimentId(expController.getActiveExperiment().getId());
        for (ExperimentResult result : results) {
            resultMap.put(new ResultIdentifier(result.getSolverConfigId(), result.getInstanceId(), result.getRun()), result);
        }
    }

    /**
     * Returns a Vector of all ExperimentResults in the current experiment with the solverConfig id and instance id specified
     * @param solverConfigId the solverConfig id of the ExperimentResults
     * @param instanceId the instance id of the ExperimentResults
     * @return returns an empty vector if there are no such ExperimentResults
     */
    public Vector<ExperimentResult> getResults(int solverConfigId, int instanceId) {
        Vector<ExperimentResult> res = new Vector<ExperimentResult>();
        for (int i = 0; i < expController.getActiveExperiment().getNumRuns(); i++) {
            ExperimentResult result = resultMap.get(new ResultIdentifier(solverConfigId, instanceId, i));
            if (result != null) {
                res.add(result);
            }
        }
        return res;
    }

    /**
     * Returns a ExperimentResult identified by solverConfig id, instance id and run for the current experiment
     * @param solverConfigId the solverConfig id for the ExperimentResult
     * @param instanceId the instance id for the ExperimentResult
     * @param run the run
     * @return returns null if there is no such ExperimentResult
     */
    public ExperimentResult getResult(int solverConfigId, int instanceId, int run) {
        return resultMap.get(new ResultIdentifier(solverConfigId, instanceId, run));
    }

    /**
     * Calculates the average time for the given ExperimentResults, i.e. the sum of the times divided by the count
     * @param results
     * @return
     */
    public double getAverageTime(Vector<ExperimentResult> results) {
        if (results.size() == 0) {
            throw new IllegalArgumentException("There are no results.");
        }
        double res = 0.;
        for (ExperimentResult result : results) {
            res += result.getTime();
        }
        res /= results.size();
        return res;
    }

    /**
     * Returns the median time of the given ExperimentResults
     * @param results
     * @return
     */
    public double getMedianTime(Vector<ExperimentResult> results) {
        if (results.size() == 0) {
            throw new IllegalArgumentException("There are no results.");
        }
        float[] times = new float[results.size()];
        int k = 0;
        for (ExperimentResult res : results) {
            times[k++] = res.getTime();
        }
        java.util.Arrays.sort(times);
        if (times.length % 2 == 1) {
            // this is the median
            return times[times.length / 2];
        } else {
            // we have two medians, so we use the average of both
            return (times[(times.length - 1) / 2] + times[times.length / 2]) / 2;
        }
    }

    public Vector<double[]> getPoints(Rengine re, double[] xs, double[] ys) {

        Vector<double[]> res = new Vector<double[]>();
        if (xs.length != ys.length) {
            return res;
        }
        for (int i = 0; i < xs.length; i++) {
            double[] tmp = new double[2];
            tmp[0] = xs[i];
            tmp[1] = ys[i];
            res.add(tmp);
        }
        return res;
    }

    /**
     * Will be called to reinitialize the dependency gui values.
     * @throws Exception can throw an exception
     */
    public abstract void loadDefaultValues() throws Exception;

    /**
     * Returns the dependencies for that plot.
     * @return the dependencies
     */
    public abstract Dependency[] getDependencies();

    public abstract String getTitle();
    /**
     * Plots the plot to the R-engine
     * @param engine
     * @throws SQLException
     * @throws DependencyException
     */
    public void plot(Rengine engine, Vector<PointInformation> pointInformations) throws SQLException, DependencyException {
        this.rengine = engine;
        initializeResults();
    }
}

class ResultIdentifier {

    int solverConfigId;
    int instanceId;
    int run;

    public ResultIdentifier(int solverConfigId, int instanceId, int run) {
        this.solverConfigId = solverConfigId;
        this.instanceId = instanceId;
        this.run = run;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResultIdentifier other = (ResultIdentifier) obj;
        if (this.solverConfigId != other.solverConfigId) {
            return false;
        }
        if (this.instanceId != other.instanceId) {
            return false;
        }
        if (this.run != other.run) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.solverConfigId;
        hash = 53 * hash + this.instanceId;
        hash = 53 * hash + this.run;
        return hash;
    }
}
