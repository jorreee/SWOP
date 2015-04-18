package taskMan.state;

import java.time.LocalDateTime;

import taskMan.Task;
import taskMan.util.Dependant;

/**
 * This class represents the executing state of a task
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class ExecutingTask implements TaskStatus{
	
	private Task task;
	
	/**
	 * Construct a new executing status
	 * 
	 * @param t
	 *            | The task that this status belongs to
	 */
	public ExecutingTask(Task t) {
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
		if(!isValidTimeStamps(task.getBeginTime(), endTime)) {
			return false;
		}
		if(!task.setEndTime(endTime)) {
			return false;
		}

		task.setTaskStatus(new FinishedTask(task));

		task.notifyDependants();

		return true;
	}

	@Override
	public boolean fail(LocalDateTime endTime) {
		if(!isValidTimeStamps(task.getBeginTime(), endTime)) {
			return false;
		}
		if(!task.setEndTime(endTime)) {
			return false;
		}

		task.setTaskStatus(new FailedTask(task));

		return true;
	}
	
	/**
	 * Checks whether the given timestamps are valid as start- and endtimes
	 * 
	 * @param 	beginTime
	 * 			The new begin time of the Task.
	 * @param 	endTime
	 * 			The new end time of the Task.
	 * @return	True if and only if the timestamps are valid start- and endtimes.
	 */
	private boolean isValidTimeStamps(LocalDateTime beginTime, LocalDateTime endTime) {
		if(beginTime == null || endTime == null) {
			return false;
		}
		if(endTime.isBefore(beginTime)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isAvailable() {
		return false;
	}
	
	@Override
	public boolean isExecuting() {
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
	public String toString() {
		return "Executing";
	}

	//TODO needs to be specified
	@Override
	public boolean register(Dependant d) {
		task.addDependant(d);
		return true;
	}
}
