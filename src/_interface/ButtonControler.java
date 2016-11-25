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

	private Fenetre fenetre;
	
	public ButtonControler(Fenetre fenetre) {
		super();
		this.fenetre = fenetre;
	}

	/**
	 * Ici voir quel bouton a été actionnée et en agir en conséquence sur la figure de visPanel
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(fenetre.getOptionPanel().getDirectionalLight())) {
			fenetre.getVisPanel().setDirectionalLight(!fenetre.getVisPanel().isDirectionalLight());
			fenetre.getVisPanel().refreshObject();
		}
		if (e.getSource().equals(fenetre.getOptionPanel().getshowFaces())) {
			fenetre.getVisPanel().setDrawFaces(!fenetre.getVisPanel().isDrawFaces());
			fenetre.getVisPanel().refreshObject();
		}
		if (e.getSource().equals(fenetre.getOptionPanel().getshowSegments())) {
			fenetre.getVisPanel().setDrawSegments(!fenetre.getVisPanel().isDrawSegments());
			fenetre.getVisPanel().refreshObject();
		}
		if (e.getSource().equals(fenetre.getOptionPanel().getshowPoints())) {
			fenetre.getVisPanel().setDrawPoints(!fenetre.getVisPanel().isDrawPoints());
			fenetre.getVisPanel().refreshObject();
		}
	}

}
