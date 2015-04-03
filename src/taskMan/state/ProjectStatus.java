package taskMan.state;

import java.util.List;

import taskMan.Task;

public interface ProjectStatus {
	
	public boolean finish(List<Task> tasks, Task lastTask);
	public boolean isFinished();
	public boolean isOngoing();
	
}
