package taskMan.state;

import java.time.LocalDateTime;
import java.util.List;

import taskMan.Task;
import taskMan.util.Dependant;
import taskMan.util.Prerequisite;

public class FinishedTask implements TaskStatus {

	private Task task;

	public FinishedTask(Task t) {
		task = t;
	}

	@Override
	public boolean shouldBecomeAvailable(List<Prerequisite> preList) {
		return false;
	}

	@Override
	public boolean canFinish(LocalDateTime beginTime, LocalDateTime endTime) {
		return false;
	}

	@Override
	public boolean canFail(LocalDateTime beginTime, LocalDateTime endTime) {
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
