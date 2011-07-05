package edacc.parameterspace.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class IntegerDomain extends Domain {
	protected Integer low, high;
	
	private IntegerDomain() {
		
	}
	
	public IntegerDomain(Integer low, Integer high) {
		this.low = low;
		this.high = high;
	}
	
	@Override
	public boolean contains(Object value) {
		if (!(value instanceof Number)) return false;
		double d = ((Number)value).doubleValue();
		if (d != Math.round(d)) return false;
		return d >= this.low && d <= this.high;
	}
	
	@Override
	public Object randomValue(Random rng) {
		return rng.nextInt(this.high - this.low + 1) + this.low; 
	}

	@Override
	public String toString() {
		return "[" + this.low + "," + this.high + "]";
	}

	public Integer getLow() {
		return low;
	}

	public void setLow(Integer low) {
		this.low = low;
	}

	public Integer getHigh() {
		return high;
	}

	public void setHigh(Integer high) {
		this.high = high;
	}

	@Override
	public Object mutatedValue(Random rng, Object value) {
		if (!contains(value)) return value;
		double r = rng.nextGaussian() * ((high - low) * 0.2);
		return Math.min(Math.max(this.low, Math.round(((Number)value).doubleValue() + r)), this.high);
	}

	@Override
	public List<Object> getDiscreteValues() {
		List<Object> values = new LinkedList<Object>();
		for (int i = this.low; i <= this.high; i++) {
			values.add(i);
		}
		return values;
	}
}
