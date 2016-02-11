package de.ifgi.optimizer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.Subgraph;

import de.ifgi.importer.ParsedInput;
import de.ifgi.importer.Relation;
import de.ifgi.objects.Geometry;

public class Optimizer {

	private String outputFile;
	private SimpleWeightedGraph<Geometry, Relation> g;

	public Optimizer(ParsedInput input, String outputFile) {
		this.outputFile = outputFile;
		this.g = input.getG();
	}

	public void optimize() throws IOException {
		Decomposer d = new Decomposer(g);
		// decompose the grpah into smaller subsets if possible
		ArrayList<Subgraph> decomposition = d.decompose();
		// grounded points that were already printed (to prevent doubled output)
		ArrayList<Geometry> printed = new ArrayList<Geometry>();
		//
		List<String> output = new ArrayList<String>();

		for (int i = 1; i < 3; i++) {
			printed.clear();

			g.vertexSet().forEach(g -> {
				g.grounded = false;
			});

			int[] xRange = i < 2 ? new int[] { -2, -1 } : new int[] { -2, -2 };
			System.out.println(xRange[0] + " " + xRange[1]);

			decomposition.forEach(subGraph -> {
				xRange[0] += 2;
				xRange[1] += 2;
				// choose points for grounding depending on vertex score and
				// relation type
				ArrayList<Geometry> chosenObjects = chooseObjects(g, subGraph);

				System.out.println("Grounding: " + chosenObjects.get(0).getClass().getName() + " "
						+ chosenObjects.get(0).getName() + ", " + chosenObjects.get(1).getClass().getName() + " "
						+ chosenObjects.get(1).getName());

				// apply case F
				caseF(chosenObjects, xRange[0], xRange[1]);
			});

			// Output
			output.add("SUBCASE " + i);
			System.out.println("SUBCASE " + i);
			createOutput(g, output, printed);
		}

		Path file = Paths.get(outputFile);
		Files.write(file, output, Charset.forName("UTF-8"));
	}

	/**
	 * Choose points for grounding depending on the node score and relations
	 * 
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
		// System.out.println("VERTEX SET " + vertexSet.length);
		for (int i = 0; i < vertexSet.length; i++) {
			Geometry v = vertexSet[i];

			// only grounding 2 objects for now
			if (chosenObjects.size() > 1)
				break;

			// don't ground lines for now..
			if (!v.getClass().getName().contains("Line") && !v.getClass().getName().contains("Circle")) {
				chosenObjects.add(v);
			}

		}

		return chosenObjects;
	}

	/**
	 * Grounds two objects
	 * 
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

	private void createOutput(SimpleWeightedGraph<Geometry, Relation> g, List<String> output,
			ArrayList<Geometry> printed) {
		g.edgeSet().iterator().forEachRemaining(e -> {
			Geometry g1 = (Geometry) e.getV1();
			Geometry g2 = (Geometry) e.getV2();
			//System.out.println("OUTPUT GROUNDED " + g1.grounded + " " + g2.grounded);
			// if there is a centre relation between two objects
			// and one is grounded, ground the other one
			if (!g1.getClass().getName().contains("Line") && !g2.getClass().getName().contains("Line")) {
				String type = e.getType();
				if (type.contentEquals("centre") || type.contentEquals("equal")) {
					if (g1.isGrounded()) {
						g2.setX(g1.getX());
						g2.setY(g1.getY());
					} else if (g2.isGrounded()) {
						g1.setX(g2.getX());
						g1.setY(g2.getY());
					}
				}
			}

			if (!printed.contains(g1)) {
				g1.print();
				printed.add(g1);
				if (g1.isGrounded()) {
					output.add(g1.getName() + " x " + g1.getX());
					output.add(g1.getName() + " y " + g1.getY());
				}
			}
			if (!printed.contains(g2)) {
				g2.print();
				printed.add(g2);
				if (g2.isGrounded()) {
					output.add(g2.getName() + " x " + g2.getX());
					output.add(g2.getName() + " y " + g2.getY());
				}
			}

		});
	}

}
