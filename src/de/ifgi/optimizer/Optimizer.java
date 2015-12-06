package de.ifgi.optimizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.Subgraph;

import de.ifgi.importer.ParsedInput;
import de.ifgi.importer.Relation;
import de.ifgi.objects.Geometry;

public class Optimizer {
	// object types preserved under various affine transformations
	private HashMap<String, String[]> objectTypes;
	// Qualitative spatial relations preserved under various affine
	// transformations
	private HashMap<String, String[]> spatialRelations;

	public Optimizer() {
		objectTypes = new HashMap<String, String[]>();
		spatialRelations = new HashMap<String, String[]>();
		// point, line, ellipse, convex polygon
		this.objectTypes.put("1", new String[] { "translate", "rotate", "scale", "reflect" });
		// circle, square, rectangle
		this.objectTypes.put("2", new String[] { "translate", "rotate", "uniform scale", "reflect" });

		// topology, mereology, coincidence, collinearity, line parallelism,
		// packing size
		this.spatialRelations.put("1", new String[] { "translate", "rotate", "scale", "reflect" });
		// distance, length, line perpendicularity
		this.spatialRelations.put("2", new String[] { "translate", "rotate", "uniform scale", "reflect" });
		// relative orientation
		this.spatialRelations.put("3", new String[] { "translate", "rotate", "scale" });
	}

	public void optimize(ParsedInput input) {
		// available transformations regarding objects
		Set<String> objectTransforms = new HashSet<String>();
		// available transformations regarding relations
		Set<String> relationTransforms = new HashSet<String>();
		SimpleWeightedGraph<Geometry, Relation> g = input.getG();
		ConnectivityInspector<Geometry, Relation> inspector = new ConnectivityInspector<Geometry, Relation>(g);
		// grounded points that were already printed (to prevent doubled output)
		ArrayList<Geometry> printed = new ArrayList<Geometry>();

		// Find tradable transformations
		if (input.hasCircles()) {
			objectTransforms.addAll(Arrays.asList(objectTypes.get("2")));
		} else {
			objectTransforms.addAll(Arrays.asList(objectTypes.get("1")));
		}

		if (input.hasTypeThreeRelations()) {
			relationTransforms.addAll(Arrays.asList(spatialRelations.get("3")));
		} else if (input.hasTypeTwoRelations()) {
			relationTransforms.addAll(Arrays.asList(spatialRelations.get("2")));
		} else {
			relationTransforms.addAll(Arrays.asList(spatialRelations.get("1")));
		}
		
		// intersect, result is in objectTransforms
		// all transformations available for trading
		objectTransforms.retainAll(relationTransforms);
		int[] xRange = {-2, -1};
		inspector.connectedSets().forEach(set -> {
			xRange[0] += 2;
			xRange[1] += 2;
			Subgraph subGraph = new Subgraph(g, set);
			// choose points for grounding depending on vertex score and relation type
			ArrayList<Geometry> chosenObjects = chooseObjects(g, subGraph);
			// apply case F
			caseF(chosenObjects, xRange[0], xRange[1]);
		});

		
		// Output
		System.out.println("## OUTPUT");
		g.edgeSet().iterator().forEachRemaining(e -> {
			Geometry g1 = (Geometry) e.getV1();
			Geometry g2 = (Geometry) e.getV2();
			// if there is a centre relation between two objects 
			// and one is grounded, ground the other one 
			if (e.getType().contains("centre")) {
				if (g1.isGrounded()) {
					g2.setX(g1.getX());
					g2.setY(g1.getY());
				} else if (g2.isGrounded()) {
					g1.setX(g2.getX());
					g1.setY(g2.getY());
				}
			}
			
			if(!printed.contains(g1)) {
				g1.print();
				printed.add(g1);
			}
			if(!printed.contains(g2)) {
				g2.print();
				printed.add(g2);
			}
		});
	}
	
	/**
	 * Choose points for grounding depending on the node score and relations
	 * @param vertices
	 * @param chosenObjects
	 * @param g
	 */
	private ArrayList<Geometry> chooseObjects(SimpleWeightedGraph<Geometry, Relation> g, Subgraph subGraph) {
		ArrayList<Geometry> chosenObjects = new ArrayList<Geometry>();
		// vertices array sorted by node score
		Geometry[] vertexSet = new Geometry[subGraph.vertexSet().size()];
		subGraph.vertexSet().toArray(vertexSet);
		Arrays.sort(vertexSet);

		
		for (int i = 0; i < vertexSet.length; i++) {
			Geometry v = vertexSet[i];
			if (chosenObjects.size() > 1) break;
			
			if (i == 0) {
				// add vertex with highest score
				chosenObjects.add(v);
			} else {
				int size = chosenObjects.size();
				for (int j = 0; j < size; j++) {
					if (!g.containsEdge(chosenObjects.get(j), v)) {
						chosenObjects.add(v);
						 break;
					} else {
						String type = g.getEdge(chosenObjects.get(j), v).getType();
						if(!type.equalsIgnoreCase("centre") && !type.equalsIgnoreCase("equal"))  chosenObjects.add(v);
					}
				}
				
			}
			
		}
		
		return chosenObjects;
	}
	
	/**
	 * Grounds two objects
	 * @param chosenObjects
	 */
	private void caseF(ArrayList<Geometry> chosenObjects, int minX, int maxX) {
		Geometry g1 = chosenObjects.get(0);
		Geometry g2 = chosenObjects.get(1);
		g1.setX(minX);
		g1.setY(0);
		g2.setX(maxX);
		g2.setY(0);
	}

}
