package ply.bdd.modeles;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

/**
 * Cette clase est un modèle de JTable comme {@link DefaultTableModel}. Elle permet de customiser son comportment. Ici nous l'utilisons principalement pour
 * modifier les permissions d'édition des cellules
 * 
 * @author L3
 *
 */
public class TableDataModel extends AbstractTableModel {

	/**
	 * Array de noms de colonnes que comportera la JTable
	 */
	private Object[] columnNames;
	/**
	 * Array 2D de données que comportera la JTable
	 */
	private List<Object[]> data;
	/**
	 * Array 2D qui permet de sauvegarder les permissions d'édition des cellules
	 */
	private List<boolean[]> editable;

	@SuppressWarnings("javadoc")
	static final long serialVersionUID = 2590977835553440556L;

	/**
	 * Constructeur qui exécute super() et met en place les noms de colonnes.
	 * 
	 * @param columnNames les noms de colonnes à appliquer
	 */
	private TableDataModel(Object[] columnNames) {
		super();

		this.columnNames = new Object[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			this.columnNames[i] = columnNames[i];
		}

	}

	/**
	 * Crée un tableau dont les noms des colonnes sont connus mais pas les données
	 * 
	 * @param columnData
	 * @param dataRows le nombre de lignes vides à avoir dans le tableau
	 * @param dataColumns le nombre de colonnes vides à avoir dans le tableau
	 */
	public TableDataModel(Object[] columnData, int dataRows, int dataColumns) {
		this(columnData);
		if (dataRows > 0 && dataColumns > 0) {
			initData(dataRows, dataColumns);
		} else {
			try {
				if (dataRows <= 0) {
					throw new Exception("dataRows must be superior to 0");
				} else {
					throw new Exception("dataColumns must be superior to 0");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Crée un tableau avec les noms des colonnes et remplit le tableau avec l'array donné
	 * 
	 * @param columnData les noms de colonnes à appliquer
	 * @param data les données à placer dans la table
	 */
	public TableDataModel(Object[] columnData, Object[][] data) {
		this(columnData);
		setData(data);
	}

	/**
	 * Crée un tableau avec les noms des colonnes et remplit le tableau avec l'array donné
	 * 
	 * @param columnData les noms de colonnes à appliquer
	 * @param data les données à placer dans la table
	 */
	public TableDataModel(String[] columnData, List<String[]> data) {
		this(columnData);
		setData(data);
	}

	/**
	 * Modifie la possibilité d'éditer la totalité du tableau
	 * 
	 * @param isEditable permission d'édition de la table entière
	 */
	public void setEditable(boolean isEditable) {
		if (editable != null) {
			for (int i = 0; i < editable.size(); i++) {
				for (int j = 0; j < editable.get(0).length; j++) {
					editable.get(i)[j] = isEditable;
				}
			}
		}
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		if (data == null) {
			return -1;
		}
		return this.data.get(0).length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (this.data.get(rowIndex)[columnIndex] == null) {
			return "";
		}
		return this.data.get(rowIndex)[columnIndex];
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (data.get(row)[column] != null) {
			return editable.get(row)[column];
		}
		return false;
	}

	@Override
	public String getColumnName(int column) {
		if (column >= columnNames.length) {
			return "";
		}
		return (String) columnNames[column];
	}

	/**
	 * Modifie la possibilité d'éditer une seule cellule en particulier
	 * 
	 * @param row la ligne de la cellule concernée
	 * @param column la colonne de la cellule concernée
	 * @param editable la permission à appliquer
	 */
	public void setCellEditable(int row, int column, boolean editable) {
		this.editable.get(row)[column] = editable;
	}

	/**
	 * Crée le tableau de valeurs nulles. Toutes les valeurs sont editables.
	 * 
	 * @param rows le nombre de lignes que comportera la table
	 * @param columns le nombre de colonnes que compertera la table
	 */
	private void initData(int rows, int columns) {
		this.editable = new ArrayList<>();
		this.data = new ArrayList<>();
		for (int i = 0; i < rows; i++) {
			this.data.add(new Object[columns]);
			this.editable.add(new boolean[columns]);
			for (int col = 0; col < columns; col++) {
				this.data.get(i)[col] = null;
				this.editable.get(i)[col] = true;
			}
		}
	}

	/**
	 * Remplace las valeurs du tableau avec celui donné
	 * 
	 * @param data les donneés à utiliser
	 */
	private void setData(Object[][] data) {
		initData(data.length, data[0].length);
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				this.data.get(i)[j] = data[i][j];
				this.editable.get(i)[j] = false;
			}
		}
	}

	/**
	 * Remplace las valeurs du tableau avec celui donné
	 * 
	 * @param data les donneés à utiliser
	 */
	private void setData(List<String[]> data) {
		initData(data.size(), data.get(0).length);
		for (int i = 0; i < data.size(); i++) {
			for (int j = 0; j < data.get(0).length; j++) {
				this.data.get(i)[j] = data.get(i)[j];
				this.editable.get(i)[j] = false;
			}
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		this.data.get(rowIndex)[columnIndex] = aValue;
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	public Object[] getRow(int rowndex) {
		return data.get(rowndex);
	}
	
	/**
	 * Ajoute une ligne à la table. Elle hérite les permisisons d'édition de la ligne au dessus sinon tout est modifiable.
	 * 
	 * @param newRow
	 */
	public void addRow(String[] newRow) {
		data.add(newRow);
		boolean[] newEditableRow = new boolean[newRow.length];
//		if (editable.size() > 0) {
//			for (int col = 0; col < newRow.length; col++) {
//				newEditableRow[col] = editable.get(editable.size()-1)[col]; // new row inheris tpermissions from row above.
//			}
//		} else {
			for (int col = 0; col < newRow.length; col++) {
				newEditableRow[col] = true;
			}
//		}
		editable.add(newEditableRow);
		fireTableRowsInserted(data.size(), data.size());
	}

	/**
	 * Supprime une ligne de la table.
	 * @param rowIndex
	 */
	public void removeRow(int rowIndex) {
		data.remove(rowIndex);
		editable.remove(rowIndex);
		fireTableRowsDeleted(rowIndex, rowIndex);
	}

}
