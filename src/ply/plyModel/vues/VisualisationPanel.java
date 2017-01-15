package ply.plyModel.vues;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import ply.math.Vecteur;
import ply.plyModel.modeles.FigureModel;
import ply.plyModel.other.Point;

/**
 * Cette classe sert à afficher l'objet ply.
 * 
 * @author Groupe L3
 *
 */
public class VisualisationPanel extends JPanel implements Observer {

	private static final long serialVersionUID = 6617022758741368018L;

	private FigureModel figureModel;
	private Dimension ptsDim = new Dimension(7, 7);
	private boolean drawPoints = true;
	private boolean drawSegments = true;
	private boolean drawFaces = true;
	private boolean directionalLight = true;
	private int heightWindow;
	private int widthWindow;

	public VisualisationPanel(FigureModel figureModel, boolean drawPoints, boolean drawSegments, boolean drawFaces) {
		super();

		this.drawPoints = drawPoints;
		this.drawSegments = drawSegments;
		this.drawFaces = drawFaces;

		this.figureModel = figureModel;
		if (figureModel != null) {
			figureModel.addObserver(this);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawBackground((Graphics2D) g);
		drawFigure((Graphics2D) g);
	}

	private void drawBackground(Graphics2D g) {
		int drawWidth = 0;
		int drawHeight= 0;
		int rectDim = 100;
		boolean grey = true;
		boolean firstGrey = true;
		
		while (drawHeight < getHeight()) {
			while (drawWidth < getWidth()) {
				g.setColor(Color.BLACK);
//				g.drawString("Chris", drawWidth, drawHeight);
				if (grey) {
					g.setColor(Color.LIGHT_GRAY);
				} else {
					g.setColor(Color.WHITE);
				}
				g.fillRect(drawWidth, drawHeight, rectDim, rectDim);
				drawWidth += 100;
				grey = !grey;
			}
			if (firstGrey) {
				grey = false;
				firstGrey = false;
			} else {
				grey = true;
				firstGrey = true;
			}
			drawWidth = 0;
			drawHeight += 100;
		}
	}
	
	/**
	 * Dessine la figure et son ombre avec g
	 * 
	 * @param g
	 */
	private void drawFigure(Graphics2D g) {
		/*
		 * On met les segments et faces dans la meme boucle pour qu'on les dessine dans le meme ordre de leur moyenne de
		 * Z
		 */
		if (drawSegments || drawFaces) {

			Stroke defaultStroke = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

			// OMBRE
			g.setColor(Color.BLACK);
			for (Path2D p : figureModel.getOmbrePolygones()) {
				if (drawSegments && !drawFaces) {
					g.draw(p);
				} else {
					g.fill(p);
				}
			}

			int i = 0;

			// SEGMENTS
			for (Path2D p : figureModel.getPolygones()) {
				if (drawSegments) {
					g.setStroke(defaultStroke);
					if (drawSegments && !drawFaces) {
						g.setColor(Color.GRAY);
					} else {
						g.setColor(Color.BLACK);
					}
					g.draw(p);
				}

				// FACES
				if (drawFaces) {
					if (directionalLight) {
						double greyScale =
								FigureModel.getGreyScale(figureModel.getFaces().get(i), figureModel.getLightVector());
						g.setColor(new Color((float) greyScale, (float) greyScale, (float) greyScale));
					} else {
						g.setColor(Color.GRAY);
					}
					g.fill(p);

				}
				i++;
			}
		}

		if (drawPoints) {
			g.setColor(Color.GRAY);
			for (Point pt : figureModel.getPoints()) {
				double x = pt.getX() - (ptsDim.getWidth() / 2);
				double y = pt.getY() - (ptsDim.getHeight() / 2);
				Ellipse2D.Double shape = new Ellipse2D.Double(x, y, ptsDim.getWidth(), ptsDim.getHeight());
				g.fill(shape);
			}
		}

		// CENTER
		// g.setColor(Color.RED);
		// double xCenter = figure.getCenter().getX() - (ptsDim.getWidth() / 2);
		// double yCenter = figure.getCenter().getY() - (ptsDim.getHeight() /
		// 2);
		// Ellipse2D.Double shapeCenter = new Ellipse2D.Double(xCenter, yCenter,
		// ptsDim.getWidth(), ptsDim.getHeight());
		// g.fill(shapeCenter);
	}

	public void setDirectionalLight(boolean directionalLight) {
		this.directionalLight = directionalLight;
	}

	public void setDrawSegments(boolean drawSegments) {
		this.drawSegments = drawSegments;
	}

	public void setDrawFaces(boolean drawFaces) {
		this.drawFaces = drawFaces;
	}

	public void setDrawPoints(boolean drawPoints) {
		this.drawPoints = drawPoints;
	}

	public boolean isDrawPoints() {
		return this.drawPoints;
	}

	public boolean isDrawSegments() {
		return this.drawSegments;
	}

	public boolean isDrawFaces() {
		return this.drawFaces;
	}

	public boolean isDirectionalLight() {
		return directionalLight;
	}

	/**
	 * Applique les dimensions du JPanel pour les calculs
	 * 
	 * @param dim
	 */
	public void setTempDimensions(Dimension dim) {
		heightWindow = dim.height;
		widthWindow = dim.width;
	}

	public FigureModel getFigure() {
		return figureModel;
	}

	/**
	 * Donne la hauteur de ce panel lors de sa création. Nécessaire pour centrer la figure avant que le panel apparaisse
	 * car sinon getHeight() retourne 0.
	 * 
	 * @return la hauteur initiale de panel.
	 */
	public int getHeightWindow() {
		return heightWindow;
	}

	/**
	 * Donne la largeur de ce panel lors de sa création. Nécessaire pour centrer la figure avant que le panel apparaisse
	 * car sinon getHeight() retourne 0.
	 * 
	 * @return la largeur initiale de panel.
	 */
	public int getWidthWindow() {
		return widthWindow;
	}

	/**
	 * Appelle ce panel pour se mettre à jour par rapport au modèle qui a évolué
	 * 
	 * @param o
	 * @param arg
	 */
	@Override
	public void update(Observable o, Object arg) {
		repaint();
	}

}