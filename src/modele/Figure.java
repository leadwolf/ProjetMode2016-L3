package modele;
import java.awt.geom.Path2D;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import math.Matrice;
import reader.Lecture;

public class Figure {

	private int nbPoints;
	private int nbFaces;
	private List<Point> points;
	private List<Face> faces;
	private List<Point> ptsTrans;
	private List<Face> facesTrans;
	private List<Path2D> polygones;
	private Point center;
	private Lecture lecture;
	private Matrice ptsMat;
	
	
	public List<Point> getPtsTrans() {
		return ptsTrans;
	}

	public List<Face> getFacesTrans() {
		return facesTrans;
	}
	
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

	public Figure(Path file) {
		lecture = new Lecture(file);
		nbPoints = lecture.getNbPoints();
		nbFaces = lecture.getNbFaces();
		points = lecture.getPoints();
		invertPoints();
		faces = lecture.getFaces();
		ptsTrans = new ArrayList<>(points);
		facesTrans = new ArrayList<>(faces);
		polygones = new ArrayList<>();
		center = new Point();
		ptsMat = new Matrice(ptsTrans.size(), 4);
		ptsMat.setHomogeneousCoords();
	}
	
	public Point getCenter() {
		return center;
	}

	public boolean getErreurLecture() {
		if (lecture != null) {
			return lecture.isErreur();
		} else {
			return true;
		}
	}
	
	/**
	 * Inverse la figure par rapport à l'axe X car on dessine du haut en bas
	 */
	public void invertPoints() {
		for (Point pt : points) {
			pt.setY(pt.getY() * -1);
		}
	}


}
