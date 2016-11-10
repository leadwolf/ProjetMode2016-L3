import java.awt.Graphics;
import java.util.List;

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
