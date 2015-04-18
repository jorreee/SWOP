package taskMan.state;

import java.util.List;

import taskMan.Project;
import taskMan.Task;

/**
 * This class represents the finished state of a project
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class FinishedProject implements ProjectStatus {
	
//	private Project project;
	
	/**
	 * Construct a new finished status
	 * 
	 * @param p
	 *            | The project that this status belongs to
	 */
	public FinishedProject(Project p) {
//		this.project = p;
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
