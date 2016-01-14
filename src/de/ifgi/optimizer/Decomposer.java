package de.ifgi.optimizer;

import java.util.ArrayList;

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
	
	public Decomposer(SimpleWeightedGraph<Geometry, Relation> g) {
		this.g = g;
		this.inspector = new ConnectivityInspector<Geometry, Relation>(g);
	}
	
	
	public ArrayList<Subgraph> decompose() {
		g.edgeSet().iterator().forEachRemaining(e -> {
			if (e.getType().equalsIgnoreCase("dc") || e.getType().equalsIgnoreCase("ntpp")) {
				g.removeEdge(e);
				removedEdges.add(e);
			}
		});
		
		inspector.connectedSets().forEach(set -> {
			System.out.println(set.size() == g.vertexSet().size());
			Subgraph subGraph = new Subgraph(g, set);
			subGraphs.add(subGraph);
		});
		
		removedEdges.forEach(e -> {
			g.addEdge((Geometry)e.getV1(), (Geometry)e.getV2(), e);
		});
		
		return subGraphs;
	}

}
