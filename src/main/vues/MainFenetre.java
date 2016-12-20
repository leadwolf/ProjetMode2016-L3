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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.BaseDeDonneesNew;
import ply.bdd.controlers.JListControler;
import ply.bdd.vues.ModelInfo;
import ply.plyModel.modeles.FigureModel;

public class MainFenetre extends JFrame {

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
	 * Create the main Frame with default view being the given in constructor. We must manually create the BDDPanel from scratch
	 * 
	 * @param figureModel
	 * @param options [0] &gt; drawPoints, [1] &gt; drawSegments, [2] &gt; drawFaces, [3] &gt; resetBase, [4] &gt; fillBase,
	 */
	public MainFenetre(FigureModel figureModel, boolean[] options) {
		super();
		this.parentPath = figureModel.getPath().getParent();
		firstSetup();

		frameDim = new Dimension(1200, 800);

		/* ModelPanel */
		Dimension modelPanelDim = new Dimension(frameDim.width - leftPanelWidth - (separatorWidth / 2), frameDim.height - tabHeight);
		ModelPanel modelPanel = new ModelPanel(figureModel, modelPanelDim, options[0], options[1], options[2]);
		modelPanel.initModelForWindow();
		modelPanelList.add(modelPanel);

		/* BDD PANEL */
		// par défaut on veut afficher toute la base
		BDDPanel bddPanel = BaseDeDonneesNew.getPanel(new String[] { "--all" }, options[3], options[4], false, null);
		if (bddPanel == null) {
			System.exit(1);
		}
		BaseDeDonneesNew.closeConnection(); // ferme la connection pour qu'on puisse créer une nouvelle connection pour ModelInfo

		/* LEFT PANEL */
		String modelName = figureModel.getPath().getFileName().toString();
		modelName = modelName.substring(0, modelName.lastIndexOf(".")); // capitalize first letter
		createLeftPanel(modelName);

		/* TABBED PANE */
		tabbedPane = new JTabbedPane();
		addTab(modelName.substring(0, 1).toUpperCase() + modelName.substring(1), modelPanel);
		addTab("Base", bddPanel);
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (tabbedPane.getSelectedComponent() instanceof ModelPanel) {
					ModelPanel newFocus = (ModelPanel) tabbedPane.getSelectedComponent();
					changeModelPanelFocus(newFocus);
				}
			}
		});

		/* MAIN PANEL */
		mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, tabbedPane);

		/* FENETRE */
		setupFenetre();

		setJMenuBar(menuBar);
	}

	/**
	 * Create the main Frame showing the result of the db command given in parameter
	 * 
	 * @param command la commande bdd avec laqelle on a lancé le programme
	 * @param options [0] &gt; resetBase, [1] &gt; fillBase
	 */
	public MainFenetre(String[] command, boolean[] options) {
		super();
		this.parentPath = Paths.get("data/");
		firstSetup();

		frameDim = new Dimension(800, 500);

		/* BDD PANEL */
		BDDPanel bddPanel = BaseDeDonneesNew.getPanel(command, options[0], options[1], false, null);
		if (bddPanel == null) {
			System.exit(1);
		}
		BaseDeDonneesNew.closeConnection(); // ferme la connection pour qu'on puisse créer une nouvelle connection pour ModelInfo

		/* LEFT PANEL */
		createLeftPanel(null);

		/* TABBED PANE */
		tabbedPane = new JTabbedPane();
		addTab("Base", bddPanel);

		/* MAIN PANEL */
		mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, tabbedPane);

		/* FENETRE */
		setupFenetre();

		setJMenuBar(menuBar);
	}

	/**
	 * Crée JMenuBar et initliase les variables.
	 */
	private void firstSetup() {
		setupMenu();
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
	 * 
	 */
	private void setupFenetre() {
		this.setTitle("Modelisationator");
		setLayout(new BorderLayout());
		setSize(frameDim);

		add(mainPanel);
		createToolTip();
		pack();
		setVisible(true);

		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void createToolTip() {
		JPanel toolPanel = new JPanel();
		toolLabel = new JLabel(
				"Bienvenue à Modelisationator. Double cliquez sur un modèle dans le \"Browser\" pour l'ouvrir dans un nouvel onglet. Maintenez \"SHIFT\" pour des transformations plus précises.");
		toolPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		toolPanel.add(toolLabel);
		add(toolPanel, BorderLayout.PAGE_END);
	}

	private void updateToolTip(String newTip) {
		toolLabel.setText(newTip);
	}

	/**
	 * @param modelName le nom du premier modèle affiché. Sert à initialiser {@link ModelInfo}. Laisser null si on affiche la bdd en premier.
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
	 * @param clickIndex the index of the click in the JList
	 */
	public void addNewModel(int clickIndex) {
		Path newModelPath = allFiles[clickIndex].toPath();
		FigureModel newFigureModel;

		if (!modelWasDisplayed(newModelPath)) { // si on n'a jamais ouvert le modèle, crée le avec une ModelPanel associé
			newFigureModel = new FigureModel(newModelPath, false);
			Dimension modelPanelDim = new Dimension(frameDim.width - leftPanelWidth - (separatorWidth / 2), frameDim.height - tabHeight);
			ModelPanel newModelPanel = new ModelPanel(newFigureModel, modelPanelDim, false, false, true);
			newModelPanel.initModelForWindow();
			modelPanelList.add(newModelPanel);

			addTab(newModelPath, newModelPanel);
		} else {
			// si on l'a déja ouvert et qu'il n'est pas en train d'être affiché, réajouter le ModelPanel associé à tabbedPane
			if (!modelCurrentlyDisplayed(newModelPath)) {
				ModelPanel newModelPanel = modelPanelList.get(getModelPanelIndex(newModelPath));
				newFigureModel = newModelPanel.getFigure();
				newModelPanel.initModelForWindow();
				
				addTab(newModelPath, newModelPanel);
			} else {
				JOptionPane.showMessageDialog(null, "Ce modèle est déja affiché.");
			}
		}

	}

	private void changeModelPanelFocus(JPanel newModelPanel) {
		String title = newModelPanel.getName().toString();
		leftPanel.setNewModelInfo(title);
		leftPanel.setModelInfoBorderTitle(title);
		tabbedPane.setSelectedComponent(newModelPanel);
	}

	/**
	 * Ajoute un onglet à tabbedPane avec un {@link ButtonTabComponent} pour le quitter.
	 * 
	 * @param index
	 * @param title
	 * @param panel
	 */
	private void addTab(Path newModelPath, JPanel panel) {
		String modelName = newModelPath.getFileName().toString();
		modelName = modelName.substring(0, 1).toUpperCase() + modelName.substring(1, modelName.lastIndexOf("."));
		tabbedPane.addTab(modelName, panel);
		tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new ButtonTabComponent(tabbedPane));
		panel.setName(modelName);
		changeModelPanelFocus(panel);
	}
	
	private void addTab(String title, JPanel panel) {
		tabbedPane.addTab(title, panel);
		tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new ButtonTabComponent(tabbedPane));
		panel.setName(title);
	}

	/**
	 * @param newModelPath
	 * @return l'index du ModelPanel correspondant à newModelPath dans modelPanelList
	 */
	private int getModelPanelIndex(Path newModelPath) {
		for (int i = 0; i < modelPanelList.size(); i++) {
			if (modelPanelList.get(i).getFigure().getPath().toAbsolutePath().equals(newModelPath.toAbsolutePath())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param newModelPath
	 * @return true si le model associé à newModelPath a déja une ModelPanel associé dans tabbedPane
	 */
	private boolean modelCurrentlyDisplayed(Path newModelPath) {
		boolean isBeingDisplayed = false;
		int i = 0;
		while (i < tabbedPane.getTabCount() && !isBeingDisplayed) {
			if (tabbedPane.getComponentAt(i) instanceof ModelPanel) {
				ModelPanel modelPanel = (ModelPanel) tabbedPane.getComponentAt(i);
				if (modelPanel.getFigure().getPath().toAbsolutePath().equals(newModelPath.toAbsolutePath())) {
					isBeingDisplayed = true;
				}
			}
			i++;
		}
		return isBeingDisplayed;
	}

	/**
	 * @param newModelPath
	 * @return true if the model corresponding to newPath is already being displayed
	 */
	private boolean modelWasDisplayed(Path newModelPath) {
		boolean isContained = false;
		int i = 0;
		while (i < modelPanelList.size() && !isContained) {
			if (modelPanelList.get(i).getFigure().getPath().toAbsolutePath().equals(newModelPath.toAbsolutePath())) {
				isContained = true;
			}
			i++;
		}
		return isContained;
	}

	/**
	 * Crée le JMenuBar
	 */
	private void setupMenu() {
		JMenu menu, controls;
		JMenuItem menuItem;

		// Create the menu bar.
		menuBar = new JMenuBar();

		menu = new JMenu("Nothing");
		menuItem = new JMenuItem("More Nothing");
		menu.add(menuItem);
		menuBar.add(menu);

		controls = new JMenu("Aide");
		menuItem = new JMenuItem("Contrôles");
		menuItem.setActionCommand("controles");
		controls.add(menuItem);
		menuItem = new JMenuItem("Credits");
		menuItem.setActionCommand("credits");
		controls.add(menuItem);
		menuBar.add(controls);
	}
}
