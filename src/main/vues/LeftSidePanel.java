package main.vues;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;

import ply.bdd.controlers.JListControler;
import ply.bdd.vues.ModelBrowser;
import ply.bdd.vues.ModelInfo;

/**
 * Cette classe est un JPanel qui toujours placé à gauche dans le JSplitPane de {@link MainFenetre}
 * 
 * @author L3
 *
 */
public class LeftSidePanel extends JPanel {

	private ModelBrowser modelBrowser;
	private ModelInfo modelInfo;

	/**
	 * @param path leave null for default data/
	 * @param dim
	 */
	public LeftSidePanel(Path path, String modelName, Dimension dim) {
		super();

		/* MODEL INFO */
		modelInfo = new ModelInfo(modelName);
		if (modelName != null) {
			modelName = modelName.substring(0, 1).toUpperCase() + modelName.substring(1);
		}
		modelInfo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Informations sur " + modelName + " : "));
		modelInfo.setPreferredSize(new Dimension(dim.width, 100));

		/* MODEL BROWSER */
		modelBrowser = new ModelBrowser(path);
		modelBrowser.setBorder(BorderFactory.createLineBorder(Color.black));
		modelBrowser.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Model Browser :"));

		/* THIS PANEL */
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		add(modelInfo);
		add(modelBrowser);
	}

	public void setNewModelInfo(String newModelName) {
		modelInfo.initModelInfo(newModelName);
	}

	public void setModelInfoBorderTitle(String title) {
		modelInfo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Informations sur " + title + " : "));
	}
	
	public void addMouseListenerToList(MouseListener listener) {
		modelBrowser.addMouseListenerToList(listener);
	}

}
