import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Figure {

	private int nbPoints;
	private int nbFaces;
	private List<Point> points;
	private List<Face> faces;
	private List<Segment> segments;
	private Lecture lecture;
	
	
	public Figure() {
		nbPoints = -1;
		nbFaces = -1;
		points = new ArrayList<>();
		faces = new ArrayList<>();
		segments = new ArrayList<>();
	}
	
	public int getNbPoints() {
		return nbPoints;
	}

	public int getNbFaces() {
		return nbFaces;
	}

	public List<Point> getPoints() {
		return points;
	}

	public List<Face> getFaces() {
		return faces;
	}

	public List<Segment> getSegments() {
		return segments;
	}

	public Figure(Path file) {
		lecture = new Lecture(file);
		nbPoints = lecture.getNbPoints();
		nbFaces = lecture.getNbFaces();
		points = lecture.getPoints();
		faces = lecture.getFaces();
		segments = lecture.getSegments();
	}
	
	public boolean getErreurLecture() {
		if (lecture != null) {
			return lecture.isErreur();
		} else {
			return true;
		}
	}


}
