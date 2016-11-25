package bddInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	private FenetreTable frame;

	/**
	 * Constructeur principale
	 * 
	 * @param frame
	 *            le {@link FenetreTable} à qui ce contrôleur est lié
	 */
	public ButtonControler(FenetreTable frame) {
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand().toLowerCase()) {
		case "insert":
			frame.insertTableAmorce(false);
			break;
		case "reset":
			frame.resetFields();
			break;
		case "confirmer":
			frame.updateTableAmorce(false);
			break;
		default:
			break;
		}

	}

}
