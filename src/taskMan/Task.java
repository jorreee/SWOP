package taskMan;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;


//TODO task moeten dependencies hebben, dependencies moeten fullfiled zijn voor available anders unavailable
//TODO enkel alternative voor task die failed is, alt pakt timespan en dependencies over
//TODO timespan via 2 constructors, via extra parameters. Extra tijd variable moet private zijn(get and set)
//TODO Finished 
//TODO
//TODO voor delay van task nu - begintime van Task
//TODO float wordt int
//TODO estimateddur int
public class Task {
	private final String description;
	private final int estimatedDuration;
	private final int acceptableDeviation;
	private TaskStatus taskStatus;
	private LocalDateTime beginTime;
	private LocalDateTime endTime;
	private final int taskID;
	private final int extraTime;

	/**
	 * Create a new Task.
	 * 
	 * @param 	taskID
	 * 			The ID of the new Task.
	 * @param 	taskDescription
	 * 			The description of the new Task.
	 * @param 	estimatedDuration
	 * 			The estimated duration of the new Task.
	 * @param 	acceptableDeviation
	 * 			The acceptable deviation of the new Task.
	 * @param	extraTime
	 * 			The extra time to add to the elapsed time
	 */
	public Task(int taskID, String taskDescription, int estimatedDuration,
			int acceptableDeviation,int extraTime) {
		this.taskID = taskID;
		this.description = taskDescription;
		this.estimatedDuration = estimatedDuration;
		this.acceptableDeviation = acceptableDeviation;
		this.extraTime = extraTime;
	}
	
	/**
	 Create a new Task without an added extra time.
	 * 
	 * @param 	taskID
	 * 			The ID of the new Task.
	 * @param 	taskDescription
	 * 			The description of the new Task.
	 * @param 	estimatedDuration
	 * 			The estimated duration of the new Task.
	 * @param 	acceptableDeviation
	 * 			The acceptable deviation of the new Task.
	 */
	public Task(int taskID, String taskDescription, int estimatedDuration,
			int acceptableDeviation){
		this(taskID,taskDescription,estimatedDuration,acceptableDeviation,0);
	}
	
	/**
	 * Create a new Task with start and end time given (only with finished or failed tasks).
	 * 
	 * @param 	taskID
	 * 			The ID of the new Task.
	 * @param 	taskDescription
	 * 			The description of the new Task.
	 * @param 	estimatedDuration
	 * 			The estimated duration of the new Task.
	 * @param 	acceptableDeviation
	 * 			The acceptable deviation of the new Task.
	 * @param 	taskStatus
	 * 			The status of the new Task.
	 * @param 	beginTime
	 * 			The begin time of the new Task.
	 * @param 	endTime
	 * 			The endtime of the new Task.
	 */
	public Task(int taskID, String taskDescription, int estimatedDuration,
			int acceptableDeviation, TaskStatus taskStatus,
			LocalDateTime beginTime, LocalDateTime endTime) throws IllegalArgumentException {
		this(taskID, taskDescription, estimatedDuration, acceptableDeviation);
		if(taskStatus != TaskStatus.FAILED && taskStatus != TaskStatus.FINISHED)
			throw new IllegalArgumentException("Time stamps are only required if a task is finished or failed");
		this.taskStatus = taskStatus;
		this.beginTime = beginTime;
		this.endTime = endTime;
	}
	
//	Methode door Tim toegevoegd.
//	/**
//	 * Create task with start and end time given (only with finished or failed tasks)
//	 */
//	public Task(int taskID, String taskDescription, LocalTime estimatedDuration,
//			float acceptableDeviation, String taskStatus,
//			LocalDateTime beginTime, LocalDateTime endTime) {
//		this(taskID, taskDescription, estimatedDuration, acceptableDeviation);
////		if(taskStatus != TaskStatus.FAILED || taskStatus != TaskStatus.FINISHED)
////			throw new IllegalArgumentException("Time stamps are only required if a task is finished or failed");
////		PARSE STRING NAAR JUISTE TASKSTATUS
//		this.taskStatus = TaskStatus.AVAILABLE;
//		this.beginTime = beginTime;
//		this.endTime = endTime;
//	}
	
	/**
	 * Checks whether the Task is finished.
	 * 
	 * @return	True if and only if the Task has a finished status.
	 */
	public boolean isFinished() {
		return (taskStatus == TaskStatus.FINISHED);
	}
	
	/**
	 * Checks whether the the Task has failed.
	 * 
	 * @return	True if and only the Task has failed.
	 */
	public boolean isFailed(){
		return (taskStatus ==TaskStatus.FAILED);
	}
	
	/**
	 * Checks whether the Task is available.
	 * 
	 * @return	True if and only the Task is available.
	 */
	public boolean isAvailable(){
		return (taskStatus == TaskStatus.AVAILABLE);
	}
	
	/**
	 * Checks whether the Task is unavailable.
	 * 
	 * @return	True if and only the Task is unavailable.
	 */
	public boolean isUnavailable(){
		return (taskStatus == TaskStatus.UNAVAILABLE);
	}
	
	/**
	 * Checks whether the Task has started.
	 * 
	 * @return	True if the Task has started.
	 * 			False otherwise.
	 */
	public boolean hasStarted(){
		if(this.getBeginTime()==null)
			return false;
		else
			return true;
	}
	
	/**
	 * checks whether the Task has ended.
	 * 
	 * @return	True if the Task has ended.
	 * 			False otherwise.
	 */
	public boolean hasEnded(){
		if(this.getEndTime()==null)
			return false;
		else
			return true;
	}
	
//	/**
//	 * Set the extra time of the Task.
//	 * 
//	 * @param 	time
//	 * 			The extra time to add.
//	 * @return	True if the extra time was added successfully.
//	 * 			False if the extra time has a negative value.
//	 */
//	private boolean setExtraTime(int time){
//		if(time<0)
//			return false;
//		this.extraTime = time;
//		return true;
//	}
	
//	private void updateBeginTime(LocalDateTime beginTime) {
//		this.beginTime = beginTime;
//	}
//	
//	private void updateEndTime(LocalDateTime endTime) {
//		this.endTime = endTime;
//	}
//	
//	private void changeStatus(String status) {
//		taskStatus = TaskStatus.valueOf(status);
//	}
	
	/**
	 * This method compares the start time of the project with a given time and calculates the
	 * elapsed time.
	 * 
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
	 * This method returns the time elapsed since the start of the project and 
	 * the end of the project. 
	 * 
	 * @return returns time elapsed between the start time and end time
	 * @throws IllegalStateException whenever the end time is not yet determined
	 */
	public LocalDateTime getTimeSpan() {
		if(endTime == null)
			throw new IllegalStateException("Project not yet finished");
		return getTimeElapsed(endTime);
	}
	
	/**
	 * Returns the start time of the Task.
	 * 
	 * @return	The start time of the Task.
	 */
	public LocalDateTime getBeginTime() {
		return beginTime;
	}

	/**
	 * This Method sets the start time of the Task.
	 * 
	 * @param 	beginTime
	 * 			The new start time for the Task.
	 * @throws	IllegalArgumentException
	 * 			If the new begin time is null or the old begin time is already set. 
	 */
	public void setBeginTime(LocalDateTime beginTime) throws IllegalArgumentException{
		if(beginTime==null)
			throw new IllegalArgumentException("The new beginTime is null");
		if(this.beginTime!=null)
			throw new IllegalArgumentException("The begintime is already set");
		else
			this.beginTime = beginTime;
	}

	/**
	 * Returns the end time of the Task.
	 * 
	 * @return	The end time of the Task.
	 */
	public LocalDateTime getEndTime() {
		return endTime;
	}

	/**
	 * Sets the end time of Task.
	 * 
	 * @param 	endTime
	 * 			The new end time of the Task.
	 * @throws	IllegalArgumentException
	 * 			If the new end time is null or the old end time is already set. 
	 */
	public void setEndTime(LocalDateTime endTime) throws IllegalArgumentException {
		if(endTime==null)
			throw new IllegalArgumentException("The new endTime is null");
		if(this.endTime!=null)
			throw new IllegalArgumentException("The endtime is already set");
		else
			this.endTime = endTime;
	}

	/**
	 * Returns the description of the Task.
	 * 
	 * @return	The description of the task.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the estimated duration of the Task.
	 * 
	 * @return	The estimated duration of the Task.
	 */
	public int getEstimatedDuration() {
		return estimatedDuration;
	}

	/**
	 * Returns the acceptable deviation of the Task.
	 * 
	 * @return	The accepatble deviation of the Task.
	 */
	public int getAcceptableDeviation() {
		return acceptableDeviation;
	}

	/**
	 * Set the status of this Task.
	 * 
	 * @param 	taskStatus
	 * 			The new status of this Task.
	 * @throws	IllegalArgumentException
	 * 			If the status isn't a valid one.
	 */
	public void setTaskStatus(String taskStatus) throws IllegalArgumentException{
		try{
			this.taskStatus = TaskStatus.valueOf(taskStatus);
		}catch(Exception e){
			throw new IllegalArgumentException("Invalid status");
		}
	}
	
	/**
	 * Returns the current status of the Task.
	 * 
	 * @return	The current status of the Task.
	 */
	private TaskStatus getTaskStatus() {
		return taskStatus;
	}
	
	/**
	 * Returns the status of the Task as a String.
	 * 
	 * @return	The status of the Task as a String.
	 */
	public String getStatus(){
		TaskStatus stat = this.getTaskStatus();
		String status ="";
		switch(stat){
			case FAILED:
				status = "FAILED";
				break;
			case FINISHED:
				status = "FINISHED";
				break;
			case AVAILABLE:
				status = "AVAILABLE";
				break;
			case UNAVAILABLE:
				status = "UNAVAILABLE";
				break;
			default:
				status ="ERROR";
				break;
		}
		return status;
				
	}

	/**
	 * Returns the TaskID of the this Task.
	 * 
	 * @return	The ID of this Task.
	 */
	public int getTaskID() { 
		return taskID; 
	}

	/**
	 * Updates the details of this Task.
	 * 
	 * @param 	startTime
	 * 			The new startTime of the Task.
	 * @param 	endTime
	 * 			The new end time of the Task.
	 * @param 	taskStatus
	 * 			The new Status of the Task.
	 * @return	True if and only if the updates succeeds.
	 */
	public boolean updateTaskDetails(LocalDateTime startTime,
			LocalDateTime endTime, String taskStatus) {
		try{
			this.setBeginTime(startTime);
			this.setEndTime(endTime);
			this.setTaskStatus(taskStatus);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null)
			return false;
		if(o.getClass()!=this.getClass())
			return false;
		if(this.getTaskID()!=((Task)o).getTaskID())
			return false;
		return true;
		
	}
	
	
}
