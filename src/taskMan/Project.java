package taskMan;

import java.time.LocalDateTime;
import java.time.LocalTime;
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * The project class used by TaskMan. A project will always have a name, a
 * description, a creation time, a due time, a status and a list containing all
 * tasks assigned to this project. A project has the responsibility to unwrap
 * taskViews in order for the tasks to communicate with the system. A project
 * will finish when all its tasks are finished or have a finished alternative
 * (in order to finish, a project must have at least one task). A project can be
 * either ongoing or finished.
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class Project implements Dependant {

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
	public Project(String projectName, String description,
			LocalDateTime creationTime, LocalDateTime dueTime) throws IllegalArgumentException {

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
		
		this.taskList = new ArrayList<Task>();
		
		this.state = new OngoingProject();

	}

	/**
	 * Check whether or not the given name is valid
	 * 
	 * @param name
	 *            | The name to check
	 * @return True if it is valid, false otherwise
	 */
	private boolean isValidName(String name) {
		if (name == null) {
			return false;
		}
		return true;
	}

	/**
	 * Check whether or not the given description is valid
	 * 
	 * @param description
	 *            | The description to check
	 * @return True if it is valid, false otherwise
	 */
	private boolean isValidDescription(String description) {
		if (description == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Check whether or not the given timestamps are valid
	 * 
	 * @param creationTime
	 *            | The first timestamp
	 * @param dueTime
	 *            | The second timestamp
	 * @return True if they are valid, false otherwise
	 */
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
	 * @param description
	 *            The description of the given Task.
	 * @param estimatedDuration
	 *            The estimated duration of the Task.
	 * @param acceptableDeviation
	 *            The acceptable deviation of the Task.
	 * @param resman
	 *            | The resource manager
	 * @param prerequisiteTasks
	 *            The prerequisites Tasks for this Task.
	 * @param requiredResources
	 *            | The required resources for the Task.
	 * @param alternativeFor
	 *            The Task this new task will replace.
	 * @param taskStatus
	 *            The Status of the Task.
	 * @param startTime
	 *            The start time of the Task.
	 * @param endTime
	 *            The end time of the Task.
	 * @param plannedStartTime
	 *            | The planned start time of the Task
	 * @param plannedDevelopers
	 *            | The assigned developers of the Task
	 * @return True if and only the creation of a Task with a given status was
	 *         successful.
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
			if(plannedStartTime != null) {
				newTask = new Task(description, 
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
				newTask = new Task(description, 
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
			setProjectStatus(new OngoingProject());
			newTask.register(this);
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
	 * @return	True if and only if the Status change was successful.
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
		return state.finish(this, taskList, preTask);
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
	
	/**
	 * Finish a given task on a given timestamp
	 * 
	 * @param t
	 *            | The task to finish
	 * @param endTime
	 *            | The given end time of the task
	 * @return True if the task was finished, false otherwise
	 */
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
	 * @param 	endTime
	 * 			the end time of the given task
	 * @return	True if setting the task to failed was successful,
	 * 			False if it was unsuccessful
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
	 * Start executing a given task on a given timestamp
	 * 
	 * @param task
	 *            | The task to execute
	 * @param startTime
	 *            | The actual begin time of the task
	 * @return True if the task started executing, false otherwise
	 */
	public boolean setTaskExecuting(TaskView task, LocalDateTime startTime){
		if(!isValidTaskView(task)) {
			return false;
		}
		if(startTime.isBefore(creationTime))
			return false;
		return unwrapTaskView(task).execute(startTime);
		
	}
	
	public boolean planTask(TaskView task, LocalDateTime plannedStartTime,
			List<ResourceView> concRes, List<ResourceView> devs) {
		Task t = unwrapTaskView(task);
		if(t == null) {
			return false;
		}
		return t.plan(plannedStartTime, concRes, devs);
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
			LocalTime[] availabilityPeriod = t.getAvailabilityPeriodBoundWorkingTimes();
			if(t.getBeginTime() != null) {
				candidate = TimeSpan.addSpanToLDT(t.getBeginTime(), t.getMaxDelayChain(), availabilityPeriod[0], availabilityPeriod[1]);
			} else { 
				candidate = TimeSpan.addSpanToLDT(currentTime, t.getMaxDelayChain(), availabilityPeriod[0], availabilityPeriod[1]);
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
//	
//	/**
//	 * Returns an amount of possible Task starting times for a given Task.
//	 * 
//	 * @param 	task
//	 * 			The Task to get the starting times from.
//	 * @param 	amount
//	 * 			The amount of possible starting times wanted.
//	 * @return	The possible starting times of the Task
//	 */
//	public List<LocalDateTime> getPossibleTaskStartingTimes(TaskView task, int amount){
//		return unwrapTaskView(task).getPossibleTaskStartingTimes(amount);
//	}

//	/**
//	 * Remove all reservations of a finished or failed task that are still
//	 * scheduled to happen. This method will also free up reserved resources by
//	 * said task if the reservation is still ongoing.
//	 * 
//	 * @param task
//	 *            | The ended task
//	 * @param currentTime
//	 *            | The current time
//	 * @return True if the operation was successful, false otherwise
//	 */
//	public boolean flushFutureReservations(TaskView task, LocalDateTime currentTime) {
//		Task t = unwrapTaskView(task);
//		if(t == null) {
//			return false;
//		}
//		return t.flushFutureReservations(currentTime);
//	}
	
	/**
	 * Reserve a resource for a task from a given start time to a given end time
	 * 
	 * @param resource
	 *            | The resource to reserve
	 * @param task
	 *            | The reserving task
	 * @param startTime
	 *            | The start time of the new reservation
	 * @param endTime
	 *            | The end time of the new reservation
	 * @return True if the resource was reserved by the given task, false
	 *         otherwise
	 */
	public boolean reserve(ResourceView resource, TaskView task,
			LocalDateTime startTime, LocalDateTime endTime) {
		Task t = unwrapTaskView(task);
		if(t == null) {
			return false;
		}
		return t.reserve(resource, startTime, endTime);
	}
	
	/**
	 * Retrieve a list of all tasks that can be updated by a given user
	 * 
	 * @param user
	 *            | The user who wants to update a task
	 * @return an immutable list of tasks that can be updated by a given user
	 */
	public List<TaskView> getUpdatableTasksForUser(ResourceView user){
		Builder<TaskView> list = ImmutableList.builder();
		for (Task task : taskList){
			if (task.hasDeveloper(user) && (task.isExecuting() || task.isAvailable()) ){
				list.add(new TaskView(task));
			}
		}
		return list.build();
	}

	@Override
	public TimeSpan getMaxDelayChain() {
		// A project will add no more delay (a project will finish immediately
		// when its tasks finish)
		return new TimeSpan(0);
	}

	/**
	 * Finds all the task in the current project's tasklist who's reservations contain the given developers or resources.
	 * @param 	task
	 * 			The task that wants to reserve the developers and resources.
	 * @param 	developers
	 * 			The developers that need to be reserved.
	 * @param 	resources
	 * 			The resources that need to be reserved.
	 * @param	plannedStartTime
	 * 			The planned start time of the reservation.
	 * @return	A list of the tasks that conflict with the reservation.
	 */
	public List<TaskView> findConflictingPlannings(
			TaskView task, List<ResourceView> developers,
			List<ResourceView> resources, LocalDateTime plannedStartTime) {
		ArrayList<TaskView> conflictTasks = new ArrayList<TaskView>();
		for (Task t : taskList){
		if(!task.hasAsTask(t)){
			if (t.hasPlanned(developers,resources,plannedStartTime)){
				conflictTasks.add(new TaskView(t));
			}
		}
		}
		return conflictTasks;
	}

}
