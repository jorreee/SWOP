package taskMan.state;

import java.time.LocalDateTime;
import java.util.List;

import taskMan.Task;
import taskMan.util.Dependant;
import taskMan.util.Prerequisite;

public class Available implements TaskStatus {
	
	private Task task;
	
	public Available(Task t) {
		task = t;
	}

	@Override
	public boolean makeAvailable(List<Prerequisite> preLists) {
		return false;
	}

	@Override
	public boolean finish(LocalDateTime beginTime, LocalDateTime endTime) {
		if(isValidTimeStamps(beginTime, endTime)) {

			task.setBeginTime(beginTime);
			task.setEndTime(endTime);
			
			task.setTaskStatus(new FinishedTask(task));
			
			task.notifyDependants();
			
			return true;
		}
		return false;
	}

	@Override
	public boolean fail(LocalDateTime beginTime, LocalDateTime endTime) {
		if(isValidTimeStamps(beginTime, endTime)) {

			task.setBeginTime(beginTime);
			task.setEndTime(endTime);
			
			task.setTaskStatus(new Failed(task));
			
			return true;
		}
		return false;
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
		return "Available";
	}

	@Override
	public boolean register(Dependant d) {
		task.addDependant(d);
		return true;
	}

}
