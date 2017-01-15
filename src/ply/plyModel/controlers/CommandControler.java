package ply.plyModel.controlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import ply.main.vues.ModelPanel;
import ply.plyModel.modeles.FigureModel;
import ply.plyModel.vues.VisualisationPanel;

/**
 * Cette classe permet d'agir à travers les clics de boutons que ce soit la rotation, translation, centrage, etc... sur
 * le modèle {@link FigureModel} grâce à ses méthodes. Celle-ci notifie alors ses observers
 * 
 * @author Master
 *
 */
public class CommandControler implements ActionListener {

	private double translationSens = 10;
	private ModelPanel fenetre;
	private FigureModel figureModel;

	public CommandControler(ModelPanel fenetre) {
		super();
		this.fenetre = fenetre;
		this.figureModel = fenetre.getFigure();
	}

	/**
	 * Ici voir quel bouton a été actionnée et en agir en conséquence sur la figure de visPanel. Elle est aussi appelé
	 * par timer.start()
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		figureModel.refreshFigDims(fenetre.getVisPanel());
		JCheckBox checkbox = null;
		if (e.getSource() instanceof JCheckBox) {
			checkbox = (JCheckBox) e.getSource();
		}
		VisualisationPanel panel = fenetre.getVisPanel();

		switch (e.getActionCommand()) {
		case "directional_light":
			fenetre.getVisPanel().setDirectionalLight(!fenetre.getVisPanel().isDirectionalLight());
			figureModel.refreshModel();
			break;
		case "show_faces":
			if (panel.isDrawPoints() || panel.isDrawSegments()) {
				fenetre.getVisPanel().setDrawFaces(!fenetre.getVisPanel().isDrawFaces());
				figureModel.refreshModel();
			} else {
				checkbox.setSelected(true);
			}
			fenetre.getOptionPanel().getDirectionalLight().setEnabled(checkbox.isSelected());
			break;
		case "show_segments":
			if (panel.isDrawPoints() || panel.isDrawFaces()) {
				fenetre.getVisPanel().setDrawSegments(!fenetre.getVisPanel().isDrawSegments());
				figureModel.refreshModel();
			} else {
				checkbox.setSelected(true);
			}
			break;
		case "show_points":
			if (panel.isDrawFaces() || panel.isDrawSegments()) {
				fenetre.getVisPanel().setDrawPoints(!fenetre.getVisPanel().isDrawPoints());
				figureModel.refreshModel();
			} else {
				checkbox.setSelected(true);
			}
			break;
		case "UP_T":
			figureModel.translatePoints(0, -translationSens, 0);
			break;
		case "DOWN_T":
			figureModel.translatePoints(0, translationSens, 0);
			break;
		case "RIGHT_T":
			figureModel.translatePoints(translationSens, 0, 0);
			break;
		case "LEFT_T":
			figureModel.translatePoints(-translationSens, 0, 0);
			break;
		case "CENTER_T":
			figureModel.prepareForWindow(fenetre.getVisPanel(), 1.0);
			break;
		case "UP_R":
			figureModel.rotateXByPoint(-5);
			break;
		case "DOWN_R":
			figureModel.rotateXByPoint(5);
			break;
		case "RIGHT_R":
			figureModel.rotateYByPoint(5);
			break;
		case "LEFT_R":
			figureModel.rotateYByPoint(-5);
			break;
		default:
			break;
		}
	}

}
