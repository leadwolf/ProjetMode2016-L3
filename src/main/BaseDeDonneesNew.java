package main;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;

import main.vues.BDDPanel;
import ply.bdd.vues.FenetreTable;
import ply.plyModel.modeles.FigureModel;
import result.BDDResult;
import result.BDDResultEnum;
import result.BasicResult;
import result.BasicResultEnum;
import result.MethodResult;

/**
 * Cette classe est pareil que BaseDeDonnesOld mais donne une JPanel au lieu d'éxécuter directement les commandes
 * 
 * @author L3
 *
 */
public class BaseDeDonneesNew {

	/**
	 * Le chemin vers le fichiers .ply
	 */
	private static File[] files;
	/**
	 * La connection utilisée par cette classe
	 */
	private static Connection connection;

	private static String[] columnNames = new String[] { "Nom", "Chemin", "Date", "Mot Clés", "Nombre de Points", "Nombre de Faces" };

	/**
	 * Constructeur par défaut, initalise la liste des modèles à utilser dans le remplissage de la table
	 * 
	 * @param dbPath le chemin le dossier contenant les modeles .ply et le fichier .sqlite
	 */
	public BaseDeDonneesNew(Path dbPath) {
		files = dbPath.toFile().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.lastIndexOf('.') > 0) {
					String str = name.substring(name.lastIndexOf("."));
					// match path name extension
					if (str.equals(".ply")) {
						return true;
					}
				}
				return false;
			}
		});
	}

	public static Connection getDefaultConnection() {
		Connection tempCon = null;
		try {
			if (connection != null && !connection.isClosed()) {
				return connection;
			} else {
				try {
					Class.forName("org.sqlite.JDBC");
					tempCon = DriverManager.getConnection("jdbc:sqlite:data/test.sqlite");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tempCon;
	}

	/**
	 * Ferme la connection utilisée par cette classe
	 */
	public static void closeConnection() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

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
	public static BDDPanel getPanel(String[] args, boolean reset, boolean fill, boolean noPrint, Path dbPath) {
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
			resetTable(connection);
		}
		if (fill) {
			fillTable(connection);
		}

		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("--delete")) {
					if (checkTable(connection).getCode().equals(BasicResultEnum.ALL_OK)) {
						return delete(i, args, connection, noPrint);
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
	@SuppressWarnings("unused")
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
	private static BDDPanel parseArgsForPanel(String[] args, boolean reset, boolean fill, boolean noPrint) {
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
						if (!noPrint) {
							JOptionPane.showMessageDialog(null, "La base de données est vide", "Base de données", JOptionPane.ERROR_MESSAGE);
						}
						return null;
					}
				}
				if (args[i].equals("--all")) {
					if (checkTable(connection).getCode().equals(BasicResultEnum.ALL_OK)) {
						return showAll(i, args, connection, noPrint);
					} else {
						if (!noPrint) {
							JOptionPane.showMessageDialog(null, "La base de données est vide", "Base de données", JOptionPane.ERROR_MESSAGE);
						}
						return null;
					}
				}
				if (args[i].equals("--find")) {
					if (checkTable(connection).getCode().equals(BasicResultEnum.ALL_OK)) {
						return find(i, args, connection, noPrint);
					} else {
						if (!noPrint) {
							JOptionPane.showMessageDialog(null, "La base de données est vide", "Base de données", JOptionPane.ERROR_MESSAGE);
						}
						return null;
					}
				}
				if (args[i].equals("--add")) {
					return add(connection);
				}
				if (args[i].equals("--edit")) {
					if (checkTable(connection).getCode().equals(BasicResultEnum.ALL_OK)) {
						return edit(i, args, connection);
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
	 * @param connection
	 * @param debug false to prevent print
	 * @return
	 */
	public static String[] getNameInfo(String name, boolean debug) {
		ResultSet rs;
		ResultSet rs2;
		boolean success = false;
		try {
			PreparedStatement statement = getDefaultConnection().prepareStatement("select COUNT(*) from PLY where NOM = ?");
			name = name.toLowerCase();
			statement.setString(1, name);
			rs = statement.executeQuery();
			if (rs.next()) {
				int totalLines = rs.getInt(1);
				if (totalLines > 0) {
					if (!debug) {
						PreparedStatement statement2 = getDefaultConnection().prepareStatement("select * from PLY where NOM = ?");
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
				try {
					getDefaultConnection().close();
				} catch (SQLException e) {
					System.err.println(e);
				}
			}
		}
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
		int nbArgs = 0;
		for (int j=0;j<args.length;j++) {
			if (!isDBOption(args[j])) {
				nbArgs++;
			}
		}
		if (nbArgs == 2) {
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
							return new BDDPanel(firstFile, totalLines, null, rs2, columnNames, new int[] { 1, 2, 3, 4 }, connection);
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
					try {

						connection.close();
					} catch (SQLException e) {
						System.err.println(e);
					}
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
	 * @param connection la connection utilsée pour les requêtes
	 * @param debug afficher ou non les requêtes
	 * @return s'il a pu créer la fenêtre
	 */
	private static BDDPanel showAll(int i, String[] args, Connection connection, boolean debug) {
		ResultSet rs;
		ResultSet rs2;
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
						return new BDDPanel("All models", totalLines, null, rs2, columnNames, new int[] { 1, 2, 3, 4 }, connection);
					}
					success = true;
					// return new
					// BDDResult(BDDResultEnum.SHOW_ALL_SUCCESSFUL);
				} else {
					if (!debug) {
						String message = "Il n'y a pas de modèles enregistré";
						JOptionPane.showMessageDialog(null, message, "Base de données vide", JOptionPane.ERROR_MESSAGE);
					}
					// System.exit(1);
					success = true;
					// return new BDDResult(BDDResultEnum.EMPTY_DB);
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
		int nbLikes = 0;
		int nbTreated = 0;
		for (int j=0;j<args.length;j++) {
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
				PreparedStatement statement = connection.prepareStatement(queryCount);
				PreparedStatement statement2 = connection.prepareStatement(queryString);
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
							return new BDDPanel("Find models", totalLines, null, rs2, columnNames, new int[] { 1, 2, 3, 4 }, connection);
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
			// return new BDDResult(BDDResultEnum.NO_DESC_SPECIFIED);
		}
		// return new BDDResult(BDDResultEnum.FIND_NOT_SUCCESSFUL);
		return null;
	}

	/**
	 * Crée la fenêtre d'insertion de modèle
	 * 
	 * @param connection la connection utilsée pour les requêtes
	 * @return si fenêtre crée. Pour savoir si requête sql éxecutée, voir {@link FenetreTable}
	 */
	private static BDDPanel add(Connection connection) {
		String[] buttonNames = new String[] { "Confirmer", "Reset" };
		return new BDDPanel("Add a model", 1, buttonNames, columnNames, new int[] { 3 }, connection);
		// return new BasicResult(BasicResultEnum.ALL_OK);
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
		int modelNames = 0;
		for (int j=0;j<args.length;j++) {
			if (!args[j].startsWith("--")) {
				modelNames++;
			}
		}
		if (modelNames == 1) {
			try {
				String firstFile = args[i + 1];
				PreparedStatement statement = connection.prepareStatement("select COUNT(*) from PLY where NOM = ?");
				statement.setString(1, firstFile);
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					int totalLines = rs.getInt(1);
					if (totalLines == 1) {
						String[] buttonNames = new String[] { "Confirmer", "Reset" };
						PreparedStatement st = connection.prepareStatement("select * from ply where nom = ?");
						st.setString(1, firstFile);
						ResultSet rs2 = st.executeQuery();
						success = true;
						return new BDDPanel("Insert", totalLines, buttonNames, rs2, columnNames, new int[] { 3 }, connection);
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
	 * @param connection la connection utilsée pour les requêtes
	 * @param debug afficher ou non les fenêtres
	 * @return si modèle a bien été supprimé
	 */
	private static MethodResult delete(int i, String[] args, Connection connection, boolean debug) {
		boolean success = false;
		if (args.length > 1) {
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
					try {
						connection.close();
					} catch (SQLException e) {
						System.err.println(e);
					}
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
			PreparedStatement secondStatement = connection.prepareStatement(
					"create table PLY(NOM text PRIMARY KEY, CHEMIN text, DATE text, DESCRIPTION text, NOMBRE_POINTS integer, NOMBRE_FACES integer)");
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
		new BaseDeDonneesNew(Paths.get("data/"));
		try {

			String insertStatement = "";
			for (int i = 0; i < files.length; i++) {
				String nom = files[i].toPath().getFileName().toString();
				nom = nom.substring(0, nom.lastIndexOf("."));
				FigureModel fig = new FigureModel(files[i].toPath().toAbsolutePath(), true);

				insertStatement = "insert into PLY values (?, ?, ?, ?, ?, ?)";
				PreparedStatement firstStatement;
				firstStatement = connection.prepareStatement(insertStatement);
				firstStatement.setString(1, nom);
				firstStatement.setString(2, files[i].getAbsolutePath().toString());
				firstStatement.setString(3, toDay());
				firstStatement.setString(4, "mes mots clés");
				firstStatement.setInt(5, fig.getNbPoints());
				firstStatement.setInt(6, fig.getNbFaces());
				firstStatement.executeUpdate();
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
