package company.taskMan.task;

import java.time.LocalDateTime;

/**
 * This class represents the executing state of a task
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class ExecutingState implements TaskStatus{
	
	/**
	 * Construct a new executing status
	 * 
	 */
	public ExecutingState() {
	}

	@Override
	public boolean makeAvailable(Task task) {
		return false;
	}
	
	@Override
	public boolean execute(Task task, LocalDateTime beginTime) {
		return false;
	}

	@Override
	public boolean finish(Task task, LocalDateTime endTime) {
		if(!isValidTimeStamps(task.getBeginTime(), endTime)) {
			return false;
		}
		if(!task.setEndTime(endTime)) {
			return false;
		}

		task.setTaskStatus(new FinishedState());

		task.notifyDependants();

		return true;
	}

	@Override
	public boolean fail(Task task, LocalDateTime endTime) {
		if(!isValidTimeStamps(task.getBeginTime(), endTime)) {
			return false;
		}
		if(!task.setEndTime(endTime)) {
			return false;
		}

		task.setTaskStatus(new FailedState());

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
	public boolean isDelegated(){
		return false;
	}
	
	@Override
	public String toString() {
		return "Executing";
	}

	@Override
	public void register(Task task, Dependant d) {
		task.addDependant(d);
	}
}
