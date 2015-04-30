package taskMan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import taskMan.resource.Resource;
import taskMan.resource.ResourceManager;
import taskMan.resource.ResourcePrototype;
import taskMan.resource.user.User;
import taskMan.state.TaskStatus;
import taskMan.state.UnavailableTask;
import taskMan.util.Dependant;
import taskMan.util.TimeSpan;
import taskMan.view.ResourceView;
import taskMan.view.TaskView;

import com.google.common.collect.Lists;

/**
 * The Task object. A task always has a description, a state, a link to the
 * resource manager, an estimated duration, an acceptable deviation to this
 * duration and a map of required resources mapping abstract resource types to
 * their required quantity. A task can replace a failed task. Tasks can also
 * serve as prerequisites to other tasks. Before a task can start, it should be
 * planned first. Information related to the planning (such a time related task
 * details) is kept in a Planning object.
 * 
 * @author Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel, Eli
 *         Vangrieken
 * 
 */
public class Task implements Dependant {

	private final String description;

	private Planning plan;
	
	private final Task alternativeFor;
	private Task replacement;
	private ArrayList<Dependant> dependants;
	private ArrayList<Task> prerequisites;
	
	private TaskStatus state;
	
	private final ResourceManager resMan;
	private Map<ResourcePrototype,Integer> requiredResources;

	/**
	 * Create a new Task.
	 * 
	 * @param taskDescription
	 *            The description of the new Task.
	 * @param estimatedDuration
	 *            The estimated duration of the new Task.
	 * @param acceptableDeviation
	 *            The acceptable deviation of the new Task.
	 * @param resMan
	 *            | The link to the resource manager
	 * @param prerequisiteTasks
	 *            | The tasks this task depends on
	 * @param requiredResources
	 *            | The required resource types for this task and their required
	 *            quantity
	 * @param alternativeFor
	 *            | The task this task will replace
	 * @throws IllegalArgumentException
	 *             if any of the parameters are invalid ( < 0 or null)
	 */
	public Task(String taskDescription, 
			int estimatedDuration,
			int acceptableDeviation, 
			ResourceManager resMan, 
			List<Task> prerequisiteTasks,
			Map<ResourceView, Integer> requiredResources, 
			Task alternativeFor) throws IllegalArgumentException {
		
		if(!isValidDescription(taskDescription)) {
			throw new IllegalArgumentException("Invalid description");
		}
		if(!isValidDuration(estimatedDuration)) {
			throw new IllegalArgumentException("Invalid duration");
		}
		if(!isValidDeviation(acceptableDeviation)) {
			throw new IllegalArgumentException("Invalid deviation");
		}
		if(!isValidAlternative(alternativeFor)) {
			throw new IllegalArgumentException("Invalid replacement");
		}
		if(!isValidPrerequisites(prerequisiteTasks)) {
			throw new IllegalArgumentException("Invalid prerequisites");
		}
		if(prerequisiteTasks.contains(alternativeFor)) {
			throw new IllegalArgumentException("Alt can't be a prerequisite");
		}
		if(!isValidResourceManager(resMan)) {
			throw new IllegalArgumentException("Invalid resource manager");
		}
		this.description = taskDescription;
		this.plan = new Planning(estimatedDuration, acceptableDeviation);
		this.resMan = resMan;
		Map<ResourcePrototype, Integer> reqRes = resMan.isValidRequiredResources(requiredResources);
		if(reqRes == null) {
			throw new IllegalArgumentException("Very bad required resources");
		}
		this.requiredResources = reqRes;
		
		this.state = new UnavailableTask();

		this.dependants = new ArrayList<Dependant>();
		this.prerequisites = new ArrayList<Task>();
		
		this.alternativeFor = alternativeFor;
		if(alternativeFor != null) {
			alternativeFor.replaceWith(this);
		}
		replacement = null;

		for(Task t : prerequisiteTasks) {
			t.register(this);
			this.prerequisites.add(t);
		}
		
		removeAlternativesDependencies();
	}
	
	/**
	 * Create a new Task
	 * 
	 * @param taskDescription
	 *            The description of the new Task.
	 * @param estimatedDuration
	 *            The estimated duration of the new Task.
	 * @param acceptableDeviation
	 *            The acceptable deviation of the new Task.
	 * @param resMan
	 *            | The link to the resource manager
	 * @param prerequisiteTasks
	 *            | The tasks this task depends on
	 * @param requiredResources
	 *            | The required resource types for this task and their required
	 *            quantity
	 * @param alternativeFor
	 *            | The task this task will replace
	 * @param taskStatus
	 *            | The new status of the task or null if the system should
	 *            determine the status (unavailable/available)
	 * @param startTime
	 *            | The actual start time of the task
	 * @param endTime
	 *            | The actual end time of the task
	 * @param plannedStartTime
	 *            | The planned start time
	 * @param plannedDevelopers
	 *            | The assigned developers
	 * @throws IllegalArgumentException
	 *             | When invalid data was supplied
	 */
	public Task(String taskDescription, 
			int estimatedDuration,
			int acceptableDeviation, 
			ResourceManager resMan, 
			List<Task> prerequisiteTasks,
			Map<ResourceView, Integer> requiredResources, 
			Task alternativeFor, 
			String taskStatus,
			LocalDateTime startTime, 
			LocalDateTime endTime,
			LocalDateTime plannedStartTime,
			List<ResourceView> plannedDevelopers) throws IllegalArgumentException {
		
		this(taskDescription, estimatedDuration, acceptableDeviation, resMan,
				prerequisiteTasks, requiredResources, alternativeFor);
		if (taskStatus != null && !isValidTaskStatus(taskStatus)) {
			throw new IllegalArgumentException("Very bad taskStatus");
		}
		if (plannedStartTime == null) {
			throw new IllegalArgumentException("A planned start time is required for this kind of creation");
		}
		plan.setPlannedBeginTime(plannedStartTime);
		if (!plan.setDevelopers(resMan.pickDevs(plannedDevelopers, this,
				startTime, endTime, true))) {
			throw new IllegalArgumentException(
					"Very bad developers, very bad! ## dit is een zéér gaye fout");
		}
		state.makeAvailable(this);
		if (taskStatus != null) {
			state.execute(this, startTime);
			if (taskStatus.equalsIgnoreCase("failed")) {
				if (!state.fail(this, endTime)) {
					throw new IllegalArgumentException("Zéér gaye fout");
				}
			} else if (taskStatus.equalsIgnoreCase("finished")) {
				if (!state.finish(this, endTime)) {
					throw new IllegalArgumentException("Zéér gaye fout");
				}
			}
		}
	}

	/**
	 * Register a new dependant to the current task through the state of the
	 * task
	 * 
	 * @param t
	 *            | The new dependant
	 * @return true if the dependant was added
	 */
	public boolean register(Dependant t) {
		if(!isValidDependant(t)) {
			return false;
		}
		return state.register(this, t);
	}
	
	/**
	 * Add a dependant to the current task
	 * 
	 * @param d
	 *            | The new dependant
	 */
	public void addDependant(Dependant d) {
		dependants.add(d);
	}

	/**
	 * Check whether the dependant is valid
	 * 
	 * @param t
	 *            | The dependant to check
	 * @return True when it is valid, false otherwise
	 */
	private boolean isValidDependant(Dependant t) {
		if(t == this) {
			return false;
		}
		if(t == null) {
			return false;
		}
		return true;
	}

	/**
	 * Notify the dependants of this task that this task has changed. If this
	 * task is an alternative for another task then this will let the
	 * alternative know to do the same
	 * 
	 * @return
	 */
	public boolean notifyDependants() {
		for(Dependant t : dependants) {
			t.updateDependency(this);
		}
		if(alternativeFor != null) {
			alternativeFor.notifyDependants();
		}
		return true;
	}

	@Override
	public boolean updateDependency(Task preTask) {
		int preIndex = prerequisites.indexOf(preTask);

		if(preIndex < 0) {
			return false;
		}

		state.makeAvailable(this);
		
		return true;
	}

	/**
	 * Prevent double storing of dependencies
	 */
	private void removeAlternativesDependencies() {
		if(alternativeFor != null) {
			int depIndex;
			for(Dependant d : alternativeFor.getDependants()) {
				depIndex = dependants.indexOf(d);
				if(depIndex >= 0) {
					dependants.remove(depIndex);
				}
			}
		}
	}

	/**
	 * Checks whether the Task is finished.
	 * 
	 * @return	True if and only if the Task has a finished status.
	 */
	public boolean isFinished() {
		return state.isFinished();
	}
	
	/**
	 * Check if this task has a finished endpoint (this task has finished or if
	 * it has failed, a replacement has finished). This check will happen
	 * recursively over the possible alternatives until a finished alternative
	 * is found (or no finished alternative exists)
	 * 
	 * @return True if the chain of alternatives starting from this task has
	 *         finished
	 */
	public boolean hasFinishedEndpoint() {
		if(isFinished()) {
			return true;
		}
		if(isFailed()) {
			if(replacement != null) {
				return replacement.hasFinishedEndpoint();
			}
		}
		return false;
	}

	/**
	 * Checks whether the the Task has failed.
	 * 
	 * @return	True if and only the Task has failed.
	 */
	public boolean isFailed(){
		return state.isFailed();
	}
	
	/**
	 * Check whether or not this task is eligible to be replaced by another
	 * 
	 * @return True when this task can be replaced by another
	 */
	private boolean canBeReplaced() {
		if(!isFailed()) {
			return false;
		}
		if(getReplacement() != null) {
			return false;
		}
		return true;
	}

	/**
	 * Checks whether the Task is available.
	 * 
	 * @return	True if and only the Task is available.
	 */
	public boolean isAvailable(){
		return state.isAvailable();
	}
	
	/**
	 * Checks whether the task is executing.
	 * @return True if the task is executing, else false
	 */
	public boolean isExecuting(){
		return state.isExecuting();
	}

	/**
	 * Checks whether the Task is unavailable.
	 * 
	 * @return	True if and only the Task is unavailable.
	 */
	public boolean isUnavailable(){
		return state.isUnavailable();
	}

	/**
	 * checks whether the Task has ended.
	 * 
	 * @return	True if and only if the Task has ended.
	 */
	public boolean hasEnded(){
		return (isFinished() || isFailed());
	}

	/**
	 * Returns the time spent on the task. 
	 * If the task is still being worked on, this method will return the 
	 * time in working time elapsed between the start time and current time.
	 * If the task has concluded (hasEnded() = true), this method will return
	 * the time in working time elapsed between the start- and end time of
	 * the task.
	 * 
	 * @param 	currentTime 
	 * 			The current time
	 * @return	time spent on this task in working time
	 * @throws 	IllegalArgumentException 
	 * 			When the start time of the task is after the time given.
	 */
	public TimeSpan getTimeSpent(LocalDateTime currentTime) {
		if(!hasEnded() && !isExecuting()){
			return new TimeSpan(0);
		}
		else if (hasEnded()) {
			int timeSpent = TimeSpan.getDifferenceWorkingMinutes(getBeginTime(), getEndTime(), null, null);
			if(alternativeFor != null) {
				timeSpent += alternativeFor.getTimeSpent(currentTime).getSpanMinutes();
			}
			return new TimeSpan(timeSpent);
		}
		else {
			int currentTimeSpent = TimeSpan.getDifferenceWorkingMinutes(
					getBeginTime(), 
					currentTime, null, null);
			
			if(alternativeFor != null) {
				currentTimeSpent = currentTimeSpent
								 + alternativeFor.getTimeSpent(currentTime).getSpanMinutes();
		}
			return new TimeSpan(currentTimeSpent);
		}

	}

	/**
	 * Returns the planned start time of the Task.
	 * 
	 * @return	The planned start time of the Task.
	 */
	public LocalDateTime getPlannedBeginTime() {
		return plan.getPlannedBeginTime();
	}
	
	/**
	 * Returns the planned end time of the Task.
	 * 
	 * @return	The planned end time of the Task.
	 */
	public LocalDateTime getPlannedEndTime() {
		LocalTime[] availabilityPeriod = getAvailabilityPeriodBoundWorkingTimes();
		return plan.getPlannedEndTime(availabilityPeriod[0], availabilityPeriod[1]);
	}
	
	/**
	 * Find the starting and ending working day timestamps bound by availability
	 * periods of the required resources
	 * 
	 * @return An array with the first element the starting time and the second
	 *         element the end time of the new work day hours. They are null if
	 *         no resource has an availability period.
	 */
	public LocalTime[] getAvailabilityPeriodBoundWorkingTimes() {
		LocalTime availabilityStart = null, availabilityEnd = null;
		for(ResourcePrototype resource : requiredResources.keySet()) {
			if(resource.isDailyAvailable()) {
				if(availabilityStart != null) {
					if(resource.getDailyAvailabilityStartTime().isAfter(availabilityStart)) {
						availabilityStart = resource.getDailyAvailabilityStartTime();
					}
					if(resource.getDailyAvailabilityEndTime().isBefore(availabilityEnd)) {
						availabilityEnd = resource.getDailyAvailabilityEndTime();
					}
				} else {
					availabilityStart = resource.getDailyAvailabilityStartTime();
					availabilityEnd = resource.getDailyAvailabilityEndTime();
				}
			}
		}
		return new LocalTime[]{availabilityStart, availabilityEnd};
	}

	/**
	 * Returns the start time of the Task.
	 * 
	 * @return	The start time of the Task.
	 */
	public LocalDateTime getBeginTime() {
		return plan.getBeginTime();
	}

	/**
	 * Returns the end time of the Task.
	 * 
	 * @return	The end time of the Task.
	 */
	public LocalDateTime getEndTime() {
		return plan.getEndTime();
	}

	/**
	 * Sets the begin time of Task.
	 * 
	 * @param 	beginTime
	 * 			The new begin time of the Task. 
	 */
	public boolean setBeginTime(LocalDateTime beginTime) {
		return plan.setBeginTime(beginTime);
	}

	/**
	 * Sets the end time of Task.
	 * 
	 * @param 	endTime
	 * 			The new end time of the Task.
	 * @throws	IllegalArgumentException
	 * 			If the new end time is null or the old end time is already set. 
	 */
	public boolean setEndTime(LocalDateTime endTime) {
		return plan.setEndTime(endTime);
	}

	/**
	 * Returns the description of the Task.
	 * 
	 * @return	The description of the task.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the estimated duration of the Task.
	 * 
	 * @return	The estimated duration of the Task.
	 */
	public TimeSpan getEstimatedDuration() {
		return plan.getEstimatedDuration();
	}

	/**
	 * Returns the acceptable deviation of the Task.
	 * 
	 * @return	The acceptable deviation of the Task.
	 */
	public int getAcceptableDeviation() {
		return plan.getAcceptableDeviation();
	}
	
	/**
	 * Return the alternative this task replaces
	 * @return the task this task is an alternative for
	 */
	public Task getAlternativeFor() {
		return alternativeFor;
	}
	
	/**
	 * Return the task which replaces this one
	 * 
	 * @return the alternative for this task
	 */
	public Task getReplacement() {
		return replacement;
	}

	/**
	 * Get the longest possible duration that a series of tasks (this one and
	 * its dependants) could require to finish (estimated)
	 * 
	 * @return the longest possible duration as a TimeSpan for this task and its
	 *         dependants to finish (estimated)
	 */
	public TimeSpan getMaxDelayChain() {
		TimeSpan longest = getEstimatedDuration();
		TimeSpan candidate;
		for(Dependant d : dependants) {
			candidate = getEstimatedDuration().add(d.getMaxDelayChain());
			if(candidate.isLonger(longest)) {
				longest = candidate;
			}
		}
		if(alternativeFor != null) {
			candidate = alternativeFor.getMaxDelayChain().minus(alternativeFor.getEstimatedDuration());
			if(candidate.isLonger(longest)) {
				longest = candidate;
			}
		}
		return longest;
	}
	
	public List<Task> getPrerequisites() {
		return prerequisites;
	}
	
	/**
	 * Return a list of all the dependants of this task
	 * 
	 * @return a list of all the dependants of this task
	 */
	public List<Dependant> getDependants() {
		return dependants;
	}

	/**
	 * Returns the status of the Task as a String.
	 * 
	 * @return	The status of the Task as a String.
	 */
	public String getStatus(){
		return state.toString();

	}

	/**
	 * End the task in a Finished state
	 * 
	 * @param 	endTime
	 * 			The new end time of the Task.
	 * @return	True if and only if the updates succeeds.
	 */
	public boolean finish(LocalDateTime endTime) {
		if(!state.finish(this, endTime)) {
			return false;
		}
		if(!resMan.releaseResources(this)) {
			return false;
		}
		return true;
	}

	/**
	 * End the task in a Failed state
	 * 
	 * @param 	endTime
	 * 			The new end time of the Task.
	 * @return	True if and only if the updates succeeds.
	 */
	public boolean fail(LocalDateTime endTime) {
		if(!state.fail(this, endTime)) {
			return false;
		}
		if(!resMan.releaseResources(this)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Ask the state to put this task in an executing state
	 * 
	 * @param startTime
	 *            | The actual start time of the task
	 * @return True if this task is now executing
	 */
	public boolean execute(LocalDateTime startTime) {
		return state.execute(this, startTime);
	}
	
	/**
	 * Tries to renew all reservations made for this task. Fails if the new
	 * reservation date doesn't fall before the planned start time or after
	 * the planned end time.
	 * 
	 * @param newReservationDate
	 * @return
	 */
	public boolean refreshReservations(LocalDateTime newReservationDate) {
		if(getPlannedBeginTime() == null) {
			return false;
		}
		if(    !newReservationDate.isBefore(getPlannedBeginTime()) 
			&& !newReservationDate.isAfter(getPlannedEndTime())) {
			return true;
		}
		return resMan.refreshReservations(this, newReservationDate, getPlannedEndTime());
	}
	
	/**
	 * Replace this task with another one
	 * 
	 * @param t
	 *            | The alternative for this task
	 * @return True if the replacement is set
	 */
	public boolean replaceWith(Task t) {
		if(!canBeReplaced()) {
			return false;
		}
		this.replacement = t;
		return true;
	}

	/**
	 * Set the state of this task
	 * 
	 * @param newStatus
	 *            | the new state
	 */
	public void setTaskStatus(TaskStatus newStatus) {
		this.state = newStatus;
	}
	
	/**
	 * Checks whether the deviation is a valid one.
	 * 
	 * @param 	deviation
	 * 			The deviation to check.
	 * @return	True if deviation >= 0
	 */
	private boolean isValidDeviation(int deviation) {
		return deviation >= 0;
	}
	
	/**
	 * Checks whether the description is a valid one.
	 * 
	 * @param 	description
	 * 			The description to check.
	 * @return	True if description != null
	 */
	private boolean isValidDescription(String description){
		return description != null;
	}
	
	/**
	 * Checks whether the duration is a valid one.
	 * 
	 * @param 	duration
	 * 			The duration to check.
	 * @return	True if duration > 0
	 */
	private boolean isValidDuration(int duration){
		return duration > 0;
	}

	/**
	 * Check whether the status is a valid one
	 * 
	 * @param status
	 *            | The status to check
	 * @return True when the status is recognized
	 */
	private boolean isValidTaskStatus(String status) {
		if(    !status.equalsIgnoreCase("finished")
			&& !status.equalsIgnoreCase("failed")
			&& !status.equalsIgnoreCase("executing")) {
			return false;
		}
		return true;
	}

	/**
	 * Checks whether the given prerequisites are valid for the given Task.
	 * 
	 * @param 	prerequisites
	 * 			The prerequisites to check.
	 * @return	True if and only the prerequisites are a valid.
	 * 			True if the prerequisites are empty
	 * 			False if the prerequisites are null
	 * 			False if the task ID isn't a valid one
	 */
	private boolean isValidPrerequisites(List<Task> prerequisites){
		if (prerequisites == null) {
			return false;
		}
		if(prerequisites.contains(null)) {
			return false;
		}
		if(prerequisites.contains(this)) {
			return false;
		}
		return true;
	}

	/**
	 * Checks whether the given alternative is valid for the given Task.
	 * 
	 * @param altTask
	 *            The alternative to check.
	 * @return True if and only the alternative are a valid.
	 */
	private boolean isValidAlternative(Task altTask) {
		if(altTask == null) {
			return true;
		}
		if(altTask.equals(this)) {
			return false;
		}
		if(!altTask.isFailed()) {
			return false;
		}
		if(!altTask.canBeReplaced()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks whether the given resource manager is valid for the given Task.
	 * 
	 * @param resMan
	 *            | The resource manager to check
	 * @return True when the resource manager is valid
	 */
	private boolean isValidResourceManager(ResourceManager resMan) {
		if(resMan == null) {
			return false;
		}
		return true;
	}
	
	private boolean isValidPlannedStartTime(LocalDateTime start) {
		if(prerequisites.isEmpty()) {
			return true;
		}
		for(Task t : prerequisites) {
			if(!checkTask(t,start)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Check if a prerequisite task doesn't interfere with a given planned start
	 * time
	 * 
	 * @param t
	 *            | The prerequisite task
	 * @param start
	 *            | The planned begin time
	 * @return True if the task doesn't interfere
	 */
	private boolean checkTask(Task t, LocalDateTime start) {
		if(t.isUnavailable() || (t.isFailed() && (t.getReplacement() == null))) {
			return false; // Heeft een prereq die nog niet KAN worden afgewerkt
		}
		if((t.isAvailable() || t.isExecuting()) && t.getPlannedEndTime().isAfter(start)) {
			return false; // Heeft een prereq die eindigt NA de gekozen planned start time
		}
		if(t.isFailed() && (t.getReplacement() != null)) {
			return checkTask(t.getReplacement(), start);
		}
		return true;
	}
	
	/**
	 * Plan a task in the system from a given start time. Reservations will be
	 * made for all the required resources and the developers will be assigned.
	 * 
	 * @param startTime
	 *            | The planned begin time
	 * @param concRes
	 *            | The resources to plan
	 * @param devs
	 *            | The developers to assign
	 * @return True if the task was planned and all reservations made
	 */
	public boolean plan(LocalDateTime startTime, List<ResourceView> concRes, List<ResourceView> devs) {
		for(Task t : prerequisites) {
			if(t.isFailed() && (t.getReplacement() == null)) {
				return false;
			}
		}
		if(!isValidPlannedStartTime(startTime)) {
			return false;
		}
		if(!plan.setPlannedBeginTime(startTime)) {
			return false;
		}
		if(resMan.hasActiveReservations(this)) {
			if(!refreshReservations(startTime)) {
				return false;
			}
		} else {
			if(!resMan.reserve(concRes, this, startTime, getPlannedEndTime(), true)) {
				return false;
			}
		}
		if(!resMan.hasActiveReservations(this)) {
			resMan.releaseResources(this);
			return false; // verkeerde hoeveelheid reservaties enzo
		}
		List<User> developers = resMan.pickDevs(devs, this, startTime, getPlannedEndTime(), true);
		if(developers == null) {
			resMan.releaseResources(this);
			return false;
		}
		if(!plan.setDevelopers(developers)) {
			resMan.releaseResources(this);
			return false;
		}
		state.makeAvailable(this);
		return true;
		
	}

	/**
	 * Plan a task in the system from a given start time. Reservations will be
	 * made for all the required resources and the developers will be assigned.
	 * It is however possible that this planning will conflict with other
	 * plannings in the system. It is the responsibility of the user when
	 * applying this method to check for conflicts using the
	 * findConflictingPlannings and handle these conflicts appropriately. If you
	 * want to be sure to not get into an inconsistent state, use planTask
	 * instead.
	 * 
	 * @param startTime
	 *            | The planned begin time
	 * @param concRes
	 *            | The resources to plan
	 * @param devs
	 *            | The developers to assign
	 * @return True if the task was planned and all reservations made
	 */
	public boolean rawPlan(LocalDateTime startTime, List<ResourceView> concRes, List<ResourceView> devs) {
		for(Task t : prerequisites) {
			if(t.isFailed() && (t.getReplacement() == null)) {
				return false;
			}
		}
		if(!isValidPlannedStartTime(startTime)) {
			return false;
		}
		if(!plan.setPlannedBeginTime(startTime)) {
			return false;
		}
		if(resMan.hasActiveReservations(this)) {
			if(!refreshReservations(startTime)) {
				return false;
			}
		} else {
			if(!resMan.reserve(concRes, this, startTime, getPlannedEndTime(), false)) {
				return false;
			}
		}
		if(!resMan.hasActiveReservations(this)) {
			resMan.releaseResources(this);
			return false; // verkeerde hoeveelheid reservaties enzo
		}
		List<User> developers = resMan.pickDevs(devs, this, startTime, getPlannedEndTime(), false);
		if(developers == null) {
			resMan.releaseResources(this);
			return false;
		}
		if(!plan.setDevelopers(developers)) {
			resMan.releaseResources(this);
			return false;
		}
		state.makeAvailable(this);
		return true;
		
	}
	
	/**
	 * Returns an amount of possible Task starting times.
	 * 
	 * @param 	amount
	 * 			The amount of possible starting times wanted.
	 * @return	The possible starting times of the Task
	 */
	public List<LocalDateTime> getPossibleTaskStartingTimes(List<ResourceView> concRes, LocalDateTime currentTime, int amount) {
		return resMan.getPossibleStartingTimes(this,concRes,currentTime,amount);
	}
	
	/**
	 * Retrieve a map of the task's required resources. This map will map an
	 * abstract resource type on the quantity required of that resource.
	 * 
	 * @return a map containing the required resources and their respective
	 *         required quantities
	 */
	public Map<ResourcePrototype,Integer> getRequiredResources(){
		Map<ResourcePrototype,Integer> reqRes = new HashMap<ResourcePrototype,Integer>();
		reqRes.putAll(requiredResources);
		return reqRes;
	}
	
	/**
	 * Reserve a resource at init
	 * 
	 * @param resource
	 *            | Resource to reserve
	 * @param startTime
	 *            | The start time of the reservation
	 * @param endTime
	 *            | The end time of the reservation
	 * @return True if the reservation was successful, false otherwise
	 */
	public boolean reserve(ResourceView resource, LocalDateTime startTime,
			LocalDateTime endTime) {
		return resMan.reserve(Lists.newArrayList(resource), this, startTime, endTime, true);
	}
	
	/**
	 * Check whether or not this task has a specific developer assigned to this
	 * task
	 * 
	 * @param user
	 *            | The developer to check
	 * @return True if the given developer is assigned to this task
	 */
	public boolean hasDeveloper(ResourceView user){
		for(User dev : plan.getPlannedDevelopers()){
			if (user.hasAsResource(dev)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return a list of the developers planned to work on this task
	 * 
	 * @return an immutable list of developers (wrapped in views) planned to
	 *         work on this task
	 */
	public List<User> getPlannedDevelopers(){
		return plan.getPlannedDevelopers();
	}
	
	/**
	 * Gets the reserved resources for a given Task
	 * @return the reserved resources of the Task
	 */
	public List<Resource> getReservedResources(){
		return resMan.getReservedResourcesForTask(this);
	}

	/**
	 * Checks whether there exists a planning conflict with the given task
	 * @param 	task
	 * 			The task to check for a conflict.
	 * @return	True if there exists a planning conflict, else false
	 */
	public boolean hasPlanningConflict(TaskView task) {
		boolean conflictFound = false;
		if ( plan.getPlannedBeginTime() == null){
			return false;
		}
		if (!task.getPlannedEndTime().isAfter(this.getPlannedBeginTime()) || !task.getPlannedBeginTime().isBefore(this.getPlannedEndTime())){
			return false;
		}
		else {
			for(Resource res : task.getReservedResources()){
				if (this.getReservedResources().contains(res)){
					conflictFound = true;
					break;
				}
			}
		}
		return conflictFound;
	}
}
