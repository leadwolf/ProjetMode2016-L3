package erreur;

import bdd.BaseDeDonnees;
import reader.Lecture;

/**
 * Cette classe englobe tout les types de résultats que peut retourner {@link BaseDeDonnees} et {@link Lecture}
 * @author L3
 *
 */
public abstract class MethodResult {

	/**
	 * La constante de retour de la méthode qui retoune celle-ci.
	 */
	protected Enum<?> result;
	
	/**
	 * @return un Enum correspondant à une constante de retour de méthode
	 */
	public Enum<?> getCode() {
		return result;
	}
	
	public MethodResult() {
		result = BasicResultEnum.ALL_OK;
	}
	
}
