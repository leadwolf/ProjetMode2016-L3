package _interface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import _interface.controlers.ButtonControler;
import _interface.controlers.KeyDispatcher;
import _interface.controlers.MouseControler;
import _interface.controlers.TimerChangeListener;
import _interface.sensitivity.Sensitivity;
import _interface.sensitivity.SensitivityPanel;
import modele.Figure;

public class Fenetre extends JFrame {

	private static final long serialVersionUID = 2549833609496985257L;

	// PANELS
	private VisualisationPanel visPanel;
	private JPanel mainPanel;
	private JPanel bottomPanel;
	private TranslationPanel translationPanel;
	private OptionPanel optionPanel;
	private RotationPanel rotationPanel;
	private SensitivityPanel sensPanel;
	private GridBagConstraints gbc = new GridBagConstraints();
	private JMenuBar menuBar;
	// CONTROLERS
	private ButtonControler buttonControler;
	private MouseControler mouseControler;
	private KeyDispatcher keyDispatcher;
	private Timer timer;

	private Sensitivity sens;
	private Dimension dim = new Dimension(800, 800);

	/**
	 * Crée une fenêtre de visualisation du modèle
	 * 
	 * @param drawPoints dessinner les points au départ
	 * @param drawSegments dessiner les segments au départ
	 * @param drawFaces dessiner les faces au départ
	 */
	public Fenetre(boolean drawPoints, boolean drawSegments, boolean drawFaces) {
		super();

		setupMenu();

		setupPanels(drawPoints, drawSegments, drawFaces);

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
	 * 
	 * @param drawPoints
	 * @param drawSegments
	 * @param drawFaces
	 */
	private void setupPanels(boolean drawPoints, boolean drawSegments, boolean drawFaces) {

		Dimension buttonDim = new Dimension(50, 50);
		Dimension buttonPanelDim = new Dimension(buttonDim.width * 3, buttonDim.height * 3);
		int borderHeight = 80;
		int extraBottomPanelHeight = 100;

		/* PANNEAU AFFICHAGE */
		visPanel = new VisualisationPanel(drawPoints, drawSegments, drawFaces);
		visPanel.setTempDimensions(new Dimension(dim.width, dim.height - buttonPanelDim.height - borderHeight));
		visPanel.setPreferredSize(new Dimension(dim.width, dim.height - buttonPanelDim.height - borderHeight));

		/* PANNEAUX BOUTONS */
		translationPanel = new TranslationPanel(buttonPanelDim, buttonDim);
		translationPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Translater le modèle"));
		rotationPanel = new RotationPanel(buttonPanelDim, buttonDim);
		rotationPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Tourner le modèle"));

		/* OPTIONS */
		optionPanel = new OptionPanel();
		optionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Autres options"));

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
		sens = new Sensitivity();
		buttonControler = new ButtonControler(this);
		mouseControler = new MouseControler(visPanel, sens);
		keyDispatcher = new KeyDispatcher(this);

		sensPanel = new SensitivityPanel(sens);
		sensPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Ajuster la sensitivité de la souris : "));
		bottomPanel.add(sensPanel, gbc);

		// TIMER pour rester appuyé sur le bouton
		TimerChangeListener timerChangeListener = new TimerChangeListener();
		timer = new Timer(50, buttonControler);
		timerChangeListener.setTimer(timer);
		translationPanel.addButtonChangeListeners(timerChangeListener);
		rotationPanel.addButtonChangeListeners(timerChangeListener);

		rotationPanel.addActionRotat(buttonControler);
		translationPanel.addActionTranslat(buttonControler);

		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(keyDispatcher);

		// SOURIS
		visPanel.addMouseWheelListener(mouseControler);
		visPanel.addMouseListener(mouseControler);
		visPanel.addMouseMotionListener(mouseControler);

		optionPanel.getDirectionalLight().addActionListener(buttonControler);
		optionPanel.getshowFaces().addActionListener(buttonControler);
		optionPanel.getshowSegments().addActionListener(buttonControler);
		optionPanel.getshowPoints().addActionListener(buttonControler);
	}

	public Figure getFigure() {
		return visPanel.getFigure();
	}

	public void setFigure(Figure figure, double zoom) {
		visPanel.setFigure(figure, zoom);
	}

	public OptionPanel getOptionPanel() {
		return optionPanel;
	}

	public VisualisationPanel getVisPanel() {
		return visPanel;
	}

}