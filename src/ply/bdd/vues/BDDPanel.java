package ply.bdd.vues;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import main.vues.MainFenetre;
import ply.bdd.controlers.ButtonControler;
import ply.bdd.modeles.Table;
import ply.bdd.modeles.TableDataModel;
import ply.bdd.other.BDDUtilities;

/**
 * Cette classe donne un panel complet d'une JTable ainsi que les boutons nécessaires à l'opération de la base.
 * 
 * @author L3
 *
 */
public class BDDPanel extends JPanel {

	private static final long serialVersionUID = 3333780528336050471L;
	private Table table;
	private String[] buttonColumns;
	private int finalWidth;

	/**
	 * Width = length of columnNames
	 * 
	 * @param mainFenetre
	 * @param rs le resultset contenant les données. Laisser null si on veut une seule ligne que l'utilisateur pourra ensuite utiliser pour insérer des doones.
	 * @param columnNames
	 * @param buttonColumns les colonnes de boutons "edit" et "delete". On doit le faire avant la création de la table car le constructeur de la table a besoin
	 *            d'un modèle.
	 * @param primaryButtons des JButtons à ajouter au panel
	 */
	public BDDPanel(ResultSet rs, String[] columnNames, String[] buttonColumns, String[] primaryButtons) {
		super();
		this.buttonColumns = buttonColumns;

		// CONVERT RS TO ARRAY
		List<String[]> data = new ArrayList<>();
		initData(rs, data, columnNames);

		// CREATE TABLE MODEL
		TableDataModel dataModel = new TableDataModel(columnNames, data);
		dataModel.setEditable(false);
		table = new Table(dataModel, data, buttonColumns);

		// CREATE THIS PANEL
		setupPanel(primaryButtons);
	}

	/**
	 * Initalise le List<String[]> que contient ce table.
	 * 
	 * @param rs le resultset qui contient les données avec lesquelles remplir la table s'il existe.
	 * @param data la liste dans laquelle on veut traduire ces données.
	 * @param columnNames array des noms des colonnes
	 */
	private void initData(ResultSet rs, List<String[]> data, String[] columnNames) {
		if (rs != null) {
			try {
				while (rs.next()) {
					data.add(createRowFromResultSet(rs, data, columnNames));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			addSimpleRow(data, columnNames);
		}
	}

	/**
	 * Crée une ligne simple à partir de rien pour l'ajouter à data.
	 * 
	 * @param data la liste que le modèle de ce table se basera.
	 * @param columnNames array des noms des colonnes
	 */
	private void addSimpleRow(List<String[]> data, String[] columnNames) {
		String[] row;
		if (buttonColumns == null || (buttonColumns != null && buttonColumns.length == 0)) {
			// make row width of columnsNames
			row = new String[columnNames.length];
			for (int i = 0; i < row.length; i++) {
				if (columnNames[i].equalsIgnoreCase("date")) {
					row[i] = LocalDate.now().toString();
				} else {
					row[i] = "";
				}
			}
			data.add(row);
			finalWidth = columnNames.length;
		} else {
			// make row width of columnsNames + number of buttonColumns
			int initialWidth = columnNames.length;
			row = new String[initialWidth + buttonColumns.length];
			for (int i = 0; i < initialWidth; i++) {
				if (columnNames[i].equalsIgnoreCase("date")) {
					row[i] = LocalDate.now().toString();
				} else {
					row[i] = "";
				}
			}
			int newWidth = initialWidth + buttonColumns.length;
			for (int i = initialWidth; i < newWidth; i++) {
				row[i] = buttonColumns[i - initialWidth];
			}
			data.add(row);
			finalWidth = newWidth;
		}
	}

	/**
	 * Crée un String[] reprenant les données d'un ResultSet.
	 * 
	 * @param rs le resultset contenant les données
	 * @param data la liste dans laquelle on veut traduire les données.
	 * @param columnNames array des noms des colonnes de la table.
	 * @return la ligne convertie
	 */
	private String[] createRowFromResultSet(ResultSet rs, List<String[]> data, String[] columnNames) {
		String[] row = null;
		try {
			if (buttonColumns == null || (buttonColumns != null && buttonColumns.length == 0)) {
				// make row width of data passed in rs
				row = new String[columnNames.length];
				for (int i = 0; i < row.length; i++) {
					row[i] = rs.getString(i + 1);
				}
				data.add(row);
				finalWidth = columnNames.length;
			} else {
				// make row width of data passed in rs + number of buttonColumns
				int initialWidth = columnNames.length;
				row = new String[initialWidth + buttonColumns.length];
				for (int i = 0; i < initialWidth; i++) {
					row[i] = rs.getString(i + 1);
				}
				int newWidth = initialWidth + buttonColumns.length;
				for (int i = initialWidth; i < newWidth; i++) {
					row[i] = buttonColumns[i - initialWidth];
				}
				finalWidth = newWidth;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row;
	}

	/**
	 * Met en forme ce panel
	 * 
	 * @param primaryButtons
	 */
	private void setupPanel(String[] primaryButtons) {
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
		if (primaryButtons != null && primaryButtons.length > 0) {
			ButtonControler buttonControler = new ButtonControler(this);
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			for (int i = 0; i < primaryButtons.length; i++) {
				JButton button = new JButton(primaryButtons[i]);
				button.setActionCommand(primaryButtons[i]);
				if (primaryButtons[i].toLowerCase().startsWith("reset")) {
					button.setActionCommand("reset");
				}
				button.addActionListener(buttonControler);
				buttonPanel.add(button, BorderLayout.SOUTH);
			}
			add(buttonPanel, BorderLayout.SOUTH);
			setPreferredSize(new Dimension(600, 350));
		} else {
			setPreferredSize(new Dimension(600, 250));
		}
	}

	/**
	 * Modifie la valeur de la table qui autorise à ajouter une nouvelle ligne.
	 * 
	 * @param b
	 */
	public void setCanAddRow(boolean b) {
		table.setLastRowIsInDB(b);
	}

	/**
	 * Ajoute une ligne vide à la table.
	 */
	public void addRow() {
		table.addRow(buttonColumns);
		repaint();
	}

	/**
	 * Remet les valeurs de toutes les lignes aux valeurs d'origine.
	 */
	public void resetAll() {
		table.resetAll();
	}

	/**
	 * Modifie les permisisons d'édition sur toute la table
	 * 
	 * @param b
	 */
	public void setEditable(boolean b) {
		((TableDataModel) table.getModel()).setEditable(b);
	}

	/**
	 * Empeche la modifications d'une liste de colonnes
	 * 
	 * @param columnIndexes
	 * @param editable le permissions à appliquer
	 * @return si on a pu modifier tous les permissions
	 */
	public boolean setEditableColumns(int[] columnIndexes, boolean editable) {
		TableDataModel model = ((TableDataModel) table.getModel());
		if (columnIndexes != null) {
			for (int i = 0; i < columnIndexes.length; i++) {
				for (int row = 0; row < model.getRowCount(); row++) {
					model.setCellEditable(row, columnIndexes[i], editable);
				}
			}
			return true;
		}
		return false;
	}


	/**
	 * @param mainFenetre the mainFenetre to set
	 */
	public void setMainFenetre(MainFenetre mainFenetre) {
		table.setMainFenetre(mainFenetre);
	}
}
