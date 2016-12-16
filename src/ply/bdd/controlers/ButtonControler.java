package ply.bdd.controlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import main.vues.BDDPanel;
import ply.bdd.vues.FenetreTable;

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
	private BDDPanel bddPanel;

	/**
	 * Constructeur principale
	 * 
	 * @param frame
	 *            le {@link FenetreTable} à qui ce contrôleur est lié
	 */
	public ButtonControler(BDDPanel frame) {
		this.bddPanel = frame;
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
