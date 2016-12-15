package result;


/**
 * Codes de résulats de base.
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
	BAD_ARGUMENTS,
	NO_ARGUMENTS,
	CONFLICTING_ARGUMENTS,
	MULTIPLE_PLY_ARGS,
	UNKNOWN_ARG,
	NO_PLY_FILE_IN_ARG;
}
