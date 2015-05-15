package userInterface;

public class TaskManException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TaskManException(String message) {
		super(message);
	}

	public TaskManException(Throwable nested) {
		super(nested);
	}

}
