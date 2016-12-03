package math;
import java.util.List;

import _interface.VisualisationPanel;
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
		fig.getPtsMat().importPoints(fig.getPoints(), 3);
		fig.getPtsMat().setHomogeneousCoords();
		fig.getPtsMat().zoom(scaleFactor);
		fig.getPtsMat().exportToPoints(fig.getPoints());
	}
	
	/**
	 * Multiplie la figure par une matrice translation
	 * @param points
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void translatePoints(List<Point> points, double x, double y, double z) {
		Matrice matrix = new Matrice(points.size(), 4);
		matrix.importPoints(points, 3);
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
		fig.getPtsMat().importPoints(fig.getPoints(), 3);
		fig.getPtsMat().translateMatrix(x, y, z);
		fig.getPtsMat().exportToPoints(fig.getPoints());
	}
	

	/**
	 ** Actualise <b>widthFig</b>, <b>heightFig</b>, <b>depthFig</b> ainsi que {@link Figure#getCenter()}
	 **/
	public static void refreshFigDims(VisualisationPanel panel) {
		double widthFig = 0, heightFig = 0, depthFig = 0;
		double left = 0, right = 0, top = 0, bottom = 0, front = 0, back = 0;
		// set all values to opposites of what they should be because of comparisons in if
		if (panel.getHeight() == 0 || panel.getWidth() == 0) {
			widthFig = heightFig = right = bottom = 0;
			left = panel.getWidthWindow();
			top = panel.getHeightWindow();
			back = panel.getWidthWindow();
			front = -panel.getWidthWindow();
		} else {
			widthFig = heightFig = right = bottom = 0;
			left = panel.getWidth();
			top = panel.getHeight();
			back = panel.getWidth();
			front = -panel.getWidth();
		}
		for (Point p : panel.getFigure().getPoints()) {
			if (p.getX() < left) {
				left = p.getX();
			}
			if (p.getX() > right) {
				right = p.getX();
			}
			if (p.getY() > bottom) {
				bottom = p.getY();
			} else if (p.getY() < top) {
				top = p.getY();
			}
			if (p.getZ() > front) {
				front = p.getZ();
			} else if (p.getZ() < back) {
				back = p.getZ();
			}
		}
		widthFig = right - left;
		heightFig = bottom - top;
		depthFig = front - back;
		panel.getFigure().setWidthFig(widthFig);
		panel.getFigure().setHeightFig(heightFig);
		panel.getFigure().getCenter().setCoords(left + (widthFig/2), top + (heightFig/2), back + (depthFig/2)); // ajout pour donner vrai coord dessiné
	}

	/**
	 * Centre la figure par rapport au centre de ce Panel
	 */
	public static void centrerFigure(VisualisationPanel panel) {
		refreshFigDims(panel);
		double moveX, moveY;
		if (panel.getHeight() == 0 || panel.getWidth() == 0) {
			moveX = panel.getFigure().getCenter().getX()-(panel.getWidthWindow()/2);
			moveY = panel.getFigure().getCenter().getY()-(panel.getHeightWindow()/2);
		} else {
			moveX = panel.getFigure().getCenter().getX()-(panel.getWidth()/2);
			moveY = panel.getFigure().getCenter().getY()-(panel.getHeight()/2);
		}
		Calculations.translatePoints(panel.getFigure(), -moveX, -moveY, 0);
	}
	
	
	/**
	 * Applique une homothétie pour que la plus grande dimensions
	 * (largeur ou longueur) de la figure prenne <b>maxSize</b> de l'écran
	 * @param panel 
	 * @param maxSize
	 */
	public static void fitFigureToWindow(VisualisationPanel panel, double maxSize) {
		// scale by height
		refreshFigDims(panel);
		double scale = 1.0;
		if (panel.getHeight() == 0 || panel.getWidth() == 0) {
			if (panel.getFigure().getHeightFig() > panel.getFigure().getWidthFig()) {
				scale = (panel.getHeightWindow() * maxSize) / panel.getFigure().getHeightFig();
			} else { // scale by width
				scale = (panel.getHeightWindow() * maxSize) / panel.getFigure().getWidthFig();
			}
		} else {
			if (panel.getFigure().getHeightFig() > panel.getFigure().getWidthFig()) {
				scale = (panel.getHeight() * maxSize) / panel.getFigure().getHeightFig();
			} else { // scale by width
				scale = (panel.getWidth() * maxSize) / panel.getFigure().getWidthFig();
			}
		}
		Calculations.scale(panel.getFigure(), scale);
	}
	
	/**
	 * Donne la valeur absolue du cosinus de la norme d'une face et le vecteur directeur de la lumière tel que cos(N,L)
	 * @param fig
	 * @param face
	 * @param lightVector
	 * @return un double entre 0.0 et 1.0
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
		fig.getPtsMat().importPoints(fig.getPoints(), 3);
		fig.getPtsMat().setHomogeneousCoords();
		fig.getPtsMat().rotateX(fig, angle);
		fig.getPtsMat().exportToPoints(fig.getPoints());
	}
	
	/**
	 * Applique un rotation sur la figure par l'axe Y
	 * <br>Applique la translation par le centre de la figure d'abord
	 * @param fig
	 * @param angle
	 */
	public static void rotateYByPoint(Figure fig, double angle) {
		fig.getPtsMat().importPoints(fig.getPoints(), 3);
		fig.getPtsMat().setHomogeneousCoords();
		fig.getPtsMat().rotateY(fig, angle);
		fig.getPtsMat().exportToPoints(fig.getPoints());
	}
	
	/**
	 * Applique un rotation sur la figure par l'axe Z
	 * <br>Applique la translation par le centre de la figure d'abord
	 * @param fig
	 * @param angle
	 */
	public static void rotateZByPoint(Figure fig, double angle) {
		fig.getPtsMat().importPoints(fig.getPoints(), 3);
		fig.getPtsMat().setHomogeneousCoords();
		fig.getPtsMat().rotateZ(fig, angle);
		fig.getPtsMat().exportToPoints(fig.getPoints());
	}
	
}
