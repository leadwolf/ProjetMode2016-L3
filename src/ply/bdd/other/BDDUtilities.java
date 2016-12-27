package ply.bdd.other;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

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
	private static String[] columnTypes;
	private static String[] columnNames;
	/**
	 * Liste de fichiers .ply
	 */
	private static File[] files;
	
	/**
	 * Initialise la liste de fichier .ply dans files.
	 * 
	 * @param dbPath le chemin vers le dossier contenant les fichier .ply
	 */
	private static void initModelFolder(Path dbPath) {
		files = dbPath.toFile().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.lastIndexOf('.') > 0) {
					String str = name.substring(name.lastIndexOf("."));
					// match path name extension
					if (str.equals(".ply")) { // on accepte que le fichiers .ply dans ce dossier
						return true;
					}
				}
				return false;
			}
		});
	}
	

	/**
	 * Donne la connection qui a été intialisé auparavant.
	 * 
	 * @return la connection qui a été initialisé précedemment.
	 */
	public static synchronized Connection getConnection() {
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
	 * Sauvegarde les noms des colonnes et leurs types.
	 */
	public static void setColumnInfo() {
		if (checkTable().getCode().equals(BDDResultEnum.DB_NOT_EMPTY)) {
			try {
				Statement st = getConnection().createStatement();
				ResultSet rs = st.executeQuery("PRAGMA table_info(PLY)");
				int colIndex = 0;
				columnTypes = new String[rs.getMetaData().getColumnCount()];
				columnNames = new String[rs.getMetaData().getColumnCount()];
				while (rs.next()) {
					columnNames[colIndex] = rs.getString(2);
					columnTypes[colIndex] = rs.getString(3);
					colIndex++;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static String[] getColumnTypes() {
		return columnTypes;
	}

	public static String[] getColumnNames() {
		return columnNames;
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

			PreparedStatement firstStatement = getConnection().prepareStatement("DROP TABLE IF EXISTS PLY");
			firstStatement.executeUpdate();
			PreparedStatement secondStatement = getConnection().prepareStatement(
					"create table PLY(NOM text PRIMARY KEY NOT NULL, CHEMIN text, DATE text, DESCRIPTION text, NOMBRE_POINTS integer, NOMBRE_FACES integer)");
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
	 * Remplit la table PLY avec les fichiers dans le dossier data/
	 * 
	 */
	public static void fillTable() {
		boolean success = false;
		initModelFolder(Paths.get("data/"));
		try {

			String insertStatement = "";
			for (int i = 0; i < files.length; i++) {
				String nom = files[i].toPath().getFileName().toString();
				nom = nom.substring(0, nom.lastIndexOf("."));
				FigureModel fig = new FigureModel(files[i].toPath().toAbsolutePath(), true);

				insertStatement = "insert into PLY values (?, ?, ?, ?, ?, ?)";
				PreparedStatement firstStatement;
				firstStatement = getConnection().prepareStatement(insertStatement);
				firstStatement.setString(1, nom);
				firstStatement.setString(2, files[i].getAbsolutePath().toString());
				firstStatement.setString(3, LocalDate.now().toString());
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
	 * @return un {@link MethodResult décrivant l'état de la base}
	 */
	public static MethodResult checkTable() {
		try {
			DatabaseMetaData md = connection.getMetaData();
			ResultSet rsTable = md.getTables(null, null, "PLY", null);
			if (rsTable.next()) {
				PreparedStatement statement;
				statement = connection.prepareStatement("select COUNT(*) from PLY");
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					int totalLines = rs.getInt(1);
					if (totalLines > 0) {
						return new BDDResult(BDDResultEnum.DB_NOT_EMPTY);
					}
				}
				return new BDDResult(BDDResultEnum.EMPTY_DB);
			} else {
				return new BDDResult(BDDResultEnum.TABLE_NOT_FOUND);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new BasicResult(BasicResultEnum.UNKNOWN_ERROR);
	}

}
