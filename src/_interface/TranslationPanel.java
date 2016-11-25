package _interface;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class TranslationPanel extends JPanel {

	private static final long serialVersionUID = 8757878476291206876L;
	
	JButton center;
	JButton left;
	JButton right;
	JButton up;
	JButton down;
	
	public TranslationPanel(Dimension buttonPanelDim, Dimension buttonDim) {
		super();
		
		setLayout(new GridLayout(3, 3));
		setPreferredSize(buttonPanelDim);
		Icon resetCenter = new ImageIcon(getClass().getResource("/center.png"));
		center = new JButton(resetCenter);
		Icon moveLeft = new ImageIcon(getClass().getResource("/left.png"));
		left = new JButton(moveLeft);
		Icon moveRight = new ImageIcon(getClass().getResource("/right.png"));
		right = new JButton(moveRight);
		Icon moveUp = new ImageIcon(getClass().getResource("/up.png"));
		up = new JButton(moveUp);
		Icon moveDown = new ImageIcon(getClass().getResource("/down.png"));
		down = new JButton(moveDown);
		
		center.setPreferredSize(buttonDim);
		left.setPreferredSize(buttonDim);
		right.setPreferredSize(buttonDim);
		up.setPreferredSize(buttonDim);
		down.setPreferredSize(buttonDim);
		
		add(Box.createRigidArea(buttonDim));
		add(up);
		add(Box.createRigidArea(buttonDim));
		add(left);
		add(center);
		add(right);
		add(Box.createRigidArea(buttonDim));
		add(down);
		add(Box.createRigidArea(buttonDim));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}


	public void addButtonChangeListeners(TimerChangeListener timerControler) {
		left.getModel().addChangeListener(timerControler);
		right.getModel().addChangeListener(timerControler);
		up.getModel().addChangeListener(timerControler);
		down.getModel().addChangeListener(timerControler);
	}
	
	public static void scaleImageIcon(ImageIcon imageIcon, int width, int height) {
		Image newImage = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		imageIcon = new ImageIcon(newImage);
	}
}
