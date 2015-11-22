package de.ifgi.objects;

public abstract class Geometry {
	private String name;
	
	public Geometry(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

	public abstract int getX();
	public abstract int getY();
}
