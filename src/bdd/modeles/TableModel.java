package bdd.modeles;

import javax.swing.table.AbstractTableModel;

/**
 * Cette clase est un modèle de JTable. Elle permet de customiser son comportment. Ici nous l'utilisons principalement pour modifier les permissions
 * d'édition des cellules
 * 
 * @author L3
 *
 */
public class TableModel extends AbstractTableModel {

	/**
	 * Array de noms de colonnes que comportera la JTable
	 */
	private Object[] columnNames;
	/**
	 * Array 2D de données que comportera la JTable
	 */
	private Object[][] data;
	/**
	 * Array 2D qui permet de sauvegarder les permissions d'édition des cellules
	 */
	private boolean[][] editable;

	@SuppressWarnings("javadoc")
	static final long serialVersionUID = 2590977835553440556L;

	/**
	 * Constructeur qui exécute super() et met en place les noms de colonnes.
	 * @param columnNames les noms de colonnes à appliquer
	 */
	private TableModel(Object[] columnNames) {
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
	 * 
	 * @param columnData les noms de colonnes  à appliquer
	 * @param data les données à placer dans la table
	 */
	public TableModel(Object[] columnData, Object[][] data) {
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
	 * 
	 * @param row la ligne de la cellule concernée
	 * @param column la colonne de la cellule concernée
	 * @param editable la permission à appliquer
	 */
	public void setCellEditable(int row, int column, boolean editable) {
		this.editable[row][column] = editable;
	}

	/**
	 * Crée le tableau de valeurs nulles
	 * 
	 * @param rows le nombre de lignes que comportera la table
	 * @param columns le nombre de colonnes que compertera la table
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
	 * 
	 * @param data les donneés à utiliser
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
