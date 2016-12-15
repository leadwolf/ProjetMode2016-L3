package _interface.controlers;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

import _interface.VisualisationPanel;
import _interface.sensitivity.Sensitivity;
import math.Calculations;

/**
 * Contrôle de modèle avec la souris
 * 
 * @author L3
 *
 */
public class MouseControler extends MouseAdapter {

	/**
	 * On a besoin de sa Figure et après transformation du modèle, de rafraîchir le panel.
	 */
	private VisualisationPanel visPanel;

	private Sensitivity sens;
	
	/**
	 * Valeur pour stocker le point X original de la souris lors d'un drag.
	 */
	private int originalMouseX;

	/**
	 * Valeur pour stocker le point Y original de la souris lors d'un drag.
	 */
	private int originalMouseY;

	/**
	 * Savoir si l'utilisateur est déja en train de faire une translation
	 */
	private boolean translating;

	/**
	 * Savoir si l'utilisateur est déja en train de faire une rotation
	 */
	private boolean rotatating;

	/**
	 * 
	 * @param visualisationPanel On a besoin de sa Figure et après transformation du modèle, de rafraîchir le panel.
	 */
	public MouseControler(VisualisationPanel visualisationPanel, Sensitivity sens) {
		super();

		visPanel = visualisationPanel;
		this.sens = sens;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		zoom(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (SwingUtilities.isLeftMouseButton(e) && !rotatating) {
			translating = true;
		} else if (SwingUtilities.isRightMouseButton(e) && !translating) {
			rotatating = true;
		}

		originalMouseX = e.getX();
		originalMouseY = e.getY();
		Calculations.refreshFigDims(visPanel);
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		double localReduceTransSens, localReduceRotationSens;
		int nextMouseX, nextMouseY;
		nextMouseX = e.getX();
		nextMouseY = e.getY();

		/* SLOW MOVEMENT IF SHIFT PRESSED */
		if (e.isShiftDown()) {
			localReduceTransSens = sens.getReduceTransSens();
			localReduceRotationSens = sens.getReduceRotationSens();
		} else {
			// NO SHIFT = NO SLOW DOWN
			localReduceTransSens = 1.0;
			localReduceRotationSens = 1.0;
		}

		/* Translate Figure */
		if (SwingUtilities.isLeftMouseButton(e) && !rotatating) {
			translate(nextMouseX, nextMouseY, localReduceTransSens);
		}

		/* Rotate Figure */
		if (SwingUtilities.isRightMouseButton(e) && !translating) {
			rotate(nextMouseX, nextMouseY, localReduceRotationSens);
		}

		originalMouseX = nextMouseX;
		originalMouseY = nextMouseY;
		visPanel.refreshObject();
		visPanel.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			translating = false;
		} else if (SwingUtilities.isRightMouseButton(e)) {
			rotatating = false;
		}
	}

	/**
	 * Zoom sur la figure. On zoom vers le curseur et dézoom dans le sens opposé.
	 * 
	 * @param e
	 */
	private void zoom(MouseWheelEvent e) {

		double localReduceZoomSens;
		/* SLOW MOVEMENT IF SHIFT PRESSED */
		if (e.isShiftDown()) {
			localReduceZoomSens = sens.getReduceZoomSens();
		} else {
			localReduceZoomSens = 1.0;
		}

		Calculations.refreshFigDims(visPanel);

		double figXToMouseX = visPanel.getWidth() / 2 - e.getX(); // distance from center of panel to mouse x
		double figYToMouseY = visPanel.getHeight() / 2 - e.getY(); // distance from center of panel to mouse y

		double zoom = 1.0 + (sens.getZoomSens() * -e.getWheelRotation() * localReduceZoomSens);

		double movingX = 0, movingY = 0;
		movingX = ((figXToMouseX / sens.getZoomTransSens()) * zoom) * localReduceZoomSens;
		movingY = ((figYToMouseY / sens.getZoomTransSens()) * zoom) * localReduceZoomSens;

		visPanel.zoom(zoom);
		if (e.getWheelRotation() > 0) {
			// zooming out
			Calculations.translatePoints(visPanel.getFigure(), -movingX, -movingY, 0);
		} else {
			// zoom in
			Calculations.translatePoints(visPanel.getFigure(), movingX, movingY, 0);
		}

		visPanel.refreshObject();
		visPanel.repaint();
	}

	/**
	 * Translation d'après ces paramètres
	 * 
	 * @param nextMouseX le point X qui correspond au point actuel de la souris
	 * @param nextMouseY le point Y qui correspond au point actuel de la souris
	 * @param localReduceTransSens la sensitivité à appliquer
	 */
	private void translate(int nextMouseX, int nextMouseY, double localReduceTransSens) {
		double moveX = (originalMouseX - nextMouseX) * -1 * localReduceTransSens;
		double moveY = (originalMouseY - nextMouseY) * -1 * localReduceTransSens;
		Calculations.translatePoints(visPanel.getFigure().getPoints(), moveX, moveY, 0);
	}

	/**
	 * Translation d'après ces paramètres
	 * 
	 * @param nextMouseX le point X qui correspond au point actuel de la souris
	 * @param nextMouseY le point Y qui correspond au point actuel de la souris
	 * @param localReduceRotationSens la sensitivité à appliquer
	 */
	private void rotate(int nextMouseX, int nextMouseY, double localReduceRotationSens) {
		// si le mouvement Y est plus important que le mouvement X de la souris
		if (Math.abs(nextMouseY - originalMouseY) > Math.abs(nextMouseX - originalMouseX)) {
			// rotation autour de l'axe X entend un mouvement haut/bas donc
			// Y
			if (nextMouseY > originalMouseY) {
				Calculations.rotateXByPoint(visPanel.getFigure(), sens.getRotationSens() * localReduceRotationSens);
			} else {
				Calculations.rotateXByPoint(visPanel.getFigure(), -sens.getRotationSens() * localReduceRotationSens);
			}
		} else {
			if (nextMouseX > originalMouseX) {
				Calculations.rotateYByPoint(visPanel.getFigure(), sens.getRotationSens() * localReduceRotationSens);
			} else {
				Calculations.rotateYByPoint(visPanel.getFigure(), -sens.getRotationSens() * localReduceRotationSens);
			}
		}
	}
}