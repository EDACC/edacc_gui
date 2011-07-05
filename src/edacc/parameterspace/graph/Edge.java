package edacc.parameterspace.graph;

import javax.xml.bind.annotation.XmlIDREF;

public class Edge {
	protected Node source;
	protected Node target;
	protected int group;
	
	private Edge() {
		
	}
	
	public Edge(Node source, Node target, int group) {
		this.source = source;
		this.target = target;
		this.group = group;
	}

	@XmlIDREF public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	@XmlIDREF public Node getTarget() {
		return target;
	}

	public void setTarget(Node target) {
		this.target = target;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}
}
