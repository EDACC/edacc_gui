package edacc.experiment.plots;

import java.awt.Point;

/**
 *
 * @author simon
 */
public class PointInformation {
    private double[] point;
    private String description;

    public PointInformation(double[] point, String description) {
        this.point = point;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double[] getPoint() {
        return point;
    }

    @Override
    public String toString() {
        return "[PointInformation point=" + point + ", description="+description + "]";
    }




}
