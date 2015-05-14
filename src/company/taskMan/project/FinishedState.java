package company.taskMan.project;

import java.util.List;

import company.taskMan.task.Task;

/**
 * This class represents the finished state of a project
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class FinishedState implements ProjectStatus {
	
	/**
	 * Construct a new finished status
	 * 
	 */
	public FinishedState() {
	}

	@Override
	public boolean finish(Project project, List<Task> tasks, Task lastTask) {
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
