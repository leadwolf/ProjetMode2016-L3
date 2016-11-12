package bddInterface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class FenetreEdit extends JFrame {

	private static final long serialVersionUID = 3259687165838481557L;
	private JPanel mainPanel;
	private TablePanel insertPanel;
	private JButton insertButton;
	private JButton resetButton;
	private JPanel buttonPanel;

	String[] columnNames = { "Nom", "Chemin", "Date", "Description", "" };
	private Dimension dim = new Dimension(600, 100);

	public FenetreEdit(String title) {
		super();

		insertButton = new JButton("Insert");
		resetButton = new JButton("Reset");
		insertButton.setActionCommand("insert");
		resetButton.setActionCommand("reset");
		ButtonControler buttonControler = new ButtonControler(this);
		insertButton.addActionListener(buttonControler);
		resetButton.addActionListener(buttonControler);

		insertPanel = new TablePanel(columnNames, 1, 4);
		DataTableModel dataTableModel = insertPanel.getDataTableModel();
		dataTableModel.setValueAt(today(), 0, 2);
		dataTableModel.setCellEditable(0, 2, false);

		/* PANNEAU BOUTONS */
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(insertButton);
		buttonPanel.add(resetButton);

		/* PANNEAU PRINCIPAL */
		mainPanel = new JPanel();
		mainPanel.setPreferredSize(dim);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createTitledBorder("Insert model data"));
		mainPanel.add(insertPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		/* FENETRE */
		String output = title.substring(0, 1).toUpperCase() + title.substring(1);
		setTitle(output);
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		setSize(dim);
		add(mainPanel);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public void setBorderTitle(String title) {
		mainPanel.setBorder(BorderFactory.createTitledBorder(title));
	}

	public void Insert() {

		String name = "";
		String chemin = "";
		String keywords = "";

		name = (String) insertPanel.getTable().getModel().getValueAt(0, 0);
		chemin = (String) insertPanel.getTable().getModel().getValueAt(0, 1);
		keywords = (String) insertPanel.getTable().getModel().getValueAt(0, 3);

		if (name != null && !name.equals("") && chemin != null && !chemin.equals("") && keywords != null && !keywords.equals("")) {
			try {
				// load the sqlite-JDBC driver using the current class loader
				// Class.forName("org.sqlite.JDBC");
				// ucanacces for Java 8, to replace when using Java 7
				Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
				Connection con = null;

				// creation de la table
				// connection =
				// DriverManager.getConnection("jdbc:sqlite:test.sqlite");
				// replace by line above when using Java 7
				con = DriverManager.getConnection("jdbc:ucanaccess://C:/Users/Master/git/test.accdb");

				PreparedStatement statement;
				statement = con.prepareStatement("insert into PLY values ?, ?, ?, ?");
				statement.setString(1, name);
				statement.setString(2, "ply/" + name + ".ply");
				statement.setString(3, today());
				statement.setString(4, keywords);
				super.dispose();
				statement.executeUpdate();
				String message = "L'insertion du modèle & été réalisé avec succès!";
				JOptionPane.showMessageDialog(null, message);
				System.exit(0);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();

			} catch (

			SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			showFieldErrorMessage(name, chemin, keywords);
		}

	}

	public void resetFields() {
		DataTableModel dataTableModel = insertPanel.getDataTableModel();
		for (int i = 0; i < dataTableModel.getRowCount(); i++) {
			for (int j = 0; j < dataTableModel.getColumnCount(); j++) {
				if (j != 2) {
					dataTableModel.setValueAt("", i, j);
				}
			}
		}
	}

	public String today() {
		Calendar dat = new GregorianCalendar();
		return dat.get(Calendar.YEAR) + "/" + (dat.get(Calendar.MONTH) + 1) + "/" + dat.get(Calendar.DAY_OF_MONTH);
	}

	public void showFieldErrorMessage(String name, String chemin, String keywords) {

		String message = "";
		int iter = 0;
		if (name == null || name.equals("")) {
			iter++;
		}
		if (chemin == null || chemin.equals("")) {
			iter++;
		}
		if (keywords == null || keywords.equals("")) {
			iter++;
		}
		if (iter > 1) {
			message += "Les champs";
		} else {
			message += "Le champ";
		}
		if (name == null || name.equals("")) {
			message += " nom";
		}
		if (chemin == null || chemin.equals("")) {
			if (iter != 0) {
				if (iter > 2 && message.contains("nom")) {
					message += ", chemin";
				} else if (message.contains("nom")) {
					message += " et chemin";
				} else {
					message += " chemin";
				}
			} else {
				message += " chemin";
			}
		}
		if (keywords == null || keywords.equals("")) {
			if (iter != 0) {
				if (iter > 2 && message.contains("nom")) {
					message += ", et keyword";
				} else if (iter > 1) {
					message += " et keyword";
				} else {
					message += "keyword";
				}
			} else {
				message += "keyword";
			}
		}
		if (iter == 1) {
			message += " n'a pas été précisé.";
		} else {
			message += " n'ont pas été précisés.";
		}
		message += "\nVeuillez remplir tous les champs.";
		JOptionPane.showMessageDialog(null, message, "Erreur d'insertion", JOptionPane.ERROR_MESSAGE);
	}

}
