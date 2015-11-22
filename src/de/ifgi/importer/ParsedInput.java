package de.ifgi.importer;
import java.util.ArrayList;
import java.util.HashMap;
import org.jgrapht.*;
import org.jgrapht.graph.*;

import de.ifgi.objects.Circle;
import de.ifgi.objects.Geometry;
import de.ifgi.objects.Point;

/**
 * Represents the input from the text file
 * Stores objects, relations and a corresponding graph
 * 
 * @author Niko
 *
 */
public class ParsedInput {
	
	private HashMap<String, Geometry> objects;
	private HashMap<String, Point> points;
	private HashMap<String, Point> lines;
	private HashMap<String, Circle> circles;
	private ArrayList<String> relations;
	private ArrayList<String> tests;
	private UndirectedGraph<Geometry, Relation> g;


	public ParsedInput() {
		this.objects = new HashMap<String, Geometry>();
		this.points = new HashMap<String, Point>();
		this.lines = new HashMap<String, Point>();
		this.circles = new HashMap<String, Circle>();
		this.relations = new ArrayList<String>();
		this.tests = new ArrayList<String>();
		this.g = new SimpleGraph<Geometry, Relation>(new ClassBasedEdgeFactory<Geometry, Relation>(Relation.class));

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
		// this.lines.add(name);
	}
	
	public void removeLine(String name) {
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
		circles.remove(name);
		objects.remove(name);
	}

	public void addRelation(String[] relation) {
		String rel = relation[0];
		String[] vertices = relation[1].split("\\,");
		Geometry v1 = objects.get(vertices[0]);
		Geometry v2 = objects.get(vertices[1]);
		relations.add(rel);
		g.addEdge(v1, v2, new Relation<Geometry>(v1, v2, rel));
	}

	public void removeRelation(String[] relation) {
		String rel = relation[0];
		String[] vertices = relation[1].split("\\,");
		Geometry v1 = objects.get(vertices[0]);
		Geometry v2 = objects.get(vertices[1]);
		relations.remove(rel);
		g.removeAllEdges(v1, v2);
	}
	
	public void addTest(String name) {
		tests.add(name);
	}
	
	public void removeTest(String name) {
		tests.remove(name);
	}
	
	public boolean hasPoints() {
		return !points.isEmpty();
	}
	
	public boolean hasLiness() {
		return !lines.isEmpty();
	}
	
	public boolean hasCircles() {
		return !circles.isEmpty();
	}
	
	public boolean hasTypeTwoRelations() {
		return relations.contains("perpendicular") || tests.contains("distanceEQ");
	}
	
	public boolean hasTypeThreeRelations() {
		return relations.contains("leftOF") || relations.contains("rightOF") ;
	}

	public void print() {
		System.out.println(points.toString());
		System.out.println(lines.toString());
		System.out.println(circles.toString());
		System.out.println(relations.toString());
		System.out.println(tests.toString());
		System.out.println(g);
		g.edgeSet().iterator().forEachRemaining(e -> {
			Geometry p = (Geometry) e.getV1();

			System.out.println(p.getName());
			//System.out.println(e.toString());
		});
	}

}
