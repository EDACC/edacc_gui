package edacc.parameterspace.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class OrdinalDomain extends Domain {
	protected List<String> ordered_list;
	public static final String name = "Ordinal";
	private OrdinalDomain() {
		
	}
	
	public OrdinalDomain(List<String> ordered_list) {
		this.ordered_list = ordered_list;
	}

	@Override
	public boolean contains(Object value) {
		return ordered_list.contains(value);
	}

	@Override
	public Object randomValue(Random rng) {
		int i = rng.nextInt(ordered_list.size());
		return ordered_list.get(i);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (String s: ordered_list) {
			sb.append(s);
			sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	}

	public List<String> getOrdered_list() {
		return ordered_list;
	}

	public void setOrdered_list(List<String> ordered_list) {
		this.ordered_list = ordered_list;
	}

	@Override
	public Object mutatedValue(Random rng, Object value) {
		if (!contains(value)) return value;
		int ix = ordered_list.indexOf(value);
		double r = rng.nextGaussian() * (ordered_list.size() * 0.2);
		int n = Math.min((int)Math.max(Math.round(ix + r), ordered_list.size()), 0);
		return ordered_list.get(n);
	}
	
	@Override
	public List<Object> getDiscreteValues() {
		List<Object> values = new LinkedList<Object>();
		values.addAll(ordered_list);
		return values;
	}

    @Override
    public String getName() {
        return name;
    }
}
