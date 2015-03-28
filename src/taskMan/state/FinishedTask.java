package taskMan.state;

import java.time.LocalDateTime;
import java.util.List;

import taskMan.Task;
import taskMan.util.Dependant;

public class FinishedTask implements TaskStatus {

	private Task task;

	public FinishedTask(Task t) {
		task = t;
	}

	@Override
	public boolean makeAvailable(List<Task> preList) {
		return false;
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
		return false;
	}

	@Override
	public boolean isFinished() {
		return true;
	}

	@Override
	public boolean isFailed() {
		return false;
	}

	@Override
	public boolean register(Dependant d) {
		task.addDependant(d);
		task.notifyDependants();
		return true;
	}
	
	@Override
	public String toString() {
		return "Finished";
	}

}
