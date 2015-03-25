package taskMan.state;

import java.time.LocalDateTime;

import taskMan.Task;

public class Unavailable implements TaskStatus {

	private Task task;

	public Unavailable(Task t) {
		task = t;
	}

	@Override
	public boolean shouldBecomeAvailable(int numberOfPendingPrerequisites) {
		if(numberOfPendingPrerequisites == 0) {
			return true;
		} else if(numberOfPendingPrerequisites < 0) {
			System.out.println("#### ERROR: te veel dependencies afgehandeld");
		}
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
	public String toString() {
		return "Unavailable";
	}
	
}
