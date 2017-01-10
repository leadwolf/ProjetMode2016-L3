package ply.bdd.other;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	public ResultSet getName(String modelName) {
		try {
			PreparedStatement stCount = BDDUtilities.getConnection().prepareStatement("select * from PLY where NOM = ?");
			stCount.setString(1, modelName);
			return stCount.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
