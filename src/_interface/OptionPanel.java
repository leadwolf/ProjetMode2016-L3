package _interface;

import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class OptionPanel extends JPanel{

	JCheckBox directionalLight;
	
	public OptionPanel() {
		super();
		
		setLayout(new GridLayout(0, 1));
		
		directionalLight = new JCheckBox("Lumière directionnelle");
		directionalLight.setSelected(true);
		
		add(directionalLight);
		
	}

	public JCheckBox getDirectionalLight() {
		return directionalLight;
	}	
	

}
