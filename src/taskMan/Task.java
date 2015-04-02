package taskMan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import taskMan.resource.ResourceManager;
import taskMan.state.TaskStatus;
import taskMan.state.UnavailableTask;
import taskMan.util.Dependant;
import taskMan.util.TimeSpan;

import com.google.common.collect.ImmutableList;

/**
 * The Task object. A task will have an ID, a description, an estimated duration, 
 * acceptable deviation and a status. Also it will be possible to set the begin and end time
 * once the project is finished or failed. There is also the extra time that is determined by
 * the alternative.
 * 
 * @author	Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel, Eli Vangrieken
 *
 */
public class Task implements Dependant {

	private final int taskID;
	private final String description;
	private final TimeSpan estimatedDuration;
	private final int acceptableDeviation;
	
	private LocalDateTime beginTime;
	private LocalDateTime endTime;
	
	private final Task alternativeFor;
	private ArrayList<Dependant> dependants;
	private ArrayList<Task> prerequisites;
	private ArrayList<Task> unfinishedPrerequisites;
	
	private TaskStatus state;
	
	private final ResourceManager resMan;
	
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
		if(prerequisiteTasks.contains(alternativeFor)) {
			throw new IllegalArgumentException("Alt can't be a prerequisite");
		}
		this.taskID = taskID;
		this.description = taskDescription;
		this.estimatedDuration = new TimeSpan(estimatedDuration);
		this.acceptableDeviation = acceptableDeviation;
//		this.extraTime = extraTime;
		
		this.state = new UnavailableTask(this);

		this.dependants = new ArrayList<Dependant>();
		this.prerequisites = new ArrayList<Task>();
		this.unfinishedPrerequisites = new ArrayList<Task>();
		
		this.alternativeFor = alternativeFor;

		for(Task t : prerequisiteTasks) {
			t.register(this);
			this.prerequisites.add(t);
			this.unfinishedPrerequisites.add(t);
		}
		removeAlternativesDependencies();

		state.makeAvailable(unfinishedPrerequisites);
		
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
		
		if(taskStatus.equalsIgnoreCase("failed")) {
			state.fail(beginTime, endTime);
		} else if(taskStatus.equalsIgnoreCase("finished")) {
			state.finish(beginTime, endTime);
		} else {
			throw new IllegalArgumentException(
					"Time stamps are only allowed if a task is finished or failed");
		}
//		this.state = TaskStatus.valueOf(taskStatus);
//		if(!isValidTimeStamps(beginTime, endTime)) {
//			throw new IllegalArgumentException("Very bad timestamps");
//		}
//		this.beginTime = beginTime;
//		this.endTime = endTime;
	}

	public boolean register(Dependant t) {
		if(!isValidDependant(t)) {
			return false;
		}
		return state.register(t);
	}
	
	public void addDependant(Dependant d) {
		dependants.add(d);
	}

	public boolean unregister(Dependant t) {
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
	
	private boolean isValidDependant(Dependant t) {
		return t != this;
	}

	public boolean notifyDependants() {
		for(Dependant t : dependants) {
			t.updateDependency(this);
		}
		if(alternativeFor != null) {
			alternativeFor.notifyDependants();
		}
		return true;
	}

	@Override
	public boolean updateDependency(Task preTask) {
		int preIndex = unfinishedPrerequisites.indexOf(preTask);
		if(preIndex < 0) {
			return false;
		}
		unfinishedPrerequisites.remove(preIndex);
		state.makeAvailable(unfinishedPrerequisites);
		return true;
	}
	
	public void removeAlternativesDependencies() {
		if(alternativeFor != null) {
			int depIndex;
			for(Dependant d : alternativeFor.getDependants()) {
				depIndex = dependants.indexOf(d);
				if(depIndex >= 0) {
					dependants.remove(depIndex);
				}
			}
		}
	}

	/**
	 * Checks whether the Task is finished.
	 * 
	 * @return	True if and only if the Task has a finished status.
	 */
	public boolean isFinished() {
		return state.isFinished();
	}

	/**
	 * Checks whether the the Task has failed.
	 * 
	 * @return	True if and only the Task has failed.
	 */
	public boolean isFailed(){
		return state.isFailed();
	}

	/**
	 * Checks whether the Task is available.
	 * 
	 * @return	True if and only the Task is available.
	 */
	public boolean isAvailable(){
		return state.isAvailable();
	}

	/**
	 * Checks whether the Task is unavailable.
	 * 
	 * @return	True if and only the Task is unavailable.
	 */
	public boolean isUnavailable(){
		return state.isUnavailable();
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
	public TimeSpan getTimeSpent(LocalDateTime currentTime) {
//		if(beginTime == null) { 
//			throw new IllegalArgumentException("Project not yet started");
//		}
//		if(beginTime.isAfter(currentTime)) {
//			throw new IllegalArgumentException("Timestamp is in the past");
//		}
		int currentTimeSpent = TimeSpan.getDifferenceWorkingMinutes(
				this.beginTime, 
				currentTime);
		if(alternativeFor != null) {
			currentTimeSpent = currentTimeSpent
								 + alternativeFor.getTimeSpent().getSpanMinutes();
		}
		return new TimeSpan(currentTimeSpent);
	}

	/**
	 * Returns the time elapsed since the start of the project and 
	 * the end of the project. 
	 * 
	 * @return 	time elapsed between the start time and end time
	 * @throws 	IllegalStateException 
	 * 			whenever the end time is not yet determined
	 */
	public TimeSpan getTimeSpent() {
		return getTimeSpent(endTime);
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
		if(getBeginTime()!=null) {
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

	/**
	 * Sets the end time of Task.
	 * 
	 * @param 	endTime
	 * 			The new end time of the Task.
	 * @throws	IllegalArgumentException
	 * 			If the new end time is null or the old end time is already set. 
	 */
	public void setEndTime(LocalDateTime endTime) throws IllegalArgumentException {
		if(endTime==null) {
			throw new IllegalArgumentException("The new endTime is null");
		}
		if(getEndTime()!=null) {
			throw new IllegalArgumentException("The endtime is already set");
		}
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
	 * @return	The acceptable deviation of the Task.
	 */
	public int getAcceptableDeviation() {
		return acceptableDeviation;
	}
	
	public Task getAlternativeFor() {
		return alternativeFor;
	}

	public TimeSpan getMaxDelayChain() {
		TimeSpan longest = getEstimatedDuration();
		TimeSpan candidate;
		for(Dependant d : dependants) {
			try {
				candidate = getEstimatedDuration().add(((Task) d).getMaxDelayChain());
				if(candidate.isLonger(longest)) {
					longest = candidate;
				}
			} catch(ClassCastException e) {
				// project is ook Dependant maar moet geen maxdelaychain kunnen geven. Lege methode maybe, maar das ook dirty
				System.out.println("dirty."); //TODO dependant moet altijd kunnen maxDelayChain geven?
			}
		}
		if(alternativeFor != null) {
			candidate = alternativeFor.getMaxDelayChain();
			if(candidate.isLonger(longest)) {
				longest = candidate;
			}
		}
		return longest;
	}
	
	public List<Task> getTaskPrerequisites() {
//		ArrayList<Task> allPrerequisites = new ArrayList<Task>();
//		for(Task t : prerequisites) {
//			allPrerequisites.add(t);
//			allPrerequisites.addAll(t.getTaskPrerequisites());
//		}
////		if(alternativeFor != null) {
////			allPrerequisites.add(alternativeFor);
////		}
//		return allPrerequisites
		return prerequisites;
	}
	
	public List<Dependant> getDependants() {
		return dependants;
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
		return state.toString();
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
		
		return state.finish(beginTime, endTime);
//		if(state.finish(beginTime, endTime)) {
//			
//			setBeginTime(beginTime);
//			setEndTime(endTime);
//			setFinished();
//			
//			notifyDependants();
//			
//			return true;
//			
//		}
//		return false;
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
	public boolean setTaskFailed(LocalDateTime beginTime,
			LocalDateTime endTime) {
		return state.fail(beginTime, endTime);
//		if(state.finish(beginTime, endTime)) {
//			setBeginTime(beginTime);
//			setEndTime(endTime);
//			setFailed();
//			return true;
//		}
//		return false;
	}

	public void setTaskStatus(TaskStatus newStatus) {
		this.state = newStatus;
	}

//	/**
//	 * End the task finished or failed
//	 * 
//	 * @param 	beginTime
//	 * 			The new begin time of the Task.
//	 * @param 	endTime
//	 * 			The new end time of the Task.
//	 * @param	status
//	 * 			The new status of the Task.
//	 * @return	True if and only if the updates succeeds.
//	 */
//	private boolean setTaskStatus(LocalDateTime beginTime,
//			LocalDateTime endTime, 
//			TaskStatus status) {
//		
////		if(hasEnded() || isUnavailable()) {
////			return false;
////		}
//		if(isValidTimeStamps(beginTime, endTime)) {
//			this.beginTime = beginTime;
//			this.endTime = endTime;
////			taskStatus = status;
//			return true;
//		}
//		return false;
//	}
	
	/**
	 * Checks whether the deviation is a valid one.
	 * 
	 * @param 	deviation
	 * 			The deviation to check.
	 * @return	True if deviation >= 0
	 */
	private boolean isValidDeviation(int deviation) {
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
		if(prerequisites.contains(null)) {
			return false;
		}
		if(prerequisites.contains(this)) {
			return false;
		}
		return true;
	}
	
	private boolean isValidAlternative(Task altTask) {
		if(altTask == null) {
			return true;
		}
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
	public boolean isOnTime(LocalDateTime currentTime){
		if(beginTime == null) {
			return false;
		}
		TimeSpan acceptableSpan = getEstimatedDuration();
		LocalDateTime acceptableEndDate = TimeSpan.addSpanToLDT(beginTime, acceptableSpan);
		
		if(hasEnded()) {
			return !endTime.isAfter(acceptableEndDate);
		}
		
		return !currentTime.isAfter(acceptableEndDate);
	}

	/**
	 * Returns whether the current task in unacceptably overdue.
	 * 
	 * @return	True if the project is overtime beyond the deviation.
	 * 			False otherwise.
	 */
	public boolean isUnacceptableOverdue(LocalDateTime currentTime) {
		// interpretatie: currentTime - beginTime is de tijd die erin is gestoken so far
		if(beginTime == null) {
			return true;
		}
		TimeSpan acceptableSpan = estimatedDuration.getAcceptableSpan(getAcceptableDeviation());
		LocalDateTime acceptableEndDate = TimeSpan.addSpanToLDT(beginTime, acceptableSpan);
		
		if(hasEnded()) {
			return endTime.isAfter(acceptableEndDate);
		}
		
		return currentTime.isAfter(acceptableEndDate);
	}

	/**
	 * Get the percentage of the overdue.
	 * 
	 * @return	The percentage of overdue.
	 */
	//TODO hoe?
	public int getOverTimePercentage(LocalDateTime currentTime) {
		if(!isOnTime(currentTime)){
			int overdue = 0; // this.getTimeSpent().getDifferenceMinute(estimatedDuration);
			return (overdue/this.estimatedDuration.getSpanMinutes())*100;
		}
		return 0;
	}
	
	/**
	 * Returns an amount of possible Task starting times.
	 * 
	 * @param 	amount
	 * 			The amount of possible starting times wanted.
	 * @return	The possible starting times of the Task
	 */
	public ImmutableList<LocalDateTime> getPossibleTaskStartingTimes(int amount){
		return null; //TODO
	}
	
}