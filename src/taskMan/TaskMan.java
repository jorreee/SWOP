package taskMan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//TODO Still not done

/**
 * The Main System that keeps track of the list of projects and the current Time.
 * @author Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel, Eli Vangrieken
 *
 */
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
		currentTime = time;
	}
	
	/**
	 * Advances the current time to the given time.
	 * 
	 * @param 	time
	 * 			The time to which the system should advance
	 * @return	True if the advance time was successful.
	 * 			False if the time parameter is earlier than the current time.
	 */
	public boolean advanceTimeTo(LocalDateTime time) {
		if(time == null)
			return false;
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
	 * 			null if the ID isn't a valid one
	 */
	private Project getProject(int projectID) {
		if(!isValidProjectID(projectID)) {
			return null;
		}
		return projectList.get(projectID);
	}
		
	/**
	 * Gets the current time
	 * 
	 * @return the current time
	 */
	public LocalDateTime getCurrentTime() { 
		return currentTime; 
		}
	
	/**
	 * Creates a new Project with a creation time
	 * 
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
		Project project = null;
		try{
			 project = new Project(projectList.size(), name, description, creationTime, dueTime);
		} catch(IllegalArgumentException e) {
			return false;
		}
		return projectList.add(project);
	}
	
	/**
	 * Creates a new Project with the current time as the creation time.
	 * 
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
		return createProject(name, description, getCurrentTime(), dueTime);
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
	public boolean createTask(int projectID, String description, int estimatedDuration, 
			int acceptableDeviation, String taskStatus, Integer alternativeFor, 
			List<Integer> prerequisiteTasks, LocalDateTime startTime, LocalDateTime endTime) {
		if(!isValidProjectID(projectID)) {
			return false;
		}
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
	 * 			False if the projectID is a valid one.
	 * 			False if the creation was unsuccessful
	 */
	public boolean createTask(int projectID, String description, int estimatedDuration, 
			int acceptableDeviation, Integer alternativeFor, List<Integer> prerequisiteTasks) {
		if(!isValidProjectID(projectID)) {
			return false;
		}
		return getProject(projectID).createTask(description,estimatedDuration, acceptableDeviation, alternativeFor, prerequisiteTasks);
	}
	
	/**
	 * Returns the name of the project with the given ID
	 * 
	 * @param 	projectID
	 * 			The id of the project
	 * @return	the name of the project
	 * 			null if the ID isn't a valid one
	 */
	public String getProjectName(int projectID) {
		if(!isValidProjectID(projectID)) {
			return null;
		}
		return getProject(projectID).getProjectName();
	}
	
	/**
	 * Returns the description of the project with the given ID
	 * 
	 * @param 	projectID
	 * 			The id of the project
	 * @return	the description of the project
	 * 			null if the ID isn't a valid one
	 */
	public String getProjectDescription(int projectID) {
		if(!isValidProjectID(projectID)) {
			return null;
		}
		return getProject(projectID).getProjectDescription();
	}
	
	/**
	 * Returns the creation time of the project with the given ID
	 * 
	 * @param 	projectID
	 * 			The id of the project
	 * @return	the creation time of the project
	 * 			null if the ID isn't a valid one
	 */
	public LocalDateTime getProjectCreationTime(int projectID) {
		if(!isValidProjectID(projectID)) {
			return null;
		}
		return getProject(projectID).getProjectCreationTime();
	}
	
	/**
	 * Returns the due time of the project with the given ID
	 * 
	 * @param 	projectID
	 * 			The id of the project
	 * @return	the due time of the project
	 * 			null if the ID isn't a valid one
	 */
	public LocalDateTime getProjectDueTime(int projectID) {
		if(!isValidProjectID(projectID)) {
			return null;
		}
		return getProject(projectID).getProjectDueTime();
	}
	
	/**
	 * Returns the end time of the project with the given ID
	 * 
	 * @param 	projectID
	 * 			The id of the project
	 * @return	the end time of the project
	 * 			null if the ID isn't a valid one
	 */
	public LocalDateTime getProjectEndTime(int projectID) {
		if(!isValidProjectID(projectID)) {
			return null;
		}
		return getProject(projectID).getProjectEndTime();
	}
	
	/**
	 * Returns the status of the project with the given ID
	 * 
	 * @param 	projectID
	 * 			the id of the project
	 * @return	the status of the project
	 * 			null if the ID isn't a valid one
	 */
	public String getProjectStatus(int projectID) {
		if(!isValidProjectID(projectID)) {
			return null;
		}
		return getProject(projectID).getProjectStatus();
	}
	
	/**
	 * Returns the delay of the project with the given ID
	 * 
	 * @param 	projectID
	 * 			the id of the project
	 * @return	the delay of the project
	 * 			null if the ID isn't a valid one
	 */
	public int[] getProjectDelay(int projectID){
		if(!isValidProjectID(projectID)) {
			return null;
		}
		return getProject(projectID).getDelay(currentTime);

	}
	
	/**
	 * Returns all available tasks per project
	 * 
	 * @return	a mapping of a project id on a list of available task id's
	 */
	public HashMap<Integer, List<Integer>> getAvailableTasks() {
		HashMap<Integer, List<Integer>> hashMap = new HashMap<Integer, List<Integer>>();
		for (Project proj : projectList){
			hashMap.put(proj.getProjectID(), getAvailableTasks(proj.getProjectID()));
		}
		return hashMap;
	}
	
	/**
	 * Returns all available tasks for a given project
	 * 
	 * @param 	projectID
	 * 			The id of the project
	 * @return	The available task id's of the project
	 * 			null if the ID isn't a valid one
	 */
	public List<Integer> getAvailableTasks(int projectID) {
		if(!isValidProjectID(projectID)) {
			return null;
		}
		return getProject(projectID).getAvailableTasks();
	}
	
	/**
	 * Returns the total amount of projects
	 * 
	 * @return	the amount of projects
	 */
	public int getProjectAmount() {
		return projectList.size();
	}

	/**
	 * Returns whether the project with the given ID is on time or not
	 * 
	 * @param 	projectID
	 * 			the id of the project
	 * @return	True if the project is on time,
	 * 			false if the project is not on time
	 */
	public boolean isProjectOnTime(int projectID) {
		if(!isValidProjectID(projectID)) {
			return false;
		}
		return getProject(projectID).isOnTime(currentTime);
	}

	/**
	 * Returns the amount of tasks of the project with the given ID
	 * 
	 * @param 	projectID
	 * 			the id of the project
	 * @return	the amount of tasks of the project
	 * 			-1 if the ID isn't a valid one
	 */
	public int getTaskAmount(int projectID) {
		if(!isValidProjectID(projectID)) {
			return -1;
		}
		return projectList.get(projectID).getTaskAmount();
	}

	/**
	 * Returns the description of the task with the given task id belonging 
	 * to the project with the given project id
	 * 
	 * @param 	projectID
	 * 			the id of the given project
	 * @param 	taskID
	 * 			the id of the given task
	 * @return	the description of the task
	 * 			null if the ID isn't a valid one
	 */
	public String getTaskDescription(int projectID, int taskID) {
		if(!isValidProjectID(projectID)) {
			return null;
		}
		return projectList.get(projectID).getTaskDescription(taskID);
	}

	/**
	 * Returns the start time of the task with the given task id 
	 * belonging to the project with the given project id
	 * 
	 * @param 	projectID
	 * 			the id of the given project
	 * @param 	taskID
	 * 			the id of the given task
	 * @return	the start time of the task
	 * 			null if the ID isn't a valid one 
	 */
	public LocalDateTime getTaskStartTime(int projectID, int taskID) {
		if(!isValidProjectID(projectID)) {
			return null;
		}
		return projectList.get(projectID).getTaskStartTime(taskID);
	}

	/**
	 * Returns the estimated duration of the task with the given task id 
	 * belonging to the project with the given project id
	 * 
	 * @param 	projectID
	 * 			the id of the given project
	 * @param 	taskID
	 * 			the id of the given task
	 * @return	the estimated duration of the task 
	 * 			-1 if the project ID isn't a valid one
	 */
	public int getEstimatedTaskDuration(int projectID, int taskID) {
		if(!isValidProjectID(projectID)) {
			return -1;
		}
		return projectList.get(projectID).getEstimatedTaskDuration(taskID);
	}

	/**
	 * Returns the acceptable deviation of the task with the given task id 
	 * belonging to the project with the given project id
	 * 
	 * @param 	projectID
	 * 			the id of the given project
	 * @param 	taskID
	 * 			the id of the given task
	 * @return	the acceptable deviation of the task 
	 * 			-1 if the project ID isn't a valid one
	 */
	public int getAcceptableTaskDeviation(int projectID, int taskID) {
		if(!isValidProjectID(projectID)) {
			return -1;
		}
		return projectList.get(projectID).getAcceptableTaskDeviation(taskID);
	}

	/**
	 * Returns whether the task with the given task id belonging to the project 
	 * with the given project id has ended
	 * 
	 * @param 	projectID
	 * 			the id of the given project
	 * @param 	taskID
	 * 			the id of the given task
	 * @return	True if the task has ended,
	 * 			false if the task hasn't ended
	 * 			false if the project ID isn't a valid one
	 */
	public boolean hasTaskEnded(int projectID, int taskID) {
		if(!isValidProjectID(projectID)) {
			return false;
		}
		return projectList.get(projectID).hasTaskEnded(taskID);
	}

	/**
	 * Returns the end time of the task with the given task id belonging 
	 * to the project with the given project id
	 * 
	 * @param 	projectID
	 * 			the id of the given project
	 * @param 	taskID
	 * 			the id of the given task
	 * @return	the end time of the task 
	 * 			null if the project ID isn't a valid one
	 */
	public LocalDateTime getTaskEndTime(int projectID, int taskID) {
		if(!isValidProjectID(projectID)) {
			return null;
		}
		return projectList.get(projectID).getTaskEndTime(taskID);
	}

	/**
	 * Returns the status of the task with the given task id belonging 
	 * to the project with the given project id
	 * 
	 * @param 	projectID
	 * 			the id of the given project
	 * @param 	taskID
	 * 			the id of the given task
	 * @return	the status of the task 
	 * 			null if the project ID isn't a valid one
	 */
	public String getTaskStatus(int projectID, int taskID) {
		if(!isValidProjectID(projectID)) {
			return null;
		}
		return projectList.get(projectID).getTaskStatus(taskID);
	}

	/**
	 * Returns whether the task with the given task id belonging to the project 
	 * with the given project id has prerequisites
	 * 
	 * @param 	projectID
	 * 			the id of the given project
	 * @param 	taskID
	 * 			the id of the given task
	 * @return	True if the task has prerequisites,
	 * 			false if the task doesn't have prerequisites
	 * 			false if the project ID isn't a valid one
	 */
	public boolean hasTaskPrerequisites(int projectID, int taskID) {
		if(!isValidProjectID(projectID)) {
			return false;
		}
		return projectList.get(projectID).hasPrerequisites(taskID);
	}

	/**
	 * Returns the prerequisites for the task with the given task id belonging 
	 * to the project with the given project id
	 * 
	 * @param 	projectID
	 * 			the id of the given project
	 * @param 	taskID
	 * 			the id of the given task
	 * @return	the prerequisites for the task 
	 * 			null if the project ID isn't a valid one
	 */
	public List<Integer> getTaskPrerequisitesFor(int projectID, int taskID) {
		if(!isValidProjectID(projectID)) {
			return null;
		}
		return projectList.get(projectID).getPrerequisites(taskID);
	}

	/**
	 * Returns whether the task with the given task id belonging 
	 * to the project with the given project id has alternatives
	 * 
	 * @param 	projectID
	 * 			the id of the given project
	 * @param 	taskID
	 * 			the id of the given task
	 * @return	True if the task has alternatives,
	 * 			false if the task doesn't have alternatives
	 * 			false if the project ID isn't a valid one
	 */
	public boolean hasTaskAlternative(int projectID, int taskID) {
		if(!isValidProjectID(projectID)) {
			return false;
		}
		return projectList.get(projectID).hasAlternative(taskID);
	}

	/**
	 * Returns the alternative for the task with the given task id belonging 
	 * to the project with the given project id
	 * 
	 * @param 	projectID
	 * 			the id of the given project
	 * @param 	taskID
	 * 			the id of the given task
	 * @return	the alternatives for the task 
	 * 			-1 if the project ID isn't a valid one
	 */
	public int getTaskAlternativeTo(int projectID, int taskID) {
		if(!isValidProjectID(projectID)) {
			return -1;
		}
		return projectList.get(projectID).getAlternative(taskID);
	}
	
	/**
	 * Sets the task with the given task id belonging to the project 
	 * with the given project id to finished
	 * 
	 * @param 	projectID
	 * 			the id of the given project
	 * @param 	taskID
	 * 			the id of the given task
	 * @param 	startTime
	 * 			the start time of the given task
	 * @param 	endTime
	 * 			the end time of the given task
	 * @return	True if setting the task to finished was successful,
	 * 			False if it was unsuccessful
	 * 			false if the project ID isn't a valid one
	 */
	public boolean setTaskFinished(int projectID, int taskID, LocalDateTime startTime, LocalDateTime endTime) {
		if(endTime == null || endTime.isAfter(getCurrentTime())) {
			return false;
		}
		if(!isValidProjectID(projectID)) {
			return false;
		}
		return projectList.get(projectID).setTaskFinished(taskID,startTime,endTime);
	}
	
	/**
	 * Sets the task with the given task id belonging to the project 
	 * with the given project id to failed
	 * 
	 * @param 	projectID
	 * 			the id of the given project
	 * @param 	taskID
	 * 			the id of the given task
	 * @param 	startTime
	 * 			the start time of the given task
	 * @param 	endTime
	 * 			the end time of the given task
	 * @return	True if setting the task to failed was successful.
	 * 			False if it was unsuccessful.
	 * 			False if the project ID isn't a valid one.
	 * 			False if the end time is null or the end time comes after the current time.
	 */
	public boolean setTaskFailed(int projectID, int taskID, LocalDateTime startTime, LocalDateTime endTime) {
		if(endTime == null || endTime.isAfter(getCurrentTime())) {
			return false;
		}
		if(!isValidProjectID(projectID)) {
			return false;
		}
		return projectList.get(projectID).setTaskFailed(taskID,startTime, endTime);
	}

	/**
	 * Returns whether the Task is unacceptable overdue.
	 * 
	 * @param 	projectID
	 * 			The ID of the project
	 * @param 	taskID
	 * 			The ID of the Task
	 * @return	True if the Task is unacceptable overdue.
	 * 			False otherwise or the task has not ended or failed.
	 * 			False if the project ID isn't a valid one
	 */
	public boolean isTaskUnacceptableOverdue(int projectID, int taskID) {
		if(!isValidProjectID(projectID)) {
			return false;
		}
		return getProject(projectID).isTaskUnacceptableOverdue(taskID);
	}

	/**
	 * Checks whether the Task in on time.
	 * 
	 * @param 	projectID
	 * 			The ID of the project
	 * @param 	taskID
	 * 			The ID of the task.
	 * @return	True if the Task is on time or has not yet finished or failed.
	 * 			False otherwise.
	 * 			False if the project ID isn't a valid one
	 */
	public boolean isTaskOnTime(int projectID, int taskID) {
		if(!isValidProjectID(projectID)) {
			return false;
		}
		return getProject(projectID).isTaskOnTime(taskID);
	}

	/**
	 * Returns the percentage of overdue of the task if any.
	 * 
	 * @param 	projectID
	 * 			The ID of the project
	 * @param 	taskID
	 * 			The ID of the task
	 * @return	The percentage of overdue.
	 * 			0 if the task is on time.
	 * 			-1 if the project ID or tak ID isn't a valid one.
	 */
	public int getTaskOverTimePercentage(int projectID, int taskID) {
		if(!isValidProjectID(projectID)) {
			return -1;
		}
		return getProject(projectID).getTaskOverTimePercentage(taskID);
	}

	/**
	 * Returns whether a certain project has finished
	 * 
	 * @param	projectID
	 * 			the id of the given project
	 * @return	True if the project has finished
	 * 			false if the project ID isn't a valid one
	 */
	public boolean isProjectFinished(int projectID) {
		if(!isValidProjectID(projectID)) {
			return false;
		}
		return getProject(projectID).isFinished();
	}
	
	/**
	 * Returns whether the unfinished project is estimated to end on time
	 * 
	 * @param	projectID
	 * 			The id of the given project
	 * @return	True if the project is estimated to end on time
	 * 			If the project has already finished,
	 * 				it will return whether or not it had accumulated a delay
	 * 			False if the project ID isn't a valid one
	 */
	public boolean isProjectEstimatedOnTime(int projectID) {
		if(!isValidProjectID(projectID)) {
			return false;
		}
		return getProject(projectID).isEstimatedOnTime(currentTime);
	}
	
	/**
	 * Returns the estimated time until the project should end
	 * 
	 * @param	projectID
	 * 			the id of the given project
	 * @return	The amount of years, months, days, hours and minutes
	 * 				that are estimated to be required to finish the project
	 * 			null if the project ID isn't a valid one
	 */
	public int[] getEstimatedProjectDelay(int projectID) {
		if(!isValidProjectID(projectID)) {
			return null;
		}
		return getProject(projectID).getEstimatedProjectDelay(currentTime);
	}
	
	/**
	 * Checks whether the given project ID is a valid one.
	 * 
	 * @param 	PID
	 * 			The ID to check.
	 * @return	True if the ID is valid.
	 * 			False otherwise.
	 */
	private boolean isValidProjectID(int PID) {
		if(PID < 0 || PID >= getProjectAmount()) {
			return false;
		}
		return true;
	}
	
}