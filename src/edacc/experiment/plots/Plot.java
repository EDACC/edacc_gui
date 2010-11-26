package edacc.experiment.plots;

import edacc.experiment.ExperimentController;
import edacc.model.ExperimentResult;
import edacc.model.Property;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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

    protected Plot(ExperimentController expController) {
        try {
            InputStream is = this.getClass().getResourceAsStream("/edacc/experiment/resources/stylesheet.css");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                htmlHeader += line + '\n';
            }
            htmlHeader += htmlHeader + "</style>\n"
                    + "</head>\n"
                    + "<body>";
        } catch (IOException ex) {
            htmlHeader = "<body>";
        }
        this.expController = expController;
    }

    /**
     * Calculates the average property value for the given ExperimentResults, i.e. the sum of the property values divided by the count
     * @param results
     * @param property
     * @return the average of the ExperimentResult values
     */
    public Double getAverage(ArrayList<ExperimentResult> results, Property property) {
        if (results.isEmpty()) {
            return null;
        }

        double res = 0.;
        int count = 0;
        for (ExperimentResult result : results) {
            Double value = expController.getValue(result, property);
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
     * @return the median of the ExperimentResult values
     */
    public Double getMedian(ArrayList<ExperimentResult> results, Property property) {
        if (results.isEmpty()) {
            return null;
        }

        ArrayList<Double> values = new ArrayList<Double>();
        for (ExperimentResult res : results) {
            Double value = expController.getValue(res, property);
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
