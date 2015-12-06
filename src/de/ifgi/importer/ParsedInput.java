package de.ifgi.importer;
import java.util.ArrayList;
import java.util.HashMap;
import org.jgrapht.graph.*;

import de.ifgi.objects.Circle;
import de.ifgi.objects.Geometry;
import de.ifgi.objects.Line;
import de.ifgi.objects.Point;

/**
 * Represents the input from the text file
 * Stores objects, relations and a corresponding graph
 * 
 * @author Niko
 *
 */
public class ParsedInput {
	
	private HashMap<String, Geometry> objects; // all objects
	private HashMap<String, Point> points;
	private HashMap<String, Line> lines;
	private HashMap<String, Circle> circles;
	private ArrayList<String> relations;
	private ArrayList<String> tests;
	private SimpleWeightedGraph<Geometry, Relation> g; // graph representation


	public ParsedInput() {
		this.objects = new HashMap<String, Geometry>();
		this.points = new HashMap<String, Point>();
		this.lines = new HashMap<String, Line>();
		this.circles = new HashMap<String, Circle>();
		this.relations = new ArrayList<String>();
		this.g = new SimpleWeightedGraph<Geometry, Relation>(new ClassBasedEdgeFactory<Geometry, Relation>(Relation.class));

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
		points.remove(name);
		objects.remove(name);
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

	public void addRelation(String[] relation) {
		String rel = relation[0];
		String[] vertices = relation[1].split("\\,");
		Geometry v1 = objects.get(vertices[0]);
		Geometry v2 = objects.get(vertices[1]);
		relations.add(rel);
		Relation<Geometry> edge = new Relation<Geometry>(v1, v2, rel);
		g.addEdge(v1, v2, edge);
		if (rel.contentEquals("centre") | rel.contentEquals("equal")) {
			g.setEdgeWeight(edge, 2.0);
		} else if (rel.contentEquals("ec")) {
			g.setEdgeWeight(edge, 3.0);
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
	
	public boolean hasTypeTwoRelations() {
		return relations.contains("perpendicular") || relations.contains("distanceEQ");
	}
	
	public boolean hasTypeThreeRelations() {
		return relations.contains("leftOF") || relations.contains("rightOF") ;
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
		System.out.println("Graph " +g);
		g.vertexSet().forEach(v ->{
			System.out.println(v.getName() + " scrore: " + v.score);
		});
	}

}
