package ply.bdd.controlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ply.bdd.legacy.FenetreTable;
import ply.bdd.vues.BDDPanelOld;
import ply.bdd.vues.BDDPanelOld;

/**
 * Cette classe permet d'exécuter les requêtes SQL correspondants aux boutons concernés
 * 
 * @author L3
 *
 */
public class ButtonControler implements ActionListener {

	/**
	 * le {@link FenetreTable} à qui ce contrôleur est lié
	 */
	private BDDPanelOld bddPanel;

	/**
	 * Constructeur principale
	 * 
	 * @param bddPanel le {@link FenetreTable} à qui ce contrôleur est lié
	 */
	public ButtonControler(BDDPanelOld bddPanel) {
		this.bddPanel = bddPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand().toLowerCase()) {
		case "insert":
			bddPanel.insertTableAmorce(false);
			break;
		case "reset":
			bddPanel.resetFields();
			break;
		case "confirmer":
			bddPanel.updateTableAmorce(false);
			break;
		default:
			break;
		}

	}

}
