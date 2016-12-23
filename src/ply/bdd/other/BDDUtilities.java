package ply.bdd.other;

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

import ply.plyModel.modeles.FigureModel;
import result.BDDResult;
import result.BDDResultEnum;
import result.BasicResult;
import result.BasicResultEnum;
import result.MethodResult;

/**
 * Contient toutes les méthodes nécessaires à la manipulation de la base
 * 
 * @author L3
 *
 */
public class BDDUtilities {

	private static Connection connection;
	private static Path previousPath;
	/**
	 * Le chemin vers le fichiers .ply
	 */
	private static File[] files;

	public BDDUtilities(Path dbPath) {
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

	/**
	 * We don't need dbPath as parameter here because if we are using this method, we must have initialized a connection beforehand. Therefore we use the same
	 * file.
	 * 
	 * @return
	 */
	public static Connection getConnection() {
		try {
			if (connection == null || (connection != null && connection.isClosed())) {
				initConnection(previousPath);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}

	/**
	 * Initalise la connection vers un fichier .sqlite précis ou le fichier par défaut
	 * 
	 * @param dbPath
	 */
	public static void initConnection(Path dbPath) {
		previousPath = dbPath;
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
					closeConnection();
				}
			}
		} else {
			boolean success = false;
			try {
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath.toAbsolutePath().toString());
				success = true;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (!success) {
					closeConnection();
				}
			}

		}
	}

	/**
	 * Ferme la connection utilisée par cette classe
	 */
	public static void closeConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Drop la table PLY et la recrée
	 */
	public static void resetTable() {
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
				closeConnection();
			}
		}

	}

	/**
	 * Remplit la table PLY avec les fichiers dans le dossier ply/
	 * 
	 */
	public static void fillTable() {
		boolean success = false;
		BDDUtilities utilities = new BDDUtilities(Paths.get("data/"));
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
				closeConnection();
			}
		}
	}

	/**
	 * Vérifie si la table est vide
	 * 
	 * @param connection
	 * @return
	 */
	public static MethodResult checkTable() {
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
				closeConnection();
			}
		}
		return new BasicResult(BasicResultEnum.UNKNOWN_ERROR);
	}

	/**
	 * @return la date d'aujourd'hui sous forme YYYY/MM/DD
	 */
	private static String toDay() {
		Calendar dat = new GregorianCalendar();
		return dat.get(Calendar.YEAR) + "/" + dat.get(Calendar.MONTH) + "/" + dat.get(Calendar.DAY_OF_MONTH);
	}
}
