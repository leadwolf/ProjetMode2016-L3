package ply.bdd.modeles;

import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import main.vues.MainFenetre;
import ply.bdd.other.BDDUtilities;
import res.ButtonColumn;
import result.BDDResult;
import result.BDDResultEnum;
import result.MethodResult;

/**
 * Modèle de JTable, éxécute les requêtes sql.
 * 
 * @author L3
 *
 */
public class Table extends JTable {

	/**
	 * Generated ID.
	 */
	private static final long serialVersionUID = 3437754753518654239L;
	/**
	 * On sauvegarde les données initiales.
	 */
	private List<String[]> orignalData;
	/**
	 * La largeur des données, sans compter les colonnes de boutons.
	 */
	private int dataWidth;
	/**
	 * Si la dernière ligne crée a été ajouté à la base, on pourra ajouter une nouvelle ligne à insérer.
	 */
	private boolean lastRowIsInDB = true;

	/**
	 * La fenête à laquelle ceci est attaché. Utilisé pour indiquer à l'utilisateur que fait l'application.
	 */
	private MainFenetre mainFenetre;

	/**
	 * @param dm
	 * @param originalData
	 * @param buttonColumns
	 * @param mainFenetre
	 */
	public Table(TableDataModel dm, List<String[]> originalData, String[] buttonColumns, MainFenetre mainFenetre) {
		super(dm);
		this.orignalData = originalData;
		this.mainFenetre = mainFenetre;
		dataWidth = orignalData.get(0).length - 3;

		if (buttonColumns != null && buttonColumns.length > 0) {
			setButtons();
		}
	}

	/**
	 * @param lastRowIsInDB the lastRowIsInDB to set
	 */
	public void setLastRowIsInDB(boolean lastRowIsInDB) {
		this.lastRowIsInDB = lastRowIsInDB;
	}

	/**
	 * Ajoute les boutons "confirmer", "reset" et "delete"
	 */
	@SuppressWarnings({ "serial", "unused" })
	private void setButtons() {
		// Actions
		Action editAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int modelRow = Integer.valueOf(e.getActionCommand());
				boolean updateOrInsert = !((modelRow == getRowCount() - 1) && !lastRowIsInDB);
				if (modelRow != -1) {
					modifyTableDirect(modelRow, updateOrInsert, false);
				}
			}
		};

		Action resetAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int modelRow = Integer.valueOf(e.getActionCommand());
				if (modelRow != -1) {
					resetRow(modelRow);
				}
			}
		};

		Action deleteAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int modelRow = Integer.valueOf(e.getActionCommand());
				if (modelRow != -1) {
					deleteRow(modelRow, false);
				}
			}
		};

		// BUTTONS
		ButtonColumn editButton = new ButtonColumn(this, editAction, dataWidth);
		ButtonColumn resetButton = new ButtonColumn(this, resetAction, dataWidth + 1);
		ButtonColumn deleteButton = new ButtonColumn(this, deleteAction, dataWidth + 2);

		// SET BUTTONS EDITABLE FOR ACTIONS
		for (int col = dataWidth; col < dataWidth + 3; col++) {
			for (int i = 0; i < getRowCount(); i++) {
				((TableDataModel) getModel()).setCellEditable(i, col, true);
			}
		}
	}

	/**
	 * Exécute indirectement la mise à jour de la table. Cette méthode est appelé par le contrôleur
	 * 
	 * @param rowIndex l'index de la ligne où le bouton a été appuyé
	 * @param updateOrInsert true = update, false = insert
	 * @param quiet
	 * @return le résultat de l'opération sql ou si il n'a pas de valeurs différentes sur lesquelles opérer
	 */
	public MethodResult modifyTableDirect(int rowIndex, boolean updateOrInsert, boolean quiet) {
		// GET SELECTED ROW DATA
		int colCount = getModel().getColumnCount();
		boolean differentValues = false;
		String[] rowData = (String[]) ((TableDataModel) getModel()).getRow(rowIndex);
		for (int colIndex = 0; colIndex < colCount; colIndex++) {
			if (isCellDifferent(rowIndex, colIndex)) {
				differentValues = true;
			}
		}

		// CHECK IF UPDATE POSSIBLE
		if (!differentValues) {
			if (!quiet) {
				JOptionPane.showMessageDialog(null, "Vous n'avez pas changé les donnes.", "Modelisationator : Mise à jour",
						JOptionPane.ERROR_MESSAGE);
			}
			return new BDDResult(BDDResultEnum.NO_DIFFERENT_VALUES);
		} else {
			if (!checkTypes(rowData)) {
				if (!quiet) {
					String message = "Vous avez saisi une valeur(s) ne correpondant(s) pas au(x) type(s) attendu(s).";
					message += "\nVeuillez vérifier les donnnées.";
					JOptionPane.showMessageDialog(null, message, "Echec", JOptionPane.ERROR_MESSAGE);
				}
				return new BDDResult(BDDResultEnum.INCORRECT_TYPES);
			}
			// UPDATE
			if (updateOrInsert) {
				return updateTableBypass(rowData, rowIndex, quiet);
			}
			return insertTableBypass(rowData, quiet);
		}
	}

	/**
	 * Exécute la mise à jour SQL avec les champs rowData
	 * 
	 * @param rowData les valeurs des champs à mettre à jour
	 * @param rowIndex la ligne dans la jtable ou se situent ces valeurs
	 * @param quiet true = empecher affichage
	 * @return le résultat de la requête
	 */
	public MethodResult updateTableBypass(String[] rowData, int rowIndex, boolean quiet) {
		String modelName = getModel().getValueAt(getRowCount() - 1, 0).toString();
		mainFenetre.setToolTip("Mise à jour de " + modelName + " en cours.");

		try {
			Class.forName("org.sqlite.JDBC");
			String[] dbColNames = BDDUtilities.getColumnNames();

			// CREATE STRING
			String updateString = "update PLY set";
			for (int colIndex = 0; colIndex < rowData.length; colIndex++) {
				if (isCellDifferent(rowIndex, colIndex)) {
					String colName = dbColNames[colIndex];
					updateString += " " + colName + " = ?,";
				}
			}
			updateString = updateString.substring(0, updateString.length() - 1); // remove last comma
			updateString += " where " + dbColNames[0] + " = ?";

			// CREATE STATEMENT
			PreparedStatement statement = BDDUtilities.getConnection().prepareStatement(updateString);
			String[] colTypes = BDDUtilities.getColumnTypes();
			int stIndex = 1;
			for (int colIndex = 0; colIndex < rowData.length; colIndex++) {
				if (isCellDifferent(rowIndex, colIndex)) {
					if (colTypes[colIndex].equalsIgnoreCase("integer")) {
						statement.setInt(stIndex, Integer.parseInt(rowData[colIndex].toString()));
					} else if (colTypes[colIndex].equalsIgnoreCase("text")) {
						statement.setString(stIndex, rowData[colIndex].toString());
					}
					stIndex++;
				}
			}
			statement.setString(stIndex, orignalData.get(0)[0]); // WHERE clause
			if (statement.executeUpdate() > 0) {
				// REUSSITE
				updateOriginal(rowIndex, rowData);
				mainFenetre.setToolTip("Mise à jour réussie.");
				if (!quiet) {
					JOptionPane.showMessageDialog(null, "Mise à jour réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
				}
				BDDUtilities.closeConnection();
				return new BDDResult(BDDResultEnum.UPDATE_SUCCESSFUL);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// ECHEC
		if (!quiet) {
			JOptionPane.showMessageDialog(null, "Echec de la mise à jour", "Echec", JOptionPane.ERROR_MESSAGE);
		}
		// resetRow(rowIndex); // reset ou pas si erreur d'insertion? sinon perte des données. de toute facon l'utilisateur peut appuyer sur le bouton reset
		BDDUtilities.closeConnection();
		mainFenetre.setToolTip("Mise à jour non réussie.");
		return new BDDResult(BDDResultEnum.UPDATE_NOT_SUCCESSFUL);
	}

	/**
	 * Vérifie si la valeur à insérer n'existe pas déja et exécute l'insertion avec les valeurs de rowData.
	 * 
	 * @param rowData les valeurs des champs à insérer
	 * @param quiet true = empecher affichage
	 * @return le résultat de la requête
	 */
	public MethodResult insertTableBypass(String[] rowData, boolean quiet) {
		String modelName = getModel().getValueAt(getRowCount() - 1, 0).toString();
		mainFenetre.setToolTip("Insertion de " + modelName + " en cours.");

		if (rowData[0].toString().equalsIgnoreCase("")) {
			mainFenetre.setToolTip("Insertion impossible.");
			if (!quiet) {
				JOptionPane.showMessageDialog(null, "Vous ne pouvez pas insérer un nom de modèle nulle.", "Modelisationator : Insertion",
						JOptionPane.ERROR_MESSAGE);
			}
			return new BDDResult(BDDResultEnum.NAME_NOT_SPECIFIED);
		}

		try {
			Class.forName("org.sqlite.JDBC");
			String[] dbColTypes = BDDUtilities.getColumnTypes();
			String[] dbColNames = BDDUtilities.getColumnNames();

			// VERIF PRE EXISTING
			String verifString = "select * from PLY where " + dbColNames[0] + " = ?";
			PreparedStatement verifSt = BDDUtilities.getConnection().prepareStatement(verifString);
			verifSt.setString(1, rowData[0].toString());
			ResultSet verifRs = verifSt.executeQuery();

			if (!verifRs.next()) { // le modèle existe pas
				String insertString = "insert into PLY values(";
				// question marks
				for (int colIndex = 0; colIndex < dataWidth; colIndex++) {
					insertString += " ?,";
				}
				insertString = insertString.substring(0, insertString.length() - 1); // remove last comma
				insertString += ")";

				// CREATE STATEMENT
				PreparedStatement statement = null;
				statement = BDDUtilities.getConnection().prepareStatement(insertString);
				for (int colIndex = 0; colIndex < dataWidth; colIndex++) {
					if (dbColTypes[colIndex].equalsIgnoreCase("integer")) {
						statement.setInt(colIndex + 1, Integer.parseInt(rowData[colIndex].toString())); // setString starts at 1
					} else if (dbColTypes[colIndex].equalsIgnoreCase("text")) {
						statement.setString(colIndex + 1, rowData[colIndex].toString()); // setString starts at 1
					}
				}

				// REUSSITE
				if (statement.executeUpdate() > 0) {
					updateOriginal(getRowCount() - 1, rowData);
					mainFenetre.setToolTip("Insertion réussie.");
					lastRowIsInDB = true;
					if (!quiet) {
						JOptionPane.showMessageDialog(null, "Insertion réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
					}
					BDDUtilities.closeConnection();
					return new BDDResult(BDDResultEnum.INSERT_SUCCESSFUL);
				}
			} else {
				mainFenetre.setToolTip("Ce nom existe déja dans la base");
				if (!quiet) {
					JOptionPane.showMessageDialog(null, "Le nom " + rowData[0].toString() + " existe déja", "Echec", JOptionPane.ERROR_MESSAGE);
				}
				BDDUtilities.closeConnection();
				return new BDDResult(BDDResultEnum.PRE_EXISTING_MODEL);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (!quiet) {
			JOptionPane.showMessageDialog(null, "Echec de l'insertion", "Echec", JOptionPane.ERROR_MESSAGE);
		}
		// resetRow(rowIndex); // reset ou pas si erreur d'insertion? sinon perte des données. de toute facon l'utilisateur peut appuyer sur le bouton reset
		mainFenetre.setToolTip("Insertion non réussie.");
		BDDUtilities.closeConnection();
		return new BDDResult(BDDResultEnum.INSERT_NOT_SUCCESSFUL);
	}

	/**
	 * Supprime une ligne
	 * 
	 * @param rowIndex la ligne dans la jtable à supprimer dans la base
	 * @param quiet true = empecher affichage
	 * @return le résultat de la requête
	 */
	public MethodResult deleteRow(int rowIndex, boolean quiet) {
		String modelName = getModel().getValueAt(rowIndex, 0).toString();
		mainFenetre.setToolTip("Suppression de " + modelName + " en cours.");
		if (true) { // TODO choisir si toujours garder une alerte avant suppression
			if (!quiet) {
				String message = "Voulez vous vraiment supprimer le modèle de la base de données?";
				String[] options = new String[] { "Oui, je sais ce que je fais", "Non, je n'ai plus envie" };
				int n = JOptionPane.showOptionDialog(null, message, "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						options, options[1]);
				if (n == JOptionPane.NO_OPTION) {
					mainFenetre.setToolTip("Suppresion de " + modelName + " avortée.");
					return new BDDResult(BDDResultEnum.CANCEL_DELETE);
				}
			}
		}
		try {
			String deleteString = "delete from PLY where " + getColumnName(0) + " = ?";
			PreparedStatement statement = BDDUtilities.getConnection().prepareStatement(deleteString);
			statement.setString(1, modelName);
			if (statement.executeUpdate() > 0) {
				((TableDataModel) getModel()).removeRow(rowIndex);
				mainFenetre.setToolTip("Suppresion de " + modelName + " réussie.");
				if (!quiet) {
					JOptionPane.showMessageDialog(null, "Suppression réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
				}
				BDDUtilities.closeConnection();
				return new BDDResult(BDDResultEnum.DELETE_SUCCESSFUL);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (!quiet) {
			JOptionPane.showMessageDialog(null, "Echec de la suppression.", "Echec", JOptionPane.ERROR_MESSAGE);
		}
		mainFenetre.setToolTip("Echec de la suppression de " + modelName + ".");
		BDDUtilities.closeConnection();
		return new BDDResult(BDDResultEnum.DELETE_NOT_SUCCESSFUL);
	}

	/**
	 * Si une valeur est supposé être modifiable et qu'elle est différente que celle d'origine
	 * 
	 * @param row la ligne à laquelle la valeur se trouve dans la table
	 * @param column la colonne à laquelle la valeur se trouve dans la table
	 * @return true si elle est différente
	 */
	private boolean isCellDifferent(int row, int column) {
		return getModel().isCellEditable(row, column) && !orignalData.get(row)[column].equals(getModel().getValueAt(row, column));
	}

	/**
	 * Vérifie que les valeurs saisies sont correctes d'apres les types dans la base de données.
	 * 
	 * @param rowData
	 * @return true si les donneés respectent les types
	 */
	private boolean checkTypes(Object[] rowData) {
		boolean correctTypes = true;
		String[] colTypes = BDDUtilities.getColumnTypes();
		for (int i = 0; i < dataWidth; i++) {
			if (colTypes[i].equalsIgnoreCase("integer")) {
				if (!rowData[i].toString().matches("^-?\\d+$")) {
					correctTypes = false;
				}
			}
		}
		return correctTypes;
	}

	/**
	 * Remet toutes les valeurs à celles d'origine.
	 */
	public void resetAll() {
		for (int i = 0; i < getRowCount(); i++) {
			resetRow(i);
		}
		mainFenetre.setToolTip("Toute la table à été remis à zéro.");
	}

	/**
	 * Remet les valeurs du modèle à celles d'origine (quand la table a été initialisé)
	 * 
	 * @param rowIndex
	 */
	public void resetRow(int rowIndex) {
		for (int col = 0; col < getColumnCount(); col++) {
			getCellEditor(rowIndex, col).stopCellEditing(); // on désactive le mode "edition" de toutes le cases pour qu'on puisse mettre à jour leurs valeurs
			((TableDataModel) getModel()).setValueAt(orignalData.get(rowIndex)[col], rowIndex, col);
		}
		mainFenetre.setToolTip("La ligne du modèle \"" + getValueAt(rowIndex, 0) + "\" a été remis à zéro.");
	}

	/**
	 * Met à jour la liste des valeurs originales à celles qui ont été mises à jour
	 * 
	 * @param rowIndex
	 * @param rowData
	 */
	private void updateOriginal(int rowIndex, String[] rowData) {
		String[] originalRow = orignalData.get(rowIndex);
		for (int col = 0; col < rowData.length; col++) {
			originalRow[col] = rowData[col]; // strings are immutable so the string in orignalRow[col] won't change even if rowData[col] (table model)
												// eventually changes.
		}
	}

	/**
	 * Crée une ligne vide avec les boutons s'il y en avait et l'ajoute à son modèle.
	 * 
	 * @param buttonColumns
	 */
	public void addRow(String[] buttonColumns) {
		// CREATE NEW ROW
		if (lastRowIsInDB) {
			String[] newRow = new String[getColumnCount()];
			String[] newRowKeep = new String[getColumnCount()];
			int buttonIndex = 0;
			for (int col = 0; col < getColumnCount(); col++) {
				if (getModel().getColumnName(col).equalsIgnoreCase("date")) {
					newRow[col] = LocalDate.now().toString();
					newRowKeep[col] = LocalDate.now().toString();
				} else if (buttonColumns != null && col >= getColumnCount() - buttonColumns.length) {
					newRow[col] = buttonColumns[buttonIndex];
					newRowKeep[col] = buttonColumns[buttonIndex];
					buttonIndex++;
				} else {
					newRow[col] = "";
					newRowKeep[col] = "";
				}
			}

			// ADD ROW TO DATA
			orignalData.add(newRowKeep);
			((TableDataModel) getModel()).addRow(newRow);
			for (int i = 0; i < getColumnCount(); i++) {
				if (getColumnName(i).equalsIgnoreCase("date")) {
					((TableDataModel) getModel()).setCellEditable(orignalData.size() - 1, i, false);
				}
			}
			lastRowIsInDB = false;
			mainFenetre.setToolTip("Nouvelle ligne crée. Saisissez les valeurs que vous voulez insérer dans la base.");
		} else {
			mainFenetre.setToolTip("Vous ne pouvez pas ajouter une ligne tant que vous n'avez pas inséré la dernière dans la base.");
		}
	}
}
