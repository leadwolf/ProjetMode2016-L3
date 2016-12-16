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
 * @author L3
 *
 */
public class LeftSidePanel extends JPanel{

	private ModelBrowser modelBrowser;
	
	/**
	 * @param path leave null for default data/
	 * @param dim
	 */
	public LeftSidePanel(Path path, String modelName, Dimension dim) {
		super();
				
		/* MODEL INFO */
		ModelInfo modelInfo = new ModelInfo(modelName);
		modelInfo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Informations sur " + modelName + " :"));
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
	
	public void addMouseListenerToList(MouseListener listener) {
		modelBrowser.addMouseListenerToList(listener);
	}
	
}
