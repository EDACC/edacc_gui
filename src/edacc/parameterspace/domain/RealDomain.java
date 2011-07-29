package edacc.parameterspace.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RealDomain extends Domain {
	protected Double low, high;
	public static final String name = "Real";
	private RealDomain() {
		
	}
	
	public RealDomain(Double low, Double high) {
		this.low = low;
		this.high = high;
	}
	
	public RealDomain(Integer low, Integer high) {
		this.low = Double.valueOf(low);
		this.high = Double.valueOf(high);
	}
	
	@Override
	public boolean contains(Object value) {
		if (!(value instanceof Number)) return false;
		double d = ((Number)value).doubleValue();
		return d >= this.low && d <= this.high;
	}
	
	@Override
	public Object randomValue(Random rng) {
		return rng.nextDouble() * (this.high - this.low) + this.low; 
	}

	@Override
	public String toString() {
		return "[" + this.low + "," + this.high + "]";
	}

	public Double getLow() {
		return low;
	}

	public void setLow(Double low) {
		this.low = low;
	}

	public Double getHigh() {
		return high;
	}

	public void setHigh(Double high) {
		this.high = high;
	}
	
	@Override
	public Object mutatedValue(Random rng, Object value) {
		if (!contains(value)) return value;
		double r = rng.nextGaussian() * ((high - low) * 0.2);
		return Math.min(Math.max(this.low, ((Number)value).doubleValue() + r), this.high);
	}
	
	@Override
	public List<Object> getDiscreteValues() {
		List<Object> values = new LinkedList<Object>();
		for (double d = low; d <= high; d += (high - low) / 100.0f) {
			values.add(d);
		}
		return values;
	}

    @Override
    public String getName() {
        return name;
    }
}
