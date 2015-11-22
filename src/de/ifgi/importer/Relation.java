package de.ifgi.importer;
import org.jgrapht.graph.DefaultEdge;

public class Relation<Geometry> extends DefaultEdge {
	private static final long serialVersionUID = -7975181062553556033L;
	private Geometry v1;
    private Geometry v2;
    private String relation;

    public Relation(Geometry v1, Geometry v2, String relation) {
        this.v1 = v1;
        this.v2 = v2;
        this.relation = relation;
    }

    public Geometry getV1() {
        return v1;
    }

    public Geometry getV2() {
        return v2;
    }

    public String toString() {
        return relation;
    }
}	
