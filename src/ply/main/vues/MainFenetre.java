package ply.main.vues;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ply.bdd.base.DAO;
import ply.bdd.strategy.BDDPanelStrategy;
import ply.bdd.strategy.DataBaseStrategy;
import ply.bdd.vues.BDDPanel;
import ply.bdd.vues.ModelBrowser;
import ply.bdd.vues.ModelInfo;
import ply.main.Modelisationator;
import ply.plyModel.controlers.KeyControler;
import ply.plyModel.modeles.FigureModelNew;
import ply.read.reader.AsciiReader;
import ply.read.reader.Reader;
import ply.result.BasicResult;
import ply.result.MethodResult;

/**
 * Ceci est la JFrame que lance l'application. Elle comporte un JSplitPane avec à gauche un {@link ModelInfo} et {@link ModelBrowser} et à droite des onglets de
 * {@link BDDPanel} et {@link ModelPanel}.
 * 
 * @author L3
 *
 */
public class MainFenetre extends JFrame {

	/**
	 * ID générée.
	 */
	private static final long serialVersionUID = 8476676570899803066L;
	/**
	 * Le Path vers le dossier contenant les modèles. Soit le dossier contenant le modèle précisé dans le premier constructeur ou le dossier "data/".
	 */
	private Path parentPath;
	/**
	 * Soit tous les fichiers .ply dans le meme dossier que le modele avec lequel on a lancé l'appli soit le dossier data/.
	 */
	private static List<File> allFiles;
	/**
	 * Sauvegarde tous les ModelPanel qu'on affichera. Utilisé pour charger rapidement un modèle qu'on a quitté auparavant.
	 */
	private List<ModelPanel> modelPanelList;
	/**
	 * Le panel principal qui compose ce JFrame.
	 */
	private JSplitPane mainSplitPanel;
	/**
	 * Le JTabbedPane qui contiendra les onglets de modeles.
	 */
	private JTabbedPane modelTabbedPane;
	/**
	 * Le JTabbedPane qui contiendra les onglets Base et Modeles.
	 */
	private JTabbedPane mainTabbedPane;
	/**
	 * Le JPanel de gauche, dans lequel les informations doivent êtres mises à jour par rapport à l'intéraction utilisateur.
	 */
	private LeftSidePanel leftPanel;

	/**
	 * La notice du bas du fenêtre.
	 */
	private JLabel toolLabel;

	/**
	 * La barre de menu de cette JFrame.
	 */
	private JMenuBar menuBar;
	/**
	 * Dimension de ce JFrame. Utilisé car tous les JPanels ont des tailles relatifs à ceci à leurs constructions.
	 */
	private Dimension frameDim;
	/**
	 * La dimension du panel de gauche dans mainSplitPanel, donne aussi celle de droite par soustraction.
	 */
	private Dimension leftPanelDim;
	/**
	 * Largeur du séparateur. Utilisé pour calculer les tailles des panels qu'on ajoute par la suite.
	 */
	private int dividerWidth = 8;
	/**
	 * Hauteur des tabs. Utilisé pour calculer les tailles des panels qu'on ajoute par la suite.
	 */
	private int tabHeight = 23;
	private DataBaseStrategy strategy;

	private boolean canControlFigure;

	/**
	 * Crée la fenêtre de l'application. Un JSplitPane contenant à gauche {@link ModelInfo} et {@link ModelBrowser} et à droite, un {@link JTabbedPane}
	 * contenant un {@link ModelPanel} du FigureModel précisé en paramètre et un {@link BDDPanel}.
	 * 
	 * @param figureModel le modèle avec lequel créer le ModelPanel.
	 * @param options [0] &gt; drawPoints, [1] &gt; drawSegments, [2] &gt; drawFaces, [3] &gt; resetBase, [4] &gt; fillBase,
	 */
	public MainFenetre(FigureModelNew figureModel, boolean[] options) {
		super();
		this.parentPath = figureModel.getPath().getParent();
		frameDim = new Dimension(1250, 800);
		firstSetup();
		int leftPanelWidth = 350;

		/* ModelPanel */
		Dimension modelPanelDim = new Dimension(frameDim.width - leftPanelWidth - (dividerWidth / 2), frameDim.height - tabHeight);
		ModelPanel modelPanel = new ModelPanel(figureModel, modelPanelDim, options[0], options[1], options[2]);
		modelPanel.initModelForWindow();
		modelPanelList.add(modelPanel);

		/* BDD PANEL */
		// par défaut on veut afficher toute la base
		BDDPanel bddPanel = strategy.treatArguments(new String[] { "--all" }, null, new boolean[] { options[3], options[4], false })
				.getPanelResult();
		if (bddPanel == null) {
			System.exit(1);
		}
		bddPanel.setMainFenetre(this);

		/* LEFT PANEL */
		String modelName = figureModel.getPath().getFileName().toString();
		modelName = modelName.substring(0, modelName.lastIndexOf(".")); // capitalize first letter
		createLeftPanel(modelName);
		modelName = modelName.substring(0, 1).toUpperCase() + modelName.substring(1);

		/* MODEL TABBED PANE */
		modelTabbedPane = new JTabbedPane();
		addModelTab(modelName, modelPanel);
		addTabChangeListener();

		/* MAIN TABBED PANE */
		mainTabbedPane = new JTabbedPane();
		mainTabbedPane.addTab("Base", bddPanel);
		mainTabbedPane.addTab("Modèles", modelTabbedPane);
		mainTabbedPane.setSelectedComponent(modelTabbedPane);

		mainTabbedPane.setMinimumSize(new Dimension((int) (frameDim.width * 0.6), frameDim.height));
		mainTabbedPane.setMaximumSize(new Dimension((int) (frameDim.width * 0.9), frameDim.height));

		/* MAIN PANEL */
		mainSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, mainTabbedPane);
		mainSplitPanel.setDividerLocation(0.25);
		mainSplitPanel.setDividerSize(dividerWidth);

		/* FENETRE */
		setupFenetre(modelName);
	}

	/**
	 * Crée la fenêtre de l'application. Un JSplitPane contenant à gauche {@link ModelInfo} et {@link ModelBrowser} et à droite, un {@link JTabbedPane}
	 * contenant {@link BDDPanel} crée avec la commande précisée.
	 * 
	 * @param command la commande bdd avec laqelle on a lancé le programme et qu'on passe à BDDPanel.
	 * @param options [0] &gt; resetBase, [1] &gt; fillBase
	 */
	public MainFenetre(String[] command, boolean[] options) {
		super();
		this.parentPath = Paths.get("data/");
		frameDim = new Dimension(800, 500);
		firstSetup();

		/* BDD PANEL */
		BDDPanel bddPanel = strategy.treatArguments(command, null, new boolean[] { options[0], options[1], false }).getPanelResult();
		if (bddPanel == null) {
			System.exit(1);
		}
		bddPanel.setMainFenetre(this);

		/* LEFT PANEL */
		createLeftPanel(null);

		/* MODEL TABBED PANE */
		modelTabbedPane = new JTabbedPane();

		/* MAIN TABBED PANE */
		mainTabbedPane = new JTabbedPane();
		mainTabbedPane.addTab("Base", bddPanel);
		mainTabbedPane.addTab("Modèles", modelTabbedPane);
		mainTabbedPane.setSelectedComponent(bddPanel);

		mainTabbedPane.setMinimumSize(new Dimension((int) (frameDim.width * 0.6), frameDim.height));
		mainTabbedPane.setMaximumSize(new Dimension((int) (frameDim.width * 0.9), frameDim.height));

		/* MAIN PANEL */
		mainSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, mainTabbedPane);
		mainSplitPanel.setDividerLocation(0.25);
		mainSplitPanel.setDividerSize(dividerWidth);

		/* FENETRE */
		setupFenetre("Base");
	}

	/**
	 * Crée JMenuBar et initialise les variables.
	 */
	private void firstSetup() {
		setupMenu();
		setJMenuBar(menuBar);
		leftPanelDim = new Dimension((int) (frameDim.width * 0.28), frameDim.height);
		strategy = new BDDPanelStrategy();

		allFiles = new ArrayList<>();
		File[] tempFiles = parentPath.toFile().listFiles(new FilenameFilter() {
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
		Collections.addAll(allFiles, tempFiles);
		Collections.sort(allFiles);
		modelPanelList = new ArrayList<>();
		canControlFigure = true;
	}

	/**
	 * @param title
	 * 
	 */
	private void setupFenetre(String title) {
		changeTitle(title);
		setLayout(new BorderLayout());
		setSize(frameDim);

		add(mainSplitPanel);
		createToolTip();
		pack();
		// setVisible(true); // dont set visible unless need to (bc of tests).
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyControler(this));

		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// focus on tabbedPane to prevent the searchBar from being cleared (when in focus, the tip is removed)
		mainTabbedPane.requestFocusInWindow();
	}

	/**
	 * @param modelName le nom du premier modèle affiché. Sert à initialiser {@link ModelInfo}. Laisser null si on affiche la bdd en premier.
	 */
	private void createLeftPanel(String modelName) {
		leftPanel = new LeftSidePanel(modelName, leftPanelDim, this);
		leftPanel.setPreferredSize(leftPanelDim);
		leftPanel.setMaximumSize(leftPanelDim);
		leftPanel.setMinimumSize(new Dimension(0, frameDim.height));
	}

	/**
	 * Crée tabbedPane et ajoute un {@link ChangeListener}.
	 */
	private void addTabChangeListener() {
		modelTabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (modelTabbedPane.getSelectedComponent() != null) {
					if (modelTabbedPane.getSelectedComponent() instanceof ModelPanel) {
						ModelPanel newFocus = (ModelPanel) modelTabbedPane.getSelectedComponent();
						changeModelPanelFocus(newFocus);
					}
					changeTitle(modelTabbedPane.getSelectedComponent().getName());
				}
			}
		});
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
	 * Ajoute un ModelPanel à modelTabbedPane quand on double clique sur un nom de modèle dans ModelBrowser. Elle prend le path associé au nom dans la base.
	 * 
	 * @param modelName le nom du modèle à ajouter SANS EXTENSION .PLY
	 */
	public void addNewModel(String modelName) {
		// on a déja affiché le modèle associé à newPath. Il est dans modelPanelList.
		if (modelWasDisplayed(modelName)) {
			// le modèle est dans tabbedPane et qu'il n'est pas actuellement sélectionné
			if (isModelInTabbedPane(modelName) && modelTabbedPane.getSelectedIndex() != getIndexInTabbedPane(modelName)) {
				int indexInTabbedPane = getIndexInTabbedPane(modelName);
				ModelPanel modelPanel = (ModelPanel) modelTabbedPane.getComponentAt(indexInTabbedPane);

				// SET VIEW
				changeModelPanelFocus(modelPanel);
			} else {
				// le modèle n'est pas dans tabbedPane, récupère le depuis modelPanelList
				ModelPanel newModelPanel = modelPanelList.get(getModelPanelIndex(modelName));
				newModelPanel.initModelForWindow();

				// ADD AND SET VIEW
				addModelTab(modelName, newModelPanel);
				changeModelPanelFocus(newModelPanel);
			}
		} else { // si on n'a jamais ouvert le modèle, crée le avec une ModelPanel associé

			// GET
			Path newModelPath = DAO.INSTANCE.getPathByName(modelName.toLowerCase());
			MethodResult result = new BasicResult(null);
			Reader asciiReader = null;
			try {
				asciiReader = new AsciiReader(newModelPath, result);
			} catch (IOException e) {
				String message = "Modelisationator encountered an error while reading the .ply file.\nError : " + e.getMessage() + "\n"
						+ "Error code : " + result.getCode();
				JOptionPane.showMessageDialog(null, message, "Modelisationator", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				// System.exit(1);
			}
			FigureModelNew newFigureModel = new FigureModelNew(asciiReader);
			Dimension modelPanelDim = new Dimension(frameDim.width - leftPanelDim.width - (dividerWidth / 2), frameDim.height - tabHeight);
			ModelPanel newModelPanel = new ModelPanel(newFigureModel, modelPanelDim, false, false, true);
			newModelPanel.setName(modelName);
			newModelPanel.initModelForWindow();
			modelPanelList.add(newModelPanel);

			// ADD AND SET VIEW
			addModelTab(modelName, newModelPanel);
			changeModelPanelFocus(newModelPanel);

		}
	}

	private void changeTitle(String modelName) {
		String newTitle = Modelisationator.NAME + " - " + modelName;
		setTitle(newTitle);
	}

	/**
	 * Change le focus de tabbedPane à newModelPanel, met à jour modelInfo et le titre de la fenêtre.
	 * 
	 * @param newModelPanel
	 */
	private void changeModelPanelFocus(ModelPanel newModelPanel) {
		String title = newModelPanel.getName().toString();
		leftPanel.setNewModelInfo(title);
		leftPanel.setModelInfoBorderTitle(title);
		mainTabbedPane.setSelectedComponent(modelTabbedPane);
		modelTabbedPane.setSelectedComponent(newModelPanel);
	}

	/**
	 * Ajoute un onglet à modelTabbedPane avec un {@link ButtonTabComponent} pour le quitter.
	 * 
	 * @param title
	 * @param panel
	 */
	private void addModelTab(String title, JPanel panel) {
		modelTabbedPane.addTab(title, panel);
		modelTabbedPane.setTabComponentAt(modelTabbedPane.getTabCount() - 1, new ButtonTabComponent(modelTabbedPane));
		panel.setName(title);
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
	 * @param name
	 * @return l'index du Panel ayant comme nom name dans tabbedPane
	 */
	private int getIndexInTabbedPane(String name) {
		for (int i = 0; i < modelTabbedPane.getTabCount(); i++) {
			if (modelTabbedPane.getComponentAt(i).getName().equalsIgnoreCase(name)) {
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
		for (int i = 0; i < modelTabbedPane.getTabCount(); i++) {
			if (modelTabbedPane.getComponentAt(i) instanceof ModelPanel) {
				if (modelTabbedPane.getComponentAt(i).getName().equalsIgnoreCase(modelName)) {
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
	 * Affiche/Cache les contrôles dans le {@link ModelPanel} courant de modelTabbedPane.
	 */
	public void toggleControls() {
		ModelPanel currentPanel = (ModelPanel) modelTabbedPane.getSelectedComponent();
		currentPanel.toggleControls();
	}

	/**
	 * Affiche/Cache le menu gauche.
	 */
	public void toggleLeftPanel() {
		boolean extended = mainSplitPanel.getDividerLocation() >= mainSplitPanel.getMinimumDividerLocation() + 20;
		if (extended) {
			mainSplitPanel.setDividerLocation(0.0);
		} else {
			mainSplitPanel.setDividerLocation(0.25);
		}
		extended = !extended;
	}

	/**
	 * Crée le JMenuBar
	 */
	private void setupMenu() {
		JMenu menu;
		JMenuItem menuItem;
		MenuControler menuListener = new MenuControler();

		// Create the menu bar.
		menuBar = new JMenuBar();

		// /* NOTHING */
		// menu = new JMenu("Nothing");
		// menuItem = new JMenuItem("More Nothing");
		// menu.add(menuItem);
		// menuBar.add(menu);

		/* FENETRE */
		menu = new JMenu("Fenêtre");

		menuItem = new JMenuItem("Afficher/Cacher le menu gauche");
		menuItem.setActionCommand("toggle left panel");
		menuItem.addActionListener(menuListener);
		menu.add(menuItem);
		menuBar.add(menu);

		menuItem = new JMenuItem("Afficher/Cacher les contrôles de la figure");
		menuItem.setActionCommand("toggle commands");
		menuItem.addActionListener(menuListener);
		menu.add(menuItem);
		menuBar.add(menu);

		/* AIDE */
		menu = new JMenu("Aide");
		menuItem = new JMenuItem("Contrôles");
		menuItem.setActionCommand("controles");
		menuItem.addActionListener(menuListener);
		menu.add(menuItem);
		menuItem = new JMenuItem("Credits");
		menuItem.setActionCommand("credits");
		menuItem.addActionListener(menuListener);
		menu.add(menuItem);
		menuBar.add(menu);
	}

	/**
	 * Contrôleur du menu.
	 * 
	 * @author L3
	 *
	 */
	private class MenuControler implements ActionListener {

		private Controls controls;
		private Credits credits;

		@SuppressWarnings("javadoc")
		public MenuControler() {
			controls = new Controls();
			credits = new Credits();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "toggle commands":
				toggleControls();
				break;
			case "toggle left panel":
				toggleLeftPanel();
				break;
			case "controles":
				controls.setVisible(true);
				break;
			case "credits":
				credits.setVisible(true);
				break;
			default:
				break;
			}
		}
	}

	public ModelPanel getCurrentModelPanel() {
		if (modelTabbedPane.getComponentCount() > 0) {
			return (ModelPanel) modelTabbedPane.getSelectedComponent();
		}
		return null;
	}

	/**
	 * @return the controlFigure
	 */
	public boolean canControlFigure() {
		return canControlFigure;
	}

	/**
	 * Set la valeur de la possibilité de contrôle de la figure au lieu d'écriture. Si on lui passe <b>true</b> on force le focus au dernier {@link ModelPanel}.
	 * 
	 * @param controlFigure the controlFigure to set
	 */
	public void setCanControlFigure(boolean controlFigure) {
		this.canControlFigure = controlFigure;
		if (controlFigure) {
			modelTabbedPane.getSelectedComponent().requestFocus();
		}
	}
}