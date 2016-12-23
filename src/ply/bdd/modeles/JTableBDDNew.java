package ply.bdd.modeles;

import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import ply.bdd.other.BDDUtilities;
import res.ButtonColumn;
import result.BDDResult;
import result.BDDResultEnum;
import result.MethodResult;

/**
 * Modèle de JTable, éxécute les requêtes sql
 * 
 * @author L3
 *
 */
public class JTableBDDNew extends JTable {

	private List<String[]> orignalData;
	private int dataWidth;

	/**
	 * @param dm
	 * @param originalData
	 * @param buttons
	 */
	public JTableBDDNew(TableDataModel dm, List<String[]> originalData, boolean buttons) {
		super(dm);
		this.orignalData = originalData;
		dataWidth = orignalData.get(0).length - 2;

		if (buttons) {
			setButtons();
		}
	}

	/**
	 * Ajoute deux boutons "edit" et "delete"
	 */
	@SuppressWarnings({ "serial", "unused" })
	private void setButtons() {
		// CREATE BUTTON COLUMNS
		Action editAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int modelRow = Integer.valueOf(e.getActionCommand());
				modifyTableDirect(modelRow, true, false);
			}
		};
		ButtonColumn editButton = new ButtonColumn(this, editAction, dataWidth);

		Action deleteAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int modelRow = Integer.valueOf(e.getActionCommand());
				deleteRow(modelRow);
			}
		};
		ButtonColumn deleteButton = new ButtonColumn(this, deleteAction, dataWidth + 1);

		// SET BUTTONS EDITABLE FOR ACTIONS
		for (int col = dataWidth; col < dataWidth + 2; col++) {
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
	 * @param noPrint
	 * @return le résultat de l'opération sql ou si il n'a pas de valeurs différentes sur lesquelles opérer
	 */
	public MethodResult modifyTableDirect(int rowIndex, boolean updateOrInsert, boolean noPrint) {
		// GET SELECTED ROW DATA
		int colCount = getModel().getColumnCount();
		boolean differentValues = false;
		String[] rowData = new String[colCount];
		for (int colIndex = 0; colIndex < colCount; colIndex++) {
			rowData[colIndex] = getModel().getValueAt(rowIndex, colIndex).toString();
			if (!rowData[colIndex].equals(orignalData.get(rowIndex)[colIndex])) {
				differentValues = true;
			}
		}

		// CHECK IF UPDATE POSSIBLE
		if (!differentValues) {
			if (!noPrint) {
				JOptionPane.showMessageDialog(null, "Il n'y a rien à mettre à jour", "Action impossible", JOptionPane.ERROR_MESSAGE);
			}
			return new BDDResult(BDDResultEnum.NO_DIFFERENT_VALUES);
		} else {
			// UPDATE
			if (updateOrInsert) {
				return updateTableBypass(rowData, rowIndex, noPrint);
			}
			return insertTableBypass(rowData, rowIndex, noPrint);
		}
	}

	/**
	 * Exécute la mise à jour SQL avec les champs rowData
	 * 
	 * @param rowData les valeurs des champs à mettre à jour
	 * @param rowIndex la ligne dans la jtable ou se situent ces valeurs
	 * @param noPrint true = empecher affichage
	 * @return true si l'update a réussi
	 */
	public MethodResult updateTableBypass(String[] rowData, int rowIndex, boolean noPrint) {
		try {
			Class.forName("org.sqlite.JDBC");
			// CREATE STRING
			String updateString = "update PLY set";
			for (int colIndex = 0; colIndex < rowData.length; colIndex++) {
				if (isDifferent(rowIndex, colIndex)) {
					String colName = getModel().getColumnName(colIndex);
					updateString += " " + colName + " = ?,";
				}
			}
			updateString = updateString.substring(0, updateString.length() - 1); // remove last comma
			updateString += " where nom = ?";

			// CREATE STATEMENT
			PreparedStatement statement = BDDUtilities.getConnection().prepareStatement(updateString);
			String[] colTypes = BDDUtilities.getColumnTypes();
			int stIndex = 1;
			for (int colIndex = 0; colIndex < rowData.length; colIndex++) {
				if (isDifferent(rowIndex, colIndex)) {
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
				if (!noPrint) {
					JOptionPane.showMessageDialog(null, "Mise à jour réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
				}
				updateOriginal(rowIndex, rowData);
				BDDUtilities.closeConnection();
				return new BDDResult(BDDResultEnum.UPDATE_SUCCESSFUL);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// ECHEC
		resetFields(rowIndex);
		if (!noPrint) {
			JOptionPane.showMessageDialog(null, "Echec de la mise à jour", "Echec", JOptionPane.ERROR_MESSAGE);
		}
		BDDUtilities.closeConnection();
		return new BDDResult(BDDResultEnum.UPDATE_NOT_SUCCESSFUL);
	}

	/**
	 * Si une valeur est supposé être modifiable et qu'elle est différente que celle d'origine
	 * @param row la ligne à laquelle la valeur se trouve dans la table
	 * @param column la colonne à laquelle la valeur se trouve dans la table
	 * @return true si elle est différente
	 */
	private boolean isDifferent(int row, int column) {
		return getModel().isCellEditable(row, column) && !orignalData.get(row)[column].equals(getModel().getValueAt(row, column));
	}


	/**
	 * Supprime une ligne
	 * @param rowIndex
	 */
	public void deleteRow(int rowIndex) {
		System.out.println("deleting at = " + rowIndex);
	}
	
	public MethodResult insertTableBypass(Object[] rowData, int rowIndex, boolean noPrint) {
		// TODO vérifier fonctionnement
		try {
			Class.forName("org.sqlite.JDBC");
			String insertString = "insert into PLY values(";
			for (int colIndex = 0; colIndex < rowData.length; colIndex++) {
				insertString += " ?,";
			}
			insertString = insertString.substring(0, insertString.length() - 2); // remove last comma
			insertString += ")";

			// CREATE STATEMENT
			PreparedStatement statement = BDDUtilities.getConnection().prepareStatement(insertString);
			String[] colTypes = BDDUtilities.getColumnTypes();
			for (int colIndex = 0; colIndex < rowData.length; colIndex++) {
				if (colTypes[colIndex].equalsIgnoreCase("integer")) {
					statement.setInt(colIndex + 1, Integer.parseInt(rowData[colIndex].toString())); // setString starts at 1
				} else if (colTypes[colIndex].equalsIgnoreCase("text")) {
					statement.setString(colIndex + 1, rowData[colIndex].toString()); // setString starts at 1
				}
			}

			if (statement.executeUpdate() > 0) {
				if (!noPrint) {
					JOptionPane.showMessageDialog(null, "Insertion réussie", "Succès", JOptionPane.INFORMATION_MESSAGE);
				}
				BDDUtilities.closeConnection();
				return new BDDResult(BDDResultEnum.INSERT_SUCCESSFUL);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (!noPrint) {
			JOptionPane.showMessageDialog(null, "Echec de l'insertion", "Echec", JOptionPane.ERROR_MESSAGE);
		}
		BDDUtilities.closeConnection();
		return new BDDResult(BDDResultEnum.INSERT_NOT_SUCCESSFUL);
	}

	/**
	 * Remet les valeurs du modèle à celles d'origine (quand la table a été initialisé)
	 * 
	 * @param rowIndex
	 */
	private void resetFields(int rowIndex) {
		for (int i = 0; i < getColumnCount(); i++) {
			setValueAt(orignalData.get(rowIndex)[i], rowIndex, i);
		}
	}

	/**
	 * Met à jour la liste des valeurs originales à celles qui ont été mises à jour
	 * @param rowIndex
	 * @param rowData
	 */
	private void updateOriginal(int rowIndex, String[] rowData) {
		orignalData.set(rowIndex, rowData);
	}
}
