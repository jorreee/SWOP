package taskMan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import taskMan.resource.ResourceManager;
import taskMan.state.TaskStatus;
import taskMan.state.UnavailableTask;
import taskMan.util.Dependant;
import taskMan.util.TimeSpan;
import taskMan.view.ResourceView;

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
//	private final TimeSpan estimatedDuration;
//	private final int acceptableDeviation;
	
//	private LocalDateTime beginTime;
	private Planning plan;
//	private LocalDateTime endTime;
	
	private final Task alternativeFor;
	private Task replacement;
	private ArrayList<Dependant> dependants;
	private ArrayList<Task> prerequisites;
//	private ArrayList<Task> unfinishedPrerequisites;

	private HashMap<ResourceView,Integer> requiredResources;
	
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
			ResourceManager resMan, 
			List<Task> prerequisiteTasks,
			Map<ResourceView, Integer> requiredResources, 
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
		if(!isValidResourceManager(resMan)) {
			throw new IllegalArgumentException("Invalid resource manager");
		}
		this.taskID = taskID;
		this.description = taskDescription;
		this.plan = new Planning(estimatedDuration, acceptableDeviation);
//		this.estimatedDuration = new TimeSpan(estimatedDuration);
//		this.acceptableDeviation = acceptableDeviation;
		this.resMan = resMan;
		
		this.state = new UnavailableTask(this);

		this.dependants = new ArrayList<Dependant>();
		this.prerequisites = new ArrayList<Task>();
//		this.unfinishedPrerequisites = new ArrayList<Task>();
		
		this.alternativeFor = alternativeFor;
		if(alternativeFor != null) {
			alternativeFor.replaceWith(this);
		}
		replacement = null;

		for(Task t : prerequisiteTasks) {
			t.register(this);
			this.prerequisites.add(t);
//			this.unfinishedPrerequisites.add(t);
		}
		removeAlternativesDependencies();

		state.makeAvailable();
//		state.makeAvailable(unfinishedPrerequisites);
		
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
			ResourceManager resMan, 
			List<Task> prerequisiteTasks,
			Map<ResourceView, Integer> requiredResources, 
			Task alternativeFor,
			String taskStatus,
			LocalDateTime beginTime, 
			LocalDateTime endTime) throws IllegalArgumentException {
		
		this(	taskID, 
				taskDescription, 
				estimatedDuration, 
				acceptableDeviation, 
				resMan, 
				prerequisiteTasks,
				requiredResources, 
				alternativeFor);
		
		if(taskStatus.equalsIgnoreCase("failed")) {
			plan.setBeginTime(beginTime);
			if(!state.fail(endTime)) {
				throw new IllegalArgumentException("Very bad timeStamps");
			}
		} else if(taskStatus.equalsIgnoreCase("finished")) {
			plan.setBeginTime(beginTime);
			if(!state.finish(endTime)) {
				throw new IllegalArgumentException("Very bad timeStamps");
			}
		} else {
			throw new IllegalArgumentException(
					"Time stamps are only allowed if a task is finished or failed");
		}
		
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

	//TODO met huidig ontwerp wordt dit nooit gebruikt. Kan 
	// gebruikt worden om dependencies van gefaalde tasks over 
	// te plaatsen wanneer die wordt vervangen.
//	public boolean unregister(Dependant t) {
//		if(!isValidDependant(t)) {
//			return false;
//		}
//		int depIndex = dependants.indexOf(t);
//		if(depIndex < 0) {
//			return false;
//		}
//		dependants.remove(depIndex);
//		return true;
//	}
	
	private boolean isValidDependant(Dependant t) {
		if(t == this) {
			return false;
		}
		if(t == null) {
			return false;
		}
		return true;
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
		int preIndex = prerequisites.indexOf(preTask);
//		int preIndex = unfinishedPrerequisites.indexOf(preTask);
		if(preIndex < 0) {
			return false;
		}
		prerequisites.remove(preIndex);
//		unfinishedPrerequisites.remove(preIndex);
		state.makeAvailable();
//		state.makeAvailable(unfinishedPrerequisites);
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
	
	public boolean hasFinishedEndpoint() {
		if(isFinished()) {
			return true;
		}
		if(isFailed()) {
			if(replacement != null) {
				return replacement.hasFinishedEndpoint();
			}
		}
		return false;
	}

	/**
	 * Checks whether the the Task has failed.
	 * 
	 * @return	True if and only the Task has failed.
	 */
	public boolean isFailed(){
		return state.isFailed();
	}
	
	private boolean canBeReplaced() {
		if(!isFailed()) {
			return false;
		}
		if(getReplacement() != null) {
			return false;
		}
		return true;
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
	 */ //TODO moet dit bestaan?
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
	 * Returns the time spent on the task. 
	 * If the task is still being worked on, this method will return the 
	 * time in working time elapsed between the start time and current time.
	 * If the task has concluded (hasEnded() = true), this method will return
	 * the time in working time elapsed between the start- and end time of
	 * the task.
	 * 
	 * @param 	currentTime 
	 * 			The current time
	 * @return	time spent on this task in working time
	 * @throws 	IllegalArgumentException 
	 * 			When the start time of the task is after the time given.
	 */ //TODO geeft TimeSpan object terug, ça marche?
	public TimeSpan getTimeSpent(LocalDateTime currentTime) {
		TimeSpan taskTimeSpent = plan.getTimeSpent(currentTime);
		if(alternativeFor == null) {
			return taskTimeSpent;
		}
		return taskTimeSpent.add(alternativeFor.getTimeSpent(currentTime));
	}

//	/**
//	 * Assumes the task is concluded (hasEnded() == true) and returns the time
//	 * in working minutes that was spent on this task.
//	 * 
//	 * @return 	time elapsed between the start time and end time of the task.
//	 * 			
//	 * @throws 	IllegalStateException 
//	 * 			whenever the end time is not yet determined
//	 */ //TODO moet eigenlijk niet bestaan
//	public TimeSpan getTimeSpent() {
//		return getTimeSpent(endTime);
//	}

	/**
	 * Returns the start time of the Task.
	 * 
	 * @return	The start time of the Task.
	 */
	public LocalDateTime getBeginTime() {
		return plan.getBeginTime();
	}

//	/**
//	 * Sets the start time of the Task.
//	 * 
//	 * @param 	beginTime
//	 * 			The new start time for the Task.
//	 * @throws	IllegalArgumentException
//	 * 			If the new begin time is null or the old begin time is already set. 
//	 */
//	public void setBeginTime(LocalDateTime beginTime) throws IllegalArgumentException{
//		plan.setBeginTime(beginTime);
//	}

	/**
	 * Returns the end time of the Task.
	 * 
	 * @return	The end time of the Task.
	 */
	public LocalDateTime getEndTime() {
		return plan.getEndTime();
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
//		if(endTime==null) {
//			throw new IllegalArgumentException("The new endTime is null");
//		}
//		if(getEndTime()!=null) {
//			throw new IllegalArgumentException("The endtime is already set");
//		}
//		this.endTime = endTime;
		plan.setEndTime(endTime);
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
		return plan.getEstimatedDuration();
	}

	/**
	 * Returns the acceptable deviation of the Task.
	 * 
	 * @return	The acceptable deviation of the Task.
	 */
	public int getAcceptableDeviation() {
		return plan.getAcceptableDeviation();
	}
	
	public Task getAlternativeFor() {
		return alternativeFor;
	}
	
	public Task getReplacement() {
		return replacement;
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
				System.out.println("dirty."); //TODO niet doen
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
	
	public List<Task> getPrerequisites() {
//		ArrayList<Task> allPrerequisites = new ArrayList<Task>();
//		for(Task t : prerequisites) {
//			allPrerequisites.add(t);
//			allPrerequisites.addAll(t.getTaskPrerequisites());
//		}
//		if(alternativeFor != null) {
//			allPrerequisites.add(alternativeFor);
//		}
//		return allPrerequisites
		return prerequisites;
	}
	
	public List<Dependant> getDependants() {
		return dependants;
	}

	/**
	 * Returns the status of the Task as a String.
	 * 
	 * @return	The status of the Task as a String.
	 */
	public String getStatus(){
		return state.toString();

	}

	/**
	 * Returns the TaskID of the this Task.
	 * 
	 * @return	The ID of this Task.
	 */
	public int getID() { 
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
	public boolean setFinished(LocalDateTime endTime) {
		
		return state.finish(endTime);
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
	public boolean setFailed(LocalDateTime endTime) {
		return state.fail(endTime);
	}
	
	public boolean replaceWith(Task t) {
		if(!canBeReplaced()) {
			return false;
		}
		this.replacement = t;
		return true;
	}

	public void setTaskStatus(TaskStatus newStatus) {
		this.state = newStatus;
	}
	
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
		if(!altTask.canBeReplaced()) {
			return false;
		}
		return true;
	}
	
	private boolean isValidResourceManager(ResourceManager resMan) {
		if(resMan == null) {
			return false;
		}
		return true;
	}
	
//	/**
//	 * Returns whether the current Task is on time, depending on the estimated duration
//	 * 
//	 * @return	True if the Task is on time.
//	 * 			False if the current date false after beginTime + EstimatedDur (in working minutes)
//	 */
//	public boolean isOnTime(LocalDateTime currentTime){
//		if(beginTime == null) {
//			return false;
//		}
//		TimeSpan acceptableSpan = getEstimatedDuration();
//		LocalDateTime acceptableEndDate = TimeSpan.addSpanToLDT(beginTime, acceptableSpan);
//		
//		if(hasEnded()) {
//			return !endTime.isAfter(acceptableEndDate);
//		}
//		
//		return !currentTime.isAfter(acceptableEndDate);
//	}
//
//	/**
//	 * Returns whether the current is unacceptably overdue, depending on the estimated
//	 * duration and acceptable deviation of the task.
//	 * 
//	 * @return	True if the project is overtime beyond the deviation.
//	 * 			False otherwise.
//	 */
//	public boolean isUnacceptableOverdue(LocalDateTime currentTime) {
//		if(beginTime == null) {
//			return true;
//		}
//		TimeSpan acceptableSpan = estimatedDuration.getAcceptableSpan(getAcceptableDeviation());
//		LocalDateTime acceptableEndDate = TimeSpan.addSpanToLDT(beginTime, acceptableSpan);
//		
//		if(hasEnded()) {
//			return endTime.isAfter(acceptableEndDate);
//		}
//		
//		return currentTime.isAfter(acceptableEndDate);
//	}
//
//	/**
//	 * Returns the percentage of overdueness of the task, depending on the 
//	 * estimated duration of the task. Returns 0 if the task
//	 * is well on time.
//	 * 
//	 * @return	The percentage of overdue.
//	 */
//	public int getOverTimePercentage(LocalDateTime currentTime) {
//		if(isOnTime(currentTime)) {
//			return 0;
//		}
//		int overdue = getTimeSpent(currentTime).getDifferenceMinute(estimatedDuration);
//		return ( overdue / estimatedDuration.getSpanMinutes() ) * 100;
//	}
	
	public boolean plan(LocalDateTime startTime) {
//		if(getBeginTime() != null) {
//			return false;
//		}
		plan.setBeginTime(startTime);
		return true;
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
	
	public HashMap<ResourceView,Integer> getRequiredResources(){
		return requiredResources;
	}
	
	public ImmutableList<ResourceView> getPossibleResourceInstances(ResourceView resourceType){
		return resMan.getPossibleResourceInstances(resourceType);
	}
	
}