package modele;


public class Segment {
	Point pointA;
	Point pointB;
	
	/**
	 * Cree un segment à partir de deux points A et B
	 * @param a
	 * @param b
	 */
	public Segment(Point a, Point b){
		this.pointA=a;
		this.pointB=b;
	}
	
	/**
	 * Retourne le point A du segment
	 * @return le point A
	 */
	public Point getPointA(){
		return this.pointA;
	}
	
	/**
	 * Retourne le point B du segment
	 * @return le point B
	 */
	public Point getPointB(){
		return this.pointB;
	}

	@Override
	public String toString() {
		return "Segment [pointA=" + pointA + ", pointB=" + pointB + "]";
	}
	
}
