package ply.plyModel.modeles;

import java.awt.geom.Path2D;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import ply.math.Matrice;
import ply.math.Vecteur;
import ply.plyModel.elements.Face;
import ply.plyModel.elements.Point;
import ply.plyModel.vues.VisualisationPanel;
import ply.reader.Reader;

public class FigureModelNew extends Observable {

	private Path path;
	private int vertexCount;
	private int faceCount;
	private List<Point> vertexList;
	private List<Face> faceList;

	private List<Path2D> polygones;
	private List<Path2D> ombrePolygones;

	private Point center;
	private Matrice ptsMat;
	private Matrice ombre;

	private Vecteur lightVector;
	private double heightFig, widthFig, depthFig;

	public FigureModelNew(Reader reader) {
		this.path = reader.getFile().toPath();
		vertexCount = reader.getVertexCount();
		faceCount = reader.getFaceCount();
		vertexList = reader.getVertexList();
		invertPoints();
		faceList = reader.getFaceList();
		polygones = new ArrayList<>();
		ombrePolygones = new ArrayList<>();
		center = new Point();
		ptsMat = new Matrice(vertexCount, 4);
		ptsMat.setHomogeneousCoords();
		lightVector = new Vecteur(new double[] { 0, 0, -1 });
	}

	/**
	 * Inverse la figure par rapport à  l'axe X car on dessine du haut en bas
	 */
	private void invertPoints() {
		for (Point pt : vertexList) {
			pt.setY(pt.getY() * -1);
		}
	}

	/**
	 * @return the path
	 */
	public Path getPath() {
		return path;
	}

	/**
	 * @return the vertexCount
	 */
	public int getVertexCount() {
		return vertexCount;
	}

	/**
	 * @return the faceCount
	 */
	public int getFaceCount() {
		return faceCount;
	}

	/**
	 * @return the vertexList
	 */
	public List<Point> getVertexList() {
		return vertexList;
	}

	/**
	 * @return the faceList
	 */
	public List<Face> getFaceList() {
		return faceList;
	}

	/**
	 * @return the polygones
	 */
	public List<Path2D> getPolygones() {
		return polygones;
	}

	/**
	 * @return the ombrePolygones
	 */
	public List<Path2D> getOmbrePolygones() {
		return ombrePolygones;
	}

	/**
	 * @return the center
	 */
	public Point getCenter() {
		return center;
	}

	/**
	 * @return the ombre
	 */
	public Matrice getOmbre() {
		return ombre;
	}

	/**
	 * @return the lightVector
	 */
	public Vecteur getLightVector() {
		return lightVector;
	}

	/**
	 * Multiplie la figure par une homothétie de rapport <b>scaleFactor</b>
	 * 
	 * @param scaleFactor
	 */
	public void scale(double scaleFactor) {
		ptsMat.importPoints(vertexList, 3);
		ptsMat.setHomogeneousCoords();
		ptsMat.zoom(scaleFactor);
		ptsMat.exportToPoints(vertexList);
		refreshModel();
	}

	/**
	 * Multiplie la figure par une matrice translation
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void translatePoints(double x, double y, double z) {
		ptsMat.importPoints(vertexList, 3);
		ptsMat.translateMatrix(x, y, z);
		ptsMat.exportToPoints(vertexList);
		refreshModel();
	}

	/**
	 ** Actualise <b>widthFig</b>, <b>heightFig</b>, <b>depthFig</b> ainsi que {@link FigureModel#getCenter()}
	 * 
	 * @param panel
	 **/
	public void refreshFigDims(VisualisationPanel panel) {
		widthFig = 0;
		heightFig = 0;
		depthFig = 0;
		double left = 0, right = 0, top = 0, bottom = 0, front = 0, back = 0;
		// set all values to opposites of what they should be because of
		// comparisons in if
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
		for (Point p : vertexList) {
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
		center.setCoords(left + (widthFig / 2), top + (heightFig / 2), back + (depthFig / 2)); // ajout
																								// pour
																								// donner
																								// vrai
																								// coord
																								// dessiné
	}

	/**
	 * Centre la figure par rapport au centre de ce Panel
	 * 
	 * @param panel
	 */
	private void centrerFigure(VisualisationPanel panel) {
		refreshFigDims(panel);
		double moveX, moveY;
		if (panel.getHeight() == 0 || panel.getWidth() == 0) {
			moveX = center.getX() - (panel.getWidthWindow() / 2);
			moveY = center.getY() - (panel.getHeightWindow() / 2);
		} else {
			moveX = center.getX() - (panel.getWidth() / 2);
			moveY = center.getY() - (panel.getHeight() / 2);
		}
		translatePoints(-moveX, -moveY, 0);
	}

	/**
	 * Applique une homothétie pour que la plus grande dimensions (largeur ou longueur) de la figure prenne <b>maxSize</b> de
	 * l'écran
	 * 
	 * @param panel
	 * @param maxSize
	 */
	private void fitFigureToWindow(VisualisationPanel panel, double maxSize) {
		// scale by height
		refreshFigDims(panel);
		double scaleHeight = 1.0;
		double scaleWidth = 1.0;
		if (panel.getHeight() == 0 || panel.getWidth() == 0) {
			scaleHeight = (panel.getHeightWindow() * maxSize) / heightFig;
			scaleWidth = (panel.getWidthWindow() * maxSize) / widthFig;
		} else {
			scaleHeight = (panel.getHeight() * maxSize) / heightFig;
			scaleWidth = (panel.getWidth() * maxSize) / widthFig;
		}
		if (scaleHeight < scaleWidth) {
			scale(scaleHeight);
		} else {
			scale(scaleWidth);
		}
	}

	/**
	 * Donne la valeur absolue du cosinus de la norme d'une face et le vecteur directeur de la lumière tel que cos(N,L)
	 * 
	 * @param face
	 * @param lightVector
	 * @return un double entre 0.0 et 1.0
	 */
	public static double getGreyScale(Face face, Vecteur lightVector) {
		Point firstPoint = face.getVertexList().get(0);
		Point secondPoint = face.getVertexList().get(1);
		Point thirdPoint = face.getVertexList().get(2);
		Vecteur firstVector = new Vecteur(firstPoint, secondPoint);
		Vecteur secondVector = new Vecteur(firstPoint, thirdPoint);
		Vecteur normale = Vecteur.prodVectoriel(firstVector, secondVector); // normale = produit de 2 vec du plan

		// abs(cos(L, N) = abs(prodScal(L,N)/norme(L) * norme(N))
		return Math.abs((Vecteur.prodScalaire(lightVector, normale)) / (lightVector.getNorme() * normale.getNorme()));
	}

	/**
	 * Applique un rotation sur la figure par l'axe X <br>
	 * Applique la translation par le centre de la figure d'abord
	 * 
	 * @param angle
	 */
	public void rotateXByPoint(double angle) {
		ptsMat.importPoints(vertexList, 3);
		ptsMat.setHomogeneousCoords();
		ptsMat.rotateX(this, angle);
		ptsMat.exportToPoints(vertexList);
		refreshModel();
	}

	/**
	 * Applique un rotation sur la figure par l'axe Y <br>
	 * Applique la translation par le centre de la figure d'abord
	 * 
	 * @param angle
	 */
	public void rotateYByPoint(double angle) {
		ptsMat.importPoints(vertexList, 3);
		ptsMat.setHomogeneousCoords();
		ptsMat.rotateY(this, angle);
		ptsMat.exportToPoints(vertexList);
		refreshModel();
	}

	/**
	 * Met la figure à la bonne échelle pour le panel concerné et la centre.
	 * 
	 * @param visPanel le panel concerné
	 * @param zoom le niveau de zoom qu'on veut au départ
	 */
	public void prepareForWindow(VisualisationPanel visPanel, double zoom) {
		if (zoom != 1.0) {
			scale(zoom);
		} else {
			fitFigureToWindow(visPanel, 0.65);
		}
		centrerFigure(visPanel);
		ptsMat.importPoints(vertexList, 3);
		refreshModel();
	}

	/**
	 * Applique un rotation sur la figure par l'axe Z <br>
	 * Applique la translation par le centre de la figure d'abord
	 * 
	 * @param angle
	 */
	public void rotateZByPoint(double angle) {
		ptsMat.importPoints(vertexList, 3);
		ptsMat.setHomogeneousCoords();
		ptsMat.rotateZ(this, angle);
		ptsMat.exportToPoints(vertexList);
		refreshModel();
	}

	public void setProjection(Vecteur lightVector) {
		//	@formatter:off
			double[][] projection = new double[][] { 
				{ 1, 0, 0.0, 0.0 }, 
				{ 0, 1, 0.0, 0.0 }, 
				{ 0.0, 0.0, 0, 0.0 }, 
				{ 0.0, 0.0, 0.0, 1.0 } };
		//	@formatter:on

		final int distance = 100;

		ptsMat.importPoints(vertexList, 3);
		ptsMat.setHomogeneousCoords();

		ombre = new Matrice(Matrice.multiply(projection, ptsMat.getMatrice()));
		ombre.zoom(0.75);
		ombre.translateMatrix(200, 1, distance);

		ombrePolygones.clear();
		for (int i = 0; i < faceList.size(); i++) {
			Path2D ombrePath = new Path2D.Double();
			List<Point> pt = faceList.get(i).getVertexList();
			int pointNumber = pt.get(0).getNumber();
			ombrePath.moveTo(ombre.getMatrice()[0][pointNumber], ombre.getMatrice()[1][pointNumber]);
			for (int j = 1; j < pt.size(); j++) {
				pointNumber = pt.get(j).getNumber();
				ombrePath.lineTo(ombre.getMatrice()[0][pointNumber], ombre.getMatrice()[1][pointNumber]);
			}

			ombrePath.closePath();
			ombrePolygones.add(ombrePath);
		}
	}

	/**
	 * Vide la liste de polygones. Trie les faces selon l'ordre d'apparence et re ajoute les polygones. <br>
	 * Calls {@link FigureModel#notifyObservers()}
	 */
	public void refreshModel() {
		polygones.clear();
		Collections.sort(faceList);
		setProjection(lightVector);

		// FIGURE
		for (int i = 0; i < faceList.size(); i++) {
			Path2D figurePath = new Path2D.Double();
			List<Point> pt = faceList.get(i).getVertexList();
			figurePath.moveTo(pt.get(0).getX(), pt.get(0).getY());
			for (int j = 1; j < pt.size(); j++) {
				figurePath.lineTo(pt.get(j).getX(), pt.get(j).getY());
			}
			figurePath.closePath();
			polygones.add(figurePath);
		}

		setChanged();
		notifyObservers();
	}

	/**
	 * Appelle {@link #readFile()} pour réinitialiser les listes de vertexList et faces d'après le fichier .ply d'origine.
	 */
	public void resetModel() {
		// TODO redo
		// readFile();
		// if (getErreurLecture()) {
		// String message = "Un erreur a été rencontré lors de la relecture du fichier .ply";
		// JOptionPane.showMessageDialog(null, message, "Erreur de lecture", JOptionPane.ERROR_MESSAGE);
		// System.exit(1);
		// } else {
		// refreshModel();
		// }
	}

}
