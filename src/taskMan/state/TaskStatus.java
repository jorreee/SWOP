package taskMan.state;

import java.time.LocalDateTime;

import taskMan.Planning;
import taskMan.util.Dependant;

public interface TaskStatus {

	public boolean makeAvailable();
	public boolean execute(Planning plan);
	public boolean finish(LocalDateTime endTime);
	public boolean fail(LocalDateTime endTime);
	public boolean register(Dependant d);
	public boolean isAvailable();
	public boolean isUnavailable();
	public boolean isFinished();
	public boolean isFailed();
	public boolean isExecuting();
	
}
