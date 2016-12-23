package ply.bdd.modeles;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import ply.bdd.other.BDDUtilities;
import result.BDDResult;
import result.BDDResultEnum;
import result.MethodResult;

public class JTableBDD extends JTable {

	/**
	 * Sauvegarde des données initiales de la ligne lors du chargement de la base
	 */
	String[] orignalFields;
	
	public JTableBDD() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Prend les valeurs à update du JTable, vérifie qu'elles sont différentes que les originales et exécute
	 * {@link #updateTable(String, String, String, boolean)}
	 * 
	 * @param debug afficher ou non les JOptionPane d'erreur/succès
	 * @return si le modèle a bien été mis à jour
	 */
	public MethodResult updateTableAmorce(boolean debug) {

		String name = "";
		String chemin = "";
		String keywords = "";

		name = (String) getModel().getValueAt(0, 0);
		chemin = (String) getModel().getValueAt(0, 1);
		keywords = (String) getModel().getValueAt(0, 3);

		// si au moins un des 3 est renseigné et différent de l'original
		if ((name != null && !name.equals("") && !name.equals(orignalFields[0]))
				|| (chemin != null && !chemin.equals("") && !chemin.equals(orignalFields[1]))
				|| (keywords != null && !keywords.equals("") && !keywords.equals(orignalFields[3]))) {
			return updateTable(name, chemin, keywords, debug);
		} else if ((name != null && name.equals(orignalFields[0])) || (chemin != null && chemin.equals(orignalFields[1]))
				|| (keywords != null && keywords.equals(orignalFields[3]))) {
			if (!debug) {
				showFieldErrorMessage(name, chemin, keywords);
			}
			return new BDDResult(BDDResultEnum.NO_DIFFERENT_VALUES);
		} else if ((name != null && name.equals("")) && (chemin != null && chemin.equals("")) && (keywords != null && keywords.equals(""))) {
			if (!debug) {
				showFieldErrorMessage(name, chemin, keywords);
			}
			return new BDDResult(BDDResultEnum.NO_VALUES_SPECIFIED);
		}
		return new BDDResult(BDDResultEnum.UPDATE_NOT_SUCCESSFUL);
	}

	/**
	 * Prend les valeurs à update en paramètre, vérifie qu'elles sont différentes que les originales et exécute
	 * {@link #updateTable(String, String, String, boolean)}
	 * 
	 * @param name
	 * @param chemin
	 * @param keywords
	 * @param debug afficher ou non les JOptionPane d'erreur/succès
	 * @return si le modèle a bien été mis à jour
	 */
	public MethodResult updateTableAmorce(String name, String chemin, String keywords, boolean debug) {
		if ((name != null && !name.equals("") && !name.equals(orignalFields[0]))
				|| (chemin != null && !chemin.equals("") && !chemin.equals(orignalFields[1]))
				|| (keywords != null && !keywords.equals("") && !keywords.equals(orignalFields[3]))) {
			return updateTable(name, chemin, keywords, debug);
		} else if ((name != null && name.equals(orignalFields[0])) || (chemin != null && chemin.equals(orignalFields[1]))
				|| (keywords != null && keywords.equals(orignalFields[3]))) {
			if (!debug) {
				showFieldErrorMessage(name, chemin, keywords);
			}
			return new BDDResult(BDDResultEnum.NO_DIFFERENT_VALUES);
		} else if ((name != null && name.equals("")) && (chemin != null && chemin.equals("")) && (keywords != null && keywords.equals(""))) {
			if (!debug) {
				showFieldErrorMessage(name, chemin, keywords);
			}
			return new BDDResult(BDDResultEnum.NO_VALUES_SPECIFIED);
		}
		return new BDDResult(BDDResultEnum.UPDATE_NOT_SUCCESSFUL);
	}

	/**
	 * Exécute la requête SQL qui met à jour la ligne de la table
	 * 
	 * @param name
	 * @param chemin
	 * @param keywords
	 * @param debug
	 * @return si l'update a bien modifié plus d'une ligne
	 */
	private MethodResult updateTable(String name, String chemin, String keywords, boolean debug) {
		boolean success = false;
		try {
			Class.forName("org.sqlite.JDBC");
			PreparedStatement statement;

			int updateCpt = 0;
			boolean updateNom = false, updateChemin = false, updateDesc = false;
			String updateString = "update PLY set ";
			if (name != null && !name.equals("") && name != orignalFields[0]) {
				updateString += "nom = ?";
				updateNom = true;
				updateCpt++;
			}
			if (chemin != null && !chemin.equals("")) {
				if (updateCpt == 0) {
					updateString += "chemin = ?";
				} else {
					updateString += ", chemin = ?";
				}
				updateChemin = true;
				updateCpt++;
			}
			if (keywords != null && !keywords.equals("")) {
				if (updateCpt == 0) {
					updateString += "description = ?";
				} else {
					updateString += ", description = ?";
				}
				updateDesc = true;
				updateCpt++;
			}
			updateString += " where nom = ?";

			statement = BDDUtilities.getConnection().prepareStatement(updateString);
			if (updateNom) {
				statement.setString(1, name);
			} else if (updateCpt == 0 && updateChemin) {
				statement.setString(1, chemin);
			} else if (updateCpt > 1 && updateChemin) {
				statement.setString(2, chemin);
			} else if (updateCpt == 0 && updateDesc) {
				statement.setString(1, keywords);
			} else if (updateCpt > 1 && updateDesc) {
				statement.setString(2, keywords);
			} else if (updateCpt > 2 && updateDesc) {
				statement.setString(3, keywords);
			}
			statement.setString(updateCpt + 1, orignalFields[0]);

			int result = statement.executeUpdate();
			if (!debug && result > 0) {
				String message = "La mise à jour du modèle " + orignalFields[0] + " vers " + name + " été réalisée avec succès!";
				JOptionPane.showMessageDialog(null, message);
			} else if (!debug && result <= 0) {
				String message = "La mise à jour du modèle " + orignalFields[0] + " vers " + name + " n'a pas pu être réalisée";
				JOptionPane.showMessageDialog(null, message);
			}
			// System.exit(0);
			success = true;
			if (result > 0) {
				return new BDDResult(BDDResultEnum.UPDATE_SUCCESSFUL);
			}
			return new BDDResult(BDDResultEnum.UPDATE_NOT_SUCCESSFUL);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (!success) {
				try {

					BDDUtilities.getConnection().close();
				} catch (SQLException e) {
					System.err.println(e);
				}
			}
		}
		if (!debug) {
			String message = "La mise à jour du modèle " + orignalFields[0] + " vers " + name + " n'a pas pu être réalisée";
			JOptionPane.showMessageDialog(null, message);
		}
		return new BDDResult(BDDResultEnum.UPDATE_NOT_SUCCESSFUL);
	}

	/**
	 * Prend les valeurs à insérer du JTable, vérifie si le nom du modèle à insérer n'existe pas et éxecute
	 * {@link #insertTable(String, String, String, boolean)}
	 * 
	 * @param debug
	 * @return si l'insertion du modèle a pu être réalisée
	 */
	public MethodResult insertTableAmorce(boolean debug) {

		String name = "";
		String chemin = "";
		String keywords = "";

		name = (String) getModel().getValueAt(0, 0);
		chemin = (String) getModel().getValueAt(0, 1);
		keywords = (String) getModel().getValueAt(0, 3);

		// il faut qu'au moins le nom du modèle soit renseigné
		if ((name != null && !name.equals("")) || ((chemin != null && !chemin.equals("")) || (keywords != null && !keywords.equals("")))) {
			boolean success = false;
			try {
				Class.forName("org.sqlite.JDBC");
				PreparedStatement statement = BDDUtilities.getConnection().prepareStatement("select * from PLY where nom = ?");
				statement.setString(1, name);
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					if (!debug) {
						String message = "Le modèle " + name + " existe déja.";
						JOptionPane.showMessageDialog(null, message);
					}
					success = true;
					return new BDDResult(BDDResultEnum.PRE_EXISTING_MODEL);
				} else {
					success = true;
					return insertTable(name, chemin, keywords, debug);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (!success) {
					try {
						BDDUtilities.getConnection().close();
					} catch (SQLException e) {
						System.err.println(e);
					}
				}
			}
		} else {
			if (!debug) {
				showFieldErrorMessage(name, chemin, keywords);
			}
			return new BDDResult(BDDResultEnum.NO_VALUES_SPECIFIED);
		}
		return new BDDResult(BDDResultEnum.INSERT_NOT_SUCCESSFUL);
	}

	/**
	 * Prend les valeurs à insérer en paramètre, vérifie si le nom du modèle à insérer n'existe pas et éxecute
	 * {@link #insertTable(String, String, String, boolean)}
	 * 
	 * @param name
	 * @param chemin
	 * @param keywords
	 * @param debug
	 * @return si l'insertion du modèle a pu être réalisée
	 */
	public MethodResult insertTableAmorce(String name, String chemin, String keywords, boolean debug) {// il faut qu'au moins le nom du modèle soit renseigné
		if ((name != null && !name.equals("")) || ((chemin != null && !chemin.equals("")) || (keywords != null && !keywords.equals("")))) {
			boolean success = false;
			try {
				Class.forName("org.sqlite.JDBC");
				PreparedStatement statement = BDDUtilities.getConnection().prepareStatement("select * from PLY where nom = ?");
				statement.setString(1, name);
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					if (!debug) {
						String message = "Le modèle " + name + " existe déja.";
						JOptionPane.showMessageDialog(null, message);
					}
					success = true;
					return new BDDResult(BDDResultEnum.PRE_EXISTING_MODEL);
				} else {
					success = true;
					return insertTable(name, chemin, keywords, debug);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (!success) {
					try {
						BDDUtilities.getConnection().close();
					} catch (SQLException e) {
						System.err.println(e);
					}
				}
			}
		} else {
			if (!debug) {
				showFieldErrorMessage(name, chemin, keywords);
			}
			return new BDDResult(BDDResultEnum.NO_VALUES_SPECIFIED);
		}
		return new BDDResult(BDDResultEnum.INSERT_NOT_SUCCESSFUL);
	}

	/**
	 * Crée une requête SQL en fonction de la validité des paramètres fournis et l'éxecute
	 * 
	 * @param name
	 * @param chemin
	 * @param keywords
	 * @param debug
	 * @return si l'insertion du modèle a pu être réalisée
	 */
	private MethodResult insertTable(String name, String chemin, String keywords, boolean debug) {
		boolean success = false;
		try {
			Class.forName("org.sqlite.JDBC");
			PreparedStatement statement;

			statement = BDDUtilities.getConnection().prepareStatement("insert into PLY values(?, ?, ?, ?);");

			if (name != null && !name.equals("")) {
				statement.setString(1, name);
			} else {
				statement.setString(1, "");
			}
			if (chemin != null && !chemin.equals("")) {
				statement.setString(2, chemin);
			} else {
				statement.setString(2, "");
			}
			statement.setString(3, LocalDate.now().toString());
			if (keywords != null && !keywords.equals("")) {
				statement.setString(4, keywords);
			} else {
				statement.setString(4, "");
			}

			int result = statement.executeUpdate();
			if (!debug && result > 0) {
				String message = "L'insertion du modèle " + name + " a été réalisée avec succès!";
				JOptionPane.showMessageDialog(null, message);
			} else if (!debug && result <= 0) {
				String message = "L'insertion du modèle " + name + " n'a pas pu être réalisée";
				JOptionPane.showMessageDialog(null, message);
			}
			// System.exit(0);
			success = true;
			if (result > 0) {
				return new BDDResult(BDDResultEnum.INSERT_SUCCESSFUL);
			}
			return new BDDResult(BDDResultEnum.INSERT_NOT_SUCCESSFUL);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (!success) {
				try {
					BDDUtilities.getConnection().close();
				} catch (SQLException e) {
					System.err.println(e);
				}
			}
		}
		if (!debug) {
			String message = "Le modèle " + name + " n'a pas pu être insérée";
			JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
		}
		return new BDDResult(BDDResultEnum.INSERT_NOT_SUCCESSFUL);
	}

	/**
	 * Vide les champs modifiables
	 */
	public void resetFields() {
		TableDataModel dataTableModel = (TableDataModel) getModel();
		for (int i = 0; i < dataTableModel.getRowCount(); i++) {
			for (int j = 0; j < dataTableModel.getColumnCount(); j++) {
				if (dataTableModel.isCellEditable(i, j)) {
					dataTableModel.setValueAt("", i, j);
				}
			}
		}
	}

	/**
	 * <b>A REFAIRE CAR CHANGMENT DE PRATIQUE DE REMPLISSAGE = il suffit q'un champ soit rensigné</b><br>
	 * Crée un JOptionPane d'erreur en fonction de la validité du nom, chemin et keywords
	 * 
	 * @param name
	 * @param chemin
	 * @param keywords
	 */
	public void showFieldErrorMessage(String name, String chemin, String keywords) {

		String message = "";
		int iter = 0;
		if (name == null || name.equals("")) {
			iter++;
		}
		if (chemin == null || chemin.equals("")) {
			iter++;
		}
		if (keywords == null || keywords.equals("")) {
			iter++;
		}
		if (iter > 1) {
			message += "Les champs";
		} else {
			message += "Le champ";
		}
		if (name == null || name.equals("")) {
			message += " nom";
		}
		if (chemin == null || chemin.equals("")) {
			if (iter != 0) {
				if (iter > 2 && message.contains("nom")) {
					message += ", chemin";
				} else if (message.contains("nom")) {
					message += " et chemin";
				} else {
					message += " chemin";
				}
			} else {
				message += " chemin";
			}
		}
		if (keywords == null || keywords.equals("")) {
			if (iter != 0) {
				if (iter > 2 && message.contains("nom")) {
					message += ", et keyword";
				} else if (iter > 1) {
					message += " et keyword";
				} else {
					message += "keyword";
				}
			} else {
				message += "keyword";
			}
		}
		if (iter == 1) {
			message += " n'a pas été précisé.";
		} else {
			message += " n'ont pas été précisés.";
		}
		message += "\nVeuillez remplir tous les champs.";
		JOptionPane.showMessageDialog(null, message, "Erreur d'insertion", JOptionPane.ERROR_MESSAGE);
	}

}
