package ply.result;

import ply.bdd.table.Table;
import ply.result.BasicResult.BasicResultEnum;
import ply.result.ReaderResult.FaceResultEnum;
import ply.result.ReaderResult.PointResultEnum;
import ply.result.ReaderResult.ReaderResultEnum;

/**
 * Cette classe englobe tout les types de résultats que peut retourner {@link ExecuteCommand}, {@link Table} et {@link LecteurAscii}
 * 
 * @author L3
 *
 */
public abstract class MethodResult {

	/**
	 * La constante (Enum) de retour qu'une méthode pourra retourner.
	 */
	protected Enum<?> result;
	
	/**
	 * @return un Enum correspondant au de retour d'une méthode implémentant MethodResult.
	 */
	public Enum<?> getCode() {
		return result;
	}
	
	public MethodResult() {
		result = BasicResultEnum.ALL_OK;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(Enum<?> result) {
		this.result = result;
	}

	public void setCode(ReaderResultEnum result) {
		this.result = result;
	}

	public void setCode(PointResultEnum result) {
		this.result = result;
	}

	public void setCode(FaceResultEnum result) {
		this.result = result;
	}
	
}
