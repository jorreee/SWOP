package taskMan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import taskMan.util.TimeSpan;

import com.google.common.collect.ImmutableList;


//TODO voorspellen of task op tijd afgehandled kan worden
//TODO methode om te weten hoeveel uw project te laat is
//TODO werken met taskID ipv task objecten.
//TODO isontime boolean, getDelay int 

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
	 * @throws	IllegalArgumentException
	 * 			Throws exception when the dueTime comes before the creationTime.
	 */
	public Project(int projectID, String projectName, String description,
			LocalDateTime creationTime, LocalDateTime dueTime) throws IllegalArgumentException{
		if(dueTime==null || creationTime==null)
			throw new IllegalArgumentException("One of the arguments is null");
		if(dueTime.isBefore(creationTime))
			throw new IllegalArgumentException("Duetime comes before the creationTime");
		if(dueTime.equals(creationTime))
			throw new IllegalArgumentException("No time difference");
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
			int acceptableDeviation, String taskStatus, int alternativeFor, 
			List<Integer> prerequisiteTasks, LocalDateTime startTime, LocalDateTime endTime) {
		if(description==null) // A task must have a description
			return false;
		if(!isValidEstimatedTaskDuration(estimatedDuration)) // A task must have a valid estimated duration
			return false;
		if(!isValidTaskID(alternativeFor) && alternativeFor != -1)
			return false;
		TaskStatus status = null;
		if(taskStatus != null)
			status = TaskStatus.valueOf(taskStatus);
		Task newTask = null;
		try{
			if(status != null)
				newTask = new Task(taskList.size(), description, estimatedDuration, 
						acceptableDeviation, status, startTime, endTime);
			else
				newTask = new Task(taskList.size(), description, estimatedDuration, acceptableDeviation);
		}catch(IllegalArgumentException e){
			return false;
		}
		if(prerequisiteTasks.contains(alternativeFor)) return false;
		if(!isValidAlternative(alternativeFor, newTask.getTaskID())) return false;
		if(!addAlternative(alternativeFor, newTask.getTaskID()))
			return false;
		if(!isValidPrerequisites(newTask.getTaskID(), prerequisiteTasks)){
			return false;
		}
		if(!addPrerequisites(newTask.getTaskID(), prerequisiteTasks))
			return false;
		updateTaskStatus(newTask);
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
	public boolean createTask(String description, int estimatedDuration, 
			int acceptableDeviation,int alternativeFor, List<Integer> prerequisiteTasks){
		return createTask(description, estimatedDuration, acceptableDeviation, null, alternativeFor, prerequisiteTasks, null, null);
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
		if(taskAlternatives.get(task) == null)
			return false;
		return getTask(taskAlternatives.get(task)).isFinished() || hasFinishedAlternative(taskAlternatives.get(task));

	}

	private void updateTaskStatus(Task task){
		if(!task.hasEnded()){
			if(hasPrerequisites(task.getTaskID())) {
				for(Integer preID : getPrerequisites(task.getTaskID())){
					if(!(getTask(preID).isFinished() || getTask(preID).isFailed())){
						task.setUnavailable();
						return;
					}
					if(getTask(preID).isFailed() && (!hasAlternative(preID) || !hasFinishedAlternative(preID))) {
						task.setUnavailable();
						return;
					}
				}
			}
			task.setAvailable();
		}
	}

	/**
	 * This method will adjust the status of the project, depending on its tasks.
	 * If the project has at least one task and all of those tasks are finished (or failed with a finished alternative),
	 * the project itself will be considered finished.
	 */
	private void recalculateProjectStatus() {
		for(Task task : taskList) {
			String status = task.getTaskStatusName();
			if( status.equals("AVAILABLE") || status.equals("UNAVAILABLE"))
				return;
			if( status.equals("FAILED")) {
				if(!hasFinishedAlternative(task.getTaskID()))
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
		if(taskID<=this.getTaskAmount() && taskID >= 0){
			return true;
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
		if(task == -1) return true;
		if(!isValidAlternative(task, alternative))
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
	 * @param 	oldTask
	 * 			The task to add the alternative to.
	 * @param 	alt
	 * 			The alternative to check.
	 * @return	True if and only the alternative is valid one.
	 */
	private boolean isValidAlternative(int oldTask,int alt){
		if(oldTask == -1) return true;
		if(!isValidTaskID(oldTask)) return false;
		if(taskAlternatives.containsKey(oldTask)) return false;
		return oldTask!=alt;
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
	private boolean addPrerequisites(int taskID, List<Integer> pre){
		if (pre.isEmpty()) return true;
		if(!isValidPrerequisites(taskID, pre)){
			return false;
		}
		for (int prereq : pre) {
			if (getTask(prereq).isFailed() && hasAlternative(prereq)){
				pre.remove(prereq);
				pre.add(getAlternative(prereq));
			}
		}
		if(hasPrerequisites(taskID)){
			
			List<Integer> preOld = getPrerequisites(taskID);
			pre.addAll(preOld);
			taskPrerequisites.put(taskID, pre);
			return true;
		}
		else { taskPrerequisites.put(taskID, pre);
			return true; }
}

	/**
	 * Checks whether the given prerequisites are valid for the given Task.
	 * 
	 * @param 	task
	 * 			The given Task.
	 * @param 	pre
	 * 			The prerequisites to check.
	 * @return	True if and only the prerequisites are a valid.
	 */
	private boolean isValidPrerequisites(int task, List<Integer> pre){
		if (pre == null) return false;
		if (!isValidTaskID(task)) return false;
		else if (pre.isEmpty()) return true;
		else if(pre.contains(task)) return false;
		for (int prereq : pre){
			if (!isValidTaskID(prereq)){
				return false;
			}
		}
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
		return this.taskList.size();
	}

	/**
	 * Returns a list of the id's of the available tasks of the project
	 * 
	 * @return	a list of the availabke tasks' id's
	 */
	public List<Integer> getAvailableTasks() {
		ArrayList<Integer> availableTasks = new ArrayList<Integer>();
		for(Task task : taskList) {
			if(task.getStatus().equals("available"))
				availableTasks.add(task.getTaskID());
		}
		return availableTasks;
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

//	/**
//	 * Checks whether the Task belonging to the given ID has started.
//	 * 
//	 * @param 	taskID
//	 * 			The ID of the Task to check.
//	 * @return	True is the Task has started.
//	 * 			False otherwise or if the ID isn't a valid one.
//	 */
//	public boolean hasTaskStarted(int taskID) {
//		if(!isValidTaskID(taskID))
//			return false;
//		return getTask(taskID).hasStarted();
//	}

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
		if(!hasTaskEnded(taskID))
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
		return getTask(taskID).getEstimatedDuration().getSpanMinutes();
	}

	/**
	 * Returns whether a given estimated duration is a valid estimated task duration
	 * @param 	estimatedDuration
	 * 			the estimated duration
	 * @return	True if the estimated duration is valid,
	 * 			False if the estimated duration is not valid
	 */
	private boolean isValidEstimatedTaskDuration(int estimatedDuration) {
		return estimatedDuration > 0;
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

	/**
	 * Returns the delay of the project if any.
	 * 
	 * @param 	current
	 * 			The current time to check with.
	 * @return	The delay of the project
	 */
	public int[] getDelay(LocalDateTime current){
		if (current.isBefore(dueTime)) {
			return new int[] { 0,0,0,0,0};		
		}
		else {
			TimeSpan delay = new TimeSpan(current, dueTime);
			return delay.getSpan();
		}
	}

	/**
	 * Sets the task with the given task id to finished
	 * @param 	taskID
	 * 			the id of the given task
	 * @param 	startTime
	 * 			the start time of the given task
	 * @param 	endTime
	 * 			the end time of the given task
	 * @return	True if setting the task to finished was successful,
	 * 			False if it was unsuccessful
	 */
	public boolean setTaskFinished(int taskID, LocalDateTime startTime, LocalDateTime endTime) {
		if(startTime == null || startTime.isBefore(creationTime))
			return false;
		boolean success = getTask(taskID).setTaskFinished(startTime, endTime);
		if(success) {
			for(Task task : taskList)
				updateTaskStatus(task);
			recalculateProjectStatus();
			if(this.projectStatus==ProjectStatus.FINISHED){
				this.endTime = endTime;
			}
			return true;
		}
		return false;
	}

	/**
	 * Sets the task with the given task id to failed
	 * @param 	taskID
	 * 			the id of the given task
	 * @param 	startTime
	 * 			the start time of the given task
	 * @param 	endTime
	 * 			the end time of the given task
	 * @return	True if setting the task to failed was successful,
	 * 			False if it was unsuccessful
	 */
	public boolean setTaskFailed(int taskID, LocalDateTime startTime, LocalDateTime endTime) {
		if(startTime == null || startTime.isBefore(creationTime))
			return false;
		boolean success = getTask(taskID).setTaskFailed(startTime, endTime);
		if(success) {
			for(Task task : taskList)
				updateTaskStatus(task);
			return true;
		}
		return false;
	}

	public boolean isTaskUnacceptableOverdue(int taskID) {
		return getTask(taskID).isUnacceptableOverdue(currentTime);
	}

	public boolean isTaskOnTime(int taskID) {
		return getTask(taskID).isOnTime(currentTime);
	}

	public int getTaskOverTimePercentage(int taskID) {
		return getTask(taskID).getOverTimePercentage(currentTime);
	}

	public boolean isFinished() {
		return getProjectStatus().equalsIgnoreCase("FINISHED");
	}
	
	/**
	 * Returns the estimated time until the project should end
	 * @param	projectID
	 * 			the id of the given project
	 * @return	The amount of years, months, days, hours and minutes
	 * 			that are estimated to be required to finish the project
	 */
	public int[] getEstimatedProjectDelay() {
		if(!hasAvailableTasks())
			return new TimeSpan(0).getSpan();
		
		// FOR EACH AVAILABLE TASK CALCULATE CHAIN
		int availableBranches = getAvailableTasks().size();
		TimeSpan[] timeChains = new TimeSpan[availableBranches];
		Integer testTask;
		ArrayList<Integer> ch;
		TimeSpan longestPre = new TimeSpan(0);
		for(int i = 0 ; i < availableBranches ; i++) {
			testTask = getAvailableTasks().get(i);
			timeChains[i] = getTask(testTask).getEstimatedDuration().add(getMaxDelayChain(testTask));
		}
		// FIND LONGEST CHAIN
		TimeSpan longest = new TimeSpan(0);
		for(TimeSpan span : timeChains) {
			if(span.isLonger(longest))
				longest = span;
		}
		
		// SUBTRACT TIME UNTIL DUE TIME FROM CHAIN (5/7 week, 8 hours/day)
		
		
		// RESULT
		return null;
	}
	
	private TimeSpan getMaxDelayChain(int taskID) {
		if(!isPrerequisite(taskID))
			return new TimeSpan(0);
		List<Integer> dependants = getDependants(taskID);
		TimeSpan longest = new TimeSpan(0);
		TimeSpan chain;
		for(Integer dependant : dependants) {
			chain = getTask(dependant).getEstimatedDuration().add(getMaxDelayChain(dependant));
			if(chain.isLonger(longest))
				longest = chain;
		}
		return longest;
	}
	
	private boolean isPrerequisite(int taskID) {
		Set<Integer> hasPrereq = taskPrerequisites.keySet();
		for(Integer taskWithPrereq : hasPrereq) {
			if(taskPrerequisites.get(taskWithPrereq).contains(taskID))
				return true;
		}
		return false;
	}
	
	private List<Integer> getDependants(int taskID) {
		Set<Integer> hasPrereq = taskPrerequisites.keySet();
		ArrayList<Integer> dependants = new ArrayList<Integer>();
		for(Integer taskWithPrereq : hasPrereq) {
			if(taskPrerequisites.get(taskWithPrereq).contains(taskID))
				dependants.add(taskWithPrereq);
		}
		return dependants;
	}

	public boolean isEstimatedOnTime() {
		TimeSpan estimatedDuration = new TimeSpan(getEstimatedProjectDelay());
		return estimatedDuration.isZero();
	}
	
	private boolean hasAvailableTasks() {
		return getAvailableTasks().size() > 0;
	}
}
