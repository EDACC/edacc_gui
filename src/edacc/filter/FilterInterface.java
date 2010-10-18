package edacc.filter;

/**
 *
 * @author simon
 */
public interface FilterInterface {
    public boolean include(Object value);
    public void apply();
    public void undo();
}
