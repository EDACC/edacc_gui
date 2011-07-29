package edacc.parameterspace.domain;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class FlagDomain extends Domain {
	protected Set<FLAGS> values;

    public static final String name = "Flag";
    @Override
    public String getName() {
        return name;
    }
	
	public static enum FLAGS {
		ON {
			public String toString() {
				return "ON";
			}
		},
		OFF {
			public String toString() {
				return "OFF";
			}
		}
	}
	
	private FlagDomain() {
		
	}
	
	public Set<FLAGS> getValues() {
		return values;
	}

	public void setValues(Set<FLAGS> values) {
		this.values = values;
	}

	public FlagDomain(boolean contain_on, boolean contain_off) {
		values = new HashSet<FLAGS>();
		if (contain_on) values.add(FLAGS.ON);
		if (contain_off) values.add(FLAGS.OFF);
	}
	
	@Override
	public boolean contains(Object value) {
		if (!(value instanceof FLAGS)) return false;
		return values.contains(value);
	}

	@Override
	public Object randomValue(Random rng) {
		int n = rng.nextInt(values.size());
		return values.toArray()[n];
	}

	@Override
	public String toString() {
		if (values.contains(FLAGS.ON) && values.contains(FLAGS.OFF)) {
			return "{ON, OFF}";
		}
		else if (values.contains(FLAGS.ON)) {
			return "{ON}";
		}
		else {
			return "{OFF}";
		}
	}

	@Override
	public Object mutatedValue(Random rng, Object value) {
		if (!contains(value)) return value;
		return randomValue(rng);
	}

	@Override
	public List<Object> getDiscreteValues() {
		List<Object> values = new LinkedList<Object>();
		values.addAll(this.values);
		return values;
	}

}
