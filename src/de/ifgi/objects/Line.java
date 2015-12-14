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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void print() {
		// TODO Auto-generated method stub
		
	}

}
