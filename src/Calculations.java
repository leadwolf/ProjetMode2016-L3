import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class Calculations {

	/**
	 * <b>VIEUX A NE PAS UTILISER</b><br>
	 * Donne un transformation de x. <br>
	 * Suite de {@link #transformePoints(List, List)}
	 * 
	 * @param pt
	 *            le point à transformer
	 * @return la coordonnée x transformée
	 */
	private static double transformeX(Point pt) {
		return ((pt.x * 20) + (-10 * pt.z));
	}

	/**
	 * <b>VIEUX A NE PAS UTILISER</b><br>
	 * Donne un transformation de y. <br>
	 * Suite de {@link #transformePoints(List, List)}
	 * 
	 * @param pt
	 *            pt le point à transformer
	 * @return la coordonnée y transformée
	 */
	private static double transformeY(Point pt) {
		return ((pt.y * 20) + (10 * pt.z)) * -1;
	}

	/**
	 * <b>VIEUX A NE PAS UTILISER</b><br>
	 * Transforme les points selon l'équation DESTx = (SRC.x * 20) + (10 *
	 * SRC.z) et DESTy = (SRC.y * 20) + (SRC * pt.z) * -1 pour un affichage dans
	 * {@link #paintComponent(Graphics)}
	 * 
	 * @param src
	 *            la source de points
	 * @param dest
	 *            la liste dans laquelle stocker les points transformés
	 */
	public static void transformePoints(List<Point> src, List<Point> dest) {
		for (Point pt : src) {
			Point tmp = new Point();
			tmp.add(transformeX(pt));
			tmp.add(transformeY(pt));
			dest.add(tmp);
		}
	}

	/**
	 * <b>VIEUX A NE PAS UTILISER</b><br>
	 * @param src
	 * @param dest
	 */
	public static void transformePoint(Point src, Point dest) {
		dest.add(transformeX(src));
		dest.add(transformeY(src));
	}
	
	/**
	 * 
	 * @param points
	 * @param x distance to translate
	 * @param y distance to translate
	 */
	public static void translatePoints(List<Point> points, double x, double y) {
		for (Point pt : points) {
			pt.setX(pt.getX() + (x));
			pt.setY(pt.getY() + (y));
		}
	}
	
	/**
	 * 
	 * @param points
	 * @param x distance to translate
	 * @param y distance to translate
	 */
	public static void translatePoints(List<Point> points, int x, int y, int z) {
		for (Point pt : points) {
			pt.setX(pt.getX() + (x));
			pt.setY(pt.getY() + (y));
			pt.setZ(pt.getY() + (z));
		}
	}
	
    
	/**
	 * Prend un List de Point pour appliquer une rotation par X selon l'origine.
	 * <br>Les stocke dans la Matrice donnée  pour le calcul.
	 * @param points
	 * @param mat
	 * @param angle
	 */
	public static void rotateX(Figure fig, double angle) {
		fig.getPtsMat().importPoints(fig.getPtsTrans());
		fig.getPtsMat().rotateX(angle);
		fig.getPtsMat().exportToPoints(fig.getPtsTrans());
	}
	
	/**
	 * Prend un List de Point pour appliquer une rotation par Y selon l'origine.
	 * <br>Les stocke dans la Matrice donnée pour le calcul.
	 * @param points
	 * @param mat
	 * @param angle
	 */
	public static void rotateY(Figure fig, double angle) {
		fig.getPtsMat().importPoints(fig.getPtsTrans());
		fig.getPtsMat().rotateY(angle);
		fig.getPtsMat().exportToPoints(fig.getPtsTrans());
	}
	
	/**
	 * Prend un List de Point pour appliquer une rotation par Z selon l'origine.
	 * <br>Les stocke dans la Matrice donnée pour le calcul.
	 * @param points
	 * @param mat
	 * @param angle
	 */
	public static void rotateZ(Figure fig, double angle) {
		fig.getPtsMat().importPoints(fig.getPtsTrans());
		fig.getPtsMat().rotateZ(angle);
		fig.getPtsMat().exportToPoints(fig.getPtsTrans());
	}
	
	
	/**
	 * Prend une List de Point et applique une rotation par X selon l'origine.
	 * <br>Prend plus de mémoire car crée une nouvelle matrice.
	 * @param points
	 * @param nBCoords
	 * @param angle
	 */
	public static void rotateXByPoint(Figure fig, double angle) {
		fig.getPtsMat().importPoints(fig.getPtsTrans());
		fig.getPtsMat().rotateXByPoint(fig.getCenter(), angle);
		fig.getPtsMat().exportToPoints(fig.getPtsTrans());
	}
}
