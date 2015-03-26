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
		return project.getTasks();
	}

	public List<TaskView> getAvailableTasks() {
		return project.getAvailableTaskViews();
	}
	
	public int[] getCurrentProjectDelay(LocalDateTime time) {
		return project.getDelay(time);
	}
	
	public int[] getEstimatedProjectDelay(LocalDateTime time) {
		return project.getEstimatedProjectDelay(time);
	}
	
	public boolean isProjectEstimatedOnTime(LocalDateTime time) {
		return project.isEstimatedOnTime(time);
	}
	
	public boolean isProjectFinished() {
		return project.isFinished();
	}

	//TODO eh...
	@Override
	public boolean equals(Object obj) {
		return project.equals(obj);
	}
	
	public boolean hasAsProject(Project p) {
		return project == p;
//		return project.equals(p); ?
	}

}
