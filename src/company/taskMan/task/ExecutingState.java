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
	public void makeAvailable(Task task) { }

	@Override
	public void delegate(Task task, Task newTask) {
		throw new IllegalStateException("An executing task can't be delegated");
	}
	
	@Override
	public void execute(Task task, LocalDateTime beginTime) 
			throws IllegalArgumentException, IllegalStateException {
		throw new IllegalStateException("The task is already executing");
	}

	@Override
	public void finish(Task task, LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException {
		if(!isValidTimeStamps(task.getBeginTime(), endTime)) {
			throw new IllegalArgumentException("Invalid end time for a task");
		}
		task.setEndTime(endTime);

		task.setTaskStatus(new FinishedState());

		task.notifyFinished();
	}

	@Override
	public void fail(Task task, LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException {
		if(!isValidTimeStamps(task.getBeginTime(), endTime)) {
			throw new IllegalArgumentException("Invalid end time for a task");
		}
		task.setEndTime(endTime);

		task.setTaskStatus(new FailedState());
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
