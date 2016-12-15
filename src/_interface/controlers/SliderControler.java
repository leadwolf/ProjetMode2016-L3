package _interface.controlers;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import _interface.sensitivity.SensitivityPanel;

public class SliderControler implements ChangeListener {

	private SensitivityPanel sensPanel;

	public SliderControler(SensitivityPanel sensPanel) {
		this.sensPanel = sensPanel;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() instanceof JSlider) {
			JSlider source = (JSlider) e.getSource();
			if (source.equals(sensPanel.getZoomSensSlider())) {
				sensPanel.getSens().setZoomSens(source.getValue());
			} else if (source.equals(sensPanel.getRotationSensSlider())) {
				sensPanel.getSens().setRotationSens((double) source.getValue());
			}
		}
}

}
