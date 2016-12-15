package ply.plyModel.vues;

import java.awt.GridLayout;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class OptionPanel extends JPanel{
	
	private static final long serialVersionUID = 5934434784791064085L;
	
	private JCheckBox directionalLight;
	private JCheckBox showFaces;
	private JCheckBox showSegments;
	private JCheckBox showPoints;
	
	public OptionPanel() {
		super();
		
		setLayout(new GridLayout(0, 1));
		
		directionalLight = new JCheckBox("Lumière directionnelle");
		directionalLight.setSelected(true);
		directionalLight.setActionCommand("directional_light");
		showFaces = new JCheckBox("Afficher Faces");
		showFaces.setSelected(true);
		showFaces.setActionCommand("show_faces");
		showSegments = new JCheckBox("Afficher Segments");
		showSegments.setSelected(false);
		showSegments.setActionCommand("show_segments");
		showPoints = new JCheckBox("Afficher Points");
		showPoints.setSelected(false);
		showPoints.setActionCommand("show_points");
		
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
