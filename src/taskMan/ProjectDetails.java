package taskMan;

import java.time.LocalDateTime;

import com.google.common.collect.ImmutableList;

@Deprecated
public class ProjectDetails extends Detail {
	
	private final ImmutableList<Task> taskList;
	private final String projectName;
	private final String description;
	private final LocalDateTime creationTime;
	private final LocalDateTime dueTime;
	private final LocalDateTime endTime;
	private final ProjectStatus projectStatus;
	private final int projectID;

	public ProjectDetails(ImmutableList<Task> taskList, String projectName,
			String description, LocalDateTime creationTime,
			LocalDateTime dueTime, LocalDateTime endTime,
			ProjectStatus projectStatus, int projectID) {
		super();
		this.taskList = taskList;
		this.projectName = projectName;
		this.description = description;
		this.creationTime = creationTime;
		this.dueTime = dueTime;
		this.endTime = endTime;
		this.projectStatus = projectStatus;
		this.projectID = projectID;
	}

	public ImmutableList<Task> getTaskList() {
		return taskList;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getDescription() {
		return description;
	}

	public LocalDateTime getCreationTime() {
		return creationTime;
	}

	public LocalDateTime getDueTime() {
		return dueTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public ProjectStatus getProjectStatus() {
		return projectStatus;
	}

	public int getProjectID() {
		return projectID;
	}
	
	
}
