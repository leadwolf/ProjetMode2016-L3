package main.controlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import main.vues.Controls;

public class MenuListener implements ActionListener{

	public MenuListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "controles":
			Controls controls = new Controls();
			break;
		default:
			break;
		}
	}

}
