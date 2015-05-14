package company.taskMan.task;

import java.time.LocalDateTime;

/**
 * This class represents the failed state of a task
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class FailedState implements TaskStatus {
	
	/**
	 * Construct a new failed status
	 * 
	 */
	public FailedState() {
	}

	@Override
	public boolean makeAvailable(Task task) {
		return false;
	}
	
	@Override
	public boolean execute(Task task, LocalDateTime beginTime) {
		return false;
	}

	@Override
	public boolean finish(Task task, LocalDateTime endTime) {
		return false;
	}

	@Override
	public boolean fail(Task task, LocalDateTime endTime) {
		return false;
	}

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
	public boolean isDelegated(){
		return false;
	}
	
	@Override
	public String toString() {
		return "Failed";
	}

	@Override
	public void register(Task task, Dependant d) {
		task.addDependant(d);
	}

	@Override
	public boolean delegate(Task task) {
		return false;
	}

	@Override
	public boolean makeUnavailable(Task task) {
		return false;
	}

}
