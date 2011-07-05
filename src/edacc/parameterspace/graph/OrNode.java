package edacc.parameterspace.graph;

import edacc.parameterspace.Parameter;
import java.io.Serializable;
import edacc.parameterspace.domain.Domain;

public class OrNode extends Node implements Serializable {
	
	private OrNode() {
		
	}
	
	public OrNode(Parameter parameter, Domain domain) {
		this.parameter = parameter;
		this.domain = domain;
	}

	@Override
	public String toString() {
		return "OrNode [" + parameter + "]";
	}
}
