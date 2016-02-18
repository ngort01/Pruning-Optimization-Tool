package de.ifgi.objects;

public class Line extends Geometry {
	private Point s, e;

	public Line(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public Line (String name, Point s, Point e) {
		super(name);
		this.s = s;
		this.e = e;
		//this.ground();
	}

	@Override
	public void print() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setX(int x) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setY(int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getY() {
		return this.s.getY();
	}

	@Override
	public void setStartPoint(Point s) {
		this.s = s;	
	}

	@Override
	public void setEndPoint(Point e) {
		this.e = e;
	}

	@Override
	public Point getStart() {
		return this.s;
	}

	@Override
	public Point getEnd() {
		return this.e;
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
