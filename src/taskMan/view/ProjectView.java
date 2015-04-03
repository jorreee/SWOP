package taskMan.view;

import java.time.LocalDateTime;
import java.util.List;

import taskMan.Project;

import com.google.common.collect.ImmutableList;

public class ProjectView {

	private final Project project;

	public ProjectView(Project p) {
		this.project = p;
	}
	
	public int getID() {
		return project.getID();
	}

	public String getName() {
		return project.getName();
	}

	public String getDescription() {
		return project.getDescription();
	}

	public LocalDateTime getCreationTime() {
		return project.getCreationTime();
	}

	public LocalDateTime getDueTime() {
		return project.getDueTime();
	}

	public LocalDateTime getEndTime() {
		return project.getEndTime();
	}

	public String getStatusAsString() {
		return project.getStatus();
	}

	public List<TaskView> getTasks() {
		ImmutableList.Builder<TaskView> tasks = ImmutableList.builder();
		tasks.addAll(project.getTaskViews());
		return tasks.build();
	}

	public List<TaskView> getAvailableTasks() {
		ImmutableList.Builder<TaskView> tasks = ImmutableList.builder();
		tasks.addAll(project.getAvailableTaskViews());
		return tasks.build();
	}
	
	public int[] getCurrentDelay(LocalDateTime time) {
		return project.getDelay(time);
	}
	
	public int[] getEstimatedDelay(LocalDateTime time) {
		return project.getEstimatedDelay(time);
	}
	
	public boolean isEstimatedOnTime(LocalDateTime time) {
		return project.isEstimatedOnTime(time);
	}
	
	public boolean isFinished() {
		return project.isFinished();
	}
	
	public boolean hasAsProject(Project p) {
		return project == p;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectView other = (ProjectView) obj;
		return other.hasAsProject(project);
	}

	public List<TaskView> getUnplannedTasks() {
		// TODO Auto-generated method stub
		return null;
	}

}
