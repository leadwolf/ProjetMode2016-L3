
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Fenetre extends JFrame {
	
	private static final long serialVersionUID = 2549833609496985257L;
	Panneau panneau;
	JPanel mainPanel;
	JPanel buttonPanel;
	JPanel bottomPanel;
	JButton center;
	JButton left;
	JButton right;
	JButton up;
	JButton down;
	private Dimension dim = new Dimension(800,800);
	private Dimension buttonDim = new Dimension(30, 30);
	private Dimension buttonPanelDim = new Dimension(buttonDim.width*3, buttonDim.height*3);
	
	public Fenetre(boolean drawPoints, boolean drawSegments, boolean drawFaces) {
		
		/* PANNEAU AFFICHAGE */
		panneau = new Panneau(drawPoints, drawSegments, drawFaces);
		this.setTitle("Modelisationator");
		this.setSize(dim);
		panneau.setDimensions(new Dimension(dim.width, dim.height-buttonPanelDim.height));
		panneau.setPreferredSize(new Dimension(dim.width, dim.height-buttonPanelDim.height));
		
		/* PANNEAU BOUTONS */
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(3, 3));
		buttonPanel.setPreferredSize(buttonPanelDim);
		center = new JButton("center");
		left = new JButton("left");
		right = new JButton("right");
		up = new JButton("up");
		down = new JButton("down");
		
		center.setPreferredSize(buttonDim);
		left.setPreferredSize(buttonDim);
		right.setPreferredSize(buttonDim);
		up.setPreferredSize(buttonDim);
		down.setPreferredSize(buttonDim);
		
		buttonPanel.add(Box.createRigidArea(buttonDim));
		buttonPanel.add(up);
		buttonPanel.add(Box.createRigidArea(buttonDim));
		buttonPanel.add(left);
		buttonPanel.add(center);
		buttonPanel.add(right);
		buttonPanel.add(Box.createRigidArea(buttonDim));
		buttonPanel.add(down);
		buttonPanel.add(Box.createRigidArea(buttonDim));
		buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		/* PANNEAU DU BAS */
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		bottomPanel.add(buttonPanel);
		bottomPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		bottomPanel.setBackground(Color.WHITE);
		
		/* PANNEAU PRINCIPAL */
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(panneau);
		mainPanel.add(bottomPanel);
		
		
		/* FENETRE */
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		add(mainPanel);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
		
	public void setFigure(Figure figure, double zoom) {
		panneau.setFigure(figure, zoom);
	}
}