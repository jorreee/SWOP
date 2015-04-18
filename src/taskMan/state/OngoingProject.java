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
	
	private Project project;
	
	/**
	 * Construct a new ongoing status
	 * 
	 * @param p
	 *            | The project that this status belongs to
	 */
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
