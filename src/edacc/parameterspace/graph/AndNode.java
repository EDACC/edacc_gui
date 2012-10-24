package edacc.parameterspace.graph;

import edacc.parameterspace.Parameter;
import edacc.parameterspace.domain.Domain;
import java.io.Serializable;

public class AndNode extends Node implements Serializable {
	protected Domain domain;
	
	private AndNode() {
		
	}
	
	public AndNode(Parameter parameter, Domain domain) {
		this.parameter = parameter;
		this.domain = domain;
	}

	@Override
	public String toString() {
		return "AndNode [" + domain + "]";
	}
	
	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}
}
