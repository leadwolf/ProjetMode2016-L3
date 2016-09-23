
public class Segment {
	Point pointA;
	Point pointB;
	
	public Segment(Point a, Point b){
		this.pointA=a;
		this.pointB=b;
	}
	
	public Point getPointA(){
		return this.pointA;
	}
	
	public Point getPointB(){
		return this.pointB;
	}

	@Override
	public String toString() {
		return "Segment [pointA=" + pointA + ", pointB=" + pointB + "]";
	}
	
}
