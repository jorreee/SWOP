package taskMan.state;

import java.time.LocalDateTime;

import taskMan.Task;
import taskMan.util.Dependant;

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
	public String toString() {
		return "Failed";
	}

	@Override
	public boolean register(Task task, Dependant d) {
		task.addDependant(d);
		return true;
	}

}
