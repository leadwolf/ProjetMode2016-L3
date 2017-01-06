package ply.bdd.controlers;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ply.bdd.vues.ModelBrowser;

public class SearchListener implements DocumentListener, FocusListener{

	private ModelBrowser modelBrowser;
	
	public SearchListener(ModelBrowser modelBrowser) {
		this.modelBrowser = modelBrowser;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		updateList();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		updateList();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	
	private void updateList() {
		modelBrowser.updateList();
	}

	@Override
	public void focusGained(FocusEvent e) {
		modelBrowser.clearSearchBar();
	}

	@Override
	public void focusLost(FocusEvent e) {
		modelBrowser.resetTip();
	}
	
}
