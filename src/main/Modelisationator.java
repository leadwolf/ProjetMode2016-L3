package main;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import main.vues.MainFenetre;
import ply.plyModel.modeles.FigureModel;
import result.BDDResult;
import result.BDDResultEnum;
import result.BasicResult;
import result.BasicResultEnum;
import result.MethodResult;

public class Modelisationator {

	private static boolean drawFaces = true;
	private static boolean drawSegments = false;
	private static boolean drawPoints = false;
	private static boolean reset = false;
	private static boolean fill = false;
	private static Path plyPath;

	private static boolean executeDB = false;
	private static boolean foundFile = false;
	private static boolean found3DOptions = false;
	
	private static Pattern singleMinus = Pattern.compile("^(\\-)\\w+");
	private static Pattern doubleMinus = Pattern.compile("^(\\-\\-)\\w+");

	public static void main(String[] args) {
		parseArgs(args, false);
	}

	/**
	 * Méthode principale pour éxécuter le programme en fonction des arguments
	 * 
	 * @param args
	 * @param noPrint
	 * @return soit un erreur dans les arguments, soit le résultat de l'éxécution de la méthode pertinent aux arguments
	 */
	public static MethodResult parseArgs(String[] args, boolean noPrint) {
		if (args.length > 0) {
			// CHECK THE FIRST ARGUMENT, IT CAN EITHER BE
			// 1) 3D OPTIONS
			// 2) BDD OPTION
			// 3) .PLY FILE
			MethodResult verifArgsResult = verifArgs(args, false);
			if (verifArgsResult.getCode().equals(BasicResultEnum.ALL_OK)) {
				return execute(args, false);
			}
		} else {
			if (!noPrint) {
				System.out.println("Aucun paramètre précisé");
			}
			return new BasicResult(BasicResultEnum.NO_ARGUMENTS);
		}
		return null;
	}

	/**
	 * Vérifie tous les options qu'ils soient pour un modèle 3D ou une commande bdd
	 * @param args
	 * @param noPrint
	 * @return
	 */
	private static MethodResult verifArgs(String[] args, boolean noPrint) {
		for (int i = 0; i < args.length; i++) {
			String extension = args[i].substring(args[i].lastIndexOf(".") + 1, args[i].length());
			if (isExecutableArg(args[i])) {
				executeDB = true;
			}
			if (singleMinus.matcher(args[i]).find()) {
				MethodResult plyResult = parse3Dargs(args[i], noPrint);
				if (!plyResult.getCode().equals(BasicResultEnum.ALL_OK)) {
					return plyResult;
				}
			} else if (doubleMinus.matcher(args[i]).find()) {
				MethodResult dbResult = parseDBArg(args[i], noPrint);
				if (!dbResult.getCode().equals(BasicResultEnum.ALL_OK)) {
					return dbResult;
				}
			} else if (extension.equals("ply")) {
				if (!foundFile) {
					foundFile = true;
					plyPath = Paths.get(args[i]);
				} else {
					if (!noPrint) {
						System.out.println("Vous avez spécifié deux fichiers ply");
					}
					return new BasicResult(BasicResultEnum.MULTIPLE_PLY_ARGS);
				}
			}
		}
		if (found3DOptions && !foundFile) {
			if (!noPrint) {
				System.out.println("Vous avez tenté d'éxécuter une commande 3D mais vous n'avez pas spécifié de fichier .ply");
			}
			return new BasicResult(BasicResultEnum.NO_PLY_FILE_IN_ARG);
		}
		return new BasicResult(BasicResultEnum.ALL_OK);
	}
	
	/**
	 * Crée le {@link MainFenetre} adapté.
	 * @param args
	 * @param noPrint
	 * @return
	 */
	private static MethodResult execute(String[] args, boolean noPrint) {
		if (foundFile) {
			FigureModel figureModel = new FigureModel(plyPath, false);
			if (figureModel != null && !figureModel.getErreurLecture()) {
				boolean[] options = new boolean[] { drawPoints, drawSegments, drawFaces, reset, fill };
				MainFenetre mainFrame = new MainFenetre(figureModel, options);
				mainFrame.setTitle("Modelisationator");
				return new BasicResult(BasicResultEnum.ALL_OK);
			} else {
				return figureModel.getLectureResult();
			}
		} else if (executeDB) {
			boolean options[] = new boolean[] { reset, fill };
			MainFenetre mainFrame = new MainFenetre(args, options);
			mainFrame.setTitle("Modelisationator");
			return new BasicResult(BasicResultEnum.UNKNOWN_ERROR);
		}
		return new BasicResult(BasicResultEnum.UNKNOWN_ERROR);
	}
	
	/**
	 * Vérifie un argument destiné pour un modèle
	 * @param arg
	 * @param noPrint
	 * @return
	 */
	private static MethodResult parse3Dargs(String arg, boolean noPrint) {
		for (int j = 1; j < arg.length(); j++) { // start comparing after "-"
			char c = arg.charAt(j);
			if (c == 'f') {
				drawFaces = true;
				found3DOptions = true;
			} else if (c == 's') {
				drawSegments = true;
				found3DOptions = true;
			} else if (c == 'p') {
				drawPoints = true;
				found3DOptions = true;
			} else {
				if (!noPrint) {
					System.out.println("Paramètre non reconnu : " + arg);
				}
				return new BasicResult(BasicResultEnum.UNKNOWN_ARG);
			}
		}
		return new BasicResult(BasicResultEnum.ALL_OK);
	}
	
	/**
	 * Vérifie un argument destiné pour la base de données
	 * @param arg
	 * @param noPrint
	 * @return
	 */
	private static MethodResult parseDBArg(String arg, boolean noPrint) {
		if (arg.equalsIgnoreCase("--reset")) {
			reset = true;
		} else if (arg.equalsIgnoreCase("--fill")) {
			fill = true;
		} else if (!isExecutableArg(arg)) { // if neither a command to reset/fill db, neither a direct command for the db, check for single letter options 
			for (int j = 2; j < arg.length(); j++) { // start comparing after "--"
				char c = arg.charAt(j);
				if (c == 'r') {
					reset = true;
				} else if (c == 'f') {
					fill = true;
				} else {
					if (!noPrint) {
						System.out.println("Paramètre non reconnu : " + arg);
					}
					return new BasicResult(BasicResultEnum.UNKNOWN_ARG);
				}
			}
		}
		if (reset && !fill) {
			if (!noPrint) {
				System.out.println("Vous avez précisé de vider la base sans la ré-remplir.");
			}
			return new BDDResult(BDDResultEnum.EMPTY_DB);
		} else if (fill && !reset) {
			if (!noPrint) {
				System.out.println("Vous avez précisé de remplir la base sans qu'elle soit vide au départ.");
			}
			return new BDDResult(BDDResultEnum.INCORRECT_FILL);
		}
		return new BasicResult(BasicResultEnum.ALL_OK);
	}
	
	/**
	 * @param arg
	 * @return si l'arg correspond à une option d'éxécution de bdd
	 */
	public static boolean isExecutableArg(String arg) {
		return arg.equals("--name") || arg.equals("--all") || arg.equals("--find") || arg.equals("--add") || arg.equals("--delete") || arg.equals("--edit");
	}

}
