package company.taskMan.project;

import company.taskMan.task.Task;

/**
 * This class represents the finished state of a project
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class FinishedProject implements ProjectStatus {
	
	/**
	 * Construct a new finished status
	 * 
	 */
	public FinishedProject() {
	}

	@Override
	public void finish(Project project, Task lastTask) { }

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
