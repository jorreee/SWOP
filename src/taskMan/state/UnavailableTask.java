package taskMan.state;

import java.time.LocalDateTime;

import taskMan.Task;
import taskMan.util.Dependant;

public class UnavailableTask implements TaskStatus {

	private Task task;

	public UnavailableTask(Task t) {
		task = t;
	}

	@Override
	public boolean makeAvailable() {
		for(Task t : task.getPrerequisites()) {
			if(!t.hasFinishedEndpoint()) {
				return false;
			}
		}
		task.setTaskStatus(new AvailableTask(task));
		return true;
	}

	@Override
	public boolean finish(LocalDateTime beginTime, LocalDateTime endTime) {
		return false;
	}

	@Override
	public boolean fail(LocalDateTime beginTime, LocalDateTime endTime) {
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
