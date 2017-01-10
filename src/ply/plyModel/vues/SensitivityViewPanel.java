package ply.plyModel.vues;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ply.plyModel.other.SensitivityModel;

/**
 * JPanel présentant deux JSliders pour changer la sensitvité de la rotation et du zoom.
 * Pas de MVC 
 * @author L3
 *
 */
public class SensitivityViewPanel extends JPanel {
	
	private static final long serialVersionUID = 8575930089328626742L;
	private SensitivityModel sens;
	
	/**
	 * Crée deux JSliders pour changer la sensitvité de la rotation et du zoom
	 * @param sens le modèle comportant les valeurs de sensitivité
	 */
	public SensitivityViewPanel(SensitivityModel sens) {
		super();
		
		this.sens = sens;
		
		/* ROTATION SLIDER */
		int fakeRotationSens = (int) (sens.getRotationSens() * 10);
		JSlider rotationSensSlider = new JSlider(SwingConstants.HORIZONTAL, 5, 25, fakeRotationSens);

		// TICKS
		rotationSensSlider.setMajorTickSpacing(10);
		rotationSensSlider.setMinorTickSpacing(5);
		rotationSensSlider.setPaintTicks(true);
		
		// LABELS
		Hashtable<Integer, JLabel> labelTableRotation = new Hashtable<Integer, JLabel>();
		labelTableRotation.put( new Integer( 5 ), new JLabel("Lent") );
		labelTableRotation.put( new Integer( 25 ), new JLabel("Rapide") );
		rotationSensSlider.setLabelTable( labelTableRotation );
		rotationSensSlider.setPaintLabels(true);
		
		rotationSensSlider.setFocusable(false);

		/* ZOOM SLIDER */
		int fakeZoomSens = (int) (sens.getZoomSens() * 100);
		JSlider zoomSensSlider = new JSlider(SwingConstants.HORIZONTAL, 4, 17, fakeZoomSens);

		// TICKS
		zoomSensSlider.setMajorTickSpacing(4);
		zoomSensSlider.setMinorTickSpacing(1);
		zoomSensSlider.setPaintTicks(true);
		
		// LABELS
		Hashtable<Integer, JLabel> labelTableZoom = new Hashtable<Integer, JLabel>();
		labelTableZoom.put( new Integer( 4 ), new JLabel("Lent") );
		labelTableZoom.put( new Integer( 17 ), new JLabel("Rapide") );
		zoomSensSlider.setLabelTable( labelTableZoom );
		zoomSensSlider.setPaintLabels(true);
		
		zoomSensSlider.setFocusable(false);

		
		rotationSensSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				sens.setRotationSens(source.getValue());
			}
		});
		zoomSensSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				sens.setZoomSens(source.getValue());
			}
		});
		
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());
		gbc.insets = new Insets(10, 10, 10, 10);
		add(new JLabel("Rotation :"), gbc);
		gbc.gridx = 1;
		add(rotationSensSlider, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		add(new JLabel("Zoom :"), gbc);
		gbc.gridx = 1;
		add(zoomSensSlider, gbc);
	}
	
}
