package de.ifgi.optimizer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.Subgraph;

import de.ifgi.importer.ParsedInput;
import de.ifgi.importer.Relation;
import de.ifgi.objects.Geometry;
import de.ifgi.objects.Line;

public class Optimizer {

	private String outputFile;
	private ParsedInput input;
	private SimpleWeightedGraph<Geometry, Relation> g;
	// grounded points that were already printed (to prevent doubled output)
	private ArrayList<Geometry> printed = new ArrayList<Geometry>();
	List<String> output = new ArrayList<String>();
	
	private final static String osNewLine = System.lineSeparator();

	public Optimizer(ParsedInput input, String outputFile) {
		this.outputFile = outputFile;
		this.input = input;
		this.g = input.getG();
	}

	public void optimize() throws IOException {
		System.out.println("**********************************************");
		System.out.println("******************* Scores *******************");
		System.out.println("**********************************************");
		g.vertexSet().forEach(v -> {
			System.out.println(v.getName() + " score: " + v.score);
		});

		Decomposer d = new Decomposer(g);
		
		// decompose the grpah into smaller subsets if possible
		ArrayList<Subgraph> decomposition = d.decompose();
		// edges removed to create the decompositions
		ArrayList<Relation> removedEdges = d.getRemovedEdges();

		int subIndex = 1;
		for (Subgraph subGraph : decomposition) {
			// choose points for grounding depending on vertex score and relation type
			ArrayList<Geometry> chosenObjects = chooseObjects(subGraph);
			
			// add current subgraph to output
			output.add("Subgraph " + subIndex + osNewLine);
			
			// add subgraph objects to output
			String objects = "OBJECTS ";
			for (Object o : subGraph.vertexSet()) {
				Geometry g = (Geometry) o;
				objects += g.getName() + " ";
			}
			output.add(objects + osNewLine);
			
			for (Relation e : removedEdges) {
				Geometry v1 = (Geometry)e.getV1();
				Geometry v2 = (Geometry)e.getV2();
				if (subGraph.vertexSet().contains(v1) || subGraph.vertexSet().contains(v2)) {
					output.add("DELETED " + e.getType() + " " + v1.getName() + " " + v2.getName() + osNewLine);
				}
			}
			
			System.out.println("**********************************************");
			System.out.println("****************** GROUNDING *****************");
			System.out.println("**********************************************");
			chosenObjects.forEach(o -> {
				System.out.println("Grounding: " + o.getClass().getName() + " " + o.getName());
			});

			// ground objects
			subcaseLoop: for (int i = 0; i < 2; i++) {
				int[] xRange = i < 1 ? new int[] { 0, 1 } : new int[] { 0, 0 };

				System.out.println("**********************************************");
				System.out.println("*********** Subgraph " + subIndex + "  " + "SUBCASE " + (i + 1) + " ************");
				System.out.println("**********************************************");
				
				// add current subcase for the subgraph
				output.add("SUBCASE " + (i + 1) + osNewLine);
				// output for current subgraph
				output.add(ground(chosenObjects, xRange));

				// subcase where a = b ignored if grounded objects are part of
				// the same line
				if (chosenObjects.get(0).getClass().getName().contains("Line")) {
					break;
				} else if (chosenObjects.get(0).getClass().getName().contains("Point")
						&& chosenObjects.get(1).getClass().getName().contains("Point")) {
					Geometry g1 = chosenObjects.get(0);
					Geometry g2 = chosenObjects.get(1);
					if (isLine(g1, g2, input.getLines()))
						break subcaseLoop;
				} else if (chosenObjects.get(0).getClass().getName().contains("Point")
						&& chosenObjects.get(1).getClass().getName().contains("Circle")) {
					Geometry g1 = chosenObjects.get(0);
					Geometry g2 = chosenObjects.get(1).getCentre();
					if (isLine(g1, g2, input.getLines()))
						break subcaseLoop;
				} else if (chosenObjects.get(0).getClass().getName().contains("Circle")
						&& chosenObjects.get(1).getClass().getName().contains("Point")) {
					Geometry g1 = chosenObjects.get(0).getCentre();
					Geometry g2 = chosenObjects.get(1);
					if (isLine(g1, g2, input.getLines()))
						break subcaseLoop;
				} else if (chosenObjects.get(0).getClass().getName().contains("Circle")
						&& chosenObjects.get(1).getClass().getName().contains("Circle")) {
					Geometry g1 = chosenObjects.get(0).getCentre();
					Geometry g2 = chosenObjects.get(1).getCentre();
					if (isLine(g1, g2, input.getLines()))
						break subcaseLoop;
				}

				printed.clear();
				g.vertexSet().forEach(g -> {
					g.grounded = false;
				});

			}
			subIndex++;
		}

		printed.clear();
		createOutput();
	}

	/**
	 * check if two points create a line
	 * 
	 * @param g1
	 * @param g2
	 * @param lines
	 * @return
	 */
	private boolean isLine(Geometry g1, Geometry g2, HashMap<String, Line> lines) {
		boolean isLine = false;

		for (String key : lines.keySet()) {
			Geometry l = lines.get(key);
			if ((l.getStart().getName() == g1.getName() || l.getStart().getName() == g2.getName())
					&& (l.getEnd().getName() == g1.getName() || l.getEnd().getName() == g2.getName())) {
				isLine = true;
			}

		}
		return isLine;
	}

	/**
	 * Choose objects for grounding depending on the node score and relations
	 * 
	 * @param subGraph
	 *            current subgraph
	 * @return
	 */
	private ArrayList<Geometry> chooseObjects(Subgraph subGraph) {
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
	 * Grounds chosen objects of the current subgraph and related objects if
	 * possible
	 * 
	 * @param chosenObjects
	 * @param xRange
	 * @return goundings as a string
	 */
	private String ground(ArrayList<Geometry> chosenObjects, int[] xRange) {
		String osNewLine = System.getProperty("line.separator");
		// string holding all the groundings for the current subgraph
		String subOutput = "";

		Iterator<Geometry> iterator = chosenObjects.iterator();
		int i = 0;

		// ground chosen objects
		while (iterator.hasNext()) {
			Geometry g = iterator.next();
			if (g.getClass().getName().contains("Point")) {
				groundPoint(g, xRange[i]);
			} else if (g.getClass().getName().contains("Circle")) {
				groundCircle(g, xRange[i]);
			} else {
				groundLine(g, xRange);
			}
			i += 1;
		}

		// (partially) ground additional objects if it`s possible by their
		// relation
		for (Relation e : g.edgeSet()) {
			Geometry g1 = (Geometry) e.getV1();
			Geometry g2 = (Geometry) e.getV2();

			String g1Class = g1.getClass().getName();
			String g2Class = g2.getClass().getName();

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
						subOutput += g1.getName() + " x " + x + osNewLine;
					if (!(y == -1))
						subOutput += g1.getName() + " y " + y + osNewLine;
					printed.add(g1);
				}
			}
			if (!printed.contains(g2) && !g2Class.contains("Line")) {
				g2.print();
				if (g2.isGrounded()) {
					int x = g2.getX();
					int y = g2.getY();
					if (!(x == -1))
						subOutput += g2.getName() + " x " + x + osNewLine;
					if (!(y == -1))
						subOutput += g2.getName() + " y " + y + osNewLine;
					printed.add(g2);
				}
			}
		}
		;
		return subOutput;
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
	 * Creates the final output file
	 */
	private void createOutput() {
		Path file = Paths.get(outputFile);
		try {
			Files.write(file, output, Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		if (type.contentEquals("coincidentPP") || type.contentEquals("equal")) {
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
		if (type.contentEquals("coincidentPL") || type.contentEquals("collinear")) {
			if (b.isGrounded()) {
				a.setY(b.getY());
			}
		}
	}

}
