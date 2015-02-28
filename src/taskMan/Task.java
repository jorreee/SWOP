package taskMan;

public class Task {
	private final String description;
	private final Time estimatedDuration;
	private final float acceptableDeviation;
	private TaskStatus taskStatus;
	private Time beginTime;
	private Time endTime;
	private final int taskID;

	public Task(int taskID, String taskDescription, Time estimatedDuration,
			float acceptableDeviation, TaskStatus taskStatus) {
		this.taskID = taskID;
		this.description = taskDescription;
		this.estimatedDuration = estimatedDuration;
		this.acceptableDeviation = acceptableDeviation;
		this.taskStatus = taskStatus;
	}
	
	public void changeStatus(String status) {
		taskStatus = TaskStatus.valueOf(status);
	}
	
	
	public String getDescription() { return description; }
	public Time getEstimatedDuration() { return estimatedDuration; }
	public float getAcceptableDeviation() { return acceptableDeviation;	}
	public TaskStatus getTaskStatus() {	return taskStatus; }
	public Time getBeginTime() { return beginTime; }
	public Time getEndTime() { return endTime; }
	public int getTaskID() { return taskID; }
	
}
