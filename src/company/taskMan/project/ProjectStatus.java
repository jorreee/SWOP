package company.taskMan.project;

import company.taskMan.task.Task;

/**
 * The status of a project is contained in classes implementing this interface.
 * This follows the state pattern. A project will ask its status to change to
 * another and the implementation will determine if it is possible to change.
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public interface ProjectStatus {

	/**
	 * Change the status to a finished status
	 * 
	 * @param project
	 *            | The project to finish
	 * @param lastTask
	 *            | The last task to end
	 */
	public void finish(Project project, Task lastTask);

	/**
	 * Check whether or not the project is finished
	 * 
	 * @return True when this state is a finished state, false otherwise
	 */
	public boolean isFinished() throws IllegalStateException;

	/**
	 * Check whether or not the project is still ongoing
	 * 
	 * @return True when this state is an ongoing state, false otherwise
	 */
	public boolean isOngoing();

}
