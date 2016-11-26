package modele;
import java.awt.geom.Path2D;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import erreur.BasicResultEnum;
import math.Matrice;
import reader.Lecture;

public class Figure {

	private Path path;
	private int nbPoints;
	private int nbFaces;
	private List<Point> points;
	private List<Face> faces;
	private List<Path2D> polygones;
	private Point center;
	private Lecture lecture;
	private Matrice ptsMat;
	private double heightFig, widthFig;
	
	
	public Matrice getPtsMat() {
		return ptsMat;
	}

	public List<Path2D> getPolygones() {
		return polygones;
	}

	public int getNbPoints() {
		return nbPoints;
	}

	public int getNbFaces() {
		return nbFaces;
	}

	public List<Point> getPoints() {
		return points;
	}

	public List<Face> getFaces() {
		return faces;
	}
		
	public double getHeightFig() {
		return heightFig;
	}

	public void setHeightFig(double heightFig) {
		this.heightFig = heightFig;
	}

	public double getWidthFig() {
		return widthFig;
	}

	public void setWidthFig(double widthFig) {
		this.widthFig = widthFig;
	}

	/**
	 * Cree une figure
	 * @param file le <b>Path</b> de l'objet .ply
	 * @param noPrint si on veut empêcher les System.out.println
	 */
	public Figure(Path file, boolean noPrint) {
		this.path = file;
		lecture = new Lecture(file, noPrint);
		nbPoints = lecture.getNbPoints();
		nbFaces = lecture.getNbFaces();
		points = lecture.getPoints();
		invertPoints();
		faces = lecture.getFaces();
		polygones = new ArrayList<>();
		center = new Point();
		ptsMat = new Matrice(points.size(), 4);
		ptsMat.setHomogeneousCoords();
	}
	
	
	public Path getPath() {
		return path;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	public void setFaces(List<Face> faces) {
		this.faces = faces;
	}

	public Lecture getLecture() {
		return lecture;
	}

	public Point getCenter() {
		return center;
	}

	public boolean getErreurLecture() {
		if (lecture != null) {
			return lecture.getResult().equals(BasicResultEnum.ALL_OK);
		} else {
			return true;
		}
	}
	
	/**
	 * Inverse la figure par rapport à  l'axe X car on dessine du haut en bas
	 */
	public void invertPoints() {
		for (Point pt : points) {
			pt.setY(pt.getY() * -1);
		}
	}


}
