package taskMan.state;

import java.util.List;

import taskMan.Project;
import taskMan.Task;

public class FinishedProject implements ProjectStatus {
	
	private Project project;
	
	public FinishedProject(Project p) {
		this.project = p;
	}

	@Override
	public boolean finish(List<Task> tasks, Task lastTask) {
		return false;
	}

	@Override
	public boolean isFinished() {
		return true;
	}

	@Override
	public boolean isOngoing() {
		return false;
	}
	
	@Override
	public String toString() {
		return "Finished";
	}

}
