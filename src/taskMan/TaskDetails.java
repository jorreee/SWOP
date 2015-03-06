package taskMan;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Deprecated
public class TaskDetails extends Detail {
	
	private final String description;
	private final LocalTime estimatedDuration;
	private final float acceptableDeviation;
	private final TaskStatus taskStatus;
	private final LocalDateTime beginTime;
	private final LocalDateTime dueTime;
	private final LocalDateTime endTime;
	private final int taskID;
	
	public TaskDetails(String description, LocalTime estimatedDuration,
			float acceptableDeviation, TaskStatus taskStatus,
			LocalDateTime beginTime, LocalDateTime dueTime,
			LocalDateTime endTime, int taskID) {
		super();
		this.description = description;
		this.estimatedDuration = estimatedDuration;
		this.acceptableDeviation = acceptableDeviation;
		this.taskStatus = taskStatus;
		this.beginTime = beginTime;
		this.dueTime = dueTime;
		this.endTime = endTime;
		this.taskID = taskID;
	}

	public String getDescription() {
		return description;
	}

	public LocalTime getEstimatedDuration() {
		return estimatedDuration;
	}

	public float getAcceptableDeviation() {
		return acceptableDeviation;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public LocalDateTime getBeginTime() {
		return beginTime;
	}

	public LocalDateTime getDueTime() {
		return dueTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public int getTaskID() {
		return taskID;
	}
	
	
}
