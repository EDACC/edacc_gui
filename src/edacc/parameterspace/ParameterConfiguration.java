package edacc.parameterspace;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParameterConfiguration {
	private Map<Parameter, Object> parameter_instances;
	byte[] checksum;
	
	public ParameterConfiguration(Set<Parameter> parameters) {
		this.checksum = null;
		this.parameter_instances = new HashMap<Parameter, Object>();
		for (Parameter p: parameters) parameter_instances.put(p, null);
	}
	
	public byte[] getChecksum() {
		return checksum;
	}
	
	public void updateChecksum() {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			for (Parameter p: parameter_instances.keySet()) {
				if (parameter_instances.get(p) != null) {
					md.update(parameter_instances.get(p).toString().getBytes());
				}
			}
			this.checksum = md.digest();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Object getParameterValue(Parameter p) {
		if (!parameter_instances.containsKey(p))
			throw new IllegalArgumentException("The parameter has to be part of a solver configuration");
		return parameter_instances.get(p);
	}
	
	public void setParameterValue(Parameter p, Object v) {
		if (!parameter_instances.containsKey(p))
			throw new IllegalArgumentException("The parameter has to be part of a solver configuration");
		if (!p.getDomain().contains(v)) {
			throw new IllegalArgumentException("Parameter domain does not contain the given value");
		}
		parameter_instances.put(p, v);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((parameter_instances == null) ? 0 : parameter_instances
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParameterConfiguration other = (ParameterConfiguration) obj;
		if (parameter_instances == null) {
			if (other.parameter_instances != null)
				return false;
		} else if (!parameter_instances.equals(other.parameter_instances)) // set comparison
			return false;
		return true;
	}

	public Map<Parameter, Object> getParameter_instances() {
		return parameter_instances;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		List<Parameter> params = new ArrayList<Parameter>();
		params.addAll(parameter_instances.keySet());
		for (Parameter p: params) {
			sb.append(p.getName());
			sb.append(": ");
			sb.append(parameter_instances.get(p));
			sb.append(" ");
		}
		return sb.toString();
	}
	
	public ParameterConfiguration(ParameterConfiguration other) {
		// TODO: ensure that other.getParameterValue(p) makes a copy in all cases
		this.parameter_instances = new HashMap<Parameter, Object>();
		this.checksum = other.checksum.clone();
		for (Parameter p: other.parameter_instances.keySet()) {
			parameter_instances.put(p, other.getParameterValue(p));
		}
	}
}
