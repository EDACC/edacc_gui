package edacc.api.costfunctions;

import java.util.List;

import edacc.model.Experiment;
import edacc.model.ExperimentResult;

public class PARX implements CostFunction {
	private int penaltyFactor;
    private boolean minimize;
    private Experiment.Cost costType;
    private float costPenaltyValue;
    
    /**
     * Default constructor initialising the PARX cost function to PAR10.
     */
    public PARX(Experiment.Cost costType, boolean minimize, float costPenaltyValue) {
        this.minimize = minimize;
        this.costType = costType;
        this.costPenaltyValue = costPenaltyValue;
        penaltyFactor = 10;
        
    }

	/**
	 * Initialises the cost function to PARX with X = <code>penaltyFactor</code>
	 * 
	 * @param penaltyFactor
	 */
	public PARX(Experiment.Cost costType, boolean minimize, float costPenaltyValue, int penaltyFactor) {
        this.minimize = minimize;
        this.costType = costType;
        this.costPenaltyValue = costPenaltyValue;
		if (penaltyFactor <= 0)
			throw new IllegalArgumentException("penalty factor should be greater than 0");
		this.penaltyFactor = penaltyFactor;
	}
	
	@Override
	public float singleCost(edacc.model.ExperimentResult job){
		if (String.valueOf(job.getResultCode().getResultCode()).startsWith("1")) {
		    if (costType.equals(Experiment.Cost.resultTime))
		        return job.getResultTime();
		    else if (costType.equals(Experiment.Cost.wallTime)) 
		        return job.getWallTime();
		    else
		        return job.getCost();
		} else {
		    if (costType.equals(Experiment.Cost.resultTime))
		        return job.getCPUTimeLimit() * (float) penaltyFactor;
		    else if (costType.equals(Experiment.Cost.wallTime)) 
		        return job.getWallClockTimeLimit() * (float) penaltyFactor;
		    else
		        return costPenaltyValue * penaltyFactor;
		}
	}
	
	/**
	 * Calculates the cost according to a PARX function. Only finished jobs are taken into consideration. 
	 * Running, not started or crashed jobs are not used for the computation!
	 */
	@Override
	public float calculateCost(List<ExperimentResult> results) {
		float sum = 0.0f;
		int count = 0;
		for (ExperimentResult res : results) {
		    if (res.getStatus().getStatusCode() > 0) {
		        sum += singleCost(res);
		        count ++;
		    }
		}
		if (count == 0) return 0.0f;
		return sum / count;
	}

	/**
	 * Calcultate the total runtime of all finished jobs i.e resultCode > 0!
	 * 
	 */
	@Override
	public float calculateCumulatedCost(List<ExperimentResult> results) {
		// TODO: Take into account if the cost or runtime is wanted!
		float sum = 0.0f;
		for (ExperimentResult res : results) {
			if (res.getStatus().getStatusCode() > 0) {
			    sum += singleCost(res);
			}
		}
		return sum;
	}

	@Override
	public String databaseRepresentation() {
		return "par" + String.valueOf(penaltyFactor);
	}

    @Override
    public boolean getMinimize() {
        return minimize;
    }
}
