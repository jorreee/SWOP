package company.taskMan.project;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import company.taskMan.resource.Resource;
import company.taskMan.resource.ResourcePrototype;
import company.taskMan.resource.ResourceView;
import company.taskMan.resource.user.User;
import company.taskMan.task.Task;
import company.taskMan.util.TimeSpan;

/**
 * A taskView is a wrapper for tasks. The taskView only has limited access to
 * the task and is thus safe to send out to the UI. The UI will hence also only
 * have limited access to the underlying tasks and cannot do anything
 * unauthorized.
 * 
 * @author Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class TaskView {

	private final Task task;

	/**
	 * Wrap a task in a new view
	 * 
	 * @param t
	 *            | The task to wrap
	 */
	public TaskView(Task t) {
		this.task = t;
	}
	
	protected Task unwrap() {
		return task;
	}

	/**
	 * Retrieve the description of the task
	 * 
	 * @return the task description
	 */
	public String getDescription() {
		return task.getDescription();
	}

	/**
	 * Retrieve the start time of the task
	 * 
	 * @return the task start time
	 */
	public LocalDateTime getStartTime() {
		return task.getBeginTime();
	}

	/**
	 * Retrieve the estimated duration of the task
	 * 
	 * @return the task estimated duration
	 */
	public int getEstimatedDuration() {
		return task.getEstimatedDuration().getSpanMinutes();
	}

	/**
	 * Retrieve the acceptable deviation of the task
	 * 
	 * @return the task acceptable deviation
	 */
	public int getAcceptableDeviation() {
		return task.getAcceptableDeviation();
	}

	/**
	 * Retrieve the end time of the task
	 * 
	 * @return the task end time
	 */
	public LocalDateTime getEndTime() {
		return task.getEndTime();
	}

	/**
	 * Retrieve the status of the task as a string
	 * 
	 * @return the task status as a string
	 */
	public String getStatusAsString() {
		return task.getStatus();
	}

	/**
	 * Check whether the task has any prerequisites
	 * 
	 * @return True if the task has any prerequisites, false otherwise
	 */
	public boolean hasPrerequisites() {
		return !getPrerequisites().isEmpty();
	}

	/**
	 * A method to retrieve all prerequisite tasks (wrapped in views) for this
	 * task
	 * 
	 * @return an immutable list of the task prerequisites of this task
	 */
	public List<TaskView> getPrerequisites() {
		ImmutableList.Builder<TaskView> taskPrereqs = ImmutableList.builder();
		for (Task t : task.getPrerequisites()) {
			taskPrereqs.add(new TaskView(t));
		}
		return taskPrereqs.build();
	}
	
	/**
	 * Check whether or not this (failed) task is replaced by another
	 * 
	 * @return True when this task has a replacement, false when it has not
	 */
	public boolean hasReplacement() {
		return getReplacement() != null;
	}

	/**
	 * A method to retrieve the replacement of this (failed) task
	 * 
	 * @return the replacement of this task
	 */
	public TaskView getReplacement() {
		Task rep = task.getReplacement();
		if (rep == null) {
			return null;
		}
		return new TaskView(rep);
	}

	/**
	 * Check whether or not this task replaces another (failed) task
	 * 
	 * @return True when this task has an alternative, false when it hasn't
	 */
	public boolean isAlternative() {
		return getAlternativeTo() != null;
	}

	/**
	 * A method to get the (failed) task this task replaces
	 * 
	 * @return the task this task is an alternative to
	 */
	public TaskView getAlternativeTo() {
		Task alt = task.getAlternativeFor();
		if (alt == null) {
			return null;
		}
		return new TaskView(alt);
	}

	/**
	 * A method to check whether or not this task is in an available status
	 * 
	 * @return True if this task is available
	 */
	public boolean isAvailable() {
		return task.isAvailable();
	}

	/**
	 * A method to check whether or not this task is in an unavailable status
	 * 
	 * @return True if this task is unavailable
	 */
	public boolean isUnavailable() {
		return task.isUnavailable();
	}
	
	/**
	 * A method to check whether or not this task has ended (finished or failed)
	 * 
	 * @return True if this task has ended
	 */
	public boolean hasEnded() {
		return task.hasEnded();
	}

	/**
	 * Returns whether the current Task is on time, depending on the estimated
	 * duration
	 * 
	 * @param currentTime
	 *            | The current Time to compare to
	 * @return True if the Task is on time. False if the current date false
	 *         after beginTime + EstimatedDur (in working minutes)
	 */
	public boolean isOnTime(LocalDateTime currentTime) {
		if (getStartTime() == null && getPlannedBeginTime() == null) {
			return true;
		}
		TimeSpan acceptableSpan = task.getEstimatedDuration();
		LocalDateTime acceptableEndDate = getAcceptableEndDate(acceptableSpan);

		if (hasEnded()) {
			return !task.getEndTime().isAfter(acceptableEndDate);
		}

		return !currentTime.isAfter(acceptableEndDate);
	}
	
	/**
	 * Find the acceptable end date of this task
	 * 
	 * @param acceptableSpan
	 *            | The acceptable span for the task to last
	 * @return The timestamp when the task is still acceptable to end
	 */
	private LocalDateTime getAcceptableEndDate(TimeSpan acceptableSpan) {

		LocalDateTime startTime = getStartTime();
		if(startTime == null) {
			startTime = getPlannedBeginTime();
		}
		
		LocalTime[] availabilityPeriod = task.getAvailabilityPeriodBoundWorkingTimes();
		return TimeSpan.addSpanToLDT(
				startTime, acceptableSpan, availabilityPeriod[0], availabilityPeriod[1]);
	}

	/**
	 * Returns whether the current is unacceptably overdue, depending on the
	 * estimated duration and acceptable deviation of the task.
	 * 
	 * @param currentTime
	 *            | The time to compare to
	 * @return True if the project is overtime beyond the deviation. False
	 *         otherwise.
	 */
	public boolean isUnacceptableOverdue(LocalDateTime currentTime) {
		if (getStartTime() == null && getPlannedBeginTime() == null) {
			return false;
		}
		TimeSpan acceptableSpan = task.getEstimatedDuration()
				.getAcceptableSpan(getAcceptableDeviation());
		LocalDateTime acceptableEndDate = getAcceptableEndDate(acceptableSpan);

		if (hasEnded()) {
			return getEndTime().isAfter(acceptableEndDate);
		}

		return currentTime.isAfter(acceptableEndDate);
	}

	/**
	 * Returns the percentage of overdueness of the task, depending on the
	 * estimated duration of the task. Returns 0 if the task is well on time.
	 * 
	 * @param currentTime
	 *            | The time to compare to
	 * @return The percentage of overdue.
	 */
	public int getOverTimePercentage(LocalDateTime currentTime) {
		if (isOnTime(currentTime)) {
			return 0;
		}
		
		TimeSpan acceptableSpan = task.getEstimatedDuration();
		LocalDateTime acceptableEndDate = getAcceptableEndDate(acceptableSpan);
		if(hasEnded()) {
			currentTime = getEndTime();
		}
		
		LocalTime[] availabilityPeriod = task.getAvailabilityPeriodBoundWorkingTimes();

		int overdue = TimeSpan.getDifferenceWorkingMinutes(acceptableEndDate, currentTime, availabilityPeriod[0], availabilityPeriod[1]);

		return (overdue* 100) / task.getEstimatedDuration().getSpanMinutes();
	}

	/**
	 * Check whether or not this view contains a specific task
	 * 
	 * @param t
	 *            | The task to check with
	 * @return True if this view contains the given task, false otherwise
	 */
	public boolean hasAsTask(Task t) {
		if (t == null || task == null) {
			return false;
		}
		return task.equals(t);
	}

	/**
	 * Retrieve a map of the task's required resources. This map will map an
	 * abstract resource type on the quantity required of that resource.
	 * 
	 * @return a map containing the required resources and their respective
	 *         required quantities
	 */
	public Map<ResourceView, Integer> getRequiredResources() {
		ImmutableMap.Builder<ResourceView,Integer> reqRes = ImmutableMap.builder();
		for(ResourcePrototype p : task.getRequiredResources().keySet()) {
			reqRes.put(new ResourceView(p), task.getRequiredResources().get(p));
		}
		return reqRes.build();
	}

	/**
	 * A method to retrieve a given amount of possible starting times. A task is
	 * possible to start when enough resources are available for the estimated
	 * duration of the task (taking into account developer pauses and
	 * availability periods of resources). These suggested starting times can be
	 * used by a project manager to plan the task in a specific slot.
	 * 
	 * @param concRes
	 *            | A list of all the resources (possible abstract resource
	 *            types or concrete resources) that all should be available for
	 *            the estimated duration of the task
	 * @param currentTime
	 *            | The time to start counting from
	 * @param amount
	 *            | The amount of suggestions that should be returned
	 * @return a given amount of suggested starting times for the task to be
	 *         planned at
	 */
	public List<LocalDateTime> getPossibleStartingTimes(
			List<ResourceView> concRes, LocalDateTime currentTime, int amount) {
		ImmutableList.Builder<LocalDateTime> times = ImmutableList.builder();
		times.addAll(task.getPossibleTaskStartingTimes(concRes, currentTime, amount));
		return times.build();
	}

	/**
	 * Check whether two views are equal to each other
	 * 
	 * @param otherView
	 *            | The other view to compare with
	 * @return True if the views are identical or share the same task
	 */
	public boolean equals(Object otherView) {
		if (this == otherView)
			return true;
		if (otherView == null)
			return false;
		try {
			return ((TaskView) otherView).hasAsTask(task);	
		} catch(ClassCastException e) {
			return false;
		}	
	}

	/**
	 * Return a list of the developers planned to work on this task
	 * 
	 * @return an immutable list of developers (wrapped in views) planned to
	 *         work on this task
	 */
	public List<ResourceView> getPlannedDevelopers() {
		ImmutableList.Builder<ResourceView> plannedDevs = ImmutableList.builder();
		for(User u : task.getPlannedDevelopers()) {
			plannedDevs.add(new ResourceView(u));
		}
		return plannedDevs.build();
	}

	/**
	 * A method to retrieve the planned starting time of this task
	 * 
	 * @return the planned starting time of this task
	 */
	public LocalDateTime getPlannedBeginTime() {
		return task.getPlannedBeginTime();
	}

	/**
	 * A method to check whether or not this task has already been planned by
	 * the project manager
	 * 
	 * @return True if the task has been planned in the system, false if it
	 *         hasn't
	 */
	public boolean isPlanned() {
		return (task.getPlannedBeginTime() != null);
	}
	
	/**
	 * Checks whether the task is executing.
	 * @return True if the task is executing, else false
	 */
	public boolean isExecuting(){
		return (task.isExecuting());
	}
	
	/**
	 * Returns the planned end time of the Task.
	 * 
	 * @return	The planned end time of the Task.
	 */
	public LocalDateTime getPlannedEndTime() {
		return task.getPlannedEndTime();
	}
	
	/**
	 * Gets the reserved resources for a given Task
	 * @return the reserved resources of the Task
	 */
	public List<ResourceView> getReservedResources(){
		ImmutableList.Builder<ResourceView> resRes = ImmutableList.builder();
		for(Resource r : task.getReservedResources()) {
			resRes.add(new ResourceView(r));
		}
		return resRes.build();
	}

	public boolean isDelegated() {
		return task.isDelegated();
	}
}
