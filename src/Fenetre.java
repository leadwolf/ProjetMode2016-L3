
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
		
	public void setFigure(Figure figure, double zoom) {
		panneau.setFigure(figure, zoom);
	}
}