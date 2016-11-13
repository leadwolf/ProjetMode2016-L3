package bddInterface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Sert à présenter les informations à éditer, visualiser ou à insérer.
 * 
 * @author L3
 *
 */
public class FenetreTable extends JFrame {

	private static final long serialVersionUID = 3259687165838481557L;
	private JPanel mainPanel;
	private TablePanel tablePanel;
	private List<JButton> buttonList;
	private JPanel buttonPanel;

	String[] orignalFields;

	Dimension dim;

	/**
	 * Crée une fenetre avec un TablePanel avec des boutons mais champs vides
	 * @param title le titre de ce fenetre
	 * @param rows le nombre de lignes vides à avoir
	 * @param buttonNames les noms des boutons à avoir, laisser null si pas de boutons
	 * @param colNames les noms des colonnes
	 * @param noModifyColumns les colonnes qui ne seront pas possibles de modifier
	 */
	public FenetreTable(String title, int rows, String[] buttonNames, String[] colNames, int[] noModifyColumns) {
		super();

		tablePanel = new TablePanel(rows, colNames.length, colNames);
		orignalFields = new String[colNames.length];
		TableModel dataTableModel = tablePanel.getTableModel();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < noModifyColumns.length; j++) {
				dataTableModel.setValueAt(today(), i, noModifyColumns[j] - 1);
				dataTableModel.setCellEditable(i, noModifyColumns[j] - 1, false);
			}
		}

		if (buttonNames != null && buttonNames.length > 0) {
			setDims(true, rows);
			SetupPanelWithButtons(title, rows, buttonNames);
		} else {
			setDims(false, rows);
			SetupPanelWithOutButtons(title, rows);
		}
	}

	/**
	 * Crée une fenetre avec un TabelPanel, des boutons et des champs remplis
	 * @param title le titre de ce fenetre
	 * @param rows le nombre de lignes que la table comportera
	 * @param buttonNames les noms des boutons à avoir, laisser null si pas de boutons
	 * @param rs un ResultSet qui comporte les valeurs avec lesquelles remplir la table
	 * @param colNames les noms des colonnes
	 * @param noModifyColumns les colonnes qui ne seront pas possibles à modifier
	 */
	public FenetreTable(String title, int rows, String[] buttonNames, ResultSet rs, String[] colNames, int[] noModifyColumns) {
		super();

		tablePanel = new TablePanel(rows, colNames.length, colNames);
		orignalFields = new String[colNames.length];
		TableModel dataTableModel = tablePanel.getTableModel();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < noModifyColumns.length; j++) {
				dataTableModel.setCellEditable(i, noModifyColumns[j] - 1, false);
			}
		}
		try {
			int currentRow = 0;
			while (rs.next()) {
				int colCount = rs.getMetaData().getColumnCount();
				for (int j = 1; j <= colCount; j++) {
					String data = rs.getString(j);
					if (currentRow == 0) {
						orignalFields[j - 1] = data;
					}
					dataTableModel.setValueAt(data, currentRow, j - 1);
				}
				currentRow++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (buttonNames != null && buttonNames.length > 0) {
			setDims(true, rows);
			SetupPanelWithButtons(title, rows, buttonNames);
		} else {
			setDims(false, rows);
			SetupPanelWithOutButtons(title, rows);
		}
	}
	
	/**
	 * Crée les dimensions du fenetre en fonction du nombre de lignes dans la table
	 * @param buttons
	 * @param rows
	 */
	private void setDims(boolean buttons, int rows) {
		dim = new Dimension(600, 250);
		if (buttons) {
			if (rows <= 10) {
				dim = new Dimension(600, 125 + (16 * rows));
			}
		} else {
			if (rows <= 10) {
				dim = new Dimension(600, 100 + (16 * rows));
			}
		}
	}
	
	/**
	 * Crée mainPanel
	 * @param title
	 * @param rows
	 */
	private void setUpMainPanel(String title, int rows) {

		/* PANNEAU PRINCIPAL */
		mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension((int) dim.getWidth(), (int) dim.getHeight()) );
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(tablePanel, BorderLayout.CENTER);
	}

	/**
	 * Crée le reste du fenetre et ajoute mainPanel
	 * @param title
	 */
	private void setupFenetre(String title) {
		/* FENETRE */
		String output = title.substring(0, 1).toUpperCase() + title.substring(1);
		setTitle(output);
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		add(mainPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * Crée mainPanel et fenetre sans boutons
	 * 
	 * @param title
	 * @param rows
	 */
	private void SetupPanelWithOutButtons(String title, int rows) {
		/* PANNEAU PRINCIPAL */
		setUpMainPanel(title, rows);

		/* FENETRE */
		setupFenetre(title);
		
		setSize(dim);
	}

	/**
	 * Crée mainPanel, fenetre et ajoute des boutons
	 * 
	 * @param title
	 * @param rows
	 */
	private void SetupPanelWithButtons(String title, int rows, String[] buttonNames) {

		/* BOUTONS */
		buttonList = new ArrayList<>();
		ButtonControler buttonControler = new ButtonControler(this);

		for (int i = 0; i < buttonNames.length; i++) {
			JButton tmpButton = new JButton(buttonNames[i]);
			tmpButton.setActionCommand(buttonNames[i]);
			tmpButton.addActionListener(buttonControler);
			buttonList.add(tmpButton);
		}

		/* PANNEAU BOUTONS */
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		for (JButton button : buttonList) {
			buttonPanel.add(button);
		}

		/* PANNEAU PRINCIPAL */
		SetupPanelWithOutButtons(title, rows);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		setSize(dim);
	}

	/**
	 * Crée un border défaut pour le panneau qui comporte la table
	 * @param title
	 */
	public void setPanelBorderTitle(String title) {
		mainPanel.setBorder(BorderFactory.createTitledBorder(title));
	}

	/**
	 * Exécute la requète qui met à jour la ligne de la table
	 */
	public void modifyFields() {

		String name = "";
		String chemin = "";
		String keywords = "";

		name = (String) tablePanel.getTable().getModel().getValueAt(0, 0);
		chemin = (String) tablePanel.getTable().getModel().getValueAt(0, 1);
		keywords = (String) tablePanel.getTable().getModel().getValueAt(0, 3);

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
				statement = con.prepareStatement("update PLY set nom = ?, chemin = ?, description = ? where nom = ?");
				statement.setString(1, name);
				statement.setString(2, chemin);
				statement.setString(3, keywords);
				statement.setString(4, orignalFields[0]);
				super.dispose();
				statement.executeUpdate();
				String message = "La mise à jour du modèle " + orignalFields[0] + " vers " + name + " été réalisée avec succès!";
				JOptionPane.showMessageDialog(null, message);
				System.exit(0);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			showFieldErrorMessage(name, chemin, keywords);
		}
	}

	/**
	 * Exécute la requète qui va insérer les données de la ligne de la table
	 */
	public void insert() {

		String name = "";
		String chemin = "";
		String keywords = "";

		name = (String) tablePanel.getTable().getModel().getValueAt(0, 0);
		chemin = (String) tablePanel.getTable().getModel().getValueAt(0, 1);
		keywords = (String) tablePanel.getTable().getModel().getValueAt(0, 3);

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
					statement.setString(2, chemin);
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

	/**
	 * Vide les champs modifiables
	 */
	public void resetFields() {
		TableModel dataTableModel = tablePanel.getTableModel();
		for (int i = 0; i < dataTableModel.getRowCount(); i++) {
			for (int j = 0; j < dataTableModel.getColumnCount(); j++) {
				if (dataTableModel.isCellEditable(i, j)) {
					dataTableModel.setValueAt("", i, j);
				}
			}
		}
	}
	
	/**
	 * Donne la date d'aujourd'hui
	 * @return
	 */
	public String today() {
		Calendar dat = new GregorianCalendar();
		return dat.get(Calendar.YEAR) + "/" + (dat.get(Calendar.MONTH) + 1) + "/" + dat.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Crée un JOptionPane d'erreur en fonction de la validité du nom, chemin et keywords
	 * @param name
	 * @param chemin
	 * @param keywords
	 */
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
