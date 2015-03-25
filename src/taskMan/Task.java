package taskMan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import taskMan.util.DependentTask;
import taskMan.util.PrerequisiteTask;
import taskMan.util.TimeSpan;

/**
 * The Task object. A task will have an ID, a description, an estimated duration, 
 * acceptable deviation and a status. Also it will be possible to set the begin and end time
 * once the project is finished or failed. There is also the extra time that is determined by
 * the alternative.
 * 
 * @author	Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel, Eli Vangrieken
 *
 */
public class Task implements DependentTask, PrerequisiteTask {

	private final int taskID;
	private final String description;
	private final TimeSpan estimatedDuration;
	private final int acceptableDeviation;
	
	private LocalDateTime beginTime;
	private LocalDateTime endTime;

	private TaskStatus taskStatus;
	
	private final Task alternativeFor; //TODO mag maar één keer worden geSet
	private ArrayList<DependentTask> dependants;
	private int numberOfPrerequisites;
	
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
	 * 			The extra time offset to add to the elapsed time
	 * @throws	IllegalArgumentException
	 * 			if any of the parameters are invalid ( < 0 or null)
	 */
	public Task(int taskID, 
			String taskDescription, 
			int estimatedDuration,
			int acceptableDeviation, 
			List<Task> prerequisiteTasks,
			Task alternativeFor) throws IllegalArgumentException {
		if(!isValidTaskID(taskID)) {
			throw new IllegalArgumentException("Invalid task ID");
		}
		if(!isValidDescription(taskDescription)) {
			throw new IllegalArgumentException("Invalid description");
		}
		if(!isValidDuration(estimatedDuration)) {
			throw new IllegalArgumentException("Invalid duration");
		}
		if(!isValidDeviation(acceptableDeviation)) {
			throw new IllegalArgumentException("Invalid deviation");
		}
		if(!isValidAlternative(alternativeFor)) {
			throw new IllegalArgumentException("Invalid replacement");
		}
		if(!isValidPrerequisites(prerequisiteTasks)) {
			throw new IllegalArgumentException("Invalid prerequisites");
		}
		this.taskID = taskID;
		this.description = taskDescription;
		this.estimatedDuration = new TimeSpan(estimatedDuration);
		this.acceptableDeviation = acceptableDeviation;
//		this.extraTime = extraTime;
		this.alternativeFor = alternativeFor;
		for(Task t : prerequisiteTasks) {
			 t.register(this);
		}
		this.numberOfPrerequisites = 0; //TODO change!
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
	 * 			The end time of the new Task.
	 * @throws	IllegalArgumentException
	 * 			if any of the parameters are invalid ( < 0 or null)
	 */
	public Task(int taskID, 
			String taskDescription, 
			int estimatedDuration,
			int acceptableDeviation, 
			List<Task> prerequisiteTasks,
			Task alternativeFor,
			String taskStatus,
			LocalDateTime beginTime, 
			LocalDateTime endTime) throws IllegalArgumentException {
		
		this(	taskID, 
				taskDescription, 
				estimatedDuration, 
				acceptableDeviation, 
				prerequisiteTasks,
				alternativeFor);
		
		if(!taskStatus.equalsIgnoreCase("failed") && !taskStatus.equalsIgnoreCase("finished")) {
			throw new IllegalArgumentException(
					"Time stamps are only allowed if a task is finished or failed");
		}
		this.taskStatus = TaskStatus.valueOf(taskStatus); //TODO STATE
		if(!isValidTimeStamps(beginTime, endTime)) {
			throw new IllegalArgumentException("Very bad timestamps");
		}
		this.beginTime = beginTime;
		this.endTime = endTime;
	}

	@Override
	public boolean register(DependentTask t) {
		return dependants.add(t);
	}

	@Override
	public boolean unregister(DependentTask t) {
		if(!isValidDependant(t)) {
			return false;
		}
		int depIndex = dependants.indexOf(t);
		if(depIndex < 0) {
			return false;
		}
		dependants.remove(depIndex);
		return true;
	}
	
	private boolean isValidDependant(DependentTask t) {
		return t != this;
	}

	@Override
	public boolean notifyDependants() {
		// TODO Check op validity?
		boolean goingGood = true;
		for(DependentTask t : dependants) {
			goingGood = goingGood && t.update();
		}
		return goingGood;
	}

	// TODO als failt, nog GEEN update. Als vervanger Finisht, laat dan de 
	// vervangen task updaten.
	@Override
	public boolean update() {
		numberOfPrerequisites--;
		//TODO als == 0, set available
		return true;
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

	/**
	 * checks whether the Task has ended.
	 * 
	 * @return	True if and only if the Task has ended.
	 */
	public boolean hasEnded(){
		return (isFinished() || isFailed());
	}

	/**
	 * Compares the start time of the project with a given time and calculates the
	 * elapsed time in working time.
	 * 
	 * @param 	currentTime 
	 * 			The time with which the start time of the task must be compared with.
	 * @return	time spent on this project in working time
	 * @throws 	IllegalArgumentException 
	 * 			When the start time of the task is after the time given.
	 */
	public TimeSpan getTimeElapsed(LocalDateTime currentTime) {
//		if(beginTime == null) { //TODO geen argument meer ?
//			throw new IllegalArgumentException("Project not yet started");
//		}
//		if(beginTime.isAfter(currentTime)) {
//			throw new IllegalArgumentException("Timestamp is in the past");
//		}
		TimeSpan currentTimeSpent = TimeSpan.getDifferenceWorkingMinutes(
				this.beginTime, 
				currentTime); //TODO kan ook variabele in Task zijn?
		if(alternativeFor != null) {
			currentTimeSpent = currentTimeSpent
								.add(alternativeFor.getTimeElapsed(currentTime));
		}
		return currentTimeSpent;
	}

//	/**
//	 * Returns the time elapsed since the start of the project and 
//	 * the end of the project. 
//	 * 
//	 * @return 	time elapsed between the start time and end time
//	 * @throws 	IllegalStateException 
//	 * 			whenever the end time is not yet determined
//	 */
//	public TimeSpan getTimeSpent() {
//		return getTimeElapsed(endTime);
//	}

	/**
	 * Returns the start time of the Task.
	 * 
	 * @return	The start time of the Task.
	 */
	public LocalDateTime getBeginTime() {
		return beginTime;
	}

	/**
	 * Sets the start time of the Task.
	 * 
	 * @param 	beginTime
	 * 			The new start time for the Task.
	 * @throws	IllegalArgumentException
	 * 			If the new begin time is null or the old begin time is already set. 
	 */
	public void setBeginTime(LocalDateTime beginTime) throws IllegalArgumentException{
		if(beginTime==null) {
			throw new IllegalArgumentException("The new beginTime is null");
		}
		if(this.beginTime!=null) {
			throw new IllegalArgumentException("The beginTime is already set");
		}
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

//	/**
//	 * Sets the end time of Task.
//	 * 
//	 * @param 	endTime
//	 * 			The new end time of the Task.
//	 * @throws	IllegalArgumentException
//	 * 			If the new end time is null or the old end time is already set. 
//	 */
//	public void setEndTime(LocalDateTime endTime) throws IllegalArgumentException {
//		if(endTime==null) {
//			throw new IllegalArgumentException("The new endTime is null");
//		}
//		if(getEndTime()!=null) {
//			throw new IllegalArgumentException("The endtime is already set");
//		}
//		this.endTime = endTime;
//	}

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
	 * @return	The acceptable deviation of the Task.
	 */
	public int getAcceptableDeviation() {
		return acceptableDeviation;
	}
	
	public Task getAlternativeFor() {
		return alternativeFor;
	}
	
	public ArrayList<Task> getTaskPrerequisites() {
		return null; //TODO change!
	}

	public TimeSpan getMaxDelayChain() {
		//TODO te doen
		return null;
	}
	
//	/**
//	 * Set the status of this Task on available.
//	 * 
//	 * @return	True is the operation was successful.
//	 * 			False if the status was already failed or finished.
//	 */
//	public boolean setAvailable(){
//		if(this.taskStatus == TaskStatus.FAILED || this.taskStatus == TaskStatus.FINISHED) {
//			return false;
//		}
//		this.taskStatus = TaskStatus.AVAILABLE;
//		return true;
//	}

//	/**
//	 * Set the status of this Task on unavailable.
//	 * 
//	 * @return	True is the operation was successful.
//	 * 			False if the status was already failed or finished.
//	 */
//	public boolean setUnavailable(){
//		if(this.taskStatus == TaskStatus.FAILED || this.taskStatus == TaskStatus.FINISHED) {
//			return false;
//		}
//		this.taskStatus = TaskStatus.UNAVAILABLE;
//		return true;
//	}

//	/**
//	 * Returns the current status of the Task.
//	 * 
//	 * @return	The current status of the Task.
//	 */
//	private TaskStatus getTaskStatus() {
//		return taskStatus;
//	}

	/**
	 * Returns the status of the Task as a String.
	 * 
	 * @return	The status of the Task as a String.
	 */
	public String getStatus(){
		//TODO state.asString(); hehe ass
		return "werk is";
//		TaskStatus stat = this.getTaskStatus();
//		String status ="";
//		switch(stat){
//		case FAILED:
//			status = "failed";
//			break;
//		case FINISHED:
//			status = "finished";
//			break;
//		case AVAILABLE:
//			status = "available";
//			break;
//		case UNAVAILABLE:
//			status = "unavailable";
//			break;
//		default:
//			status ="ERROR";
//			break;
//		}
//		return status;

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
	 * @param 	beginTime
	 * 			The new begin time of the Task.
	 * @param 	endTime
	 * 			The new end time of the Task.
	 * @return	True if and only if the updates succeeds.
	 */
	public boolean setTaskFinished(LocalDateTime beginTime,
			LocalDateTime endTime) {
		//TODO return state.finish();
		return setTaskStatus(beginTime,endTime,TaskStatus.FINISHED);
	}

	/**
	 * End the task in a Failed state
	 * 
	 * @param 	beginTime
	 * 			The new begin time of the Task.
	 * @param 	endTime
	 * 			The new end time of the Task.
	 * @return	True if and only if the updates succeeds.
	 */
	public boolean setTaskFailed(LocalDateTime beginTime, LocalDateTime endTime) {
		//TODO return state.fail();
		return setTaskStatus(beginTime,endTime,TaskStatus.FAILED);
	}

	/**
	 * End the task finished or failed
	 * 
	 * @param 	beginTime
	 * 			The new begin time of the Task.
	 * @param 	endTime
	 * 			The new end time of the Task.
	 * @param	status
	 * 			The new status of the Task.
	 * @return	True if and only if the updates succeeds.
	 */
	private boolean setTaskStatus(LocalDateTime beginTime,
			LocalDateTime endTime, 
			TaskStatus status) {
		
//		if(hasEnded() || isUnavailable()) {
//			return false;
//		}//TODO STATE laten afhandelen
		if(isValidTimeStamps(beginTime, endTime)) {
			this.beginTime = beginTime;
			this.endTime = endTime;
//			taskStatus = status;
			return true;
		}
		return false;
	}
	
	/**
	 * Checks whether the given timestamps are valid as start- and endtimes
	 * 
	 * @param 	beginTime
	 * 			The new begin time of the Task.
	 * @param 	endTime
	 * 			The new end time of the Task.
	 * @return	True if and only if the timestamps are valid start- and endtimes.
	 */
	private boolean isValidTimeStamps(LocalDateTime beginTime, LocalDateTime endTime) {
		if(beginTime == null || endTime == null) {
			return false;
		}
		if(endTime.isBefore(beginTime)) {
			return false;
		}
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
		return deviation >= 0;
	}
	
	/**
	 * Checks whether the taskID is a valid one.
	 * 
	 * @param 	taskID
	 * 			The taskID to check.
	 * @return	True if taskID >= 0
	 */
	private boolean isValidTaskID(int taskID){
		return taskID >= 0;
	}
	
	/**
	 * Checks whether the description is a valid one.
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
		return duration > 0;
	}

	/**
	 * Checks whether the given prerequisites are valid for the given Task.
	 * 
	 * @param 	task
	 * 			The given Task.
	 * @param 	prerequisites
	 * 			The prerequisites to check.
	 * @return	True if and only the prerequisites are a valid.
	 * 			True if the prerequisites are empty
	 * 			False if the prerequisites are null
	 * 			False if the task ID isn't a valid one
	 */
	private boolean isValidPrerequisites(List<Task> prerequisites){
		if (prerequisites == null) {
			return false;
		}
		else if(prerequisites.contains(this)) {
			return false;
		}
		return true;
	}
	
	//TODO wa is er nog invalid alt task?
	private boolean isValidAlternative(Task altTask) {
		if(altTask.equals(this)) {
			return false;
		}
		if(!altTask.isFailed()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns whether the current Task in on time.
	 * 
	 * @return	True if the Task is on time.
	 * 			False if the elapsed time is longer then the acceptable duration.
	 */
	public boolean isOnTime(){
		TimeSpan acceptableSpan = this.getEstimatedDuration();
		if(isFinished() || isFailed()) {
			return this.getTimeElapsed(this.getEndTime()).isShorter(acceptableSpan);
		}
		return true;
	}

	/**
	 * Returns whether the current task in unacceptably overdue.
	 * 
	 * @return	True if the project is overtime beyond the deviation.
	 * 			False otherwise.
	 */
	public boolean isUnacceptableOverdue() {
		TimeSpan acceptableSpan = this.getEstimatedDuration().getAcceptableSpan(this.getAcceptableDeviation());
		if(isFinished() || isFailed()) {
			return this.getTimeElapsed(this.getEndTime()).isLonger(acceptableSpan);
		}
		return false;
	}

	/**
	 * Get the percentage of the overdue.
	 * 
	 * @return	The percentage of overdue.
	 */
	//TODO hoe?
	public int getOverTimePercentage() {
		if(!isOnTime()){
			int overdue = 0; // this.getTimeSpent().getDifferenceMinute(estimatedDuration);
			return (overdue/this.estimatedDuration.getSpanMinutes())*100;
		}
		return 0;
	}
}
