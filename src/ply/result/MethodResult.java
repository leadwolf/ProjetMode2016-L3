package ply.result;

import ply.bdd.strategy.ExecuteStrategy;
import ply.bdd.table.Table;
import ply.reader.LecteurAscii;
import ply.result.BasicResult.BasicResultEnum;

/**
 * Cette classe englobe tout les types de résultats que peut retourner {@link ExecuteStrategy}, {@link Table} et {@link LecteurAscii}
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
	
}
