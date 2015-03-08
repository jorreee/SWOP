package taskMan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Task {
	private final String description;
	private final LocalTime estimatedDuration;
	private final float acceptableDeviation;
	private TaskStatus taskStatus;
	private LocalDateTime beginTime;
	private LocalDateTime endTime;
	private final int taskID;

	//TODO status niet INIT
	public Task(int taskID, String taskDescription, LocalTime estimatedDuration,
			float acceptableDeviation) {
		this.taskID = taskID;
		this.description = taskDescription;
		this.estimatedDuration = estimatedDuration;
		this.acceptableDeviation = acceptableDeviation;
	}
	
	/**
	 * Create task with start and end time given (only with finished or failed tasks)
	 */
	public Task(int taskID, String taskDescription, LocalTime estimatedDuration,
			float acceptableDeviation, TaskStatus taskStatus,
			LocalDateTime beginTime, LocalDateTime endTime) {
		this(taskID, taskDescription, estimatedDuration, acceptableDeviation);
		if(taskStatus != TaskStatus.FAILED || taskStatus != TaskStatus.FINISHED)
			throw new IllegalArgumentException("Time stamps are only required if a task is finished or failed");
		this.taskStatus = taskStatus;
		this.beginTime = beginTime;
		this.endTime = endTime;
	}
	
	private void changeStatus(String status) {
		taskStatus = TaskStatus.valueOf(status);
	}
	
	public boolean isFinished() {
		return (taskStatus == TaskStatus.FINISHED);
	}
	
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}
	
	private void updateBeginTime(LocalDateTime beginTime) {
		this.beginTime = beginTime;
	}
	
	private void updateEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
	
	/**
	 * @param currentTime The time with which the start time of the task must be compared with
	 * @throws IllegalStateException when no start time has yet been documented
	 * @throws IllegalArgumentException when the start time of the task is after the time given
	 */
	public LocalDateTime getTimeElapsed(LocalDateTime currentTime) {
		if(beginTime == null)
			throw new IllegalStateException("Project not yet started");
		if(beginTime.isAfter(currentTime))
			throw new IllegalArgumentException("Timestamp is in the past");
		
		LocalDateTime tempDateTime = LocalDateTime.from( beginTime );
		long years = tempDateTime.until( currentTime, ChronoUnit.YEARS);
		tempDateTime = tempDateTime.plusYears( years );

		long months = tempDateTime.until( currentTime, ChronoUnit.MONTHS);
		tempDateTime = tempDateTime.plusMonths( months );

		long days = tempDateTime.until( currentTime, ChronoUnit.DAYS);
		tempDateTime = tempDateTime.plusDays( days );

		long hours = tempDateTime.until( currentTime, ChronoUnit.HOURS);
		tempDateTime = tempDateTime.plusHours( hours );

		long minutes = tempDateTime.until( currentTime, ChronoUnit.MINUTES);
		tempDateTime = tempDateTime.plusMinutes( minutes );

		long seconds = tempDateTime.until( currentTime, ChronoUnit.SECONDS);
		tempDateTime = tempDateTime.plusSeconds( seconds );
		
		return tempDateTime;
	}
	
	/**
	 * @return returns time elapsed between the start time and end time
	 * @throws IllegalStateException whenever the end time is not yet determined
	 */
	public LocalDateTime getTimeSpan() {
		if(endTime == null)
			throw new IllegalStateException("Project not yet finished");
		return getTimeElapsed(endTime);
	}
	
	public int getTaskID() { return taskID; }

	public boolean updateTaskDetails(LocalDateTime startTime,
			LocalDateTime endTime, TaskStatus taskStatus) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
