package ply.bdd.vues;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
		
		String[] modelInfo = BaseDeDonneesNew.getNameInfo(modelName, false);
		if (modelInfo != null) {
			setLayout(new GridLayout(4, 2));
			add(new JLabel("Chemin : "));
			add(new JLabel(modelInfo[1]));
			add(new JLabel("Date : "));
			add(new JLabel(modelInfo[2]));
			add(new JLabel("Mots Clés : "));
			add(new JLabel(modelInfo[3]));
			add(new JLabel("Autres colonnes à ajouter : "));
			add(new JLabel("autre"));
		} else {
			add(new JLabel("Aucune information sur le modèle"));
		}
	}

}
