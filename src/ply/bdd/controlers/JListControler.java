package ply.bdd.controlers;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;

import main.vues.MainFenetre;

public class JListControler extends MouseAdapter {

	private MainFenetre mainFenetre;

	public JListControler(MainFenetre mainFenetre) {
		this.mainFenetre = mainFenetre;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getSource() instanceof JList<?>) {
			JList<String> list = (JList<String>) e.getSource();
			mainFenetre.addNewModel(list.getSelectedIndex());
		}
	}
}
