package de.ifgi.importer;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Relation<Geometry> extends DefaultWeightedEdge {
	private static final long serialVersionUID = -7975181062553556033L;
	private Geometry v1;
    private Geometry v2;
    private String type;

    public Relation(Geometry v1, Geometry v2, String type) {
    	super();
        this.v1 = v1;
        this.v2 = v2;
        this.type = type;
    }

    public Geometry getV1() {
        return v1;
    }

    public Geometry getV2() {
        return v2;
    }

    public String getType() {
        return type;
    }
}	
