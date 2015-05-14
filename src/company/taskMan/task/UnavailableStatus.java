package company.taskMan.task;

import java.time.LocalDateTime;

/**
 * This class represents the unavailable state of a task
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class UnavailableStatus implements TaskStatus {

	/**
	 * Construct a new unavailable status
	 */
	public UnavailableStatus() {
	}

	@Override
	public boolean makeAvailable(Task task) {
		for(Task t : task.getPrerequisites()) {
			if(!t.hasFinishedEndpoint()) {
				return false; //unfulfilled prereqs
			}
		}
		if(task.getPlannedBeginTime() == null) {
			return false; //not planned
		}
		task.setTaskStatus(new AvailableState());
		return true;
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
