package company.taskMan.task;

import java.time.LocalDateTime;

/**
 * This class represents the finished state of a task
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class FinishedTask implements TaskStatus {

	/**
	 * Construct a new finished status
	 * 
	 */
	public FinishedTask() {
	}

	@Override
	public void makeAvailable(Task task) { }
	
	@Override
	public void execute(Task task, LocalDateTime beginTime) 
			throws IllegalArgumentException, IllegalStateException { }

	@Override
	public void finish(Task task, LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException { }

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
	public boolean register(Task task, Dependant d) {
		task.notifyDependants();
		return true;
	}
	
	@Override
	public String toString() {
		return "Finished";
	}

}
