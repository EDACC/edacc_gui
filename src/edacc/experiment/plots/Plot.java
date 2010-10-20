package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.Experiment;
import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultDAO;
import edacc.model.ExperimentResultHasProperty;
import edacc.model.Instance;
import edacc.model.InstanceHasProperty;
import edacc.model.Property;
import edacc.model.PropertyDAO;
import edacc.satinstances.ConvertException;
import edacc.satinstances.PropertyValueType;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.rosuda.JRI.Rengine;

/**
 * The abstract plot class. Plot classes have to extend it.
 * @author simon
 */
public abstract class Plot {

    public String htmlHeader = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\">\n"
            + "<html>\n"
            + "<head>\n"
            + "<style type=\"text/css\">\n";
    public final String htmlFooter = "</body>\n"
            + "</html>";
    public static final String[] colors = {"red", "green", "blue", "darkgoldenrod1", "darkolivegreen", "darkorchid", "deeppink", "darkgreen", "blue4"};
    public static final int ALLRUNS = -3;
    public static final int AVERAGE = -2;
    public static final int MEDIAN = -1;
    public static final String ALLRUNS_TEXT = "all runs";
    public static final String AVERAGE_TEXT = "all runs - average";
    public static final String MEDIAN_TEXT = "all runs - median";
    protected ExperimentController expController;
    public static Property PROP_CPUTIME;
    private static HashMap<ResultIdentifier, ExperimentResult> resultMap;
    private static Timestamp lastUpdated;
    private static Experiment experiment;

    protected Plot(ExperimentController expController) {
        try {
            InputStream is = this.getClass().getResourceAsStream("/edacc/experiment/resources/stylesheet.css");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                htmlHeader += line + '\n';
            }
        } catch (IOException ex) {
            // TODO: ...
            ex.printStackTrace();
        }
        htmlHeader += htmlHeader + "</style>\n"
                + "</head>\n"
                + "<body>";
        this.expController = expController;
    }

    /**
     * This method has to be called to use the higher level methods of this class
     * @throws SQLException
     * @throws Exception
     */
    protected static void initialize(ExperimentController expController) throws SQLException, Exception {
        int count = ExperimentResultDAO.getCountByExperimentId(expController.getActiveExperiment().getId());
        Timestamp ts = ExperimentResultDAO.getLastModifiedByExperimentId(expController.getActiveExperiment().getId());
        if (resultMap == null || count != resultMap.size() || !ts.equals(lastUpdated) || experiment != expController.getActiveExperiment()) {
            if (resultMap == null) {
                resultMap = new HashMap<ResultIdentifier, ExperimentResult>();
            } else {
                resultMap.clear();
            }
            ArrayList<ExperimentResult> results = ExperimentResultDAO.getAllByExperimentId(expController.getActiveExperiment().getId());
            for (ExperimentResult result : results) {
                resultMap.put(new ResultIdentifier(result.getSolverConfigId(), result.getInstanceId(), result.getRun()), result);
            }
            lastUpdated = ts;
            experiment = expController.getActiveExperiment();
        }
    }

    public static ArrayList<Property> getResultProperties() throws Exception {
        ArrayList<Property> res = new ArrayList<Property>();
        if (PROP_CPUTIME == null) {
            PROP_CPUTIME = new Property();
            PROP_CPUTIME.setName("CPU-Time");
        }
        res.add(PROP_CPUTIME);
        res.addAll(PropertyDAO.getAllResultProperties());
        return res;
    }

    public static ArrayList<Property> getInstanceProperties() throws Exception {
        ArrayList<Property> res = new ArrayList<Property>();
        res.addAll(PropertyDAO.getAllInstanceProperties());
        return res;
    }

    /**
     * Returns a Vector of all ExperimentResults in the current experiment with the solverConfig id and instance id specified
     * @param solverConfigId the solverConfig id of the ExperimentResults
     * @param instanceId the instance id of the ExperimentResults
     * @return returns an empty vector if there are no such ExperimentResults
     */
    public static ArrayList<ExperimentResult> getResults(int solverConfigId, int instanceId) {
        ArrayList<ExperimentResult> res = new ArrayList<ExperimentResult>();
        for (int i = 0; i < experiment.getNumRuns(); i++) {
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
    public static ExperimentResult getResult(int solverConfigId, int instanceId, int run) {
        ExperimentResult res = resultMap.get(new ResultIdentifier(solverConfigId, instanceId, run));
        // if the result is not in our map or it is not a verified result then return null
        // TODO: überdenken
        // if (res == null || !String.valueOf(res.getResultCode().getValue()).startsWith("1")) {
        //     return null;
        // }
        return res;
    }

    private static Double transformPropertyValueTypeToDouble(PropertyValueType type, String value) {
        Double res = null;
        try {
            if (type.getJavaType() == Integer.class) {
                res = new Double((Integer) type.getJavaTypeRepresentation(value));
            } else if (type.getJavaType() == Float.class) {
                res = new Double((Float) type.getJavaTypeRepresentation(value));
            } else if (type.getJavaType() == Double.class) {
                res = (Double) type.getJavaTypeRepresentation(value);
            }
        } catch (ConvertException ex) {
            return null;
        }
        return res;
    }

    public static Double getValue(ExperimentResult result, Property property) {
        if (property == PROP_CPUTIME) {
            if (!String.valueOf(result.getResultCode().getValue()).startsWith("1")) {
                return new Double(experiment.getCPUTimeLimit());
            }
            return Double.valueOf(result.getResultTime());
        } else {
            if (!String.valueOf(result.getResultCode().getValue()).startsWith("1")) {
                return null;
            }
            ExperimentResultHasProperty erhsp = result.getPropertyValues().get(property.getId());

            if (erhsp == null || erhsp.getValue().isEmpty()) {
                return null;
            }
            return transformPropertyValueTypeToDouble(property.getPropertyValueType(), erhsp.getValue().get(0));
        }
    }

    public static Double getValue(Instance instance, Property property) {
        InstanceHasProperty ihip = instance.getPropertyValues().get(property.getId());
        if (ihip == null) {
            return null;
        }
        return transformPropertyValueTypeToDouble(property.getPropertyValueType(), ihip.getValue());
    }

    /**
     * Calculates the average property value for the given ExperimentResults, i.e. the sum of the property values divided by the count
     * @param results
     * @param property
     * @return
     */
    public static Double getAverage(ArrayList<ExperimentResult> results, Property property) {
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
    public static Double getMedian(ArrayList<ExperimentResult> results, Property property) {
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

    public static ArrayList<Point2D> getPoints(Rengine re, double[] xs, double[] ys) {
        ArrayList<Point2D> res = new ArrayList<Point2D>();
        if (xs.length != ys.length) {
            return res;
        }
        for (int i = 0; i < xs.length; i++) {
            res.add(new Point2D.Double(xs[i], ys[i]));
        }
        return res;
    }

    /**
     * Will be called to reinitialize the dependency gui values.
     * @throws Exception can throw an exception
     */
    public static void loadDefaultValues(ExperimentController expController) throws Exception {
    }

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
    public String getAdditionalInformations() {
        return null;
    }

    /**
     * Updates the `static` dependencies of the static plot type to the data for the
     * current plot instance.
     */
    public abstract void updateDependencies();
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
