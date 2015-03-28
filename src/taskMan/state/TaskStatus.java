package taskMan.state;

import java.time.LocalDateTime;
import java.util.List;

import taskMan.util.Dependant;
import taskMan.util.Prerequisite;

public interface TaskStatus {

	public boolean shouldBecomeAvailable(List<Prerequisite> preList);
	public boolean canFinish(LocalDateTime beginTime, LocalDateTime endTime);
	public boolean canFail(LocalDateTime beginTime, LocalDateTime endTime);
	public boolean register(Dependant d);
	public boolean isAvailable();
	public boolean isUnavailable();
	public boolean isFinished();
	public boolean isFailed();
	
}
