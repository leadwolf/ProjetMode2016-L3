package erreur;

/**
 * Type de résultat basique.
 * @author L3
 *
 */
public class BasicResult extends MethodResult{

	/**
	 * Crée un {@link MethodResult} de type basique
	 * @param basicResult 
	 */
	public BasicResult(BasicResultEnum basicResult) {
		result = basicResult;
	}
}
