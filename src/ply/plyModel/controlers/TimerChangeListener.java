package ply.plyModel.controlers;

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

	public TimerChangeListener() {
		super();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		ButtonModel buttonModel = (ButtonModel) e.getSource();
		String command = buttonModel.getActionCommand();
		
		if (buttonModel.isPressed() && !timer.isRunning() && command != null) {
			timer.setActionCommand(command);
			timer.start();
		} else if (!buttonModel.isPressed() && timer.isRunning()) {
			timer.stop();
		}
	}
	
	/**
	 * @param timer le Timer à utiliser pour éxécuter l'actionListener
	 */
	public void setTimer(Timer timer) {
		this.timer = timer;
	}

}
