package taskMan.view;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import taskMan.Task;
import taskMan.resource.ResourcePrototype;
import taskMan.resource.user.User;
import taskMan.util.TimeSpan;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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

	//
	// public boolean isUnacceptableOverdue(LocalDateTime currentTime) {
	// return task.isUnacceptableOverdue(currentTime);
	// }
	//
	// public boolean isOnTime(LocalDateTime currentTime) {
	// return task.isOnTime(currentTime);
	// }
	//
	// public int getOvertimePercentage(LocalDateTime currentTime) {
	// return task.getOverTimePercentage(currentTime);
	// }

	/**
	 * Returns whether the current Task is on time, depending on the estimated
	 * duration
	 * 
	 * @return True if the Task is on time. False if the current date false
	 *         after beginTime + EstimatedDur (in working minutes)
	 */
	public boolean isOnTime(LocalDateTime currentTime) {
		if (getStartTime() == null) {
			return false;
		}
		TimeSpan acceptableSpan = task.getEstimatedDuration();
		LocalDateTime acceptableEndDate = TimeSpan.addSpanToLDT(
				task.getBeginTime(), acceptableSpan);

		if (hasEnded()) {
			return !task.getEndTime().isAfter(acceptableEndDate);
		}

		return !currentTime.isAfter(acceptableEndDate);
	}

	/**
	 * Returns whether the current is unacceptably overdue, depending on the
	 * estimated duration and acceptable deviation of the task.
	 * 
	 * @return True if the project is overtime beyond the deviation. False
	 *         otherwise.
	 */
	public boolean isUnacceptableOverdue(LocalDateTime currentTime) {
		if (getStartTime() == null) {
			return true;
		}
		TimeSpan acceptableSpan = task.getEstimatedDuration()
				.getAcceptableSpan(getAcceptableDeviation());
		LocalDateTime acceptableEndDate = TimeSpan.addSpanToLDT(getStartTime(),
				acceptableSpan);

		if (hasEnded()) {
			return getEndTime().isAfter(acceptableEndDate);
		}

		return currentTime.isAfter(acceptableEndDate);
	}

	/**
	 * Returns the percentage of overdueness of the task, depending on the
	 * estimated duration of the task. Returns 0 if the task is well on time.
	 * 
	 * @return The percentage of overdue.
	 */
	public int getOverTimePercentage(LocalDateTime currentTime) {
		if (isOnTime(currentTime)) {
			return 0;
		}
		int overdue = task.getTimeSpent(currentTime).getDifferenceMinute(
				task.getEstimatedDuration());
		return (overdue / task.getEstimatedDuration().getSpanMinutes()) * 100;
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

	// public List<ResourceView> getPossibleResourceInstances(ResourceView
	// resourceType){
	// return task.getPossibleResourceInstances(resourceType);
	// }

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
	 * @param amount
	 *            | The amount of suggestions that should be returned
	 * @return a given amount of suggested starting times for the task to be
	 *         planned at
	 */
	public List<LocalDateTime> getPossibleStartingTimes(
			List<ResourceView> concRes, int amount) {
		return task.getPossibleTaskStartingTimes(concRes, amount);
	}

	/**
	 * Check whether two views are equal to each other
	 * 
	 * @param otherView
	 *            | The other view to compare with
	 * @return True if the views are identical or share the same task
	 */
	public boolean equals(TaskView otherView) {
		if (this == otherView)
			return true;
		if (otherView == null)
			return false;
		return otherView.hasAsTask(task);
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
}
