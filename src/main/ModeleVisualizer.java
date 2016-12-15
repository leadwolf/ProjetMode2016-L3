package main;

import java.awt.Dimension;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;

import main.vues.MainFenetre;
import main.vues.ModelPanel;
import ply.plyModel.modeles.FigureModel;
import result.BasicResult;
import result.BasicResultEnum;
import result.MethodResult;

public class ModeleVisualizer {


	private static boolean drawFaces = true;
	private static boolean drawSegments = false;
	private static boolean drawPoints = false;
	private static Path plyPath;
	/**
	 * Prend en compte les choix des options 3D et puis éxécute la présentation du fichier .ply
	 * @param args
	 * @param noPrint true si pas d'affichage
	 * @return soit un erreur dans les arguments, soit {@link #executePly(String[], boolean)}
	 */
	public static MethodResult execute3Dargs(String[] args, boolean noPrint) {
		String firstArg = args[0];
		for (int j = 1; j < firstArg.length(); j++) { // start comparing after "-"
			char c = firstArg.charAt(j);
			if (c == 'f') {
				drawFaces = true;
			} else if (c == 's') {
				drawSegments = true;
			} else if (c == 'p') {
				drawPoints = true;
			} else {
				if (!noPrint) {
					System.out.println("Paramètre non reconnu : " + firstArg);
				}
				return new BasicResult(BasicResultEnum.UNKNOWN_ARG);
			}
		}
		return executePly(args, noPrint);
	}
	
	/**
	 * Stocke le path vers le fichier .ply et éxécute la fenêtre a la fin si trouvé;
	 * @param args les arguments dans lesquelles trouver le path
	 * @param noPrint true si pas d'affichage
	 * @return soit des fichiers multiples, soit l'erreur lors de la lecture de fichier .ply ou ALL_OK
	 */
	public static MethodResult executePly(String[] args, boolean noPrint) {/* CHECK FILE */
		boolean foundFile = false;
		for (int i=0;i<args.length;i++) {
			String extension = args[i].substring(args[i].lastIndexOf(".") + 1, args[i].length());
			if (extension != null && extension.equals("ply")) {
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
		if (!foundFile) {
			if (!noPrint) {
				System.out.println("Vous avez tenté d'éxécuter une commande 3D mais vous n'avez pas spécifié de fichier .ply");
			}
			return new BasicResult(BasicResultEnum.NO_PLY_FILE_IN_ARG);
		} else {
			FigureModel figureModel = new FigureModel(plyPath, false);
			if (figureModel != null && !figureModel.getErreurLecture()) {
				MainFenetre mainFrame = new MainFenetre(figureModel, drawPoints, drawSegments, drawFaces);
				mainFrame.setTitle("Modelisationator");
				return new BasicResult(BasicResultEnum.ALL_OK);
			} else {
				return figureModel.getLecture().getResult();
			}
		}
	}
}
