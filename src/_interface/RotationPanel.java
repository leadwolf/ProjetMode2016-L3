package _interface;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class RotationPanel extends JPanel {

	private static final long serialVersionUID = 8757878476291206876L;
	
	JButton left;
	JButton right;
	JButton up;
	JButton down;
	
	public RotationPanel(Dimension buttonPanelDim, Dimension buttonDim) {
		super();
		
		setLayout(new GridLayout(3, 3));
		setPreferredSize(buttonPanelDim);
		Icon rotateLeft = new ImageIcon(getClass().getResource("/rotate_left.png"));
		left = new JButton(rotateLeft);
		Icon rotateRight = new ImageIcon(getClass().getResource("/rotate_right.png"));
		right = new JButton(rotateRight);
		Icon rotateBackwards = new ImageIcon(getClass().getResource("/rotate_back_front.png"));
		up = new JButton(rotateBackwards);
		Icon rotateForwards = new ImageIcon(getClass().getResource("/rotate_front_back.png"));
		down = new JButton(rotateForwards);
		
		left.setPreferredSize(buttonDim);
		right.setPreferredSize(buttonDim);
		up.setPreferredSize(buttonDim);
		down.setPreferredSize(buttonDim);
		
		add(Box.createRigidArea(buttonDim));
		add(up);
		add(Box.createRigidArea(buttonDim));
		add(left);
		add(Box.createRigidArea(buttonDim));
		add(right);
		add(Box.createRigidArea(buttonDim));
		add(down);
		add(Box.createRigidArea(buttonDim));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
	}

}
