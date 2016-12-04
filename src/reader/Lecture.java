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
import result.BasicResult;
import result.BasicResultEnum;
import result.MethodResult;
import result.ReaderResult;
import result.ReaderResultEnum;

/**
 * Cette classe donne accès à la liste des Points et de Faces de l'objet .ply dont on veut modéliser à  travers les méthodes {@link #getPoints()} et
 * {@link #getFaces()}
 * 
 * @author Groupe L3
 *
 */
public class Lecture {

	/**
	 * Le Path vers le fichier .ply qu'on manipule.
	 */
	private Path file;
	/**
	 * Le nombre de Points d'après "element vertex X". Sert à  garder la trace de lecture du fichier.
	 */
	private int nbPoints;
	/**
	 * Le nombre de Faces d'apres "element face X". Sert à  garder la trace de lecture du fichier.
	 */
	private int nbFaces;
	/**
	 * Règle le Charset de lecture pour les fichiers .ply
	 */
	private Charset charset;
	/**
	 * Les Points lus sont stockés ici.
	 */
	private List<Point> points;
	/**
	 * Les Faces lues sont stockés ici.
	 */
	private List<Face> faces;
	/**
	 * Si on veut empêcher les System.out.println lors d'erreus
	 */
	private boolean noPrint;
	/**
	 * Le résultat d'éxécution de Lecture();
	 */
	private MethodResult result;

	/**
	 * Retourne la <b>List&lt;Point&gt;</b> de l'objet .ply. Si celle n'est pas encore faite, on éxécute {@link #stockerPoints()}
	 * 
	 * @return la liste des points
	 */
	public List<Point> getPoints() {
		return points;
	}

	/**
	 * Retourne la <b>List&lt;Face&gt;</b> de l'objet .ply. Si celle n'est pas encore faite, on éxécute {@link #stockerFaces()}
	 * 
	 * @return la liste des Faces
	 */
	public List<Face> getFaces() {
		return faces;
	}

	/**
	 * Donne le résultat d'éxécution de Lecture.
	 * 
	 * @return Soit un {@link ReaderResult} ou {@link BasicResult}
	 */
	public MethodResult getResult() {
		if (result == null) {
			return new BasicResult(BasicResultEnum.ALL_OK);
		}
		return result;
	}

	/**
	 * Initialise variables
	 * 
	 * @param file le <b>Path</b> de l'objet .ply
	 * @param noPrint si on veut empêcher les System.out.println
	 */
	public Lecture(Path file, boolean noPrint) {
		this.file = file;
		this.noPrint = noPrint;
		result = null;
		nbPoints = -1;
		nbFaces = -1;
		charset = Charset.forName("US-ASCII");
		points = new ArrayList<>();
		faces = new ArrayList<>();
		getElements();
	}

	/**
	 * Verifie que le fichier en parametre est un vrai fichier et que la premiere ligne est simplement "ply"
	 * 
	 * @return si la vérification du fichier est OK
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
						if (result == null) {
							result = new ReaderResult(ReaderResultEnum.PLY_NOT_FOUND);
						}
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if (!noPrint) {
					System.out.println("Erreur dans fichier " + file.getFileName() + " : Fichier n'a pas l'extension .ply");
				}
				if (result == null) {
					result = new ReaderResult(ReaderResultEnum.BAD_EXTENSION);
				}
				return false;
			}
		}
		if (!noPrint) {
			System.out.println("Fichier " + file.getFileName() + " inexistant");
		}
		result = new ReaderResult(ReaderResultEnum.FILE_NONEXISTING);
		return false;
	}

	public int getNbPoints() {
		return nbPoints;
	}

	public int getNbFaces() {
		return nbFaces;
	}

	/**
	 * Exécute les méthodes de stockage de Points et Faces
	 */
	private void getElements() {
		if (verifieFichier()) {
			findNb();
			if (result == null) {
				stockerPoints();
			}
			if (result == null) {
				stockerFaces();
			}
		}
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
					if (result == null) {
						result = new ReaderResult(ReaderResultEnum.MISSING_ELEMENT_VERTEX);
					}
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
					if (result == null) {
						result = new ReaderResult(ReaderResultEnum.MISSING_ELEMENT_FACE);
					}
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
	 * Lit le fichier .ply et sauvegarde la liste des Points dans une dans une <b>List&lt;Point&gt;</b>
	 */
	private void stockerPoints() {
		int nbLignesLus = 0;
		boolean startCount = false;
		String line = null;
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			do {
				if (line != null && startCount) {
					String[] strArray = line.split(" ");
					if (strArray.length != 3 && result == null) {
						if (strArray.length < 3) {
							if (!noPrint) {
								System.out.println("line = " + line);
								System.out.println("Erreur dans fichier " + file.getFileName() + " : pas assez de coordonnées dans un Point");
								System.out.println("Peut être causée par un Point(s) manquant");
							}
							if (result == null) {
								result = new ReaderResult(ReaderResultEnum.MISSING_COORD);
							}
						} else if (strArray.length > 3) {
							if (!noPrint) {
								System.out.println("Erreur dans fichier " + file.getFileName() + " : trop de coordonnées dans un Point");
								System.out.println("Peut être causée par un Point(s) manquant ou nombre de Points incorrects");
							}
							if (result == null) {
								result = new ReaderResult(ReaderResultEnum.TOO_MANY_COORDS);
							}
						}
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
	 * Lit le fichier .ply et sauvegarde la liste des faces dans une dans une <b>List&lt;Face&gt;</b> Si la liste des points n'est pas encore faite, elle éxécute
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
				if (!noPrint && result == null) {
					System.out.println("Erreur dans fichier " + file.getFileName() + " : Face(s) non trouvé(s)");
					System.out.println("Peut être causée par une ligne supprimée ou nombre de faces incorrects");
				}
				if (result == null) {
					result = new ReaderResult(ReaderResultEnum.MISSING_FACE);
				}
			}
			if (line != null && !line.startsWith("{")) {
				// Fichier comporte plus de lignes qu'attendu
				if (!noPrint && result == null) {
					System.out.println("Erreur dans fichier " + file.getFileName() + " : Entête ne correspond pas à la description");
					System.out.println("Trop de lignes par rapport au nombre de Faces");
				}
				if (result == null) {
					result = new ReaderResult(ReaderResultEnum.TOO_MANY_LINES);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sauvegarde la <b>List&lt;Point&gt;</b> pour chaque Face de <b>List&lt;Face&gt;</b>
	 * 
	 * @param line la ligne contenant la liste de Points composant la Face courante
	 * @param faces la liste de faces
	 * @param points la liste de points
	 */
	private void getPointsDeFace(String line, List<Face> faces, List<Point> points) {
		int expectedPoints = getPremierNombre(line);
		String[] strArray = line.split(" ");
		if (strArray.length - 1 != expectedPoints) {
			// Pas assez de point dans cette face ou lu point alors qu'attend face
			if (!noPrint && result == null) {
				System.out.println("line = " + line);
				System.out.println("Erreur dans fichier " + file.getFileName() + " : Face incomplet ou a trouvé point");
			}
			if (result == null) {
				result = new ReaderResult(ReaderResultEnum.MISSING_POINT_IN_FACE);
			}
		}
		faces.add(new Face());
		Face tmpFace = faces.get(faces.size() - 1);
		for (int i = 1; i < strArray.length; i++) {
			Integer element = Integer.parseInt(strArray[i]);
			if (element < 0 || element > points.size()) {
				// Face comporte point n=" + element + " alors qu'il est inexistant
				if (!noPrint && result == null) {
					System.out.println("Erreur dans fichier " + file.getFileName() + " : Face fait référence à point \"" + element + "\" inexistant");
				}
				if (result == null) {
					result = new ReaderResult(ReaderResultEnum.POINT_NOT_FOUND);
				}
			} else {
				tmpFace.addPoint(points.get(element));
			}
		}
	}

	/**
	 * Remplis les coordonnes pour chaque Point dans <b>List&lt;Point&gt;</b>
	 * 
	 * @param line le string a extraire les coordonnes
	 * @param points la liste des points dont on ajoute les coordonnees
	 */
	private void getDoubles(String line, List<Point> points) {
		String[] strArray = line.split(" ");
		if (strArray.length > 3) {
			// Lu face alors qu'attend point
			if (!noPrint && result == null) {
				System.out.println("Erreur dans fichier " + file.getFileName() + " : Point comporte plus que 3 coordonnées");
				System.out.println("Peut être causée si attendait Point mais trouvé Face");
			}
			if (result == null) {
				result = new ReaderResult(ReaderResultEnum.TOO_MANY_COORDS);
			}
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
			if (!noPrint && result == null) {
				System.out.println("Erreur dans fichier " + file.getFileName() + " : Point ne comporte pas 3 coordonnées");
			}
			if (result == null) {
				result = new ReaderResult(ReaderResultEnum.MISSING_COORD);
			}
		}
	}

	/**
	 * Donne un nombre a la fin d'un string
	 * 
	 * @param line la ligne a extraire le numero
	 * @return le numero
	 */
	private int getDernierNombre(String line) {
		Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
		int number = 0;
		String convert = "";
		Matcher matcher = lastIntPattern.matcher(line);
		if (matcher.find()) {
			convert = matcher.group(1);
			number = Integer.parseInt(convert);
		}
		return number;
	}

	/**
	 * @param line
	 * @return le premier nombre dans la ligne
	 */
	private int getPremierNombre(String line) {
		Matcher matcher = Pattern.compile("\\d+").matcher(line);
		matcher.find();
		return Integer.valueOf(matcher.group());
	}
}
