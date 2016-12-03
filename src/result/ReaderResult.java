package result;

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
	
}
