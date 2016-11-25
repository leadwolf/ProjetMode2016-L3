package erreur;

import bdd.BaseDeDonnees;

/**
 *  Type d'erreur correspondant à une erreur de {@link BaseDeDonnees}
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