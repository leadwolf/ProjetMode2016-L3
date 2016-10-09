import java.awt.Graphics;
import java.util.List;

public class Calculations {
	
	
	/**
	 * Donne un transformation de x.
	 * <br>Suite de {@link #transformePoints(List, List)}
	 * @param pt le point à transformer
	 * @return la coordonnée x transformée
	 */
	private static double transformeX(Point pt) {
		return ((pt.x * 20) + (-10 * pt.z));
	}
	
	
	/**
	 * Donne un transformation de y.
	 * <br>Suite de {@link #transformePoints(List, List)}
	 * @param pt pt le point à transformer
	 * @return la coordonnée y transformée
	 */
	private static double transformeY(Point pt) {
		return ((pt.y * 20) + (10 * pt.z)) * -1;
	}
	
	/**
	 * Transforme les points selon l'équation DESTx = (SRC.x * 20) + (10 * SRC.z) et DESTy = (SRC.y * 20) + (SRC * pt.z) * -1
	 * pour un affichage dans {@link #paintComponent(Graphics)}
	 * @param src la source de points
	 * @param dest la liste dans laquelle stocker les points transformés
	 */
	public static void transformePoints(List<Point> src, List<Point> dest) {
		for (Point pt : src) {
			Point tmp = new Point();
			tmp.add(transformeX(pt));
			tmp.add(transformeY(pt));
			dest.add(tmp);
		}
	}
	
	public static void transformePoint(Point src, Point dest) {
		dest.add(transformeX(src));
		dest.add(transformeY(src));
	}
	
	/**
	 * <b>INUTILE A L'INSTANT</b>
	 * <br>Determine quel face est la plus en avant.
	 * <br>Servira de comparaison pour savoir quels segments cacher.
	 */
	public static int determineFrontFace(List<Face> faces) {
		if (faces.size() > 1) {
			int premFace = 0;
			double sommeZPtsFace = 0.0;
			double moyenneZPtsFace = 0.0;
			double oldMoyenne = -1;
			for (int i=0;i<faces.size();i++) {
				sommeZPtsFace = 0.0;
				moyenneZPtsFace = 0.0;
				for (Point pt : faces.get(i).getList()) {
					sommeZPtsFace += pt.getZ();
				}
				moyenneZPtsFace = sommeZPtsFace / (faces.get(i).getList().size()-1);
				if (moyenneZPtsFace > oldMoyenne) {
					oldMoyenne = moyenneZPtsFace;
					premFace = i;
				}
			}
			return premFace;
		}
		return -1;
	}
}
