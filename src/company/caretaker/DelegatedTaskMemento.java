package company.caretaker;

import company.taskMan.task.DelegatingTaskProxy;

public class DelegatedTaskMemento {
	
	private final Integer projectID;
	
	private final Integer taskID;
	
	private final DelegatingTaskProxy delegatingProxy;

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
