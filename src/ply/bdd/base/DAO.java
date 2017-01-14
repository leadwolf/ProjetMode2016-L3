package ply.bdd.base;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import main.Modelisationator;

/**
 * Cette classe récupère les ResultSet utilisés par l'application.
 * 
 * @author L3
 *
 */
public class DAO {

	public final static DAO INSTANCE = new DAO();

	/**
	 * Constructeur privé pour empêcher instanciation.
	 */
	private DAO() {
	}

	/**
	 * @return tout la base.
	 */
	public ResultSet getAll() {
		PreparedStatement st;
		try {
			st = BDDUtilities.getConnection().prepareStatement("select * from PLY");
			return st.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param args les mots cles a rechercher
	 * @return les modeles contenant les mots cles
	 */
	public ResultSet find(String[] args) {
		int nbLikes = 0;
		int nbTreated = 0;
		for (int j = 0; j < args.length; j++) {
			if (!args[j].startsWith("--")) {
				nbLikes++;
			}
		}
		try {
			String queryString = "select * from PLY where DESCRIPTION like";
			for (int j = 0; j < args.length; j++) {
				if (!BDDUtilities.isDBOption(args[j])) {
					if (nbTreated == 0) {
						queryString += " ?";
					} else {
						queryString += " OR DESCRIPTION LIKE ?";
					}
					nbTreated++;
				}
			}
			PreparedStatement st = BDDUtilities.getConnection().prepareStatement(queryString);
			for (int j = 0; j < args.length; j++) {
				if (!args[j].startsWith("--")) {
					st.setString(j + 1, "%" + args[j] + "%");
				}
			}
			return st.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Trouve les lignes dont la description se compose par au moins par un des keywords ou dont le nom se compose par un des
	 * keywords.
	 * 
	 * @param keywords les mots cles a rechercher
	 * @return les modeles contenant les mots cles
	 */
	public ResultSet findKeywordsAndName(String[] keywords) {
		int nbLikes = 0;
		int nbTreated = 0;
		for (int j = 0; j < keywords.length; j++) {
			if (!keywords[j].startsWith("--")) {
				nbLikes++;
			}
		}
		try {
			String queryString = "select * from PLY where DESCRIPTION || NOM like";
			for (int j = 0; j < keywords.length; j++) {
				if (!BDDUtilities.isDBOption(keywords[j])) {
					if (nbTreated == 0) {
						queryString += " ?";
					} else {
						queryString += " OR DESCRIPTION || NOM LIKE ?";
					}
					nbTreated++;
				}
			}
			PreparedStatement st = BDDUtilities.getConnection().prepareStatement(queryString);
			for (int j = 0; j < keywords.length; j++) {
				if (!keywords[j].startsWith("--")) {
					st.setString(j + 1, "%" + keywords[j] + "%");
				}
			}
			return st.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param modelName
	 * @return le modele correspondant à modelName
	 */
	public ResultSet getAllByName(String modelName) {
		try {
			PreparedStatement stCount = BDDUtilities.getConnection().prepareStatement("select * from PLY where NOM = ?");
			stCount.setString(1, modelName);
			return stCount.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param columnName le nom de la colonne voulue
	 * @return un ResultSet ayant comme seule colonne columnName
	 */
	public ResultSet getColumn(String columnName) {
		try {
			PreparedStatement st = BDDUtilities.getConnection().prepareStatement("select " + columnName + " from PLY");
			return st.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param modelName
	 * @return le nom correspondant au chemin voulue
	 */
	public Path getPathByName(String modelName) {
		Path result = null;
		try {
			PreparedStatement st = BDDUtilities.getConnection().prepareStatement("select CHEMIN from PLY where NOM = ?");
			st.setString(1, modelName);
			ResultSet rs = st.executeQuery();
			result = Paths.get(rs.getString(1));
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Donne toutes les colonnes de la base de données
	 * 
	 * @param name the name of the model
	 * @param quiet true pour empecher affichage.
	 * @return un String[] contentant toute la ligne de la base ou null s'il y a eu erreur.
	 */
	public String[] getNameInfo(String name, boolean quiet) {
		try {
			name = name.toLowerCase();
			ResultSet rs = DAO.INSTANCE.getAllByName(name);
			if (rs.next()) {
				String[] nameInfo = new String[rs.getMetaData().getColumnCount()];
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					nameInfo[i - 1] = rs.getString(i);
				}
				BDDUtilities.closeConnection();
				return nameInfo;
			} else {
				if (!quiet) {
					JOptionPane.showMessageDialog(null, "Le modèle " + name + " n'est pas dans la base", Modelisationator.NAME,
							JOptionPane.ERROR_MESSAGE);
				}
				BDDUtilities.closeConnection();
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
