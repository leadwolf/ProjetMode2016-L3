package ply.bdd.vues;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ply.bdd.modeles.JTableBDDNew;
import ply.bdd.modeles.TableDataModel;

/**
 * Cette classe donne un panel complet d'une JTable ainsi que les boutons nécessaires à l'opération de la base.
 * 
 * @author L3
 *
 */
public class BDDPanelNew extends JPanel {

	private static final long serialVersionUID = 3333780528336050471L;
	private JTableBDDNew table;
	private TableDataModel dataModel;
	private Dimension panelDim = new Dimension(600, 250);

	/**
	 * Width = length of columnNames
	 * 
	 * @param rs
	 * @param columnNames
	 * @param noModifyColumns
	 * @param buttonColumns
	 */
	public BDDPanelNew(ResultSet rs, String[] columnNames, boolean buttons) {
		super();

		// CONVERT RS TO ARRAY
		List<String[]> data = new ArrayList<>();
		try {
			while (rs.next()) {
				String[] row;
				if (!buttons) {
					row = new String[columnNames.length];
					for (int i = 0; i < row.length; i++) {
						row[i] = rs.getString(i + 1);
					}
					data.add(row);
				} else {
					int nameLength = columnNames.length;
					row = new String[nameLength + 2];
					for (int i = 0; i < nameLength; i++) {
						row[i] = rs.getString(i + 1);
					}
					row[nameLength] = "Mette à jour la base";
					row[nameLength + 1] = "Supprimer";
				}
				data.add(row);
			}

			// CREATE TABLE MODEL
			dataModel = new TableDataModel(columnNames, data);
			dataModel.setEditable(false);
			table = new JTableBDDNew(dataModel, data, buttons);

			// CREATE THIS PANEL
			setupPanel();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Met en forme ce panel
	 */
	private void setupPanel() {
		setPreferredSize(panelDim);
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Modifie les permisisons d'édition sur toute la table
	 * 
	 * @param b
	 */
	public void setEditable(boolean b) {
		dataModel.setEditable(b);
	}

	/**
	 * Empeche la modifications d'une liste de colonnes
	 * 
	 * @param noModifyColumns
	 * @return si on a pu modifier tous les permissions
	 */
	public boolean setEditableColumns(int[] noModifyColumns, boolean editable) {
		if (noModifyColumns != null) {
			for (int column = 0; column < noModifyColumns.length; column++) {
				for (int row = 0; row < dataModel.getRowCount(); row++) {
					dataModel.setCellEditable(row, column, editable);
				}
			}
			return true;
		}
		return false;
	}

}
