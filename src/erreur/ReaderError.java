package erreur;

/**
 * Type d'erreur correspondant à une erreur de {@link Lecture}
 * @author L3
 *
 */
public class ReaderError extends MethodResult{

	/**
	 * Crée une {@link MethodResult} de ce type, {@link ReaderError}.
	 * @param readerEror
	 */
	public ReaderError(ReaderErrorEnum readerEror) {
		result = readerEror;
	}
	
}
