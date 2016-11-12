package bddInterface;

import java.awt.Dimension;
import java.awt.Font;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Peut implémenter soir une JTable ou JList<br>
 * La JList aura des données alignés
 * @author Master
 *
 */
public class DescriptionPanel extends JPanel{

	private static final long serialVersionUID = 6086405568083027160L;
	
	private JList<String> list;
	private JTable table;
	private Object[][] dataArray;
	private DefaultListModel<String> listModel;
	private JScrollPane scrollPane;
	private List<String> listData;
	String[] columnNames = {"Nom", "Chemin", "Date", "Description"};

	int nameLength = 0;
	int cheminLength = 0;
	int dateLength = 0;
	int descLength = 0;
	
	public DescriptionPanel(ResultSet rs) {
		super();

		listData = new ArrayList<>();
		
		/**
		 * Copie les champs du ResultSet dans une ArrayList pour les traiter pour une JList
		 * car une JList comporte pas de colonnes
		 */
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while(rs.next()) {
				String data = "";
				for (int i=1; i<cols;i++) {
					data += rs.getString(i) + ";";
				}
				data += rs.getString(cols);
				listData.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		dataArray = new Object[listData.size()][4];
		
		listModel = new DefaultListModel<>();
		list = new JList<String>(listModel);
		list.setFont( new Font("monospaced", Font.PLAIN, 13) );
		list.setVisibleRowCount(listData.size());
		
		treatListData();

		scrollPane = new JScrollPane(list);
		// JTable ou JList ??
//		scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(550, 200));
		add(scrollPane);
	}
	
	/**
	 * Permet d'initialiser le tableau de valeurs, moins de traitment que JList
	 */
	private void treatTableData() {
		for (int i=0;i<listData.size();i++) {
			String data = listData.get(i);
			
			String name = null;
			String chemin = null;
			String date = null;
			String desc = null;

			int begin = 0;
			int separator = data.indexOf(";");
			name = data.substring(begin, separator);
			
			begin = separator + 1;
			separator = data.indexOf(";", begin);
			chemin = data.substring(begin, separator);

			begin = separator + 1;
			separator = data.indexOf(";", begin);
			date = data.substring(begin, separator);

			begin = separator + 1;
			desc = data.substring(begin, data.length());

			dataArray[i][0] = name;
			dataArray[i][1] = chemin;
			dataArray[i][2] = date;
			dataArray[i][3] = desc;
			
			
			if (name.length() > nameLength) {
				nameLength = name.length();
			}
			if (chemin.length() > cheminLength) {
				cheminLength = chemin.length();
			}
			if (date.length() > dateLength) {
				dateLength = date.length();
			}
			if (desc.length() > descLength) {
				descLength = desc.length();
			}
		}

		table = new JTable(dataArray, columnNames);
	}
	
	/**
	 * Permet d'avoir des strings de taille uniforme, nécessaire dans JList
	 */
	private void treatListData() {
		
		treatTableData();
		
		String nameCol = "Nom";
		String cheminCol = "Chemin";
		String dateCol = "Date";
		String descCol = "Description";
		
		String nameCol2 = String.format("%" + nameLength + "s", nameCol);
		String cheminCol2 = String.format("%" + cheminLength + "s", cheminCol);
		String dateCol2 = String.format("%" + dateLength + "s", dateCol);
		String descCol2 = String.format("%" + descLength + "s", descCol);

		String printData = nameCol2 + "  " + cheminCol2 + "  " + dateCol2 + "  " + descCol2;
		listModel.addElement(printData);
		
		for (String data : listData) {

			String name = null;
			String chemin = null;
			String date = null;
			String desc = null;

			String name2 = null;
			String chemin2 = null;
			String date2 = null;
			String desc2 = null;

			int begin = 0;
			int separator = data.indexOf(";");
			name = data.substring(begin, separator);
			
			begin = separator + 1;
			separator = data.indexOf(";", begin);
			chemin = data.substring(begin, separator);

			begin = separator + 1;
			separator = data.indexOf(";", begin);
			date = data.substring(begin, separator);

			begin = separator + 1;
			desc = data.substring(begin, data.length());
			
			name2 = String.format("%" + nameLength + "s", name);
			chemin2 = String.format("%" + cheminLength + "s", chemin);
			date2 = String.format("%" + dateLength + "s", date);
			desc2 = String.format("%" + descLength + "s", desc);
			
			String printData2 = name2 + "  " + chemin2 + "  " + date2 + "  " + desc2;
			listModel.addElement(printData2);
		}
	}
	

}
