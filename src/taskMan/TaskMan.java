package taskMan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

//TODO Still not done
//TODO 
public class TaskMan {
	
	private ArrayList<Project> projectList;
	private LocalDateTime currentTime;
	//private Project currentProject;
	
	public void advanceTime(LocalDateTime time) {
		currentTime = currentTime.plusHours(time.getHour()).plusMinutes(time.getMinute());
	}
	
	private Project getProject(int projectID) {
		return projectList.get(projectID);
	}
		
	public LocalDateTime getCurrentTime() { return currentTime; }
	
	public boolean createProject(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime) {
		Project project = new Project(projectList.size(), name, description, creationTime, dueTime);
		return projectList.add(project);
	}
	
	public boolean createTask(int projectID, String description, LocalTime estimatedDuration, int acceptableDeviation, String taskStatus, Integer alternativeFor, List<Integer> prerequisiteTasks, LocalDateTime startTime, LocalDateTime endTime) {
		return getProject(projectID).createTask(description, estimatedDuration, acceptableDeviation, taskStatus, alternativeFor, prerequisiteTasks, startTime, endTime);
	}
	
	public boolean updateTaskDetails(int projectID, int taskID, LocalDateTime startTime, LocalDateTime endTime, String taskStatus) {
		return getProject(projectID).updateTaskDetails(taskID, startTime, endTime, taskStatus);
	}
	
	public String getProjectName(int projectID) {
		return getProject(projectID).getProjectName();
	}
	
	public String getProjectDescription(int projectID) {
		return getProject(projectID).getProjectDescription();
	}
	
	public LocalDateTime getProjectCreationTime(int projectID) {
		return getProject(projectID).getProjectCreationTime();
	}
	
	public LocalDateTime getProjectDueTime(int projectID) {
		return getProject(projectID).getProjectDueTime();
	}
	
	public LocalDateTime getProjectEndTime(int projectID) {
		return getProject(projectID).getProjectEndTime();
	}
	
	public String getProjectStatus(int projectID) {
		return getProject(projectID).getProjectStatus();
	}
	
	
}