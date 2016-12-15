package ply;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import ply.modeles.FigureModel;
import ply.modeles.Point;
import ply.vues.Fenetre;
import result.BasicResult;
import result.BasicResultEnum;
import result.MethodResult;

public class Main {

	private static boolean paramsOK = true;
	private static boolean found3Dcommand;
	private static boolean foundDBcommand;

	private static boolean fileChosen = false;
	private static boolean drawPoints = false;
	private static boolean drawSegments = false;
	private static boolean drawFaces = true;
	private static boolean displayOption = false;

	private static Path path = null;

	public static void main(String[] args) {

		Fenetre fen = null;
		FigureModel figureModel = null;

		/* PARSE ARGS */
		if (args.length > 0) {
			int i = 0;
			// tant que pas arrivé à la fin et qu'une commande n'a pas été trouvé
			while (i<args.length && (!found3Dcommand || !foundDBcommand)) {
				String extension = args[i].substring(args[i].lastIndexOf(".") + 1, args[i].length());

				if (args[i].startsWith("-")) { // if found parameter starting with "-"
					found3Dcommand = true;
					if (!foundDBcommand) {
						parse3DArg(args[i]);
					} else {
						System.out.println("Vous ne pouvez pas lancer une commande 3D en meme temps qu'une commande BDD.");
					}
				} else if (args[i].startsWith("--")) {
					// des qu'on trouve une commande DB, on vérifie tous les arguments passés
					foundDBcommand = true;
					if (!found3Dcommand) {
						parseDBArgs(args, false);
					} else {
						System.out.println("Vous ne pouvez pas lancer une commande BDD en meme temps qu'une commande 3D.");
					}
				} else if (extension != null && extension.equals("ply")) { // didnt find "-"
					parseFile(args[i]);
				} else {
//					DO NOT DO ANYTHING BECAUSE EITHER A MODEL NAME OR MODEL FILE COULD BE SPECIFIED
//					paramsOK = false;
//					System.out.println("Paramètre non reconnu : " + args[i]);
				}
				i++;
			}
		} else {
			System.out.println("Aucun paramètre précisé");
		}

		if (paramsOK && fileChosen) {
			figureModel = new FigureModel(path, false);
			if (figureModel != null && !figureModel.getErreurLecture()) {
				fen = new Fenetre(figureModel, drawPoints, drawSegments, drawFaces);
				fen.setVisible(true);
				fen.initModelForWindow();
			}
		} else if (paramsOK && !fileChosen) {
			// TODO
			// execute BDD part
		}
	}

	/**
	 * @param args
	 * @param debug true si veut empecher affichage
	 * @return
	 */
	private static MethodResult parseDBArgs(String[] args, boolean debug) {
		MethodResult testArgs = verifArgs(args);
		if (!testArgs.getCode().equals(BasicResultEnum.ALL_OK)) {
			if (!debug) {
				String message = "Vous avez spécifié deux commandes incompatibles.";
				JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			}
			return testArgs;
		} // else continue program
		return testArgs;
	}

	/**
	 * Verifie seuelement si la syntaxe des arguments sont corrects, pas si la commande a pu être éxecutée
	 * 
	 * @param args
	 * @return un {@link MethodResult} décrivant la validité des arguments
	 */
	private static MethodResult verifArgs(String[] args) {
		if (args[0].equals("--name")) {
			for (int i = 1; i < args.length; i++) {
				if (args[i].startsWith("--")) {
					return new BasicResult(BasicResultEnum.CONFLICTING_ARGUMENTS);
				}
			}
			return new BasicResult(BasicResultEnum.ALL_OK);
		} else if (args[0].equals("--all")) {
			for (int i = 1; i < args.length; i++) {
				if (args[i].startsWith("--")) {
					return new BasicResult(BasicResultEnum.CONFLICTING_ARGUMENTS);
				}
			}
			return new BasicResult(BasicResultEnum.ALL_OK);
		} else if (args[0].equals("--find")) {
			for (int i = 1; i < args.length; i++) {
				if (args[i].startsWith("--")) {
					return new BasicResult(BasicResultEnum.CONFLICTING_ARGUMENTS);
				}
			}
			return new BasicResult(BasicResultEnum.ALL_OK);
		} else if (args[0].equals("--add")) {
			for (int i = 1; i < args.length; i++) {
				if (args[i].startsWith("--")) {
					return new BasicResult(BasicResultEnum.CONFLICTING_ARGUMENTS);
				}
			}
			return new BasicResult(BasicResultEnum.ALL_OK);
		} else if (args[0].equals("--delete")) {
			for (int i = 1; i < args.length; i++) {
				if (args[i].startsWith("--")) {
					return new BasicResult(BasicResultEnum.CONFLICTING_ARGUMENTS);
				}
			}
			return new BasicResult(BasicResultEnum.ALL_OK);
		} else if (args[0].equals("--edit")) {
			for (int i = 1; i < args.length; i++) {
				if (args[i].startsWith("--")) {
					return new BasicResult(BasicResultEnum.CONFLICTING_ARGUMENTS);
				}
			}
			return new BasicResult(BasicResultEnum.ALL_OK);
		}
		return new BasicResult(BasicResultEnum.CONFLICTING_ARGUMENTS);
	}
	
	/**
	 * Stocke le string path donné en argument dans le path de cette classe.
	 * @param arg
	 */
	private static void parseFile(String arg) {
		if (!fileChosen) {
			path = Paths.get(arg);
			fileChosen = true;
		} else {
			paramsOK = false;
			System.out.println("Erreur : Fichiers multiples");
		}
	}

	/**
	 * Parses single String corresponding to a 3D command
	 * 
	 * @param arg
	 */
	private static void parse3DArg(String arg) {
		for (int j = 1; j < arg.length(); j++) { // start comparing after "-"
			char c = arg.charAt(j);
			if (c == 'f') {
				drawFaces = true;
				displayOption = true;
			} else if (c == 's') {
				drawSegments = true;
				displayOption = true;
			} else if (c == 'p') {
				displayOption = true;
				drawPoints = true;
			} else {
				paramsOK = false;
				System.out.println("Paramètre non reconnu : " + arg);
			}
		}
	}
}
