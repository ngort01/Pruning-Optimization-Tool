package de.ifgi.objects;
public class Circle extends Geometry{
	
	private Point c; 
	private double r = -1;
	
	public Circle(String name) {
		super(name);
	}
	
	public Circle (String name, Point c) {
		super(name);
		this.c = c;
		//this.ground();
	}
	
	@Override
	public void setX(int x) {
		this.c.setX(x);
		this.ground();
	}

	@Override
	public void setY(int y) {
		this.c.setY(y);
		this.ground();
	}

	@Override
	public int getX() {
		return this.c.getX();
	}

	@Override
	public int getY() {
		return this.c.getY();
	}
	

	public void print() {
		if (this.isGrounded()) {
			Point centre = this.c;
			int x = centre.getX();
			int y = centre.getY();
			if (x != -1) System.out.println(this.getName() + "x = " + x);
			if (y != -1) System.out.println(this.getName() + "y = " + y);
			if (this.r != -1) System.out.println(this.getName() + "r = " + this.r);	
		}
	}

	@Override
	public void setStartPoint(Point s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEndPoint(Point e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Point getStart() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point getEnd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCentre(Point c) {
		this.c = c;
	}

	@Override
	public Point getCentre() {
		return this.c;
	}

	@Override
	public void setRadius(double r) {
		this.r = r;
	}

	@Override
	public double getRadius() {
		return this.r;
	}

}
