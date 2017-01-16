package ply.plyModel.controlers;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

import ply.main.vues.MainFenetre;
import ply.main.vues.ModelPanel;
import ply.plyModel.modeles.FigureModelNew;

public class KeyControler implements KeyEventDispatcher {

	private MainFenetre mainFenetre;
	private double translationSens;
	private long old;
	private long now;

	public KeyControler(MainFenetre mainFenetre) {
		super();
		this.mainFenetre = mainFenetre;
		translationSens = 10;
		old = System.currentTimeMillis();
		now = System.currentTimeMillis();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (mainFenetre.canControlFigure() && e.getID() == KeyEvent.KEY_PRESSED) {
			ModelPanel modelPanel = mainFenetre.getCurrentModelPanel();
			FigureModelNew figureModel = modelPanel.getFigure();
			figureModel.refreshFigDims(modelPanel.getVisPanel());

			switch (e.getKeyCode()) {
			case KeyEvent.VK_DOWN:
				figureModel.translatePoints(0, translationSens, 0);
				break;
			case KeyEvent.VK_UP:
				figureModel.translatePoints(0, -translationSens, 0);
				break;
			case KeyEvent.VK_RIGHT:
				figureModel.translatePoints(translationSens, 0, 0);
				break;
			case KeyEvent.VK_LEFT:
				figureModel.translatePoints(-translationSens, 0, 0);
				break;
			case KeyEvent.VK_D:
				figureModel.rotateYByPoint(5);
				break;
			case KeyEvent.VK_Q:
				figureModel.rotateYByPoint(-5);
				break;
			case KeyEvent.VK_S:
				figureModel.rotateXByPoint(5);
				break;
			case KeyEvent.VK_Z:
				figureModel.rotateXByPoint(-5);
				break;
			case KeyEvent.VK_C:
				figureModel.prepareForWindow(modelPanel.getVisPanel(), 1.0);
				break;
			case KeyEvent.VK_R:
				now = System.currentTimeMillis();
				if ((now - old) > 5000) {
					modelPanel.resetModel();
					old = now;
				}
				break;
			default:
				break;
			}
		} else if (e.getID() == KeyEvent.KEY_PRESSED) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				mainFenetre.setCanControlFigure(true);
			}
		} else if (e.getID() == KeyEvent.KEY_RELEASED) {
		} else if (e.getID() == KeyEvent.KEY_TYPED) {
		}
		return false;
	}

}
