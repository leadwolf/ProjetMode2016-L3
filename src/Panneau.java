import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class Panneau extends JPanel {

	List<Point> points = new ArrayList<>();
	List<Point> pointsTransformes = new ArrayList<>();
	List<Face> faces = new ArrayList<>();
	List<Face> facesTrans = new ArrayList<>();
	List<Segment> segments = new ArrayList<>();
	List<Path2D> polygones = new ArrayList<>();

	public void paintComponent(Graphics gg) {
		super.paintComponent(gg);

		Graphics2D g = (Graphics2D) gg;
		g.drawLine(0, 200, 400, 200);
		g.drawLine(200, 0, 200, 400);

		g.setColor(Color.PINK);

		for (Point pt : pointsTransformes) {
			Ellipse2D.Double shape = new Ellipse2D.Double(getWidth() / 2 + pt.x, getHeight() / 2 + pt.y, 7, 7);
			// System.out.println("drawing point =" + shape.x + " " + shape.y);
			g.fill(shape);
		}

		for (Path2D pa : polygones) {
			g.draw(pa);
		}
	}

	private double transformeXPoint(Point pt) {
		return ((pt.x * 20) + (10 * pt.z));
	}

	private double transformeY(Point pt) {
		return ((pt.y * 20) + (10 * pt.z));
	}

	private void transformePoints() {
		for (Point pt : points) {
			pointsTransformes.add(new Point());
			Point tmp = pointsTransformes.get(pointsTransformes.size() - 1);
			tmp.add(transformeXPoint(pt));
			tmp.add(transformeY(pt));
		}
	}

	public void setPoints(List<Point> points) {
		this.points = points;
		transformePoints();
	}

	public void setSegments(List<Segment> segments) {
		this.segments = segments;
		setPolyGones();
	}

	public void setFaces(List<Face> faces) {
		this.faces = faces;
		// for all existing normal faces
		for (Face f: faces) {
			Face fTrans = new Face();
			facesTrans.add(fTrans); // create equivalent transformed face
			List<Point> pt = f.getList();
			for (int i=0; i<pt.size();i++) { // for all the points in the normal face
				Point ptTrans = new Point();
				ptTrans.add(transformeXPoint(pt.get(i))); // add a point in tranformedFace that is the transformation of normal point
				ptTrans.add(transformeY(pt.get(i)));
				fTrans.addPoint(ptTrans);
			}
		}
	}

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

}