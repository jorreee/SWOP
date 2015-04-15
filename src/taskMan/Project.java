package taskMan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import taskMan.resource.ResourceManager;
import taskMan.state.OngoingProject;
import taskMan.state.ProjectStatus;
import taskMan.util.Dependant;
import taskMan.util.TimeSpan;
import taskMan.view.ResourceView;
import taskMan.view.TaskView;

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
	private int nextTaskID;

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
		nextTaskID = 0;
		
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
	
//	/**
//	 * Find a specific task in this project by id
//	 * 
//	 * @param taskID
//	 *            the ID of the task to look for
//	 * @return The task with the specified ID or null when no task is found
//	 */
//	private Task findTask(int taskID) {
//		for(Task t : taskList) {
//			if(t.getID() == taskID)
//				return t;
//		}
//		return null;
//	}

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
						Map<ResourceView, Integer> requiredResources, 
						TaskView alternativeFor, 
						String taskStatus,
						LocalDateTime startTime, 
						LocalDateTime endTime,
						LocalDateTime plannedStartTime,
						List<ResourceView> plannedDevelopers) {

		int newTaskID = nextTaskID;
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
						requiredResources, 
						altFor,
						taskStatus, 
						startTime, 
						endTime,
						plannedStartTime,
						plannedDevelopers);
			}
			else {
				newTask = new Task(
						newTaskID, 
						description, 
						estimatedDuration, 
						acceptableDeviation, 
						resMan, 
						prereqTasks,
						requiredResources, 
						altFor);
			}
		} catch(IllegalArgumentException e) {
			return false;
		}
		
		boolean success = taskList.add(newTask);
		
		if(success) {
			newTask.register(this);
			nextTaskID++;
		}
		return success;
	}
	
//	/**
//	 * Creates a new Task without a set status.
//	 * 
//	 * @param 	description
//	 * 			The description of the given Task.
//	 * @param 	estimatedDuration
//	 * 			The estimated duration of the Task.
//	 * @param 	acceptableDeviation
//	 * 			The acceptable deviation of the Task.
//	 * @param 	alternativeFor
//	 * 			The alternative Task.
//	 * @param 	prerequisiteTasks
//	 * 			The prerequisites Tasks for this Task.
//	 * @return	True if the creation of a new Task was successful.
//	 */
//	public boolean createTask(String description, 
//			int estimatedDuration, 
//			int acceptableDeviation, 
//			ResourceManager resMan, 
//			List<TaskView> prerequisiteTasks, 
//			Map<ResourceView, Integer> requiredResources, 
//			TaskView alternativeFor) {
//		
//		return createTask(description, 
//				estimatedDuration, 
//				acceptableDeviation, 
//				resMan, 
//				prerequisiteTasks,
//				requiredResources, 
//				alternativeFor, 
//				null,
//				null, 
//				null);
//	}
	
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
		if(getEndTime()!=null) {
			throw new IllegalArgumentException("The endtime is already set");
		}
		this.endTime = endTime;
	}

	/**
	 * Returns the ID of the Project.
	 * 
	 * @return	The ID of the Project.
	 */
	public int getID() {
		return projectID;
	}
	
	/**
	 * Returns the name of this Project.
	 * 
	 * @return	The name of this Project.
	 */
	public String getName() { 
		return projectName; 
	}

	/**
	 * Returns the description of this Project.
	 * 
	 * @return	The description of this Project.
	 */
	public String getDescription() {	
		return description;	
	}

	/**
	 * Returns the creation time of this Project.
	 * 
	 * @return	The creation time of this Project.
	 */
	public LocalDateTime getCreationTime() { 
		return creationTime; 
	}

	/**
	 * Get the due time of this Project.
	 * 
	 * @return	The due time of this Project.
	 */
	public LocalDateTime getDueTime() { 
		return dueTime; 
	}

	/**
	 * Returns the end time of this Project.
	 * 
	 * @return	The end time of this Project.
	 */
	public LocalDateTime getEndTime() { 
		return endTime; 
	}

	/**
	 * Returns the status of this Project.
	 * 
	 * @return	The status of this Project.
	 */
	public String getStatus() { 
		return state.toString();
	}

	/**
	 * Returns the list containing all known Tasks.
	 * 
	 * @return	A list of Tasks.
	 */
	public List<TaskView> getTaskViews(){
		ArrayList<TaskView> tasks = new ArrayList<TaskView>();
		for(Task t : taskList) {
			tasks.add(new TaskView(t));
		}
		return tasks;
	}
	
	/**
	 * Returns a list of the id's of the available tasks of the project
	 * 
	 * @return	a list of the available tasks' id's
	 */
	private List<Task> getAvailableTasks() {
		ArrayList<Task> availableTasks = new ArrayList<Task>();
		for(Task task : taskList) {
			if(task.isAvailable()) {
				availableTasks.add(task);
			}
		}
		return availableTasks;
	}

//	/**
//	 * Returns whether the project is on time;
//	 * 
//	 * @param 	current
//	 * 			The current time to compare with.
//	 * @return	True if the end time comes before the due time
//	 * 			True if the project has not yet finished
//	 * 			False otherwise.
//	 */
//	public boolean isOnTime(LocalDateTime current){
//		return new TimeSpan(getEstimatedDelay(current)).isZero();
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
//	}
//
//	/**
//	 * Returns the real-time delay of the project. It compares the current time to the
//	 * due date of the project.
//	 * 
//	 * @param 	current
//	 * 			The current time to check with.
//	 * @return	The delay of the project
//	 * 			a zero array if there isn't any.
//	 */
//	public int[] getDelay(LocalDateTime current){
//		if (current.isBefore(dueTime)) {
//			return new int[] { 0,0,0,0,0 };		
//		} 
//		TimeSpan delay = new TimeSpan(dueTime, current);
//		return delay.getSpan();
//		
//	}
	
	public boolean setTaskFinished(TaskView t, LocalDateTime endTime) {
		if(!isValidTaskView(t)) {
			return false;
		}
//		if(startTime.isBefore(creationTime)) {
//			return false;
//		}
		return unwrapTaskView(t).finish(endTime);
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
	public boolean setTaskFailed(TaskView t, LocalDateTime endTime) {
		if(!isValidTaskView(t)) {
			return false;
		}
//		if(startTime.isBefore(creationTime)) {
//			return false;
//		}
		return unwrapTaskView(t).fail(endTime);
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
	 * Calculate the estimated end time of the project, depending on
	 * the task that has the longest delay chain. If no tasks are
	 * currently available it will return the current Time. 
	 * 
	 * @return The estimated end date of the project. Will always be 
	 */
	public LocalDateTime getEstimatedEndTime(LocalDateTime currentTime) {
		if(isFinished()) {
			return endTime;
		}
		List<Task> availableTasks = getAvailableTasks();
		LocalDateTime furthestEndDate = currentTime;
		
		if(availableTasks.size() == 0) {
			return furthestEndDate;
		}
		LocalDateTime candidate = furthestEndDate;
		for(Task t : availableTasks) {
			if(t.getBeginTime() != null) {
				candidate = TimeSpan.addSpanToLDT(t.getBeginTime(), t.getMaxDelayChain());
			} else { 
				candidate = TimeSpan.addSpanToLDT(currentTime, t.getMaxDelayChain());
			}
			if(candidate.isAfter(furthestEndDate)) {
				furthestEndDate = candidate;
			}
		}
		
		return furthestEndDate;
		
	}
//
//	/**
//	 * Returns the estimated time that the project will be finished over time.
//	 * 
//	 * e.g. if the due time is tomorrow and there's an estimated 2 more days needed,
//	 * 		this method will return [0,0,1,0,0]
//	 * 
//	 * @param	projectID
//	 * 			the id of the given project
//	 * @return	The amount of years, months, days, hours and minutes
//	 * 			that are estimated to be required to finish the project
//	 */
//	public int[] getEstimatedDelay(LocalDateTime currentTime) {
//		if(getAvailableTasks().size() == 0) {
//			return new TimeSpan(0).getSpan();
//		}
//
//		LocalDateTime estimatedEndTime = getEstimatedEndTime(currentTime);
////		LocalDateTime estimatedEndTime = TimeSpan.addSpanToLDT(currentTime, getMaxTimeChainTask());
//		if(dueTime.isAfter(estimatedEndTime)) {
//			return new TimeSpan(0).getSpan();
//		}
//		
//		return new TimeSpan(TimeSpan.getDifferenceWorkingMinutes(estimatedEndTime, dueTime)).getSpan();
//		
//	}
	
//	private TimeSpan getMaxTimeChainTask() {
//		List<Task> availableTasks = getAvailableTasks();
//		
//		// FOR EACH AVAILABLE TASK CALCULATE CHAIN
//		int availableBranches = availableTasks.size();
//		TimeSpan[] timeChains = new TimeSpan[availableBranches];
//		for(int i = 0 ; i < availableBranches ; i++) {
//			timeChains[i] = availableTasks.get(i).getMaxDelayChain();
//		}
//		
//		// FIND LONGEST CHAIN
//		TimeSpan longest = new TimeSpan(0);
//		for(TimeSpan span : timeChains) {
//			if(span.isLonger(longest)) {
//				longest = span;
//			}
//		}
//		return longest;
//	}
	
	/**
	 * Returns an amount of possible Task starting times for a given Task.
	 * 
	 * @param 	task
	 * 			The Task to get the starting times from.
	 * @param 	amount
	 * 			The amount of possible starting times wanted.
	 * @return	The possible starting times of the Task
	 */
	public List<LocalDateTime> getPossibleTaskStartingTimes(TaskView task, int amount){
		return unwrapTaskView(task).getPossibleTaskStartingTimes(amount);
	}

	public boolean flushFutureReservations(TaskView task) {
		Task t = unwrapTaskView(task);
		if(t == null) {
			return false;
		}
		return t.flushFutureReservations();
	}

}
