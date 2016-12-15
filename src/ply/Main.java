package ply;
import java.nio.file.Path;
import java.nio.file.Paths;

import ply.modeles.FigureModel;
import ply.modeles.Point;
import ply.vues.Fenetre;

public class Main {
	
	
	public static void main(String[] args) {

		Fenetre fen = null;
		Path path = null;
		FigureModel figureModel = null;
		boolean fileChosen = false;
		boolean paramsOK = true;
		boolean drawPoints = false, drawSegments = false, drawFaces = false;
		boolean displayOption = false;

		/* PARSE ARGS */
		if (args.length > 0) {
			for (int i=0;i<args.length;i++) {
				String extension = args[i].substring(args[i].lastIndexOf(".") + 1, args[i].length());
				if (args[i].equals("-f")) {
					if (!displayOption) {
						drawFaces = true;
						displayOption = true;
					} else {
						paramsOK = false;
						System.out.println("Erreur : Paramètres d'affichages contradictoires");
					}
				} else if (args[i].equals("-s")) {
					if (!displayOption) {
						drawSegments = true;
						displayOption = true;
					} else {
						paramsOK = false;
						System.out.println("Erreur : Paramètres d'affichages contradictoires");
					}
				} else if (extension.equals("ply")) {
					if (!fileChosen) {
						path = Paths.get(args[i]);
						fileChosen = true;
					} else {
						paramsOK = false;
						System.out.println("Erreur : Fichiers multiples");
					}
				} else {
					paramsOK = false;
					System.out.println("Paramètre non reconnu");
				}
			}
			if (!displayOption) {
				drawFaces = true;
				drawSegments = false;
			}
			if (paramsOK) {
				figureModel = new FigureModel(path, false);
			}
		} else {
			System.out.println("Aucun fichier précisé");
		}
		
		/* EXECUTE */
		if (paramsOK && figureModel != null && !figureModel.getErreurLecture()) {
			fen = new Fenetre(figureModel, drawPoints, drawSegments, drawFaces);
			fen.setVisible(true);
			fen.initModelForWindow();
		}
	}
}
