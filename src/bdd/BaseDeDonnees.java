package bdd;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

	public static String insert(String nom, String motcles) {
		return "insert into PLY values " + "('" + nom + "', 'ply/" + nom + ".ply', '" + toDay() + "' ,'" + motcles
				+ "')";

	}
	
	

	public static void main(String[] args) throws ClassNotFoundException {
		System.out.println(33 / 2);
		// load the sqlite-JDBC driver using the current class loader
		Class.forName("org.sqlite.JDBC");
		Connection connection = null;

		try {
			// mise des noms du dossier ply dans un tableau de string
			new BaseDeDonnees();

			// creation de la table
			connection = DriverManager.getConnection("jdbc:sqlite:test.sqlite");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			statement.executeUpdate("drop table PLY");
			statement.executeUpdate("create table PLY(NOM text, CHEMIN text, DATE text, DESCRIPTION text)");

			// insertion des donnees dans la bdd
			for (int i = 0; i < items.length; i++) {
				String nom = items[i].substring(0, items[i].lastIndexOf(".ply"));
				statement.executeUpdate("insert into PLY values " + "('" + nom + "', 'ply/" + items[i] + "', '"
						+ toDay() + "' ,'mes mots clés')");
			}
			
			// declaration de rs pour les affichages
			ResultSet rs;

			if (args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					if (args[i].equals("--name")) {
						rs = statement.executeQuery("select * from PLY where NOM = '" + args[i + 1] + "'");
						affichageTable(rs);
					}if (args[i].equals("--all")) {
						rs = statement.executeQuery("select * from PLY");
						affichageTable(rs);
					}if (args[i].equals("--find")) {
						rs = statement.executeQuery("select * from PLY where DESCRIPTION like '%" + args[i + 1] + "%'");
						affichageTable(rs);
					}if(args[i].equals("--add")){
						statement.executeQuery(insert(args[i+1], args[i+2]));
					}if(args[i].equals("--delete")){
						statement.executeQuery("Delete * from PLY where NOM = '" + args[i+1] +"'");
					}if(args[i].equals("--edit")){
						// ouverture formulaire
					}

				}
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
	}
}
