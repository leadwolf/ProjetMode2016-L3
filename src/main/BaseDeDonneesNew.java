package main;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;

import main.vues.BDDPanel;
import main.vues.MainFenetre;
import ply.bdd.vues.FenetreTable;
import result.BDDResult;
import result.BDDResultEnum;
import result.BasicResult;
import result.BasicResultEnum;
import result.MethodResult;

public class BaseDeDonneesNew {

	/**
	 * La liste de modèles
	 */
	private static String[] items;
	/**
	 * La connection utilisée par cette classe
	 */
	private static Connection connection;

	/**
	 * Constructeur par défaut, initalise la liste des modèles à utilser dans le remplissage de la table
	 * @param dbPath le chemin vers la bdd.sqlite
	 */
	public BaseDeDonneesNew(Path dbPath) {
		items = dbPath.toFile().list();
	}

	
	/**
	 * Creates a BDDPanel from the args given
	 * @param args
	 * @param reset 
	 * @param fill 
	 * @param dbPath path to the db.sqlite
	 * @param noPrint
	 * @return
	 */
	public static BDDPanel getPanel(String[] args, boolean reset, boolean fill, boolean noPrint, Path dbPath) {	
		if ( (args == null) || (args != null && args.length <= 0)) {
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
		
		if (dbPath == null) {
			boolean success = false;
			try {
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection("jdbc:sqlite:data/test.sqlite");
				success = true;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (!success) {
					try {
						connection.close();
					} catch (SQLException e) {
						System.err.println(e);
					}
				}
			}
		} else {
			boolean success = false;
			try {
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath.getParent() + "/" + dbPath.getFileName());
				success = true;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (!success) {
					try {
						connection.close();
					} catch (SQLException e) {
						System.err.println(e);
					}
				}
			}

		}
		return parseArgs(args, reset, fill, noPrint);
	}
	
	/**
	 * Verifie seuelement si la syntaxte des arguments sont corrects, pas si la commande a pu être éxecutée
	 * 
	 * @param args
	 * @return un {@link MethodResult} décrivant la validité des arguments
	 */
	@SuppressWarnings("unused")
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
		return new BasicResult(BasicResultEnum.NO_ARGUMENTS);
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
	private static BDDPanel parseArgs(String[] args, boolean reset, boolean fill, boolean noPrint) {
		if (reset) {
			resetTable(connection);
		}
		if (fill) {
			fillTable(connection);
		}

		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("--name")) {
					if (checkTable(connection).getCode().equals(BasicResultEnum.ALL_OK)) {
						return showName(i, args, connection, noPrint);
					} else {
//						return new BDDResult(BDDResultEnum.EMPTY_DB);
					}
				}
				if (args[i].equals("--all")) {
					if (checkTable(connection).getCode().equals(BasicResultEnum.ALL_OK)) {
						return showAll(i, args, connection, noPrint);
					} else {
//						return new BDDResult(BDDResultEnum.EMPTY_DB);
					}
				}
				if (args[i].equals("--find")) {
					if (checkTable(connection).getCode().equals(BasicResultEnum.ALL_OK)) {
						return find(i, args, connection, noPrint);
					} else {
//						return new BDDResult(BDDResultEnum.EMPTY_DB);
					}
				}
				if (args[i].equals("--add")) {
					return add(connection);
				}
				if (args[i].equals("--delete")) {
					if (checkTable(connection).getCode().equals(BasicResultEnum.ALL_OK)) {
						delete(i, args, connection, noPrint);
					} else {
//						return new BDDResult(BDDResultEnum.EMPTY_DB);
					}
				}
				if (args[i].equals("--edit")) {
					if (checkTable(connection).getCode().equals(BasicResultEnum.ALL_OK)) {
						return edit(i, args, connection);
					} else {
//						return new BDDResult(BDDResultEnum.EMPTY_DB);
					}
				}
			}
		}
//		return new BasicResult(BasicResultEnum.UNKNOWN_ERROR);
		return null;
	}
	
	/**
	 * Vérifie et crée la fenetre d'informations sur le modèle précisé
	 * 
	 * @param i la place de "--name" dans les arguments. Servira à parcourir la liste si on aura besoin d'afficher de multiples modèles
	 * @param args le modèle à afficher
	 * @param connection la connection utilsée pour les requêtes
	 * @param debug afficher ou non les fenêtres
	 * @return si le modele existe et qu'il a pu créer la fenêtre
	 */
	private static BDDPanel showName(int i, String[] args, Connection connection, boolean debug) {
		ResultSet rs;
		ResultSet rs2;
		if (args.length - 1 == i + 1) {
			boolean success = false;
			try {
				String firstFile = args[i + 1];
				PreparedStatement statement = connection.prepareStatement("select COUNT(*) from PLY where NOM = ?");
				statement.setString(1, firstFile);
				rs = statement.executeQuery();
				if (rs.next()) {
					int totalLines = rs.getInt(1);
					if (totalLines > 0) {
						if (!debug) {
							PreparedStatement statement2 = connection.prepareStatement("select * from PLY where NOM = ?");
							statement2.setString(1, firstFile);
							rs2 = statement2.executeQuery();
							String[] columnNames = new String[] { "Nom", "Chemin", "Date", "Mot Clés" };
							return new BDDPanel(firstFile, totalLines, null, rs2, columnNames, new int[] { 1, 2, 3, 4 }, connection);
						}
						success = true;
//						return new BDDResult(BDDResultEnum.SHOW_NAME_SUCCESSFUL);
					} else {
						if (!debug) {
							String message = "Le modèle " + firstFile + " n'existe pas\nUtilisation: basededonneés --name <name>";
							JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
						}
						// System.exit(1);
						success = true;
//						return new BDDResult(BDDResultEnum.MODEL_NOT_FOUND);
					}
				} else {
					if (!debug) {
						String message = "Le modèle " + firstFile + " n'existe pas\nUtilisation: basededonneés --name <name>";
						JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
					}
					// System.exit(1);
					success = true;
//					return new BDDResult(BDDResultEnum.MODEL_NOT_FOUND);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (!success) {
					try {

						connection.close();
					} catch (SQLException e) {
						System.err.println(e);
					}
				}
			}
		} else if (args.length - 1 == i) {
			if (!debug) {
				String message = "Vous n'avez pas spécifié de nom\nUtilisation: basededonneés --name <name>";
				JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			}
			// System.exit(1);
//			return new BDDResult(BDDResultEnum.NAME_NOT_SPECIFIED);
		} else {
			if (!debug) {
				String message = "Trop d'arguments\nUtilisation: basededonneés --name <name>";
				JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			}
			// System.exit(1);
//			return new BDDResult(BDDResultEnum.TOO_MANY_ARGS);
		}
//		return new BDDResult(BDDResultEnum.SHOW_NAME_NOT_SUCCESSFUL);
		return null;
	}
	
	/**
	 * Vérifie les arguments et crée la fenetre listant toute la base
	 * 
	 * @param i la place de "--all" dans les arguments
	 * @param args les arguments, sert à vérifier aucun paramètres inutiles
	 * @param connection la connection utilsée pour les requêtes
	 * @param debug afficher ou non les requêtes
	 * @return s'il a pu créer la fenêtre
	 */
	private static BDDPanel showAll(int i, String[] args, Connection connection, boolean debug) {
		ResultSet rs;
		ResultSet rs2;
		if (args.length - 1 == i) {
			boolean success = false;
			try {
				PreparedStatement statement = connection.prepareStatement("select COUNT(*) from PLY");
				rs = statement.executeQuery();
				if (rs.next()) {
					int totalLines = rs.getInt(1);
					if (totalLines > 0) {
						if (!debug) {
							PreparedStatement statement2 = connection.prepareStatement("select * from PLY");
							rs2 = statement2.executeQuery();
							String[] columnNames = new String[] { "Nom", "Chemin", "Date", "Mot Clés" };
							return new BDDPanel("All models", totalLines, null, rs2, columnNames, new int[] { 1, 2, 3, 4 }, connection);
						}
						success = true;
//						return new BDDResult(BDDResultEnum.SHOW_ALL_SUCCESSFUL);
					} else {
						if (!debug) {
							String message = "Il n'y a pas de modèles enregistré";
							JOptionPane.showMessageDialog(null, message, "Base de données vide", JOptionPane.ERROR_MESSAGE);
						}
						// System.exit(1);
						success = true;
//						return new BDDResult(BDDResultEnum.EMPTY_DB);
					}
				}

				success = true;

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (!success) {
					try {
						connection.close();
					} catch (SQLException e) {
						System.err.println(e);
					}
				}
			}
		} else {
			if (!debug) {
				String message = "Trop d'arguments\nUtilisation: basededonneés --all";
				JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			}
			// System.exit(1);
//			return new BDDResult(BDDResultEnum.TOO_MANY_ARGS);
		}
//		return new BDDResult(BDDResultEnum.SHOW_ALL_NOT_SUCCESSFUL);
		return null;
	}
	
	/**
	 * Vérifie les arguments et puis crée la fenetre listant les informations des modèles correspondants aux keywords
	 * 
	 * @param i la place de "--find" dans les arguments
	 * @param args les mots clés à rechercher
	 * @param connection la connection utilsée pour les requêtes
	 * @param debug afficher ou non les fenêtres
	 * @return s'il a trouvé des modèles et a pu affiché la fenêtre
	 */
	private static BDDPanel find(int i, String[] args, Connection connection, boolean debug) {
		ResultSet rs;
		ResultSet rs2;
		String matching = "";
		if (args.length - 1 > i) {
			boolean success = false;
			try {
				String queryCount = "select COUNT(*) from PLY where DESCRIPTION like";
				String queryString = "select * from PLY where DESCRIPTION like";
				for (int j = i + 1; j < args.length; j++) {
					if (j == i + 1) {
						queryString += " ?";
						queryCount += " ?";
					} else {
						queryString += " union select * from PLY where DESCRIPTION LIKE ?";
						queryCount += " ?";
					}
				}
				queryString += ";";
				queryCount += ";";
				PreparedStatement statement = connection.prepareStatement(queryCount);
				PreparedStatement statement2 = connection.prepareStatement(queryString);
				for (int j = i + 1; j < args.length; j++) {
					statement.setString(j, "%" + args[j] + "%");
					statement2.setString(j, "%" + args[j] + "%");
					if (j < args.length - 1) {
						matching += "\"" + args[j] + "\", ";
					} else {
						matching += "\"" + args[j] + "\"";
					}
				}
				rs = statement.executeQuery();
				if (rs.next()) {
					int totalLines = Integer.parseInt(rs.getString(1));
					if (totalLines > 0) {
						if (!debug) {
							rs2 = statement2.executeQuery();
							String[] columnNames = new String[] { "Nom", "Chemin", "Date", "Mot Clés" };
							return new BDDPanel("Find models", totalLines, null, rs2, columnNames, new int[] { 1, 2, 3, 4 }, connection);
						}
						success = true;
//						return new BDDResult(BDDResultEnum.FIND_SUCCESSFUL);
					} else {
						if (!debug) {
							String message = "Aucun modèle trouvé comportant ces mot clés";
							JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
						}
						// System.exit(1);
						success = true;
//						return new BDDResult(BDDResultEnum.MODEL_NOT_FOUND);
					}
				}

				success = true;

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (!success) {
					try {

						connection.close();
					} catch (SQLException e) {
						System.err.println(e);
					}
				}
			}
		} else {
			if (!debug) {
				String message = "Pas de mots clés spécifiés\nUtilisation: basededonneés --find <mots clés>..";
				JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			}
			// System.exit(1);
//			return new BDDResult(BDDResultEnum.NO_DESC_SPECIFIED);
		}
//		return new BDDResult(BDDResultEnum.FIND_NOT_SUCCESSFUL);
		return null;
	}

	/**
	 * Crée la fenêtre d'insertion de modèle
	 * 
	 * @param connection la connection utilsée pour les requêtes
	 * @return si fenêtre crée. Pour savoir si requête sql éxecutée, voir {@link FenetreTable}
	 */
	private static BDDPanel add(Connection connection) {
		String[] columnNames = { "Nom", "Chemin", "Date", "Description" };
		String[] buttonNames = new String[] { "Confirmer", "Reset" };
		return new BDDPanel("Add a model", 1, buttonNames, columnNames, new int[] { 3 }, connection);
//		return new BasicResult(BasicResultEnum.ALL_OK);
	}

	/**
	 * Vérifie les argumuments et puis crée la fenetre de modification du modèle précisé s'ils sont valides
	 * 
	 * @param i la place de "--edit" dans les arguments. Servira à parcourir la liste si on aura besoin de modifier de multiples modèles
	 * @param args le modèle à supprimer
	 * @param connection la connection utilsée pour les requêtes
	 * @return si fenêtre bien crée. Pour savoir si requête sql éxecutée, voir {@link FenetreTable}
	 */
	private static BDDPanel edit(int i, String[] args, Connection connection) {

		boolean success = false;
		if (args.length - 1 == i + 1) {
			try {
				String firstFile = args[i + 1];
				PreparedStatement statement = connection.prepareStatement("select COUNT(*) from PLY where NOM = ?");
				statement.setString(1, firstFile);
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					int totalLines = rs.getInt(1);
					if (totalLines == 1) {
						String[] buttonNames = new String[] { "Confirmer", "Reset" };
						String[] columnNames = new String[] { "Nom", "Chemin", "Date", "Mot Clés" };
						PreparedStatement st = connection.prepareStatement("select * from ply where nom = ?");
						st.setString(1, firstFile);
						ResultSet rs2 = st.executeQuery();
						success = true;
						return new BDDPanel("Insert", totalLines, buttonNames, rs2, columnNames, new int[] { 3 }, connection);
//						return new BasicResult(BasicResultEnum.ALL_OK);
					} else {
						String message = "Le modèle " + firstFile + " n'existe pas";
						JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
						// System.exit(1);
						success = true;
//						return new BDDResult(BDDResultEnum.MODEL_NOT_FOUND);
					}
				}
				success = true;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (!success) {

					try {

						connection.close();
					} catch (SQLException e) {
						System.err.println(e);
					}
				}
			}
		} else if (args.length > i + 1) {
			String message = "Trop d'arguments\nUtilisation: basededonneés --name <name>";
			JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			// System.exit(1);
//			return new BDDResult(BDDResultEnum.TOO_MANY_ARGS);
		} else {
			String message = "Pas de nom précisé\nUtilisation: basededonneés --name <name>";
			JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			// System.exit(1);
//			return new BDDResult(BDDResultEnum.NAME_NOT_SPECIFIED);
		}
//		return new BDDResult(BDDResultEnum.EDIT_NOT_SUCCESSFUL);
		return null;
	}

	/**
	 * @param i la place de "--delete" dans les arguments. Servira à parcourir la liste si on aura besoin de supprimer de multiples modèles
	 * @param args le modèle à supprimer
	 * @param connection la connection utilsée pour les requêtes
	 * @param debug afficher ou non les fenêtres
	 * @return si modèle a bien été supprimé
	 */
	private static MethodResult delete(int i, String[] args, Connection connection, boolean debug) {
		boolean success = false;
		try {
			String firstFile = args[i + 1];
			PreparedStatement stExists = connection.prepareStatement("select COUNT(*) from PLY where NOM = ?");
			stExists.setString(1, firstFile);
			ResultSet rs = stExists.executeQuery();
			if (rs.next()) {
				int totalLines = rs.getInt(1);
				if (totalLines == 1) {
					PreparedStatement stDelete = connection.prepareStatement("delete from PLY where NOM = ?");
					stDelete.setString(1, firstFile);
					int result = stDelete.executeUpdate(); // result = nombre de lignes affectés par le delete
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
				try {
					connection.close();
				} catch (SQLException e) {
					System.err.println(e);
				}
			}
		}
		return new BDDResult(BDDResultEnum.DELETE_NOT_SUCCESSFUL);
	}
	
	/**
	 * Drop la table PLY et la recrée
	 * 
	 * @param connection la conenction utilisée pour supprimer la table
	 */
	public static void resetTable(Connection connection) {
		boolean success = false;
		try {

			PreparedStatement firstStatement = connection.prepareStatement("drop table PLY");
			firstStatement.executeUpdate();
			PreparedStatement secondStatement = connection.prepareStatement("create table PLY(NOM text, CHEMIN text, DATE text, DESCRIPTION text)");
			secondStatement.executeUpdate();
			success = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (!success) {
				try {
					connection.close();
				} catch (SQLException e) {
					System.err.println(e);
				}
			}
		}

	}

	/**
	 * Remplit la table PLY avec les fichiers dans le dossier ply/
	 * 
	 * @param connection la connection utilisée pour remplir la table
	 */
	public static void fillTable(Connection connection) {
		boolean success = false;
		new BaseDeDonneesOld(Paths.get("data/"));
		try {

			Statement firstStatement;
			firstStatement = connection.createStatement();
			for (int i = 0; i < items.length; i++) {
				if (items[i].lastIndexOf(".ply") > 0) {
					String nom = items[i].substring(0, items[i].lastIndexOf(".ply"));
					firstStatement.executeUpdate("insert into PLY values " + "('" + nom + "', 'ply/" + items[i] + "', '" + toDay() + "' ,'mes mots clés')");
				}
			}
			success = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (!success) {
				try {

					connection.close();
				} catch (SQLException e) {
					System.err.println(e);
				}
			}
		}
	}
	
	/**
	 * @return la date d'aujourd'hui sous forme YYYY/MM/DD
	 */
	private static String toDay() {
		Calendar dat = new GregorianCalendar();
		return dat.get(Calendar.YEAR) + "/" + dat.get(Calendar.MONTH) + "/" + dat.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * Vérifie si la table est vide
	 * 
	 * @param connection
	 * @return
	 */
	private static MethodResult checkTable(Connection connection) {
		boolean success = false;
		try {
			PreparedStatement statement;
			statement = connection.prepareStatement("select COUNT(*) from PLY");
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				int totalLines = rs.getInt(1);
				if (totalLines > 0) {
					success = true;
					return new BasicResult(BasicResultEnum.ALL_OK);
				}
			}
			success = true;
			return new BDDResult(BDDResultEnum.EMPTY_DB);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (!success) {
				try {
					connection.close();
				} catch (SQLException e) {
					System.err.println(e);
				}
			}
		}
		return new BasicResult(BasicResultEnum.UNKNOWN_ERROR);
	}
}
