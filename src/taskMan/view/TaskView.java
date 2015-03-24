package taskMan.view;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import taskMan.Task;
import taskMan.util.TimeSpan;

public class TaskView {
	
	private final Task task;
	
	public TaskView(Task t) {
		this.task = t;
	}
	
	public int getID() {
		return task.getTaskID();
	}
	
	public String getTaskDescription() {
		return task.getDescription();
	}
	
	public LocalDateTime getTaskStartTime() {
		return task.getStartTime();
	}
	
	//TODO niet gebruikt
	public TimeSpan getEstimatedTaskDuration() {
		return task.getEstimatedDuration();
	}
	
	public int getAcceptableTaskDeviation() {
		return task.getAcceptableDeviation();
	}
	
	public int getTaskOvertimePercentage() {
		return task.getOverTimePercentage();
	}
	
	public LocalDateTime getTaskEndTime() {
		return task.getEndTime();
	}
	
	public String getTaskStatusAsString() {
		return task.getStatus();
	}
	
	public List<TaskView> getTaskPrerequisites() {
		List<TaskView> taskPrereqs = new ArrayList<TaskView>();
		for(Task t : task.getTaskPrerequisites()) {
			taskPrereqs.add(new TaskView(t));
		}
		return taskPrereqs;
	}
	
	public TaskView getTaskAlternativeTo() {
		return new TaskView(task.getAlternative());
	}
	
	public boolean hasTaskPrerequisites() {
		return !getTaskPrerequisites().isEmpty();
	}
	
	public boolean hasTaskAlternative() {
		return getTaskAlternativeTo() != null;
	}
	
	public boolean hasEnded() {
		return task.hasEnded();
	}
	
	public boolean isTaskUnacceptableOverdue() {
		return task.isUnacceptableOverdue();
	}
	
	public boolean isTaskOnTime() {
		return task.isUnacceptableOverdue();
	}
	
	//TODO eh...
	@Override
	public boolean equals(Object obj) {
		return task.equals(obj);
	}
	
	public boolean hasAsTask(Task t) {
		return task == t;
//		return task.equals(t); ?
	}

}
