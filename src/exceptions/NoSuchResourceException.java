package exceptions;

/**
 * An exception to be used if a resource does not exist
 * 
 * @author Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class NoSuchResourceException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public NoSuchResourceException(String s) {
		super(s);
	}

}
