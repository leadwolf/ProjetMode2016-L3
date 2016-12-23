package main.vues;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Controls extends JFrame {
	
	private Dimension dim = new Dimension(938, 650);

	
	public Controls() {
		Image image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/keyboard-layout.png"));
		JComponent imageComp = new JComponent() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				g.drawImage(image, 0, 0, this);
			}
		};

		imageComp.setPreferredSize(dim);
		imageComp.repaint();
		
		JPanel panel = new JPanel();
		panel.add(imageComp);
		panel.setPreferredSize(dim);
		

		add(panel);
		setLayout(new FlowLayout());
		setSize(dim);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}
	
}
