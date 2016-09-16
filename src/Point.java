
public class Point {
	
	int nom;
	double x,y,z;
	
	Point(int nom,Double Coordx,Double Coordy,Double Coordz){
		this.nom=nom;
		x = Coordx;
		y = Coordy;
		z = Coordz;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public String toString() {
		return "Le point "+ nom + "est de coordonnée " + x + y + z;
	}	
	
	

}
