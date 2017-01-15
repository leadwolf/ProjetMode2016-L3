package ply.bdd.base;

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

import javax.swing.JOptionPane;

import ply.plyModel.modeles.FigureModel;
import ply.result.BDDResult;
import ply.result.BasicResult;
import ply.result.MethodResult;
import ply.result.BDDResult.BDDResultEnum;
import ply.result.BasicResult.BasicResultEnum;

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
	 * Vérifie les chemins stockés dans la base de données. Si plus qu'une mene vers un chemin inexistant, on demande à l'utilisateur si on veut executer
	 * resetTable() et fillTable().
	 */
	public static void checkPaths() {
		double nbNotFiles = 0;
		double totalModels = 0;
		try {
			ResultSet all = DAO.INSTANCE.getColumn("CHEMIN");
			while (all.next()) {
				totalModels++;
				Path currentPath = Paths.get(all.getString(1));
				if (!currentPath.toFile().exists()) {
					nbNotFiles++;
				}
			}
			// si plus de la moitie des paths ne sont pas des fichiers
			if (nbNotFiles >= 1) {
				String message = "" + (int) nbNotFiles + " sur " + (int) totalModels
						+ " chemins dans la base conduisent vers de(s) fichier(s) .ply inexistant(s). \nVoulez vous réinitialiser la base en prenant les "
						+ "informations des modèles dans le dossier data/ ?" + "\n(Pareil que l'option--rf)";
				String[] options = new String[] { "Oui, merci", "Non merci, je comprend les risques" };
				int n = JOptionPane.showOptionDialog(null, message, "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						options, options[1]);
				if (n == JOptionPane.YES_OPTION) {
					resetTable();
					fillTable();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
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

	/**
	 * @param arg l'agument à vérifier.
	 * @return si l'arg correspond à une option d'<b>éxécution</b> de bdd.
	 */
	public static boolean isExecutableArg(String arg) {
		return arg.equals("--name") || arg.equals("--all") || arg.equals("--find") || arg.equals("--add") || arg.equals("--delete")
				|| arg.equals("--edit");
	}

	/**
	 * @param arg
	 * @return si c'est une option (--rf) et non une commande à lancer (--all/--find/...).
	 */
	public static boolean isDBOption(String arg) {
		return arg.equals("--r") || arg.equals("--r") || arg.equals("--f") || arg.equals("--rf") || arg.equals("--fr") || arg.equals("--reset")
				|| arg.equals("--fill");
	}

}
