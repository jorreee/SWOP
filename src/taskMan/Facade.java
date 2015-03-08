package taskMan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import userInterface.IFacade;

public class Facade implements IFacade {
	private TaskMan taskMan = new TaskMan();
	
	public void advanceTime(LocalTime time) {
		taskMan.advanceTime(time);;
	}
	
	public LocalDateTime getCurrentTime() { return taskMan.getCurrentTime(); }
	
	public boolean createProject(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime) {
		return taskMan.createProject(name, description, creationTime, dueTime);
	}
	
	public boolean createTask(int projectID, String description, LocalTime estimatedDuration, int acceptableDeviation, String taskStatus, Integer alternativeFor, List<Integer> prerequisiteTasks, LocalDateTime startTime, LocalDateTime endTime) {
		return taskMan.createTask(projectID, description, estimatedDuration, acceptableDeviation, taskStatus, alternativeFor, prerequisiteTasks, startTime, endTime);	}
	
	public boolean updateTaskDetails(int projectID, int taskID, LocalDateTime startTime, LocalDateTime endTime, String taskStatus) {
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
	
	public String getProjectStatus(int projectID) {
		return taskMan.getProjectStatus(projectID);
	}

}
