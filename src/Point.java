
public class Point {
	
	String nom;
	double x,y,z;
	private int iter;
	
	public Point() {
		iter = 0;
	}
	
	public Point(String nom,Double Coordx,Double Coordy,Double Coordz){
		this.nom=nom;
		iter = 0;
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

	@Override
	public String toString() {
		return "Point [nom=" + nom + ", x=" + x + ", y=" + y + ", z=" + z + "]";
	}
	
	public void add(Double nb) {
		if (iter == 0) {
			x = nb;
			iter++;
		} else if (iter == 1) {
			y = nb;
			iter++;
		} else if (iter == 2) {
			z = nb;
		}
	}
	
	public void setName(String name) {
		this.nom = name;
	}

}
