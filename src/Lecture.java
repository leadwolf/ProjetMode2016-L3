import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cette classe donne accès à la liste des Points et de Faces de l'objet .ply dont on veut modéliser
 * à travers les méthodes {@link #getPoints()} et {@link #getFaces()}
 * @author Groupe L3
 *
 */
public class Lecture {

	private Path file;
	private int nbPoints;
	private int nbFaces;
	private boolean savedPoints;
	private boolean savedFaces;
	private Pattern lastIntPattern;
	private Pattern multipleNumberPattern;
	private Charset charset;
	private List<Point> points;
	private List<Face> faces;
	
	/**
	 * Retourne la <b>List&ltPoint&gt</b> de l'objet .ply. Si celle n'est pas encore faite, on éxécute {@link #stockerPoints()}
	 * @return la liste des points
	 */
	public List<Point> getPoints() {
		if (!savedPoints) {
			stockerPoints();
		}
		return points;
	}
	
	/**
	 * Retourne la <b>List&ltFace&gt</b> de l'objet .ply. Si celle n'est pas encore faite, on �x�cute {@link #stockerFaces()}
	 * @return la liste des Faces
	 */
	public List<Face> getFaces() {
		if (!savedFaces) {
			stockerFaces();
		}
		return faces;
	}
	
	/**
	 * Initialise variables
	 * @param file le <b>Path</b> de l'objet .ply
	 */
	public Lecture(Path file) {
		this.file = file;
		nbPoints = -1;
		nbFaces = -1;
		savedPoints = false;
		savedFaces = false;
		lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
		multipleNumberPattern = Pattern.compile("[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?");
		charset = Charset.forName("US-ASCII");
		points = new ArrayList<>();
		faces = new ArrayList<>();
	}
	
	/**
	 * Verifie que le fichier en parametre est un vrai fichier et que la premiere ligne
	 * est simplement "ply"
	 * @return
	 */
	public boolean verifieFichier() {
		String filename = file.getFileName().toString();
		String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
		
		if (new File(file.toString()).isFile() && extension.equals("ply") ) {
			try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
				String line = null;
				line = reader.readLine();
				if (line.equals("ply")) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * Verifie que tous les points ont bien des coordonnées x, y et z
	 * @return
	 */
	public boolean verifieListePoints() {
		for (Point pt : points) {
			if (!pt.complet()) {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Trouve le nombre de <b>Points</b> et <b>Faces</b> qui composent l'objet .ply
	 */
	private void findNb() {
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
			x.printStackTrace();
		}
	}

	/**
	 * Lit le fichier .ply et sauvegarde la liste des Points dans une dans une <b>List&ltPoint&gt</b>
	 */
	private void stockerPoints() {
		findNb();
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

		savedPoints = true;
	}
	
	/**
	 * Lit le fichier .ply et sauvegarde la liste des faces dans une dans une <b>List&ltFace&gt</b>
	 * Si la liste des points n'est pas encore faite, elle éxécute aussi {@link #stockerPoints()}
	 */
	private void stockerFaces() {
		if (!savedPoints) {
			stockerPoints();
		}
		stockerFacesSuite();
		savedFaces = true;
	}
	
	/**
	 * Suite de {@link #stockerFaces()
	 */
	private void stockerFacesSuite() {
		int nbLignesLus = 0;
		int nbFromPoints = 0;
		// on commence a compter a partir de la ligne (end_header + nbPoints)
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
	}
	
	/**
	 * Sauvegard la <b>List&ltPoint&gt</b> pour chaque Face de <b>List&ltFace&gt</b>
	 * @param line la ligne contenant la liste de Points composant la Face courante
	 * @param faces la liste de faces
	 * @param points la liste de points
	 */
	private void getPointsDeFace(String line, List<Face> faces, List<Point> points) {
		String[] strArray = line.split(" ");
		faces.add(new Face());
		Face tmpFace = faces.get(faces.size() - 1);
		for (int i=1;i<strArray.length;i++) {
			Integer element = Integer.parseInt(strArray[i]);
			tmpFace.addPoint(points.get(element));
		}
	}
	
	/**
	 * Remplis les coordonnes pour chaque Point dans <b>List&ltPoint&gt</b>
	 * @param line le string a extraire les coordonnes
	 * @param points la liste des points dont on ajoute les coordonnees
	 */
	private void getDoubles(String line, List<Point> points) {
		Matcher matcher = multipleNumberPattern.matcher(line);
		points.add(new Point());
		while (matcher.find()) {
			double element = Double.parseDouble(matcher.group());
			points.get(points.size() - 1).add(element);
			points.get(points.size() - 1).setName("" + (points.size() - 1));
		}
	}

	/**
	 * Donne un nombre a la fin d'un string
	 * 
	 * @param line la ligne a extraire le numero
	 * @return le numero
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
		List<Point> points = new ArrayList<>();
		List<Face> faces = new ArrayList<>();
		points = lect.getPoints();
		faces = lect.getFaces();
		

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
