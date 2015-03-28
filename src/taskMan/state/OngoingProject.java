package taskMan.state;

import java.util.List;

import taskMan.Project;
import taskMan.Task;

public class OngoingProject implements ProjectStatus {
	
	private Project project;
	
	public OngoingProject(Project p) {
		this.project = p;
	}

	@Override
	public boolean finish(List<Task> pre, Task lastTask) {
		if(pre.size() == 0) {
			project.setProjectStatus(new FinishedProject(project));
			project.setEndTime(lastTask.getEndTime());
			return true;
		}
		return false;
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public boolean isOngoing() {
		return !isFinished();
	}
	
	@Override
	public String toString() {
		return "Ongoing";
	}

}
