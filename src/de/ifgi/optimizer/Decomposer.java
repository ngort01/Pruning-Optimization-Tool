package de.ifgi.optimizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.Subgraph;

import de.ifgi.importer.Relation;
import de.ifgi.objects.Geometry;

public class Decomposer {
	
	private SimpleWeightedGraph<Geometry, Relation> g;
	private ConnectivityInspector<Geometry, Relation> inspector;
	private ArrayList<Relation> removedEdges = new ArrayList<Relation>();
	private ArrayList<Subgraph> subGraphs = new ArrayList<Subgraph>();
	private static final Set<String> rels = new HashSet<String>(Arrays.asList(
		     new String[] {"dc", "ntpp", "length_equal", "length_longer"}
		));
	
	public Decomposer(SimpleWeightedGraph<Geometry, Relation> g) {
		this.g = g;
		this.inspector = new ConnectivityInspector<Geometry, Relation>(g);
	}
	
	
	public ArrayList<Subgraph> decompose() {

		g.edgeSet().iterator().forEachRemaining(e -> {
			if (rels.contains(e.getType())) {
				removedEdges.add(e);
			}
		});
		g.removeAllEdges(removedEdges);
		
		inspector.connectedSets().forEach(set -> {
			//System.out.println(set.size() == g.vertexSet().size());
			Subgraph subGraph = new Subgraph(g, set);
			subGraphs.add(subGraph);
		});
		
		removedEdges.forEach(e -> {
			g.addEdge((Geometry)e.getV1(), (Geometry)e.getV2(), e);
		});
		
		return subGraphs;
	}

}
