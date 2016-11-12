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

import bddInterface.Fenetre;

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

	public static void main(String[] args) throws ClassNotFoundException {
		// load the sqlite-JDBC driver using the current class loader
		// Class.forName("org.sqlite.JDBC");
		// ucanacces for Java 8, to replace when using Java 7
		Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		Connection connection = null;

		try {
			// mise des noms du dossier ply dans un tableau de string
			new BaseDeDonnees();

			// creation de la table
			// connection =
			// DriverManager.getConnection("jdbc:sqlite:test.sqlite");
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

			// declaration de rs pour les affichages
			ResultSet rs;
			ResultSet rs2;

			if (args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					if (args[i].equals("--name")) {
						if (args.length - 1 == i + 1) {
							String firstFile = args[i + 1];
							PreparedStatement statement = connection.prepareStatement("select * from PLY where NOM = ?");
							statement.setString(1, firstFile);
							rs = statement.executeQuery();
							PreparedStatement statement2 = connection.prepareStatement("select * from PLY where NOM = ?");
							statement2.setString(1, firstFile);
							rs2 = statement.executeQuery();
							Fenetre fen = new Fenetre(firstFile, rs, rs2);
						} else if (args.length - 1 == i){
							String message = "Vous n'avez pas spécifié de nom\nUtilisation: basededonneés --name <name>";
							JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
						} else {
							String message = "Trop d'arguments\nUtilisation: basededonneés --name <name>";
							JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
						}
					}
					if (args[i].equals("--all")) {
						if (args.length - 1 == i){
							PreparedStatement statement = connection.prepareStatement("select * from PLY");
							rs = statement.executeQuery();
							PreparedStatement statement2 = connection.prepareStatement("select * from PLY");
							rs2 = statement2.executeQuery();
							Fenetre fen = new Fenetre("Tous les modèles", rs, rs2);
						} else {
							String message = "Trop d'arguments\nUtilisation: basededonneés --all";
							JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
						}
					}
					if (args[i].equals("--find")) {
						String matching = "";
						if (args.length-1 > i) {
							String queryString = "select * from PLY where DESCRIPTION like";
							for (int j=i+1;j<args.length;j++) {
								if (j == i+1) {
									queryString += " ?";
								} else {
									queryString += " union select * from PLY where DESCRIPTION LIKE ?";
								}
							}
							queryString += ";";
							PreparedStatement statement = connection.prepareStatement(queryString);
							PreparedStatement statement2 = connection.prepareStatement(queryString);
							for (int j=i+1;j<args.length;j++) {
								statement.setString(j, "%" + args[j] + "%");
								statement2.setString(j, "%" + args[j] + "%");
								if (j<args.length-1) {
									matching += "\"" + args[j] + "\", ";
								} else {
									matching += "\"" + args[j] + "\"";
								}
							}
							rs = statement.executeQuery();
							rs2 = statement.executeQuery();
							Fenetre fen = new Fenetre("Find models", rs, rs2);
							fen.setBorderTitle("Models with keywords matching one of: " + matching);
						} else {
							String message = "Pas de mots clés spécifiés\nUtilisation: basededonneés --find <mots clés>..";
							JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
						}
					}
					if (args[i].equals("--add")) {
						String firstFile = args[i + 1];
						PreparedStatement statement = connection.prepareStatement("insert into PLY values ? ? ? ?");
						statement.setString(1, firstFile);
						statement.setString(2, "ply/" + firstFile + ".ply");
						statement.setString(3, toDay());
						statement.setString(4, args[i + 2]);
						statement.executeUpdate();
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
}
