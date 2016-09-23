import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		Fenetre fen = new Fenetre();

		Path path = Paths.get("ply/cube.ply");
		Lecture lect = new Lecture(path);
		List<Point> points = new ArrayList<>();
		List<Face> faces = new ArrayList<>();
		List<Segment> segments = new ArrayList<>();
		points = lect.getPoints();
		faces = lect.getFaces();
		segments = lect.getSegments();

		fen.setPoints(points);

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
	}
}
