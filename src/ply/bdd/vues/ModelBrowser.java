package ply.bdd.vues;

import java.awt.event.MouseListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import main.vues.ModelPanel;
import ply.bdd.controlers.JListControler;

/**
 * Cette classe propose une JList des modèles permettant de changer rapidement de modèle qui est dans {@link ModelPanel}
 * @author L3
 *
 */
public class ModelBrowser extends JPanel {

	/**
	 * Sauvegarde le path vers le dossier contenant les modèles.
	 */
	private Path path;
	/**
	 * Sauvegarde les noms de modèles
	 */
	private List<Path> modelsPathsList;
	private JList<String> modelsList;
	
	/**
	 * @param path
	 */
	public ModelBrowser(Path path) {
		super();
		
		
		/* INIT VARS */
		this.path = path.toAbsolutePath();
		modelsPathsList = new ArrayList<>();
		DefaultListModel<String> listModel = new DefaultListModel<>();
		
		/* INIT LISTS */
		for (String s : path.toFile().list()) {
			Path file = Paths.get(path.toAbsolutePath() + "/"  + s);
			String extension = file.getFileName().toString();
			extension = extension.substring(extension.lastIndexOf('.'), extension.length());
			if (extension.equals(".ply")) {
				modelsPathsList.add(file);
				listModel.addElement(file.getFileName().toString());
			}
		}
		
		/* JLIST */
		modelsList = new JList<>(listModel);
		modelsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane(modelsList);

		/* THIS PANEL */
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(scrollPane);
	}
	
	public void addMouseListenerToList(MouseListener listener) {
		modelsList.addMouseListener(listener);
	}

	/**
	 * @return the modelsList
	 */
	public JList<String> getModelsList() {
		return modelsList;
	}

}
