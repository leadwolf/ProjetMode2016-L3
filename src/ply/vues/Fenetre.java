package ply.vues;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.nio.file.Path;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.Timer;

import ply.controlers.CommandControler;
import ply.controlers.KeyControler;
import ply.controlers.MouseControler;
import ply.controlers.TimerChangeListener;
import ply.modeles.FigureModel;
import ply.modeles.SensitivityModel;

public class Fenetre extends JFrame {

	private static final long serialVersionUID = 2549833609496985257L;

	private FigureModel figureModel;
	
	// PANELS
	private VisualisationPanel visPanel;
	private JPanel mainPanel;
	private JPanel bottomPanel;
	private TranslationPanel translationPanel;
	private OptionPanel optionPanel;
	private RotationPanel rotationPanel;
	private GridBagConstraints gbc = new GridBagConstraints();
	private JMenuBar menuBar;
	// CONTROLERS

	private SensitivityModel sensModel;
	private Dimension dim = new Dimension(800, 800);

	/**
	 * Crée une fenêtre de visualisation du modèle
	 * 
	 * @param drawPoints dessinner les points au départ
	 * @param drawSegments dessiner les segments au départ
	 * @param drawFaces dessiner les faces au départ
	 */
	public Fenetre(FigureModel figureModel, boolean drawPoints, boolean drawSegments, boolean drawFaces) {
		super();

		setupMenu();

		this.figureModel = figureModel;
		
		setupPanels(figureModel, drawPoints, drawSegments, drawFaces);

		/* FENETRE */
		this.setTitle("Modelisationator");
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		setSize(dim);
		add(mainPanel);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setupControlers();
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

		setJMenuBar(menuBar);
	}

	/**
	 * Crée les panels composants cette fenêtre
	 * @param figureModel 
	 * @param drawPoints
	 * @param drawSegments
	 * @param drawFaces
	 */
	private void setupPanels(FigureModel figureModel, boolean drawPoints, boolean drawSegments, boolean drawFaces) {

		Dimension buttonDim = new Dimension(50, 50);
		Dimension buttonPanelDim = new Dimension(buttonDim.width * 3, buttonDim.height * 3);
		int extraBottomPanelHeight = 100;

		/* PANNEAU AFFICHAGE */
		visPanel = new VisualisationPanel(figureModel, drawPoints, drawSegments, drawFaces);
		visPanel.setTempDimensions(new Dimension(784, 490));
		visPanel.setPreferredSize(new Dimension(dim.width, dim.height - buttonPanelDim.height - extraBottomPanelHeight));

		/* PANNEAUX BOUTONS */
		translationPanel = new TranslationPanel(buttonPanelDim, buttonDim);
		translationPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Translater le modèle"));
		rotationPanel = new RotationPanel(buttonPanelDim, buttonDim);
		rotationPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Tourner le modèle"));

		/* OPTIONS */
		optionPanel = new OptionPanel();
		optionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Options d'affichage"));

		/* PANNEAU DU BAS */
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridBagLayout());
		gbc.insets = new Insets(10, 10, 10, 10);
		bottomPanel.add(translationPanel, gbc);
		bottomPanel.add(rotationPanel, gbc);
		bottomPanel.add(optionPanel, gbc);
		bottomPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		bottomPanel.setBackground(Color.WHITE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		bottomPanel.setPreferredSize(new Dimension(dim.width, buttonPanelDim.height + extraBottomPanelHeight));
		bottomPanel.setMaximumSize(new Dimension((int) screenSize.getWidth(), buttonPanelDim.height + extraBottomPanelHeight));

		/* PANNEAU PRINCIPAL */
		mainPanel = new JPanel();
		mainPanel.setPreferredSize(dim);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(visPanel);
		mainPanel.add(bottomPanel);
	}

	/**
	 * Initialise les contrôleurs des souris, clavier, boutons...
	 */
	private void setupControlers() {

		/* CONTROLEURS */
		sensModel = new SensitivityModel();
		CommandControler commandControler = new CommandControler(this);
		MouseControler mouseControler = new MouseControler(visPanel, sensModel);
		KeyControler keyDispatcher = new KeyControler(this);

		SensitivityViewPanel sensPanel = new SensitivityViewPanel(sensModel);
		sensPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Ajuster la sensitivité de la souris : "));
		bottomPanel.add(sensPanel, gbc);

		// TIMER pour rester appuyé sur le bouton
		TimerChangeListener timerChangeListener = new TimerChangeListener();
		Timer timer = new Timer(50, commandControler);
		timerChangeListener.setTimer(timer);
		translationPanel.addButtonChangeListeners(timerChangeListener);
		rotationPanel.addButtonChangeListeners(timerChangeListener);

		rotationPanel.addActionRotat(commandControler);
		translationPanel.addActionTranslat(commandControler);

		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(keyDispatcher);

		// SOURIS
		visPanel.addMouseWheelListener(mouseControler);
		visPanel.addMouseListener(mouseControler);
		visPanel.addMouseMotionListener(mouseControler);

		optionPanel.getDirectionalLight().addActionListener(commandControler);
		optionPanel.getshowFaces().addActionListener(commandControler);
		optionPanel.getshowSegments().addActionListener(commandControler);
		optionPanel.getshowPoints().addActionListener(commandControler);
	}

	/**
	 * Appelle {@link FigureModel#resetModel()} pour réinitialiser le modèle à ses valeurs d'origine i.e. le fichier .ply.
	 */
	public void resetModel() {
		figureModel.resetModel();
		initModelForWindow();
	}
	
	/**
	 * Appelle {@link FigureModel#refreshModel()} pour rafraichir les données et appelle {@link FigureModel#notifyObservers()}.
	 */
	public void initModelForWindow() {
		figureModel.prepareForWindow(visPanel, 1.0);
		repaint();
	}
	
	public FigureModel getFigure() {
		return this.figureModel;
	}

	/**
	 * @return the visPanel
	 */
	public VisualisationPanel getVisPanel() {
		return visPanel;
	}

	/**
	 * @return the optionPanel
	 */
	public OptionPanel getOptionPanel() {
		return optionPanel;
	}

	/**
	 * @return the sensModel
	 */
	public SensitivityModel getSensModel() {
		return sensModel;
	}
	

}