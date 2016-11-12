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

import bddInterface.Fenetre;

public class BaseDeDonnees {

	static String[] items;

	public BaseDeDonnees(){
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
//		Class.forName("org.sqlite.JDBC");
		// ucanacces for Java 8, to replace when using Java 7
		Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		Connection connection = null;

		try {
			// mise des noms du dossier ply dans un tableau de string
			new BaseDeDonnees();

			// creation de la table
//			connection = DriverManager.getConnection("jdbc:sqlite:test.sqlite");
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
					firstStatement.executeUpdate("insert into PLY values " + "('" + nom + "', 'ply/" + items[i] + "', '"
							+ toDay() + "' ,'mes mots clés')");
				}
			}
			
			// declaration de rs pour les affichages
			ResultSet rs;

			if (args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					if (args[i].equals("--name")) {
						PreparedStatement statement = connection.prepareStatement("select * from PLY where NOM = ?");
						statement.setString(1, args[i + 1]);
						rs = statement.executeQuery();
						affichageTable(rs);
					}if (args[i].equals("--all")) {
						PreparedStatement statement = connection.prepareStatement("select * from PLY");
						rs = statement.executeQuery();
						PreparedStatement statement2 = connection.prepareStatement("select * from PLY");
						ResultSet rs2 = statement2.executeQuery();
						Fenetre fen = new Fenetre("Tous les modèles", rs, rs2);
					}if (args[i].equals("--find")) {
						PreparedStatement statement = connection.prepareStatement("select * from PLY where DESCRIPTION like ?");
						statement.setString(1, "%" + args[i + 1] + "%");
						rs = statement.executeQuery();
						affichageTable(rs);
					}if(args[i].equals("--add")){
						PreparedStatement statement = connection.prepareStatement("insert into PLY values ? ? ? ?");
						statement.setString(1, args[i + 1]);
						statement.setString(2, "ply/" + args[i + 1] + ".ply");
						statement.setString(3, toDay());
						statement.setString(4, args[i + 2]);
						statement.executeUpdate();
					}if(args[i].equals("--delete")){
						PreparedStatement statement = connection.prepareStatement("delete * from PLY where NOM = ?");
						statement.setString(1, args[i + 1]);
						int result = statement.executeUpdate();
						if (result == 0) {
							System.out.println("successfully deleted " + args[i + 1]);
						} else {
							System.out.println("did not delete " + args[i + 1]);
						}
					}if(args[i].equals("--edit")){
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
