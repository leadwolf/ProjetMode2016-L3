package main.vues;

import java.awt.Dimension;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import main.BaseDeDonneesNew;
import ply.bdd.controlers.JListControler;
import ply.plyModel.modeles.FigureModel;

public class MainFenetre extends JFrame {

	private Path parentPath;
	private List<ModelPanel> modelPanelList;
	private JTabbedPane tabbedPane;

	private JMenuBar menuBar;
	private Dimension frameDim;
	private int leftPanelWidth = 350;
	private int separatorWidth = 30;
	private int tabHeight = 23;

	/**
	 * Create the main Frame with default view being the given in constructor. We must manually create the BDDPanel from scratch
	 * 
	 * @param figureModel
	 * @param drawPoints
	 * @param drawSegments
	 * @param drawFaces
	 */
	public MainFenetre(FigureModel figureModel, boolean drawPoints, boolean drawSegments, boolean drawFaces) {
		super();
		firstSetup();

		frameDim = new Dimension(1200, 800);

		/* ModelPanel */
		Dimension modelPanelDim = new Dimension(frameDim.width - leftPanelWidth - (separatorWidth / 2), frameDim.height - tabHeight);
		ModelPanel modelPanel = new ModelPanel(figureModel, modelPanelDim, drawPoints, drawSegments, drawFaces);
		modelPanel.initModelForWindow();
		modelPanelList.add(modelPanel);

		/* BDD PANEL */
		// par défaut on veut afficher toute la base
		BDDPanel bddPanel = BaseDeDonneesNew.getPanel(new String[] { "--all" }, false, false, false, null);

		/* LEFT PANEL */
		Dimension leftPanelDim = new Dimension(leftPanelWidth - (separatorWidth / 2), frameDim.height);
		String modelName = figureModel.getPath().toAbsolutePath().getFileName().toString();
		modelName = modelName.substring(0, modelName.lastIndexOf("."));
		this.parentPath = figureModel.getPath().getParent();
		LeftSidePanel leftPanel = new LeftSidePanel(parentPath, modelName, leftPanelDim);
		leftPanel.setPreferredSize(leftPanelDim);
		// leftPanel.setMinimumSize(leftPanelDim);
		leftPanel.setMinimumSize(new Dimension(10, leftPanelDim.height));
		leftPanel.addMouseListenerToList(new JListControler(this));

		/* TABBED PANE */
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(modelName.substring(0, 1).toUpperCase() + modelName.substring(1), modelPanel);
		tabbedPane.addTab("Base", bddPanel);

		/* MAIN PANEL */
		JSplitPane mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, tabbedPane);

		/* FENETRE */
		this.setTitle("Modelisationator");
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		setSize(frameDim);

		add(mainPanel);
		pack();
		setVisible(true);

		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setJMenuBar(menuBar);
	}

	/**
	 * Create the main Frame showing the result of the db command given in parameter. We don't create a ModelPanel yet
	 * 
	 * @param figureModel
	 * @param drawPoints
	 * @param drawSegments
	 * @param drawFaces
	 */
	public MainFenetre(String[] command) {
		super();
		firstSetup();

		int tabHeight = 23;
		frameDim = new Dimension(800, 500);

		/* BDD PANEL */
		// par défaut on veut afficher toute la base
		BDDPanel bddPanel = BaseDeDonneesNew.getPanel(command, false, false, false, null);
		BaseDeDonneesNew.closeConnection();

		/* LEFT PANEL */
		Dimension leftPanelDim = new Dimension(leftPanelWidth - (separatorWidth / 2), frameDim.height);
		this.parentPath = Paths.get("data/");
		LeftSidePanel leftPanel = new LeftSidePanel(parentPath, null, leftPanelDim);
		leftPanel.setPreferredSize(leftPanelDim);
		// leftPanel.setMinimumSize(leftPanelDim);
		leftPanel.setMinimumSize(new Dimension(10, leftPanelDim.height));
		leftPanel.addMouseListenerToList(new JListControler(this));

		/* TABBED PANE */
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Base", bddPanel);

		/* MAIN PANEL */
		JSplitPane mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, tabbedPane);

		/* FENETRE */
		this.setTitle("Modelisationator");
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		setSize(frameDim);

		add(mainPanel);
		pack();
		setVisible(true);

		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setJMenuBar(menuBar);
	}

	/**
	 * Sets up JMenuBar and initialies vars
	 */
	private void firstSetup() {
		setupMenu();
		modelPanelList = new ArrayList<>();
	}

	/**
	 * Ajoute un ModelPanel à tabbedPane quand on double clique sur un nom de modèle dans ModelBrowser
	 * @param clickIndex the index of the click in the JList
	 */
	public void addNewModel(int clickIndex) {
		File[] allFiles = parentPath.toFile().listFiles(new FilenameFilter() {
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

		Path newModelPath = allFiles[clickIndex].toPath();
		if (!modelAlreadyDisplayed(newModelPath)) {
			FigureModel figureModel = new FigureModel(newModelPath, false);
			Dimension modelPanelDim = new Dimension(frameDim.width - leftPanelWidth - (separatorWidth / 2), frameDim.height - tabHeight);
			ModelPanel modelPanel = new ModelPanel(figureModel, modelPanelDim, false, false, true);
			modelPanel.initModelForWindow();
			modelPanelList.add(modelPanel);
			tabbedPane.addTab(figureModel.getPath().getFileName().toString(), modelPanel);
			tabbedPane.setSelectedComponent(modelPanel);
		}

	}
	
	/**
	 * @param newPath
	 * @return true if the model corresponding to newPath is already being displayed
	 */
	private boolean modelAlreadyDisplayed(Path newPath) {
		boolean isContained = false;
		int i=0;
		while (i<modelPanelList.size() && !isContained) {
			if (modelPanelList.get(i).getFigure().getPath().toAbsolutePath().equals(newPath.toAbsolutePath())) {
				isContained = true;
			}
			i++;
		}
		return isContained;
	}

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
