package taskMan.state;

import java.time.LocalDateTime;

import taskMan.Task;

public class Failed implements TaskStatus {
	
	private Task task;
	
	public Failed(Task t) {
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

}
