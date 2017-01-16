package ply.main;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import ply.bdd.base.BDDUtilities;
import ply.bdd.strategy.DataBaseStrategy;
import ply.bdd.strategy.ExecuteStrategy;
import ply.main.vues.MainFenetre;
import ply.math.Vecteur;
import ply.plyModel.modeles.FigureModelNew;
import ply.reader.AsciiReader;
import ply.reader.Reader;
import ply.result.BDDResult;
import ply.result.BasicResult;
import ply.result.MethodResult;
import ply.result.BDDResult.BDDResultEnum;
import ply.result.BasicResult.BasicResultEnum;

/**
 * ¨Programme principale de modélisation.
 * 
 * @author L3
 *
 */
public class Modelisationator {

	/**
	 * Le nom de base de ce programme.
	 */
	public final static String NAME = "Modelisationator";

	private boolean drawFaces;
	private boolean drawSegments;
	private boolean drawPoints;
	private boolean reset;
	private boolean fill;
	private boolean delete;
	private static Path plyPath;

	private boolean executeDB;
	private boolean foundFile;
	private boolean found3DOptions;

	private Pattern singleMinus;
	private Pattern doubleMinus;
	private DataBaseStrategy strategy;

	/**
	 * 
	 */
	public Modelisationator() {
		drawFaces = true;
		drawSegments = false;
		drawPoints = false;
		reset = false;
		fill = false;
		delete = false;

		executeDB = false;
		foundFile = false;
		found3DOptions = false;

		singleMinus = Pattern.compile("^(\\-)\\w+");
		doubleMinus = Pattern.compile("^(\\-\\-)\\w+");
	}

	@SuppressWarnings("javadoc")
	public static void main(String[] args) {
		Modelisationator modelisationator = new Modelisationator();
		parseArgs(args, modelisationator, null, false);
	}

	/**
	 * Méthode principale pour éxécuter le programme en fonction des arguments
	 * 
	 * @param args la commande.
	 * @param modelisationator objet instancié qui contient les booleans options pour ce lancement
	 * @param dbPath path to the db.sqlite, leave null for default data/test.sqlite
	 * @param quiet true pour empecher affichage.
	 * @return soit un erreur dans les arguments, soit le résultat de l'éxécution de la méthode pertinent aux arguments
	 */
	public static MethodResult parseArgs(String[] args, Modelisationator modelisationator, Path dbPath, boolean quiet) {
		if (args.length > 0) {
			MethodResult verifArgsResult = verifArgs(args, modelisationator, quiet);
			if (verifArgsResult.getCode().equals(BasicResultEnum.ALL_OK)) {
				return execute(args, modelisationator, dbPath, quiet);
			} else {
				return verifArgsResult;
			}
		} else {
			if (!quiet) {
				System.out.println("Aucun paramètre précisé");
			}
			return new BasicResult(BasicResultEnum.NO_ARGUMENTS);
		}
	}

	/**
	 * Vérifie tous les options qu'ils soient pour un modèle 3D ou une commande bdd
	 * 
	 * @param args la commande.
	 * @param modelisationator objet instancié qui contient les booleans options pour ce lancement
	 * @param quiet true pour empecher affichage.
	 * @return si les arguments sont corrects.
	 */
	private static MethodResult verifArgs(String[] args, Modelisationator modelisationator, boolean quiet) {
		// VERFIE UN A UN LES ARGUMENTS
		boolean emptyArg = true;
		for (int i = 0; i < args.length; i++) {
			if (!args[i].matches("\\s*")) {
				emptyArg = false;
			}
		}
		if (emptyArg) {
			if (!quiet) {
				System.out.println("Aucun paramètre précisé");
			}
			return new BasicResult(BasicResultEnum.NO_ARGUMENTS);
		}
		for (int i = 0; i < args.length; i++) {
			String extension = args[i].substring(args[i].lastIndexOf(".") + 1, args[i].length());
			if (BDDUtilities.isExecutableArg(args[i])) {
				modelisationator.executeDB = true;
			}
			if (modelisationator.singleMinus.matcher(args[i]).find()) {
				MethodResult plyResult = verif3Darg(args[i], modelisationator, quiet);
				if (!plyResult.getCode().equals(BasicResultEnum.ALL_OK)) {
					return plyResult;
				}
			} else if (modelisationator.doubleMinus.matcher(args[i]).find()) {
				MethodResult dbResult = verifDBArg(args[i], modelisationator, quiet);
				if (!dbResult.getCode().equals(BasicResultEnum.ALL_OK)) {
					return dbResult;
				}
			} else if (extension.equals("ply")) {
				if (!modelisationator.foundFile) {
					modelisationator.foundFile = true;
					plyPath = Paths.get(args[i]);
				} else {
					if (!quiet) {
						System.out.println("Erreur : Vous avez spécifié deux fichiers ply");
					}
					return new BasicResult(BasicResultEnum.MULTIPLE_PLY_ARGS);
				}
			}
		}

		// VERIFIE LA VALIDITE DE LA COMMANDE ENTIERE

		if (modelisationator.reset && !modelisationator.fill) {
			if (!quiet) {
				System.out.println("Erreur : Vous avez précisé de vider la base sans la ré-remplir.");
			}
			return new BDDResult(BDDResultEnum.INCORRECT_RESET);
		} else if (modelisationator.fill && !modelisationator.reset) {
			if (!quiet) {
				System.out.println("Erreur : Vous avez précisé de remplir la base sans qu'elle soit vide au départ.");
			}
			return new BDDResult(BDDResultEnum.INCORRECT_FILL);
		}

		if (modelisationator.executeDB && modelisationator.foundFile) {
			if (!quiet) {
				System.out.println(
						"Erreur : Vous avez tenté de lancer une commande bdd et lancer un modèle spécifique en même temps.");
			}
			return new BasicResult(BasicResultEnum.CONFLICTING_ARGUMENTS);
		}

		if (modelisationator.executeDB) {
			// On vérifie la commande ici même si on le fait dans execute() quand MainFenetre va créer un BDDPanel car
			// s'il y a une erreur, autant arrêter le
			// programme le plus tôt possible.
			modelisationator.strategy = new ExecuteStrategy();
			MethodResult verifResult = modelisationator.strategy.verifArgs(args, quiet);
			if (!verifResult.getCode().equals(BasicResultEnum.ALL_OK)) {
				return verifResult;
			}
		}

		if (modelisationator.found3DOptions && !modelisationator.foundFile) {
			if (!quiet) {
				System.out.println(
						"Erreur : Vous avez tenté d'éxécuter une commande 3D mais vous n'avez pas spécifié de fichier .ply");
			}
			return new BasicResult(BasicResultEnum.NO_PLY_FILE_IN_ARG);
		}

		if (!modelisationator.executeDB && !modelisationator.foundFile) {
			if (!quiet) {
				System.out.println("Erreur : Vous n'avez pas précisé de commande ni de fichier .ply");
			}
			return new BasicResult(BasicResultEnum.NO_COMMAND_GIVEN);
		}
		return new BasicResult(BasicResultEnum.ALL_OK);
	}

	/**
	 * Crée le {@link MainFenetre} adapté.
	 * 
	 * @param args la commande.
	 * @param modelisationator objet instancié qui contient les booleans options pour ce lancement
	 * @param dbPath path to the db.sqlite, leave null for default data/test.sqlite
	 * @param quiet true pour empecher affichage.
	 * @return s'il a pu créer la fenêtre
	 */
	private static MethodResult execute(String[] args, Modelisationator modelisationator, Path dbPath, boolean quiet) {
		if (modelisationator.foundFile) {
			Reader asciiReader = new AsciiReader(plyPath.toFile());
			FigureModelNew figureModel = new FigureModelNew(asciiReader);
			// TODO errors
			// if (figureModel != null && !figureModel.getErreurLecture()) {
			if (figureModel != null) {
				BDDUtilities.initConnection(dbPath);
				BDDUtilities.checkPaths();
				boolean[] options = new boolean[] { modelisationator.drawPoints, modelisationator.drawSegments,
						modelisationator.drawFaces, modelisationator.reset, modelisationator.fill };
				MainFenetre mainFrame = new MainFenetre(figureModel, options);
				mainFrame.setTitle("Modelisationator");
				figureModel.setProjection(new Vecteur(new double[] { 0, 0, -1 }));
				if (!quiet) {
					mainFrame.setVisible(true);
				}
				return new BasicResult(BasicResultEnum.ALL_OK);
			} else {
				return new BasicResult(BasicResultEnum.UNKNOWN_ERROR);
				// TODO errors
				// return figureModel.getLectureResult();
			}
		} else if (modelisationator.executeDB) {
			boolean options[] = new boolean[] { modelisationator.reset, modelisationator.fill };
			if (modelisationator.delete) {
				modelisationator.strategy = new ExecuteStrategy();
				return modelisationator.strategy.treatArguments(args, dbPath, new boolean[] { options[0], options[1], quiet })
						.getMethodResult();
			} else {
				BDDUtilities.initConnection(dbPath);
				BDDUtilities.checkPaths();
				MainFenetre mainFrame = new MainFenetre(args, options);
				mainFrame.setTitle("Modelisationator");
				if (!quiet) {
					mainFrame.setVisible(true);
				}
				return new BasicResult(BasicResultEnum.ALL_OK);
			}
		}
		return new BasicResult(BasicResultEnum.UNKNOWN_ERROR);
	}

	/**
	 * Vérifie un seul argument commentcant par un "-".
	 * 
	 * @param arg l'agument à vérifier.
	 * @param modelisationator objet instancié qui contient les booleans options pour ce lancement
	 * @param quiet true pour empecher affichage.
	 * @return si l'argument correspond aux options de lancement 3D.
	 */
	private static MethodResult verif3Darg(String arg, Modelisationator modelisationator, boolean quiet) {
		for (int j = 1; j < arg.length(); j++) { // start comparing after "-"
			char c = arg.charAt(j);
			if (c == 'f') {
				modelisationator.drawFaces = true;
				modelisationator.found3DOptions = true;
			} else if (c == 's') {
				modelisationator.drawSegments = true;
				modelisationator.found3DOptions = true;
			} else if (c == 'p') {
				modelisationator.drawPoints = true;
				modelisationator.found3DOptions = true;
			} else {
				if (!quiet) {
					System.out.println("Erreur : Paramètre non reconnu : " + arg);
				}
				return new BasicResult(BasicResultEnum.UNKNOWN_ARG);
			}
		}
		return new BasicResult(BasicResultEnum.ALL_OK);
	}

	/**
	 * Vérifie un seul argument destiné pour la base de données.
	 * 
	 * @param arg l'agument à vérifier.
	 * @param modelisationator objet instancié qui contient les booleans options pour ce lancement
	 * @param quiet true pour empecher affichage.
	 * @return si l'argument correspond aux options BDD.
	 */
	private static MethodResult verifDBArg(String arg, Modelisationator modelisationator, boolean quiet) {
		if (arg.equalsIgnoreCase("--reset")) {
			modelisationator.reset = true;
		} else if (arg.equalsIgnoreCase("--fill")) {
			modelisationator.fill = true;
		} else if (arg.equals("--delete")) {
			modelisationator.delete = true;
		} else if (!BDDUtilities.isExecutableArg(arg)) { // if not a direct command for the db, check for single letter
															// options
			for (int j = 2; j < arg.length(); j++) { // start comparing after "--"
				char c = arg.charAt(j);
				if (c == 'r') {
					modelisationator.reset = true;
				} else if (c == 'f') {
					modelisationator.fill = true;
				} else {
					if (!quiet) {
						System.out.println("Erreur : Paramètre non reconnu : " + arg);
					}
					return new BasicResult(BasicResultEnum.UNKNOWN_ARG);
				}
			}
		}
		return new BasicResult(BasicResultEnum.ALL_OK);
	}

}
