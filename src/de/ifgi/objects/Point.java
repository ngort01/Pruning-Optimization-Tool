package de.ifgi.objects;

public class Point extends Geometry{
	
	private int x = -1, y = -1;
	
	public Point(String name) {
		super(name);
	}
	
	public Point (String name, int x, int y) {
		super(name);
		this.x = x;
		this.y = y;
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

	public void print() {
		if (this.isGrounded()) {
			if (x != -1) System.out.println(this.getName() + "x = " + x);
			if (y != -1) System.out.println(this.getName() + "y = " + y);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public Point getCentre() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRadius(double r) {
		// TODO Auto-generated method stub
	}

	@Override
	public double getRadius() {
		// TODO Auto-generated method stub
		return 0;
	}

}
