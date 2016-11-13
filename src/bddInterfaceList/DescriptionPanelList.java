package bddInterfaceList;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Donne une JList non alignée
 * @author Master
 *
 */
public class DescriptionPanelList extends JPanel {

	private static final long serialVersionUID = 6086405568083027160L;

	private JList<String> list;
	private DefaultListModel<String> listModel;
	private JScrollPane scrollPane;
	String[] columnNames = { "Nom", "Chemin", "Date", "Description" };

	int totalLines = 0;
	ResultSetMetaData rsmd = null;

	public DescriptionPanelList(ResultSet rs) {
		super();

		listModel = new DefaultListModel<>();
		list = new JList<String>(listModel);
		
		listModel.addElement("Nom                                 Chemin                                Date                                   Mot Clés");
		
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				String data = "";
				for (int i = 1; i <= cols; i++) {
					data += rs.getString(i) + "              ";
				}
				listModel.addElement(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		scrollPane = new JScrollPane(list);
//		scrollPane.setPreferredSize(new Dimension(550, 100));
//		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(scrollPane);
	}
}
