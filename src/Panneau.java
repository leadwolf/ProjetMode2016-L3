import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * Cette classe sert à afficher l'objet ply.
 * @author Groupe L3
 *
 */
public class Panneau extends JPanel {

	private List<Point> points = new ArrayList<>();
	private List<Point> ptsTrans = new ArrayList<>();
	private List<Face> faces = new ArrayList<>();
	private List<Face> facesTrans = new ArrayList<>();
	private List<Segment> segments = new ArrayList<>();
	private List<Path2D> polygones = new ArrayList<>();
	private Dimension ptsDim = new Dimension(7, 7);
	private boolean drawPoints= true;
	private boolean drawSegments = true;
	private boolean drawFaces = true;
	private int numPremFace = 0;
	private Stroke defaultStroke = new BasicStroke(1);
	private final float dash1[] = {7.0f};
	private final Stroke dottedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
	
	public Panneau(boolean drawPoints, boolean drawSegments, boolean drawFaces) {
		this.drawPoints = drawPoints;
		this.drawSegments = drawSegments;
		this.drawFaces = drawFaces;
	}
	
	public void paintComponent(Graphics gg) {
		super.paintComponent(gg);

		Graphics2D g = (Graphics2D) gg;
		g.drawLine(0, 200, 400, 200);
		g.drawLine(200, 0, 200, 400);

		
		if (drawFaces) {
			g.setColor(Color.CYAN);
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
			 * A refaire : si une partie de face est couvert par celui de devant, et qu'une autre partie est quand meme visible, ces segments
			 * sont toujours en pointillés. => A faire sur une base de segment caché par face au lieu de face caché par face
			 * 
			try {
				g.setColor(Color.BLACK);
				Area front = new Area(polygones.get(numPremFace).getBounds2D());
				for (int i=0;i<polygones.size();i++) {
					Area current = new Area(polygones.get(i).getBounds2D());
					if (!front.intersects(current.getBounds2D())) {
						g.setStroke(defaultStroke);
					} else {
						if (i != numPremFace) {
							g.setStroke(dottedStroke);
						}
					}
					g.draw(polygones.get(i));
				}
				g.setStroke(defaultStroke);
				g.draw(polygones.get(numPremFace));
			} catch (Exception e) {
				// polygones n'ont pas encore été initialisés
			}
			*/
		}
		
		if (drawPoints) {
			g.setColor(Color.PINK);
			for (Point pt : ptsTrans) {
				double x = getWidth() / 2 + pt.x - ptsDim.getWidth()/2;
				double y = getHeight() / 2 + pt.y - ptsDim.getHeight()/2;
				Ellipse2D.Double shape = new Ellipse2D.Double( x, y, ptsDim.getWidth(), ptsDim.getHeight());
				//System.out.println("drawing point =" + shape.x + " " + shape.y);
				g.fill(shape);
			}
		}
		

		

	}
	
	/**
	 * Donne un transformation de x.
	 * <br>Suite de {@link #transformePoints(List, List)}
	 * @param pt le point à transformer
	 * @return la coordonnée x transformée
	 */
	private double transformeXPoint(Point pt) {
		return ((pt.x * 20) + (-10 * pt.z));
	}
	
	
	/**
	 * Donne un transformation de y.
	 * <br>Suite de {@link #transformePoints(List, List)}
	 * @param pt pt le point à transformer
	 * @return la coordonnée y transformée
	 */
	private double transformeY(Point pt) {
		return ((pt.y * 20) + (10 * pt.z)) * -1;
	}
	
	/**
	 * Transforme les points selon l'équation DESTx = (SRC.x * 20) + (10 * SRC.z) et DESTy = (SRC.y * 20) + (SRC * pt.z)
	 * pour un affichage dans {@link #paintComponent(Graphics)}
	 * @param src la source de points
	 * @param dest la liste dans laquelle stocker les points transformés
	 */
	private void transformePoints(List<Point> src, List<Point> dest) {
		for (Point pt : src) {
			Point tmp = new Point();
			tmp.add(transformeXPoint(pt));
			tmp.add(transformeY(pt));
			dest.add(tmp);
		}
	}
	
	
	/**
	 * Sauvegarde les points de l'objet et les transforme pour futur utilisation dans la modélisation, voir {@link #transformePoints(List, List)}
	 * @param points la liste de points de l'objet récupérés par {@link Lecture}
	 * @param zoomLevel le niveau de zoom à appliqer à l'objet. La valeur 1.0 ne modifie rien.
	 */
	public void setPoints(List<Point> points, double zoomLevel) {
		this.points = points;
		if (zoomLevel != 1.0) {
			zoom(zoomLevel);
		}
		transformePoints(points, ptsTrans);
	}
	
	
	/**
	 * Sauvegarde les segments dont sont composés les faces de l'objet ply
	 * @param segments la liste des segments récupérés par {@link Lecture}
	 */
	public void setSegments(List<Segment> segments) {
		this.segments = segments;
		setPolyGones();
		determineFrontFace();
	}

		
	/**
	 * Sauvegarde les faces et les faces transformés pour notre visualisation par rapport à {@link #transformePoints(List, List)}
	 * @param faces la liste de faces de l'objet récupérés par {@link Lecture}
	 */
	public void setFaces(List<Face> faces) {
		this.faces = faces;
		for (Face f: faces) {
			Face fTrans = new Face();
			facesTrans.add(fTrans);
			for (Point pt : f.getList()) {
				fTrans.addPoint(ptsTrans.get(Integer.parseInt(pt.getNom()))); // ajoute le point transformé ayant le meme numéro que le point original
			}
		}
	}
	
	/**
	 * Sauvegarde les polygones à dessiner grâce aux points de <b>faceTrans</b>
	 * <br>En clair, polygones contient les vrais formes que Graphics peut dessiner, voir {@link #paintComponent(Graphics)}
	 * Les faces sont alors représentés par les polygones remplis. Les listes faces ne servent simplement qu'à contenir la liste de points.
	 */
	private void setPolyGones() {
		for (int i = 0; i < facesTrans.size(); i++) {
			Path2D path = new Path2D.Double();
			polygones.add(path);
			List<Point> pt = facesTrans.get(i).getList();
			path.moveTo( (getWidth()/2) + pt.get(0).getX(), (getHeight()/2) + pt.get(0).getY());
			for (int j = 1; j < pt.size(); j++) {
				path.lineTo( (getWidth()/2) + pt.get(j).getX(), (getHeight()/2) + pt.get(j).getY());
			}
			path.closePath();
		}
	}
	
	private void determineFrontFace() {
		if (faces.size() > 1) {
			int premFace = 0;
			double sommeZPtsFace = 0.0;
			double moyenneZPtsFace = 0.0;
			double oldMoyenne = -1;
			for (int i=0;i<faces.size();i++) {
				sommeZPtsFace = 0.0;
				moyenneZPtsFace = 0.0;
				for (Point pt : faces.get(i).getList()) {
					sommeZPtsFace += pt.getZ();
				}
				moyenneZPtsFace = sommeZPtsFace / (faces.get(i).getList().size()-1);
				if (moyenneZPtsFace > oldMoyenne) {
					oldMoyenne = moyenneZPtsFace;
					premFace = i;
				}
			}
			numPremFace = premFace;
		//	System.out.println("first face = " + numPremFace + " " + faces.get(numPremFace));
		}
	}
	
	/**
	 * Applique un "zoom" en écartant les points par rapport à l'origine (0,0,0)
	 * @param zoomLevel le niveau de zoom à appliquer
	 */
	private void zoom(double zoomLevel) {
		for (Point pt : points) {
			pt.setX(pt.getX() * zoomLevel);
			pt.setY(pt.getY() * zoomLevel);
			pt.setZ(pt.getZ() * zoomLevel);
		}
	}

}