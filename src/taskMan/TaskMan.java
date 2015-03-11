package taskMan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//TODO Still not done
public class TaskMan {
	
	private ArrayList<Project> projectList;
	private LocalDateTime currentTime;
	
	/**
	 * Creates a TaskMan system instance. 
	 * This is the default constructor.
	 * 
	 * 
	 */
	public TaskMan(){
		projectList = new ArrayList<>();
		currentTime = LocalDateTime.now();
	}
	
	/**
	 * Creates a TaskMan system instance with a given time.
	 * 
	 * @param 	time
	 * 			The current TaskMan time. 
	 * 
	 */
	public TaskMan(LocalDateTime time){
		projectList = new ArrayList<>();
		currentTime = LocalDateTime.now();
		currentTime = time;
	}
	
	/**
	 * Advances the current time to the given time.
	 * 
	 * @param 	time
	 * 			The time to which the system should advance
	 * @return	True if the advance time was succesful.
	 * 			False if the time parameter is earlier than the current time.
	 */
	public boolean advanceTimeTo(LocalDateTime time) {
		if (time.isAfter(currentTime)) {
			currentTime = time;
			return true;
		}
		else return false;
		
	}
	
	/**
	 * Gets the project with the given project ID
	 * 
	 * @param 	projectID
	 * 			The ID of the project to be retrieved
	 * @return	The project with the project ID
	 */
	private Project getProject(int projectID) {
		return projectList.get(projectID);
	}
		
	/**
	 * Gets the current time
	 * @return the current time
	 */
	public LocalDateTime getCurrentTime() { return currentTime; }
	
	/**
	 * Creates a new Project with a creation time
	 * @param 	name
	 * 			The name of the project
	 * @param 	description
	 * 			The description of the project
	 * @param 	creationTime
	 * 			The creation time of the project
	 * @param 	dueTime
	 * 			The due time of the project
	 * @return 	true if the project creation was successful
	 * 			false if the creation was unsuccessful
	 */
	public boolean createProject(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime) {
		Project project = new Project(projectList.size(), name, description, creationTime, dueTime);
		return projectList.add(project);
	}
	
	/**
	 * Creates a new Project with the current time as the creation time.
	 * @param 	name
	 * 			The name of the project
	 * @param 	description
	 * 			The description of the project
	 * @param 	dueTime
	 * 			The due time of the project
	 * @return 	true if the project creation was successful
	 * 			false if the creation was unsuccessful
	 */
	public boolean createProject(String name, String description, LocalDateTime dueTime) {
		Project project = new Project(projectList.size(), name, description, currentTime, dueTime);
		return projectList.add(project);
	}
	
	/**
	 * Creates a new Task with a status of failed or finished.
	 * 
	 * @param 	description
	 * 			The description of the given Task.
	 * @param 	estimatedDuration
	 * 			The estimated duration of the Task.
	 * @param 	acceptableDeviation
	 * 			The acceptable deviation of the Task.
	 * @param 	taskStatus
	 * 			The Status of the Task.
	 * @param 	alternativeFor
	 * 			The alternative Task.
	 * @param 	prerequisiteTasks
	 * 			The prerequisites Tasks for this Task.
	 * @param 	startTime
	 * 			The start time of the Task.
	 * @param 	endTime
	 * 			The end time of the Task.
	 * @return	True if and only the creation of a Task with a status
	 * 			of failed or finished was successful.
	 */
	public boolean createTask(int projectID, String description, int estimatedDuration, int acceptableDeviation, String taskStatus, Integer alternativeFor, List<Integer> prerequisiteTasks, LocalDateTime startTime, LocalDateTime endTime) {
		return getProject(projectID).createTask(description, estimatedDuration, acceptableDeviation, taskStatus, alternativeFor, prerequisiteTasks, startTime, endTime);
	}
	
	/**
	 * Creates a new Task without a set status.
	 * 
	 * @param 	description
	 * 			The description of the given Task.
	 * @param 	estimatedDuration
	 * 			The estimated duration of the Task.
	 * @param 	acceptableDeviation
	 * 			The acceptable deviation of the Task.
	 * @param 	alternativeFor
	 * 			The alternative Task.
	 * @param 	prerequisiteTasks
	 * 			The prerequisites Tasks for this Task.
	 * @return	True if the creation of a new Task was successful.
	 */
	public boolean createTask(int projectID, String description, int estimatedDuration, int acceptableDeviation, Integer alternativeFor, List<Integer> prerequisiteTasks) {
		return getProject(projectID).createTask(description,estimatedDuration, acceptableDeviation, alternativeFor, prerequisiteTasks);
	}
	
	public boolean updateTaskDetails(int projectID, int taskID, LocalDateTime startTime, LocalDateTime endTime, String taskStatus) {
		if (startTime.isAfter(currentTime) || endTime.isAfter(currentTime) ){
			return false;
		}
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
	
	public int[] getProjectDelay(int projectID){
		return getProject(projectID).getDelay(currentTime);

	}
	
	public HashMap<Integer, List<Integer>> getAvailableTasks() {
		HashMap<Integer, List<Integer>> hashMap = new HashMap<Integer, List<Integer>>();
		for (Project proj : projectList){
			hashMap.put(proj.getProjectID(), getAvailableTasks(proj.getProjectID()));
		}
		return hashMap;
	}
	
	public List<Integer> getAvailableTasks(int projectID) {
		return getProject(projectID).getAvailableTasks();
	}

	public int getProjectAmount() {
		return projectList.size();
	}

	public boolean isOnTime(int projectID) {
		return getProject(projectID).isOnTime(currentTime);
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