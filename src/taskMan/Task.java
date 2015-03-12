package taskMan;

import java.time.LocalDateTime;

import taskMan.util.TimeSpan;


//TODO task moeten dependencies hebben, dependencies moeten fullfiled zijn voor available anders unavailable
//TODO enkel alternative voor task die failed is, alt pakt timespan en dependencies over
//TODO timespan via 2 constructors, via extra parameters. Extra tijd variable moet private zijn(get and set)
//TODO Finished 
//TODO voor delay van task nu - begintime van Task
/**
 * The Task object.
 * 
 * @author	Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel, Eli Vangrieken
 *
 */
public class Task {
	private final String description;
	private final TimeSpan estimatedDuration;
	private final int acceptableDeviation;
	private TaskStatus taskStatus;
	private LocalDateTime beginTime;
	private LocalDateTime endTime;
	private final int taskID;
	private final TimeSpan extraTime;

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
			int acceptableDeviation, TimeSpan extraTime) {
		this.taskID = taskID;
		this.description = taskDescription;
		this.estimatedDuration = new TimeSpan(estimatedDuration);
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
			int acceptableDeviation) throws IllegalArgumentException {
		if(!isValidTaskID(taskID))
			throw new IllegalArgumentException("Invalid deviation");
		if(!isValidDescription(taskDescription))
			throw new IllegalArgumentException("Invalid deviation");
		if(!isValidDuration(estimatedDuration))
			throw new IllegalArgumentException("Invalid deviation");
		if(!isValidDeviation(acceptableDeviation))
			throw new IllegalArgumentException("Invalid deviation");
		
		this.taskID = taskID;
		this.description = taskDescription;
		this.estimatedDuration = new TimeSpan(estimatedDuration);
		this.acceptableDeviation = acceptableDeviation;
		this.extraTime = new TimeSpan(0);
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
			int acceptableDeviation, String taskStatus,
			LocalDateTime beginTime, LocalDateTime endTime) throws IllegalArgumentException {
		this(taskID, taskDescription, estimatedDuration, acceptableDeviation);
		if(!taskStatus.equals("failed") && !taskStatus.equals("finished"))
			throw new IllegalArgumentException("Time stamps are only required if a task is finished or failed");
		this.taskStatus = TaskStatus.valueOf(taskStatus);
		this.beginTime = beginTime;
		this.endTime = endTime;
	}

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

//	/**
//	 * Checks whether the Task has started.
//	 * 
//	 * @return	True if the Task has started.
//	 * 			False otherwise.
//	 */
//	public boolean hasStarted(){
//		if(this.getBeginTime()==null)
//			return false;
//		else
//			return true;
//	}

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

	/**
	 * This method compares the start time of the project with a given time and calculates the
	 * elapsed time.
	 * 
	 * @param currentTime The time with which the start time of the task must be compared with
	 * @throws IllegalStateException when no start time has yet been documented
	 * @throws IllegalArgumentException when the start time of the task is after the time given
	 */
	public TimeSpan getTimeElapsed(LocalDateTime currentTime) {
		if(beginTime == null)
			throw new IllegalStateException("Project not yet started");
		if(beginTime.isAfter(currentTime))
			throw new IllegalArgumentException("Timestamp is in the past");

		return new TimeSpan(beginTime, currentTime).add(extraTime) ;
	}

	/**
	 * This method returns the time elapsed since the start of the project and 
	 * the end of the project. 
	 * 
	 * @return returns time elapsed between the start time and end time
	 * @throws IllegalStateException whenever the end time is not yet determined
	 */
	public TimeSpan getTimeSpan() {
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
	public TimeSpan getEstimatedDuration() {
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
	 * Set the status of this Task on finished.
	 * 
	 * @return	True is the operation was successful.
	 * 			False if the status was already failed or finished.
	 */
	public boolean setFinished(){
		if(this.taskStatus == TaskStatus.FAILED || this.taskStatus == TaskStatus.FINISHED)
			return false;
		this.taskStatus = TaskStatus.FINISHED;
		return true;
	}

	/**
	 * Set the status of this Task on failed.
	 * 
	 * @return	True is the operation was successful.
	 * 			False if the status was already failed or finished.
	 */
	public boolean setFailed(){
		if(this.taskStatus == TaskStatus.FAILED || this.taskStatus == TaskStatus.FINISHED)
			return false;
		this.taskStatus = TaskStatus.FAILED;
		return true;
	}

	/**
	 * Set the status of this Task on available.
	 * 
	 * @return	True is the operation was successful.
	 * 			False if the status was already failed or finished.
	 */
	public boolean setAvailable(){
		if(this.taskStatus == TaskStatus.FAILED || this.taskStatus == TaskStatus.FINISHED)
			return false;
		this.taskStatus = TaskStatus.AVAILABLE;
		return true;
	}

	/**
	 * Set the status of this Task on unavailable.
	 * 
	 * @return	True is the operation was successful.
	 * 			False if the status was already failed or finished.
	 */
	public boolean setUnavailable(){
		if(this.taskStatus == TaskStatus.FAILED || this.taskStatus == TaskStatus.FINISHED)
			return false;
		this.taskStatus = TaskStatus.UNAVAILABLE;
		return true;
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
	 * Returns the name of the current status of the Task
	 * @return	the name of the current status of the Task
	 */
	public String getTaskStatusName() {
		return getTaskStatus().name();
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
			status = "failed";
			break;
		case FINISHED:
			status = "finished";
			break;
		case AVAILABLE:
			status = "available";
			break;
		case UNAVAILABLE:
			status = "unavailable";
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
	 * End the task in a Finished state
	 * 
	 * @param 	startTime
	 * 			The new startTime of the Task.
	 * @param 	endTime
	 * 			The new end time of the Task.
	 * @return	True if and only if the updates succeeds.
	 */
	public boolean setTaskFinished(LocalDateTime startTime,
			LocalDateTime endTime) {
		return setTaskStatus(startTime,endTime,TaskStatus.FINISHED);
	}

	/**
	 * End the task in a Failed state
	 * 
	 * @param 	startTime
	 * 			The new startTime of the Task.
	 * @param 	endTime
	 * 			The new end time of the Task.
	 * @return	True if and only if the updates succeeds.
	 */
	public boolean setTaskFailed(LocalDateTime startTime,
			LocalDateTime endTime) {
		return setTaskStatus(startTime,endTime,TaskStatus.FAILED);
	}

	/**
	 * End the task in an endstate
	 * 
	 * @param 	startTime
	 * 			The new startTime of the Task.
	 * @param 	endTime
	 * 			The new end time of the Task.
	 * @param	status
	 * 			The new status of the Task.
	 * @return	True if and only if the updates succeeds.
	 */
	private boolean setTaskStatus(LocalDateTime startTime,
			LocalDateTime endTime, TaskStatus status) {
		if(hasEnded() || isUnavailable())
			return false;
		if(isValidTimeStamps(startTime, endTime)) {
			this.setBeginTime(startTime);
			this.setEndTime(endTime);
			taskStatus = status;
			return true;
		}
		return false;
	}
	
	/**
	 * Checks whether the given timestamps are valid
	 * 
	 * @param 	startTime
	 * 			The new startTime of the Task.
	 * @param 	endTime
	 * 			The new end time of the Task.
	 * @return	True if and only if the timestamps are valid.
	 */
	private boolean isValidTimeStamps(LocalDateTime startTime, LocalDateTime endTime) {
		if(startTime == null || endTime == null)
			return false;
		if(endTime.isBefore(startTime))
			return false;
		return true;
	}
	
	/**
	 * Checks whether the deviation is a valid one.
	 * 
	 * @param 	deviation
	 * 			The deviation to check.
	 * @return	True if deviation >= 0
	 */
	private boolean isValidDeviation(int deviation){
		return deviation>=0;
	}
	
	/**
	 * Checks whether the deviation is a valid one.
	 * 
	 * @param 	taskID
	 * 			The taskID to check.
	 * @return	True if taskID >= 0
	 */
	private boolean isValidTaskID(int taskID){
		return taskID>=0;
	}
	
	/**
	 * Checks whether the deviation is a valid one.
	 * 
	 * @param 	description
	 * 			The description to check.
	 * @return	True if description != null
	 */
	private boolean isValidDescription(String description){
		return description != null;
	}
	
	/**
	 * Checks whether the duration is a valid one.
	 * 
	 * @param 	duration
	 * 			The duration to check.
	 * @return	True if duration > 0
	 */
	private boolean isValidDuration(int duration){
		return duration>0;
	}
	
	/**
	 * Returns whether the current Task in on time.
	 * 
	 * @return	True if the Task is on time.
	 * 			False if the elapsed time is longer then the acceptable duration.
	 */
	public boolean isOnTime(){
		TimeSpan acceptableSpan = this.getEstimatedDuration();
		if(isFinished() || isFailed()){
			return this.getTimeElapsed(this.getEndTime()).isShorter(acceptableSpan);
		}
		else
			return true;
	}

	/**
	 * Returns whether the current task in unacceptable overdue.
	 * 
	 * @return	True if the project is overtime beyond the deviation.
	 * 			False otherwise.
	 */
	public boolean isUnacceptableOverdue() {
		TimeSpan acceptableSpan = this.getEstimatedDuration().getAcceptableSpan(this.getAcceptableDeviation());
		if(isFinished() || isFailed()){
			return this.getTimeElapsed(this.getEndTime()).isShorter(acceptableSpan);
		}
		else
			return false;
	}

	/**
	 * Get the percentage of the overdue.
	 * 
	 * @return	The percentage of overdue.
	 */
	public int getOverTimePercentage() {
		if(!isOnTime()){
			int overdue = this.getTimeSpan().getDifferenceMinute(estimatedDuration);
			return (overdue/this.estimatedDuration.getSpanMinutes())*100;
		}
		else
			return 0;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + taskID;
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
		Task other = (Task) obj;
		if (taskID != other.taskID)
			return false;
		return true;
	}
}
