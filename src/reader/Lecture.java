package reader;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import modele.Face;
import modele.Point;
import modele.Segment;
import reader.LectureErreurType.ErreurType;

/**
 * Cette classe donne accès à la liste des Points et de Faces de l'objet .ply dont on veut modéliser à travers les méthodes {@link #getPoints()} et
 * {@link #getFaces()}
 * 
 * @author Groupe L3
 *
 */
public class Lecture {

	private Path file;
	private int nbPoints;
	private int nbFaces;
	private Pattern lastIntPattern;
	private Charset charset;
	private List<Point> points;
	private List<Face> faces;
	private List<Segment> segments;
	private boolean erreur = false;
	private boolean noPrint;
	private LectureErreurType erreurType = null;

	/**
	 * Retourne la <b>List&ltPoint&gt</b> de l'objet .ply. Si celle n'est pas encore faite, on éxécute {@link #stockerPoints()}
	 * 
	 * @return la liste des points
	 */
	public List<Point> getPoints() {
		return points;
	}

	/**
	 * Retourne la <b>List&ltFace&gt</b> de l'objet .ply. Si celle n'est pas encore faite, on �x�cute {@link #stockerFaces()}
	 * 
	 * @return la liste des Faces
	 */
	public List<Face> getFaces() {
		return faces;
	}

	public List<Segment> getSegments() {
		return segments;
	}

	public boolean isErreur() {
		return erreur;
	}

	/**
	 * Initialise variables
	 * 
	 * @param file
	 *            le <b>Path</b> de l'objet .ply
	 */
	public Lecture(Path file, boolean noPrint) {
		this.file = file;
		this.noPrint = noPrint;
		nbPoints = -1;
		nbFaces = -1;
		lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
		charset = Charset.forName("US-ASCII");
		points = new ArrayList<>();
		faces = new ArrayList<>();
		segments = new ArrayList<>();
		getElements();
	}

	/**
	 * Verifie que le fichier en parametre est un vrai fichier et que la premiere ligne est simplement "ply"
	 * 
	 * @return
	 */
	public boolean verifieFichier() {
		String filename = file.getFileName().toString();
		String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());

		if (new File(file.toString()).isFile()) {
			if (extension.equals("ply")) {
				try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
					String line = null;
					line = reader.readLine();
					if (line.equals("ply")) {
						return true;
					} else {
						if (!noPrint) {
							System.out.println("Erreur dans fichier " + file.getFileName() + " : Fichier ne commence pas par \"ply\"");
						}
						if (erreurType == null) {
							erreurType = new LectureErreurType(ErreurType.PLY_NOT_FOUND);
						}
						erreur = true;
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if (!noPrint) {
					System.out.println("Erreur dans fichier " + file.getFileName() + " : Fichier n'a pas l'extension .ply");
				}
				if (erreurType == null) {
					erreurType = new LectureErreurType(ErreurType.BAD_EXTENSION);
				}
				erreur = true;
				return false;
			}
		}
		if (!noPrint) {
			System.out.println("Fichier " + file.getFileName() + " inexistant");
		}
		if (erreurType == null) {
			erreurType = new LectureErreurType(ErreurType.FILE_NONEXISTING);
		}
		erreur = true;
		return false;
	}

	public int getNbPoints() {
		return nbPoints;
	}

	public int getNbFaces() {
		return nbFaces;
	}

	private void getElements() {
		if (verifieFichier()) {
			findNb();
			if (!erreur) {
				stockerPoints();
			}
			if (!erreur) {
				stockerFaces();
			}
		}
	}

	public LectureErreurType getErreurType() {
		return erreurType;
	}

	/**
	 * Trouve le nombre de <b>Points</b> et <b>Faces</b> qui composent l'objet .ply
	 */
	private void findNb() {
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			String line = null;

			do {
				line = reader.readLine();
				if (line == null) {
					// Fin du fichier alors que cherchait nombre de points
					if (!noPrint) {
						System.out.println("Erreur dans fichier " + file.getFileName() + " : Nombre de points non indiqué");
					}
					if (erreurType == null) {
						erreurType = new LectureErreurType(ErreurType.MISSING_ELEMENT_VERTEX);
					}
					erreur = true;
				}
			} while (line != null && !line.startsWith("element vertex"));
			if (line != null) {
				nbPoints = getDernierNombre(line);
			}
			// System.out.println("nb points = " + nbPoints);

			do {
				line = reader.readLine();
				if (line == null) {
					// Fin du fichier alors que cherchait nombre de faces
					if (!noPrint && nbPoints != -1) {
						System.out.println("Erreur dans fichier " + file.getFileName() + " : Nombre de faces non indiqué");
					}
					if (erreurType == null) {
						erreurType = new LectureErreurType(ErreurType.MISSING_ELEMENT_FACE);
					}
					erreur = true;
				}
			} while (line != null && !line.startsWith("element face"));
			if (line != null) {
				nbFaces = getDernierNombre(line);
			}
			// System.out.println("nb faces = " + nbFaces);

		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	/**
	 * Lit le fichier .ply et sauvegarde la liste des Points dans une dans une <b>List&ltPoint&gt</b>
	 */
	private void stockerPoints() {
		int nbLignesLus = 0;
		boolean startCount = false;
		String line = null;
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			do {
				if (line != null && startCount) {
					String[] strArray = line.split(" ");
					if (strArray.length != 3 && erreurType == null) {
						if (strArray.length < 3) {
							if (!noPrint) {
								System.out.println("line = " + line);
								System.out.println("Erreur dans fichier " + file.getFileName() + " : pas assez de coordonnées dans un Point");
								System.out.println("Peut être causée par un Point(s) manquant");
							}
							if (erreurType == null) {
								erreurType = new LectureErreurType(ErreurType.MISSING_COORD);
							}
						} else if (strArray.length > 3) {
							if (!noPrint) {
								System.out.println("Erreur dans fichier " + file.getFileName() + " : trop de coordonnées dans un Point");
								System.out.println("Peut être causée par un Point(s) manquant ou nombre de Points incorrects");
							}
							if (erreurType == null) {
								erreurType = new LectureErreurType(ErreurType.TOO_MANY_COORDS);
							}
						}
						erreur = true;
					}
				}
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
			} while (nbLignesLus < nbPoints && line != null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lit le fichier .ply et sauvegarde la liste des faces dans une dans une <b>List&ltFace&gt</b> Si la liste des points n'est pas encore faite, elle éxécute
	 * aussi {@link #stockerPoints()}
	 */
	private void stockerFaces() {
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
			if (nbLignesLus != nbFaces) {
				// Fin du fichier alors que n'a pas lu tous les points spécifiés dans entête
				if (!noPrint && erreurType == null) {
					System.out.println("Erreur dans fichier " + file.getFileName() + " : Face(s) non trouvé(s)");
					System.out.println("Peut être causée par une ligne supprimée ou nombre de faces incorrects");
				}
				if (erreurType == null) {
					erreurType = new LectureErreurType(ErreurType.MISSING_FACE);
				}
				erreur = true;
			}
			if (line != null && !line.startsWith("{")) {
				// Fichier comporte plus de lignes qu'attendu
				if (!noPrint && erreurType == null) {
					System.out.println("Erreur dans fichier " + file.getFileName() + " : Entête ne correspond pas à la description");
					System.out.println("Trop de lignes par rapport au nombre de Faces");
				}
				if (erreurType == null) {
					erreurType = new LectureErreurType(ErreurType.TOO_MANY_LINES);
				}
				erreur = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sauvegard la <b>List&ltPoint&gt</b> pour chaque Face de <b>List&ltFace&gt</b>
	 * 
	 * @param line
	 *            la ligne contenant la liste de Points composant la Face courante
	 * @param faces
	 *            la liste de faces
	 * @param points
	 *            la liste de points
	 */
	private void getPointsDeFace(String line, List<Face> faces, List<Point> points) {
		int expectedPoints = getPremierNombre(line);
		String[] strArray = line.split(" ");
		if (strArray.length - 1 != expectedPoints) {
			// Pas assez de point dans cette face ou lu point alors qu'attend face
			if (!noPrint && erreurType == null) {
				System.out.println("line = " + line);
				System.out.println("Erreur dans fichier " + file.getFileName() + " : Face incomplet ou a trouvé point");
			}
			if (erreurType == null) {
				erreurType = new LectureErreurType(ErreurType.MISSING_POINT_IN_FACE);
			}
			erreur = true;
		}
		faces.add(new Face());
		Face tmpFace = faces.get(faces.size() - 1);
		for (int i = 1; i < strArray.length; i++) {
			Integer element = Integer.parseInt(strArray[i]);
			if (element < 0 || element > points.size()) {
				// Face comporte point n=" + element + " alors qu'il est inexistant
				if (!noPrint && erreurType == null) {
					System.out.println("Erreur dans fichier " + file.getFileName() + " : Face fait référence à point \"" + element + "\" inexistant");
				}
				if (erreurType == null) {
					erreurType = new LectureErreurType(ErreurType.POINT_NOT_FOUND);
				}
				erreur = true;
			} else {
				tmpFace.addPoint(points.get(element));
			}
		}
	}

	/**
	 * Remplis les coordonnes pour chaque Point dans <b>List&ltPoint&gt</b>
	 * 
	 * @param line
	 *            le string a extraire les coordonnes
	 * @param points
	 *            la liste des points dont on ajoute les coordonnees
	 */
	private void getDoubles(String line, List<Point> points) {
		String[] strArray = line.split(" ");
		if (strArray.length > 3) {
			// Lu face alors qu'attend point
			if (!noPrint && erreurType == null) {
				System.out.println("Erreur dans fichier " + file.getFileName() + " : Point comporte plus que 3 coordonnées");
				System.out.println("Peut être causée si attendait Point mais trouvé Face");
			}
			if (erreurType == null) {
				erreurType = new LectureErreurType(ErreurType.TOO_MANY_COORDS);
			}
			erreur = true;
		}
		Point tmpPoint = new Point();
		points.add(tmpPoint);
		for (int i = 0; i < strArray.length; i++) {
			double element = Double.parseDouble(strArray[i]);
			tmpPoint.add(element);
			tmpPoint.setName("" + (points.size() - 1));
		}
		if (!tmpPoint.complet()) {
			// Point n=" + (points.size() - 1) + " pas complet
			if (!noPrint && erreurType == null) {
				System.out.println("Erreur dans fichier " + file.getFileName() + " : Point ne comporte pas 3 coordonnées");
			}
			if (erreurType == null) {
				erreurType = new LectureErreurType(ErreurType.MISSING_COORD);
			}
			erreur = true;
		}
	}

	/**
	 * Donne un nombre a la fin d'un string
	 * 
	 * @param line
	 *            la ligne a extraire le numero
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

	private int getPremierNombre(String line) {
		int number = 0;
		Matcher matcher = Pattern.compile("\\d+").matcher(line);
		matcher.find();
		return Integer.valueOf(matcher.group());
	}
}
