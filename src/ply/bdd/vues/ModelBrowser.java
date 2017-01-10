package ply.bdd.vues;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import main.vues.MainFenetre;
import main.vues.ModelPanel;
import ply.bdd.controlers.SearchListener;
import ply.bdd.other.DAO;

/**
 * Cette classe propose une JList des modèles qu'on pourra afficher dans mainFenetre avec un {@link ModelPanel}.n peut double
 * cliquer sur un modele pour l'ouvrir dans mainFenetre et chercher un modele par ses mots cles dans la barre de recherche.
 * 
 * @author L3
 *
 */
public class ModelBrowser extends JPanel {

	/**
	 * ID générée.
	 */
	private static final long serialVersionUID = 1882610869462518928L;
	/**
	 * Le modèle de la JList à lequel on ajoute les noms des modèles.
	 */
	private DefaultListModel<String> listModel;
	/**
	 * Le JTextField dans lequel l'utilisateur saisit les mots cles à rechercher.
	 */
	private JTextField textField;
	/**
	 * Le panel comportant le JTextField de recherche et le JButton pour effacer l'écriture.
	 */
	private JPanel searchPanel;
	/**
	 * Le conseil qu'on affiche dans textField si l'utilisateur n'a rien saisi.
	 */
	private final String TIP;

	/**
	 * Constructeur par défaut.
	 * 
	 * @param mainFenetre
	 */
	public ModelBrowser(MainFenetre mainFenetre) {
		super();
		TIP = "Rechercher par mots clès";

		/* JLIST */
		listModel = new DefaultListModel<>();
		initListModel(null); // initialise la liste sans recherche de mots cles

		JList<String> modelsList = new JList<>(listModel);
		modelsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		modelsList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// ouvre un modele sur un double-clic.
				if (e.getClickCount() == 2 && e.getSource() instanceof JList<?>) {
					mainFenetre.addNewModel(modelsList.getSelectedIndex());
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(modelsList);

		/* SEARCH BAR */
		createSearchBar();

		/* THIS PANEL */
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(searchPanel);
		add(scrollPane);
	}

	/**
	 * Décharge le constructeur. Crée textField et clearButton.
	 */
	private void createSearchBar() {
		// SEARCH BAR
		Dimension dim = new Dimension(250, 25);
		SearchListener searchListener = new SearchListener(this);

		// TEXT FIELD
		textField = new JTextField(TIP);
		textField.getDocument().addDocumentListener(searchListener);
		textField.addFocusListener(searchListener);
		textField.setPreferredSize(dim);
		textField.setMaximumSize(new Dimension(300, dim.height));

		// BUTTON
		JButton clearButton = new JButton("X");
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetTip();
			}
		});
		clearButton.setMargin(new Insets(0, 0, 0, 0));
		clearButton.setPreferredSize(new Dimension(dim.height, dim.height));
		clearButton.setMaximumSize(new Dimension(dim.height, dim.height));
		clearButton.setToolTipText("Effacer la recherche");

		searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
		searchPanel.add(textField);
		searchPanel.add(clearButton);
	}

	/**
	 * Initialise la JList soit avec les mots cles de la barre de recherche ou avec les modeles de la base.
	 */
	public void updateList() {
		if (!textField.getText().matches("\\s*")) {
			String[] keywords = textField.getText().split("\\s+");
			initListModel(keywords);
		} else {
			initListModel(null);
		}
	}

	/**
	 * Initialise le DefaultListModel du JList à une liste des modeles ayant keywords comme mots cles.
	 * 
	 * @param keywords laisser null si on veut remplir avec les modeles de la base. Voir {@link DAO}
	 */
	private void initListModel(String[] keywords) {
		List<String> names = new ArrayList<>();
		try {
			listModel.clear();
			ResultSet rs = null;

			if (keywords == null) {
				rs = DAO.INSTANCE.getAll();
			} else {
				rs = DAO.INSTANCE.findKeywordsAndName(keywords);
			}

			while (rs.next()) {
				names.add(rs.getString(1));
			}
			Collections.sort(names);
			for (String name : names) {
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
				listModel.addElement(name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remet le texte de textField au tip par défaut.
	 */
	public void resetTip() {
		textField.setText(TIP);
	}

	/**
	 * Vide la textField.
	 */
	public void clearSearchBar() {
		textField.setText("");
	}

}
