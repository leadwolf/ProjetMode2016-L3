package _interface;

import javax.swing.ButtonModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Ce changeListener est appelé par ButtonModel.
 * Elle active ensuite timer qui permet d'éxécuter l'actionListener de ButtonControler
 * @author L3
 *
 */
public class TimerChangeListener implements ChangeListener {

	Timer timer;
	TranslationPanel transPanel;
	RotationPanel rotationPanel;

	public TimerChangeListener() {
		super();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (!timer.isRunning()) {
			ButtonModel buttonModel = (ButtonModel) e.getSource();
			switch (buttonModel.getActionCommand()) {
			case "DOWN_T":
				timer.setActionCommand("DOWN_T");
				break;

			default:
				break;
			}
			timer.start();
		} else if (timer.isRunning()) {
			timer.stop();
		}
	}
	
	/**
	 * @param timer le Timer à utiliser pour éxécuter l'actionListener
	 */
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	
	public void setPanels(TranslationPanel transPanel, RotationPanel rotationPanel) {
		this.transPanel = transPanel;
		this.rotationPanel = rotationPanel;
	}

}
