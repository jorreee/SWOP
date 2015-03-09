package taskMan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


//TODO voorspellen of task op tijd afgehandled kan worden
//TODO methode om te weten hoeveel uw project te laat is

public class Project {
	
	private ArrayList<Task> taskList;
	private final String projectName;
	private final String description;
	private final LocalDateTime creationTime;
	private final LocalDateTime dueTime;
	private LocalDateTime endTime;
	private HashMap<Task,ArrayList<Task>> taskAlternatives;
	private HashMap<Task,ArrayList<Task>> taskPrerequisites;
	private ProjectStatus projectStatus;
	private final int projectID;
	
	/**
	 * Creates a new Project.
	 * 
	 * @param 	projectID
	 * 			The ID of the new Project.
	 * @param 	projectName
	 * 			The name of the new Project.
	 * @param 	description
	 * 			The description of the new Project.
	 * @param 	creationTime
	 * 			The creation time of the new Project.
	 * @param 	dueTime
	 * 			The due time of the new Project.
	 */
	public Project(int projectID, String projectName, String description,
			LocalDateTime creationTime, LocalDateTime dueTime) {
		this.projectName = projectName;
		this.description = description;
		this.creationTime = creationTime;
		this.dueTime = dueTime;
		this.projectID = projectID;
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
	public boolean createTask(String description, LocalTime estimatedDuration, 
			float acceptableDeviation, String taskStatus, int alternativeFor, 
			List<Integer> prerequisiteTasks, LocalDateTime startTime, LocalDateTime endTime) {
		if(description==null)
			return false;
		if(estimatedDuration==null)
			return false;
		if(startTime==null)
			return false;
		if(endTime==null)
			return false;
		if(taskStatus==null)
			return false;
		if(!isValidTaskID(alternativeFor))
			return false;
		TaskStatus status = TaskStatus.valueOf(taskStatus);
		Task newTask = null;
		try{
			newTask = new Task(taskList.size(), description, estimatedDuration, 
					acceptableDeviation, status, startTime, endTime);
		}catch(IllegalArgumentException e){
			return false;
		}
		if(!addAlternative(getTask(alternativeFor), newTask))
			return false;
		ArrayList<Task> pre = null;
		try{
			pre = getTaskByIDs(prerequisiteTasks);
		}catch (IllegalArgumentException e){
			return false;
		}
		if(!addPrerequisite(newTask, pre))
			return false;
		return taskList.add(newTask);
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
	public boolean createTask(String description, LocalTime estimatedDuration, 
			float acceptableDeviation,int alternativeFor, List<Integer> prerequisiteTasks){
		if(description==null)
			return false;
		if(estimatedDuration==null)
			return false;
		if(!isValidTaskID(alternativeFor))
			return false;
		ArrayList<Task> pre = null;
		try{
			pre = getTaskByIDs(prerequisiteTasks);
		}catch (IllegalArgumentException e){
			return false;
		}
		Task task = new Task(taskList.size(),description,estimatedDuration,acceptableDeviation);
		if(!addAlternative(getTask(alternativeFor), task))
			return false;
		if(!addPrerequisite(task, pre))
			return false;
		return taskList.add(task);
	}
	
	/**
	 * Checks whether the given Task has finished alternatives.
	 * 
	 * @param 	task
	 * 			The Task to check.
	 * @return	True if and only the given Task has finished alternatives.
	 */
	private boolean hasFinishedAlternative(Task task) {
		if(task == null)
			return false;
		ArrayList<Task> alternatives = this.getAlternatives(task);
		boolean result = false;
		int i = 0;
		while(!result){
			result = alternatives.get(i).isFinished();
			i++;
			if(i>=alternatives.size())
				return result;
		}
		return result;
		//return taskAlternatives.get(task).isFinished() || hasFinishedAlternative(taskAlternatives.get(task));
		
	}
	
	//TODO finish this
	private void updateTaskStatus(Task task){
		if(!task.isFinished()){
			for(Task pre:getPrerequisites(task)){
				if(!pre.isFinished()){
					task.setTaskStatus(TaskStatus.UNAVAILABLE);
					return;
				}
			}
			task.setTaskStatus(TaskStatus.AVAILABLE);
		}
	}
	
	/**
	 * This method will adjust the status of the project, depending on its tasks.
	 * If the project has at least one task and all of those tasks are finished (or failed with a finished alternative),
	 * the project itself will be considered finished.
	 */
	private void recalcultateProjectStatus() {
		for(Task task : taskList) {
			TaskStatus status = task.getTaskStatus();
			if( status == TaskStatus.AVAILABLE || status == TaskStatus.UNAVAILABLE)
				return;
			if( status == TaskStatus.FAILED) {
				if(!hasFinishedAlternative(task))
					return;
			}
		}
		this.projectStatus = ProjectStatus.FINISHED;
	}
	
	/**
	 * Returns a Task of the Project.
	 * 
	 * @param 	taskID
	 * 			The ID of the needed Task.
	 * @return	The requested Task if it exists.
	 * 			Null otherwise.
	 */
	private Task getTask(int taskID) {
		if(isValidTaskID(taskID))
			return taskList.get(taskID);
		return null;
	}
	
	/**
	 * Returns whether the given TaskID is a valid TaskID.
	 * 
	 * @param 	taskID
	 * 			The ID to check.
	 * @return	True if and only the TaskID is a valid one.
	 */
	private boolean isValidTaskID(int taskID){
		if(taskID<=this.getNumberOfTasks()){
			return true;
		}
		return false;
	}
	
	/**
	 * Updates the details of a given Task.
	 * 
	 * @param 	taskID
	 * 			The ID of the Task.
	 * @param 	startTime
	 * 			The new start time of the Task.
	 * @param 	endTime
	 * 			The new end time of the Task.
	 * @param 	taskStatus
	 * 			The new status of the Task.
	 * @return	True if the update was successful.
	 * 			False if the ID isn't a valid one ore the
	 * 			update isn't valid.
	 */
	public boolean updateTaskDetails(int taskID, LocalDateTime startTime, LocalDateTime endTime, String taskStatus) {
		if(isValidTaskID(taskID)){
			return getTask(taskID).updateTaskDetails(startTime, endTime, taskStatus);
		}
		return false;
	}

	/**
	 * Returns the ID of the Project.
	 * 
	 * @return	The ID of the Project.
	 */
	public int getProjectID() {
		return projectID;
	}
	/**
	 * Returns the name of this Project.
	 * 
	 * @return	The name of this Project.
	 */
	public String getProjectName() { 
		return projectName; 
	}
	
	/**
	 * Returns the description of this Project.
	 * 
	 * @return	The description of this Project.
	 */
	public String getProjectDescription() {	
		return description;	
	}
	
	/**
	 * Returns the creation time of this Project.
	 * 
	 * @return	The creation time of this Project.
	 */
	public LocalDateTime getProjectCreationTime() { 
		return creationTime; 
	}
	
	/**
	 * Get the due time of this Project.
	 * 
	 * @return	The due time of this Project.
	 */
	public LocalDateTime getProjectDueTime() { 
		return dueTime; 
	}
	
	/**
	 * Returns the end time of this Project.
	 * 
	 * @return	The end time of this Project.
	 */
	public LocalDateTime getProjectEndTime() { 
		return endTime; 
	}
	
	/**
	 * Returns the status of this Project.
	 * 
	 * @return	The status of this Project.
	 */
	public String getProjectStatus() { 
		return "projectStatus";	
	}
	
	/**
	 * Returns the list containing all known Tasks.
	 * 
	 * @return	A list of Tasks.
	 */
	public ArrayList<Task> getTaskList(){
		return this.taskList;
	}
	
	/**
	 * Returns the number of known Tasks.
	 * 
	 * @return The number of known Tasks.
	 */
	public int getNumberOfTasks(){
		return this.taskList.size()-1;
	}
	
	/**
	 * Returns the list of Task alternatives.
	 * 
	 * @return	A list of Tasks with their alternatives.
	 */
	public HashMap<Task, ArrayList<Task>> getAllAlternatives(){
		return this.taskAlternatives;
	}
	
	/**
	 * Returns a list of alternatives for the given Task.
	 * 
	 * @param 	task
	 * 			The Task with the alternatives.
	 * @return	A list of the alternatives for the Task if any.
	 *			Null otherwise.
	 */
	public ArrayList<Task> getAlternatives(Task task) {
		if(!hasAlternatives(task)){
			return null;
		}
		return this.taskAlternatives.get(task);
	}
	
	/**
	 * Checks whether the given Tasks has alternative Tasks.
	 * 
	 * @param 	task
	 * 			The Task to check.
	 * @return	True if and only the given Task has alternatives.
	 */
	private boolean hasAlternatives(Task task){
		if(task == null)
			return false;
		return this.taskAlternatives.containsKey(task);
	}
	
	/**
	 * Add an alternative Task to the list of alternatives of the given Task.
	 * 
	 * @param 	task
	 * 			The Task to add a new alternative.
	 * @param 	alternative
	 * 			The new alternative.
	 * @return	True if and only if the addition was successful.
	 */
	private boolean addAlternative(Task task, Task alternative){
		isValidAlternative(task, alternative);
		if(hasAlternatives(task)){
			ArrayList<Task> alt=getAlternatives(task);
			alt.add(alternative);
			taskAlternatives.put(task, alt);
			return true;
		}
		ArrayList<Task> newAlt = new ArrayList<Task>();
		newAlt.add(alternative);
		taskAlternatives.put(task, newAlt);
		return true;
	}
	
	/**
	 * checks whether the given alternative is a valid one for the given Task.
	 * 
	 * @param 	task
	 * 			The task to add the alternative to.
	 * @param 	alt
	 * 			The alternative to check.
	 * @return	True if and only the alternative is valid one.
	 */
	private boolean isValidAlternative(Task task,Task alt){
		if(task==null||alt==null)
			return false;
		if(!task.equals(alt))
			return true;
		return false;
	}
	
	/**
	 * Returns the prerequisites of all the Tasks.
	 *  
	 * @return	A list of prerequisites of all the Tasks.
	 */
	public HashMap<Task, ArrayList<Task>> getTaskPrerequisites() {
		return taskPrerequisites;
	}
	
	/**
	 * Checks whether the given Task has prerequisites.
	 * 
	 * @param 	task
	 * 			The Task to check.
	 * @return	True if and only the given Task has prerequisites.
	 */
	private boolean hasPrerequisites(Task task){
		if(task == null)
			return false;
		return this.taskPrerequisites.containsKey(task);
	}
	
	/**
	 * Adds new prerequisites for the given Task.
	 * 
	 * @param 	task
	 * 			The Task to add the new prerequisites to.
	 * @param 	pre
	 * 			The new prerequisites.
	 * @return	True if and only the addition was successful.
	 */
	private boolean addPrerequisite(Task task, ArrayList<Task> pre){
		if(task==null||pre==null)
			return false;
		if(hasPrerequisites(task)){
			for(Task newPre: pre){
				if(!isValidPrerequisite(task, newPre))
					return false;
			}
			ArrayList<Task> preOld = getPrerequisites(task);
			pre.addAll(preOld);
			taskPrerequisites.put(task, pre);
			return true;
		}
		taskPrerequisites.put(task, pre);
		return true;
	}
	
	/**
	 * Returns the prerequisites of the given Task if any.
	 * 
	 * @param 	task
	 * 			The Task with the prerequisites.
	 * @return	The list of the prerequisites if any.
	 * 			Null otherwise.
	 */
	public ArrayList<Task> getPrerequisites(Task task){
		if(!hasPrerequisites(task))
			return null;
		return taskPrerequisites.get(task);
	}
	
	
	/**
	 * Checks whether the given prerequisites is a valid one for the give Task.
	 * 
	 * @param 	task
	 * 			The given Task.
	 * @param 	pre
	 * 			The prerequisite to check.
	 * @return	True if and only the prerequisite is a valid one.
	 */
	private boolean isValidPrerequisite(Task task, Task pre){
		if(pre==null||task==null)
			return false;
		if(!task.equals(pre))
			return true;
		return false;
	}
	/**
	 * Returns the Tasks belonging to the given ID's.
	 * 
	 * @param 	ids
	 * 			The ID to convert to a Task.
	 * @return	The Task belonging to the given ID's.
	 * @throws 	IllegalArgumentException
	 * 			The given ID must be a valid one.
	 */
	private ArrayList<Task> getTaskByIDs(List<Integer> ids) throws IllegalArgumentException{
		ArrayList<Task> tasks = new ArrayList<Task>();
		for(int id: ids){
			if(!isValidTaskID(id)){
				throw new IllegalArgumentException("The ID isn't a valid ID");
			}
			tasks.add(this.taskList.get(id));
		}
		return tasks;
	}
	
}
