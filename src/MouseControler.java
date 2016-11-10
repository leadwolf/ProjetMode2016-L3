import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

public class MouseControler extends MouseAdapter {
	
	private VisualationPanel visPanel;
	
	private int transX, transY;
	private int rotX, rotY;
	private double zoom = 0.0;
	private double zoomSens = 0.1;
	private double zoomTransSens = 10.0; // sensitivity of translation to mousepoint when zooming
	private double rotationSens = 5;
	private int notches;
	private int nextX, nextY;
	
	public MouseControler(VisualationPanel visualisationPanel) {
		super();
		
		visPanel = visualisationPanel;
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		/**
		 * Zoom into mouse cursor = moving the center of figure nearer to the
		 * mouse cursor
		 */
		int moveX, moveY;
		visPanel.refreshFigDims();
		moveX = (visPanel.getWidth() / 2) - e.getX();
		moveY = (visPanel.getHeight() / 2) - e.getY();
		Point tempCenter = new Point(visPanel.getFigure().getCenter().getX(), visPanel.getFigure().getCenter().getY(), visPanel.getFigure().getCenter().getZ());

		Calculations.translatePoints(visPanel.getFigure().getPtsTrans(), -tempCenter.getX(), -tempCenter.getY());

		notches = e.getWheelRotation() * -1;
		zoom = 1.0 + (zoomSens * notches);
		visPanel.zoom(zoom);

		if (e.getWheelRotation() > 0) {
			Calculations.translatePoints(visPanel.getFigure().getPtsTrans(), (-moveX / zoomTransSens) * zoom, (-moveY / zoomTransSens) * zoom);
		} else {
			// zoom in
			Calculations.translatePoints(visPanel.getFigure().getPtsTrans(), (moveX / zoomTransSens) * zoom, (moveY / zoomTransSens) * zoom);
		}
		Calculations.translatePoints(visPanel.getFigure().getPtsTrans(), tempCenter.getX(), tempCenter.getY());
		visPanel.refreshObject();
		visPanel.repaint();
		/* End Zoom */
	}

	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			transX = e.getX();
			transY = e.getY();
		}
		if (SwingUtilities.isRightMouseButton(e)) {
			rotX = e.getX();
			rotY = e.getY();
		}
		visPanel.refreshFigDims();
	}

	public void mouseDragged(MouseEvent e) {
		/* Translate Figure */
		if (SwingUtilities.isLeftMouseButton(e)) {
			nextX = e.getX();
			nextY = e.getY();
			Calculations.translatePoints(visPanel.getFigure().getPtsTrans(), (transX - nextX) * -1, (transY - nextY) * -1);
			visPanel.refreshObject();
			visPanel.repaint();
			transX = nextX;
			transY = nextY;
		}
		/* End Translate Figure */

		/* Rotate Figure */
		if (SwingUtilities.isRightMouseButton(e)) {
			nextX = e.getX();
			nextY = e.getY();
			if (Math.abs(nextY - rotY) > Math.abs(nextX - rotX)) {
				// rotation autour de l'axe X entend un mouvement haut/bas donc
				// Y
				if (nextY > rotY) {
					Calculations.rotateXByPoint(visPanel.getFigure(), rotationSens);
				} else {
					Calculations.rotateXByPoint(visPanel.getFigure(), -rotationSens);
				}
			} else {
				if (nextX > rotX) {
					Calculations.rotateYByPoint(visPanel.getFigure(), rotationSens);
				} else {
					Calculations.rotateYByPoint(visPanel.getFigure(), -rotationSens);
				}
			}

			visPanel.refreshObject();
			visPanel.repaint();
			rotX = nextX;
			rotY = nextY;
		}
		/* End Rotate Figure */
	}
}