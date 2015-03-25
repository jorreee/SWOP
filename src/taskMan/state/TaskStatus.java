package taskMan.state;

import java.time.LocalDateTime;

public interface TaskStatus {

	public boolean shouldBecomeAvailable(int numberOfPendingPrerequisites);
	public boolean canFinish(LocalDateTime beginTime, LocalDateTime endTime);
	public boolean canFail(LocalDateTime beginTime, LocalDateTime endTime);
	public boolean isAvailable();
	public boolean isUnavailable();
	public boolean isFinished();
	public boolean isFailed();
	
}
