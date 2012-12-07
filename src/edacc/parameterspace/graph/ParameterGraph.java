package edacc.parameterspace.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

import edacc.parameterspace.Parameter;
import edacc.parameterspace.ParameterConfiguration;
import java.io.Serializable;

@XmlRootElement( name="parameterspace" )
public class ParameterGraph implements Serializable {
	@XmlIDREF public AndNode startNode;
	public Set<Node> nodes;
	public Set<Parameter> parameters;
	public List<Edge> edges;
	
	private Map<Node, List<Edge>> adjacent_edges; // internal adjacency list
	
	private ParameterGraph() {
		
	}

	public ParameterGraph(Set<Node> nodes, List<Edge> edges, Set<Parameter> parameters, AndNode startNode) {
		this.startNode = startNode;
		this.nodes = nodes;
		this.edges = edges;
		this.parameters = parameters;
		buildAdjacencyList();
	}
	
	public Map<String, Parameter> getParameterMap() {
		Map<String, Parameter> map = new HashMap<String, Parameter>();
		for (Parameter p: this.parameters) {
			map.put(p.getName(), p);
		}
		return map;
	}
	
	public Set<Parameter> getParameterSet() {
		return java.util.Collections.unmodifiableSet(this.parameters);
	}
	
	public void buildAdjacencyList() {
		this.adjacent_edges = new HashMap<Node, List<Edge>>();
                if (edges == null) {
                    edges = new LinkedList<Edge>();
                }
		for (Edge e: edges) {
			if (!this.adjacent_edges.containsKey(e.getSource())) this.adjacent_edges.put(e.getSource(), new LinkedList<Edge>());
			this.adjacent_edges.get(e.getSource()).add(e);
		}
	}
	
	private Set<Node> adjacentNodes(Node node) {
		Set<Node> nodes = new HashSet<Node>();
		if (!adjacent_edges.containsKey(node)) return nodes;
		for (Edge e: adjacent_edges.get(node)) {
			nodes.add(e.getTarget());
		}
		return nodes;
	}
	
	private Set<Node> preceedingNodes(OrNode node) {
		Set<Node> nodes = new HashSet<Node>();
		for (Node n: this.nodes) {
			if (!(n instanceof OrNode)) continue;
			for (Edge e: adjacent_edges.get(n)) {
				if (e.getTarget().equals(node)) nodes.add(n);
			}
		}
		return nodes;
	}
	
	private OrNode preceedingNode(AndNode node) {
		for (OrNode n: getOrNodes()) {
			for (Edge e: adjacent_edges.get(n)) {
				if (e.getTarget().equals(node)) return n;
			}
		}
		return null;
	}
	
	private List<Edge> incomingEdges(Node node) {
		List<Edge> edges = new LinkedList<Edge>();
		for (Node n: this.nodes) {
			if (!this.adjacent_edges.containsKey(n)) continue;
			for (Edge e: this.adjacent_edges.get(n)) {
				if (e.getTarget().equals(node)) edges.add(e);
			}
		}
		return edges;
	}
	
	private Set<OrNode> getOrNodes() {
		Set<OrNode> s = new HashSet<OrNode>();
		for (Node n: this.nodes) {
			if (n instanceof OrNode) s.add((OrNode)n);
		}
		return s;
	}
	
	private Set<AndNode> getAndNodes() {
		Set<AndNode> s = new HashSet<AndNode>();
		for (Node n: this.nodes) {
			if (n instanceof AndNode) s.add((AndNode)n);
		}
		return s;
	}
	
	private boolean incomingEdgesDone(Node node, Set<AndNode> doneAndNodes) {
		Map<Integer, List<Edge>> edge_groups = new HashMap<Integer, List<Edge>>();
		for (Edge e: incomingEdges(node)) {
			if (!edge_groups.containsKey(e.getGroup())) edge_groups.put(e.getGroup(), new LinkedList<Edge>());
			edge_groups.get(e.getGroup()).add(e);
		}
		
		if (edge_groups.containsKey(new Integer(0))) {
			for (Edge e: edge_groups.get(0)) {
				if (doneAndNodes.contains(e.getSource())) return true;
			}
		}
		
		boolean any_group_done = false;
		for (Integer group: edge_groups.keySet()) {
			if (group == 0) continue;
			boolean all_group_edges_done = true;
			for (Edge e: edge_groups.get(group)) all_group_edges_done &= doneAndNodes.contains(e.getSource());
			any_group_done |= all_group_edges_done;
		}
		
		return any_group_done;
	}
	
	private <T> T randomElement(Set<T> set, Random rng) {
		int i = rng.nextInt(set.size());
		int j = 0;
		for (T o: set) {
			if (i == j++) return o;
		}
		return null;
	}
	
	private boolean valuesEqual(Object v1, Object v2) {
		if (v1 instanceof Double || v1 instanceof Float) {
			if (!(v2 instanceof Number)) return false;
			double cur_val = (Double)v2;
			double val = (Double)v1;
			if (cur_val - 0.00000001 < val && val < cur_val + 0.00000001) return true;
		}
		
		return v1.equals(v2);
	}
	
	/**
	 * Creates a random parameter configuration.
	 * @param rng Random number generator instance
	 * @return random parameter configuration
	 */
	public ParameterConfiguration getRandomConfiguration(Random rng) {
		ParameterConfiguration config = new ParameterConfiguration(this.parameters);
		
		Set<AndNode> done_and = new HashSet<AndNode>();
		done_and.add(this.startNode);
		Set<OrNode> done_or = new HashSet<OrNode>();
		
		Set<OrNode> L = new HashSet<OrNode>();
		for (Node n: adjacentNodes(this.startNode)) {
			if (n instanceof OrNode) L.add((OrNode)n);
		}
		
		while (true) {
			Set<OrNode> openOrNodes = new HashSet<OrNode>();
			for (OrNode n: L) 
				if (incomingEdgesDone(n, done_and))
					openOrNodes.add(n);
			if (openOrNodes.isEmpty()) break;
			OrNode or_node = randomElement(openOrNodes, rng);
			L.remove(or_node);
			done_or.add(or_node);
			
			Set<AndNode> adjacentAndNodes = new HashSet<AndNode>();
			for (Node n: adjacentNodes(or_node)) {
				if (n instanceof AndNode) adjacentAndNodes.add((AndNode)n);
			}
			AndNode and_node = randomElement(adjacentAndNodes, rng);
			
			if (config.getParameterValue(and_node.getParameter()) == null) {
				config.setParameterValue(and_node.getParameter(), and_node.getDomain().randomValue(rng));
                                done_and.add(and_node);
                                for (Node n: adjacentNodes(and_node)) {
                                    if (n instanceof OrNode) L.add((OrNode)n);
                                }
			} else {
                            for (AndNode an: adjacentAndNodes) {
                                if (an.getDomain().contains(config.getParameterValue(an.getParameter()))) {
                                    done_and.add(an);
                                    for (Node n: adjacentNodes(an)) {
                                        if (n instanceof OrNode) L.add((OrNode)n);
                                    }
                                }
                            }   
                        }
		}
		
		config.updateChecksum();
		return config;
	}
	
	/**
	 * Returns all parameter configurations that are considered neighbours of the given configuration.
	 * TODO: use generalized algorithm instead of the "same-AND-node" constrained neighbourhood. 
	 * @param config The configuration of which the neighbourhood should be generated.
	 * @return list of all neighbouring configurations
	 */
	public List<ParameterConfiguration> getNeighbourhood(ParameterConfiguration config) {
		Set<OrNode> assigned_or_nodes = new HashSet<OrNode>();
		Set<AndNode> assigned_and_nodes = new HashSet<AndNode>();
		for (Parameter p: config.getParameter_instances().keySet()) {
    		for (AndNode n: getAndNodes()) {
    			if (n == startNode) continue;
    			if (n.getParameter().equals(p) && n.getDomain().contains(config.getParameterValue(p))) {
    				assigned_or_nodes.add(preceedingNode(n));
    				assigned_and_nodes.add(n);
    			}
    		}
		}
		
		List<ParameterConfiguration> nbh = new LinkedList<ParameterConfiguration>();
		for (AndNode node: assigned_and_nodes) {
			for (Object value: preceedingNode(node).getParameter().getDomain().getDiscreteValues()) {
				if (node.getDomain().contains(value)) { // same subdomain, different value
					if (valuesEqual(value, config.getParameterValue(node.getParameter()))) continue;
					ParameterConfiguration neighbour = new ParameterConfiguration(config);
					neighbour.setParameterValue(node.getParameter(), value);
					neighbour.updateChecksum();
					nbh.add(neighbour);
				}
			}
			
		}
		
		return nbh;
	}
	
	/**
	 * Generates a random neighbour
	 * TODO: use generalized algorithm instead of the "same-AND-node" constrained neighbourhood. 
	 * @param config The configuration of which a random neighbour should be returned
	 * @param rng Random number generator instance
	 * @return random neighbour of the passed configuration
	 */
	public ParameterConfiguration getRandomNeighbour(ParameterConfiguration config, Random rng) {
		Set<OrNode> assigned_or_nodes = new HashSet<OrNode>();
		Set<AndNode> assigned_and_nodes = new HashSet<AndNode>();
		for (Parameter p: config.getParameter_instances().keySet()) {
    		for (AndNode n: getAndNodes()) {
    			if (n == startNode) continue;
    			if (n.getParameter().equals(p) && n.getDomain().contains(config.getParameterValue(p))) {
    				assigned_or_nodes.add(preceedingNode(n));
    				assigned_and_nodes.add(n);
    			}
    		}
		}
		
		AndNode node = randomElement(assigned_and_nodes, rng);
		List<Object> vals = node.getDomain().getDiscreteValues();
		ParameterConfiguration n = new ParameterConfiguration(config);
		
		int tried = 0;
		int num_vals = vals.size();
		while (tried++ < num_vals) {
			Object val = vals.get(rng.nextInt(vals.size()));
			vals.remove(val);
			if (valuesEqual(val, config.getParameterValue(node.getParameter()))) continue;
			n.setParameterValue(node.getParameter(), val);
			break;
		}
		n.updateChecksum();
		return n;
	}
	
	/**
	 * mutate a configuration in each parameter.
	 * TODO: generalize ...
	 * @param rng
	 * @param config
	 */
	public void mutateParameterConfiguration(Random rng, ParameterConfiguration config) {
		Set<AndNode> assigned_and_nodes = new HashSet<AndNode>();
		for (Node n: this.nodes) {
			if (n == startNode || !(n instanceof AndNode)) continue;
			if (config.getParameter_instances().containsKey(((AndNode)n).parameter) &&
				((AndNode)n).getDomain().contains(config.getParameterValue(((AndNode)n).parameter))) {
				assigned_and_nodes.add((AndNode)n);
			}
		}
		
		for (AndNode n: assigned_and_nodes) {
			config.setParameterValue(n.getParameter(), n.getDomain().mutatedValue(rng, config.getParameterValue(n.getParameter())));
		}
		config.updateChecksum();
	}
	
	public boolean validateParameterConfiguration(ParameterConfiguration config) {
		// TODO: implement
		return false;
	}
}
