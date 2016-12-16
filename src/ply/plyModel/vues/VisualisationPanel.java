package ply.plyModel.vues;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import math.Calculations;
import math.Vecteur;
import ply.plyModel.modeles.Face;
import ply.plyModel.modeles.FigureModel;
import ply.plyModel.modeles.Point;


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
	public void paintComponent(Graphics gg) {
		super.paintComponent(gg);
		Graphics2D g = (Graphics2D) gg;
		
		/*
		 * On met les segments et faces dans la meme boucle pour qu'on les dessine dans le meme ordre de leur moyenne de Z
		 */
		if (figureModel != null && (drawSegments || drawFaces)) {

			Stroke defaultStroke2 =  new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
			final float dash1[] = { 7.0f };
			final Stroke dottedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
			
			int i=0;
			for (Path2D p : figureModel.getPolygones()) {
				if (drawSegments) {
					g.setStroke(defaultStroke2);
					g.setColor(Color.BLACK);
					g.draw(p);
				}
				if (drawFaces) {
					Vecteur lightVector = new Vecteur(new double[]{0, 0, -1});
					if (directionalLight) {
						double greyScale = FigureModel.getGreyScale(figureModel.getFaces().get(i), lightVector);
						g.setColor(new Color((float) greyScale, (float) greyScale, (float) greyScale));
					} else {
						g.setColor(Color.GRAY);
					}
					g.fill(p);
					
				}
				i++;
			}
		}
		
//		g.setColor(Color.RED);
//		double xCenter = figure.getCenter().getX() - (ptsDim.getWidth() / 2);
//		double yCenter = figure.getCenter().getY() - (ptsDim.getHeight() / 2);
//		Ellipse2D.Double shapeCenter = new Ellipse2D.Double(xCenter, yCenter, ptsDim.getWidth(), ptsDim.getHeight());
//		g.fill(shapeCenter);
		

		if (figureModel != null && drawPoints) {
			g.setColor(Color.GRAY);
			for (Point pt : figureModel.getPoints()) {
				double x = pt.getX() - (ptsDim.getWidth() / 2);
				double y = pt.getY() - (ptsDim.getHeight() / 2);
				Ellipse2D.Double shape = new Ellipse2D.Double(x, y, ptsDim.getWidth(), ptsDim.getHeight());
				g.fill(shape);
			}
		}
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
	 * Donne la hauteur de ce panel lors de sa création. Nécessaire pour centrer la figure avant que le panel apparaisse car sinon getHeight() retourne 0.
	 * @return la hauteur initiale de panel.
	 */
	public int getHeightWindow() {
		return heightWindow;
	}
	
	/**
	 * Donne la largeur de ce panel lors de sa création. Nécessaire pour centrer la figure avant que le panel apparaisse car sinon getHeight() retourne 0.
	 * @return la largeur initiale de panel.
	 */
	public int getWidthWindow() {
		return widthWindow;
	}

	/**
	 * Appelle ce panel pour se mettre à jour par rapport au modèle qui a évolué
	 * @param o
	 * @param arg
	 */
	@Override
	public void update(Observable o, Object arg) {
		repaint();
	}


}