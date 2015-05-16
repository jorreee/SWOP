package company.taskMan.project;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import company.BranchView;
import company.taskMan.Delegator;
import company.taskMan.resource.ResourceManager;
import company.taskMan.resource.ResourceView;
import company.taskMan.task.Dependant;
import company.taskMan.task.Task;
import company.taskMan.util.TimeSpan;

import exceptions.ResourceUnavailableException;
import exceptions.UnexpectedViewContentException;

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
		
		this.state = new OngoingState();

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

	/**
	 * Creates a new Task with a status of failed or finished.
	 * 
	 * @param description
	 *            The description of the given Task.
	 * @param estimatedDuration
	 *            The estimated duration of the Task.
	 * @param acceptableDeviation
	 *            The acceptable deviation of the Task.
	 * @param resMan
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
	 * @throws IllegalArgumentException, IllegalStateException, ResourceUnavailableException 
	 */
	public void createTask(String description, 
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
						List<ResourceView> plannedDevelopers) 
				throws IllegalArgumentException, IllegalStateException, ResourceUnavailableException {

		if(!isValidAlternative(alternativeFor)) {
			throw new IllegalArgumentException("Invalid alternative");
		}
		Task altFor;
		try {
			altFor = unwrapTaskView(alternativeFor);
		} catch(IllegalArgumentException e) {
			altFor = null;
		}
		
		ArrayList<Task> prereqTasks = new ArrayList<Task>();
		for(TaskView t : prerequisiteTasks) {
//			if(!isValidTaskView(t)) {
//				return false;
//			}
			prereqTasks.add(unwrapTaskView(t));
		}
		
		Task newTask;

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

		taskList.add(newTask);
		setProjectStatus(new OngoingState());
		newTask.register(this);
	}
//	
//	/**
//	 * Checks whether the given TaskView is a valid TaskView representing a valid Task.
//	 * 
//	 * @param 	view
//	 * 			The TaskView to check.
//	 * @return	True if and only if the TaskView is a valid TaskView.
//	 */
//	private boolean isValidTaskView(TaskView view) {
//		if(view == null) {
//			return false;
//		}
//		if(view.unwrap() == null) {
//			return false;
//		}
//		return taskList.contains(unwrapTaskView(view));
//	}
	
	/**
	 * Checks whether the given TaskView represents a valid Alternative for a certain Task in this Project.
	 * 
	 * @param 	view
	 * 			The TaskView to check.
	 * @return	True if and only if the TaskView represents a valid Alternative.
	 */
	private boolean isValidAlternative(TaskView view) {
		if(view == null) {
			return true;
		}
//		if(isValidTaskView(view)) {
		try {
			Task task = unwrapTaskView(view);
			for(Task t : taskList) {
				if(t.getAlternativeFor() == task) {
					return false;
				}
			}
			return true;
		} catch(Exception e) {
			return false;
		}
//		}
	}
	
	/**
	 * Unwraps the TaskView object and returns the Task that it contained
	 * 		IF the unwrapped task belongs to this project:
	 * 				(getTaskList().contains(task)
	 * 
	 * @param 	view
	 * 			the TaskView to unwrap
	 * @return 	the unwrapped Task if it belonged to this project
	 * @throws IllegalArgumentException, UnexpectedViewContentException 
	 */
	private Task unwrapTaskView(TaskView view) 
			throws IllegalArgumentException, UnexpectedViewContentException {
		if(view == null) {
			throw new IllegalArgumentException("There was no task to unwrap!");
		}
		Task task = view.unwrap();
		if(!taskList.contains(task)) {
			throw new UnexpectedViewContentException("The task does not belong to this project!");
		}
		return task;
	}
	
	/**
	 * Sets the Project Status to the given Status.
	 * 
	 * @param 	newStatus
	 * 			The Status to change to.
	 * @return	True if and only if the Status change was successful.
	 */
	protected void setProjectStatus(ProjectStatus newStatus) {
		this.state = newStatus;
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
	public void updateDependency(Task preTask) throws IllegalStateException {
		if (!taskList.contains(preTask)){
			throw new IllegalStateException("The preTask didn't belong to this project");
		}
		state.finish(this, preTask);
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
//		if(getEndTime()!=null) {
//			throw new IllegalArgumentException("The endtime is already set");
//		}
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
	public List<Task> getTasks(){
//		ArrayList<TaskView> tasks = new ArrayList<TaskView>();
//		for(Task t : taskList) {
//			tasks.add(new TaskView(t));
//		}
//		return tasks;
		ImmutableList.Builder<Task> tasks = ImmutableList.builder();
		tasks.addAll(taskList);
		return tasks.build();
	}
	
	/**
	 * Returns a list of the available tasks of the project
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
	
	/**
	 * Finish a given task on a given timestamp
	 * 
	 * @param t
	 *            | The task to finish
	 * @param endTime
	 *            | The given end time of the task
	 * @throws IllegalArgumentException, IllegalStateException 
	 */
	public void setTaskFinished(TaskView t, LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException {
		unwrapTaskView(t).finish(endTime);
	}

	/**
	 * Sets the task with the given task id to failed
	 * 
	 * @param 	t
	 * 			the id of the given task
	 * @param 	endTime
	 * 			the end time of the given task
	 * @throws  IllegalArgumentException, IllegalStateException 
	 */
	public void setTaskFailed(TaskView t, LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException {
		unwrapTaskView(t).fail(endTime);
	}
	
	/**
	 * Start executing a given task on a given timestamp
	 * 
	 * @param task
	 *            | The task to execute
	 * @param startTime
	 *            | The actual begin time of the task
	 * @throws IllegalArgumentException, IllegalStateException 
	 */
	public void setTaskExecuting(TaskView task, LocalDateTime startTime) 
			throws IllegalArgumentException, IllegalStateException {
//		if(!isValidTaskView(task)) {
//			return false;
//		}
		if(startTime.isBefore(creationTime)) {
			throw new IllegalArgumentException("The task has to be executed after its creation time");
		}
		unwrapTaskView(task).execute(startTime);
		
	}
	
	/**
	 * Passes its arguments to the provided task (if it exists) and tries to
	 * plan it by invoking the plan(...) method on it.
	 * 
	 * @param task
	 * 			| the task to plan
	 * @param plannedStartTime
	 * 			| the planned start time for the task
	 * @param concRes
	 * 			| the concrete resources to plan for the task
	 * @param devs
	 * 			| the developers to plan for the task
	 * @throws IllegalArgumentException, IllegalStateException, ResourceUnavailableException 
	 */
	public void planTask(TaskView task, LocalDateTime plannedStartTime,
			List<ResourceView> concRes, List<ResourceView> devs) 
					throws IllegalArgumentException, 
					IllegalStateException, 
					ResourceUnavailableException {
		unwrapTaskView(task).plan(plannedStartTime, concRes, devs);
	}
	
	/**
	 * Passes its arguments to the provided task (if it exists) and tries to
	 * plan it by invoking the rawPlan(...) method on it. It is however possible
	 * that this planning will conflict with other plannings in the system. It
	 * is the responsibility of the user when applying this method to check for
	 * conflicts using the findConflictingPlannings and handle these conflicts
	 * appropriately. If you want to be sure to not get into an inconsistent
	 * state, use planTask instead.
	 * 
	 * 
	 * @param task
	 *            | the task to plan
	 * @param plannedStartTime
	 *            | the planned start time for the task
	 * @param concRes
	 *            | the concrete resources to plan for the task
	 * @param devs
	 *            | the developers to plan for the task
	 * @throws IllegalArgumentException, IllegalStateException, ResourceUnavailableException 
	 */
	public boolean planRawTask(TaskView task, LocalDateTime plannedStartTime,
			List<ResourceView> concRes, List<ResourceView> devs) 
					throws IllegalArgumentException, 
					IllegalStateException, 
					ResourceUnavailableException {
		return unwrapTaskView(task).rawPlan(plannedStartTime, concRes, devs);
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
	 * Calculate the estimated end time of the project, depending on the task
	 * that has the longest delay chain. If no tasks are currently available it
	 * will return the current Time.
	 * 
	 * @param currentTime
	 *            | The current time to use as an offset
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
	 * @throws IllegalArgumentException, ResourceUnavailableException 
	 */
	public boolean reserve(ResourceView resource, TaskView task,
			LocalDateTime startTime, LocalDateTime endTime) 
				throws IllegalArgumentException, ResourceUnavailableException, IllegalStateException {
		Task t = unwrapTaskView(task);
		if(t == null) {
			return false;
		}
		t.reserve(resource, startTime, endTime);
		return true;
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

	/**
	 * Answers the question from tasks of how much delay this Dependant adds
	 * to the total time chain it is investigating. Always returns a TimeSpan
	 * object with length 0.
	 * 
	 * @return
	 * 			| a TimeSpam with length 0
	 */
	@Override
	public TimeSpan getMaxDelayChain() {
		// A project will add no more delay (a project will finish immediately
		// when its tasks finish)
		return new TimeSpan(0);
	}

	/**
	 * Finds all the task in the current project's tasklist who's reservations conflict with the given task.
	 * @param 	task
	 * 			The task that wants to reserve
	 * @return	A list of the tasks that conflict with the reservation.
	 */
	public List<TaskView> findConflictingPlannings(TaskView task) {
		ArrayList<TaskView> conflictTasks = new ArrayList<TaskView>();
		for (Task t : taskList) {
			if(!task.hasAsTask(t)) {
				if (t.hasPlanningConflict(task.getPlannedBeginTime(),
						task.getPlannedEndTime(),
						task.getReservedResources())) {
					conflictTasks.add(new TaskView(t));
				}
			}
		}
		return conflictTasks;
	}

	public Optional<BranchView> getResponsibleBranch(Delegator delegator, TaskView task) {
		return delegator.getResponsibleBranch(task.unwrap());
	}

}
