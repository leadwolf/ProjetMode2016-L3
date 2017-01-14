package result;

import ply.bdd.base.BDDUtilities;
import ply.bdd.strategy.ExecuteStrategy;
import ply.bdd.table.Table;

/**
 * Type d'erreur correspondant à une erreur de {@link ExecuteStrategy} ou {@link BDDUtilities}.
 * 
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


	/**
	 * Codes d'erreurs que peut retourner {@link ExecuteStrategy}, {@link Table} et {@link BDDUtilities}.
	 * 
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
		 * On a souhaité remplir la base sans qu'elle soit vide au départ.
		 */
		INCORRECT_FILL,
		/**
		 * On a souhaité remplir la base sans qu'elle soit vide au départ.
		 */
		INCORRECT_RESET,
		/**
		 * La table PLY n'existe pas.
		 */
		TABLE_NOT_FOUND,
		/**
		 * La base n'est PAS vide.
		 */
		DB_NOT_EMPTY,
		/**
		 * Aucun modèle comportant ces mot clés n'a été retrouvé dans la base.
		 */
		NONE_FOUND_WITH_KEYWORDS,
		/**
		 * Il n'y a pas de description recherché en argument.
		 */
		NO_KEYWORDS_SPECIFIED,
		/**
		 * Il n'y a pas de description recherché en argument.
		 */
		TOO_MANY_KEYWORDS_SPECIFIED,

		/* ICI CODES D'ECHEC CONCERNANTS REQUETES SQL */

		/**
		 * Aucun champ n'est différent de l'original. Dans update SQL.
		 */
		NO_DIFFERENT_VALUES,
		/**
		 * L'update SQl n'a pas réussi.
		 */
		UPDATE_NOT_SUCCESSFUL,
		/**
		 * Le modèle existe déja dans la base. Quand on éxécute une insertion SQL.
		 */
		PRE_EXISTING_MODEL,
		/**
		 * Aucune valeur renseigné pour une insertion SQL.
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
		/**
		 * On a renseigné des valeurs qui ne correspondent pas aux types de données que la base attend.
		 */
		INCORRECT_TYPES,
		/**
		 * On a pas voulu supprimer une ligne de la base.
		 */
		CANCEL_DELETE,

		/* ICI CODES DE SUCCES CONCERNANTS REQUETES SQL */

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
}