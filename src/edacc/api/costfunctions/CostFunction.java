package edacc.api.costfunctions;

import java.util.List;

public interface CostFunction {
    /**
     * Calculate the value of the cost function over the list of
     * experiment <code>results</code>.
     * @param results
     * @return
     */
	public float calculateCost(List<edacc.model.ExperimentResult> results);
	
	 /**
     * Calculate the cumulated value of the costs over the list of
     * experiment <code>results</code>.
     * @param results
     * @return cumulated costs
     */
	public float calculateCumulatedCost(List<edacc.model.ExperimentResult> results);
	
	/**
	 * Returns a string that uniquely identifies the cost function type
	 * in the database (cost_function column in the solver configuration table)
	 * @return representation string
	 */
	public String databaseRepresentation();
}
