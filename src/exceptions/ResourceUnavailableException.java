package exceptions;

import java.rmi.UnexpectedException;

/**
 * An exception to be used when a resource is not available
 * 
 * @author Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class ResourceUnavailableException extends UnexpectedException {

	private static final long serialVersionUID = 1L;

	public ResourceUnavailableException(String s) {
		super(s);
	}

}
