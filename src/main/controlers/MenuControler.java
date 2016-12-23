package main.controlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import main.vues.Controls;

public class MenuControler implements ActionListener{

	Controls controls;
	
	public MenuControler() {
		controls = new Controls();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "controles":
			controls.setVisible(true);
			break;
		default:
			break;
		}
	}

}
