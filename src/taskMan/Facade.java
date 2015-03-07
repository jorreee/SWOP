package taskMan;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Facade {
	private TaskMan taskMan = new TaskMan();
	
	public void advanceTime(LocalTime time) {
		taskMan.advanceTime(time);;
	}
	
	public LocalDateTime getCurrentTime() { return taskMan.getCurrentTime(); }
	
	public boolean createProject(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime) {
		return taskMan.createProject(name, description, creationTime, dueTime);
	}
	
	public boolean createTask(int projectID, String description, LocalTime estimatedDuration, float acceptableDeviation) {
		return taskMan.createTask(projectID, description, estimatedDuration, acceptableDeviation);
	}
	
	public boolean createFinishedTask(int projectID, String description, LocalTime estimatedDuration, float acceptableDeviation, TaskStatus taskStatus, LocalDateTime startTime, LocalDateTime endTime) {
		return taskMan.createFinishedTask(projectID, description, estimatedDuration, acceptableDeviation, taskStatus, startTime, endTime);
	}
	
	public boolean updateTaskDetails(int projectID, int taskID, LocalDateTime startTime, LocalDateTime endTime, TaskStatus taskStatus) {
		return taskMan.updateTaskDetails(projectID, taskID, startTime, endTime, taskStatus);
	}
	
	public String getProjectName(int projectID) {
		return taskMan.getProjectName(projectID);
	}
	
	public String getProjectDescription(int projectID) {
		return taskMan.getProjectDescription(projectID);
	}
	
	public LocalDateTime getProjectCreationTime(int projectID) {
		return taskMan.getProjectCreationTime(projectID);
	}
	
	public LocalDateTime getProjectDueTime(int projectID) {
		return taskMan.getProjectDueTime(projectID);
	}
	
	public LocalDateTime getProjectEndTime(int projectID) {
		return taskMan.getProjectEndTime(projectID);
	}
	
	public ProjectStatus getProjectStatus(int projectID) {
		return taskMan.getProjectStatus(projectID);
	}
}
