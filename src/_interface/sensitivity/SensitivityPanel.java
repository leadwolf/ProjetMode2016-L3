package _interface.sensitivity;

import java.awt.GridLayout;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import _interface.controlers.SliderControler;

public class SensitivityPanel extends JPanel {

	private JSlider rotationSensSlider;
	private JSlider zoomSensSlider;
	private SliderControler sliderControler;
	
	private Sensitivity sens;
	
	public SensitivityPanel(Sensitivity sens) {
		super();
		
		this.sens = sens;
		sliderControler = new SliderControler(this);
		
		/* ROTATION SLIDER */
		int fakeRotationSens = (int) (sens.getRotationSens() * 10);
		rotationSensSlider = new JSlider(SwingConstants.HORIZONTAL, 5, 25, fakeRotationSens);

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

		/* ZOOM SLIDER */
		int fakeZoomSens = (int) (sens.getZoomSens() * 100);
		zoomSensSlider = new JSlider(SwingConstants.HORIZONTAL, 4, 17, fakeZoomSens);

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

		
		rotationSensSlider.addChangeListener(sliderControler);
		zoomSensSlider.addChangeListener(sliderControler);
		
		setLayout(new GridLayout(4, 1));
		add(new JLabel("Rotation :"));
		add(rotationSensSlider);
		add(new JLabel("Zoom :"));
		add(zoomSensSlider);
	}

	/**
	 * @return the rotationSens
	 */
	public JSlider getRotationSensSlider() {
		return rotationSensSlider;
	}

	/**
	 * @return the zoomSens
	 */
	public JSlider getZoomSensSlider() {
		return zoomSensSlider;
	}
	
	/**
	 * @param zoomSens 
	 */
	public void setZoomSens(int zoomSens) {
		this.sens.setZoomSens(zoomSens);
	}
	
	/**
	 * @param rotationSens
	 */
	public void setRotationSens(double rotationSens) {
		this.sens.setRotationSens(rotationSens);
	}

	/**
	 * @return
	 */
	public Sensitivity getSens() {
		return this.sens;
	}
	
}
