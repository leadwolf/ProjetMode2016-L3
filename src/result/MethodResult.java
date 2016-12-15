package result;

import reader.Lecture;

/**
 * Cette classe englobe tout les types de résultats que peut retourner {@link BaseDeDonnees} et {@link Lecture}
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
