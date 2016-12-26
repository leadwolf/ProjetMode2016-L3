package ply.bdd.other;

import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import main.vues.MainFenetre;
import ply.bdd.legacy.FenetreTable;
import ply.bdd.vues.BDDPanel;
import result.BDDResult;
import result.BDDResultEnum;
import result.BasicResult;
import result.BasicResultEnum;
import result.MethodResult;

/**
 * Cette classe est pareil que BaseDeDonnesOld mais donne une JPanel au lieu d'éxécuter directement les commandes/ Elle ne contient plus les méthodes de
 * manipulation de base, qui sont maintenant dans {@link BDDUtilities}
 * 
 * @author L3
 *
 */
public class BaseDeDonneesNew {

	private static String[] columnNames = new String[] { "Nom", "Chemin", "Date", "Mot Clés", "Nombre de Points", "Nombre de Faces" };
	private static String[] buttonColumns = new String[] { "Confirmer insertion/edition", "Reset", "Supprimer" };
	private static String[] primaryButtons = new String[] { "Ajouter une ligne", "Reset" };
	/**
	 * Le nombre de mots clés qu'on peut chercher avec --find.
	 */
	private static int nbKeywordsLimit = 10;

	/**
	 * Crée un {@link BDDPanel} d'après la commande dans args.
	 * 
	 * @param args
	 * @param dbPath path to the db.sqlite, leave null for default data/test.sqlite
	 * @param mainFenetre
	 * @param options [0] = reset, [1] = fill, [2] = quiet true pour empecher affichage
	 * @return le panel correspondant à la commande.
	 */
	public static BDDPanel getPanel(String[] args, Path dbPath, MainFenetre mainFenetre, boolean[] options) {
		// VERIF ARGS
		MethodResult testArgs = verifArgs(args, options[2]);
		if (!testArgs.getCode().equals(BasicResultEnum.ALL_OK)) {
			return null;
		} // else continue program

		initConnection(dbPath, options[0], options[1]);
		MethodResult checkResult = BDDUtilities.checkTable();
		if (checkResult.getCode().equals(BDDResultEnum.DB_NOT_EMPTY)) {
			return parseArgsForPanel(args, mainFenetre);
		} else {
			if (!options[2]) {
				JOptionPane.showMessageDialog(null, "La base de données est vide.", "Modelisationator", JOptionPane.ERROR_MESSAGE);
			}
			return null;
		}
	}

	/**
	 * Execute une commande bdd sans interface. Vérifie que la base comporte des modèles et que le modèle à supprimer existe.
	 * 
	 * @param args
	 * @param dbPath path to the db.sqlite, leave null for default data/test.sqlite
	 * @param options [0] = reset, [1] = fill, [2] = quiet true pour empecher affichage
	 * @return le résultat de l'éxécution de la commande ou l'erreur empechant celle ci.
	 */
	public static MethodResult executeCommand(String[] args, Path dbPath, boolean[] options) {
		// VERIF ARGS
		MethodResult verifResult = verifArgs(args, options[2]);
		if (!verifResult.getCode().equals(BasicResultEnum.ALL_OK)) {
			return verifResult;
		}

		initConnection(dbPath, options[0], options[1]);
		MethodResult checkResult = BDDUtilities.checkTable();
		if (checkResult.getCode().equals(BDDResultEnum.DB_NOT_EMPTY)) {
			return delete(args, options[2]);
		} else {
			if (!options[2]) {
				JOptionPane.showMessageDialog(null, "La base de données est vide. Impossible de supprimmer un modèle.", "Erreur",
						JOptionPane.ERROR_MESSAGE);
			}
			return checkResult;
		}
	}

	/**
	 * Initalise la connection, recree la table si besoin.
	 * 
	 * @param dbPath
	 * @param reset
	 * @param fill
	 */
	private static void initConnection(Path dbPath, boolean reset, boolean fill) {
		BDDUtilities.initConnection(dbPath);
		if (reset) {
			BDDUtilities.resetTable();
		}
		if (fill) {
			BDDUtilities.fillTable();
		}
		BDDUtilities.setColumnInfo();
	}

	/**
	 * Verifie s'il y a une seule commande et qu'elle est bien écrite. Affiche l'erreur si !quiet.
	 * 
	 * @param args les arguments passés au programme.
	 * @param quiet true pour empêcher affichge.
	 * @return un {@link MethodResult} décrivant la validité des arguments.
	 */
	public static MethodResult verifArgs(String[] args, boolean quiet) {

		if (args == null || (args != null && args.length <= 0)) {
			if (!quiet) {
				JOptionPane.showMessageDialog(null, "Vous n'avez pas donné de commande", "Modelisationator", JOptionPane.ERROR_MESSAGE);
			}
			return new BasicResult(BasicResultEnum.NO_ARGUMENTS);
		}

		boolean foundExecutableArg = false; // switch to verify we only have one command.
		boolean findCommand = false; // si la commande est --find
		int normalStringsFound = 0; // the counter of how many normal strings have been found (Strings in args that are not a command and not an option).
		int normalStringsNeeded = 0; // the counter of how many normal strings should be found

		for (int i = 0; i < args.length; i++) {
			boolean currrentIsExecutable = isExecutableArg(args[i]);
			if (currrentIsExecutable) {
				if (!foundExecutableArg) { // si on a n'a pas encore trouvé une commande, vérifier cette commande à l'emplacement [i].
					foundExecutableArg = true;
					if (args[i].equals("--name")) {
						normalStringsNeeded = 1;
					} else if (args[i].equals("--all")) {
						// no need to verify since the enclosing for verifies multiple commands.
					} else if (args[i].equals("--find")) {
						findCommand = true;
						normalStringsNeeded = nbKeywordsLimit; // limit find to 10;
					} else if (args[i].equals("--add")) {
						// no need to verify since the enclosing for verifies multiple commands.
					} else if (args[i].equals("--delete")) {
						normalStringsNeeded = 1;
					} else if (args[i].equals("--edit")) {
						normalStringsNeeded = 1;
					}
				} else { // on a trouvé plus qu'une commande.
					if (!quiet) {
						JOptionPane.showMessageDialog(null, "Vous n'avez précisé plusieurs commandes incompatibles.\nVeuillez réessayer.",
								"Modelisationator", JOptionPane.ERROR_MESSAGE);
					}
					return new BasicResult(BasicResultEnum.CONFLICTING_ARGUMENTS);
				}
			} else if (!currrentIsExecutable && !isDBOption(args[i])) {
				normalStringsFound++;
			}
		}
		if (!findCommand && normalStringsFound != normalStringsNeeded) {
			if (!quiet) {
				JOptionPane.showMessageDialog(null, "Vous n'avez pas précisé de modèle", "Modelisationator", JOptionPane.ERROR_MESSAGE);
			}
			return new BDDResult(BDDResultEnum.NAME_NOT_SPECIFIED);
		} else if (findCommand && normalStringsFound > normalStringsNeeded) {
			if (!quiet) {
				JOptionPane.showMessageDialog(null, "Vous avez mis plus de mots clés que la limite.\nLa limite est de " + nbKeywordsLimit, "Erreur",
						JOptionPane.ERROR_MESSAGE);
			}
			return new BDDResult(BDDResultEnum.TOO_MANY_KEYWORDS_SPECIFIED);
		}
		return new BasicResult(BasicResultEnum.ALL_OK);
	}

	/**
	 * @param arg l'agument à vérifier.
	 * @return si l'arg correspond à une option d'<b>éxécution</b> de bdd.
	 */
	public static boolean isExecutableArg(String arg) {
		return arg.equals("--name") || arg.equals("--all") || arg.equals("--find") || arg.equals("--add") || arg.equals("--delete")
				|| arg.equals("--edit");
	}

	/**
	 * @param arg
	 * @return si c'est une option (--rf) et non une commande à lancer (--all/--find/...).
	 */
	private static boolean isDBOption(String arg) {
		return arg.equals("--r") || arg.equals("--r") || arg.equals("--rf");
	}

	/**
	 * Récupère les données pertientes pour l'affichage en question.
	 * 
	 * @param args la commande de l'utlisateur
	 * @param mainFenetre
	 * @param quiet true pour empecher affichage
	 * @return si la requête était correcte et que l'interface, si besoin, a été éxecutée
	 */
	private static BDDPanel parseArgsForPanel(String[] args, MainFenetre mainFenetre) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--name")) {
				return showName(args, mainFenetre);
			}
			if (args[i].equals("--all")) {
				return showAll(mainFenetre);
			}
			if (args[i].equals("--find")) {
				return find(args, mainFenetre);
			}
			if (args[i].equals("--add")) {
				return add(mainFenetre);
			}
			if (args[i].equals("--edit")) {
				return edit(args, mainFenetre);
			}
		}
		return null;
	}

	/**
	 * Donne toutes les colonnes de la base de données
	 * 
	 * @param name the name of the model
	 * @param quiet true pour empecher affichage.
	 * @return un String[] contentant toute la ligne de la base ou null s'il y a eu erreur.
	 */
	public static String[] getNameInfo(String name, boolean quiet) {
		ResultSet rs;
		try {
			name = name.toLowerCase();
			PreparedStatement st = BDDUtilities.getConnection().prepareStatement("select * from PLY where NOM = ?");
			st.setString(1, name);
			rs = st.executeQuery();
			if (rs.next()) {
				String[] nameInfo = new String[rs.getMetaData().getColumnCount()];
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					nameInfo[i - 1] = rs.getString(i);
				}
				BDDUtilities.closeConnection();
				return nameInfo;
			} else {
				if (!quiet) {
					JOptionPane.showMessageDialog(null, "Le modèle " + name + " n'est pas dans la base", "Modelisationator",
							JOptionPane.ERROR_MESSAGE);
				}
				BDDUtilities.closeConnection();
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Execute la requête donnant les informations sur le modèle et crée le panel concerné.
	 * 
	 * @param i la place de "--name" dans les arguments. Servira à parcourir la liste si on aura besoin d'afficher de multiples modèles
	 * @param args le modèle à afficher
	 * @param mainFenetre
	 * @return le {@link BDDPanel} contenant les données du modèle précisé ou null.
	 */
	private static BDDPanel showName(String[] args, MainFenetre mainFenetre) {
		ResultSet rsCount;
		ResultSet rs;
		try {
			String modelName = "";
			for (int i = 0; i < args.length; i++) {
				if (!isDBOption(args[i]) && !isExecutableArg(args[i])) {
					modelName = args[i];
				}
			}
			PreparedStatement statement = BDDUtilities.getConnection().prepareStatement("select * from PLY where NOM = ?");
			statement.setString(1, modelName);
			rsCount = statement.executeQuery();
			if (rsCount.next()) {
				PreparedStatement statement2 = BDDUtilities.getConnection().prepareStatement("select * from PLY where NOM = ?");
				statement2.setString(1, modelName);
				rs = statement2.executeQuery();
				BDDUtilities.closeConnection();
				BDDPanel result = new BDDPanel(mainFenetre, rs, columnNames, buttonColumns, primaryButtons);
				result.setEditableColumns(new int[] { 0, 1, 3 }, true);
				return result;
			} else {
				String message = "Le modèle " + modelName + " n'existe pas\nUtilisation: basededonneés --name <name>";
				JOptionPane.showMessageDialog(null, message, "Modelisationator", JOptionPane.ERROR_MESSAGE);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Vérifie les arguments et crée la fenetre listant toute la base
	 * 
	 * @param mainFenetre
	 * @return s'il a pu créer la fenêtre
	 */
	private static BDDPanel showAll(MainFenetre mainFenetre) {
		ResultSet rs;
		// no need for statement to check because its done before this method is executed
		try {
			PreparedStatement st = BDDUtilities.getConnection().prepareStatement("select * from PLY");
			rs = st.executeQuery();
			BDDPanel result = new BDDPanel(mainFenetre, rs, columnNames, buttonColumns, primaryButtons);
			result.setEditableColumns(new int[] { 0, 1, 3 }, true);
			BDDUtilities.closeConnection();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		BDDUtilities.closeConnection();
		return null;
	}

	/**
	 * Vérifie les arguments et puis crée la fenetre listant les informations des modèles correspondants aux keywords
	 * 
	 * @param args les mots clés à rechercher
	 * @param mainFenetre
	 * @return le {@link BDDPanel} contenant les modèles ayant les mots cles ou null.
	 */
	private static BDDPanel find(String[] args, MainFenetre mainFenetre) {
		ResultSet rsCount;
		ResultSet rs;
		int nbLikes = 0;
		int nbTreated = 0;
		for (int j = 0; j < args.length; j++) {
			if (!args[j].startsWith("--")) {
				nbLikes++;
			}
		}
		if (nbLikes > 0) {
			try {
				String queryString = "select * from PLY where DESCRIPTION like";
				String queryCount = "select * from PLY where DESCRIPTION like";
				for (int j = 0; j < args.length; j++) {
					if (!isDBOption(args[j])) {
						nbTreated++;
						if (nbTreated == 1) {
							queryString += " ?";
							queryCount += " ?";
						} else {
							if (nbTreated < nbLikes) {
								queryString += " union select * from PLY where DESCRIPTION LIKE ?,";
							} else {
								queryString += " union select * from PLY where DESCRIPTION LIKE ?";
							}
							queryCount += " OR DESCRIPTION LIKE ?";
						}
					}
				}
				PreparedStatement stCount = BDDUtilities.getConnection().prepareStatement(queryCount);
				PreparedStatement statement2 = BDDUtilities.getConnection().prepareStatement(queryString);
				for (int j = 0; j < args.length; j++) {
					if (!args[j].startsWith("--")) {
						stCount.setString(j, "%" + args[j] + "%");
						statement2.setString(j, "%" + args[j] + "%");
					}
				}
				rsCount = stCount.executeQuery();
				if (rsCount.next()) {
					rs = statement2.executeQuery();
					BDDPanel result = new BDDPanel(mainFenetre, rs, columnNames, buttonColumns, primaryButtons);
					result.setEditableColumns(new int[] { 0, 1, 3 }, true);
					BDDUtilities.closeConnection();
					return result;
				} else {
					String message = "Aucun modèle trouvé comportant ces mot clés";
					JOptionPane.showMessageDialog(null, message, "Modelisationator", JOptionPane.ERROR_MESSAGE);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			String message = "Pas de mots clés spécifiés\nUtilisation: basededonneés --find <mots clés>..";
			JOptionPane.showMessageDialog(null, message, "Modelisationator", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

	/**
	 * Crée la fenêtre d'insertion de modèle.
	 * 
	 * @param mainFenetre
	 * @return si fenêtre crée. Pour savoir si requête sql éxecutée, voir {@link FenetreTable}
	 */
	private static BDDPanel add(MainFenetre mainFenetre) {
		BDDPanel result = new BDDPanel(mainFenetre, null, columnNames, buttonColumns, primaryButtons);
		result.setCanAddRow(false);
		result.setEditable(true); // tout est editable dans --edit
		return result;
	}

	/**
	 * Vérifie les argumuments et puis crée la fenetre de modification du modèle précisé s'ils sont valides
	 * 
	 * @param args le modèle à éditer
	 * @param mainFenetre
	 * @return si fenêtre bien crée. Pour savoir si requête sql éxecutée, voir {@link FenetreTable}
	 */
	private static BDDPanel edit(String[] args, MainFenetre mainFenetre) {
		try {
			String modelName = "";
			for (int i = 0; i < args.length; i++) {
				if (!isDBOption(args[i]) && !isExecutableArg(args[i])) {
					modelName = args[i];
				}
			}
			PreparedStatement stCount = BDDUtilities.getConnection().prepareStatement("select * from PLY where NOM = ?");
			stCount.setString(1, modelName);
			ResultSet rsCount = stCount.executeQuery();
			if (rsCount.next()) {
				PreparedStatement st = BDDUtilities.getConnection().prepareStatement("select * from ply where nom = ?");
				st.setString(1, modelName);
				ResultSet rs = st.executeQuery();
				BDDPanel result = new BDDPanel(mainFenetre, rs, columnNames, buttonColumns, primaryButtons);
				result.setEditable(true); // tout est editable dans --edit
				BDDUtilities.closeConnection();
				return result;
			} else {
				String message = "Le modèle " + modelName + " n'existe pas";
				JOptionPane.showMessageDialog(null, message, "Modelisationator", JOptionPane.ERROR_MESSAGE);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Vérifie que le modèle existe dans la base et la supprime.
	 * 
	 * @param args le modèle à supprimer
	 * @param quiet afficher ou non les fenêtres
	 * @return un {@link MethodResult} décrivant le résultat de la requête ou si le modèle existait pas.
	 */
	private static MethodResult delete(String[] args, boolean quiet) {
		try {
			String modelName = "";
			for (int i = 0; i < args.length; i++) {
				if (!isDBOption(args[i]) && !isExecutableArg(args[i])) {
					modelName = args[i];
				}
			}
			PreparedStatement stExists = BDDUtilities.getConnection().prepareStatement("select * from PLY where NOM = ?");
			stExists.setString(1, modelName);
			ResultSet rs = stExists.executeQuery();
			if (rs.next()) {
				PreparedStatement stDelete = BDDUtilities.getConnection().prepareStatement("delete from PLY where NOM = ?");
				stDelete.setString(1, modelName);
				int result = stDelete.executeUpdate(); // result = nombre de lignes affectés par le delete
				BDDUtilities.closeConnection();

				if (result > 0) {
					if (!quiet) {
						String message = "Le modèle " + modelName + " a été supprimé avec succès!";
						JOptionPane.showMessageDialog(null, message);
					}
					return new BDDResult(BDDResultEnum.DELETE_SUCCESSFUL);
				}
				if (!quiet) {
					String message = "Le modèle " + modelName + " n'a pas pu être supprimé!";
					JOptionPane.showMessageDialog(null, message);
				}
				return new BDDResult(BDDResultEnum.DELETE_NOT_SUCCESSFUL);
			} else {
				if (!quiet) {
					String message = "Le modèle " + modelName + " n'existe pas";
					JOptionPane.showMessageDialog(null, message, "Modelisationator", JOptionPane.ERROR_MESSAGE);
				}
				BDDUtilities.closeConnection();
				return new BDDResult(BDDResultEnum.MODEL_NOT_FOUND);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new BDDResult(BDDResultEnum.DELETE_NOT_SUCCESSFUL);
	}

}
