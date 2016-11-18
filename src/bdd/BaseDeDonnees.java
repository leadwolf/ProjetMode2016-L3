package bdd;

import java.awt.FlowLayout;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import bddInterface.FenetreTable;

public class BaseDeDonnees {

	static String[] items;

	public BaseDeDonnees() {
		File path = new File("ply/");
		items = path.list();
	}

	public static void affichageTable(ResultSet rs) throws SQLException {
		while (rs.next()) {
			System.out.println("nom = " + rs.getString("NOM"));
			System.out.println("chemin = " + rs.getString("CHEMIN"));
			System.out.println("date = " + rs.getString("DATE"));
			System.out.println("description = " + rs.getString("DESCRIPTION"));
			System.out.println();
		}
	}

	public static String toDay() {
		Calendar dat = new GregorianCalendar();
		return dat.get(Calendar.YEAR) + "/" + dat.get(Calendar.MONTH) + "/" + dat.get(Calendar.DAY_OF_MONTH);
	}

	public static void main(String[] args) {
		Connection connection = null;
		try {

			// load the sqlite-JDBC driver using the current class loader
			 Class.forName("org.sqlite.JDBC");
			// mise des noms du dossier ply dans un tableau de string
			new BaseDeDonnees();

			connection = DriverManager.getConnection("jdbc:sqlite:test.sqlite");

			boolean restart = false;

//			 creation de la table
			if (restart) {
				Statement firstStatement = connection.createStatement();
				firstStatement.setQueryTimeout(30);
				firstStatement.executeUpdate("drop table PLY");
				firstStatement.executeUpdate("create table PLY(NOM text, CHEMIN text, DATE text, DESCRIPTION text)");

				// insertion des donnees dans la bdd
				for (int i = 0; i < items.length; i++) {
					String nom = items[i].substring(0, items[i].lastIndexOf(".ply"));
					firstStatement.executeUpdate("insert into PLY values " + "('" + nom + "', 'ply/" + items[i] + "', '" + toDay() + "' ,'mes mots clés')");
				}
			}

			if (args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					if (args[i].equals("--name")) {
						showName(i, args, connection);
					}
					if (args[i].equals("--all")) {
						showAll(i, args, connection);
					}
					if (args[i].equals("--find")) {
						find(i, args, connection);
					}
					if (args[i].equals("--add")) {
						String[] columnNames = { "Nom", "Chemin", "Date", "Description" };
						String[] buttonNames = new String[] { "Confirmer", "Reset" };
						FenetreTable fen = new FenetreTable("Add a model", 1, buttonNames, columnNames, new int[] { 3 });
						fen.setPanelBorderTitle("Set model information: ");
					}
					if (args[i].equals("--delete")) {
						delete(i, args, connection);
					}
					if (args[i].equals("--edit")) {
						edit(i, args, connection);
					}
				}
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
	}

	/**
	 * Vérifie et crée la fenetre d'informations sur le modèle précisé
	 * 
	 * @param i
	 * @param args
	 * @param connection
	 * @throws SQLException
	 */
	public static void showName(int i, String[] args, Connection connection) throws SQLException {
		ResultSet rs;
		ResultSet rs2;
		if (args.length - 1 == i + 1) {
			String firstFile = args[i + 1];
			PreparedStatement statement = connection.prepareStatement("select COUNT(*) from PLY where NOM = ?");
			statement.setString(1, firstFile);
			rs = statement.executeQuery();
			if (rs.next()) {
				int totalLines = rs.getInt(1);
				if (totalLines > 0) {
					PreparedStatement statement2 = connection.prepareStatement("select * from PLY where NOM = ?");
					statement2.setString(1, firstFile);
					rs2 = statement2.executeQuery();
					String[] columnNames = new String[] { "Nom", "Chemin", "Date", "Mot Clés" };
					FenetreTable fen = new FenetreTable(firstFile, totalLines, null, rs2, columnNames, new int[] { 1, 2, 3, 4 });
					fen.setPanelBorderTitle("");
				} else {
					String message = "Le modèle " + firstFile + " n'existe pas\nUtilisation: basededonneés --name <name>";
					JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}
			}
		} else if (args.length - 1 == i) {
			String message = "Vous n'avez pas spécifié de nom\nUtilisation: basededonneés --name <name>";
			JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} else {
			String message = "Trop d'arguments\nUtilisation: basededonneés --name <name>";
			JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	/**
	 * Vérifie les arguments et crée la fenetre listant toute la base
	 * 
	 * @param i
	 * @param args
	 * @param connection
	 * @throws SQLException
	 */
	public static void showAll(int i, String[] args, Connection connection) throws SQLException {
		ResultSet rs;
		ResultSet rs2;
		if (args.length - 1 == i) {
			PreparedStatement statement = connection.prepareStatement("select COUNT(*) from PLY");
			rs = statement.executeQuery();
			if (rs.next()) {
				int totalLines = rs.getInt(1);
				if (totalLines > 0) {
					PreparedStatement statement2 = connection.prepareStatement("select * from PLY");
					rs2 = statement2.executeQuery();
					String[] columnNames = new String[] { "Nom", "Chemin", "Date", "Mot Clés" };
					FenetreTable fen = new FenetreTable("All models", totalLines, null, rs2, columnNames, new int[] { 1, 2, 3, 4 });
					fen.setPanelBorderTitle("");
				} else {
					String message = "Il n'y a pas de modèles enregistré";
					JOptionPane.showMessageDialog(null, message, "Base de données vide", JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}
			}
		} else {
			String message = "Trop d'arguments\nUtilisation: basededonneés --all";
			JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	/**
	 * Vérifie les arguments et puis crée la fenetre listant les informations
	 * des modèles correspondants aux keywords
	 * 
	 * @param i
	 * @param args
	 * @param connection
	 * @throws SQLException
	 */
	public static void find(int i, String[] args, Connection connection) throws SQLException {
		ResultSet rs;
		ResultSet rs2;
		String matching = "";
		if (args.length - 1 > i) {
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
					rs2 = statement2.executeQuery();
					String[] columnNames = new String[] { "Nom", "Chemin", "Date", "Mot Clés" };
					FenetreTable fen = new FenetreTable("Find models", totalLines, null, rs2, columnNames, new int[] { 1, 2, 3, 4 });
					fen.setPanelBorderTitle("Models with keywords matching one of: " + matching);
				} else {
					String message = "Aucun modèle trouvé comportant ces mot clés";
					JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}
			}
		} else {
			String message = "Pas de mots clés spécifiés\nUtilisation: basededonneés --find <mots clés>..";
			JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	/**
	 * Vérifie les argumuments et puis crée la fenetre de modification du modèle
	 * précisé s'ils sont valides
	 * 
	 * @param i
	 * @param args
	 * @param connection
	 * @throws SQLException
	 */
	private static void edit(int i, String[] args, Connection connection) throws SQLException {
		if (args.length - 1 == i + 1) {
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
					FenetreTable fen = new FenetreTable("Insert", totalLines, buttonNames, rs2, columnNames, new int[] { 3 });
				} else {
					String message = "Le modèle " + firstFile + " n'existe pas";
					JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}
			}
		} else if (args.length > i + 1) {
			String message = "Trop d'arguments\nUtilisation: basededonneés --name <name>";
			JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} else {
			String message = "Pas de nom précisé\nUtilisation: basededonneés --name <name>";
			JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	private static void delete(int i, String[] args, Connection connection) throws SQLException {
		String firstFile = args[i + 1];
		PreparedStatement stExists = connection.prepareStatement("select COUNT(*) from PLY where NOM = ?");
		stExists.setString(1, firstFile);
		ResultSet rs = stExists.executeQuery();
		if (rs.next()) {
			int totalLines = rs.getInt(1);
			if (totalLines == 1) {
				PreparedStatement stDelete = connection.prepareStatement("delete * from PLY where NOM = ?");
				stDelete.setString(1, firstFile);
				int result = stDelete.executeUpdate();
				String message = "Le modèle " + firstFile + " a été supprimé avec succès!";
				JOptionPane.showMessageDialog(null, message);
				System.exit(0);
			} else {
				String message = "Le modèle " + firstFile + " n'existe pas";
				JOptionPane.showMessageDialog(null, message, "Erreur", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		} else {
			String message = "Le modèle " + firstFile + " n'existe pas";
			JOptionPane.showMessageDialog(null, message, "Erreur", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}
}
