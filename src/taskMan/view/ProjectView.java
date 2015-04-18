package taskMan.view;

import java.time.LocalDateTime;
import java.util.List;

import taskMan.Project;
import taskMan.util.TimeSpan;

import com.google.common.collect.ImmutableList;

/**
 * A projectView is a wrapper for projects. The projectView only has limited
 * access to the project and is thus safe to send out to the UI. The UI will
 * hence also only have limited access to the underlying projects and cannot do
 * anything unauthorized.
 * 
 * @author Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class ProjectView {

	private final Project project;

	/**
	 * Wrap a project in a new view
	 * 
	 * @param p
	 *            | The project to wrap
	 */
	public ProjectView(Project p) {
		this.project = p;
	}

	@Deprecated
	/**
	 * Use getName() instead
	 */
	public int getID() {
		return project.getID();
	}

	/**
	 * Retrieve the name of the project
	 * 
	 * @return the project name
	 */
	public String getName() {
		return project.getName();
	}

	/**
	 * Retrieve the description of the project
	 * 
	 * @return the project description
	 */
	public String getDescription() {
		return project.getDescription();
	}

	/**
	 * Retrieve the creation time of the project
	 * 
	 * @return the project creation time
	 */
	public LocalDateTime getCreationTime() {
		return project.getCreationTime();
	}

	/**
	 * Retrieve the due time of the project
	 * 
	 * @return the project due time
	 */
	public LocalDateTime getDueTime() {
		return project.getDueTime();
	}

	/**
	 * Retrieve the end time of the project
	 * 
	 * @return the project end time
	 */
	public LocalDateTime getEndTime() {
		return project.getEndTime();
	}

	/**
	 * Retrieve the status of the project as a string
	 * 
	 * @return the project status as a string
	 */
	public String getStatusAsString() {
		return project.getStatus();
	}

	/**
	 * Retrieve the tasks (wrapped in TaskViews) of the project
	 * 
	 * @return the project tasks
	 */
	public List<TaskView> getTasks() {
		ImmutableList.Builder<TaskView> tasks = ImmutableList.builder();
		tasks.addAll(project.getTaskViews());
		return tasks.build();
	}

	/**
	 * Returns a list of the the available tasks (wrapped in TaskViews) of the
	 * project
	 * 
	 * @return a list of the available tasks
	 */
	public List<TaskView> getAvailableTasks() {
		ImmutableList.Builder<TaskView> availableTasks = ImmutableList
				.builder();
		for (TaskView task : project.getTaskViews()) {
			if (task.isAvailable()) {
				availableTasks.add(task);
			}
		}
		return availableTasks.build();
	}

	// public int[] getRealDelay(LocalDateTime time) {
	// if(!project.getEndTime().isAfter(project.getDueTime())) {
	// return new int[]{ 0,0,0,0,0 };
	// }
	// return new TimeSpan(project.getDueTime(),project.getEndTime()).getSpan();
	// }

	/**
	 * The amount of working time the current project is delayed by compared to
	 * a timestamp
	 * 
	 * @param currentTime
	 *            | The timestamp to compare to
	 * @return the amount of years, months, days, hours and minutes of working
	 *         time the project is delayed
	 */
	public int[] getDelay(LocalDateTime currentTime) {
		LocalDateTime estimatedEndTime = project
				.getEstimatedEndTime(currentTime);

		if (project.getDueTime().isAfter(estimatedEndTime)) {
			return new TimeSpan(0).getSpan();
		}

		return new TimeSpan(TimeSpan.getDifferenceWorkingMinutes(
				project.getDueTime(), estimatedEndTime)).getSpan();

	}

	/**
	 * A check to determine if the project will end on time
	 * 
	 * @param currentTime
	 *            The current time of the system
	 * @return True if the estimated required time to finish all tasks is less
	 *         than the time until the project due time
	 */
	public boolean isEstimatedOnTime(LocalDateTime currentTime) {
		return !project.getEstimatedEndTime(currentTime).isAfter(
				project.getDueTime());
	}

	/**
	 * Check whether or not the project is finished
	 * 
	 * @return True when the project is finished, false otherwise
	 */
	public boolean isFinished() {
		return project.isFinished();
	}

	/**
	 * Check whether or not a given project belongs to this view
	 * 
	 * @param p
	 *            | The project to check
	 * @return True if this view contains the given project, false otherwise
	 */
	public boolean hasAsProject(Project p) {
		if (p == null || project == null) {
			return false;
		}
		return project.equals(p);
	}

	/**
	 * Check whether two views are equal
	 * 
	 * @param otherView
	 *            | The other view to compare to
	 * @return True if the other view contains the same project as this one
	 */
	public boolean equals(ProjectView otherView) {
		if (this == otherView)
			return true;
		if (otherView == null)
			return false;
		return otherView.hasAsProject(project);
	}

	/**
	 * A method to retrieve an immutable list of unplanned tasks
	 * 
	 * @return all unplanned tasks (wrapped in views) in the project contained
	 *         in this view
	 */
	public List<TaskView> getUnplannedTasks() {
		ImmutableList.Builder<TaskView> unavailableTasks = ImmutableList
				.builder();
		for (TaskView task : project.getTaskViews()) {
			if (task.isUnavailable()) {
				unavailableTasks.add(task);
			}
		}
		return unavailableTasks.build();
	}

	/**
	 * Calculate the estimated end time of the project contained in this view
	 * relative to a given timestamp
	 * 
	 * @param currentTime
	 *            | The timestamp to calculate the estimated end time from
	 * @return a timestamp when the project should be estimated to end relative
	 *         to a given timestamp
	 */
	public LocalDateTime getEstimatedEndTime(LocalDateTime currentTime) {
		return project.getEstimatedEndTime(currentTime);
	}

}
