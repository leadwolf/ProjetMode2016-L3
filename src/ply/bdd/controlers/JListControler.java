package ply.bdd.controlers;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;

import main.vues.MainFenetre;

public class JListControler implements MouseListener{

	private MainFenetre mainFenetre;
	
	public JListControler(MainFenetre mainFenetre) {
		this.mainFenetre = mainFenetre;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() instanceof JList<?>) {
			JList<String> list = (JList<String>) e.getSource();
			if (e.getClickCount() == 2) {
				mainFenetre.addNewModel(list.getSelectedIndex());
		    }
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
