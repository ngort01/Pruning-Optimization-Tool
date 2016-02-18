package de.ifgi.objects;

public abstract class Geometry implements Comparable<Geometry> {
	private String name;
	public boolean grounded = false;
	// shows how likely it is that this object will be chosen for grounding
	public int score = 0;

	public Geometry(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void ground() {
		this.grounded = true;
	}

	public boolean isGrounded() {
		return grounded;
	}

	@Override
	public int compareTo(Geometry g) {
		if (this.score > g.score) {
			return -1;
		} else if(this.score < g.score) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public abstract void print();

	// points
	public abstract void setX(int x);

	public abstract void setY(int y);

	public abstract int getX();

	public abstract int getY();
	
	// lines
	public abstract void setStartPoint(Point s);

	public abstract void setEndPoint(Point e);

	public abstract Point getStart();

	public abstract Point getEnd();
	
	// circles
	public abstract void setCentre(Point c);
	
	public abstract Point getCentre();
	
	public abstract void setRadius(double r);
	
	public abstract double getRadius();
}
