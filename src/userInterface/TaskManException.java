package userInterface;

@SuppressWarnings("serial")
/**
 * <code>Facade</code> is not allowed to throw exceptions except for <code>TaskManException</code>.
 * 
 * Do not use TaskManException outside of <code>Facade</code>.
 */
public class TaskManException extends RuntimeException {
  public TaskManException(String message) {
    super(message);
  }

  public TaskManException(Throwable nested) {
    super(nested);
  }
}
