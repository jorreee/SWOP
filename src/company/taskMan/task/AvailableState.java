package company.taskMan.task;

import java.time.LocalDateTime;

/**
 * This class represents the available state of a task
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class AvailableState implements TaskStatus {
	
	/**
	 * Construct a new available status
	 * 
	 */
	public AvailableState() {
	}

	@Override
	public void makeAvailable(Task task) { }

	@Override
	public void delegate(Task task, boolean real) { 
		throw new IllegalStateException("An available task can't be delegated");
	}
	
	@Override
	public void execute(Task task, LocalDateTime beginTime) 
			throws IllegalArgumentException, IllegalStateException {
		task.setBeginTime(beginTime);
		task.setTaskStatus(new ExecutingState());
	}

	@Override
	public void finish(Task task, LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException { 
		throw new IllegalStateException("An available task can't finish");
	}

	@Override
	public void fail(Task task, LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException { 
		throw new IllegalStateException("An available task can't fail");
	}
	
	@Override
	public boolean isAvailable() {
		return true;
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
		return false;
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
		return "Available";
	}

	@Override
	public void register(Task task, Dependant d) {
		task.addDependant(d);
	}

}
