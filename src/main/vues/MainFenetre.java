package main.vues;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import ply.plyModel.modeles.FigureModel;

public class MainFenetre extends JFrame {

	private JMenuBar menuBar;
	private Dimension frameDim = new Dimension(1200, 800);
	private int leftPanelWidth = 400;
	private int separatorWidth = 30;

	public MainFenetre(FigureModel figureModel, boolean drawPoints, boolean drawSegments, boolean drawFaces) {
		super();
		setupMenu();

		/* MainPanel */
		Dimension mainPanelDim = new Dimension(frameDim.width - leftPanelWidth - (separatorWidth / 2), frameDim.height);
		ModelPanel modelPanel = new ModelPanel(figureModel, mainPanelDim, drawPoints, drawSegments, drawFaces);
		modelPanel.initModelForWindow();

		/* SPLIT PANE */
		BDDPanel bddPanel = new BDDPanel();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, bddPanel, modelPanel);
		bddPanel.setPreferredSize(new Dimension(leftPanelWidth - (separatorWidth / 2), frameDim.height));

		/* FENETRE */
		this.setTitle("Modelisationator");
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		setSize(frameDim);

//		add(mainPanel);
		add(splitPane);
		setVisible(true);
		
		add(splitPane);
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
