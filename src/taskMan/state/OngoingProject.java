package taskMan.state;

import java.util.List;

import taskMan.Project;
import taskMan.Task;

/**
 * This class represents the ongoing state of a project
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class OngoingProject implements ProjectStatus {
	
	/**
	 * Construct a new ongoing status
	 * 
	 */
	public OngoingProject() {
	}

	@Override
	public boolean finish(Project project, List<Task> tasks, Task lastTask) {
		for (Task t : tasks) {
			if(!t.hasFinishedEndpoint()) {
				return false;
			}
		}
		project.setProjectStatus(new FinishedProject());
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
