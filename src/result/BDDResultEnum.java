package result;

/**
 * Codes d'erreurs que peut retourner {@link BaseDeDonneesOld}
 * @author L3
 *
 */
public enum BDDResultEnum {

	/**
	 * Le nom de ce modèle n'a pas été retrouvé dans la base.
	 */
	MODEL_NOT_FOUND,
	/**
	 * Il n'y a pas de nom recherché en argument.
	 */
	NAME_NOT_SPECIFIED,
	/**
	 * Il y a trop d'arguments.
	 */
	TOO_MANY_ARGS,
	/**
	 * La base est vide.
	 */
	EMPTY_DB,
	/**
	 * Aucun modèle comportant ces mot clés n'a été retrouvé dans la base.
	 */
	NONE_FOUND_WITH_DESC,
	/**
	 * Il n'y a pas de description recherché en argument.
	 */
	NO_DESC_SPECIFIED,
	/**
	 * Aucun champ n'est différent de l'original. Dans update SQL.
	 */
	NO_DIFFERENT_VALUES,
	
	/**
	 * L'update SQl n'a pas réussi.
	 */
	UPDATE_NOT_SUCCESSFUL,
	/**
	 * Le modèle existe déja. Quand on éxécute une insertion SQL. N'utiliser que quand l'erreur ne peut pas être précisé.
	 */
	PRE_EXISTING_MODEL,
	/**
	 * Aucune valeur renseigné pour une insetion SQL.
	 */
	NO_VALUES_SPECIFIED,
	/**
	 * L'insertion SQL n'a pas réussi. N'utiliser que quand l'erreur ne peut pas être précisé.
	 */
	INSERT_NOT_SUCCESSFUL,
	/**
	 * On a pu trouver le modèle et afficher ses métadonnées.
	 */
	SHOW_NAME_NOT_SUCCESSFUL,
	/**
	 * On a pu afficher le contenu de la base.
	 */
	SHOW_ALL_NOT_SUCCESSFUL,
	/**
	 * On a pu trouver un/des modèle(s) correspondant(s) à/aux mot(s) clé(s)
	 */
	FIND_NOT_SUCCESSFUL,
	/**
	 * Le delete SQL a réussi.
	 */
	DELETE_NOT_SUCCESSFUL,
	/**
	 * La méthode --edit n'a pas pu être éxécutée.
	 */
	EDIT_NOT_SUCCESSFUL,
	
	/* ICI CODE DE SUCCES */


	/**
	 * On a pu trouver le modèle et afficher ses métadonnées.
	 */
	SHOW_NAME_SUCCESSFUL,
	/**
	 * On a pu afficher le contenu de la base.
	 */
	SHOW_ALL_SUCCESSFUL,
	/**
	 * On a pu trouver un/des modèle(s) correspondant(s) à/aux mot(s) clé(s)
	 */
	FIND_SUCCESSFUL,
	/**
	 * Le delete SQL a réussi.
	 */
	DELETE_SUCCESSFUL,
	/**
	 * L'update SQl a réussi.
	 */
	UPDATE_SUCCESSFUL,
	/**
	 * L'insertion SQl a réussi.
	 */
	INSERT_SUCCESSFUL;
	
}
