package exceptions;

public class UnexpectedViewContentException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public UnexpectedViewContentException(String s) {
		super(s);
	}

}
