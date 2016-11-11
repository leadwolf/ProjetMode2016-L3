package math;
import java.awt.Graphics;
import java.util.List;

import modele.Face;
import modele.Figure;
import modele.Point;

public class Calculations {

	/**
	 * Multiplie la figure par une homothétie de rapport <b>scaleFactor</b>
	 * @param fig
	 * @param scaleFactor
	 */
	public static void scale(Figure fig, double scaleFactor) {
		fig.getPtsMat().importPoints(fig.getPtsTrans());
		fig.getPtsMat().setHomogeneousCoords();
		fig.getPtsMat().zoom(scaleFactor);
		fig.getPtsMat().exportToPoints(fig.getPtsTrans());
	}
	
	/**
	 * Multiuplie la figure par une matrice translation
	 * @param points
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void translatePoints(List<Point> points, double x, double y, double z) {
		Matrice matrix = new Matrice(points.size(), 4);
		matrix.importPoints(points);
		matrix.setHomogeneousCoords();
		matrix.translateMatrix(x, y, z);
		matrix.exportToPoints(points);
	}
	
	/**
	 * Multiplie la figure par une matrice translation
	 * @param fig
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void translatePoints(Figure fig, double x, double y, double z) {
		fig.getPtsMat().importPoints(fig.getPtsTrans());
		fig.getPtsMat().translateMatrix(x, y, z);
		fig.getPtsMat().exportToPoints(fig.getPtsTrans());
	}
	
	/**
	 * Done la valeur absolue du cosinus de la norme d'une face et le vecteur directeur de la lumière
	 * @param fig
	 * @param face
	 * @param lightVector
	 * @return
	 */
	public static double getGreyScale(Figure fig, Face face, Vecteur lightVector) {
		Point firstPoint = face.getList().get(0);
		Point secondPoint = face.getList().get(1);
		Point thirdPoint = face.getList().get(2);
		Vecteur firstVector = new Vecteur(firstPoint, secondPoint);
		Vecteur secondVector = new Vecteur(firstPoint, thirdPoint);
		Vecteur normale = Vecteur.prodVectoriel(firstVector, secondVector); // normale = produit de 2 vec du plan
		
//		abs(cos(L, N) = abs(prodScal(L,N)/norme(L) * norme(N)) 
		return Math.abs( (Vecteur.prodScalaire(lightVector, normale)) / (lightVector.getNorme()*normale.getNorme()) );
	}
	
	/**
	 * Applique un rotation sur la figure par l'axe X
	 * <br>Applique la translation par le centre de la figure d'abord
	 * @param fig
	 * @param angle
	 */
	public static void rotateXByPoint(Figure fig, double angle) {
		fig.getPtsMat().importPoints(fig.getPtsTrans());
		fig.getPtsMat().setHomogeneousCoords();
		fig.getPtsMat().rotateX(fig, angle);
		fig.getPtsMat().exportToPoints(fig.getPtsTrans());
	}
	
	/**
	 * Applique un rotation sur la figure par l'axe Y
	 * <br>Applique la translation par le centre de la figure d'abord
	 * @param fig
	 * @param angle
	 */
	public static void rotateYByPoint(Figure fig, double angle) {
		fig.getPtsMat().importPoints(fig.getPtsTrans());
		fig.getPtsMat().setHomogeneousCoords();
		fig.getPtsMat().rotateY(fig, angle);
		fig.getPtsMat().exportToPoints(fig.getPtsTrans());
	}
	
	/**
	 * Applique un rotation sur la figure par l'axe Z
	 * <br>Applique la translation par le centre de la figure d'abord
	 * @param fig
	 * @param angle
	 */
	public static void rotateZByPoint(Figure fig, double angle) {
		fig.getPtsMat().importPoints(fig.getPtsTrans());
		fig.getPtsMat().setHomogeneousCoords();
		fig.getPtsMat().rotateZ(fig, angle);
		fig.getPtsMat().exportToPoints(fig.getPtsTrans());
	}
	
}
