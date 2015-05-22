package exceptions;

/**
 * An exception to be thrown when the contents of a View (BranchView,
 * ProjectView, TaskView, ...) are not recognized by the object unwrapping the
 * view
 * 
 * @author Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class UnexpectedViewContentException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public UnexpectedViewContentException(String s) {
		super(s);
	}

}
