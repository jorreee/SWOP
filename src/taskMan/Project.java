package taskMan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import taskMan.util.TimeSpan;
import taskMan.view.TaskView;

import com.google.common.collect.ImmutableList;

/**
 * The project class used by TaskMan. A project will always have a unique
 * identifier, a name, a description, a creation time, a due time, a status and
 * a list containing all tasks assigned to this project. A project will also
 * know if a task is another one's alternative or if a task depends on a series
 * of other tasks.
 * A project can be either ongoing or finished.
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
//TODO weg met ID
public class Project {

	private ArrayList<Task> taskList = new ArrayList<Task>();
	private final String projectName;
	private final String description;
	private final LocalDateTime creationTime;
	private final LocalDateTime dueTime;
	private LocalDateTime endTime;
//	private HashMap<Integer,Integer> taskAlternatives = new HashMap<Integer, Integer>();
//	private HashMap<Integer,List<Integer>> taskPrerequisites = new HashMap<Integer, List<Integer>>();
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
	 * @throws	IllegalArgumentException
	 * 			Throws exception when the dueTime comes before the creationTime.
	 */
	public Project(int projectID, String projectName, String description,
			LocalDateTime creationTime, LocalDateTime dueTime) throws IllegalArgumentException{
		if(dueTime==null || creationTime==null) {
			throw new IllegalArgumentException("One of the arguments is null");
		}
		if(dueTime.isBefore(creationTime)) {
			throw new IllegalArgumentException("Duetime comes before the creationTime");
		}
		if(dueTime.equals(creationTime)) {
			throw new IllegalArgumentException("No time difference");
		}
		
		this.projectName = projectName;
		this.description = description;
		this.creationTime = creationTime;
		this.dueTime = dueTime;
		this.projectID = projectID;
		this.projectStatus = ProjectStatus.ONGOING;

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
	 * 			The Task this new task will replace.
	 * @param 	prerequisiteTasks
	 * 			The prerequisites Tasks for this Task.
	 * @param 	startTime
	 * 			The start time of the Task.
	 * @param 	endTime
	 * 			The end time of the Task.
	 * @return	True if and only the creation of a Task with a status
	 * 			of failed or finished was successful.
	 */
	public boolean createTask(String description, 
						int estimatedDuration, 
						int acceptableDeviation, 
						List<TaskView> prerequisiteTasks, 
						TaskView alternativeFor, 
						String taskStatus,
						LocalDateTime startTime, 
						LocalDateTime endTime) {

		int newTaskID = taskList.size();
//		if(prerequisiteTasks.contains(alternativeFor)) {
//			return false;
//		}
		
//		if(!isValidAlternative(alternativeFor, newTaskID)) {
//			return false;
//		}
//		if(!isValidPrerequisites(newTaskID, prerequisiteTasks)) {
//			return false;
//		}
		if(isFinished()) {
			return false;
		}
		
		if(!isValidTaskView(alternativeFor)) {
			return false;
		}
		Task altFor = unwrapTaskView(alternativeFor);
		
		ArrayList<Task> prereqTasks = new ArrayList<Task>();
		for(TaskView t : prerequisiteTasks) {
			if(!isValidTaskView(t)) {
				return false;
			}
			prereqTasks.add(unwrapTaskView(t));
		}
		
		Task newTask = null;
//		TimeSpan extraTime = getExtraTime(alternativeFor);
		
		try{
			if(taskStatus != null) {
				newTask = new Task(
						newTaskID, 
						description, 
						estimatedDuration, 
						acceptableDeviation, 
						prereqTasks,
						altFor,
						taskStatus, 
						startTime, 
						endTime);
			}
			else {
				newTask = new Task(
						newTaskID, 
						description, 
						estimatedDuration, 
						acceptableDeviation, 
						prereqTasks,
						altFor);
			}
		} catch(IllegalArgumentException e) {
//			System.out.println(e.getMessage());
//			e.printStackTrace();
			return false;
		}
		
//		if(!addAlternative(alternativeFor, newTaskID)) {
//			return false;
//		}
//		if(!addPrerequisites(newTaskID, prerequisiteTasks)) {
//			return false;
//		}
		
//		updateTaskStatus(newTask);
		
		boolean success = taskList.add(newTask);
		
		if(success) {
			recalculateProjectStatus();
		} 
		return success;
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
	public boolean createTask(String description, 
			int estimatedDuration, 
			int acceptableDeviation,
			List<TaskView> prerequisiteTasks,
			TaskView alternativeFor) {
		
		return createTask(description, 
				estimatedDuration, 
				acceptableDeviation, 
				prerequisiteTasks,
				alternativeFor, 
				null,
				null, 
				null);
	}
	
	private boolean isValidTaskView(TaskView t) {
		return taskList.contains(unwrapTaskView(t));
	}
	
	/**
	 * Unwraps the TaskView object and returns the Task that it contained
	 * 		IF the unwrapped task belongs to this project:
	 * 				(getTaskList().contains(task)
	 * 
	 * @param 	
	 * 			| the TaskView to unwrap
	 * @return 
	 * 			| the unwrapped Task if it belonged to this project
	 * 			| NULL otherwise
	 */
	private Task unwrapTaskView(TaskView t) {
		for(Task task : taskList) {
			if (t.hasAsTask(task)) {
				return task;
			}
		}
		return null;
	}

//	/**
//	 * Checks whether the given Task has finished alternatives.
//	 * 
//	 * @param 	task
//	 * 			The Task to check.
//	 * @return	True if and only the given Task has finished alternatives.
//	 */
//	//TODO naar TASK
//	private boolean hasFinishedAlternative(Task task) {
//		if(!isValidTaskID(taskID)) {
//			return false;
//		}
//		if(!hasAlternative(taskID)) {
//			return false;
//		}
//		return task.isFinished() || hasFinishedAlternative(taskAlternatives.get(taskID));
//
//	}

	//TODO Task doet dit zelf
//	/**
//	 * Update the status of a given Task
//	 * 
//	 * @param 	task
//	 * 			The Task to update.
//	 */
//	//TODO Refactor as OBSERVER
//	private void updateTaskStatus(Task task){
//		if(task.hasEnded()) { 
//			return;
//		}
//		if(!hasPrerequisites(task.getTaskID())) {
//			task.setAvailable();
//			return;
//		}
//		for(Integer preID : getPrerequisites(task.getTaskID())) {
//			if(!getTask(preID).hasEnded()) {
//				task.setUnavailable();
//				return;
//			}
//			if(getTask(preID).isFailed() && !hasFinishedAlternative(preID)) {
//				task.setUnavailable();
//				return;
//			}
//		}
//		task.setAvailable();
//	}
	
	/**
	 * This method will adjust the status of the project, depending on its tasks.
	 * If the project has at least one task and all of those tasks are finished (or failed with a finished alternative),
	 * the project itself will be considered finished.
	 */
	private void recalculateProjectStatus() {
		//TODO status.check(taskList) -> hasFinishedEndpoint
		for(Task task : taskList) {
			String status = task.getStatus();
			if( status.equals("available") || status.equals("unavailable")) {
				return;
			}
			if( status.equals("failed")) {
				return;
			}
		}
		this.projectStatus = ProjectStatus.FINISHED;
	}

	//TODO is vervangen door unwrap
//	/**
//	 * Returns a Task of the Project.
//	 * 
//	 * @param 	taskID
//	 * 			The ID of the needed Task.
//	 * @return	The requested Task if it exists.
//	 * 			Null otherwise.
//	 */
//	private Task getTask(int taskID) {
//		if(!isValidTaskID(taskID)) {
//			return null;
//		}
//		return taskList.get(taskID);
//	}

//	/**
//	 * Returns whether the given TaskID is a valid TaskID.
//	 * 
//	 * @param 	taskID
//	 * 			The ID to check.
//	 * @return	True if and only the TaskID is a valid one.
//	 * 			False otherwise.
//	 */
//	private boolean isValidNewTaskID(int taskID){
//		return isValidTaskID(taskID) || taskID == taskList.size();
//	}

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
			status = "finished";
			break;
		case ONGOING:
			status = "ongoing";
			break;
		}
		return status;
	}

	/**
	 * Returns the list containing all known Tasks.
	 * 
	 * @return	A list of Tasks.
	 */
	//TODO TaskViews hier aanmaken of pas in ProjectView?
	public ArrayList<Task> getTaskList(){
		return taskList;
	}

//	/** // TODO remove this
//	 * Returns a immutable list of the Task ID's of the project.
//	 * 
//	 * @return	an immutable list of the task ID's.
//	 */
//	public ImmutableList<Integer> getTaskIDs(){
//		List<Integer> id = new ArrayList<>();
//		for(Task task: this.taskList){
//			id.add(task.getTaskID());
//		}
//		return new ImmutableList.Builder<Integer>().addAll(id).build();
//	}

//	/** // TODO remove this
//	 * Returns the list of Task alternatives.
//	 * 
//	 * @return	A list of Tasks with their alternatives.
//	 */
//	public HashMap<Integer, Integer> getAllAlternatives(){
//		return this.taskAlternatives;
//	}

//	/** // TODO refactor as OBSERVER
//	 * Returns a list of alternatives for the given Task.
//	 * 
//	 * @param 	task
//	 * 			The Task with the alternatives.
//	 * @return	A list of the alternatives for the Task if any.
//	 *			-1 otherwise.
//	 */
//	public int getAlternative(int task) {
//		if(!hasAlternative(task)){
//			return -1;
//		}
//		return this.taskAlternatives.get(task);
//	}

//	/** // TODO refactor as OBSERVER
//	 * Checks whether the given Tasks has alternative Tasks.
//	 * 
//	 * @param 	task
//	 * 			The Task to check.
//	 * @return	True if and only the given Task has alternatives.
//	 * 			False if the Task IS isn't a valid one.
//	 */
//	public boolean hasAlternative(Integer taskID){
//		if(!isValidTaskID(taskID))
//			return false;
//		return this.taskAlternatives.containsKey(taskID);
//	}

//	/** // TODO refactor as OBSERVER
//	 * Add an alternative Task to the list of alternatives of the given Task.
//	 * 
//	 * @param 	toReplace
//	 * 			The Task to replace.
//	 * @param 	alternative
//	 * 			The new alternative.
//	 * @return	True if and only if the addition was successful.
//	 * 			True of toReplace ==-1
//	 * 			False if the alternative isn't valid.
//	 */
//	private boolean addAlternative(int toReplace, int alternative){
//		if(toReplace == -1) 
//			return true;
//		if(!isValidAlternative(toReplace, alternative))
//			return false;
//		
//		this.taskAlternatives.put(toReplace, alternative);
//		return true;
//
//	}

//	/**
//	 * Checks whether the given alternative is a valid one for the given Task.
//	 * 
//	 * @param 	toReplace
//	 * 			The task to replace
//	 * @param 	alternative
//	 * 			The alternative to check.
//	 * @return	True if and only the alternative is valid one.
//	 * 			True is toReplace == -1
//	 * 			False if the ID isn't a valid one
//	 * 			False if toReplace hasn't failed
//	 * 			False if teReplace has already an alternative
//	 */
//	TODO deze check is eigenlijk nog wel altijd belangrijk
//	TODO niet op deze plek
//	private boolean isValidAlternative(int toReplace, int alternative){
//		if(toReplace == -1) // deze niet
//			return true;
//		if(!isValidTaskID(toReplace)) // deze ook niet
//			return false;
//		if(taskAlternatives.containsKey(toReplace)) // vraag aan task da je wilt vervangen
//			return false;
//		if(!this.getTask(toReplace).isFailed()) // same
//			return false;
//		return toReplace != alternative; // same
//	}

//	/** // TODO naar task
//	 * Adds new prerequisites for the given Task.
//	 * 
//	 * @param 	task
//	 * 			The Task to add the new prerequisites to.
//	 * @param 	pre
//	 * 			The new prerequisites.
//	 * @return	True if and only the addition was successful.
//	 * 			True if the prerequisites are empty
//	 * 			False if the prerequisites aren't valid
//	 */
//	private boolean addPrerequisites(int taskID, List<Integer> pre){
//		if (pre.isEmpty()) return true;
//		if(!isValidPrerequisites(taskID, pre)){
//			return false;
//		}
//		for (int prereq : pre) {
//			if (getTask(prereq).isFailed() && hasAlternative(prereq)){
//				pre.remove(prereq);
//				pre.add(getAlternative(prereq));
//			}
//		}
//		
//		taskPrerequisites.put(taskID, pre);
//		return true;
//	}

//	/** // TODO remove this
//	 * Returns the number of Tasks in the project.
//	 * 
//	 * @return	The number of Tasks in the project.
//	 */
//	public int getTaskAmount() {
//		return this.taskList.size();
//	}

	/**
	 * Returns a list of the id's of the available tasks of the project
	 * 
	 * @return	a list of the availabke tasks' id's
	 */
	//TODO TaskViews hier aanmaken of pas in ProjectView?
	public ArrayList<Task> getAvailableTasks() {
		ArrayList<Task> availableTasks = new ArrayList<Task>();
		for(Task task : taskList) {
			if(task.isAvailable()) {
				availableTasks.add(task);
			}
		}
		return availableTasks;
	}
	
//	/** // TODO remove all these
//	 * Return the description of the Task belonging to the given ID.
//	 * 
//	 * @param 	taskID
//	 * 			The ID of the Task
//	 * @return	The description of the given Task.
//	 * 			Null if the given ID isn't a valid one.
//	 */
//	public String getTaskDescription(int taskID) {
//		if(!isValidTaskID(taskID)) {
//			return null;
//		}
//		return getTask(taskID).getDescription();
//	}
//
//	/**
//	 * Return the start time of the Task belonging to the given ID.
//	 * 
//	 * @param 	taskID
//	 * 			The ID of the Task.
//	 * @return	The start time of the Task.
//	 * 			Null if the Task hasn't started or the ID isn't a valid one.
//	 */
//	public LocalDateTime getTaskStartTime(int taskID) {
//		if(!isValidTaskID(taskID)) {
//			return null;
//		}
//		if(!hasTaskEnded(taskID)) {
//			return null;
//		}
//		return getTask(taskID).getStartTime();
//	}
//
//	/**
//	 * Returns the estimated duration of the Task belonging to the given ID.
//	 * 
//	 * @param 	taskID
//	 * 			The ID of the Task.
//	 * @return	The estimated duration of the Task.
//	 * 			-1 if the ID isn't a valid one.
//	 */
//	public int getEstimatedTaskDuration(int taskID) {
//		if(!isValidTaskID(taskID)) {
//			return -1;
//		}
//		return getTask(taskID).getEstimatedDuration().getSpanMinutes();
//	}
//
//	/**
//	 * Returns the estimated deviation of the Task belonging to the given ID.
//	 * 
//	 * @param 	taskID
//	 * 			The ID of the task.
//	 * @return	The acceptable deviation of the Task.
//	 * 			-1 if the ID isn't a valid one.
//	 */
//	public int getAcceptableTaskDeviation(int taskID) {
//		if(!isValidTaskID(taskID)) {
//			return -1;
//		}
//		return getTask(taskID).getAcceptableDeviation();
//	}
//
//	/**
//	 * Checks whether the Task belonging to the given ID has ended.
//	 * 
//	 * @param 	taskID
//	 * 			The ID of the Task.
//	 * @return	True if the Task has ended.
//	 * 			False if the ID isn't a valid one or the Task has ended.
//	 */
//	public boolean hasTaskEnded(int taskID) {
//		if(!isValidTaskID(taskID)) {
//			return false;
//		}
//		return getTask(taskID).hasEnded();
//	}
//
//	/**
//	 * Return the end time of the Task belonging to the given ID.
//	 * 
//	 * @param 	taskID
//	 * 			The ID of the given Task.
//	 * @return	The end time of the Task.
//	 * 			Null if the Task hasn't ended or the ID isn't a valid one.
//	 */
//	public LocalDateTime getTaskEndTime(int taskID) {
//		if(!isValidTaskID(taskID)) {
//			return null;
//		}
//		if(!hasTaskEnded(taskID)) {
//			return null;
//		}
//		return getTask(taskID).getEndTime();
//	}
//
//	/**
//	 * Returns the status of the Task belonging to the given ID.
//	 * 
//	 * @param 	taskID
//	 * 			The ID of the Task.
//	 * @return	The status of the Task.
//	 * 			Null if the ID isn't a valid one.
//	 */
//	public String getTaskStatus(int taskID) {
//		if(!isValidTaskID(taskID)) {
//			return null;
//		}
//		return getTask(taskID).getStatus();
//	}

//	/** // TODO naar Task
//	 * Checks whether the given Task belonging to the given ID has prerequisites.
//	 * 
//	 * @param 	taskID
//	 * 			The ID of the task.
//	 * @return	True if the given Task has prerequisites.
//	 * 			False otherwise or the ID isn't a valid one.
//	 */
//	public boolean hasPrerequisites(int taskID) {
//		if(!isValidNewTaskID(taskID)) {
//			return false;
//		}
//		return this.taskPrerequisites.containsKey(taskID);
//	}

//	/**
//	 * Returns the prerequisites of the Task belonging to the given ID if any.
//	 * 
//	 * @param 	taskID
//	 * 			The ID of the Task.
//	 * @return	The list of the prerequisites if any.
//	 * 			Null otherwise.
//	 */
//	//TODO REFACTOR AS OBSERVER + move to Task
//	public List<Integer> getPrerequisites(int taskID) {
//		if(!isValidNewTaskID(taskID)) {
//			return null;
//		}
//		return this.taskPrerequisites.get(taskID);
//	}

	/**
	 * Returns whether the project is on time;
	 * 
	 * @param 	current
	 * 			The current time to compare with.
	 * @return	True if the end time comes before the due time
	 * 			True if the project has not yet finished
	 * 			False otherwise.
	 */
	public boolean isOnTime(LocalDateTime current){
		if(endTime == null) {
			if(current.isAfter(dueTime)) {
				return false; //TODO berekenen
			} else {
				return true;
			}
		}
		else{
			return endTime.isBefore(dueTime);
		}
	}

	/**
	 * Returns the delay of the project if any.
	 * 
	 * @param 	current
	 * 			The current time to check with.
	 * @return	The delay of the project
	 * 			a zero array if there isn't any.
	 */
	public int[] getDelay(LocalDateTime current){
		if (current.isBefore(dueTime)) {
			return new int[] { 0,0,0,0,0};		
		} 
		TimeSpan delay = new TimeSpan(current, dueTime);
		return delay.getSpan();
		
	}

	/**
	 * Sets the task with the given task id to finished
	 * 
	 * @param 	task
	 * 			the id of the given task
	 * @param 	startTime
	 * 			the start time of the given task
	 * @param 	endTime
	 * 			the end time of the given task
	 * @return	True if setting the task to finished was successful,
	 * 			False if it was unsuccessful
	 * 			False if the task ID isn't a valid one
	 * 			False if the start time is null
	 * 			False if the start time is before creation time
	 */
	public boolean setTaskFinished(TaskView taskView, LocalDateTime startTime, LocalDateTime endTime) {
//		if(!isValidTaskID(taskID)) {
//			return false;
//		}
		Task task = unwrapTaskView(taskView);
		if(task == null ||startTime == null || startTime.isBefore(creationTime)) {
			return false;
		}
		boolean success = task.setTaskFinished(startTime, endTime);
		if(success) { // TODO notify observers IN TASK
//			for(Task t : taskList) {
//				updateTaskStatus(t);
//			}
			
			recalculateProjectStatus();
			
			//TODO laat status dit afhandelen
//			if(this.projectStatus==ProjectStatus.FINISHED){
//				this.endTime = endTime;
//			}
		}
		return success;
	}

	/**
	 * Sets the task with the given task id to failed
	 * 
	 * @param 	task
	 * 			the id of the given task
	 * @param 	startTime
	 * 			the start time of the given task
	 * @param 	endTime
	 * 			the end time of the given task
	 * @return	True if setting the task to failed was successful,
	 * 			False if it was unsuccessful
	 * 			False is the ID isn't a valid one
	 * 			False if the start time is null
	 * 			False if the start time is before creation time
	 */
	public boolean setTaskFailed(TaskView taskView, LocalDateTime startTime, LocalDateTime endTime) {
//		if(!isValidTaskID(taskID)) {
//			return false;
//		}
		Task task = unwrapTaskView(taskView);
		if(task == null ||startTime == null || startTime.isBefore(creationTime)) {
			return false;
		}
		boolean success = task.setTaskFailed(startTime, endTime);
//		if(success) { // Notify Observers
//			for(Task t : taskList)
//				updateTaskStatus(t);
//			return true;
//		}
		return success;
	}

//	/** // TODO remove this
//	 * Returns whether the current task in unacceptably overdue.
//	 * 
//	 * @param	TaskID
//	 * 			The ID of the Task.
//	 * @return	True if the project is overtime beyond the deviation.
//	 * 			False otherwise.
//	 */
//	public boolean isTaskUnacceptableOverdue(int taskID) {
//		if(!isValidTaskID(taskID)) {
//			return false;
//		}
//		return getTask(taskID).isUnacceptableOverdue();
//	}

//	/** // TODO remove this
//	 * Returns whether the current Task in on time.
//	 * 
//	 * @return	True if the Task is on time.
//	 * 			False if the elapsed time is longer then the acceptable duration.
//	 */
//	public boolean isTaskOnTime(int taskID) {
//		if(!isValidTaskID(taskID)) {
//			return false;
//		}
//		return getTask(taskID).isOnTime();
//	}

//	/** // TODO remove this
//	 * Determine the percentage of over time for a certain task
//	 * 
//	 * @param	taskID
//	 * 			the given task
//	 * @return	The percentage of overdue
//	 * 			-1 if the ID isn't a valid one.
//	 */
//	public int getTaskOverTimePercentage(int taskID) {
//		if(!isValidTaskID(taskID)) {
//			return -1;
//		}
//		return getTask(taskID).getOverTimePercentage();
//	}

	/**
	 * A method to check whether this project is finished
	 * 
	 * @return	True if and only if this project is finished
	 */
	public boolean isFinished() {
		//TODO vraag aan status
		return projectStatus == ProjectStatus.FINISHED;
	}

	/**
	 * Returns the estimated time until the project should end
	 * 
	 * @param	projectID
	 * 			the id of the given project
	 * @return	The amount of years, months, days, hours and minutes
	 * 			that are estimated to be required to finish the project
	 */
	public int[] getEstimatedProjectDelay(LocalDateTime currentTime) {
		if(!hasAvailableTasks()) {
			return new TimeSpan(0).getSpan();
		}

		// FOR EACH AVAILABLE TASK CALCULATE CHAIN
		int availableBranches = getAvailableTasks().size();
		TimeSpan[] timeChains = new TimeSpan[availableBranches];
		Task testTask;
		for(int i = 0 ; i < availableBranches ; i++) {
			testTask = getAvailableTasks().get(i);
			timeChains[i] = testTask.getEstimatedDuration().add(getMaxDelayChain(testTask));
		}
		// FIND LONGEST CHAIN
		TimeSpan longest = new TimeSpan(0);
		for(TimeSpan span : timeChains) {
			if(span.isLonger(longest)) {
				longest = span;
			}
		}

		// SUBTRACT TIME UNTIL DUE TIME FROM CHAIN (5/7 week, 8 hours/day)
		TimeSpan timeUntilDue = TimeSpan.getDifferenceWorkingMinutes(currentTime, dueTime);
		
		// RESULT
		return longest.minus(timeUntilDue);
	}

	/**
	 * Determine the longest timespan needed for a chain of dependant tasks.
	 * The timespan is the largest sum of the estimated durations of the dependant
	 * task of the given task and the longest chain of it's dependancies
	 * 
	 * @param	taskID
	 * 			the given task
	 * @return	The longest chain of durations possible from the given task
	 * 			null if the task ID isn't a valid one
	 */
	private TimeSpan getMaxDelayChain(Task task) {
//		if(!isValidTaskID(taskID)) {
//			return null;
//		}
//		if(!isPrerequisite(taskID)) {
//			return new TimeSpan(0);
//		}
		// TODO hoe nu?
		return task.getMaxDelayChain();
//		List<Task> dependants = getDependants(task);
//		TimeSpan longest = new TimeSpan(0);
//		TimeSpan chain;
//		for(Integer dependant : dependants) {
//			chain = getTask(dependant).getEstimatedDuration().add(getMaxDelayChain(dependant));
//			if(chain.isLonger(longest)) {
//				longest = chain;
//			}
//		}
//		return longest;
	}

//	/** // TODO naar Task
//	 * A method to determine if a task is a prerequisite to another task
//	 * 
//	 * @param	taskID
//	 * 			the given task
//	 * @return	True if and only if the supplied task is a prerequisite to another task
//	 * 			False if the ID isn't a valid one
//	 */
//	private boolean isPrerequisite(int taskID) {
//		if(!isValidTaskID(taskID)) {
//			return false;
//		}
//		Set<Integer> hasPrereq = taskPrerequisites.keySet();
//		for(Integer taskWithPrereq : hasPrereq) {
//			if(taskPrerequisites.get(taskWithPrereq).contains(taskID)) {
//				return true;
//			}
//		}
//		return false;
//	}

//	/** // TODO naar Task
//	 * A method to retrieve all task identifiers from tasks that are dependent
//	 * on the supplied task identifier
//	 * 
//	 * @param	taskID
//	 * 			the given task
//	 * @return	A list of task identifiers from tasks that are dependent on the given task
//	 * 			null if the ID isn't a valid one
//	 */
//	private List<Task> getDependants(int taskID) {
//		if(!isValidTaskID(taskID)) {
//			return null;
//		}
//		Set<Task> hasPrereq = taskPrerequisites.keySet();
//		ArrayList<Task> dependants = new ArrayList<Task>();
//		for(Task taskWithPrereq : hasPrereq) {
//			if(taskPrerequisites.get(taskWithPrereq).contains(taskID)) {
//				dependants.add(taskWithPrereq);
//			}
//		}
//		return dependants;
//	}

	/**
	 * A check to determine if the project will end on time
	 * 
	 * @param	currentTime
	 * 			The current time of the system
	 * @return	True if the estimated required time to finish all tasks is
	 * 			less than the time until the project due time
	 */
	public boolean isEstimatedOnTime(LocalDateTime currentTime) {
		TimeSpan estimatedDuration = new TimeSpan(getEstimatedProjectDelay(currentTime));
		return estimatedDuration.isZero();
	}

	/**
	 * Returns whether or not this project has any tasks available
	 * 
	 * @return	True if there is a task assigned to this project which is available
	 */
	private boolean hasAvailableTasks() {
		return getAvailableTasks().size() > 0;
	}
}
