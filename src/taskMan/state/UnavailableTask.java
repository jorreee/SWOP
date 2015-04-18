package taskMan.state;

import java.time.LocalDateTime;

import taskMan.Task;
import taskMan.util.Dependant;

/**
 * This class represents the unavailable state of a task
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class UnavailableTask implements TaskStatus {

	private Task task;

	/**
	 * Construct a new unavailable status
	 * 
	 * @param t
	 *            | The task that this status belongs to
	 */
	public UnavailableTask(Task t) {
		task = t;
	}

	@Override
	public boolean makeAvailable() {
		for(Task t : task.getPrerequisites()) {
			if(!t.hasFinishedEndpoint()) {
				return false; //unfulfilled prereqs
			}
		}
		if(task.getPlannedBeginTime() == null) {
			return false; //not planned
		}
		task.setTaskStatus(new AvailableTask(task));
		return true;
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
	public boolean register(Dependant d) {
		task.addDependant(d);
		return true;
	}
	
	@Override
	public String toString() {
		return "Unavailable";
	}
	
}
