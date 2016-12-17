package ply.bdd.vues;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import main.BaseDeDonneesNew;

/**
 * Cette classe donne les informations sur le modèle en vue, comme --name &lt;modele&gt;
 * @author L3
 *
 */
public class ModelInfo extends JPanel {

	private static final long serialVersionUID = 8222717499996369456L;

	public ModelInfo(String modelName) {
		super();
		initModelInfo(modelName);
	}
	
	public void initModelInfo(String name) {
		String[] modelInfo = BaseDeDonneesNew.getNameInfo(name, false);
		if (modelInfo != null) {
			if (getComponentCount() > 0) {
				removeAll();
			}
			setLayout(new GridLayout(6, 2));
			add(new JLabel("Chemin : "));
//			add(new JLabel(modelInfo[1]));
			JTextArea chemin = new JTextArea(modelInfo[1]);
			chemin.setWrapStyleWord(true);
			chemin.setLineWrap(true);
			chemin.setFocusable(false);
			add(chemin);
			add(new JLabel("Date : "));
			add(new JLabel(modelInfo[2]));
			add(new JLabel("Mots Clés : "));
			add(new JLabel(modelInfo[3]));
			add(new JLabel("Nombre de points : "));
			add(new JLabel(modelInfo[4]));
			add(new JLabel("Nombre de faces : "));
			add(new JLabel(modelInfo[5]));
		} else {
			add(new JLabel("Aucune information sur le modèle"));
		}
	}

}
