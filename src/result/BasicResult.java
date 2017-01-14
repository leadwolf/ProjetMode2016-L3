package result;

/**
 * Type de résultat basique.
 * @author L3
 *
 */
public class BasicResult extends MethodResult{

	/**
	 * Crée un {@link MethodResult} de type basique
	 * 
	 * @param basicResult
	 */
	public BasicResult(BasicResultEnum basicResult) {
		result = basicResult;
	}

	/**
	 * Codes de résulats de base.
	 * 
	 * @author L3
	 *
	 */
	public enum BasicResultEnum {
		/**
		 * Pas d'erreurs
		 */
		ALL_OK,
		/**
		 * Erreur quelconque.
		 */
		UNKNOWN_ERROR,
		/**
		* 
		*/
		BAD_ARGUMENTS,
		/**
		* 
		*/
		NO_ARGUMENTS,
		/**
		 * Il y avait soit une commande BDD en même temps qu'une commande 3D OU il y avait plusieurs commands BDD a la fois.
		 */
		CONFLICTING_ARGUMENTS,
		/**
		* 
		*/
		MULTIPLE_PLY_ARGS,
		/**
		 * L'argument n'est pas une commande ou ne fait pas partie d'une commande. Elle ne doit pas être là.
		 */
		UNKNOWN_ARG,
		/**
		 * La méthode requiert des options qui n'ont pas été précisées.
		 */
		MISSING_OPTIONS,
		/**
		 * On a bien donné des arguments mais on a pas trouvé un String correspondant à une commande.
		 */
		NO_COMMAND_GIVEN,
		/**
		* 
		*/
		NO_PLY_FILE_IN_ARG;
	}
}
