package edacc.parameterspace.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MixedDomain extends Domain {
	protected List<Domain> domains;
	public static final String name = "Mixed";
	private MixedDomain() {
		
	}
	
	public MixedDomain(List<Domain> domains) {
		this.domains = domains;
	}

	@Override
	public boolean contains(Object value) {
		for (Domain d: domains) {
			if (d.contains(value)) return true;
		}
		return false;
	}

	@Override
	public Object randomValue(Random rng) {
		int i = rng.nextInt(domains.size());
		return domains.get(i).randomValue(rng);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Domain d: domains) {
			sb.append(d.toString());
			sb.append("+");
		}
		return sb.toString();
	}

	public List<Domain> getDomains() {
		return domains;
	}

	public void setDomains(List<Domain> domains) {
		this.domains = domains;
	}

	@Override
	public Object mutatedValue(Random rng, Object value) {
		if (!contains(value)) return value;
		int dom = rng.nextInt(domains.size());
		return domains.get(dom).mutatedValue(rng, value);
	}

	@Override
	public List<Object> getDiscreteValues() {
		List<Object> values = new LinkedList<Object>();
		values.addAll(domains);
		return values;
	}

    @Override
    public String getName() {
        return name;
    }
}
