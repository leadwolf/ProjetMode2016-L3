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
import math.Vecteur;
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
	private int heightWindow;
	private int widthWindow;
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

		Stroke defaultStroke2 =  new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		final float dash1[] = { 7.0f };
		final Stroke dottedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
		
		/*
		 * On met les segments et faces dans la meme boucle pour qu'on les dessine dans le meme ordre de leur moyenne de Z
		 */
		int i=0;
		for (Path2D p : figure.getPolygones()) {
			if (drawSegments) {
				g.setStroke(defaultStroke2);
				g.setColor(Color.BLACK);
				g.draw(p);
			}
			if (drawFaces) {
				Vecteur lightVector = new Vecteur(new double[]{0, 0, -1});
				double greyScale = Calculations.getGreyScale(figure, figure.getFacesTrans().get(i), lightVector);
				g.setColor(new Color((float) greyScale, (float) greyScale, (float) greyScale));
				g.fill(p);
				
			}
			i++;
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
	public void setTempDimensions(Dimension dim) {
		heightWindow = dim.height;
		widthWindow = dim.width;
	}

	public void setFigure(Figure figure, double zoom) {
		this.figure = figure;
		if (zoom != 1.0) {
			zoom(zoom);
		} else {
			Calculations.fitFigureToWindow(this, 0.65);
		}
		Calculations.centrerFigure(this);
		figure.getPtsMat().importPoints(figure.getPtsTrans(), 3);
		refreshObject();
	}

	public Figure getFigure() {
		return figure;
	}
	

	public int getHeightWindow() {
		return heightWindow;
	}

	public int getWidthWindow() {
		return widthWindow;
	}

	/**
	 * Sauvegarde les polygones à dessiner grâce aux points de {@link Figure#getFacesTrans()}
	 * <br>En clair, {@link Figure#getPolygones()} contient les vrais formes que Graphics peut dessiner.
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
	 * Vide le container Path2D de {@link Figure#getPolygones()} pour le ré-remplir avec les
	 * nouveaux points tranformés Sinon on afficherait encore les vieux points
	 * en plus des nouveaux points transformés.
	 * <br>Tri aussi les faces dans l'ordre d'appartition (arrière -> devant)
	 */
	public void refreshObject() {
		figure.getPolygones().clear();
		Collections.sort(figure.getFacesTrans());
		setPolyGones();
	}


}