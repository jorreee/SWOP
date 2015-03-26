package taskMan.state;

import java.time.LocalDateTime;
import java.util.List;

import taskMan.Task;
import taskMan.util.Dependant;
import taskMan.util.Prerequisite;

public class Failed implements TaskStatus {
	
	private Task task;
	
	public Failed(Task t) {
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
		return false;
	}

	@Override
	public boolean isFailed() {
		return true;
	}
	
	@Override
	public String toString() {
		return "Failed";
	}

	@Override
	public List<Task> adoptDependants() {
		List<Task> deps = task.getDependants();
		for(Task t : deps) {
			t.unregister(task);
		}
		return deps;
	}

	@Override
	public boolean register(Dependant d) {
		task.addDependant(d);
		return true;
	}

}
