package taskMan.view;

import java.time.LocalDateTime;
import java.util.List;

import taskMan.Project;
import taskMan.util.TimeSpan;

import com.google.common.collect.ImmutableList;

public class ProjectView {

	private final Project project;

	public ProjectView(Project p) {
		this.project = p;
	}
	
	public int getID() {
		return project.getID();
	}

	public String getName() {
		return project.getName();
	}

	public String getDescription() {
		return project.getDescription();
	}

	public LocalDateTime getCreationTime() {
		return project.getCreationTime();
	}

	public LocalDateTime getDueTime() {
		return project.getDueTime();
	}

	public LocalDateTime getEndTime() {
		return project.getEndTime();
	}

	public String getStatusAsString() {
		return project.getStatus();
	}

	public List<TaskView> getTasks() {
		ImmutableList.Builder<TaskView> tasks = ImmutableList.builder();
		tasks.addAll(project.getTaskViews());
		return tasks.build();
	}

	/**
	 * Returns a list of the id's of the available tasks of the project
	 * 
	 * @return	a list of the available tasks
	 */
	public List<TaskView> getAvailableTasks() {
		ImmutableList.Builder<TaskView> availableTasks = ImmutableList.builder();
		for(TaskView task : project.getTaskViews()) {
			if(task.isAvailable()) {
				availableTasks.add(task);
			}
		}
		return availableTasks.build();
	}
	
//	public int[] getRealDelay(LocalDateTime time) {
//		if(!project.getEndTime().isAfter(project.getDueTime())) {
//			return new int[]{ 0,0,0,0,0 };
//		}
//		return new TimeSpan(project.getDueTime(),project.getEndTime()).getSpan();
//	}

	/**
	 * Returns the estimated time in working minutes that the project 
	 * will be finished over time.
	 * 
	 * @param	projectID
	 * 			the id of the given project
	 * @return	The amount of years, months, days, hours and minutes
	 * 			that are estimated to be required to finish the project
	 */
	public int[] getDelay(LocalDateTime currentTime) {
		LocalDateTime estimatedEndTime = project.getEstimatedEndTime(currentTime);
		
		if(project.getDueTime().isAfter(estimatedEndTime)) {
			return new TimeSpan(0).getSpan();
		}
		
		return new TimeSpan(TimeSpan.getDifferenceWorkingMinutes(project.getDueTime(), estimatedEndTime)).getSpan();
		
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
		return !project.getEstimatedEndTime(currentTime).isAfter(project.getDueTime());
	}
	
	public boolean isFinished() {
		return project.isFinished();
	}
	
	public boolean hasAsProject(Project p) {
		if(p == null || project == null) {
			return false;
		}
		return project.equals(p);
	}

	public boolean equals(ProjectView otherView) {
		if (this == otherView)
			return true;
		if (otherView == null)
			return false;
		return otherView.hasAsProject(project);
	}

	public List<TaskView> getUnplannedTasks() {
		// TODO Auto-generated method stub
		return null;
	}

	public LocalDateTime getEstimatedEndTime() {
		// TODO Auto-generated method stub
		return null;
	}

}
