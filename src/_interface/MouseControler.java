package _interface;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

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
	 * Valeur globale par lequel est mutliplée la valeur de translation si SHIFT est appuyé. La translation est donc moins importante. Valeur conseillée 0.3
	 */
	private double reduceTransSens = 0.3;

	/**
	 * Valeur globale par lequel est mutliplée la valeur de rotation si SHIFT est appuyé. La rotation est donc moins importante. Valeur conseillée 0.3
	 */
	private double reduceRotationSens = 0.3;

	/**
	 * Valeur globale par lequel est mutliplée la valeur de zoom si SHIFT est appuyé. Le zoom est donc moins importante. Valeur conseillée 0.3
	 */
	private double reduceZoomSens = 0.3;

	/**
	 * Valeur globale pour la sensitivité de rotation en degrés. Valeur conseillée 5
	 */
	private double rotationSens = 5;

	/**
	 * Valeur globale pour la sensitivté de zoom. Elle est ajouté à 1.0 car on refactorise le modèle apr un facteur de 1.0+x. Valeur conseillée 0.06
	 */
	private double zoomSens = 0.06;

	/**
	 * Valeur globale pour la sensitivé de translations lors du zoom. Elle va diviser la distance entre le centre du panel et la souris. Valeur conseillée 5.0
	 */
	private double zoomTransSens = 5.0; // sensitivity of translation to mousepoint when zooming

	/**
	 * 
	 * @param visualisationPanel On a besoin de sa Figure et après transformation du modèle, de rafraîchir le panel.
	 */
	public MouseControler(VisualisationPanel visualisationPanel) {
		super();

		visPanel = visualisationPanel;
	}

	/**
	 * @param reduceTransSens Valeur globale par lequel est mutliplée la valeur de translation si SHIFT est appuyé. La translation est donc moins importante.
	 *            <b>Valeur conseillée 0.3</b>
	 */
	public void setReduceTransSens(double reduceTransSens) {
		this.reduceTransSens = reduceTransSens;
	}

	/**
	 * @param reduceRotationSens Valeur globale par lequel est mutliplée la valeur de rotation si SHIFT est appuyé. La rotation est donc moins importante.
	 *            <b>Valeur conseillée 0.3</b>
	 */
	public void setReduceRotationSens(double reduceRotationSens) {
		this.reduceRotationSens = reduceRotationSens;
	}

	/**
	 * @param reduceZoomSens Valeur globale par lequel est mutliplée la valeur de zoom si SHIFT est appuyé. Le zoom est donc moins importante. <b>Valeur
	 *            conseillée 0.3</b>
	 */
	public void setReduceZoomSens(double reduceZoomSens) {
		this.reduceZoomSens = reduceZoomSens;
	}

	/**
	 * @param zoomSens Valeur globale pour la sensitivté de zoom. Elle est ajouté à 1.0 car on refactorise le modèle apr un facteur de 1.0+x. <b>Valeur
	 *            conseillée 0.06</b>
	 */
	public void setZoomSens(double zoomSens) {
		this.zoomSens = zoomSens;
	}

	/**
	 * @param zoomTransSens Valeur globale pour la sensitivé de translations lors du zoom. Elle va diviser la distance entre le centre du panel et la souris.
	 *            <b>Valeur conseillée 5.0</b>
	 */
	public void setZoomTransSens(double zoomTransSens) {
		this.zoomTransSens = zoomTransSens;
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
			localReduceTransSens = reduceTransSens;
			localReduceRotationSens = reduceRotationSens;
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
			localReduceZoomSens = reduceZoomSens;
		} else {
			localReduceZoomSens = 1.0;
		}

		Calculations.refreshFigDims(visPanel);

		double figXToMouseX = visPanel.getWidth() / 2 - e.getX(); // distance from center of panel to mouse x
		double figYToMouseY = visPanel.getHeight() / 2 - e.getY(); // distance from center of panel to mouse y

		double zoom = 1.0 + (zoomSens * -e.getWheelRotation() * localReduceZoomSens);

		double movingX = 0, movingY = 0;
		movingX = ((figXToMouseX / zoomTransSens) * zoom) * localReduceZoomSens;
		movingY = ((figYToMouseY / zoomTransSens) * zoom) * localReduceZoomSens;

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
		Calculations.translatePoints(visPanel.getFigure().getPtsTrans(), moveX, moveY, 0);
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
				Calculations.rotateXByPoint(visPanel.getFigure(), rotationSens * localReduceRotationSens);
			} else {
				Calculations.rotateXByPoint(visPanel.getFigure(), -rotationSens * localReduceRotationSens);
			}
		} else {
			if (nextMouseX > originalMouseX) {
				Calculations.rotateYByPoint(visPanel.getFigure(), rotationSens * localReduceRotationSens);
			} else {
				Calculations.rotateYByPoint(visPanel.getFigure(), -rotationSens * localReduceRotationSens);
			}
		}
	}
}