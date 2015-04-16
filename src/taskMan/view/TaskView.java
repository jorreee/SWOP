package taskMan.view;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import taskMan.Task;
import taskMan.util.TimeSpan;

import com.google.common.collect.ImmutableList;

public class TaskView {
	
	private final Task task;
	
	public TaskView(Task t) {
		this.task = t;
	}
	
	public int getID() {
		return task.getID();
	}
	
	public String getDescription() {
		return task.getDescription();
	}
	
	public LocalDateTime getStartTime() {
		return task.getBeginTime();
	}
	
	public int getEstimatedDuration() {
		return task.getEstimatedDuration().getSpanMinutes();
	}
	
	public int getAcceptableDeviation() {
		return task.getAcceptableDeviation();
	}
	
	public LocalDateTime getEndTime() {
		return task.getEndTime();
	}
	
	public String getStatusAsString() {
		return task.getStatus();
	}
	
	public boolean hasPrerequisites() {
		return !getPrerequisites().isEmpty();
	}
	
	public List<TaskView> getPrerequisites() {
		ImmutableList.Builder<TaskView> taskPrereqs = ImmutableList.builder();
		for(Task t : task.getPrerequisites()) {
			taskPrereqs.add(new TaskView(t));
		}
		return taskPrereqs.build();
	}
	
	public boolean hasReplacement() {
		return getReplacement() != null;
	}
	
	public TaskView getReplacement() {
		Task rep = task.getReplacement();
		if(rep == null) {
			return null;
		}
		return new TaskView(rep);
	}
	
	public boolean isAlternative() {
		return getAlternativeTo() != null;
	}
	
	public TaskView getAlternativeTo() {
		Task alt = task.getAlternativeFor();
		if(alt == null) {
			return null;
		}
		return new TaskView(alt);
	}
	
	public boolean isAvailable() {
		return task.isAvailable();
	}
	
	public boolean isUnavailable() {
		return task.isUnavailable();
	}
	
	public boolean hasEnded() {
		return task.hasEnded();
	}
//	
//	public boolean isUnacceptableOverdue(LocalDateTime currentTime) {
//		return task.isUnacceptableOverdue(currentTime);
//	}
//	
//	public boolean isOnTime(LocalDateTime currentTime) {
//		return task.isOnTime(currentTime);
//	}
//	
//	public int getOvertimePercentage(LocalDateTime currentTime) {
//		return task.getOverTimePercentage(currentTime);
//	}
	
	/**
	 * Returns whether the current Task is on time, depending on the estimated duration
	 * 
	 * @return	True if the Task is on time.
	 * 			False if the current date false after beginTime + EstimatedDur (in working minutes)
	 */
	public boolean isOnTime(LocalDateTime currentTime){
		if(getStartTime() == null) {
			return false;
		}
		TimeSpan acceptableSpan = task.getEstimatedDuration();
		LocalDateTime acceptableEndDate = TimeSpan.addSpanToLDT(task.getBeginTime(), acceptableSpan);
		
		if(hasEnded()) {
			return !task.getEndTime().isAfter(acceptableEndDate);
		}
		
		return !currentTime.isAfter(acceptableEndDate);
	}

	/**
	 * Returns whether the current is unacceptably overdue, depending on the estimated
	 * duration and acceptable deviation of the task.
	 * 
	 * @return	True if the project is overtime beyond the deviation.
	 * 			False otherwise.
	 */
	public boolean isUnacceptableOverdue(LocalDateTime currentTime) {
		if(getStartTime() == null) {
			return true;
		}
		TimeSpan acceptableSpan = task.getEstimatedDuration().getAcceptableSpan(getAcceptableDeviation());
		LocalDateTime acceptableEndDate = TimeSpan.addSpanToLDT(getStartTime(), acceptableSpan);
		
		if(hasEnded()) {
			return getEndTime().isAfter(acceptableEndDate);
		}
		
		return currentTime.isAfter(acceptableEndDate);
	}

	/**
	 * Returns the percentage of overdueness of the task, depending on the 
	 * estimated duration of the task. Returns 0 if the task
	 * is well on time.
	 * 
	 * @return	The percentage of overdue.
	 */ //TODO we kunnen ook stellen dat een taak enkel overtime is wanneer hij voorbij
		// de unacceptable delay is.
	public int getOverTimePercentage(LocalDateTime currentTime) {
		if(isOnTime(currentTime)) {
			return 0;
		}
		int overdue = task.getTimeSpent(currentTime).getDifferenceMinute(task.getEstimatedDuration());
		return ( overdue / task.getEstimatedDuration().getSpanMinutes() ) * 100;
	}
	
	public boolean hasAsTask(Task t) {
		if(t == null || task == null) {
			return false;
		}
		return task.equals(t);
	}
	
	public Map<ResourceView,Integer> getRequiredResources(){
		return task.getRequiredResources();
	}
	
	public List<ResourceView> getPossibleResourceInstances(ResourceView resourceType){
		return task.getPossibleResourceInstances(resourceType);
	}

	public boolean equals(TaskView otherView) {
		if (this == otherView)
			return true;
		if (otherView == null)
			return false;
		return otherView.hasAsTask(task);
	}
	
	public List<ResourceView> getPlannedDevelopers(){
		return task.getPlannedDevelopers();
	}
	
	public LocalDateTime getPlannedBeginTime() {
		return task.getPlannedBeginTime();
	}

	public boolean isPlanned(){
		return (task.getPlannedBeginTime() != null);
	}
}
