import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/**
 * Cette classe sert à afficher l'objet ply.
 * 
 * @author Groupe L3
 *
 */
@SuppressWarnings("unused")
public class Panneau extends JPanel {

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
	private Mouse mouse = new Mouse();
	private double rotationSens = 5;
	private double zoomSens = 0.1;

	public Panneau(boolean drawPoints, boolean drawSegments, boolean drawFaces) {
		this.drawPoints = drawPoints;
		this.drawSegments = drawSegments;
		this.drawFaces = drawFaces;
		this.addMouseWheelListener(mouse);
		this.addMouseListener(mouse);
		this.addMouseMotionListener(mouse);
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

		if (drawPoints) {
			g.setColor(Color.PINK);
			for (Point pt : figure.getPtsTrans()) {
				double x = pt.x - ptsDim.getWidth() / 2;
				double y = pt.y - ptsDim.getHeight() / 2;
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
		centrerFigure();
		if (zoom != 1.0) {
			zoom(zoom);
		} else {
		//	applyDefaultZoom();
		}
		figure.getPtsMat().importPoints(figure.getPtsTrans());
		setPolyGones();
	}

	/**
	 * Actualise les dimensions de la figure
	 */
	private void refreshFigDims() {
		// set all values to opposites of what they should be because of comparisons in if
		widthFig = heightFig = right = bottom = 0;
		left = width;
		top = height;
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

	/**
	 * Centre la figure si on centre dépasse les axes du centre
	 */
	private void centrerFigure() {
		refreshFigDims();
		// left of center
		if (figure.getCenter().getX() < width/2) {
			if (figure.getCenter().getX() < 0) {
				for (Point p : figure.getPtsTrans()) {
					p.setX(p.getX() + Math.abs(figure.getCenter().getX()) + (width/2));
				}
			} else {
				for (Point p : figure.getPtsTrans()) {
					p.setX(p.getX() + ((width/2) - Math.abs(figure.getCenter().getX())));
				}
			}
		} else {
			for (Point p : figure.getPtsTrans()) {
				p.setX(p.getX() - (figure.getCenter().getX() - (width/2)));
			}
		}
		// above center
		if (figure.getCenter().getY() < height/2) {
			for (Point p : figure.getPtsTrans()) {
				p.setY(p.getY() + ((height/2) - figure.getCenter().getY()));
			}
		} else {
			for (Point p : figure.getPtsTrans()) {
				p.setY(p.getY() - (figure.getCenter().getY() - (height/2)));
			}
		}
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
	 * Applique un "zoom" en écartant les points <b>ptsTrans</b> par rapport à
	 * l'origine (0,0,0) <br>
	 * ATTENTION : si on applique des zooms en chaîne, l'effet sera de plus en
	 * plus fort car meme si le niveau de zoom reste constant, on l'applique à
	 * un objet de plus en plus grand
	 * 
	 * @param zoomLevel
	 *            le niveau de zoom à appliquer
	 */
	private void zoom(double zoomLevel) {
		for (Point pt : figure.getPtsTrans()) {
			pt.setX(pt.getX() * zoomLevel);
			pt.setY(pt.getY() * zoomLevel);
			pt.setZ(pt.getZ() * zoomLevel);
		}
	}

	/**
	 * Applique un zoom permettant à la figure de prendre 65% de l'écran
	 */
	private void applyDefaultZoom() {
		double zoomLevel = 1.0;
		if (widthFig > width || heightFig > height) {
			// reduce
			while (widthFig > 0.65 * width || heightFig > 0.65 * height && zoomLevel > 0) {
				zoomLevel = 0.995;
				refreshFigDims();
				zoom(zoomLevel);
			}
		} else {
			// enlarge
			while (widthFig < 0.65 * width && heightFig < 0.65 * height) {
				zoomLevel = 1.005;
				refreshFigDims();
				zoom(zoomLevel);
			}
		}
	}

	private class Mouse extends MouseAdapter {
		int transX, transY;
		int rotX, rotY;
		double zoom = 0.0;
		int zoomTransSens = 20; // sensitivity of translation to mousepoint when zooming
		int notches;
		int nextX, nextY;
		double totalY = 0.0;
		public void mouseWheelMoved(MouseWheelEvent e) {
			/**
			 * Zoom into mouse cursor
			 * = moving the center of figure nearer to the mouse cursor
			 */
			refreshFigDims();
			int moveX = (width/2) - e.getX();
			int moveY = (height/2) - e.getY();
			if (e.getWheelRotation() > 0) {
				Calculations.translatePoints(figure.getPtsTrans(), -moveX/zoomTransSens, -moveY/zoomTransSens);
			} else {
				Calculations.translatePoints(figure.getPtsTrans(), moveX/zoomTransSens, moveY/zoomTransSens);
			}
			notches = e.getWheelRotation() * -1;
			zoom = 1.0 + (zoomSens * notches);
			zoom(zoom);
			refreshObject();
			repaint();
			/* End Zoom */
		}

		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				transX = e.getX();
				transY = e.getY();
			}
			if (SwingUtilities.isRightMouseButton(e)) {
				rotX = e.getX();
				rotY = e.getY();
			}
			refreshFigDims();
		}
				
		public void mouseDragged(MouseEvent e) {
			/* Translate Figure */
			if (SwingUtilities.isLeftMouseButton(e)) {
				nextX = e.getX();
				nextY = e.getY();
				Calculations.translatePoints(figure.getPtsTrans(), (transX - nextX) * -1, (transY - nextY) * -1);
				refreshObject();
				repaint();
				transX = nextX;
				transY = nextY;
			}
			/* End Translate Figure */
			
			/* Rotate Figure */
			if (SwingUtilities.isRightMouseButton(e)) {
				nextX = e.getX();
				nextY = e.getY();
				if (Math.abs(nextY - rotY) > Math.abs(nextX - rotX)) {
					// rotation autour de l'axe X entend un mouvement haut/bas donc Y
					if (nextY > rotY) {
						Calculations.translatePoints(figure.getPtsTrans(), -figure.getCenter().getX(), -figure.getCenter().getY());
						Calculations.rotateX(figure, rotationSens);
						Calculations.translatePoints(figure.getPtsTrans(), figure.getCenter().getX(), figure.getCenter().getY());
					} else {
						Calculations.translatePoints(figure.getPtsTrans(), -figure.getCenter().getX(), -figure.getCenter().getY());
						Calculations.rotateX(figure, -rotationSens);
						Calculations.translatePoints(figure.getPtsTrans(), figure.getCenter().getX(), figure.getCenter().getY());
					}
				} else {
					if (nextX > rotX) {
						Calculations.translatePoints(figure.getPtsTrans(), -figure.getCenter().getX(), -figure.getCenter().getY());
						Calculations.rotateY(figure, rotationSens);
						Calculations.translatePoints(figure.getPtsTrans(), figure.getCenter().getX(), figure.getCenter().getY());
					} else {
						Calculations.translatePoints(figure.getPtsTrans(), -figure.getCenter().getX(), -figure.getCenter().getY());
						Calculations.rotateY(figure, -rotationSens);
						Calculations.translatePoints(figure.getPtsTrans(), figure.getCenter().getX(), figure.getCenter().getY());
					}
				}
								
				refreshObject();
				repaint();
				rotX = nextX;
				rotY = nextY;
			}
			/* End Rotate Figure */
		}
	}

	/**
	 * Vide le container Path2D de polygone pour le ré-remplir avec les
	 * nouveaux points tranformés Sinon on afficherait encore les vieux points
	 * en plus des nouveaux points transformés
	 */
	private void refreshObject() {
		figure.getPolygones().clear();
		Collections.sort(figure.getFacesTrans());
		setPolyGones();
	}


}