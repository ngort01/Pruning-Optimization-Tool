
public class Point extends Geometry{
	
	private int x, y;
	
	public Point(String name) {
		super(name);
	}
	
	public Point (String name, int x, int y) {
		super(name);
		this.x = x;
		this.y = y;
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
}
