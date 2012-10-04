package edacc.api.costfunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edacc.model.Experiment;
import edacc.model.ExperimentResult;

public class Median implements CostFunction {
    private boolean minimize;
    private Experiment.Cost costType;
    
    public Median(Experiment.Cost costType, boolean minimize) {
        this.minimize = minimize;
        this.costType = costType;
    }
    
	@Override
	public double singleCost(edacc.model.ExperimentResult job){
	    if (costType.equals(Experiment.Cost.resultTime)) 
	        return job.getResultTime();
	    else if (costType.equals(Experiment.Cost.wallTime))
	        return job.getWallTime();
	    else
	        return job.getCost();
	}
	
	@Override
	public double calculateCost(List<ExperimentResult> results) {
		if (results.size() == 0)
			return 0;
		List<Double> vals = new ArrayList<Double>();
		for (ExperimentResult res : results) {
				vals.add(singleCost(res));
		}
		Collections.sort(vals);
		return vals.get(vals.size() / 2);
	}

	@Override
	public double calculateCumulatedCost(List<ExperimentResult> results) {
		double sum = 0.0f;
		if (results.size() == 0)
			return 0;
		for (ExperimentResult res : results)
			if (res.getStatus().getStatusCode() > 0) {
				sum += singleCost(res);
			}
		return sum;
	}

	@Override
	public String databaseRepresentation() {
		return "median";
	}

    @Override
    public boolean getMinimize() {
        return minimize;
    }

}
