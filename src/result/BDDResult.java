package result;

/**
 *  Type d'erreur correspondant à une erreur de {@link BaseDeDonneesOld}
 * @author L3
 *
 */
public class BDDResult extends MethodResult{

	/**
	 * Crée une {@link MethodResult} de ce type, {@link BDDResult}.
	 * @param bddError
	 */
	public BDDResult(BDDResultEnum bddError) {
		result = bddError;
	}
}