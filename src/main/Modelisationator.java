package main;

import java.util.regex.Pattern;

import result.BasicResult;
import result.BasicResultEnum;
import result.MethodResult;

public class Modelisationator {
	
	public static void main(String[] args) {
		parseArgs(args, false);
	}
	
	/**
	 * Méthode principale pour éxécuter le programme en fonction des arguments
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

			Pattern singleMinus = Pattern.compile("^(\\-)\\w+");
			Pattern doubleMinus = Pattern.compile("^(\\-\\-)\\w+");
			String extension = args[0].substring(args[0].lastIndexOf(".") + 1, args[0].length());
			
			if (singleMinus.matcher(args[0]).find()) {
				return ModeleVisualizer.execute3Dargs(args, false);
			} else if (doubleMinus.matcher(args[0]).find()) {
				return BaseDeDonnees.executeDB(args, false, false, false, null);
			} else if (extension != null && extension.equals("ply")) {
				return ModeleVisualizer.executePly(args, false);
			} else {
				if (!noPrint) {
					System.out.println("Argument non reconnu");
				}
				return new BasicResult(BasicResultEnum.UNKNOWN_ARG);
			}
		} else {
			if (!noPrint) {
				System.out.println("Aucun paramètre précisé");
			}
			return new BasicResult(BasicResultEnum.NO_ARGUMENTS);
		}
	}
	
}
