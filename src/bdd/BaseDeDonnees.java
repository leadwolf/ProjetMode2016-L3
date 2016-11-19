package bdd;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;

import bddInterface.FenetreTable;

/**
 * @author L3
 *
 */
public class BaseDeDonnees {

	static String[] items;
	private static Connection connection;

	public BaseDeDonnees() {
		File path = new File("ply/");
		items = path.list();
	}

	public static String toDay() {
		Calendar dat = new GregorianCalendar();
		return dat.get(Calendar.YEAR) + "/" + dat.get(Calendar.MONTH) + "/" + dat.get(Calendar.DAY_OF_MONTH);
	}

	public static void main(String[] args) {
		parseArgs(args, false, false, false);
	}

	/**
	 * Verifie seuelement si la syntaxte des arguments sont corrects, pas si la
	 * commande a pu être éxecutée
	 * 
	 * @param args
	 * @return
	 */
	public static boolean verifArgs(String[] args) {
		if (args[0].equals("--name")) {
			for (int i = 1; i < args.length; i++) {
				if (args[i].startsWith("--")) {
					return false;
				}
			}
			return true;
		} else if (args[0].equals("--all")) {
			// nothing is wrong
			return true;
		} else if (args[0].equals("--find")) {
			for (int i = 1; i < args.length; i++) {
				if (args[i].startsWith("--")) {
					return false;
				}
			}
			return true;
		} else if (args[0].equals("--add")) {
			// nothing is wrong
			return true;
		} else if (args[0].equals("--delete")) {
			for (int i = 1; i < args.length; i++) {
				if (args[i].startsWith("--")) {
					return false;
				}
			}
			return true;
		} else if (args[0].equals("--edit")) {
			for (int i = 1; i < args.length; i++) {
				if (args[i].startsWith("--")) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Vérifie les arguments et éxecute l'interface pertinente
	 * 
	 * @param args
	 * @param debug
	 * @return si la requête était correcte et que l'interface, si besoin, a été
	 *         éxecutée
	 */
	public static boolean parseArgs(String[] args, boolean reset, boolean fill, boolean debug) {

		if (!verifArgs(args)) {
			return false;
		} // else continue program

		connection = null;
		boolean success = false;
		try {

			// load the sqlite-JDBC driver using the current class loader
			Class.forName("org.sqlite.JDBC");

			connection = DriverManager.getConnection("jdbc:sqlite:data/test.sqlite");
			
			if (reset) {
				resetTable(connection);
			}
			if (fill) {
				fillTable(connection);
			}

			if (args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					if (args[i].equals("--name")) {
						success = true;
						return showName(i, args, connection, debug);
					}
					if (args[i].equals("--all")) {
						success = true;
						return showAll(i, args, connection, debug);
					}
					if (args[i].equals("--find")) {
						success = true;
						return find(i, args, connection, debug);
					}
					if (args[i].equals("--add")) {
						success = true;
						return add(connection);
					}
					if (args[i].equals("--delete")) {
						success = true;
						return delete(i, args, connection, debug);
					}
					if (args[i].equals("--edit")) {
						success = true;
						return edit(i, args, connection);
					}
				}
			}
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
		return false;
	}

	/**
	 * Drop la table PLY et la recrée
	 * 
	 * @param connection
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
	 * @param connection
	 */
	public static void fillTable(Connection connection) {
		boolean success = false;
		new BaseDeDonnees();
		try {
			
			Statement firstStatement;
			firstStatement = connection.createStatement();
			for (int i = 0; i < items.length; i++) {
				String nom = items[i].substring(0, items[i].lastIndexOf(".ply"));
				firstStatement.executeUpdate("insert into PLY values " + "('" + nom + "', 'ply/" + items[i] + "', '" + toDay() + "' ,'mes mots clés')");
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
	 * Vérifie et crée la fenetre d'informations sur le modèle précisé
	 * 
	 * @param i
	 * @param args
	 * @param connection
	 * @param debug
	 * @return si le modele existe et qu'il a pu créer la fenêtre
	 */
	public static boolean showName(int i, String[] args, Connection connection, boolean debug) {
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
							FenetreTable fen = new FenetreTable(firstFile, totalLines, null, rs2, columnNames, new int[] { 1, 2, 3, 4 }, connection);
							fen.setPanelBorderTitle("");
						}
						success = true;
						return true;
					} else {
						if (!debug) {
							String message = "Le modèle " + firstFile + " n'existe pas\nUtilisation: basededonneés --name <name>";
							JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
						}
						// System.exit(1);
						success = true;
						return false;
					}
				} else {
					if (!debug) {
						String message = "Le modèle " + firstFile + " n'existe pas\nUtilisation: basededonneés --name <name>";
						JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
					}
					// System.exit(1);
					success = true;
					return false;
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
			return false;
		} else {
			if (!debug) {
				String message = "Trop d'arguments\nUtilisation: basededonneés --name <name>";
				JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			}
			// System.exit(1);
			return false;
		}
		return false;
	}

	/**
	 * Vérifie les arguments et crée la fenetre listant toute la base
	 * 
	 * @param i
	 * @param args
	 * @param connection
	 * @param debug
	 * @return s'il a pu créer la fenêtre
	 */
	public static boolean showAll(int i, String[] args, Connection connection, boolean debug) {
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
							FenetreTable fen = new FenetreTable("All models", totalLines, null, rs2, columnNames, new int[] { 1, 2, 3, 4 }, connection);
							fen.setPanelBorderTitle("");
						}
						success = true;
						return true;
					} else {
						if (!debug) {
							String message = "Il n'y a pas de modèles enregistré";
							JOptionPane.showMessageDialog(null, message, "Base de données vide", JOptionPane.ERROR_MESSAGE);
						}
						// System.exit(1);
						success = true;
						return false;
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
			return false;
		}
		return false;
	}

	/**
	 * Vérifie les arguments et puis crée la fenetre listant les informations
	 * des modèles correspondants aux keywords
	 * 
	 * @param i
	 * @param args
	 * @param connection
	 * @param debug
	 * @return s'il a trouvé des modèles et a pu affiché la fenêtre
	 */
	public static boolean find(int i, String[] args, Connection connection, boolean debug) {
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
						FenetreTable fen = new FenetreTable("Find models", totalLines, null, rs2, columnNames, new int[] { 1, 2, 3, 4 }, connection);
						fen.setPanelBorderTitle("Models with keywords matching one of: " + matching);
					}
					success = true;
					return true;
				} else {
					if (!debug) {
						String message = "Aucun modèle trouvé comportant ces mot clés";
						JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
					}
					// System.exit(1);
					success = true;
					return false;
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
			return false;
		}
		return false;
	}

	/**
	 * Crée la fenêtre d'insertion de modèle
	 * 
	 * @param connection
	 * @return si fenêtre crée. Pour savoir si requête sql éxecutée, voir
	 *         {@link FenetreTable}
	 */
	public static boolean add(Connection connection) {
		String[] columnNames = { "Nom", "Chemin", "Date", "Description" };
		String[] buttonNames = new String[] { "Confirmer", "Reset" };
		FenetreTable fen = new FenetreTable("Add a model", 1, buttonNames, columnNames, new int[] { 3 }, connection);
		fen.setPanelBorderTitle("Set model information: ");
		return true;
	}

	/**
	 * Vérifie les argumuments et puis crée la fenetre de modification du modèle
	 * précisé s'ils sont valides
	 * 
	 * @param i
	 * @param args
	 * @param connection
	 * @return si fenêtre bien crée. Pour savoir si requête sql éxecutée, voir
	 *         {@link FenetreTable}
	 */
	private static boolean edit(int i, String[] args, Connection connection) {
		
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
						FenetreTable fen = new FenetreTable("Insert", totalLines, buttonNames, rs2, columnNames, new int[] { 3 }, connection);
						success = true;
						return true;
					} else {
						String message = "Le modèle " + firstFile + " n'existe pas";
						JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
						// System.exit(1);
						success = true;
						return false;
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
			return false;
		} else {
			String message = "Pas de nom précisé\nUtilisation: basededonneés --name <name>";
			JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			// System.exit(1);
			return false;
		}
		return false;
	}

	/**
	 * @param i
	 * @param args
	 * @param connection
	 * @return si modèle bien supprimée
	 */
	private static boolean delete(int i, String[] args, Connection connection, boolean debug) {
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
					return result > 0;
				} else {
					if (!debug) {
						String message = "Le modèle " + firstFile + " n'existe pas";
						JOptionPane.showMessageDialog(null, message, "Erreur", JOptionPane.ERROR_MESSAGE);
					}
					// System.exit(1);
					success = true;
					return false;
				}
			} else {
				if (!debug) {
					String message = "Le modèle " + firstFile + " n'existe pas";
					JOptionPane.showMessageDialog(null, message, "Erreur", JOptionPane.ERROR_MESSAGE);
				}
				// System.exit(1);
				success = true;
				return false;
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
		return false;
	}
}
