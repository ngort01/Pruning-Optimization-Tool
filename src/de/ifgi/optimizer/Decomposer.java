package de.ifgi.optimizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	private static final List<String> rels = Arrays.asList("dc", "ntpp", "length_equal", "length_longer",
			"length_shorter");

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
			//System.out.println("SET: " + set.toString());
			Subgraph subGraph = new Subgraph(g, set);
			subGraphs.add(subGraph);
		});

		removedEdges.forEach(e -> {
			g.addEdge((Geometry) e.getV1(), (Geometry) e.getV2(), e);
		});

		return subGraphs;
	}
	
	public ArrayList<Relation> getRemovedEdges() {
		return this.removedEdges;		
	}

}
