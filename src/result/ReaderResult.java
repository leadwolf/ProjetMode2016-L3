package result;

import reader.Lecture;
/**
 * Type d'erreur correspondant à une erreur de {@link Lecture}
 * @author L3
 *
 */
public class ReaderResult extends MethodResult{

	/**
	 * Crée une {@link MethodResult} de ce type, {@link ReaderResult}.
	 * @param readerEror
	 */
	public ReaderResult(ReaderResultEnum readerEror) {
		result = readerEror;
	}
	
	/**
	 * Codes d'erreurs de {@link Lecture}
	 * 
	 * @author L3
	 *
	 */
	public enum ReaderResultEnum {
		/**
		 * On suppose qu'on lit un point. Il manque une coordonnée alors qu'on s'attend à en avoir 3.
		 */
		MISSING_COORD,
		/**
		 * On n'a pas pu trouvé la ligne "element face X".
		 */
		MISSING_ELEMENT_FACE,
		/**
		 * On n'a pas pu trouvé la ligne "element vertex X".
		 */
		MISSING_ELEMENT_VERTEX,
		/**
		 * On a atteint la fin du ficher alors qu'on s'attendait à lire une face d'après le nombre de faces indiquées par l'entête.
		 */
		MISSING_FACE,
		/**
		 * On a commencé à lire les faces et le nombre de points ne correspond pas au nombre indiqué par le premier chiffre de la ligne lue.
		 */
		MISSING_POINT_IN_FACE,
		/**
		 * A a lu toutes les faces mais le fichier comporte encore des lignes. Ca se peut que l'entête est erroné.
		 */
		TOO_MANY_LINES,
		/**
		 * Le fichier ne comporte pas l'extension <i>.ply</i>.
		 */
		BAD_EXTENSION,
		/**
		 * Le fichier n'existe pas.
		 */
		FILE_NONEXISTING,
		/**
		 * On a commencé à lire les Points mais on a trouvé plus que 3 coordonnées/nombres. Ca se peut qu'il y ait un Point manquant et qu'on a alors lu une
		 * Face ou simplement que l'entête est fausse.
		 */
		TOO_MANY_COORDS,
		/**
		 * La première ligne du ficher ne correspond pas à "ply".
		 */
		PLY_NOT_FOUND,
		/**
		 * On a commencé à lire les Faces et elle fait référence à un Point inexistant. Ca se peut qu'il y ait un Point manquant ou que la liste de Points de
		 * cette Face est erronée.
		 */
		POINT_NOT_FOUND;
	}
}
