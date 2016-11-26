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
	
	private double translationSens = 10;
	private Fenetre fenetre;
	
	public ButtonControler(Fenetre fenetre) {
		super();
		this.fenetre = fenetre;
	}

	/**
	 * Ici voir quel bouton a été actionnée et en agir en conséquence sur la figure de visPanel.
	 * Elle est aussi appelé par timer.start()
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Calculations.refreshFigDims(fenetre.getVisPanel());
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
		switch (e.getActionCommand()) {
		case "UP_T":
			Calculations.translatePoints(fenetre.getFigure(), 0, -translationSens, 0);
			fenetre.getVisPanel().refreshObject();
			break;
		case "DOWN_T":
			Calculations.translatePoints(fenetre.getFigure(), 0, translationSens, 0);
			fenetre.getVisPanel().refreshObject();
			break;
		case "RIGHT_T":
			Calculations.translatePoints(fenetre.getFigure(), translationSens, 0, 0);
			fenetre.getVisPanel().refreshObject();
			break;
		case "LEFT_T":
			Calculations.translatePoints(fenetre.getFigure(), -translationSens, 0, 0);
			fenetre.getVisPanel().refreshObject();
			break;
		case "CENTER_T":
			Calculations.fitFigureToWindow(fenetre.getVisPanel(), 0.75);
			Calculations.centrerFigure(fenetre.getVisPanel());
			fenetre.getVisPanel().refreshObject();
			break;
		case "UP_R":
			Calculations.rotateXByPoint(fenetre.getFigure(), -5);
			fenetre.getVisPanel().refreshObject();
			break;
		case "DOWN_R":
			Calculations.rotateXByPoint(fenetre.getFigure(), 5);
			fenetre.getVisPanel().refreshObject();
			break;
		case "RIGHT_R":
			Calculations.rotateYByPoint(fenetre.getFigure(), 5);
			fenetre.getVisPanel().refreshObject();
			break;
		case "LEFT_R":
			Calculations.rotateYByPoint(fenetre.getFigure(), -5);
			fenetre.getVisPanel().refreshObject();
			break;
		default:
			break;
		}
	}

}
