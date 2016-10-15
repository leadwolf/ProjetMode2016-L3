
import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;

public class Fenetre extends JFrame {
	
	Panneau panneau;
	private Dimension dim = new Dimension(800,800);
	
	public Fenetre(boolean drawPoints, boolean drawSegments, boolean drawFaces) {
		panneau = new Panneau(drawPoints, drawSegments, drawFaces);
		this.setTitle("Modelisationator");
		this.setSize(dim);
		panneau.setDimensions(dim);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(panneau);
	}
		
	public void setPoints(List<Point> points, double zoomLevel) {
		panneau.setPoints(points, zoomLevel);
	}
	
	public void setSegments(List<Segment> segments) {
		panneau.setSegments(segments);
	}
	
	public void setFaces(List<Face> faces) {
		panneau.setFaces(faces);
	}
	
	public void setFigure(Figure figure, double zoomLevel) {
		panneau.setPoints(figure.getPoints(), zoomLevel);
		panneau.setFaces(figure.getFaces());
		panneau.setSegments(figure.getSegments());
	}
}