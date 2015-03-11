package taskMan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//TODO Still not done
//TODO Update Task Details: start en endtime moeten in het verleden liggen
public class TaskMan {
	
	private ArrayList<Project> projectList = new ArrayList<>();
	private LocalDateTime currentTime = LocalDateTime.now();
	//private Project currentProject;
	
	public TaskMan(){
		
	}
	
	public TaskMan(LocalDateTime time){
		currentTime = time;
	}
	
	public boolean advanceTimeTo(LocalDateTime time) {
		if (time.isAfter(currentTime)) {
			currentTime = time;
			return true;
		}
		else return false;
		
	}
	
	private Project getProject(int projectID) {
		return projectList.get(projectID);
	}
		
	public LocalDateTime getCurrentTime() { return currentTime; }
	
	public boolean createProject(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime) {
		Project project = new Project(projectList.size(), name, description, creationTime, dueTime);
		return projectList.add(project);
	}
	
	public boolean createProject(String name, String description, LocalDateTime dueTime) {
		Project project = new Project(projectList.size(), name, description, currentTime, dueTime);
		return projectList.add(project);
	}
	
	public boolean createTask(int projectID, String description, int estimatedDuration, int acceptableDeviation, String taskStatus, Integer alternativeFor, List<Integer> prerequisiteTasks, LocalDateTime startTime, LocalDateTime endTime) {
		return getProject(projectID).createTask(description, estimatedDuration, acceptableDeviation, taskStatus, alternativeFor, prerequisiteTasks, startTime, endTime);
	}
	
	public boolean createTask(int projectID, String description, int estimatedDuration, int acceptableDeviation, Integer alternativeFor, List<Integer> prerequisiteTasks) {
		return getProject(projectID).createTask(description,estimatedDuration, acceptableDeviation, alternativeFor, prerequisiteTasks);
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
	
	//TODO als er geen delay is, geef 0 terug
	public int getProjectDelay(int projectID){
		return getProject(projectID).getDelay(currentTime);

	}
	
	public HashMap<Integer, List<Integer>> getAvailableTasks() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<Integer> getAvailableTasks(int projectID) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getProjectAmount() {
		return projectList.size();
	}

	public boolean isOnTime(int projectID) {
		// TODO Auto-generated method stub
		return false;
	}

	public int getTaskAmount(int projectID) {
		return projectList.get(projectID).getTaskAmount();
	}

	public String getTaskDescription(int projectID, int taskID) {
		return projectList.get(projectID).getTaskDescription(taskID);
	}

	public boolean hasTaskStarted(int projectID, int taskID) {
		return projectList.get(projectID).hasTaskStarted(taskID);
	}

	public LocalDateTime getTaskStartTime(int projectID, int taskID) {
		return projectList.get(projectID).getTaskStartTime(taskID);
	}

	public int getEstimatedTaskDuration(int projectID, int taskID) {
		return projectList.get(projectID).getEstimatedTaskDuration(taskID);
	}

	public int getAcceptableTaskDeviation(int projectID, int taskID) {
		return projectList.get(projectID).getAcceptableTaskDeviation(taskID);
	}

	public boolean hasTaskEnded(int projectID, int taskID) {
		return projectList.get(projectID).hasTaskEnded(taskID);
	}

	public LocalDateTime getTaskEndTime(int projectID, int taskID) {
		return projectList.get(projectID).getTaskEndTime(taskID);
	}

	public String getTaskStatus(int projectID, int taskID) {
		return projectList.get(projectID).getTaskStatus(taskID);
	}

	public boolean hasTaskPrerequisites(int projectID, int taskID) {
		return projectList.get(projectID).hasPrerequisites(taskID);
	}

	public List<Integer> getTaskPrerequisitesFor(int projectID, int taskID) {
		return projectList.get(projectID).getPrerequisites(taskID);
	}

	public boolean hasTaskAlternative(int projectID, int taskID) {
		return projectList.get(projectID).hasAlternative(taskID);
	}

	public int getTaskAlternativeTo(int projectID, int taskID) {
		return projectList.get(projectID).getAlternative(taskID);
	}
}