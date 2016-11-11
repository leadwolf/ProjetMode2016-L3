package _interface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import math.Calculations;

/**
 * Cette classe permet d'agir sur les clics de boutons que ce soit la rotation, translation, centrage, etc... à partir
 * des méthodes de {@link Calculations}
 * @author Master
 *
 */
public class ButtonControler implements ActionListener{

	private VisualisationPanel visPanel;
	
	public ButtonControler(VisualisationPanel visualisationPanel) {
		this.visPanel = visualisationPanel;
	}

	/**
	 * Ici voir quel bouton a été actionnée et en agir en conséquence sur la figure de visPanel
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
