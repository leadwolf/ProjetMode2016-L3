package bddInterface;

import javax.swing.table.AbstractTableModel;

public class DataTableModel extends AbstractTableModel{
	
    private Object[] columnNames;
    private Object[][] data;
    private boolean isEditable;


	private static final long serialVersionUID = 2590977835553440556L;

	public DataTableModel(Object[] columnData, Object[][] data) {
		super();

	    columnNames = new Object[columnData.length];
	    this.data = new Object[data.length][data[0].length];
		for (int i=0;i<columnData.length;i++) {
			columnNames[i] = columnData[i];
		}
		for (int i=0;i<data.length;i++) {
			for (int j=0;j<data[0].length;j++) {
				this.data[i][j] = data[i][j];
			}
		}
		isEditable = true;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return data[0].length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}
	
	@Override
	public boolean isCellEditable(int row, int column){ 
		if (data[row][column] != null) {
	        return isEditable;
		}
		return false;
    }
	
	@Override
	public String getColumnName(int column) {
		return (String) columnNames[column];
	}

}
