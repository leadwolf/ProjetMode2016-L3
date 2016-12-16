package main.vues;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import main.BaseDeDonneesOld;
import main.BaseDeDonneesNew;
import ply.plyModel.modeles.FigureModel;

public class MainFenetre extends JFrame {

	private JMenuBar menuBar;
	private Dimension frameDim;
	private int leftPanelWidth = 350;
	private int separatorWidth = 30;

	/**
	 * Create the main Frame with default view being the given in constructor.
	 * We must manually create the BDDPanel from scratch
	 * @param figureModel
	 * @param drawPoints
	 * @param drawSegments
	 * @param drawFaces
	 */
	public MainFenetre(FigureModel figureModel, boolean drawPoints, boolean drawSegments, boolean drawFaces) {
		super();
		setupMenu();

		int tabHeight = 23;
		frameDim = new Dimension(1200, 800);
		
		/* ModelPanel */
		Dimension modelPanelDim = new Dimension(frameDim.width - leftPanelWidth - (separatorWidth / 2), frameDim.height - tabHeight);
		ModelPanel modelPanel = new ModelPanel(figureModel, modelPanelDim, drawPoints, drawSegments, drawFaces);
		modelPanel.initModelForWindow();

		/* BDD PANEL */
		// par défaut on veut afficher toute la base
		BDDPanel bddPanel = BaseDeDonneesNew.getPanel(new String[]{"--all"}, false, false, false, null);
		
		/* LEFT PANEL */
		Dimension leftPanelDim = new Dimension(leftPanelWidth - (separatorWidth / 2), frameDim.height);
		LeftSidePanel leftPanel = new LeftSidePanel(figureModel.getPath().getParent(), leftPanelDim);
		leftPanel.setPreferredSize(leftPanelDim);
//		leftPanel.setMinimumSize(leftPanelDim);
		leftPanel.setMinimumSize(new Dimension(10, leftPanelDim.height));
		
		/* TABBED PANE */
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Visualizer", modelPanel);
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
	 * Create the main Frame showing the result of the db command given in parameter.
	 * We don't create a ModelPanel yet
	 * @param figureModel
	 * @param drawPoints
	 * @param drawSegments
	 * @param drawFaces
	 */
	public MainFenetre(String[] command) {
		super();
		setupMenu();

		int tabHeight = 23;
		frameDim = new Dimension(800, 500);
		
		/* BDD PANEL */
		// par défaut on veut afficher toute la base
		BDDPanel bddPanel = BaseDeDonneesNew.getPanel(command, false, false, false, null);
		
		/* LEFT PANEL */
		Dimension leftPanelDim = new Dimension(leftPanelWidth - (separatorWidth / 2), frameDim.height);
		LeftSidePanel leftPanel = new LeftSidePanel(null, leftPanelDim);
		leftPanel.setPreferredSize(leftPanelDim);
//		leftPanel.setMinimumSize(leftPanelDim);
		leftPanel.setMinimumSize(new Dimension(10, leftPanelDim.height));
		
		/* TABBED PANE */
		JTabbedPane tabbedPane = new JTabbedPane();
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
