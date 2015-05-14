package company.taskMan.task;

import java.time.LocalDateTime;

/**
 * This class represents the failed state of a task
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class FailedTask implements TaskStatus {
	
	/**
	 * Construct a new failed status
	 * 
	 */
	public FailedTask() {
	}

	@Override
	public void makeAvailable(Task task) { }
	
	@Override
	public void execute(Task task, LocalDateTime beginTime) 
				throws IllegalArgumentException, IllegalStateException { }

	@Override
	public void finish(Task task, LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException{ }

	@Override
	public void fail(Task task, LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException { }

	@Override
	public boolean isAvailable() {
		return false;
	}

	@Override
	public boolean isUnavailable() {
		return false;
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public boolean isFailed() {
		return true;
	}
	
	@Override
	public boolean isExecuting() {
		return false;
	}
	
	@Override
	public String toString() {
		return "Failed";
	}

	@Override
	public boolean register(Task task, Dependant d) {
		return true;
	}

}
