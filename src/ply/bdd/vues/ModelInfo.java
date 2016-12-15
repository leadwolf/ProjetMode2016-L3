package ply.bdd.vues;

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Cette classe donne les informations sur le modèle en vue, comme --name &lt;modele&gt;
 * @author L3
 *
 */
public class ModelInfo extends JPanel {

	private static final long serialVersionUID = 8222717499996369456L;

	public ModelInfo(String modelName) {
		super();
		

		setLayout(new GridLayout(4, 2));
		add(new JLabel("Chemin : "));
		add(new JLabel("chemin default"));
		add(new JLabel("Date : "));
		add(new JLabel("date default"));
		add(new JLabel("Mots Clés : "));
		add(new JLabel("mots cles default"));
		add(new JLabel("Autres colonnes à ajouter : "));
		add(new JLabel("autre"));
	}

}
