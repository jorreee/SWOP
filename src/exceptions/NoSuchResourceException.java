package exceptions;

public class NoSuchResourceException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public NoSuchResourceException(String s) {
		super(s);
	}

}
