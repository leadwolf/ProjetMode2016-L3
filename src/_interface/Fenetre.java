package _interface;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import modele.Figure;

public class Fenetre extends JFrame {
	
	private static final long serialVersionUID = 2549833609496985257L;
	VisualisationPanel visPanel;
	JPanel mainPanel;
	JPanel bottomPanel;
	TranslationPanel translationPanel;
	RotationPanel rotationPanel;
	
	public Fenetre(boolean drawPoints, boolean drawSegments, boolean drawFaces) {
		super();

		Dimension dim = new Dimension(800,800);
		Dimension buttonDim = new Dimension(30, 30);
		Dimension buttonPanelDim = new Dimension(buttonDim.width*3, buttonDim.height*3);
		int borderHeight = 70;
		
		/* PANNEAU AFFICHAGE */
		visPanel = new VisualisationPanel(drawPoints, drawSegments, drawFaces);
		visPanel.setTempDimensions(new Dimension(dim.width, dim.height-buttonPanelDim.height-borderHeight));
		visPanel.setPreferredSize(new Dimension(dim.width, dim.height-buttonPanelDim.height-borderHeight));
		
		/* PANNEAUX BOUTONS */
		translationPanel = new TranslationPanel(buttonPanelDim, buttonDim);
		rotationPanel = new RotationPanel(buttonPanelDim, buttonDim);
		
		/* PANNEAU DU BAS */
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
		bottomPanel.setPreferredSize(new Dimension(dim.width, buttonPanelDim.height+30));
		gbc.insets = new Insets(10, 10, 10, 10);
		bottomPanel.add(translationPanel, gbc);
		bottomPanel.add(rotationPanel, gbc);
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
	}
		
	public void setFigure(Figure figure, double zoom) {
		visPanel.setFigure(figure, zoom);
	}
	
}