package bddInterface;

import javax.swing.table.AbstractTableModel;

public class DataTableModel extends AbstractTableModel {

	private Object[] columnNames;
	private Object[][] data;
	private boolean[][] editable;

	private static final long serialVersionUID = 2590977835553440556L;

	private DataTableModel(Object[] columnData) {
		super();

		this.columnNames = new Object[columnData.length];
		for (int i = 0; i < columnData.length; i++) {
			this.columnNames[i] = columnData[i];
		}

	}

	public DataTableModel(Object[] columnData, int dataRows, int dataColumns) {
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

	public DataTableModel(Object[] columnData, Object[][] data) {
		this(columnData);
		setData(data);
	}

	/**
	 * Needs this.data to have been initialized
	 * 
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

	public void setCellEditable(int row, int column, boolean editable) {
		this.editable[row][column] = editable;
	}

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
