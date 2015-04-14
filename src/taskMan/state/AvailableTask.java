package taskMan.state;

import java.time.LocalDateTime;

import taskMan.Task;
import taskMan.util.Dependant;

public class AvailableTask implements TaskStatus {
	
	private Task task;
	
	public AvailableTask(Task t) {
		task = t;
	}

	@Override
	public boolean makeAvailable() {
		return false;
	}
	
	@Override
	public boolean execute(LocalDateTime beginTime) {
		if(beginTime.isBefore(task.getPlannedBeginTime()) || beginTime.isAfter(task.getPlannedEndTime())) {
			if(!task.refreshReservations(beginTime)) {
				return false;
			}
			if(!task.setBeginTime(beginTime)) {
				return false;
			}
			return true;
		}
		return task.setBeginTime(beginTime);
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
	public boolean isExecuting() {
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
