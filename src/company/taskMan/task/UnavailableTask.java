package company.taskMan.task;

import java.time.LocalDateTime;

/**
 * This class represents the unavailable state of a task
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class UnavailableTask implements TaskStatus {

	/**
	 * Construct a new unavailable status
	 */
	public UnavailableTask() {
	}

	@Override
	public void makeAvailable(Task task) {
		for(Task t : task.getPrerequisites()) {
			if(!t.hasFinishedEndpoint()) {
				return; //unfulfilled prereqs
			}
		}
		if(task.getPlannedBeginTime() == null) {
			return; //not planned
		}
		task.setTaskStatus(new AvailableTask());
	}
	
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
	public boolean register(Task task, Dependant d) {
		return true;
	}
	
	@Override
	public String toString() {
		return "Unavailable";
	}
	
}
