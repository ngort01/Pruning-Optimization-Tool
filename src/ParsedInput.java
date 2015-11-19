import java.util.ArrayList;

public class ParsedInput {

	private ArrayList<String> points;
	private ArrayList<String> lines;
	private ArrayList<String> circles;
	private ArrayList<String> relations;

	public ParsedInput() {
		this.points = new ArrayList<String>();
		this.lines = new ArrayList<String>();
		this.circles = new ArrayList<String>();
		this.relations = new ArrayList<String>();
	}

	public void addPoint(String name) {
		this.points.add(name);
	}

	public void addLine(String name) {
		this.lines.add(name);
	}

	public void addCircle(String name) {
		this.circles.add(name);
	}

	public void addRelation(String relation) {
		this.relations.add(relation);
	}

	public void removePoint(String name) {
		this.points.remove(name);
	}

	public void removeLine(String name) {
		this.lines.remove(name);
	}

	public void removeCircle(String name) {
		this.circles.remove(name);
	}

	public void removeRelation(String relation) {
		this.relations.remove(relation);
	}

	public String toString() {
		System.out.println(this.points.toString());
		System.out.println(this.lines.toString());
		System.out.println(this.circles.toString());
		System.out.println(this.relations.toString());
		return "";
	}

}
