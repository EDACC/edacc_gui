package edacc.parameterspace.graph;

import edacc.parameterspace.Parameter;
import java.io.Serializable;

public class OrNode extends Node implements Serializable {
	private OrNode() {
		
	}
	
	public OrNode(Parameter parameter) {
		this.parameter = parameter;
	}

	@Override
	public String toString() {
		return "OrNode [" + parameter + "]";
	}
}
