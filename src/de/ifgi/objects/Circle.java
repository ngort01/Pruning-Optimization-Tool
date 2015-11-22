package de.ifgi.objects;
public class Circle extends Geometry{
	
	private int x, y, r;
	
	public Circle(String name) {
		super(name);
	}
	
	public Circle (String name, int x, int y, int r) {
		super(name);
		this.x = x;
		this.y = y;
		this.r = r;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getX() {
		return this.x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setR(int r) {
		this.r = r;
	}
	
	public int getR() {
		return this.r;
	}
}
