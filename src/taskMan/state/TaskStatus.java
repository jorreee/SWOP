package taskMan.state;

import java.time.LocalDateTime;
import java.util.List;

import taskMan.Task;
import taskMan.util.Dependant;

public interface TaskStatus {

	public boolean makeAvailable(List<Task> preList);
	public boolean finish(LocalDateTime beginTime, LocalDateTime endTime);
	public boolean fail(LocalDateTime beginTime, LocalDateTime endTime);
	public boolean register(Dependant d);
	public boolean isAvailable();
	public boolean isUnavailable();
	public boolean isFinished();
	public boolean isFailed();
	
}
