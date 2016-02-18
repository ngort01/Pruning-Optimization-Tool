package de.ifgi.optimizer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.Subgraph;

import de.ifgi.importer.ParsedInput;
import de.ifgi.importer.Relation;
import de.ifgi.objects.Geometry;
import de.ifgi.objects.Point;

public class Optimizer {

	private String outputFile;
	private SimpleWeightedGraph<Geometry, Relation> g;

	public Optimizer(ParsedInput input, String outputFile) {
		this.outputFile = outputFile;
		this.g = input.getG();
	}

	public void optimize() throws IOException {
		g.vertexSet().forEach(v -> {
			System.out.println(v.getName() + " score: " + v.score);
		});

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
			// System.out.println(xRange[0] + " " + xRange[1]);

			decomposition.forEach(subGraph -> {
				xRange[0] += 2;
				xRange[1] += 2;
				// choose points for grounding depending on vertex score and
				// relation type
				ArrayList<Geometry> chosenObjects = chooseObjects(g, subGraph);

				chosenObjects.forEach(o -> {
					System.out.println("Grounding: " + o.getClass().getName() + " " + o.getName());
				});

				// ground objects
				ground(chosenObjects, xRange[0], xRange[1]);
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
		// sort vertices by their score
		Arrays.sort(vertexSet);

		for (int i = 0; i < vertexSet.length; i++) {
			Geometry geom = vertexSet[i];

			boolean geomIsLine = geom.getClass().getName().contains("Line");
			boolean hasObjects = chosenObjects.size() > 0;
			boolean firstObjIsLine = hasObjects && chosenObjects.get(0).getClass().getName().contains("Line");

			// only grounding 1 line or 2 points/circles
			if (chosenObjects.size() > 1 || (hasObjects && firstObjIsLine))
				break;

			if (!hasObjects) {
				chosenObjects.add(geom);
			} else if (!firstObjIsLine && !geomIsLine) {
				chosenObjects.add(geom);
			}

		}

		return chosenObjects;
	}

	/**
	 * 
	 * @param chosenObjects
	 * @param minX
	 * @param maxX
	 */
	private void ground(ArrayList<Geometry> chosenObjects, int minX, int maxX) {
		int[] xRange = { minX, maxX };
		Iterator iterator = chosenObjects.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Geometry g = (Geometry) iterator.next();
			if (g.getClass().getName().contains("Point")) {
				groundPoint(g, xRange[i]);
			} else if (g.getClass().getName().contains("Circle")) {
				groundCircle(g, xRange[i]);
			} else {
				groundLine(g, xRange);
			}
			i += 1;
		}
	}

	private void groundPoint(Geometry p, int x) {
		p.setX(x);
		p.setY(0);
	}

	private void groundCircle(Geometry c, int x) {
		c.getCentre().setX(x);
		c.getCentre().setY(0);
		c.ground();
	}

	private void groundLine(Geometry l, int[] xRange) {
		Geometry s = l.getStart();
		Geometry e = l.getEnd();
		s.setX(xRange[0]);
		s.setY(0);
		e.setX(xRange[1]);
		e.setY(0);
		l.ground();
	}

	/**
	 * 
	 * @param g
	 * @param output
	 * @param printed
	 */
	private void createOutput(SimpleWeightedGraph<Geometry, Relation> g, List<String> output,
			ArrayList<Geometry> printed) {

		g.edgeSet().iterator().forEachRemaining(e -> {
			Geometry g1 = (Geometry) e.getV1();
			Geometry g2 = (Geometry) e.getV2();

			String g1Class = g1.getClass().getName();
			String g2Class = g2.getClass().getName();
			
			// (partially) ground additional objects if it`s possible by their relation 
			if (g1Class.contains("Point") && g2Class.contains("Point")) {
				handlePP(g1, g2, e);
			} else if (g1Class.contains("Point") && g2Class.contains("Circle")) {
				handlePC(g1, g2, e);
			} else if (g1Class.contains("Point") && g2Class.contains("Line")) {
				handlePL(g1, g2, e);
			} else if (g1Class.contains("Circle") && g2Class.contains("Circle")) {

			} else if (g1Class.contains("Circle") && g2Class.contains("Point")) {
				handlePC(g1, g2, e);
			} else if (g1Class.contains("Circle") && g2Class.contains("Line")) {

			} else if (g1Class.contains("Line") && g2Class.contains("Line")) {

			} else if (g1Class.contains("Line") && g2Class.contains("Point")) {
				handlePL(g2, g1, e);
			} else if (g1Class.contains("Line") && g2Class.contains("Circle")) {

			}

			if (!printed.contains(g1) && !g1Class.contains("Line")) {
				g1.print();
				if (g1.isGrounded()) {
					int x = g1.getX();
					int y = g1.getY();
					if (!(x == -1))
						output.add(g1.getName() + " x " + x);
					if (!(y == -1))
						output.add(g1.getName() + " y " + y);
					printed.add(g1);
				}
			}
			if (!printed.contains(g2) && !g2Class.contains("Line")) {
				g2.print();
				if (g2.isGrounded()) {
					int x = g2.getX();
					int y = g2.getY();
					if (!(x == -1))
						output.add(g2.getName() + " x " + g2.getX());
					if (!(y == -1))
						output.add(g2.getName() + " y " + g2.getY());
					printed.add(g2);
				}
			}

		});
	}

	/**
	 * Handle Point - Point rellations
	 * 
	 * @param a
	 * @param b
	 * @param e
	 */
	private void handlePP(Geometry a, Geometry b, Relation e) {
		String type = e.getType();
		if (type.contentEquals("coincident") || type.contentEquals("equal")) {
			if (a.isGrounded()) {
				b.setX(a.getX());
				b.setY(a.getY());
			} else if (b.isGrounded()) {
				a.setX(b.getX());
				a.setY(b.getY());
			}
		}
	}

	/**
	 * Handle Point - Circle rellations
	 * 
	 * @param a
	 * @param b
	 * @param e
	 */
	private void handlePC(Geometry a, Geometry b, Relation e) {
		String type = e.getType();
		if (type.contentEquals("centre") || type.contentEquals("center")) {
			if (a.isGrounded()) {
				b.setX(a.getX());
				b.setY(a.getY());
			} else if (b.isGrounded()) {
				a.setX(b.getX());
				a.setY(b.getY());
			}
		}
	}

	/**
	 * Handle Point - Line rellations
	 * 
	 * @param a
	 * @param b
	 * @param e
	 */
	private void handlePL(Geometry a, Geometry b, Relation e) {
		String type = e.getType();
		if (type.contentEquals("coincident") || type.contentEquals("collinear")) {
			if (b.isGrounded()) {
				a.setY(b.getY());
			}
		}
	}

}
