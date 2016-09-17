import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lecture {

	Path file;
	int nbPoints;
	int nbFaces;
	Pattern lastIntPattern;
	Pattern multipleIntPattern;
	Pattern multipleNumberPattern;
	Charset charset;
	List<Point> points;
	List<Face> faces;

	public Lecture(Path file) {
		this.file = file;
		lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
		multipleIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
		multipleNumberPattern = Pattern.compile("[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?");
		points = new ArrayList<>();
		faces = new ArrayList<>();
	}

	public void findNb() {
		charset = Charset.forName("US-ASCII");

		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			String line = null;

			do {
				line = reader.readLine();
			} while (line != null && !line.startsWith("element vertex"));
			nbPoints = getDernierNombre(line);
			System.out.println("nb points = " + nbPoints);

			do {
				line = reader.readLine();
			} while (line != null && !line.startsWith("element face"));
			nbFaces = getDernierNombre(line);
			System.out.println("nb faces = " + nbFaces);

		} catch (Exception x) {
		}
	}

	/**
	 * Stocke les points dans une <b>List</b> de <b>Point</b> 
	 */
	public void stockerPoints() {
		int nbLignesLus = 0;
		boolean startCount = false;
		String line = null;
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			do {
				if (startCount) {
					getDoubles(line, points);
					nbLignesLus++;
				}
				line = reader.readLine();
				if (line.equals("end_header")) {
					startCount = true;
					// lis encore une ligne pour commencer conversion
					line = reader.readLine();
				}
			} while (nbLignesLus < nbPoints);
		} catch (Exception e) {
		}

		System.out.println("\n Liste des points\n");
		for (Point pt : points) {
			System.out.println(pt.toString());
		}

	}
	
	/**
	 * Stocke les faces dans une <b>List</b> de <b>Face</b> 
	 */
	public void stockerFaces() {
		int nbLignesLus = 0;
		int nbFromPoints = 0;
		// on comment a compter a partir de la ligne (end_header + nbPoints)
		boolean startCount = false;
		String line = null;
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			do {
				if (nbFromPoints > nbPoints) {
					getPointsDeFace(line, faces, points);
					nbLignesLus++;
				}
				if (startCount) {
					nbFromPoints++;
				}
				line = reader.readLine();
				if (line != null && line.equals("end_header")) {
					startCount = true;
				}
			} while (nbLignesLus < nbFaces && line != null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("\n Liste des Faces\n");
		for (int i = 0; i < faces.size(); i++) {
			System.out.println("Fn=" + i + "  " + faces.get(i));
		}
	}
	
	/**
	 * Ajoute les points dans les faces
	 * @param input la ligne des numeros des points
	 * @param faces la liste de faces
	 * @param points la liste de points
	 */
	private void getPointsDeFace(String input, List<Face> faces, List<Point> points) {
		Matcher matcher = multipleNumberPattern.matcher(input);
		faces.add(new Face());
		Face tmpFace = faces.get(faces.size() - 1);
		while (matcher.find()) {
			Integer element = Integer.parseInt(matcher.group());
			tmpFace.addPoint(points.get(element));
		}
	}
	
	/**
	 * Remplis les coordonnes des Points dans une liste
	 * @param input le string a extraire les coordonnes
	 * @param points la liste des points dont on ajoute les coordonnees
	 */
	private void getDoubles(String input, List<Point> points) {
		Matcher matcher = multipleNumberPattern.matcher(input);
		points.add(new Point());
		while (matcher.find()) {
			double element = Double.parseDouble(matcher.group());
			points.get(points.size() - 1).add(element);
		}
	}

	/**
	 * Donne un nombre a la fin d'un string
	 * 
	 * @param line
	 * @return
	 */
	private int getDernierNombre(String line) {
		int number = 0;
		String convert = "";
		Matcher matcher = lastIntPattern.matcher(line);
		if (matcher.find()) {
			convert = matcher.group(1);
			number = Integer.parseInt(convert);
		}
		return number;
	}

	public static void main(String[] args) {
		Path path = Paths.get("ply/cube.ply");
		Lecture lect = new Lecture(path);
		lect.findNb();
		lect.stockerPoints();
		lect.stockerFaces();
	}
}
