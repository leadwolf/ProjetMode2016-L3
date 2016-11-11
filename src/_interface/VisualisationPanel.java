package _interface;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import math.Calculations;
import modele.Figure;
import modele.Point;


/**
 * Cette classe sert à afficher l'objet ply.
 * 
 * @author Groupe L3
 *
 */
public class VisualisationPanel extends JPanel {

	private static final long serialVersionUID = 6617022758741368018L;

	private Figure figure;
	private Dimension ptsDim = new Dimension(7, 7);
	private boolean drawPoints = true;
	private boolean drawSegments = true;
	private boolean drawFaces = true;
	private int numPremFace = 0;
	private int height;
	private int width;
	private double widthFig = 0, heightFig = 0, depthFig = 0;
	private double left = 0, right = 0, top = 0, bottom = 0, front = 0, back = 0;
	private MouseControler mouseControler;

	public VisualisationPanel(boolean drawPoints, boolean drawSegments, boolean drawFaces) {
		super();
		
		this.drawPoints = drawPoints;
		this.drawSegments = drawSegments;
		this.drawFaces = drawFaces;
		
		mouseControler = new MouseControler(this);
		this.addMouseWheelListener(mouseControler);
		this.addMouseListener(mouseControler);
		this.addMouseMotionListener(mouseControler);
	}

	public void paintComponent(Graphics gg) {
		super.paintComponent(gg);
		Graphics2D g = (Graphics2D) gg;

		Stroke defaultStroke = new BasicStroke(2);
		final float dash1[] = { 7.0f };
		final Stroke dottedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
		
		/*
		 * On met les segments et faces dans la meme boucle pour qu'on les dessine dans le meme ordre de leur moyenne de Z
		 */
		for (Path2D p : figure.getPolygones()) {
			if (drawSegments) {
				g.setStroke(defaultStroke);
				g.setColor(Color.BLACK);
				g.draw(p);
			}
			if (drawFaces) {
				g.setColor(Color.GRAY);
				g.fill(p);
				
			}
		}
		
//		g.setColor(Color.RED);
//		double xCenter = figure.getCenter().getX() - (ptsDim.getWidth() / 2);
//		double yCenter = figure.getCenter().getY() - (ptsDim.getHeight() / 2);
//		Ellipse2D.Double shapeCenter = new Ellipse2D.Double(xCenter, yCenter, ptsDim.getWidth(), ptsDim.getHeight());
//		g.fill(shapeCenter);
		

		if (drawPoints) {
			g.setColor(Color.PINK);
			for (Point pt : figure.getPtsTrans()) {
				double x = pt.getX() - (ptsDim.getWidth() / 2);
				double y = pt.getY() - (ptsDim.getHeight() / 2);
				Ellipse2D.Double shape = new Ellipse2D.Double(x, y, ptsDim.getWidth(), ptsDim.getHeight());
				g.fill(shape);
			}
		}
	}

	/**
	 * Applique les dimensions du JPanel pour les calculs
	 * @param dim
	 */
	public void setDimensions(Dimension dim) {
		height = dim.height;
		width = dim.width;
	}

	public void setFigure(Figure figure, double zoom) {
		this.figure = figure;
		if (zoom != 1.0) {
			zoom(zoom);
		} else {
			fitFigureToWindow(0.65);
		}
		centrerFigure();
		figure.getPtsMat().importPoints(figure.getPtsTrans());
		refreshObject();
	}

	/**
	 * Actualise les dimensions de la figure
	 */
	public void refreshFigDims() {
		// set all values to opposites of what they should be because of comparisons in if
		widthFig = heightFig = right = bottom = 0;
		left = width;
		top = height;
		back = width;
		front = -width;
		// w/2 or h/2 because all points are set to center when drawn, see setPolygones()
		for (Point p : figure.getPtsTrans()) {
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
		figure.getCenter().setCoords(left + (widthFig/2), top + (heightFig/2), back + (depthFig/2)); // ajout pour donner vrai coord dessiné
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public Figure getFigure() {
		return figure;
	}

	/**
	 * Centre la figure si on centre dépasse les axes du centre
	 */
	public void centrerFigure() {
		refreshFigDims();
		double moveX = figure.getCenter().getX()-(width/2);
		double moveY = figure.getCenter().getY()-(height/2);
		Calculations.translatePoints(figure, -moveX, -moveY, 0);
	}

	/**
	 * Sauvegarde les polygones à dessiner grâce aux points de <b>faceTrans</b>
	 * <br>
	 * En clair, polygones contient les vrais formes que Graphics peut dessiner,
	 * voir {@link #paintComponent(Graphics)} Les faces sont alors représentés
	 * par les polygones remplis. Les listes faces ne servent simplement qu'à
	 * contenir la liste de points.
	 */
	private void setPolyGones() {
		for (int i = 0; i < figure.getFacesTrans().size(); i++) {
			Path2D path = new Path2D.Double();
			figure.getPolygones().add(path);
			List<Point> pt = figure.getFacesTrans().get(i).getList();
			path.moveTo(pt.get(0).getX(), pt.get(0).getY());
			for (int j = 1; j < pt.size(); j++) {
				path.lineTo(pt.get(j).getX(),pt.get(j).getY());
			}
			path.closePath();
		}
	}

	
	/**
	 * Applique un zoom en utilisant une matrice
	 * 
	 * @param scaleFactor le niveau de zoom à appliquer
	 */
	public void zoom(double scaleFactor) {
		Calculations.scale(figure, scaleFactor);
	}

	/**
	 * Applique une homothétie pour que la plus grande dimensions
	 * (largeur ou longueur) de la figure prend <b>maxSize</b> de l'écran
	 * @param maxSize
	 */
	private void fitFigureToWindow(double maxSize) {
		// scale by height
		refreshFigDims();
		double scale = 1.0;
		if (heightFig > widthFig) {
			scale = (height * maxSize) / heightFig;
		} else { // scale by width
			scale = (width * maxSize) / widthFig;
		}
		Calculations.scale(figure, scale);
	}

	/**
	 * Vide le container Path2D de polygone pour le ré-remplir avec les
	 * nouveaux points tranformés Sinon on afficherait encore les vieux points
	 * en plus des nouveaux points transformés
	 */
	public void refreshObject() {
		figure.getPolygones().clear();
		Collections.sort(figure.getFacesTrans());
		setPolyGones();
	}


}