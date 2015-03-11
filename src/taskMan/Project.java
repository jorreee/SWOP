package taskMan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ImmutableList;


//TODO voorspellen of task op tijd afgehandled kan worden
//TODO methode om te weten hoeveel uw project te laat is
//TODO werken met taskID ipv task objecten.
//TODO isontime boolean, getDelay int 

public class Project {
	
	private ArrayList<Task> taskList = new ArrayList<Task>();
	private final String projectName;
	private final String description;
	private final LocalDateTime creationTime;
	private final LocalDateTime dueTime;
	private LocalDateTime endTime;
	private HashMap<Integer,Integer> taskAlternatives = new HashMap<Integer, Integer>();
	private HashMap<Integer,List<Integer>> taskPrerequisites = new HashMap<Integer, List<Integer>>();
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
		recalcultateProjectStatus();
		
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
	public boolean createTask(String description, int estimatedDuration, 
			int acceptableDeviation, String taskStatus, Integer alternativeFor, 
			List<Integer> prerequisiteTasks, LocalDateTime startTime, LocalDateTime endTime) {
		if(description==null)
			return false;
//		if(estimatedDuration==null)
//			return false;
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
		if(!addAlternative(alternativeFor, newTask.getTaskID()))
			return false;
		if(!addPrerequisite(newTask.getTaskID(), prerequisiteTasks))
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
	public boolean createTask(String description, int estimatedDuration, 
			int acceptableDeviation,int alternativeFor, List<Integer> prerequisiteTasks){
		if(description==null)
			return false;
//		if(estimatedDuration==null)
//			return false;
		if(!isValidTaskID(alternativeFor))
			return false;
//		ArrayList<Task> pre = null;
//		try{
//			pre = getTaskByIDs(prerequisiteTasks);
//		}catch (IllegalArgumentException e){
//			return false;
//		}
		Task task = new Task(taskList.size(),description,estimatedDuration,acceptableDeviation);
		if(!addAlternative(alternativeFor, task.getTaskID()))
			return false;
		if(!addPrerequisite(task.getTaskID(), prerequisiteTasks))
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
	private boolean hasFinishedAlternative(Integer task) {
		if(!isValidTaskID(task))
			return false;
		return getTask(taskAlternatives.get(task)).isFinished() || hasFinishedAlternative(taskAlternatives.get(task));
		
	}
	
	//TODO finish this
//	private void updateTaskStatus(Task task){
//		if(!task.isFinished()){
//			for(Task pre:getPrerequisites(task)){
//				if(!pre.isFinished()){
//					task.setTaskStatus(TaskStatus.UNAVAILABLE);
//					return;
//				}
//			}
//			task.setTaskStatus(TaskStatus.AVAILABLE);
//		}
//	}
	
	/**
	 * This method will adjust the status of the project, depending on its tasks.
	 * If the project has at least one task and all of those tasks are finished (or failed with a finished alternative),
	 * the project itself will be considered finished.
	 */
	private void recalcultateProjectStatus() {
//		for(Task task : taskList) {
//			TaskStatus status = task.getTaskStatus();
//			if( status == TaskStatus.AVAILABLE || status == TaskStatus.UNAVAILABLE)
//				return;
//			if( status == TaskStatus.FAILED) {
//				if(!hasFinishedAlternative(task.getTaskID()))
//					return;
//			}
//		}
//		this.projectStatus = ProjectStatus.FINISHED;
		// TODO Dees
		this.projectStatus = ProjectStatus.ONGOING;
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
		if(taskID<=this.getTaskAmount()){
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
		ProjectStatus stat = this.projectStatus;
		String status = "";
		switch(stat){
			case FINISHED:
				status = "FINISHED";
				break;
			case ONGOING:
				status = "ONGOING";
				break;
		}
		return status;
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
	 * Returns a immutable list of the Task ID's of the project.
	 * 
	 * @return	an immutable list of the task ID's.
	 */
	public ImmutableList<Integer> getTaskIDs(){
		List<Integer> id = new ArrayList<>();
		for(Task task: this.taskList){
			id.add(task.getTaskID());
		}
		return new ImmutableList.Builder<Integer>().addAll(id).build();
	}
	
	/**
	 * Returns the list of Task alternatives.
	 * 
	 * @return	A list of Tasks with their alternatives.
	 */
	public HashMap<Integer, Integer> getAllAlternatives(){
		return this.taskAlternatives;
	}
	
	/**
	 * Returns a list of alternatives for the given Task.
	 * 
	 * @param 	task
	 * 			The Task with the alternatives.
	 * @return	A list of the alternatives for the Task if any.
	 *			-1 otherwise.
	 */
	public int getAlternative(Integer task) throws IllegalArgumentException{
		if(!hasAlternative(task)){
			return -1;
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
	public boolean hasAlternative(Integer taskID){
		if(!isValidTaskID(taskID))
			return false;
		return this.taskAlternatives.containsKey(taskID);
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
	private boolean addAlternative(int task, int alternative){
		if(isValidAlternative(task, alternative))
			return false;
		if(!this.getTask(task).isFailed())
			return false;
		else{
			
			this.taskAlternatives.put(task, alternative);
			return true;
		}
		
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
	private boolean isValidAlternative(int task,int alt){
		return task!=alt;
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
	private boolean addPrerequisite(int taskID, List<Integer> pre){
		if(!isValidTaskID(taskID)||pre==null)
			return false;
		if(hasPrerequisites(taskID)){
			for(int newPre: pre){
				if(!isValidPrerequisite(taskID, newPre))
					return false;
			}
			List<Integer> preOld = getPrerequisites(taskID);
			pre.addAll(preOld);
			taskPrerequisites.put(taskID, pre);
			return true;
		}
		taskPrerequisites.put(taskID, pre);
		return true;
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
	private boolean isValidPrerequisite(int task, int pre){
//		if(pre==null||task==null)
//			return false;
		if(task == pre)
			return false;
		else
			return true;
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

	/**
	 * Returns the number of Tasks in the project.
	 * 
	 * @return	The number of Tasks in the project.
	 */
	public int getTaskAmount() {
		return this.taskList.size()-1;
	}

	/**
	 * Return the description of the Task belonging to the given ID.
	 * 
	 * @param 	taskID
	 * 			The ID of the Task
	 * @return	The description of the given Task.
	 * 			Null if the given ID isn't a valid one.
	 */
	public String getTaskDescription(int taskID) {
		if(!isValidTaskID(taskID))
			return null;
		return getTask(taskID).getDescription();
	}

	/**
	 * Checks whether the Task belonging to the given ID has started.
	 * 
	 * @param 	taskID
	 * 			The ID of the Task to check.
	 * @return	True is the Task has started.
	 * 			False otherwise or if the ID isn't a valid one.
	 */
	public boolean hasTaskStarted(int taskID) {
		if(!isValidTaskID(taskID))
			return false;
		return getTask(taskID).hasStarted();
	}

	/**
	 * Return the start time of the Task belonging to the given ID.
	 * 
	 * @param 	taskID
	 * 			The ID of the Task.
	 * @return	The start time of the Task.
	 * 			Null if the Task hasn't started or the ID isn't a valid one.
	 */
	public LocalDateTime getTaskStartTime(int taskID) {
		if(!isValidTaskID(taskID))
			return null;
		if(!hasTaskStarted(taskID))
			return null;
		return getTask(taskID).getBeginTime();
	}

	/**
	 * Returns the estimated duration of the Task belonging to the given ID.
	 * 
	 * @param 	taskID
	 * 			The ID of the Task.
	 * @return	The estimated duration of the Task.
	 * 			-1 if the ID isn't a valid one.
	 */
	public int getEstimatedTaskDuration(int taskID) {
		if(!isValidTaskID(taskID))
			return -1;
		return getTask(taskID).getEstimatedDuration();
	}

	/**
	 * Returns the estimated deviation of the Task belonging to the given ID.
	 * 
	 * @param 	taskID
	 * 			The ID of the task.
	 * @return	The acceptable deviation of the Task.
	 * 			-1 if the ID isn't a valid one.
	 */
	public int getAcceptableTaskDeviation(int taskID) {
		if(!isValidTaskID(taskID))
			return -1;
		return getTask(taskID).getAcceptableDeviation();
	}

	/**
	 * Checks whether the Task belonging to the given ID has ended.
	 * 
	 * @param 	taskID
	 * 			The ID of the Task.
	 * @return	True if the Task has ended.
	 * 			False if the ID isn't a valid one or the Task has ended.
	 */
	public boolean hasTaskEnded(int taskID) {
		if(!isValidTaskID(taskID))
			return false;
		return getTask(taskID).hasEnded();
	}

	/**
	 * Return the end time of the Task belonging to the given ID.
	 * 
	 * @param 	taskID
	 * 			The ID of the given Task.
	 * @return	The end time of the Task.
	 * 			Null if the Task hasn't ended or the ID isn't a valid one.
	 */
	public LocalDateTime getTaskEndTime(int taskID) {
		if(!isValidTaskID(taskID))
			return null;
		if(!hasTaskEnded(taskID))
			return null;
		return getTask(taskID).getEndTime();
	}

	/**
	 * Returns the status of the Task belonging to the given ID.
	 * 
	 * @param 	taskID
	 * 			The ID of the Task.
	 * @return	The status of the Task.
	 * 			Null if the ID isn't a valid one.
	 */
	public String getTaskStatus(int taskID) {
		if(!isValidTaskID(taskID))
			return null;
		return getTask(taskID).getStatus();
	}

	/**
	 * Checks whether the given Task belonging to the given ID has prerequisites.
	 * 
	 * @param 	taskID
	 * 			The ID of the task.
	 * @return	True if the given Task has prerequisites.
	 * 			False otherwise or the ID isn't a valid one.
	 */
	public boolean hasPrerequisites(int taskID) {
		if(!isValidTaskID(taskID))
			return false;
		return this.taskPrerequisites.containsKey(taskID);
	}

	/**
	 * Returns the prerequisites of the Task belonging to the given ID if any.
	 * 
	 * @param 	taskID
	 * 			The ID of the Task.
	 * @return	The list of the prerequisites if any.
	 * 			Null otherwise.
	 */
	public List<Integer> getPrerequisites(int taskID) {
		if(!isValidTaskID(taskID))
			return null;
		return this.taskPrerequisites.get(taskID);
	}
	
	/**
	 * Returns whether the project is on time;
	 * 
	 * @param 	current
	 * 			The current time to compare with.
	 * @return
	 */
	public boolean isOnTime(LocalDateTime current){
		if(endTime == null){
			if(current.isAfter(dueTime))
				return false;
			else
				return true;
		}
		else{
			return endTime.isBefore(dueTime);
		}
	}
	
	//TODO
	/**
	 * Returns the delay of the project if any.
	 * 
	 * @param 	current
	 * 			The current time to check with.
	 * @return	The delay of the project
	 */
	public int getDelay(LocalDateTime current){
		if(!isOnTime(current))
			return -1;
		else
			return 1;
	}
	
}
