package _interface;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import math.Calculations;
import modele.Figure;

import math.Calculations;

public class KeyDispatcher implements KeyEventDispatcher {

	private Fenetre fenetre;
	private double translationSens = 10;
	
	public KeyDispatcher(Fenetre fenetre) {
		super();
		this.fenetre = fenetre;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getID() == KeyEvent.KEY_PRESSED) {
			Calculations.refreshFigDims(fenetre.getVisPanel());
			switch (e.getKeyCode()) {
			case KeyEvent.VK_DOWN:
				Calculations.translatePoints(fenetre.getFigure(), 0, translationSens, 0);
				fenetre.getVisPanel().refreshObject();
				break;
			case KeyEvent.VK_UP:
				Calculations.translatePoints(fenetre.getFigure(), 0, -translationSens, 0);
				fenetre.getVisPanel().refreshObject();
				break;
			case KeyEvent.VK_RIGHT:
				Calculations.translatePoints(fenetre.getFigure(), translationSens, 0, 0);
				fenetre.getVisPanel().refreshObject();
				break;
			case KeyEvent.VK_LEFT:
				Calculations.translatePoints(fenetre.getFigure(), -translationSens, 0, 0);
				fenetre.getVisPanel().refreshObject();
				break;
			case KeyEvent.VK_D:
				Calculations.rotateYByPoint(fenetre.getFigure(), 5);
				fenetre.getVisPanel().refreshObject();
				break;
			case KeyEvent.VK_Q:
				Calculations.rotateYByPoint(fenetre.getFigure(), -5);
				fenetre.getVisPanel().refreshObject();
				break;
			case KeyEvent.VK_S:
				Calculations.rotateXByPoint(fenetre.getFigure(), 5);
				fenetre.getVisPanel().refreshObject();
				break;
			case KeyEvent.VK_Z:
				Calculations.rotateXByPoint(fenetre.getFigure(), -5);
				fenetre.getVisPanel().refreshObject();
				break;
			case KeyEvent.VK_C:
				Calculations.fitFigureToWindow(fenetre.getVisPanel(), 0.75);
				Calculations.centrerFigure(fenetre.getVisPanel());
				fenetre.getVisPanel().refreshObject();
				break;
			case KeyEvent.VK_R:
				fenetre.setFigure(new Figure(fenetre.getFigure().getPath(), false), 1.0);
				break;
			default:
				break;
			}
		} else if (e.getID() == KeyEvent.KEY_RELEASED) {
		} else if (e.getID() == KeyEvent.KEY_TYPED) {
		}
		return false;
	}

}
