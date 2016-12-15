package bdd.vues;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import bdd.modeles.TableModel;

/**
 * Cette classe permet d'implémenter une JTable soit à remplir soit avec des valeurs données
 * 
 * @author L3
 *
 */
public class TablePanel extends JPanel {

	@SuppressWarnings("javadoc")
	private static final long serialVersionUID = -2526625545035179794L;
	/**
	 * La table qui sera affichée
	 */
	private JTable table;
	/**
	 * Le modèle du table utilisée
	 */
	private TableModel tableModel;
	/**
	 * Les noms des colonnes, sert à inialiser les dimensions ou valeurs
	 */
	private String[] columnNames;
	/**
	 * Array 2D des données, sert à initialiser les dimensions ou valeurs
	 */
	private String[][] dataArray;
	/**
	 * ScrollPane qui contiendra la table
	 */
	private JScrollPane scrollPane;

	/**
	 * Crée un tableau avec les noms et valeurs des colonnes et champs donnés
	 * 
	 * @param columnNames les noms de colonnes à utiliser
	 * @param dataArray les données à utliser pour remplir la table
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
	 * 
	 * @param dataRows le nombre de lignes de la table des données
	 * @param dataColumns le nombre de colonnes de la table des données
	 * @param columnNames les noms de colonnes à utliser
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

	/**
	 * @return le JTable utilisée
	 */
	public JTable getTable() {
		return this.table;
	}

	/**
	 * @return le TableModel du JTable utilisée
	 */
	public TableModel getTableModel() {
		return this.tableModel;
	}
}
