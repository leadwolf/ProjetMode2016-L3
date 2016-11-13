package bddInterface;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Cette classe permet d'implémenter une JTable soit à remplir soit avec des valeurs données
 * 
 * @author L3
 *
 */
public class TablePanel extends JPanel {

	private static final long serialVersionUID = -2526625545035179794L;
	private JTable table;
	private TableModel tableModel;
	private String[][] dataArray;
	private JScrollPane scrollPane;
	private String[] columnNames;

	/**
	 * Crée un tableau avec les noms et valeurs des colonnes et champs donnés
	 * @param columnNames
	 * @param dataArray
	 */
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

		
		tableModel = new TableModel(this.columnNames, this.dataArray);
		table = new JTable(tableModel);
		table.putClientProperty("terminateEditOnFocusLost", true);
		tableModel.setEditable(true);
		
		scrollPane = new JScrollPane(table);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(scrollPane);
	}

	/**
	 * Crée un tableau vide mais avec des noms de colonnes donnés
	 * @param dataRows
	 * @param dataColumns
	 * @param columnNames
	 */
	public TablePanel(int dataRows, int dataColumns, String[] columnNames) {
		super();

		this.columnNames = new String[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			this.columnNames[i] = columnNames[i];
		}
		this.dataArray = new String[dataRows][dataColumns];
		for (int i = 0; i < dataRows; i++) {
			for (int j = 0; j < dataColumns; j++) {
				this.dataArray[i][j] = "";
			}
		}
		
		tableModel = new TableModel(this.columnNames, this.dataArray);
		table = new JTable(tableModel);
		table.putClientProperty("terminateEditOnFocusLost", true);
		tableModel.setEditable(true);

		scrollPane = new JScrollPane(table);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(scrollPane);
	}

	public JTable getTable() {
		return this.table;
	}
	
	public TableModel getTableModel() {
		return this.tableModel;
	}
}
