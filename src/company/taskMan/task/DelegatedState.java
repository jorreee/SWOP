package company.taskMan.task;

import java.time.LocalDateTime;


/**
 * This class represents the delegated state of a task
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class DelegatedState implements TaskStatus {

	@Override
	public void makeAvailable(Task task) { 
		
	}

	@Override
	public void delegate(Task task, boolean real)
			throws IllegalArgumentException, IllegalStateException {
		if(!real) {
			task.setTaskStatus(new UnavailableState());
		} else {
			throw new IllegalStateException("This task is already delegated!");
		}
	}

	@Override
	public void execute(Task task, LocalDateTime beginTime) 
			throws IllegalArgumentException, IllegalStateException { 
		throw new IllegalStateException("This task is delegated and you can't access it anymore");
	}

	@Override
	public void finish(Task task, LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException { 
//		throw new IllegalStateException("This task is delegated and you don't have control anymore");
		finished = true;

		task.setEndTime(endTime);
		
		task.notifyFinished();
	}
	
	@Override
	public void fail(Task task, LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException { 
		throw new IllegalStateException("This task is delegated and you don't have control anymore");
	}

	@Override
	public void register(Task task, Dependant d) {
		task.addDependant(task);
	}

	@Override
	public boolean isAvailable() {
		return false;
	}

	@Override
	public boolean isUnavailable() {
		return false;
	}

	private boolean finished = false;
	@Override
	public boolean isFinished() {
		return finished;
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
		return true;
	}
	
	@Override
	public String toString() {
		return "Delegated";
	}

}
