package taskMan.view;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import taskMan.Task;

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
	
	public int getOvertimePercentage(LocalDateTime currentTime) {
		return task.getOverTimePercentage(currentTime);
	}
	
	public LocalDateTime getEndTime() {
		return task.getEndTime();
	}
	
	public String getStatusAsString() {
		return task.getStatus();
	}
	
	public List<TaskView> getPrerequisites() {
		ImmutableList.Builder<TaskView> taskPrereqs = ImmutableList.builder();
		for(Task t : task.getPrerequisites()) {
			taskPrereqs.add(new TaskView(t));
		}
		return taskPrereqs.build();
	}
	
	public TaskView getAlternativeTo() {
		Task alt = task.getAlternativeFor();
		if(alt == null) {
			return null;
		}
		return new TaskView(alt);
	}
	
	public TaskView getReplacement() {
		Task rep = task.getReplacement();
		if(rep == null) {
			return null;
		}
		return new TaskView(rep);
	}
	
	public boolean hasPrerequisites() {
		return !getPrerequisites().isEmpty();
	}
	
	public boolean isAlternative() {
		return getAlternativeTo() != null;
	}
	
	public boolean hasEnded() {
		return task.hasEnded();
	}
	
	public boolean isUnacceptableOverdue(LocalDateTime currentTime) {
		return task.isUnacceptableOverdue(currentTime);
	}
	
	public boolean isOnTime(LocalDateTime currentTime) {
		return task.isOnTime(currentTime);
	}
	
	public boolean hasAsTask(Task t) {
		return task == t;
	}
	
	public HashMap<ResourceView,Integer> getRequiredResources(){
		return task.getRequiredResources();
	}
	
	public List<ResourceView> getPossibleResourceInstances(ResourceView resourceType){
		return task.getPossibleResourceInstances(resourceType);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((task == null) ? 0 : task.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskView other = (TaskView) obj;
		return other.hasAsTask(task);
	}

}
