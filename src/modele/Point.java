package modele;

public class Point {
	
	String nom;
	double x,y,z;
	private int iter;
	
	public Point() {
		iter = -1;
	}
	
	public Point(Double Coordx,Double Coordy) {
		iter = 0;
		x = Coordx;
		y = Coordy;
	}
	
	public Point(Double Coordx,Double Coordy,Double Coordz) {
		this(Coordx, Coordy);
		z = Coordz;
	}
	
	public Point(String nom,Double Coordx,Double Coordy,Double Coordz){
		this(Coordx, Coordy, Coordz);
		this.nom=nom;
	}
	
	public void setCoords(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void setCoords(double x, double y, double z) {
		setCoords(x, y);
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public String getNom() {
		return nom;
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
	
	/**
	 * Ajoute au fur et a mesure les coordonnes de ce point
	 * @param nb le coordonnée a ajouter dans ce point
	 */
	public void add(Double nb) {
		if (iter == -1) {
			x = nb;
			iter++;
		} else if (iter == 0) {
			y = nb;
			iter++;
		} else if (iter == 1) {
			z = nb;
			iter++;
		}
	}
	
	public void setName(String name) {
		this.nom = name;
	}
	
	/**
	 * Si ce points comporte 3 coordonnées
	 * @return
	 */
	public boolean complet() {
		return iter == 2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}
	
	/**
	 * Donne la coordonnée correspondante.<br>
	 * 0 = x, 1 = y, 2 = z
	 * @param coord
	 * @return
	 */
	public double getCoord(int coord) {
		if (coord == 0) {
			return this.x;
		} else if (coord == 1) {
			return this.y;
		} else {
			return this.z;
		}
	}
	
	/**
	 * Set les coordonnées de ce points à 0
	 */
	public void resetCoords() {
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
		iter = -1;
	}
}
