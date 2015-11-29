package de.ifgi.objects;

public abstract class Geometry implements Comparable<Geometry> {
	private String name;
	private boolean grounded = false;
	// number of equivalence relations, e.g. center
	public int equivalenceRels = 0;

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
		if (this.equivalenceRels > g.equivalenceRels) {
			return -1;
		} else if(this.equivalenceRels < g.equivalenceRels) {
			return 1;
		} else {
			return 0;
		}
	}

	public abstract void setX(int x);

	public abstract void setY(int y);

	public abstract int getX();

	public abstract int getY();

	public abstract void print();
}
