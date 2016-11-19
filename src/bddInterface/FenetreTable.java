package bddInterface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.Connection;
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
	private Connection con;

	/**
	 * Sauvegarde des données initiales de la ligne lors du chargement de la base
	 */
	String[] orignalFields;

	Dimension dim;

	/**
	 * Crée une FenetreTable soit avec des données existantes pour --edit ou vide si rs est null pour --add
	 * 
	 * @param rows le nombre de lignes de la table
	 * @param cols le nombre de colonnes de la table
	 * @param rs ResultSet des données à utliser dans la table, null si veut laisser vide
	 * @param connection la connection utilisée pour les requêtes update/edit
	 */
	public FenetreTable(int rows, int cols, ResultSet rs, Connection connection) {
		boolean success = false;
		if (rows > 0 && cols > 0) {
			this.con = connection;

			if (rs != null) {
				orignalFields = new String[cols];
				try {
					if (rs.next()) {
						int colCount = rs.getMetaData().getColumnCount();
						for (int j = 1; j <= colCount; j++) {
							orignalFields[j - 1] = rs.getString(j);
						}
					}
					success = true;
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (!success) {
						try {
							con.close();
						} catch (SQLException e) {
							System.err.println(e);
						}
					}
				}
			}
		} else {
			throw new NullPointerException("Row and columns must be superior to 0");
		}
	}

	/**
	 * Crée une fenetre avec un TablePanel avec des boutons mais champs vides
	 * 
	 * @param title le titre de ce fenetre
	 * @param rows le nombre de lignes vides à avoir
	 * @param buttonNames les noms des boutons à avoir, laisser null si pas de boutons
	 * @param colNames les noms des colonnes
	 * @param noModifyColumns les colonnes qui ne seront pas possibles de modifier
	 * @param connection
	 */
	public FenetreTable(String title, int rows, String[] buttonNames, String[] colNames, int[] noModifyColumns, Connection connection) {
		super();

		this.con = connection;
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
	 * 
	 * @param title le titre de ce fenetre
	 * @param rows le nombre de lignes que la table comportera
	 * @param buttonNames les noms des boutons à avoir, laisser null si pas de boutons
	 * @param rs un ResultSet qui comporte les valeurs avec lesquelles remplir la table
	 * @param colNames les noms des colonnes
	 * @param noModifyColumns les colonnes qui ne seront pas possibles à modifier
	 * @param connection
	 */
	public FenetreTable(String title, int rows, String[] buttonNames, ResultSet rs, String[] colNames, int[] noModifyColumns, Connection connection) {
		super();

		this.con = connection;
		tablePanel = new TablePanel(rows, colNames.length, colNames);
		orignalFields = new String[colNames.length];
		TableModel dataTableModel = tablePanel.getTableModel();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < noModifyColumns.length; j++) {
				dataTableModel.setCellEditable(i, noModifyColumns[j] - 1, false);
			}
		}
		boolean success = false;
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
			success = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (!success) {
				try {
					con.close();
				} catch (SQLException e) {
					System.err.println(e);
				}
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
	 * Crée les dimensions du fenetre en fonction du nombre de lignes dans la table
	 * 
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
	 * 
	 * @param title
	 * @param rows
	 */
	private void setUpMainPanel(String title, int rows) {

		/* PANNEAU PRINCIPAL */
		mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension((int) dim.getWidth(), (int) dim.getHeight()));
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(tablePanel, BorderLayout.CENTER);
	}

	/**
	 * Crée le reste du fenetre et ajoute mainPanel
	 * 
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
	 * @param rows le nombre de lignes que comportera la table
	 * @param buttonNames les noms des boutons
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
	 * 
	 * @param title
	 */
	public void setPanelBorderTitle(String title) {
		mainPanel.setBorder(BorderFactory.createTitledBorder(title));
	}

	/**
	 * Prend les valeurs à update du JTable, vérifie qu'elles sont différentes que les originales et exécute {@link #updateTable(String, String, String, boolean)}
	 * 
	 * @param debug afficher ou non les JOptionPane d'erreur/succès
	 * @return si le modèle a bien été mis à jour
	 */
	public boolean updateTableAmorce(boolean debug) {

		String name = "";
		String chemin = "";
		String keywords = "";

		name = (String) tablePanel.getTable().getModel().getValueAt(0, 0);
		chemin = (String) tablePanel.getTable().getModel().getValueAt(0, 1);
		keywords = (String) tablePanel.getTable().getModel().getValueAt(0, 3);

		if ((name != null && !name.equals("") && name != orignalFields[0]) || (chemin != null && !chemin.equals("") && chemin != orignalFields[1])
				|| (keywords != null && !keywords.equals("") && keywords != orignalFields[3])) {
			return updateTable(name, chemin, keywords, debug);
		} else {
			if (!debug) {
				showFieldErrorMessage(name, chemin, keywords);
				return false;
			}
		}
		return false;
	}

	/**
	 * Prend les valeurs à update en paramètre, vérifie qu'elles sont différentes que les originales et exécute {@link #updateTable(String, String, String, boolean)}
	 * 
	 * @param name
	 * @param chemin
	 * @param keywords
	 * @param debug afficher ou non les JOptionPane d'erreur/succès
	 * @return si le modèle a bien été mis à jour
	 */
	public boolean updateTableAmorce(String name, String chemin, String keywords, boolean debug) {
		if ((name != null && !name.equals("") && name != orignalFields[0]) || (chemin != null && !chemin.equals("") && chemin != orignalFields[1])
				|| (keywords != null && !keywords.equals("") && keywords != orignalFields[3])) {
			return updateTable(name, chemin, keywords, debug);
		} else {
			if (!debug) {
				showFieldErrorMessage(name, chemin, keywords);
				return false;
			}
		}
		return false;
	}

	/**
	 * Exécute la requête SQL qui met à jour la ligne de la table
	 * 
	 * @param name
	 * @param chemin
	 * @param keywords
	 * @param debug
	 * @return si l'update a bien modifié plus d'une ligne
	 */
	private boolean updateTable(String name, String chemin, String keywords, boolean debug) {
		boolean success = false;
		try {
			Class.forName("org.sqlite.JDBC");
			PreparedStatement statement;

			int updateCpt = 0;
			boolean updateNom = false, updateChemin = false, updateDesc = false;
			String updateString = "update PLY set ";
			if (name != null && !name.equals("") && name != orignalFields[0]) {
				updateString += "nom = ?";
				updateNom = true;
				updateCpt++;
			}
			if (chemin != null && !chemin.equals("")) {
				if (updateCpt == 0) {
					updateString += "chemin = ?";
				} else {
					updateString += ", chemin = ?";
				}
				updateChemin = true;
				updateCpt++;
			}
			if (keywords != null && !keywords.equals("")) {
				if (updateCpt == 0) {
					updateString += "description = ?";
				} else {
					updateString += ", description = ?";
				}
				updateDesc = true;
				updateCpt++;
			}
			updateString += " where nom = ?";

			statement = con.prepareStatement(updateString);
			if (updateNom) {
				statement.setString(1, name);
			} else if (updateCpt == 0 && updateChemin) {
				statement.setString(1, chemin);
			} else if (updateCpt > 1 && updateChemin) {
				statement.setString(2, chemin);
			} else if (updateCpt == 0 && updateDesc) {
				statement.setString(1, keywords);
			} else if (updateCpt > 1 && updateDesc) {
				statement.setString(2, keywords);
			} else if (updateCpt > 2 && updateDesc) {
				statement.setString(3, keywords);
			}
			statement.setString(updateCpt + 1, orignalFields[0]);

			super.dispose();
			int result = statement.executeUpdate();
			if (!debug && result > 0) {
				String message = "La mise à jour du modèle " + orignalFields[0] + " vers " + name + " été réalisée avec succès!";
				JOptionPane.showMessageDialog(null, message);
			} else if (!debug && result <= 0) {
				String message = "La mise à jour du modèle " + orignalFields[0] + " vers " + name + " n'a pas pu être réalisée";
				JOptionPane.showMessageDialog(null, message);
			}
			// System.exit(0);
			success = true;
			return result > 0;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (!success) {
				try {

					con.close();
				} catch (SQLException e) {
					System.err.println(e);
				}
			}
		}
		if (!debug) {
			String message = "La mise à jour du modèle " + orignalFields[0] + " vers " + name + " n'a pas pu être réalisée";
			JOptionPane.showMessageDialog(null, message);
		}
		return false;
	}

	/**
	 * Prend les valeurs à insérer du JTable, vérifie si le nom du modèle à insérer n'existe pas et éxecute {@link #insertTable(String, String, String, boolean)}
	 * 
	 * @param debug
	 * @return si l'insertion du modèle a pu être réalisée
	 */
	public boolean insertTableAmorce(boolean debug) {

		String name = "";
		String chemin = "";
		String keywords = "";

		name = (String) tablePanel.getTable().getModel().getValueAt(0, 0);
		chemin = (String) tablePanel.getTable().getModel().getValueAt(0, 1);
		keywords = (String) tablePanel.getTable().getModel().getValueAt(0, 3);

		// il faut qu'au moins le nom du modèle soit renseigné
		if ((name != null && !name.equals("")) || ((chemin != null && !chemin.equals("")) || (keywords != null && !keywords.equals("")))) {
			boolean success = false;
			try {
				Class.forName("org.sqlite.JDBC");
				PreparedStatement statement = con.prepareStatement("select * from PLY where nom = ?");
				statement.setString(1, name);
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					if (!debug) {
						String message = "Le modèle " + name + " existe déja.";
						JOptionPane.showMessageDialog(null, message);
					}
					success = true;
					return false;
				} else {
					success = true;
					return insertTable(name, chemin, keywords, debug);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (!success) {
					try {
						con.close();
					} catch (SQLException e) {
						System.err.println(e);
					}
				}
			}
		} else {
			if (!debug) {
				showFieldErrorMessage(name, chemin, keywords);
				return false;
			}
		}
		return false;
	}

	/**
	 * Prend les valeurs à insérer en paramètre, vérifie si le nom du modèle à insérer n'existe pas et éxecute {@link #insertTable(String, String, String, boolean)}
	 * 
	 * @param name
	 * @param chemin
	 * @param keywords
	 * @param debug
	 * @return si l'insertion du modèle a pu être réalisée
	 */
	public boolean insertTableAmorce(String name, String chemin, String keywords, boolean debug) {// il faut qu'au moins le nom du modèle soit renseigné
		if ((name != null && !name.equals("")) || ((chemin != null && !chemin.equals("")) || (keywords != null && !keywords.equals("")))) {
			boolean success = false;
			try {
				Class.forName("org.sqlite.JDBC");
				PreparedStatement statement = con.prepareStatement("select * from PLY where nom = ?");
				statement.setString(1, name);
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					if (!debug) {
						String message = "Le modèle " + name + " existe déja.";
						JOptionPane.showMessageDialog(null, message);
					}
					success = true;
					return false;
				} else {
					success = true;
					return insertTable(name, chemin, keywords, debug);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (!success) {
					try {
						con.close();
					} catch (SQLException e) {
						System.err.println(e);
					}
				}
			}
		} else {
			if (!debug) {
				showFieldErrorMessage(name, chemin, keywords);
				return false;
			}
		}
		return false;
	}

	/**
	 * Crée une requête SQL en fonction de la validité des paramètres fournis et l'éxecute
	 * 
	 * @param name
	 * @param chemin
	 * @param keywords
	 * @param debug
	 * @return si l'insertion du modèle a pu être réalisée
	 */
	private boolean insertTable(String name, String chemin, String keywords, boolean debug) {
		boolean success = false;
		try {
			Class.forName("org.sqlite.JDBC");
			PreparedStatement statement;

			statement = con.prepareStatement("insert into PLY values(?, ?, ?, ?);");

			if (name != null && !name.equals("")) {
				statement.setString(1, name);
			} else {
				statement.setString(1, "");
			}
			if (chemin != null && !chemin.equals("")) {
				statement.setString(2, chemin);
			} else {
				statement.setString(2, "");
			}
			statement.setString(3, today());
			if (keywords != null && !keywords.equals("")) {
				statement.setString(4, keywords);
			} else {
				statement.setString(4, "");
			}

			super.dispose();
			int result = statement.executeUpdate();
			if (!debug && result > 0) {
				String message = "L'insertion du modèle " + name + " a été réalisée avec succès!";
				JOptionPane.showMessageDialog(null, message);
			} else if (!debug && result <= 0) {
				String message = "L'insertion du modèle " + name + " n'a pas pu être réalisée";
				JOptionPane.showMessageDialog(null, message);
			}
			// System.exit(0);
			success = true;
			return result > 0;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (!success) {
				try {
					con.close();
				} catch (SQLException e) {
					System.err.println(e);
				}
			}
		}
		if (!debug) {
			String message = "Le modèle " + name + " n'a pas pu être insérée";
			JOptionPane.showMessageDialog(null, message, "Mauvais arguments", JOptionPane.ERROR_MESSAGE);
		}
		return false;
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
	 * 
	 * @return un String de la date en YYYY/MM/DD
	 */
	public String today() {
		Calendar dat = new GregorianCalendar();
		return dat.get(Calendar.YEAR) + "/" + (dat.get(Calendar.MONTH) + 1) + "/" + dat.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Crée un JOptionPane d'erreur en fonction de la validité du nom, chemin et keywords
	 * 
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
