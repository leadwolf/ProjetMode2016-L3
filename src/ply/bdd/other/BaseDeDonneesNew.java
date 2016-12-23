package ply.bdd.other;

import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

import ply.bdd.legacy.FenetreTable;
import ply.bdd.vues.BDDPanelNew;
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

	/**
	 * Creates a BDDPanel from the args given
	 * 
	 * @param args
	 * @param reset
	 * @param fill
	 * @param noPrint
	 * @param dbPath path to the db.sqlite, leave null for default data/test.sqlite
	 * @return
	 */
	public static BDDPanelNew getPanel(String[] args, boolean reset, boolean fill, boolean noPrint, Path dbPath) {
		if ((args == null) || (args != null && args.length <= 0)) {
			if (!noPrint) {
				String message = "Vous n'avez pas spécifié d'arguments.";
				JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			}
			return null;
		}
		// VERIF ARGS
		MethodResult testArgs = verifArgs(args);
		if (!testArgs.getCode().equals(BasicResultEnum.ALL_OK)) {
			if (!noPrint) {
				String message = "Vous avez spécifié deux commandes incompatibles.";
				JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			}
			return null;
		} // else continue program

		BDDUtilities.initConnection(dbPath);
		return parseArgsForPanel(args, reset, fill, noPrint);
	}

	/**
	 * Execute une commande bdd
	 * 
	 * @param args
	 * @param reset
	 * @param fill
	 * @param noPrint
	 * @param dbPath path to the db.sqlite, leave null for default data/test.sqlite
	 * @return
	 */
	public static MethodResult executeCommand(String[] args, boolean reset, boolean fill, boolean noPrint, Path dbPath) {
		if ((args == null) || (args != null && args.length <= 0)) {
			if (!noPrint) {
				String message = "Vous n'avez pas spécifié d'arguments.";
				JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			}
			return null;
		}
		// VERIF ARGS
		MethodResult testArgs = verifArgs(args);
		if (!testArgs.getCode().equals(BasicResultEnum.ALL_OK)) {
			if (!noPrint) {
				String message = "Vous avez spécifié deux commandes incompatibles.";
				JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			}
			return null;
		} // else continue program

		BDDUtilities.initConnection(dbPath);
		return parseArgs(args, reset, fill, noPrint);
	}

	/**
	 * Verifie et execute la commande au lieu de donner un BDDPanel
	 * 
	 * @param args
	 * @param reset
	 * @param fill
	 * @param noPrint
	 * @return
	 */
	private static MethodResult parseArgs(String[] args, boolean reset, boolean fill, boolean noPrint) {
		if (reset) {
			BDDUtilities.resetTable();
		}
		if (fill) {
			BDDUtilities.fillTable();
		}

		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("--delete")) {
					if (BDDUtilities.checkTable().getCode().equals(BasicResultEnum.ALL_OK)) {
						return delete(i, args, noPrint);
					} else {
						if (!noPrint) {
							JOptionPane.showMessageDialog(null, "La base de données est vide", "Base de données", JOptionPane.ERROR_MESSAGE);
						}
						return null;
					}
				}
			}
		}
		return new BasicResult(BasicResultEnum.UNKNOWN_ERROR);
	}

	/**
	 * Verifie seuelement si la syntaxte des arguments sont corrects, pas si la commande a pu être éxecutée
	 * 
	 * @param args
	 * @return un {@link MethodResult} décrivant la validité des arguments
	 */
	private static MethodResult verifArgs(String[] args) {
		if (args[0].equals("--name")) {
			for (int i = 1; i < args.length; i++) {
				if (isConflicting(args[i])) {
					return new BasicResult(BasicResultEnum.CONFLICTING_ARGUMENTS);
				}
			}
			return new BasicResult(BasicResultEnum.ALL_OK);
		} else if (args[0].equals("--all")) {
			for (int i = 1; i < args.length; i++) {
				if (isConflicting(args[i])) {
					return new BasicResult(BasicResultEnum.CONFLICTING_ARGUMENTS);
				}
			}
			return new BasicResult(BasicResultEnum.ALL_OK);
		} else if (args[0].equals("--find")) {
			for (int i = 1; i < args.length; i++) {
				if (isConflicting(args[i])) {
					return new BasicResult(BasicResultEnum.CONFLICTING_ARGUMENTS);
				}
			}
			return new BasicResult(BasicResultEnum.ALL_OK);
		} else if (args[0].equals("--add")) {
			for (int i = 1; i < args.length; i++) {
				if (isConflicting(args[i])) {
					return new BasicResult(BasicResultEnum.CONFLICTING_ARGUMENTS);
				}
			}
			return new BasicResult(BasicResultEnum.ALL_OK);
		} else if (args[0].equals("--delete")) {
			for (int i = 1; i < args.length; i++) {
				if (isConflicting(args[i])) {
					return new BasicResult(BasicResultEnum.CONFLICTING_ARGUMENTS);
				}
			}
			return new BasicResult(BasicResultEnum.ALL_OK);
		} else if (args[0].equals("--edit")) {
			for (int i = 1; i < args.length; i++) {
				if (isConflicting(args[i])) {
					return new BasicResult(BasicResultEnum.CONFLICTING_ARGUMENTS);
				}
			}
			return new BasicResult(BasicResultEnum.ALL_OK);
		}
		return new BasicResult(BasicResultEnum.NO_ARGUMENTS);
	}

	private static boolean isConflicting(String arg) {
		return arg.startsWith("--") && !isDBOption(arg);
	}

	private static boolean isDBOption(String arg) {
		return arg.equals("--r") || arg.equals("--r") || arg.equals("--rf");
	}

	/**
	 * Vérifie les arguments et éxecute l'interface pertinente
	 * 
	 * @param args la commande de l'utlisateur
	 * @param reset réinitialisation ou non de la table
	 * @param fill ré-remplissage de la table
	 * @param noPrint afficher ou non les fenêtres, debug = cacher
	 * @return si la requête était correcte et que l'interface, si besoin, a été éxecutée
	 */
	private static BDDPanelNew parseArgsForPanel(String[] args, boolean reset, boolean fill, boolean noPrint) {
		if (reset) {
			BDDUtilities.resetTable();
		}
		if (fill) {
			BDDUtilities.fillTable();
		}

		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("--name")) {
					if (BDDUtilities.checkTable().getCode().equals(BasicResultEnum.ALL_OK)) {
						return showName(i, args, noPrint);
					} else {
						if (!noPrint) {
							JOptionPane.showMessageDialog(null, "La base de données est vide", "Base de données", JOptionPane.ERROR_MESSAGE);
						}
						return null;
					}
				}
				if (args[i].equals("--all")) {
					if (BDDUtilities.checkTable().getCode().equals(BasicResultEnum.ALL_OK)) {
						return showAll(i, args, noPrint);
					} else {
						if (!noPrint) {
							JOptionPane.showMessageDialog(null, "La base de données est vide", "Base de données", JOptionPane.ERROR_MESSAGE);
						}
						return null;
					}
				}
				if (args[i].equals("--find")) {
					if (BDDUtilities.checkTable().getCode().equals(BasicResultEnum.ALL_OK)) {
						return find(i, args, noPrint);
					} else {
						if (!noPrint) {
							JOptionPane.showMessageDialog(null, "La base de données est vide", "Base de données", JOptionPane.ERROR_MESSAGE);
						}
						return null;
					}
				}
				if (args[i].equals("--add")) {
					return add();
				}
				if (args[i].equals("--edit")) {
					if (BDDUtilities.checkTable().getCode().equals(BasicResultEnum.ALL_OK)) {
						return edit(i, args);
					} else {
						if (!noPrint) {
							JOptionPane.showMessageDialog(null, "La base de données est vide", "Base de données", JOptionPane.ERROR_MESSAGE);
						}
						return null;
					}
				}
			}
		}
		// return new BasicResult(BasicResultEnum.UNKNOWN_ERROR);
		return null;
	}

	/**
	 * Donne toutes les colonnes de la base de données
	 * 
	 * @param name the name of the model
	 * @param getConnection()
	 * @param debug false to prevent print
	 * @return
	 */
	public static String[] getNameInfo(String name, boolean debug) {
		ResultSet rs;
		ResultSet rs2;
		boolean success = false;
		try {
			PreparedStatement statement = BDDUtilities.getConnection().prepareStatement("select COUNT(*) from PLY where NOM = ?");
			name = name.toLowerCase();
			statement.setString(1, name);
			rs = statement.executeQuery();
			if (rs.next()) {
				int totalLines = rs.getInt(1);
				if (totalLines > 0) {
					if (!debug) {
						PreparedStatement statement2 = BDDUtilities.getConnection().prepareStatement("select * from PLY where NOM = ?");
						statement2.setString(1, name);
						rs2 = statement2.executeQuery();
						String[] nameInfo = new String[rs2.getMetaData().getColumnCount()];
						if (rs2.next()) {
							for (int i = 1; i <= rs2.getMetaData().getColumnCount(); i++) {
								nameInfo[i - 1] = rs2.getString(i);
							}
							return nameInfo;
						} else {
							if (!debug) {
								JOptionPane.showMessageDialog(null, "Le modèle " + name + " n'est pas dans la base", "Erreur Base",
										JOptionPane.ERROR_MESSAGE);
							}
							return null;
						}
					}
					success = true;
				} else {
					if (!debug) {
						JOptionPane.showMessageDialog(null, "Le modèle " + name + " n'est pas dans la base", "Erreur Base",
								JOptionPane.ERROR_MESSAGE);
					}
					return null;
				}
			} else {
				if (!debug) {
					String message = "Le modèle " + name + " n'existe pas\nUtilisation: basededonneés --name <name>";
					JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
				}
				success = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (!success) {
				BDDUtilities.closeConnection();
			}
		}
		return null;

	}

	/**
	 * Vérifie et crée la fenetre d'informations sur le modèle précisé
	 * 
	 * @param i la place de "--name" dans les arguments. Servira à parcourir la liste si on aura besoin d'afficher de multiples modèles
	 * @param args le modèle à afficher
	 * @param getConnection() la getConnection() utilsée pour les requêtes
	 * @param debug afficher ou non les fenêtres
	 * @return si le modele existe et qu'il a pu créer la fenêtre
	 */
	private static BDDPanelNew showName(int i, String[] args, boolean debug) {
		ResultSet rs;
		ResultSet rs2;
		int nbArgs = 0;
		for (int j = 0; j < args.length; j++) {
			if (!isDBOption(args[j])) {
				nbArgs++;
			}
		}
		if (nbArgs == 2) {
			boolean success = false;
			try {
				String firstFile = args[i + 1];
				PreparedStatement statement = BDDUtilities.getConnection().prepareStatement("select COUNT(*) from PLY where NOM = ?");
				statement.setString(1, firstFile);
				rs = statement.executeQuery();
				if (rs.next()) {
					int totalLines = rs.getInt(1);
					if (totalLines > 0) {
						if (!debug) {
							PreparedStatement statement2 = BDDUtilities.getConnection().prepareStatement("select * from PLY where NOM = ?");
							statement2.setString(1, firstFile);
							rs2 = statement2.executeQuery();
							return new BDDPanelNew(totalLines, rs2, columnNames, new int[] { 1, 2, 3, 4 });
						}
						success = true;
						// return new
						// BDDResult(BDDResultEnum.SHOW_NAME_SUCCESSFUL);
					} else {
						if (!debug) {
							String message = "Le modèle " + firstFile + " n'existe pas\nUtilisation: basededonneés --name <name>";
							JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
						}
						// System.exit(1);
						success = true;
						// return new BDDResult(BDDResultEnum.MODEL_NOT_FOUND);
					}
				} else {
					if (!debug) {
						String message = "Le modèle " + firstFile + " n'existe pas\nUtilisation: basededonneés --name <name>";
						JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
					}
					// System.exit(1);
					success = true;
					// return new BDDResult(BDDResultEnum.MODEL_NOT_FOUND);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (!success) {
					BDDUtilities.closeConnection();
				}
			}
		} else if (nbArgs == i) {
			if (!debug) {
				String message = "Vous n'avez pas spécifié de nom\nUtilisation: basededonneés --name <name>";
				JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			}
			// System.exit(1);
			// return new BDDResult(BDDResultEnum.NAME_NOT_SPECIFIED);
		} else {
			if (!debug) {
				String message = "Trop d'arguments\nUtilisation: basededonneés --name <name>";
				JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			}
			// System.exit(1);
			// return new BDDResult(BDDResultEnum.TOO_MANY_ARGS);
		}
		// return new BDDResult(BDDResultEnum.SHOW_NAME_NOT_SUCCESSFUL);
		return null;
	}

	/**
	 * Vérifie les arguments et crée la fenetre listant toute la base
	 * 
	 * @param i la place de "--all" dans les arguments
	 * @param args les arguments, sert à vérifier aucun paramètres inutiles
	 * @param quiet true = cacher l'affichage
	 * @return s'il a pu créer la fenêtre
	 */
	private static BDDPanelNew showAll(int i, String[] args, boolean quiet) {
		ResultSet rs;
		boolean success = false;
		try {
			if (!quiet) {
				PreparedStatement statement2 = BDDUtilities.getConnection().prepareStatement("select * from PLY");
				rs = statement2.executeQuery();
				success = true;
				BDDPanelNew result = new BDDPanelNew(rs, columnNames, true);
				result.setEditableColumns(new int[]{1, 2, 4} , true);
				return result;
			}
			success = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (!success) {
				BDDUtilities.closeConnection();
			}
		}
		return null;
	}

	/**
	 * Vérifie les arguments et puis crée la fenetre listant les informations des modèles correspondants aux keywords
	 * 
	 * @param i la place de "--find" dans les arguments
	 * @param args les mots clés à rechercher
	 * @param getConnection() la getConnection() utilsée pour les requêtes
	 * @param debug afficher ou non les fenêtres
	 * @return s'il a trouvé des modèles et a pu affiché la fenêtre
	 */
	private static BDDPanelNew find(int i, String[] args, boolean debug) {
		ResultSet rs;
		ResultSet rs2;
		int nbLikes = 0;
		int nbTreated = 0;
		for (int j = 0; j < args.length; j++) {
			if (!args[j].startsWith("--")) {
				nbLikes++;
			}
		}
		if (nbLikes > 0) {
			boolean success = false;
			try {
				String queryString = "select * from PLY where DESCRIPTION like";
				String queryCount = "select COUNT(*) from PLY where DESCRIPTION like";
				for (int j = i + 1; j < args.length; j++) {
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
				PreparedStatement statement = BDDUtilities.getConnection().prepareStatement(queryCount);
				PreparedStatement statement2 = BDDUtilities.getConnection().prepareStatement(queryString);
				for (int j = i + 1; j < args.length; j++) {
					if (!args[j].startsWith("--")) {
						statement.setString(j, "%" + args[j] + "%");
						statement2.setString(j, "%" + args[j] + "%");
					}
				}
				rs = statement.executeQuery();
				if (rs.next()) {
					int totalLines = Integer.parseInt(rs.getString(1));
					if (totalLines > 0) {
						if (!debug) {
							rs2 = statement2.executeQuery();
							return new BDDPanelNew(totalLines, rs2, columnNames, new int[] { 1, 2, 3, 4 });
						}
						success = true;
						// return new BDDResult(BDDResultEnum.FIND_SUCCESSFUL);
					} else {
						if (!debug) {
							String message = "Aucun modèle trouvé comportant ces mot clés";
							JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
						}
						// System.exit(1);
						success = true;
						// return new BDDResult(BDDResultEnum.MODEL_NOT_FOUND);
					}
				}

				success = true;

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (!success) {
					BDDUtilities.closeConnection();
				}
			}
		} else {
			if (!debug) {
				String message = "Pas de mots clés spécifiés\nUtilisation: basededonneés --find <mots clés>..";
				JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			}
			// System.exit(1);
			// return new BDDResult(BDDResultEnum.NO_DESC_SPECIFIED);
		}
		// return new BDDResult(BDDResultEnum.FIND_NOT_SUCCESSFUL);
		return null;
	}

	/**
	 * Crée la fenêtre d'insertion de modèle
	 * 
	 * @param getConnection() la getConnection() utilsée pour les requêtes
	 * @return si fenêtre crée. Pour savoir si requête sql éxecutée, voir {@link FenetreTable}
	 */
	private static BDDPanelNew add() {
		return new BDDPanelNew(1, columnNames, new int[] { 3 });
		// return new BasicResult(BasicResultEnum.ALL_OK);
	}

	/**
	 * Vérifie les argumuments et puis crée la fenetre de modification du modèle précisé s'ils sont valides
	 * 
	 * @param i la place de "--edit" dans les arguments. Servira à parcourir la liste si on aura besoin de modifier de multiples modèles
	 * @param args le modèle à supprimer
	 * @param getConnection() la getConnection() utilsée pour les requêtes
	 * @return si fenêtre bien crée. Pour savoir si requête sql éxecutée, voir {@link FenetreTable}
	 */
	private static BDDPanelNew edit(int i, String[] args) {

		boolean success = false;
		int modelNames = 0;
		for (int j = 0; j < args.length; j++) {
			if (!args[j].startsWith("--")) {
				modelNames++;
			}
		}
		if (modelNames == 1) {
			try {
				String firstFile = args[i + 1];
				PreparedStatement statement = BDDUtilities.getConnection().prepareStatement("select COUNT(*) from PLY where NOM = ?");
				statement.setString(1, firstFile);
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					int totalLines = rs.getInt(1);
					if (totalLines == 1) {
						PreparedStatement st = BDDUtilities.getConnection().prepareStatement("select * from ply where nom = ?");
						st.setString(1, firstFile);
						ResultSet rs2 = st.executeQuery();
						success = true;
						return new BDDPanelNew(totalLines, rs2, columnNames, new int[] { 3 });
						// return new BasicResult(BasicResultEnum.ALL_OK);
					} else {
						String message = "Le modèle " + firstFile + " n'existe pas";
						JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
						// System.exit(1);
						success = true;
						// return new BDDResult(BDDResultEnum.MODEL_NOT_FOUND);
					}
				}
				success = true;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (!success) {
					BDDUtilities.closeConnection();
				}
			}
		} else if (args.length > i + 1) {
			String message = "Trop d'arguments\nUtilisation: basededonneés --name <name>";
			JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			// System.exit(1);
			// return new BDDResult(BDDResultEnum.TOO_MANY_ARGS);
		} else {
			String message = "Pas de nom précisé\nUtilisation: basededonneés --name <name>";
			JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			// System.exit(1);
			// return new BDDResult(BDDResultEnum.NAME_NOT_SPECIFIED);
		}
		// return new BDDResult(BDDResultEnum.EDIT_NOT_SUCCESSFUL);
		return null;
	}

	/**
	 * @param i la place de "--delete" dans les arguments. Servira à parcourir la liste si on aura besoin de supprimer de multiples modèles
	 * @param args le modèle à supprimer
	 * @param getConnection() la getConnection() utilsée pour les requêtes
	 * @param debug afficher ou non les fenêtres
	 * @return si modèle a bien été supprimé
	 */
	private static MethodResult delete(int i, String[] args, boolean debug) {
		boolean success = false;
		if (args.length > 1) {
			try {
				String firstFile = args[i + 1];
				PreparedStatement stExists = BDDUtilities.getConnection().prepareStatement("select COUNT(*) from PLY where NOM = ?");
				stExists.setString(1, firstFile);
				ResultSet rs = stExists.executeQuery();
				if (rs.next()) {
					int totalLines = rs.getInt(1);
					if (totalLines == 1) {
						PreparedStatement stDelete = BDDUtilities.getConnection().prepareStatement("delete from PLY where NOM = ?");
						stDelete.setString(1, firstFile);
						int result = stDelete.executeUpdate(); // result = nombre de
																// lignes affectés
																// par le delete
						if (!debug && result > 0) {
							String message = "Le modèle " + firstFile + " a été supprimé avec succès!";
							JOptionPane.showMessageDialog(null, message);
						} else if (!debug && result <= 0) {
							String message = "Le modèle " + firstFile + " n'a pas pu être supprimé!";
							JOptionPane.showMessageDialog(null, message);
						}
						// System.exit(0);
						// ICI REMPLACER PAR LE RESULTAT DU DELETE
						success = true;
						if (result > 0) {
							return new BDDResult(BDDResultEnum.DELETE_SUCCESSFUL);
						}
					} else {
						if (!debug) {
							String message = "Le modèle " + firstFile + " n'existe pas";
							JOptionPane.showMessageDialog(null, message, "Erreur", JOptionPane.ERROR_MESSAGE);
						}
						// System.exit(1);
						success = true;
						return new BDDResult(BDDResultEnum.MODEL_NOT_FOUND);
					}
				} else {
					if (!debug) {
						String message = "Le modèle " + firstFile + " n'existe pas";
						JOptionPane.showMessageDialog(null, message, "Erreur", JOptionPane.ERROR_MESSAGE);
					}
					// System.exit(1);
					success = true;
					return new BDDResult(BDDResultEnum.MODEL_NOT_FOUND);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (!success) {
					BDDUtilities.closeConnection();
				}
			}
		} else {
			if (!debug) {
				JOptionPane.showMessageDialog(null, "Vous n'avez pas spécifié de modèle à supprimer", "Erreur", JOptionPane.ERROR_MESSAGE);
			}
			return new BasicResult(BasicResultEnum.NO_ARGUMENTS);
		}
		return new BDDResult(BDDResultEnum.DELETE_NOT_SUCCESSFUL);
	}

	public static void closeUsedConnection() {
		BDDUtilities.closeConnection();
	}

}
