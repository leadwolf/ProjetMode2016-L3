package bddInterface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Sert à présenter les informations à éditer ou un formulaire vide pour l'insertion
 * @author L3
 *
 */
public class FenetreEdit extends JFrame {

	private static final long serialVersionUID = 3259687165838481557L;
	private JPanel mainPanel;
	private TablePanel insertPanel;
	private JButton insertButton;
	private JButton resetButton;
	private JPanel buttonPanel;

	/**
	 * 
	 * @param title
	 * @param rows le nombre de lignes du tableau d'insertion
	 * @param colNames les noms des colonnes, donne aussi le nombre de colonnes du tableau d'insertion
	 * @param noModifyColumns les nombres réels des champs à ne pas être modifiés par l'utilisateur
	 */
	public FenetreEdit(String title, int rows, int columns, String[] colNames, int[] noModifyColumns) {
		super();

		insertButton = new JButton("Insert");
		resetButton = new JButton("Reset");
		insertButton.setActionCommand("insert");
		resetButton.setActionCommand("reset");
		ButtonControler buttonControler = new ButtonControler(this);
		insertButton.addActionListener(buttonControler);
		resetButton.addActionListener(buttonControler);

		insertPanel = new TablePanel(rows, columns, colNames);
		DataTableModel dataTableModel = insertPanel.getDataTableModel();
		for (int i=0;i<rows;i++) {
			for (int j=0;j<noModifyColumns.length;j++) {
				dataTableModel.setValueAt(today(), i, noModifyColumns[j]-1);
				dataTableModel.setCellEditable(i, noModifyColumns[j]-1, false);
			}
		}

		/* PANNEAU BOUTONS */
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(insertButton);
		buttonPanel.add(resetButton);

		/* PANNEAU PRINCIPAL */

		Dimension dim;
		if (rows <= 10) { 
			dim = new Dimension(600, 90 + (16*rows));
		} else {
			dim = new Dimension(600, 250);
		}
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

				PreparedStatement statementFind;
				statementFind = con.prepareStatement("select * from PLY where nom = ?");
				statementFind.setString(1, name);
				ResultSet found = statementFind.executeQuery();
				
				if (!found.next()) {
					PreparedStatement statement;
					statement = con.prepareStatement("insert into PLY values ?, ?, ?, ?");
					statement.setString(1, name);
					statement.setString(2, "ply/" + name + ".ply");
					statement.setString(3, today());
					statement.setString(4, keywords);
					super.dispose();
					statement.executeUpdate();
					String message = "L'insertion du modèle " + name + " été réalisé avec succès!";
					JOptionPane.showMessageDialog(null, message);
					System.exit(0);
				} else {
					String message = "Le modèle " + name + " existe déja";
					JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
				}
				
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();

			} catch (SQLException e) {
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
