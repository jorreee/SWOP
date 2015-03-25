package taskMan.state;

import java.time.LocalDateTime;

import taskMan.Task;

public class Finished implements TaskStatus {

	private Task task;

	public Finished(Task t) {
		task = t;
	}

	@Override
	public boolean shouldBecomeAvailable(int numberOfPendingPrerequisites) {
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
	public String toString() {
		return "Finished";
	}

}
