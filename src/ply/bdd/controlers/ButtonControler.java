package ply.bdd.controlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ply.bdd.vues.BDDPanel;

/**
 * Cette classe permet d'agir sur la table de {@link BDDPanel}
 * 
 * @author L3
 *
 */
public class ButtonControler implements ActionListener {

	/**
	 * le {@link BDDPanel} à qui ce contrôleur est lié
	 */
	private BDDPanel bddPanel;

	/**
	 * Constructeur principale
	 * 
	 * @param bddPanel le {@link BDDPanel} auquel ce contrôleur est lié.
	 * 
	 */
	public ButtonControler(BDDPanel bddPanel) {
		this.bddPanel = bddPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand().toLowerCase()) {
		case "ajouter une ligne":
			bddPanel.addRow();
			break;
		case "reset":
			bddPanel.resetAll();
			break;
		default:
			break;
		}

	}

}
