package _interface;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import math.Calculations;
import modele.Figure;

public class Fenetre extends JFrame {
	
	private static final long serialVersionUID = 2549833609496985257L;
	VisualisationPanel visPanel;
	JPanel mainPanel;
	JPanel bottomPanel;
	TranslationPanel translationPanel;
	OptionPanel optionPanel;
	RotationPanel rotationPanel;
	
	ButtonControler buttonControler;
	MouseControler mouseControler;
	KeyDispatcher keyDispatcher;
	Timer timer;
	private Calculations calcs;
	private Figure figure;
	
	public Fenetre(boolean drawPoints, boolean drawSegments, boolean drawFaces) {
		super();

		Dimension dim = new Dimension(800,800);
		Dimension buttonDim = new Dimension(50, 50);
		Dimension buttonPanelDim = new Dimension(buttonDim.width*3, buttonDim.height*3);
		int borderHeight = 80;
		
				/* PANNEAU AFFICHAGE */
		visPanel = new VisualisationPanel(drawPoints, drawSegments, drawFaces);
		visPanel.setTempDimensions(new Dimension(dim.width, dim.height-buttonPanelDim.height-borderHeight));
		visPanel.setPreferredSize(new Dimension(dim.width, dim.height-buttonPanelDim.height-borderHeight));
		
		/* PANNEAUX BOUTONS */
		translationPanel = new TranslationPanel(buttonPanelDim, buttonDim);
		translationPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Translater le modèle"));
		rotationPanel = new RotationPanel(buttonPanelDim, buttonDim);
		rotationPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Tourner le modèle"));
		optionPanel = new OptionPanel();
		optionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Autres options"));
		
		/* PANNEAU DU BAS */
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
		bottomPanel.setPreferredSize(new Dimension(dim.width, buttonPanelDim.height+30));
		gbc.insets = new Insets(10, 10, 10, 10);
		bottomPanel.add(translationPanel, gbc);
		bottomPanel.add(rotationPanel, gbc);
		bottomPanel.add(optionPanel, gbc);
		bottomPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		bottomPanel.setBackground(Color.WHITE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		bottomPanel.setMaximumSize(new Dimension((int) screenSize.getWidth(), buttonPanelDim.height+30));
		
		/* PANNEAU PRINCIPAL */
		mainPanel = new JPanel();
		mainPanel.setPreferredSize(dim);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(visPanel);
		mainPanel.add(bottomPanel);
		
		
		/* FENETRE */
		this.setTitle("Modelisationator");
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		setSize(dim);
		add(mainPanel);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/* CONTROLEURS */
		buttonControler = new ButtonControler(this);
		mouseControler = new MouseControler(visPanel);
		keyDispatcher = new KeyDispatcher(this);
		
		// TIMER pour rester appuyé sur le bouton
		TimerChangeListener timerChangeListener = new TimerChangeListener();
		timer = new Timer(50, buttonControler);
		timerChangeListener.setTimer(timer);
		timerChangeListener.setPanels(translationPanel, rotationPanel);
		translationPanel.addButtonChangeListeners(timerChangeListener);
		rotationPanel.addButtonChangeListeners(timerChangeListener);
		

		rotationPanel.addActionRotat(buttonControler);
		translationPanel.addActionTranslat(buttonControler);

		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(keyDispatcher);
		
		//SOURIS
		visPanel.addMouseWheelListener(mouseControler);
		visPanel.addMouseListener(mouseControler);
		visPanel.addMouseMotionListener(mouseControler);
		
		optionPanel.getDirectionalLight().addActionListener(buttonControler);
	}
		
	public Figure getFigure() {
		return this.figure;
	}

	public void setFigure(Figure figure, double zoom) {
		this.figure = figure;
		visPanel.setFigure(figure, zoom);
	}

	public OptionPanel getOptionPanel() {
		return optionPanel;
	}

	public VisualisationPanel getVisPanel() {
		return visPanel;
	}
	
	
	
}