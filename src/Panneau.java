import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * Cette classe sert à afficher l'objet ply.
 * 
 * @author Groupe L3
 *
 */
@SuppressWarnings("unused")
public class Panneau extends JPanel {

	private static final long serialVersionUID = 6617022758741368018L;
	private List<Point> points = new ArrayList<>();
	private List<Point> ptsTrans = new ArrayList<>();
	private List<Face> faces = new ArrayList<>();
	private List<Face> facesTrans = new ArrayList<>();
	private List<Segment> segments = new ArrayList<>(); // Inutile?
	private List<Path2D> polygones = new ArrayList<>();
	private Dimension ptsDim = new Dimension(7, 7);
	private boolean drawPoints = true;
	private boolean drawSegments = true;
	private boolean drawFaces = true;
	private int numPremFace = 0;
	private int height;
	private int width;
	private double widthFig = 0, heightFig = 0;
	private double left = width, right = 0, top = height, bottom = 0;
	private Mouse mouse = new Mouse();
	private double zoom = 1.0;

	public Panneau(boolean drawPoints, boolean drawSegments, boolean drawFaces) {
		this.drawPoints = drawPoints;
		this.drawSegments = drawSegments;
		this.drawFaces = drawFaces;
		this.addMouseWheelListener(mouse);
		this.addMouseListener(mouse);
		this.addMouseMotionListener(mouse);
	}

	public void paintComponent(Graphics gg) {

		Stroke defaultStroke = new BasicStroke(1);
		final float dash1[] = { 7.0f };
		final Stroke dottedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
				dash1, 0.0f);
		
		super.paintComponent(gg);
		Graphics2D g = (Graphics2D) gg;

		int centerX = height / 2;
		int centerY = width / 2;
		if (drawFaces) {
			g.setColor(Color.GRAY);
			for (Path2D pa : polygones) {
				g.setStroke(new BasicStroke(2));
				g.fill(pa);
			}
		}

		if (drawSegments) {
			g.setColor(Color.BLACK);
			g.setStroke(defaultStroke);
			for (Path2D p : polygones) {
				g.draw(p);
			}
			/*
			 * A refaire : si une partie de face est couvert par celui de
			 * devant, et qu'une autre partie est quand meme visible, ces
			 * segments sont toujours en pointillés. => A faire sur une base de
			 * segment caché par face au lieu de face caché par face
			 * 
			 * try { g.setColor(Color.BLACK); Area front = new
			 * Area(polygones.get(numPremFace).getBounds2D()); for (int
			 * i=0;i<polygones.size();i++) { Area current = new
			 * Area(polygones.get(i).getBounds2D()); if
			 * (!front.intersects(current.getBounds2D())) {
			 * g.setStroke(defaultStroke); } else { if (i != numPremFace) {
			 * g.setStroke(dottedStroke); } } g.draw(polygones.get(i)); }
			 * g.setStroke(defaultStroke); g.draw(polygones.get(numPremFace)); }
			 * catch (Exception e) { // polygones n'ont pas encore été
			 * initialisés }
			 */
		}

		if (drawPoints) {
			g.setColor(Color.PINK);
			for (Point pt : ptsTrans) {
				double x = centerX + pt.x - ptsDim.getWidth() / 2;
				double y = centerY + pt.y - ptsDim.getHeight() / 2;
				Ellipse2D.Double shape = new Ellipse2D.Double(x, y, ptsDim.getWidth(), ptsDim.getHeight());
				// System.out.println("drawing point =" + shape.x + " " +
				// shape.y);
				g.fill(shape);
			}
		}
	}

	public void setDimensions(Dimension dim) {
		height = dim.height;
		width = dim.width;
	}

	/**
	 * Sauvegarde les points de l'objet et les transforme pour futur utilisation
	 * dans la modélisation, voir {@link #transformePoints(List, List)}
	 * 
	 * @param points
	 *            la liste de points de l'objet récupérés par {@link Lecture}
	 * @param zoomLevel
	 *            le niveau de zoom à appliqer à l'objet. La valeur 1.0 ne
	 *            modifie rien.
	 */
	public void setPoints(List<Point> points, double zoomLevel) {
		this.points = points;
		this.zoom = zoomLevel;
		Calculations.transformePoints(points, ptsTrans);
		points = ptsTrans; // si jamais on a besoin de reset, on aura pas à
							// refaire les calculs de transformePoints
		centrerFigure();
		if (zoom != 1.0) {
			zoom(zoom);
		} else {
			applyDefaultZoom();
		}
	}

	/**
	 * Actualise les dimensions de la figure
	 */
	private void refreshFigDims() {
		widthFig = heightFig = right = bottom = 0;
		left = width;
		top = height;
		bottom = 0;
		for (Point p : ptsTrans) {
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
	}

	/**
	 * Centre la figure si on centre dépasse la moitié d'une coté
	 */
	private void centrerFigure() {
		refreshFigDims();
		if (left + (widthFig / 2) < width / 2) {
			// move right
			for (Point p : ptsTrans) {
				p.setX(p.getX() - left + (width / 2) - (widthFig / 2));
			}
		} else if (right - (widthFig / 2) > width / 2) {
			// move left
			for (Point p : ptsTrans) {
				p.setX(p.getX() - (right - width) - (width / 2) + (widthFig / 2));
			}
		}
		if (top + (heightFig / 2) < height / 2) {
			// move down
			for (Point p : ptsTrans) {
				p.setY(p.getY() - top + (height / 2) - (heightFig / 2));
			}
		} else if (bottom - (heightFig / 2) > height / 2) {
			// move up
			for (Point p : ptsTrans) {
				p.setY(p.getY() - bottom + (height / 2) + (heightFig / 2));
			}
		}
	}

	/**
	 * Sauvegarde les segments dont sont composés les faces de l'objet ply
	 * 
	 * @param segments
	 *            la liste des segments récupérés par {@link Lecture}
	 */
	public void setSegments(List<Segment> segments) {
		this.segments = segments;
		setPolyGones();
		numPremFace = Calculations.determineFrontFace(faces);
	}

	/**
	 * Sauvegarde les faces et les faces transformés pour notre visualisation
	 * par rapport à {@link #transformePoints(List, List)}
	 * 
	 * @param faces
	 *            la liste de faces de l'objet récupérés par {@link Lecture}
	 */
	public void setFaces(List<Face> faces) {
		this.faces = faces;
		setFacesTrans();
	}

	/**
	 * Sauvegarde les points transformés dans leurs faces transformés
	 * respectives.
	 */
	private void setFacesTrans() {
		for (Face f : faces) {
			Face fTrans = new Face();
			facesTrans.add(fTrans);
			for (Point pt : f.getList()) {
				fTrans.addPoint(ptsTrans.get(Integer.parseInt(pt.getNom()))); // ajoute
																				// le
																				// point
																				// transformé
																				// ayant
																				// le
																				// meme
																				// numéro
																				// que
																				// le
																				// point
																				// original
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
		for (int i = 0; i < facesTrans.size(); i++) {
			Path2D path = new Path2D.Double();
			polygones.add(path);
			List<Point> pt = facesTrans.get(i).getList();
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
		for (Point pt : ptsTrans) {
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
		int prevX, prevY;

		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation() * -1;
			zoom = 1.0 + (0.05 * notches);
			zoom(zoom);
			refreshObject();
			repaint();
		}

		public void mousePressed(MouseEvent e) {
			prevX = e.getX();
			prevY = e.getY();
		}

		public void mouseDragged(MouseEvent e) {
			  int nextX, nextY;
			  nextX = e.getX();
			  nextY = e.getY();
			  Calculations.translateFigure(ptsTrans, (prevX-nextX) * -1, (prevY-nextY) * -1);
	    	  refreshObject();
	    	  repaint();
	    	  prevX = nextX;
	    	  prevY = nextY;
		  }
	}

	/**
	 * Vide les containers (facesTrans et polygones) pour le ré-remplir avec les
	 * nouveaux points tranformés Sinon on afficherait encore les vieux points
	 * en plus des nouveaux points transformés
	 */
	private void refreshObject() {
		facesTrans.clear();
		polygones.clear();
		setFacesTrans();
		setPolyGones();
	}
	
	public void diagnose() {
		
		System.out.println("\n Liste des points\n");
		for (Point pt : points) {
			System.out.println(pt.toString());
		}

		System.out.println("\n Liste des Faces\n");
		for (int i = 0; i < faces.size(); i++) {
			System.out.println("Face n=" + i + "  " + faces.get(i));
		}
	}

}