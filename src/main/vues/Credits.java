package main.vues;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Credits extends JFrame {

	private Dimension dim = new Dimension(800, 400);

	public Credits() {
		JPanel mainPanel = new JPanel();

		JPanel person = new JPanel();
		JPanel name = new JPanel();
		name.add(new JLabel("Carmelo Canta"));
		person.add(name);
		JPanel contributions = new JPanel();
		contributions.add(new JLabel("Classe Point"));
		contributions.add(new JLabel("Fichiers .ply pour le livrable 1"));
		contributions.add(new JLabel("ActionCommand sur les boutons de rotation et translation"));
		contributions.setLayout(new BoxLayout(contributions, BoxLayout.Y_AXIS));

		mainPanel.add(person);
		mainPanel.add(contributions);

		person = new JPanel();
		name = new JPanel();
		name.add(new JLabel("Cyprien Behaque"));
		person.add(name);
		contributions = new JPanel();
		contributions.add(new JLabel("N/A"));
		contributions.setLayout(new BoxLayout(contributions, BoxLayout.Y_AXIS));

		mainPanel.add(person);
		mainPanel.add(contributions);
		
		person = new JPanel();
		name = new JPanel();
		name.add(new JLabel("Rémi Lasalle"));
		person.add(name);
		contributions = new JPanel();
		contributions.add(new JLabel("Classe Segment"));
		contributions.add(new JLabel("Classe Face"));
		contributions.add(new JLabel("OptionPanel : options afficher faces, segments"));
		contributions.setLayout(new BoxLayout(contributions, BoxLayout.Y_AXIS));

		mainPanel.add(person);
		mainPanel.add(contributions);

		person = new JPanel();
		name = new JPanel();
		name.add(new JLabel("Cyril Gerard"));
		person.add(name);
		contributions = new JPanel();
		contributions.add(new JLabel("Base de donneés rudimentaire"));
		contributions.add(new JLabel("Séparation des test en plusieurs méthodes"));
		contributions.add(new JLabel("Quelques tests de base de données"));
		contributions.setLayout(new BoxLayout(contributions, BoxLayout.Y_AXIS));
		mainPanel.add(person);
		mainPanel.add(contributions);

		person = new JPanel();
		name = new JPanel();
		name.add(new JLabel("Christopher Caroni"));
		person.add(name);
		contributions = new JPanel();
		contributions.add(new JLabel("Tout le reste"));
		contributions.setLayout(new BoxLayout(contributions, BoxLayout.Y_AXIS));

		mainPanel.add(person);
		mainPanel.add(contributions);

		mainPanel.setLayout(new GridLayout(5, 2, 25, 25));
		
		add(mainPanel);
		setLayout(new FlowLayout());
		setSize(dim);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setVisible(false);

	}

}
