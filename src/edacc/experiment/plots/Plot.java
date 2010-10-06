package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultDAO;
import edacc.model.ExperimentResultHasSolverProperty;
import edacc.model.ExperimentResultHasSolverPropertyDAO;
import edacc.model.Instance;
import edacc.model.InstanceProperty;
import edacc.model.SolverProperty;
import edacc.model.SolverPropertyDAO;
import edacc.satinstances.ConvertException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rosuda.JRI.Rengine;

/**
 * The abstract plot class. Plot classes have to extend it.
 * @author simon
 */
public abstract class Plot {

    public static final String[] colors = {"red", "green", "blue", "darkgoldenrod1", "darkolivegreen", "darkorchid", "deeppink", "darkgreen", "blue4"};
    public static int ALLRUNS = -3;
    public static int AVERAGE = -2;
    public static int MEDIAN = -1;
    protected ExperimentController expController;
    // "constants" for solver properties
    public static SolverProperty PROP_CPUTIME;
    private HashMap<ResultIdentifier, ExperimentResult> resultMap;

    protected Plot(ExperimentController expController) {
        this.expController = expController;
    }

    protected void initializeResults() throws SQLException, Exception {
        if (resultMap == null) {
            resultMap = new HashMap<ResultIdentifier, ExperimentResult>();
        } else {
            resultMap.clear();
        }
        ArrayList<ExperimentResult> results = ExperimentResultDAO.getAllByExperimentId(expController.getActiveExperiment().getId());
        ExperimentResultHasSolverPropertyDAO.assign(results, expController.getActiveExperiment().getId());
        for (ExperimentResult result : results) {
            resultMap.put(new ResultIdentifier(result.getSolverConfigId(), result.getInstanceId(), result.getRun()), result);
        }
    }

    public static ArrayList<SolverProperty> getSolverProperties() throws Exception {
        ArrayList<SolverProperty> res = new ArrayList<SolverProperty>();
        if (PROP_CPUTIME == null) {
            PROP_CPUTIME = new SolverProperty();
            PROP_CPUTIME.setName("CPU-Time");
        }
        res.add(PROP_CPUTIME);
        res.addAll(SolverPropertyDAO.getAll());
        return res;
    }

    /**
     * Returns a Vector of all ExperimentResults in the current experiment with the solverConfig id and instance id specified
     * @param solverConfigId the solverConfig id of the ExperimentResults
     * @param instanceId the instance id of the ExperimentResults
     * @return returns an empty vector if there are no such ExperimentResults
     */
    public ArrayList<ExperimentResult> getResults(int solverConfigId, int instanceId) {
        ArrayList<ExperimentResult> res = new ArrayList<ExperimentResult>();
        for (int i = 0; i < expController.getActiveExperiment().getNumRuns(); i++) {
            ExperimentResult result = getResult(solverConfigId, instanceId, i);
            if (result != null) {
                res.add(result);
            }
        }
        return res;
    }

    /**
     * Returns an ExperimentResult identified by solverConfig id, instance id and run for the current experiment.
     * @param solverConfigId the solverConfig id for the ExperimentResult
     * @param instanceId the instance id for the ExperimentResult
     * @param run the run
     * @return returns null if there is no such ExperimentResult
     */
    public ExperimentResult getResult(int solverConfigId, int instanceId, int run) {
        ExperimentResult res = resultMap.get(new ResultIdentifier(solverConfigId, instanceId, run));
        // if the result is not in our map or it is not a verified result then return null
        if (res == null || !String.valueOf(res.getResultCode().getValue()).startsWith("1")) {
            return null;
        }
        return res;
    }

    public Double getValue(ExperimentResult result, SolverProperty property) {
        if (property == PROP_CPUTIME) {
            return Double.valueOf(result.getResultTime());
        } else {
            ExperimentResultHasSolverProperty erhsp = result.getPropertyValues().get(property.getId());
            if (erhsp == null || erhsp.getValue().isEmpty()) {
                return null;
            }
            Double res = null;
            try {
                if (property.getPropertyValueType().getJavaType() == Integer.class) {
                    res = new Double((Integer) property.getPropertyValueType().getJavaTypeRepresentation(erhsp.getValue().get(0)));
                } else if (property.getPropertyValueType().getJavaType() == Float.class) {
                    res = new Double((Float) property.getPropertyValueType().getJavaTypeRepresentation(erhsp.getValue().get(0)));
                } else if (property.getPropertyValueType().getJavaType() == Double.class) {
                    res = (Double) property.getPropertyValueType().getJavaTypeRepresentation(erhsp.getValue().get(0));
                }
            } catch (ConvertException ex) {
                return null;
            }
            return res;
        }
    }

    public Double getValue(Instance instance, InstanceProperty property) {
        //InstanceHasInstanceProperty ihip = instance.get
        return 0.;
    }

    /**
     * Calculates the average property value for the given ExperimentResults, i.e. the sum of the property values divided by the count
     * @param results
     * @param property
     * @return
     */
    public Double getAverage(ArrayList<ExperimentResult> results, SolverProperty property) {
        if (results.isEmpty()) {
            return null;
        }

        double res = 0.;
        int count = 0;
        for (ExperimentResult result : results) {
            Double value = getValue(result, property);
            if (value != null) {
                count++;
                res += value;
            }
        }
        if (count == 0) {
            return null;
        } else {
            return res / (double) count;
        }
    }

    /**
     * Returns the median property value of the given ExperimentResults
     * @param results
     * @param property
     * @return
     */
    public Double getMedian(ArrayList<ExperimentResult> results, SolverProperty property) {
        if (results.isEmpty()) {
            return null;
        }

        ArrayList<Double> values = new ArrayList<Double>();
        for (ExperimentResult res : results) {
            Double value = getValue(res, property);
            if (value != null) {
                values.add(value);
            }
        }
        if (values.isEmpty()) {
            return null;
        }
        Collections.sort(values);
        if (values.size() % 2 == 1) {
            // this is the median
            return values.get(values.size() / 2);
        } else {
            // we have two medians, so we use the average of both
            return (values.get((values.size() - 1) / 2) + values.get(values.size() / 2)) / 2;
        }

    }

    public ArrayList<double[]> getPoints(Rengine re, double[] xs, double[] ys) {

        ArrayList<double[]> res = new ArrayList<double[]>();
        if (xs.length != ys.length) {
            return res;
        }

        for (int i = 0; i
                < xs.length; i++) {
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
    public static void loadDefaultValues(ExperimentController expController) throws Exception {
    }

    ;

    /**
     * Returns the dependencies for that plot.
     * @return the dependencies
     */
    public static Dependency[] getDependencies() {
        return null;
    }

    public abstract String getPlotTitle();

    public static String getTitle() {
        return "";
    }

    ;

    /**
     * Plots the plot to the R-engine
     * @param engine
     * @throws SQLException
     * @throws DependencyException
     */
    public abstract void plot(Rengine engine, ArrayList<PointInformation> pointInformations) throws Exception;

    /**
     * Some warnings while generating the plot.
     * @return null for no warning, any String otherwise
     */
    public String getWarning() {
        return null;
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
