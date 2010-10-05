package edacc.experiment.plots;

import java.awt.Component;

/**
 *
 * @author simon
 */
public class Dependency {
    private String description;
    private Component guiObject;

    public Dependency(String description, Component guiObject) {
        this.description = description;
        this.guiObject = guiObject;
    }

    public String getDescription() {
        return description;
    }

    public Component getGuiObject() {
        return guiObject;
    }
}
