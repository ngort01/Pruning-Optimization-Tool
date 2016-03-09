package de.ifgi.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.jgrapht.graph.*;

import de.ifgi.objects.Circle;
import de.ifgi.objects.Geometry;
import de.ifgi.objects.Line;
import de.ifgi.objects.Point;

/**
 * Represents the input from the text file Stores objects, relations and a
 * corresponding graph
 * 
 * @author Niko
 *
 */
public class ParsedInput {

	private HashMap<String, Geometry> objects = new HashMap<String, Geometry>();; // all objects
	private HashMap<String, Point> points = new HashMap<String, Point>();;
	private HashMap<String, Line> lines = new HashMap<String, Line>();
	private HashMap<String, Circle> circles = new HashMap<String, Circle>();
	private ArrayList<String> relations = new ArrayList<String>();
	private SimpleWeightedGraph<Geometry, Relation> g = new SimpleWeightedGraph<Geometry, Relation>(
			new ClassBasedEdgeFactory<Geometry, Relation>(Relation.class));;
	// relation weights
	private Properties defaultWeights = new Properties();

	public ParsedInput() {
		try {
			this.defaultWeights.load(ParsedInput.class.getResourceAsStream("relationWeights.properties"));
		} catch (Exception e) {
		}
	}

	public HashMap<String, Geometry> getObjects() {
		return this.objects;
	}

	public SimpleWeightedGraph<Geometry, Relation> getG() {
		return g;
	}

	public void addPoint(String name) {
		Point p = new Point(name);
		objects.put(name, p);
		points.put(name, p);
		g.addVertex(p);
	}

	public void removePoint(String name) {
		g.removeVertex(objects.get(name));
		points.remove(name);
		objects.remove(name);
	}

	public HashMap<String, Point> getPoints() {
		return this.points;
	}

	public void addLine(String name) {
		Line l = new Line(name);
		objects.put(name, l);
		lines.put(name, l);
		g.addVertex(l);
	}

	public void removeLine(String name) {
		g.removeVertex(objects.get(name));
		lines.remove(name);
		objects.remove(name);
	}

	public HashMap<String, Line> getLines() {
		return this.lines;
	}

	public void addCircle(String name) {
		Circle c = new Circle(name);
		objects.put(name, c);
		circles.put(name, c);
		g.addVertex(c);
	}

	public void removeCircle(String name) {
		g.removeVertex(objects.get(name));
		circles.remove(name);
		objects.remove(name);
	}

	public HashMap<String, Circle> getCircles() {
		return this.circles;
	}

	/**
	 * 
	 * @param relation
	 */
	public void addRelation(String[] relation) {
		Geometry v1 = null, v2 = null, l = null;
		Relation<Geometry> edge = null;
		String rel = relation[0];
		String[] vertices = relation[1].split("\\,");
		v1 = objects.get(vertices[0]);
		v2 = objects.get(vertices[1]);
		l = vertices.length == 3 && objects.containsKey(vertices[2]) ? objects.get(vertices[2]) : null;

		if (rel.contentEquals("end_points")) {
			relations.add("start_point");
			edge = new Relation<Geometry>(v1, l, "start_point");
			g.addEdge(v1, l, edge);
			l.setStartPoint((Point) v1);
			g.setEdgeWeight(edge, 1.0);

			relations.add("end_point");
			edge = new Relation<Geometry>(v2, l, "end_point");
			g.addEdge(v2, l, edge);
			l.setEndPoint((Point) v2);
		} else if (rel.contentEquals("start_point")) {
			if (v1.getClass().getName().contains("Line")) {
				v1.setStartPoint((Point) v2);
			} else {
				v2.setStartPoint((Point) v1);
			}
			relations.add(rel);
			edge = new Relation<Geometry>(v1, v2, rel);
			g.addEdge(v1, v2, edge);
		} else if (rel.contentEquals("end_point")) {
			if (v1.getClass().getName().contains("Line")) {
				v1.setEndPoint((Point) v2);
			} else {
				v2.setEndPoint((Point) v1);
			}
			relations.add(rel);
			edge = new Relation<Geometry>(v1, v2, rel);
			g.addEdge(v1, v2, edge);
		} else if (rel.contentEquals("centre") | rel.contentEquals("center")) {
			if (v1.getClass().getName().contains("Circle")) {
				v1.setCentre((Point) v2);
			} else {
				v2.setCentre((Point) v1);
			}
			relations.add(rel);
			edge = new Relation<Geometry>(v1, v2, rel);
			g.addEdge(v1, v2, edge);
		} else if (rel.contentEquals("coincident")) {
			String v1Class = v1.getClass().getName();
			String v2Class = v2.getClass().getName();

			// Point - Point coincidence
			boolean PP = v1Class.contains("Point") && v2Class.contains("Point");
			// Point - Line coincidence
			boolean PL = (v1Class.contains("Point") && v2Class.contains("Line"))
					|| (v1Class.contains("Line") && v2Class.contains("Point"));
			// Point - Circle coincidence
			boolean PC = (v1Class.contains("Point") && v2Class.contains("Circle"))
					|| (v1Class.contains("Circle") && v2Class.contains("Point"));

			if (PP) {
				rel += "PP";
			} else if (PL) {
				rel += "PL";
			} else {
				rel += "PC";
			}
			
			relations.add(rel);
			edge = new Relation<Geometry>(v1, v2, rel);
			g.addEdge(v1, v2, edge);

		} else {
			relations.add(rel);
			edge = new Relation<Geometry>(v1, v2, rel);
			g.addEdge(v1, v2, edge);
		}
		
		// set edge weights
		if (defaultWeights.containsKey(rel)) {
			g.setEdgeWeight(edge, Double.parseDouble(defaultWeights.get(rel).toString()));
		} else {
			g.setEdgeWeight(edge, 1.0);
		}

	}

	public void removeRelation(String[] relation) {
		String rel = relation[0];
		String[] vertices = relation[1].split("\\,");
		Geometry v1 = objects.get(vertices[0]);
		Geometry v2 = objects.get(vertices[1]);
		relations.remove(rel);
		g.removeAllEdges(v1, v2);
	}

	public boolean hasPoints() {
		return !points.isEmpty();
	}

	public boolean hasLines() {
		return !lines.isEmpty();
	}

	public boolean hasCircles() {
		return !circles.isEmpty();
	}

	public void calcScores() {
		g.edgeSet().forEach(e -> {
			Geometry v1 = (Geometry) e.getV1();
			Geometry v2 = (Geometry) e.getV2();
			v1.score += g.getEdgeWeight(e);
			v2.score += g.getEdgeWeight(e);
		});
	}

	public void print() {
		System.out.println("Points " + points.toString());
		System.out.println("Lines " + lines.toString());
		System.out.println("Circles " + circles.toString());
		System.out.println("Relations " + relations.toString());
		System.out.println("Graph " + g);
		g.vertexSet().forEach(v -> {
			System.out.println(v.getName() + " scrore: " + v.score);
		});
	}

}
