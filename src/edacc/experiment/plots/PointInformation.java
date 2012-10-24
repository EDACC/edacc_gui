package edacc.experiment.plots;

import java.awt.geom.Point2D;

/**
 *
 * @author simon
 */
public class PointInformation {
    private Point2D point;
    private String description;

    public PointInformation(Point2D point, String description) {
        this.point = point;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Point2D getPoint() {
        return point;
    }

    @Override
    public String toString() {
        return "[PointInformation point=" + point + ", description="+description + "]";
    }




}
