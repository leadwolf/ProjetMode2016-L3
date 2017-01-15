package ply.bdd.vues;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ply.bdd.base.DAO;
import ply.main.vues.LeftSidePanel;
import ply.main.vues.MainFenetre;
import ply.main.vues.ModelPanel;

/**
 * Cette classe propose une JList des modèles qu'on pourra afficher dans mainFenetre avec un {@link ModelPanel}.n peut double cliquer sur un modele pour
 * l'ouvrir dans mainFenetre et chercher un modele par ses mots cles dans la barre de recherche.
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
	 * Dimension du {@link LeftSidePanel} qui a crée ceci. Utilisée pour savoir quelle taille mettre aux composants qu'on crée.
	 */
	private Dimension leftPanelDim;
	/**
	 * Le conseil qu'on affiche dans textField si l'utilisateur n'a rien saisi.
	 */
	private final String TIP;
	/**
	 * Si la barre de recherche contient que le TIP.
	 */
	private boolean isTip;

	/**
	 * Constructeur par défaut.
	 * 
	 * @param mainFenetre la fenetre à laquelle ce browser est attaché pour ajouter un modele ou notifie qu'on veut écrire au lieu de manipuler la figure.
	 * @param leftPanelDim les dimensions du panel parent, utilisé pour les initialisations des dimensions.
	 */
	public ModelBrowser(MainFenetre mainFenetre, Dimension leftPanelDim) {
		super();
		this.leftPanelDim = leftPanelDim;
		ControlFigureFocus controlFigureFocus = new ControlFigureFocus(mainFenetre);
		TIP = "Rechercher par nom ou mots clès";
		isTip = true;

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
					mainFenetre.addNewModel(modelsList.getSelectedValue());
				}
			}
		});
		modelsList.addFocusListener(controlFigureFocus);

		JScrollPane scrollPane = new JScrollPane(modelsList);

		/* SEARCH BAR */
		createSearchBar(mainFenetre);
		textField.addFocusListener(controlFigureFocus);

		/* THIS PANEL */
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(searchPanel);
		add(scrollPane);
	}

	/**
	 * Décharge le constructeur. Crée textField et clearButton.
	 * 
	 * @param mainFenetre la fenetre qu'on notifie de l'écriture
	 */
	private void createSearchBar(MainFenetre mainFenetre) {
		// SEARCH BAR
		int buttonDims = 25;
		SearchListener searchListener = new SearchListener();

		// TEXT FIELD
		textField = new JTextField(TIP);
		textField.getDocument().addDocumentListener(searchListener);
		textField.addFocusListener(searchListener);
		textField.setPreferredSize(new Dimension(leftPanelDim.width - buttonDims, buttonDims));
		textField.setMaximumSize(new Dimension(300, buttonDims));

		// BUTTON
		JButton clearButton = new JButton("X");
		// controleur tout simple
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetTip();
				mainFenetre.setCanControlFigure(true);
			}
		});
		clearButton.setMargin(new Insets(0, 0, 0, 0));
		clearButton.setPreferredSize(new Dimension(buttonDims, buttonDims));
		clearButton.setMaximumSize(new Dimension(buttonDims, buttonDims));
		clearButton.setToolTipText("Effacer la recherche");

		searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
		searchPanel.add(textField);
		searchPanel.add(clearButton);
	}

	/**
	 * Initialise la JList soit avec les modeles correspospondant aux mots cles dans textField ou le mot dans textField, soit avec tous modeles de la base.
	 */
	public void updateList() {
		if (!textField.getText().matches("\\s*")) {
			isTip = false;
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
		isTip = true;
	}

	/**
	 * Vide la textField.
	 */
	public void clearSearchBar() {
		textField.setText("");
	}

	/**
	 * DocumentListener et FocusListener pour le JTextField. On fait la recherhe sql à chaque caractère et on notifie {@link MainFenetre} s'il peut controler la
	 * figure avec le clavier au lieu d'écrire..
	 * 
	 * @author Master
	 *
	 */
	private class SearchListener implements DocumentListener, FocusListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			updateList();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			updateList();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
		public void focusGained(FocusEvent e) {
			if (isTip) {
				clearSearchBar();
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
			if (textField.getText().matches("\\s*")) {
				resetTip();
			}
		}

	}

	/**
	 * Simple controleur pour notifier {@link MainFenetre} s'il peut controler la figure avec le clavier au lieu d'écrire. Interne car trop petite et utilisée
	 * qu'ici.
	 * 
	 * @author L3
	 *
	 */
	private class ControlFigureFocus implements FocusListener {

		private MainFenetre mainFenetre;

		public ControlFigureFocus(MainFenetre mainFenetre) {
			this.mainFenetre = mainFenetre;
		}

		@Override
		public void focusLost(FocusEvent e) {
			mainFenetre.setCanControlFigure(true);
		}

		@Override
		public void focusGained(FocusEvent e) {
			mainFenetre.setCanControlFigure(false);
		}
	}

}
