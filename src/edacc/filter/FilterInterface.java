package edacc.filter;

/**
 * Every filter type must implement this interface.
 * @author simon
 */
public interface FilterInterface {
    /**
     * Returns true iff the value matches the parameters of the filter.
     * @param value
     * @return
     */
    public boolean include(Object value);
    /**
     * Applies the values from the GUI to the filters logic.
     */
    public void apply();
    /**
     * Replaces the values from the GUI by the values of the logic.
     */
    public void undo();
}
