package math;

import java.util.Arrays;

import modele.Point;

public class Vecteur {

	private double[] vecteur;
	private double norme;

	/**
	 * Retourne la norme du vecteur.
	 */
	public double getNorme() {
		return norme;
	}
	
	/**
	 * Cree un vecteur à partir d'un autre vecteur.
	 * @param vecteur
	 */
	public Vecteur(double[] vecteur) {
		this.vecteur = new double[vecteur.length];
		norme = 0;
		for (int i=0;i<vecteur.length;i++) {
			this.vecteur[i] = vecteur[i];
		}
		setNorme();
	}

	/**
	 * Cree un vecteur depuis deux points
	 * @param pt1
	 * @param pt2
	 */
	public Vecteur(Point pt1, Point pt2) {
		vecteur = new double[3];
		norme = 0;
		for (int i=0;i<3;i++) {
			vecteur[i] = pt2.getCoord(i) - pt1.getCoord(i);
		}
		setNorme();
	}
	
	public double[] getVecteur() {
		return vecteur;
	}

	/**
	 * Set la norme du vecteur
	 */
	private void setNorme() {
		double sum = 0;
		for (int i=0;i<vecteur.length;i++) {
			sum += Math.pow(vecteur[i], 2);
		}
		this.norme = Math.sqrt(sum);
	}
	
	/**
	 * Produit scalaire de deux vecteurs
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double prodScalaire(Vecteur v1, Vecteur v2) {
		double[] first = v1.getVecteur();
		double[] second = v2.getVecteur();
		return (first[0]*second[0]) + (first[1]*second[1]) + (first[2]*second[2]);
	}
	
	/**
	 * Produit scalaire direct de deux vecteurs
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double prodScalaire(double[] v1, double[] v2) {
		return (v1[0]*v2[0]) + (v1[1]*v2[1]) + (v1[2]*v2[2]);
	}
	
	/**
	 * Permet de multiplier des vectuers ayant 3 composants
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static Vecteur prodVectoriel(Vecteur v1, Vecteur v2) {
//		a = (a0, a1, a2)
//		b = (b0, b1, b2)
//		c = a x b
//		c = (a1b2-a2b1, a2b0-a0b2, a0b1-a1b0)
		double[] v1double = v1.getVecteur();
		double[] v2double = v2.getVecteur();
		double first = (v1double[1] * v2double[2]) - (v1double[2] * v2double[1]);
		double second = (v1double[2] * v2double[0]) - (v1double[0] * v2double[2]);
		double third = (v1double[0] * v2double[1]) - (v1double[1] * v2double[0]);
		
		double[] resDouble = new double[]{first, second, third};
		return new Vecteur(resDouble);
	}
	
	/**
	 * Permet de multiplier directement deux vecteurs ayant 3 composants
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double[] prodVectoriel(double[] v1, double[] v2) {
//		a = (a1, a2, a3)
//		b = (b1, b2, b3)
//		c = a x b
//		c = (a1b2-a2b1, a2b0-a0b2, a0b1-a1b0)
		double first = (v1[1] * v2[2]) - (v1[2] * v2[1]);
		double second = (v1[2] * v2[0]) - (v1[0] * v2[2]);
		double third = (v1[0] * v2[1]) - (v1[1] * v2[0]);
		
		return new double[]{first, second, third};
	}
	
	public static String toStringVecteur(Vecteur v) {
		String res = "";
		res += "vecteur = (" + v.getVecteur()[0] + ", " + v.getVecteur()[1] + ", " + v.getVecteur()[2] + ")" ;
		return res;
	}
	
	public String toStringVecteur() {
		String res = "";
		res += "vecteur = (" + vecteur[0] + ", " + vecteur[1] + ", " + vecteur[2] + ")" ;
		return res;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(norme);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Arrays.hashCode(vecteur);
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
		Vecteur other = (Vecteur) obj;
		if (Double.doubleToLongBits(norme) != Double.doubleToLongBits(other.norme))
			return false;
		if (!Arrays.equals(vecteur, other.vecteur))
			return false;
		return true;
	}
	
	
		
}
