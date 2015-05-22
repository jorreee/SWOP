package company.caretaker;

import company.taskMan.task.DelegatingTaskProxy;

/**
 * A class designed to maintain track of delegated tasks (tasks being delegated
 * to another branch) and their respective delegating proxy. The data kept in
 * these objects is sufficient to recreate broken links during a simulation
 * revert.
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class DelegatedTaskMemento {
	
	private final Integer projectID;
	
	private final Integer taskID;
	
	private final DelegatingTaskProxy delegatingProxy;

	/**
	 * Create a new memento of a delegation
	 * 
	 * @param projectID
	 *            | The project of the delegated task
	 * @param taskID
	 *            | The delegated task
	 * @param delegatingProxy
	 *            | The respective proxy
	 */
	public DelegatedTaskMemento(Integer projectID, Integer taskID,
			DelegatingTaskProxy delegatingProxy) {
		super();
		this.projectID = projectID;
		this.taskID = taskID;
		this.delegatingProxy = delegatingProxy;
	}

	public Integer getProjectID() {
		return projectID;
	}

	public Integer getTaskID() {
		return taskID;
	}

	public DelegatingTaskProxy getDelegatingProxy() {
		return delegatingProxy;
	}
	
	
	
}
