package taskMan.view;

import java.time.LocalDateTime;
import java.util.List;

import taskMan.Project;

public class ProjectView {

	private final Project project;

	public ProjectView(Project p) {
		this.project = p;
	}
	
	public int getID() {
		return project.getProjectID();
	}

	public String getProjectName() {
		return project.getProjectName();
	}

	public String getProjectDescription() {
		return project.getProjectDescription();
	}

	public LocalDateTime getProjectCreationTime() {
		return project.getProjectCreationTime();
	}

	public LocalDateTime getProjectDueTime() {
		return project.getProjectDueTime();
	}

	public LocalDateTime getProjectEndTime() {
		return project.getProjectEndTime();
	}

	public String getProjectStatusAsString() {
		return project.getProjectStatus();
	}

	public List<TaskView> getTasks() {
		return project.getTaskList();
	}

	public List<TaskView> getAvailableTasks() {
		return project.getAvailableTasks();
	}
	
	public int[] getCurrentProjectDelay() {
		return project.getDelay();
	}
	
	public int[] getEstimatedProjectDelay() {
		return project.getEstimatedProjectDelay();
	}
	
	public boolean isProjectEstimatedOnTime() {
		return project.isEstimatedOnTime();
	}
	
	public boolean isProjectFinished() {
		return project.isFinished();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		return true;
		//TODO equals ProjectView
	}

}
