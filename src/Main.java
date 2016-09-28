import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Main {
	
	private static boolean showPoints = false;
	private static boolean showFaces= false;
	private static boolean showSegments = false;
	private static Lecture lect;
	private static Fenetre fen;
	
	public static void main(String[] args) {
	
		/*
		 * Suppose qu'option toujours en premier et que path en dernier, pas plus que de 2 arguments
		 */
		if (args.length == 1 || args.length == 2) {
			if (args[0].equals("-f")) {
				showFaces = true;
			} else if (args[0].equals("-s")) {
				showSegments = true;
			} else {
				showFaces = true;
				showSegments = true;
			}

			Path path = Paths.get(args[args.length-1]);
			lect = new Lecture(path);
			fen = new Fenetre(showPoints, showSegments, showFaces);
		}
		if (lect != null && !lect.isErreur()) {
			List<Point> points = new ArrayList<>();
			List<Face> faces = new ArrayList<>();
			List<Segment> segments = new ArrayList<>();
			points = lect.getPoints();
			faces = lect.getFaces();
			segments = lect.getSegments();

			fen.setVisible(true);
			fen.setPoints(points, 2.5);
			fen.setFaces(faces);
			fen.setSegments(segments);
			fen.repaint();
			
			/*
			System.out.println("Nombre de points = " + lect.getNbPoints() + "\n");
			System.out.println("Nombre de faces = " + lect.getNbFaces() + "\n");
			
			System.out.println("\n Liste des points\n");
			for (Point pt : points) {
				System.out.println(pt.toString());
			}
	
			System.out.println("\n Liste des Faces\n");
			for (int i = 0; i < faces.size(); i++) {
				System.out.println("Face n=" + i + "  " + faces.get(i));
			}
			
			
			System.out.println("\n Liste des Segments\n");
			for (int i = 0; i < segments.size(); i++) {
				System.out.println("Segment n=" + i + "  " + segments.get(i));
			}
			*/
		} else {
			JOptionPane.showMessageDialog(fen, "Erreur lors de la lecture du fichier", "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}
}
