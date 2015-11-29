package de.ifgi.objects;
public class Circle extends Geometry{
	
	private int x = -1, y = -1, r = -1;
	
	public Circle(String name) {
		super(name);
	}
	
	public Circle (String name, int x, int y, int r) {
		super(name);
		this.x = x;
		this.y = y;
		this.r = r;
		this.ground();
	}
	
	public void setX(int x) {
		this.x = x;
		this.ground();
	}
	
	public int getX() {
		return this.x;
	}
	
	public void setY(int y) {
		this.y = y;
		this.ground();
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setR(int r) {
		this.r = r;
		this.ground();
	}
	
	public int getR() {
		return this.r;
	}
	
	public void print() {
		if (this.isGrounded()) {
			if (x != -1) System.out.println(this.getName() + "x = " + x);
			if (y != -1) System.out.println(this.getName() + "y = " + y);
			if (r != -1) System.out.println(this.getName() + "r = " + r);	
		}
	}
}
