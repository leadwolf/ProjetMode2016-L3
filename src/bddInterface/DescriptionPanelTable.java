package bddInterface;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Donne une JTable d'une ResultSet
 * @author Master
 *
 */
public class DescriptionPanelTable extends JPanel {

	private static final long serialVersionUID = 6086405568083027160L;

	private JTable table;
	private String[][] dataArray;
	private JScrollPane scrollPane;
	String[] columnNames = { "Nom", "Chemin", "Date", "Description" };

	int totalLines = 0;
	ResultSetMetaData rsmd = null;

	public DescriptionPanelTable(int totalLines, ResultSet rs2) {
		super();

		try {
			ResultSetMetaData rsmd = rs2.getMetaData();
			int cols = rsmd.getColumnCount();
			dataArray = new String[totalLines][4];
			for (int i = 0; i < totalLines; i++) {
				for (int j = 0; j < cols; j++) {
					dataArray[i][j] = "";
				}
			}

			int rows = 0;
			while (rs2.next()) {
				for (int i = 1; i <= cols; i++) {
					dataArray[rows][i - 1] = rs2.getString(i);
				}
				rows++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		DataTableModel dataTableModel = new DataTableModel(columnNames, dataArray);
		table = new JTable(dataTableModel);
//		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		dataTableModel.setEditable(false);

		scrollPane = new JScrollPane(table);
//		scrollPane.setPreferredSize(new Dimension(550, 100));
//		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(scrollPane);
	}
}
