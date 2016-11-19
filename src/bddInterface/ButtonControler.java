package bddInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Cette classe permet de réagir aux actions des boutons
 * @author L3
 *
 */
public class ButtonControler implements ActionListener{

	private FenetreTable frame;
	
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
