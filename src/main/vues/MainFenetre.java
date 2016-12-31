package main.vues;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.controlers.MenuControler;
import ply.bdd.controlers.JListControler;
import ply.bdd.other.BaseDeDonnees;
import ply.bdd.vues.BDDPanel;
import ply.bdd.vues.ModelBrowser;
import ply.bdd.vues.ModelInfo;
import ply.plyModel.modeles.FigureModel;

public class MainFenetre extends JFrame {

	/**
	 * Le Path vers le dossier contenant les modèles. Soit le dossier contenant le modèle précisé dans le premier
	 * constructeur ou le dossier "data/".
	 */
	private Path parentPath;
	private static File[] allFiles;
	private List<ModelPanel> modelPanelList;
	private JSplitPane mainPanel;
	private JTabbedPane tabbedPane;
	private LeftSidePanel leftPanel;

	private JLabel toolLabel;

	private JMenuBar menuBar;
	private Dimension frameDim;
	private int leftPanelWidth = 350;
	private int separatorWidth = 30;
	private int tabHeight = 23;

	/**
	 * Crée la fenêtre de l'application. Un JSplitPane contenant à gauche {@link ModelInfo} et {@link ModelBrowser} et à
	 * droite, un {@link JTabbedPane} contenant un {@link ModelPanel} du FigureModel précisé en paramètre et un
	 * {@link BDDPanel}.
	 * 
	 * @param figureModel
	 *            le modèle avec lequel créer le ModelPanel.
	 * @param options
	 *            [0] &gt; drawPoints, [1] &gt; drawSegments, [2] &gt; drawFaces, [3] &gt; resetBase, [4] &gt; fillBase,
	 */
	public MainFenetre(FigureModel figureModel, boolean[] options) {
		super();
		this.parentPath = figureModel.getPath().getParent();
		firstSetup();

		frameDim = new Dimension(1200, 800);

		/* ModelPanel */
		Dimension modelPanelDim = new Dimension(frameDim.width - leftPanelWidth - (separatorWidth / 2),
				frameDim.height - tabHeight);
		ModelPanel modelPanel = new ModelPanel(figureModel, modelPanelDim, options[0], options[1], options[2]);
		modelPanel.initModelForWindow();
		modelPanelList.add(modelPanel);

		/* BDD PANEL */
		// par défaut on veut afficher toute la base
		BDDPanel bddPanel = BaseDeDonnees.getPanel(new String[] { "--all" }, null,
				new boolean[] { options[3], options[4], false });
		if (bddPanel == null) {
			System.exit(1);
		}
		bddPanel.setMainFenetre(this);

		/* LEFT PANEL */
		String modelName = figureModel.getPath().getFileName().toString();
		modelName = modelName.substring(0, modelName.lastIndexOf(".")); // capitalize first letter
		createLeftPanel(modelName);

		/* TABBED PANE */
		tabbedPane = new JTabbedPane();
		addTab(modelName.substring(0, 1).toUpperCase() + modelName.substring(1), modelPanel);
		addTab("Base", bddPanel);
		addTabChangeListener();

		/* MAIN PANEL */
		mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, tabbedPane);

		/* FENETRE */
		setupFenetre();
	}

	/**
	 * Crée la fenêtre de l'application. Un JSplitPane contenant à gauche {@link ModelInfo} et {@link ModelBrowser} et à
	 * droite, un {@link JTabbedPane} contenant {@link BDDPanel} crée avec la commande précisée.
	 * 
	 * @param command
	 *            la commande bdd avec laqelle on a lancé le programme et qu'on passe à BDDPanel.
	 * @param options
	 *            [0] &gt; resetBase, [1] &gt; fillBase
	 */
	public MainFenetre(String[] command, boolean[] options) {
		super();
		this.parentPath = Paths.get("data/");
		firstSetup();

		frameDim = new Dimension(800, 500);

		/* BDD PANEL */
		BDDPanel bddPanel = BaseDeDonnees.getPanel(command, null, new boolean[] { options[0], options[1], false });
		if (bddPanel == null) {
			System.exit(1);
		}
		bddPanel.setMainFenetre(this);

		/* LEFT PANEL */
		createLeftPanel(null);

		/* TABBED PANE */
		tabbedPane = new JTabbedPane();
		addTab("Base", bddPanel);
		addTabChangeListener();

		/* MAIN PANEL */
		mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, tabbedPane);

		/* FENETRE */
		setupFenetre();
	}

	/**
	 * Crée JMenuBar et initialise les variables.
	 */
	private void firstSetup() {
		setupMenu();
		setJMenuBar(menuBar);
		allFiles = parentPath.toFile().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.lastIndexOf('.') > 0) {
					String str = name.substring(name.lastIndexOf("."));
					// match path name extension
					if (str.equals(".ply")) {
						return true;
					}
				}
				return false;
			}
		});
		modelPanelList = new ArrayList<>();
	}

	/**
	 * Crée tabbedPane et ajoute un {@link ChangeListener}.
	 */
	private void addTabChangeListener() {
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (tabbedPane.getSelectedComponent() instanceof ModelPanel) {
					ModelPanel newFocus = (ModelPanel) tabbedPane.getSelectedComponent();
					changeModelPanelFocus(newFocus);
				}
			}
		});
	}

	/**
	 * 
	 */
	private void setupFenetre() {
		this.setTitle("Modelisationator");
		setLayout(new BorderLayout());
		setSize(frameDim);

		add(mainPanel);
		createToolTip();
		pack();
		// setVisible(true);

		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	/**
	 * Crée le toolPanel contenant toolLabel et l'ajoute à cette fenetre.
	 */
	private void createToolTip() {
		JPanel toolPanel = new JPanel();
		toolLabel = new JLabel(
				"Bienvenue à Modelisationator. Double cliquez sur un modèle dans le \"Browser\" pour l'ouvrir dans un nouvel onglet. Maintenez \"SHIFT\" pour des transformations plus précises.");
		toolPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		toolPanel.add(toolLabel);
		add(toolPanel, BorderLayout.PAGE_END);
	}

	/**
	 * @param newTip
	 */
	public void setToolTip(String newTip) {
		toolLabel.setText(newTip);
	}

	/**
	 * @param modelName
	 *            le nom du premier modèle affiché. Sert à initialiser {@link ModelInfo}. Laisser null si on affiche la
	 *            bdd en premier.
	 */
	private void createLeftPanel(String modelName) {
		Dimension leftPanelDim = new Dimension(leftPanelWidth - (separatorWidth / 2), frameDim.height);
		leftPanel = new LeftSidePanel(parentPath, modelName, leftPanelDim);
		leftPanel.setPreferredSize(leftPanelDim);
		// leftPanel.setMinimumSize(leftPanelDim);
		leftPanel.setMinimumSize(new Dimension(10, leftPanelDim.height));
		leftPanel.addMouseListenerToList(new JListControler(this));
	}

	/**
	 * Ajoute un ModelPanel à tabbedPane quand on double clique sur un nom de modèle dans ModelBrowser
	 * 
	 * @param clickIndex
	 *            the index of the click in the JList
	 */
	public void addNewModel(int clickIndex) {
		Path newModelPath = allFiles[clickIndex].toPath();
		String modelName = newModelPath.getFileName().toString();
		modelName = modelName.substring(0, 1).toUpperCase() + modelName.substring(1, modelName.lastIndexOf("."));
		FigureModel newFigureModel;

		if (modelWasDisplayed(modelName)) { // on a déja affiché le modèle associé à newPath. Il est dans
											// modelPanelList.
			if (isModelInTabbedPane(modelName)) { // le modèle est dans tabbedPane et qu'il n'est pas actuellement
													// sélectionné
				int indexInTabbedPane = getModelPanelIndex(modelName);
				if (tabbedPane.getSelectedIndex() != indexInTabbedPane) {
					ModelPanel modelPanel = (ModelPanel) tabbedPane.getComponentAt(indexInTabbedPane);
					changeModelPanelFocus(modelPanel);
				}
			} else { // le modèle n'est pas dans tabbedPane, récupère le depuis modelPanelList
				ModelPanel newModelPanel = modelPanelList.get(getModelPanelIndex(modelName));
				newModelPanel.initModelForWindow();

				addTab(modelName, newModelPanel);
				changeModelPanelFocus(newModelPanel);
			}
		} else { // si on n'a jamais ouvert le modèle, crée le avec une ModelPanel associé
			newFigureModel = new FigureModel(newModelPath, false);
			Dimension modelPanelDim = new Dimension(frameDim.width - leftPanelWidth - (separatorWidth / 2),
					frameDim.height - tabHeight);
			ModelPanel newModelPanel = new ModelPanel(newFigureModel, modelPanelDim, false, false, true);
			newModelPanel.initModelForWindow();
			modelPanelList.add(newModelPanel);

			addTab(modelName, newModelPanel);
			changeModelPanelFocus(newModelPanel);

		}
	}

	/**
	 * Change le focus de tabbedPane à newModelPanel et met à jour modelInfo
	 * 
	 * @param newModelPanel
	 */
	private void changeModelPanelFocus(ModelPanel newModelPanel) {
		String title = newModelPanel.getName().toString();
		leftPanel.setNewModelInfo(title);
		leftPanel.setModelInfoBorderTitle(title);
		tabbedPane.setSelectedComponent(newModelPanel);
	}

	/**
	 * Ajoute un onglet à tabbedPane avec un {@link ButtonTabComponent} pour le quitter.
	 * 
	 * @param title
	 * @param panel
	 */
	private void addTab(String title, JPanel panel) {
		tabbedPane.addTab(title, panel);
		tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new ButtonTabComponent(tabbedPane));
		panel.setName(title.toLowerCase());
	}

	/**
	 * @param modelName
	 * @return l'index du ModelPanel correspondant à modelName dans modelPanelList
	 */
	private int getModelPanelIndex(String modelName) {
		for (int i = 0; i < modelPanelList.size(); i++) {
			if (modelPanelList.get(i).getName().equalsIgnoreCase(modelName)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param modelName
	 * @return true si le FigureModel de nom modelName est dans tabbedPane
	 */
	private boolean isModelInTabbedPane(String modelName) {
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			if (tabbedPane.getComponentAt(i) instanceof ModelPanel) {
				if (tabbedPane.getComponentAt(i).getName().equalsIgnoreCase(modelName)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param modelName
	 * @return true si le FigureModel de nom modelName a déja été affiché.
	 */
	private boolean modelWasDisplayed(String modelName) {
		for (int i = 0; i < modelPanelList.size(); i++) {
			if (modelPanelList.get(i).getName().equalsIgnoreCase(modelName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Crée le JMenuBar
	 */
	private void setupMenu() {
		JMenu menu, controls;
		JMenuItem menuItem;
		MenuControler menuListener = new MenuControler();

		// Create the menu bar.
		menuBar = new JMenuBar();

		menu = new JMenu("Nothing");
		menuItem = new JMenuItem("More Nothing");
		menu.add(menuItem);
		menuBar.add(menu);

		controls = new JMenu("Aide");
		menuItem = new JMenuItem("Contrôles");
		menuItem.setActionCommand("controles");
		menuItem.addActionListener(menuListener);
		controls.add(menuItem);
		menuItem = new JMenuItem("Credits");
		menuItem.setActionCommand("credits");
		menuItem.addActionListener(menuListener);
		controls.add(menuItem);
		menuBar.add(controls);
	}
}