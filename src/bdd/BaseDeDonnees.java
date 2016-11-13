package bdd;

import java.awt.FlowLayout;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import bddInterface.FenetreEdit;
import bddInterface.FenetreView;

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
			// Class.forName("org.sqlite.JDBC");
			
			// ucanacces for Java 8, to replace when using Java 7
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

			// mise des noms du dossier ply dans un tableau de string
			new BaseDeDonnees();

			// creation de la table
			// connection = DriverManager.getConnection("jdbc:sqlite:test.sqlite");
			
			// replace by line above when using Java 7
			connection = DriverManager.getConnection("jdbc:ucanaccess://C:/Users/Master/git/test.accdb");

			boolean restart = false;

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
						FenetreEdit fen = new FenetreEdit("Insert", 1, 4, columnNames,  new int[]{3});
					}
					if (args[i].equals("--delete")) {
						String firstFile = args[i + 1];
						PreparedStatement statement = connection.prepareStatement("delete * from PLY where NOM = ?");
						statement.setString(1, firstFile);
						int result = statement.executeUpdate();
						if (result == 0) {
							System.out.println("successfully deleted " + firstFile);
						} else {
							System.out.println("did not delete " + firstFile);
						}
					}
					if (args[i].equals("--edit")) {
						// ouverture formulaire
					}
				}
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
	}

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
					FenetreView fen = new FenetreView(firstFile, totalLines, rs2);
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
					FenetreView fen = new FenetreView("Tous les modèles", totalLines, rs2);
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
					FenetreView fen = new FenetreView("Find models", totalLines, rs2);
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
}
