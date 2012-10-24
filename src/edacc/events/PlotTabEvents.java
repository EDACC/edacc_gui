package edacc.events;

/**
 *
 * @author simon
 */
public interface PlotTabEvents {
    /**
     * Called after a tab view is closed or a new one is created.
     * @param count the new count
     */
    public void tabViewCountChanged(int count);

    /**
     * Called after the visiblity of all tab views has been changed.
     * @param visible true, if all tab views are visible, false otherwise
     */
    public void tabViewVisibilityChanged(boolean visible);
}
