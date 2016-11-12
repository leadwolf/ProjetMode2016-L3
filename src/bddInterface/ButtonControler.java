package bddInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonControler implements ActionListener{

	private FenetreEdit frame;
	
	public ButtonControler(FenetreEdit frame) {
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "insert":
			frame.Insert();
			break;
		case "reset":
			frame.resetFields();
			break;
		default:
			break;
		}
		
	}

}
