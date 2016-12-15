package main.vues;

import java.awt.Color;
import java.awt.Dimension;
import java.nio.file.Path;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ply.bdd.vues.ModelBrowser;
import ply.bdd.vues.ModelInfo;

/**
 * Cette classe est un JPanel qui toujours placé à gauche dans le JSplitPane de {@link MainFenetre}
 * @author L3
 *
 */
public class LeftSidePanel extends JPanel{

	public LeftSidePanel(Path path, Dimension dim) {
		super();
				
		/* MODEL INFO */
		ModelInfo modelInfo = new ModelInfo("default");
		modelInfo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Informations sur default :"));
		modelInfo.setPreferredSize(new Dimension(dim.width, 100));
		
		/* MODEL BROWSER */
		ModelBrowser modelBrowser = new ModelBrowser(path);
		modelBrowser.setBorder(BorderFactory.createLineBorder(Color.black));
		modelBrowser.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Model Browser :"));
		

		/* THIS PANEL */
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(modelInfo);
		add(modelBrowser);
	}
	
}
