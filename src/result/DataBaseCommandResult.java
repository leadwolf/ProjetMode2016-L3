package result;

import main.vues.MainFenetre;
import ply.bdd.vues.BDDPanel;

public class DataBaseCommandResult {

	private BDDPanel panelResult;
	private MethodResult methodResult;

	public DataBaseCommandResult(BDDPanel bddPanel) {
		this.panelResult = bddPanel;
		this.methodResult = null;
	}

	public DataBaseCommandResult(MethodResult methodResult) {
		this.methodResult = methodResult;
		this.panelResult = null;
	}

	/**
	 * @return the panelResult
	 */
	public BDDPanel getPanelResult() {
		return panelResult;
	}

	public void setEditable(boolean b) {
		panelResult.setEditable(b);
	}

	public void setCanAddRow(boolean b) {
		panelResult.setCanAddRow(b);
	}

	public void setMainFenetre(MainFenetre mainFenetre) {
		panelResult.setMainFenetre(mainFenetre);
	}

	public Enum getCode() {
		return methodResult.getCode();
	}

	public MethodResult getMethodResult() {
		return methodResult;
	}

}
