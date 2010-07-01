package edacc.experiment.plots;

import java.awt.Component;

/**
 *
 * @author simon
 */
public class Dependency {
    private String description;
    private Component guiObject;
    private Object value;

    public Dependency(String description, Component guiObject) {
        this.description = description;
        this.guiObject = guiObject;
        value = null;
    }

    public String getDescription() {
        return description;
    }

    public Component getGuiObject() {
        return guiObject;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
