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
	private double widthFig = 0, heightFig = 0;
	private double left = 0, right = 0, top = 0, bottom = 0;
	private Mouse mouse = new Mouse();
	double sensitivity = 0.04;

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

		int centerX = height / 2;
		int centerY = width / 2;
		
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
				double x = centerX + pt.x - ptsDim.getWidth() / 2;
				double y = centerY + pt.y - ptsDim.getHeight() / 2;
				Ellipse2D.Double shape = new Ellipse2D.Double(x, y, ptsDim.getWidth(), ptsDim.getHeight());
				g.fill(shape);
			}
		}
	}

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
			applyDefaultZoom();
		}
		figure.getPtsMat().importPoints(figure.getPtsTrans());
		setPolyGones();
		numPremFace = Calculations.determineFrontFace(figure.getFaces());
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
			if (p.getX() + (width / 2) < left) {
				left = p.getX() + (width / 2);
			}
			if (p.getX() + (width / 2) > right) {
				right = p.getX() + (width / 2);
			}
			if (p.getY() + (height / 2) > bottom) {
				bottom = p.getY() + (height / 2);
			} else if (p.getY() + (height / 2) < top) {
				top = p.getY() + (height / 2);
			}
		}
		widthFig = right - left;
		heightFig = bottom - top;
		figure.getCenter().setCoords(left + (widthFig/2), top + (heightFig/2));
	}

	/**
	 * Centre la figure si on centre dépasse les axes du centre
	 */
	private void centrerFigure() {
		refreshFigDims();
		// left of center
		if (figure.getCenter().getX() < width/2) {
			for (Point p : figure.getPtsTrans()) {
				p.setX(p.getX() + ((width/2) - figure.getCenter().getX()));
			}
		} else {
			for (Point p : figure.getPtsTrans()) {
				p.setX(p.getX() - (figure.getCenter().getX() - (width/2)));
			}
			refreshFigDims();
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
			path.moveTo((width / 2) + pt.get(0).getX(), (height / 2) + pt.get(0).getY());
			for (int j = 1; j < pt.size(); j++) {
				path.lineTo((width / 2) + pt.get(j).getX(), (height / 2) + pt.get(j).getY());
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

		public void mouseWheelMoved(MouseWheelEvent e) {
			/**
			 * Zoom into mouse cursor
			 * = moving the center nearer to the mouse cursor
			 */
			double zoom = 0.0;
			refreshFigDims();
			int moveX = (int) figure.getCenter().getX() - e.getX();
			int moveY = (int) figure.getCenter().getY() - e.getY();
			if (e.getWheelRotation() > 0) {
				Calculations.translateFigure(figure.getPtsTrans(), -moveX/20, -moveY/20);
			} else {
				Calculations.translateFigure(figure.getPtsTrans(), moveX/20, moveY/20);
			}
			int notches = e.getWheelRotation() * -1;
			zoom = 1.0 + (0.05 * notches);
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
		}

		public void mouseDragged(MouseEvent e) {
			/* Translate Figure */
			if (SwingUtilities.isLeftMouseButton(e)) {
				int nextX, nextY;
				nextX = e.getX();
				nextY = e.getY();
				Calculations.translateFigure(figure.getPtsTrans(), (transX - nextX) * -1, (transY - nextY) * -1);
				refreshObject();
				repaint();
				transX = nextX;
				transY = nextY;
			}
			/* End Translate Figure */
			
			/* Rotate Figure */
			if (SwingUtilities.isRightMouseButton(e)) {
				int nextX, nextY;
				nextX = e.getX();
				nextY = e.getY();
				
				figure.setPtsMat(new Matrice(figure.getPtsTrans().size(), 3));
				figure.getPtsMat().importPoints(figure.getPtsTrans());
				if (Math.abs(nextY - rotY) > Math.abs(nextX - rotX)) {
					if (nextY > rotY) {
						figure.getPtsMat().rotateX( sensitivity );
					} else {
						figure.getPtsMat().rotateX( -sensitivity );
					}
				} else {
					if (nextX > rotX) {
						figure.getPtsMat().rotateY( sensitivity );
					} else {
						figure.getPtsMat().rotateY( -sensitivity );
					}
				}
				figure.getPtsMat().exportToPoints(figure.getPtsTrans());
								
				refreshObject();
				repaint();
				rotX = nextX;
				rotY = nextY;
			}
			/* End Rotate Figure */
		}
	}

	/**
	 * Vide les containers (facesTrans et polygones) pour le ré-remplir avec les
	 * nouveaux points tranformés Sinon on afficherait encore les vieux points
	 * en plus des nouveaux points transformés
	 */
	private void refreshObject() {
		figure.getPolygones().clear();
		Collections.sort(figure.getFacesTrans());
		setPolyGones();
	}
	
	private void rotateX(double angle) {
		figure.setPtsMat(new Matrice(figure.getPtsTrans().size(), 3));
		figure.getPtsMat().importPoints(figure.getPtsTrans());
		figure.getPtsMat().rotateX(angle);
		figure.getPtsMat().exportToPoints(figure.getPtsTrans());
	}
	
	private void rotateY(double angle) {
		figure.setPtsMat(new Matrice(figure.getPtsTrans().size(), 3));
		figure.getPtsMat().importPoints(figure.getPtsTrans());
		figure.getPtsMat().rotateY(angle);
		figure.getPtsMat().exportToPoints(figure.getPtsTrans());
	}
	
	private void rotateZ(double angle) {
		figure.setPtsMat(new Matrice(figure.getPtsTrans().size(), 3));
		figure.getPtsMat().importPoints(figure.getPtsTrans());
		figure.getPtsMat().rotateZ(angle);
		figure.getPtsMat().exportToPoints(figure.getPtsTrans());
	}

}