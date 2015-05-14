package exceptions;

import java.rmi.UnexpectedException;

public class ResourceUnavailableException extends UnexpectedException {

	private static final long serialVersionUID = 1L;

	public ResourceUnavailableException(String s) {
		super(s);
	}

}
