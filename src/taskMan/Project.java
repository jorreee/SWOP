package taskMan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import taskMan.resource.ResourceManager;
import taskMan.state.OngoingProject;
import taskMan.state.ProjectStatus;
import taskMan.util.Dependant;
import taskMan.util.IntPair;
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
public class Project implements Dependant {

	private final int projectID;
	private final String projectName;
	private final String description;
	private final LocalDateTime creationTime;
	private final LocalDateTime dueTime;
	private LocalDateTime endTime;

	private ProjectStatus state;

	private ArrayList<Task> taskList;

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
			LocalDateTime creationTime, LocalDateTime dueTime) throws IllegalArgumentException {

		if(!isValidProjectID(projectID)) {
			throw new IllegalArgumentException("Invalid project ID");
		}
		if(!isValidName(projectName)) {
			throw new IllegalArgumentException("Invalid project name");
		}
		if(!isValidDescription(description)) {
			throw new IllegalArgumentException("Invalid project description");
		}
		if(!isValidCreationAndDueTimes(creationTime,dueTime)) {
			throw new IllegalArgumentException("Very bad times");
		}
		
		this.projectName = projectName;
		this.description = description;
		this.creationTime = creationTime;
		this.dueTime = dueTime;
		this.projectID = projectID;
		
		this.taskList = new ArrayList<Task>();
		
		this.state = new OngoingProject(this);

	}
	
	private boolean isValidProjectID(int pID) {
		if(pID < 0) {
			return false;
		}
		return true;
	}
	
	private boolean isValidName(String name) {
		if(name  == null) {
			return false;
		}
		return true;
	}
	
	private boolean isValidDescription(String description) {
		if(description  == null) {
			return false;
		}
		return true;
	}
	
	private boolean isValidCreationAndDueTimes(LocalDateTime creationTime, LocalDateTime dueTime) {
		if(creationTime == null || dueTime == null) {
			return false;
		}
		if(!creationTime.isBefore(dueTime)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Creates a Raw Task: A Task as issued by the input file.
	 * 
	 * @param 	description
	 * 			The description of the Task.
	 * @param 	estimatedDuration
	 * 			The estimated duration of the Task.
	 * @param	 acceptableDeviation
	 * 			The acceptable deviation of the Task.
	 * @param	prerequisiteTasks
	 * 			The list of prerequisites of the Task.
	 * @param 	alternativeFor
	 * 			The alternative for the Task.
	 * @param 	taskStatus
	 * 			The status of the Task.
	 * @param 	startTime
	 * 			The start time of the Task.
	 * @param 	endTime
	 * 			The end time of the Task.
	 * @return	True if and only if the creation of the Raw Task
	 * 			was successful.
	 */
	public boolean createRawTask(String description, 
			int estimatedDuration, 
			int acceptableDeviation, 
			ResourceManager resMan, 
			List<Integer> prerequisiteTasks, 
			int alternativeFor, 
			String taskStatus,
			LocalDateTime startTime, 
			LocalDateTime endTime) {
		
		List<TaskView> prereqTaskViews = new ArrayList<TaskView>();
		TaskView altTaskView = null;
		
		// FIND prereqs
		for(Integer prereqID : prerequisiteTasks) {
			Task t = findTask(prereqID);
			if(t != null) {
				prereqTaskViews.add(new TaskView(t));
			}
			
		}
		// FIND alt
		if(alternativeFor != -1) {
			altTaskView = new TaskView(findTask(alternativeFor));
		}
		
		return createTask(description, estimatedDuration, acceptableDeviation, resMan, 
				prereqTaskViews, altTaskView, taskStatus, startTime, endTime);
	}
	
	/**
	 * Find a specific task in this project by id
	 * 
	 * @param taskID
	 *            the ID of the task to look for
	 * @return The task with the specified ID or null when no task is found
	 */
	private Task findTask(int taskID) {
		for(Task t : taskList) {
			if(t.getTaskID() == taskID)
				return t;
		}
		return null;
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
						ResourceManager resMan, 
						List<TaskView> prerequisiteTasks, 
						TaskView alternativeFor, 
						String taskStatus,
						LocalDateTime startTime, 
						LocalDateTime endTime) {

		int newTaskID = taskList.size();
		if(isFinished()) {
			return false;
		}
		
		if(!isValidAlternative(alternativeFor)) {
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
		
		try{
			if(taskStatus != null) {
				newTask = new Task(
						newTaskID, 
						description, 
						estimatedDuration, 
						acceptableDeviation, 
						resMan, 
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
						resMan, 
						prereqTasks,
						altFor);
			}
		} catch(IllegalArgumentException e) {
			return false;
		}
		
		boolean success = taskList.add(newTask);
		
		if(success) {
			newTask.register(this);
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
			ResourceManager resMan, 
			List<TaskView> prerequisiteTasks,
			TaskView alternativeFor) {
		
		return createTask(description, 
				estimatedDuration, 
				acceptableDeviation, 
				resMan, 
				prerequisiteTasks,
				alternativeFor, 
				null,
				null, 
				null);
	}
	
	/**
	 * Checks whether the given TaskView is a valid TaskView representing a valid Task.
	 * 
	 * @param 	view
	 * 			The TaskView to check.
	 * @return	True if and only if the TaskView is a valid TaskView.
	 */
	private boolean isValidTaskView(TaskView view) {
		return taskList.contains(unwrapTaskView(view));
	}
	
	/**
	 * Checks whether the given TaskView represents a valid Alternative for a certain Task in this Project.
	 * @param 	view
	 * 			The TaskView to check.
	 * @return	True if and only if the TaskView represents a valid Alternative.
	 */
	private boolean isValidAlternative(TaskView view) {
		if(view == null) {
			return true;
		}
		if(isValidTaskView(view)) {
			Task task = unwrapTaskView(view);
			for(Task t : taskList) {
				if(t.getAlternativeFor() == task) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Unwraps the TaskView object and returns the Task that it contained
	 * 		IF the unwrapped task belongs to this project:
	 * 				(getTaskList().contains(task)
	 * 
	 * @param 	view
	 * 			the TaskView to unwrap
	 * @return 	the unwrapped Task if it belonged to this project
	 * 			NULL otherwise
	 */
	private Task unwrapTaskView(TaskView view) {
		if(view == null) {
			return null;
		}
		for(Task task : taskList) {
			if (view.hasAsTask(task)) {
				return task;
			}
		}
		return null;
	}
	
	/**
	 * Sets the Project Status to the given Status.
	 * 
	 * @param 	newStatus
	 * 			The Status to change to.
	 * @return	True if and only if the Status change was succesful.
	 */
	public boolean setProjectStatus(ProjectStatus newStatus) {
		this.state = newStatus;
		return true;
	}

	/**
	 * Updates the Dependency of the Project by providing a finished Task.
	 * The finished Task will be removed from the unfinished Task list and it's chain of alternatives as well.
	 * The Project will also check if it can set it's status to finished.
	 * 
	 * @return 	True if and only if the removal of the Task and it's alternatives was successful.
	 * 
	 */
	@Override
	public boolean updateDependency(Task preTask) {
		if (!preTask.hasEnded()){
			return false;
		}
		return state.finish(taskList, preTask);
	}

	/**
	 * Sets the end time of the Project.
	 * 
	 * @param 	endTime
	 * 			The new end time of the Project.
	 * @throws	IllegalArgumentException
	 * 			If the new end time is null or the old end time is already set. 
	 */
	public void setEndTime(LocalDateTime endTime) throws IllegalArgumentException {
		if(endTime==null) {
			throw new IllegalArgumentException("The new endTime is null");
		}
		if(getProjectEndTime()!=null) {
			throw new IllegalArgumentException("The endtime is already set");
		}
		this.endTime = endTime;
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
		return state.toString();
	}

	/**
	 * Returns the list containing all known Tasks.
	 * 
	 * @return	A list of Tasks.
	 */
	public List<TaskView> getTasks(){
		ArrayList<TaskView> tasks = new ArrayList<TaskView>();
		for(Task t : taskList) {
			tasks.add(new TaskView(t));
		}
		return tasks;
	}

	/**
	 * Returns a list of the id's of the available tasks of the project
	 * 
	 * @return	a list of the availabke tasks' id's
	 */
	public ArrayList<TaskView> getAvailableTaskViews() {
		ArrayList<TaskView> availableTasks = new ArrayList<TaskView>();
		for(Task task : taskList) {
			if(task.isAvailable()) {
				availableTasks.add(new TaskView(task));
			}
		}
		return availableTasks;
	}
	
	/**
	 * Returns a list of the id's of the available tasks of the project
	 * 
	 * @return	a list of the availabke tasks' id's
	 */
	private ArrayList<Task> getAvailableTasks() {
		ArrayList<Task> availableTasks = new ArrayList<Task>();
		for(Task task : taskList) {
			if(task.isAvailable()) {
				availableTasks.add(task);
			}
		}
		return availableTasks;
	}

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
		return new TimeSpan(getEstimatedProjectDelay(current)).isZero();
//		if(endTime == null) {
//			if(current.isAfter(dueTime)) {
//				return false;
//			} else {
//				return true;
//			}
//		}
//		else{
//			return endTime.isBefore(dueTime);
//		}
	}

	/**
	 * Returns the real-time delay of the project. It compares the current time to the
	 * due date of the project.
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
		TimeSpan delay = new TimeSpan(dueTime, current);
		return delay.getSpan();
		
	}
	
	public boolean setTaskFinished(TaskView t, LocalDateTime startTime, LocalDateTime endTime) {
		if(!isValidTaskView(t)) {
			return false;
		}
		if(startTime.isBefore(creationTime)) {
			return false;
		}
		return unwrapTaskView(t).setTaskFinished(startTime, endTime);
	}
	
//	private boolean markTaskFinished(Task task) {
//		if(task == null) {
//			return true;
//		}
//		int taskIndex = unfinishedTaskList.indexOf(task);
//		if(taskIndex < 0) {
//			return false;
//		}
//		unfinishedTaskList.remove(taskIndex);
//
//		return true;
////		return markTaskFinished(task.getAlternativeFor());
//	}

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
	public boolean setTaskFailed(TaskView t, LocalDateTime startTime, LocalDateTime endTime) {
		if(!isValidTaskView(t)) {
			return false;
		}
		if(startTime.isBefore(creationTime)) {
			return false;
		}
		return unwrapTaskView(t).setTaskFailed(startTime, endTime);
	}

	/**
	 * A method to check whether this project is finished
	 * 
	 * @return	True if and only if this project is finished
	 */
	public boolean isFinished() {
		return state.isFinished();
	}

	/**
	 * Returns the estimated time that the project will be finished over time.
	 * 
	 * e.g. if the due time is tomorrow and there's an estimated 2 more days needed,
	 * 		this method will return [0,0,1,0,0]
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
		
		LocalDateTime estimatedEndTime = TimeSpan.addSpanToLDT(currentTime, getMaxTimeChain());
		if(dueTime.isAfter(estimatedEndTime)) {
			return new TimeSpan(0).getSpan();
		}
		
		return new TimeSpan(TimeSpan.getDifferenceWorkingMinutes(estimatedEndTime, dueTime)).getSpan();
		
	}
	
	private TimeSpan getMaxTimeChain() {
		List<Task> availableTasks = getAvailableTasks();
		
		// FOR EACH AVAILABLE TASK CALCULATE CHAIN
		int availableBranches = availableTasks.size();
		TimeSpan[] timeChains = new TimeSpan[availableBranches];
		for(int i = 0 ; i < availableBranches ; i++) {
			timeChains[i] = availableTasks.get(i).getMaxDelayChain();
		}
		
		// FIND LONGEST CHAIN
		TimeSpan longest = new TimeSpan(0);
		for(TimeSpan span : timeChains) {
			if(span.isLonger(longest)) {
				longest = span;
			}
		}
		return longest;
	}

	/**
	 * A check to determine if the project will end on time
	 * 
	 * @param	currentTime
	 * 			The current time of the system
	 * @return	True if the estimated required time to finish all tasks is
	 * 			less than the time until the project due time
	 */
	public boolean isEstimatedOnTime(LocalDateTime currentTime) {
//		TimeSpan estimatedDuration = new TimeSpan(getEstimatedProjectDelay(currentTime));
//		return estimatedDuration.isZero();
		
		if(isFinished()) {
			return !endTime.isAfter(dueTime);
		}
		
		LocalDateTime estimatedEndTime = TimeSpan.addSpanToLDT(currentTime, getMaxTimeChain());
		
		return !estimatedEndTime.isAfter(dueTime);
	}

	/**
	 * Returns whether or not this project has any tasks available
	 * 
	 * @return	True if there is a task assigned to this project which is available
	 */
	private boolean hasAvailableTasks() {
		return getAvailableTasks().size() > 0;
	}
	
	/**
	 * Creates a Raw Planned Task as issued by the input file.
	 * 
	 * @param 	description
	 * 			The description of the Task.
	 * @param 	estimatedDuration
	 * 			The estimated duration of the Task.
	 * @param 	acceptableDeviation
	 * 			The acceptable deviation of the Task.
	 * @param 	prerequisiteTasks
	 * 			The prerequisites of the Task.
	 * @param 	alternativeFor
	 * 			The alternative for the Task.
	 * @param 	statusString
	 * 			The status of the Task.
	 * @param 	startTime
	 * 			The startTime of the Task.
	 * @param 	endTime
	 * 			The endTime of the Task.
	 * @param 	planningDueTime
	 * 			The due time of the planning of the Task.
	 * @param 	plannedDevelopers
	 * 			The planned developers of the Task.
	 * @param 	plannedResources
	 * 			The planned resources of the Task.
	 * @return	True if and only if the creation of the Raw Planned Task was succesful.
	 */
	public boolean createRawPlannedTask(int project, String description,
			int estimatedDuration, int acceptableDeviation,
			List<Integer> prerequisiteTasks, int alternativeFor,
			String statusString, LocalDateTime startTime,
			LocalDateTime endTime, LocalDateTime planningDueTime,
			List<Integer> plannedDevelopers, List<IntPair> plannedResources) {
		return false; //TODO
	}
	
	/**
	 * Returns an amount of possible Task starting times for a given Task.
	 * 
	 * @param 	task
	 * 			The Task to get the starting times from.
	 * @param 	amount
	 * 			The amount of possible starting times wanted.
	 * @return	The possible starting times of the Task
	 */
	public ImmutableList<LocalDateTime> getPossibleTaskStartingTimes(TaskView task, int amount){
		return unwrapTaskView(task).getPossibleTaskStartingTimes(amount);
	}
}
