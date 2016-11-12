package bddInterface;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Cette classe permet d'implémenter une JTable à remplir
 * 
 * @author Master
 *
 */
public class TablePanel extends JPanel {

	private static final long serialVersionUID = -2526625545035179794L;
	private JTable table;
	private DataTableModel dataTableModel;
	private String[][] dataArray;
	private JScrollPane scrollPane;
	private String[] columnNames;

	
	public TablePanel(String[] columnNames, String[][] dataArray) {
		super();

		this.columnNames = new String[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			this.columnNames[i] = columnNames[i];
		}
		this.dataArray = new String[dataArray.length][dataArray[0].length];
		for (int i = 0; i < dataArray.length; i++) {
			for (int j = 0; i < dataArray[0].length; j++) {
				this.dataArray[i][j] = dataArray[i][j];
			}
		}

		
		dataTableModel = new DataTableModel(this.columnNames, this.dataArray);
		table = new JTable(dataTableModel);
		table.putClientProperty("terminateEditOnFocusLost", true);
		dataTableModel.setEditable(true);
		
		scrollPane = new JScrollPane(table);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(scrollPane);
	}

	public TablePanel(String[] columnNames, int dataRows, int dataColumns) {
		super();

		this.columnNames = new String[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			this.columnNames[i] = columnNames[i];
		}
		this.dataArray = new String[dataRows][dataColumns];
		for (int i = 0; i < dataRows; i++) {
			this.columnNames[i] = columnNames[i];
			for (int j = 0; j < dataColumns; j++) {
				this.dataArray[i][j] = "";
			}
		}
		
		dataTableModel = new DataTableModel(this.columnNames, this.dataArray);
		table = new JTable(dataTableModel);
		table.putClientProperty("terminateEditOnFocusLost", true);
		dataTableModel.setEditable(true);

		scrollPane = new JScrollPane(table);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(scrollPane);
	}

	public JTable getTable() {
		return this.table;
	}
	
	public DataTableModel getDataTableModel() {
		return this.dataTableModel;
	}
}
