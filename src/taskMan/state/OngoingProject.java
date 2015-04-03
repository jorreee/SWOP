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
	public boolean finish(List<Task> tasks, Task lastTask) {
		for (Task t : tasks) {
			if(!t.hasFinishedEndpoint()) {
				return false;
			}
		}
		project.setProjectStatus(new FinishedProject(project));
		project.setEndTime(lastTask.getEndTime());
		return true;
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
