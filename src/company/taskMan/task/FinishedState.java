package company.taskMan.task;

import java.time.LocalDateTime;

/**
 * This class represents the finished state of a task
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class FinishedState implements TaskStatus {

	/**
	 * Construct a new finished status
	 * 
	 */
	public FinishedState() {
	}

	@Override
	public void makeAvailable(Task task) { }

	@Override
	public void delegate(Task task, DelegatingTaskProxy newTask) { 
		throw new IllegalStateException("This task has already failed and no one can help you anymore");
	}
	
	@Override
	public void execute(Task task, LocalDateTime beginTime) 
			throws IllegalArgumentException, IllegalStateException { 
		throw new IllegalStateException("This task has already finished and you don't need to do it again");
	}

	@Override
	public void finish(Task task, LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException { 
		throw new IllegalStateException("This task has already finished and everyone is already happy fory you");
	}

	@Override
	public void fail(Task task, LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException { 
		throw new IllegalStateException("This task has already finished and there's no denying it");
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
	public boolean isDelegated(){
		return false;
	}

	@Override
	public void register(Task task, Dependant d) {
		task.addDependant(d);
		task.notifyFinished();
	}
	
	@Override
	public String toString() {
		return "Finished";
	}

}
