package _interface;

import java.awt.GridLayout;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class OptionPanel extends JPanel{
	
	private static final long serialVersionUID = 5934434784791064085L;
	
	JCheckBox directionalLight;
	JCheckBox showFaces;
	JCheckBox showSegments;
	JCheckBox showPoints;
	
	public OptionPanel() {
		super();
		
		setLayout(new GridLayout(0, 1));
		
		directionalLight = new JCheckBox("Lumière directionnelle");
		directionalLight.setSelected(true);
		showFaces = new JCheckBox("Afficher Faces");
		showFaces.setSelected(true);
		showSegments = new JCheckBox("Afficher Segments");
		showSegments.setSelected(true);
		showPoints = new JCheckBox("Afficher Points");
		showPoints.setSelected(false);
		
		add(directionalLight);
		add(showFaces);
		add(showSegments);
		add(showPoints);
	}

	public JCheckBox getDirectionalLight() {
		return directionalLight;
	}	
	
	public JCheckBox getshowFaces() {
		return showFaces;
	}
	public JCheckBox getshowSegments() {
		return showSegments;
	}
	public JCheckBox getshowPoints() {
		return showPoints;
	}

}
