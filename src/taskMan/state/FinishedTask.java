package taskMan.state;

import java.time.LocalDateTime;

import taskMan.Task;
import taskMan.util.Dependant;

/**
 * This class represents the finished state of a task
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class FinishedTask implements TaskStatus {

	private Task task;

	/**
	 * Construct a new finished status
	 * 
	 * @param t
	 *            | The task that this status belongs to
	 */
	public FinishedTask(Task t) {
		task = t;
	}

	@Override
	public boolean makeAvailable() {
		return false;
	}
	
	@Override
	public boolean execute(LocalDateTime beginTime) {
		return false;
	}

	@Override
	public boolean finish(LocalDateTime endTime) {
		return false;
	}

	@Override
	public boolean fail(LocalDateTime endTime) {
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
		return true;
	}

	@Override
	public boolean isFailed() {
		return false;
	}
	
	@Override
	public boolean isExecuting(){
		return false;
	}

	@Override
	public boolean register(Dependant d) {
		task.addDependant(d);
		task.notifyDependants();
		return true;
	}
	
	@Override
	public String toString() {
		return "Finished";
	}

}
