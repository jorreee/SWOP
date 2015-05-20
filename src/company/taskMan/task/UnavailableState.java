package company.taskMan.task;

import java.time.LocalDateTime;

/**
 * This class represents the unavailable state of a task
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class UnavailableState implements TaskStatus {

	/**
	 * Construct a new unavailable status
	 */
	public UnavailableState() {	}

	@Override
	public void makeAvailable(Task task) {
		for(Task t : task.getPrerequisites()) {
			if(!t.hasFinishedEndpoint()) {
				return; //unfulfilled prereqs
			}
		}
		task.notifyPlannable();
		if(task.getPlannedBeginTime() == null) {
			return; //not planned
		}
		task.setTaskStatus(new AvailableState());
	}

	@Override
	public void delegate(Task task, Task newTask) 
			throws IllegalArgumentException, IllegalStateException {
		if(newTask == null) {
			throw new IllegalArgumentException("newTask must not be null");
		}
		if(task.getPlannedBeginTime() != null) {
			throw new IllegalStateException("This task is already planned in its current branch and cannot be delegated");
		}
		if(!task.equals(newTask)) {
//			Task oldDelegator = task.getDelegatingTask();
//			if(oldDelegator != null) {
//				oldDelegator.unregister(task); //TODO hier kan nullpointer komen als de delegating task eerst wordt verwijderd
//			}
//			task.setDelegatingTask(newTask);
//			try {
//				newTask.setOriginalDelegatedTask(task);
//			} catch(IllegalStateException e) {
//				task.setDelegatingTask(oldDelegator);
//				throw e;
//			}
			newTask.register(task);
			task.setTaskStatus(new DelegatedState());
		} //else doe niks alles is okee
		
	}
	
	@Override
	public void execute(Task task, LocalDateTime beginTime) 
			throws IllegalArgumentException, IllegalStateException { 
		throw new IllegalStateException("An unavailable task can't start executing");
	}

	@Override
	public void finish(Task task, LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException { 
		throw new IllegalStateException("An unavailable task can't finish");
	}

	@Override
	public void fail(Task task, LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException { 
		throw new IllegalStateException("An unavailable task can't fail");
	}

	@Override
	public boolean isAvailable() {
		return false;
	}

	@Override
	public boolean isUnavailable() {
		return true;
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
	}
	
	@Override
	public String toString() {
		return "Unavailable";
	}
	
}
