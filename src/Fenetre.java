
import java.util.List;

import javax.swing.JFrame;

public class Fenetre extends JFrame {
	
	Panneau panneau;
	
	public Fenetre() {
		
		panneau = new Panneau(true, true, true);
		
		this.setTitle("Ma première fenêtre Java");
		this.setSize(400, 400);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(panneau);
		this.setVisible(true);
	}
	
	public void setPoints(List<Point> points) {
		panneau.setPoints(points);
	}
	
	public void setSegments(List<Segment> segments) {
		panneau.setSegments(segments);
	}
	
	public void setFaces(List<Face> faces) {
		panneau.setFaces(faces);
	}
}