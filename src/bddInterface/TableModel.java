package bddInterface;

import javax.swing.table.AbstractTableModel;

/**
 * Cette clase permet d'avoir des cellules modifiables ou non
 * @author L3
 *
 */
public class TableModel extends AbstractTableModel {

	private Object[] columnNames;
	private Object[][] data;
	private boolean[][] editable;

	private static final long serialVersionUID = 2590977835553440556L;

	private TableModel(Object[] columnData) {
		super();

		this.columnNames = new Object[columnData.length];
		for (int i = 0; i < columnData.length; i++) {
			this.columnNames[i] = columnData[i];
		}

	}
	
	/**
	 * Crée un tableau dont les noms des colonnes sont connus mais pas les champs
	 * @param columnData
	 * @param dataRows le nombre de lignes vides à avoir dans le tableau
	 * @param dataColumns le nombre de colonnes vides à avoir dans le tableau
	 */
	public TableModel(Object[] columnData, int dataRows, int dataColumns) {
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
	 * @param columnData
	 * @param data
	 */
	public TableModel(Object[] columnData, Object[][] data) {
		this(columnData);
		setData(data);
	}

	/**
	 * Modifie la possibilité d'éditer la totalité du tableau
	 * @param isEditable
	 */
	public void setEditable(boolean isEditable) {
		if (editable != null) {
			for (int i = 0; i < editable.length; i++) {
				for (int j = 0; j < editable[0].length; j++) {
					editable[i][j] = isEditable;
				}
			}
		}
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		if (data == null) {
			return -1;
		}
		return this.data[0].length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (this.data[rowIndex][columnIndex] == null) {
			return "";
		}
		return this.data[rowIndex][columnIndex];
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (data[row][column] != null) {
			return editable[row][column];
		}
		return false;
	}

	@Override
	public String getColumnName(int column) {
		return (String) columnNames[column];
	}

	/**
	 * Modifie la possibilité d'éditer une seule cellule en particulier
	 * @param row
	 * @param column
	 * @param editable
	 */
	public void setCellEditable(int row, int column, boolean editable) {
		this.editable[row][column] = editable;
	}
	
	/**
	 * Crée le tableau de valeurs nulles
	 * @param rows
	 * @param columns
	 */
	private void initData(int rows, int columns) {
		this.editable = new boolean[rows][columns];
		this.data = new Object[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				this.data[i][j] = null;
				this.editable[i][j] = true;
			}
		}
	}
	
	/**
	 * Remplace las valeurs du tableau avec celui donné
	 * @param data
	 */
	private void setData(Object[][] data) {
		this.editable = new boolean[data.length][data[0].length];
		this.data = new Object[data.length][data[0].length];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				this.data[i][j] = data[i][j];
				this.editable[i][j] = true;
			}
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		this.data[rowIndex][columnIndex] = aValue;
		fireTableCellUpdated(rowIndex, columnIndex);
	}

}
