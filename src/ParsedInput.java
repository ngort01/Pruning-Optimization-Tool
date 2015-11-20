import java.util.ArrayList;
import java.util.HashMap;
import org.jgrapht.*;
import org.jgrapht.graph.*;

/**
 * Represents the input from the text file
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
	private UndirectedGraph<Geometry, DefaultEdge> g;


	public ParsedInput() {
		this.objects = new HashMap<String, Geometry>();
		this.points = new HashMap<String, Point>();
		this.lines = new HashMap<String, Point>();
		this.circles = new HashMap<String, Circle>();
		this.relations = new ArrayList<String>();
		this.g = new SimpleGraph<Geometry, DefaultEdge>(DefaultEdge.class);

	}

	public void addPoint(String name) {
		Point p = new Point(name);
		this.objects.put(name, p);
		this.points.put(name, p);
		this.g.addVertex(p);
	}
	
	public void removePoint(String name) {
		this.points.remove(name);
		this.objects.remove(name);
	}

	public void addLine(String name) {
		// this.lines.add(name);
	}
	
	public void removeLine(String name) {
		this.lines.remove(name);
		this.objects.remove(name);
	}

	public void addCircle(String name) {
		Circle c = new Circle(name);
		this.objects.put(name, c);
		this.circles.put(name, c);
		this.g.addVertex(c);
	}


	public void removeCircle(String name) {
		this.circles.remove(name);
		this.objects.remove(name);
	}

	public void addRelation(String[] relation) {
		String[] vertices = relation[1].split("\\,");
		this.relations.add(relation[0]);
		g.addEdge(this.objects.get(vertices[0]), this.objects.get(vertices[1]));
	}

	public void removeRelation(String[] relation) {
		String[] vertices = relation[1].split("\\,");
		this.relations.remove(relation[0]);
		g.removeAllEdges(this.objects.get(vertices[0]), this.objects.get(vertices[1]));
	}

	public void print() {
		System.out.println(this.points.toString());
		System.out.println(this.lines.toString());
		System.out.println(this.circles.toString());
		System.out.println(this.relations.toString());
		System.out.println(g);
		this.g.vertexSet().iterator().forEachRemaining(v -> {
			// System.out.println(v.getX());
		});
	}

}
