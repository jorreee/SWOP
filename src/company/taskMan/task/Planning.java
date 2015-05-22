package company.taskMan.task;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import company.taskMan.resource.user.User;
import company.taskMan.util.TimeSpan;

/**
 * A planning contains data essential for the execution of a task. Every task
 * will have an instance of Planning. In this planning everything related to
 * time will be kept (The planned and actual begin time, the end time, the
 * estimated duration and acceptable deviation to this duration). If a task is
 * unplanned, its planning instance will not contain a planned begin time.
 * Obviously it will also lack an actual begin time and end time, since a task
 * cannot begin without being planned first. When a task is planned a list of
 * developers will also be added to the planning. These developers are the
 * developers assigned to the task and are responsible for finishing or failing
 * the task.
 * 
 * @author Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class Planning {

	private LocalDateTime plannedBeginTime;
	private LocalDateTime beginTime;
	private LocalDateTime endTime;
	private final TimeSpan estimatedDuration;
	private final int acceptableDeviation;
	
	private List<User> plannedDevelopers = new ArrayList<User>();
	
	/**
	 * Construct a new planning instance for a task with an estimated duration
	 * and an acceptable deviation to this duration. A new planning instance has
	 * not yet been "planned".
	 * 
	 * @param estimatedDuration
	 *            | The estimated duration of the task
	 * @param acceptableDeviation
	 *            | The acceptable deviation (a percentage) that the actual
	 *            task's duration is allowed to deviate from the planned one
	 */
	public Planning(int estimatedDuration, int acceptableDeviation) {
		this.estimatedDuration = new TimeSpan(estimatedDuration);
		this.acceptableDeviation = acceptableDeviation;
		
	}
	
	/**
	 * Get the actual begin time of the task
	 * 
	 * @return the timestamp when the task first went into the executing state
	 */
	public LocalDateTime getBeginTime() {
		return beginTime;
	}
	
	/**
	 * Get the begin time when the task was meant (planned) to actually begin
	 * 
	 * @return the timestamp the project manager gave to this task when planning
	 *         it
	 */
	public LocalDateTime getPlannedBeginTime() {
		return plannedBeginTime;
	}
	
	/**
	 * Set the planned begin time to a given timestamp
	 * 
	 * @param newPlannedBeginTime
	 *            | The timestamp when the task is meant (planned) to actually
	 *            begin
	 * @throws IllegalArgumentException, IllegalStateException 
	 */
	public void setPlannedBeginTime(LocalDateTime newPlannedBeginTime) 
			throws IllegalArgumentException, IllegalStateException {
		if(newPlannedBeginTime==null) {
			throw new IllegalArgumentException("newPlannedBeginTime can not be null");
		}
		if(beginTime != null) {
			throw new IllegalStateException("There is already a begin time set");
		}
		this.plannedBeginTime = newPlannedBeginTime;
	}
	
	/**
	 * Set the actual begin time of the task. This can happen only once.
	 * 
	 * @param beginTime
	 *            | The actual begin time of the task
	 * @throws IllegalArgumentException
	 * 			| if beginTime == null
	 * @throws IllegalStateException
	 * 			| if the begin time was already set 
	 * 			| if there was no planned begin time set
	 */
	public void setBeginTime(LocalDateTime beginTime) 
			throws IllegalArgumentException, IllegalStateException {
		if(beginTime==null) {
			throw new IllegalArgumentException("beginTime must not be null");
		}
		if(getBeginTime() != null) {
			throw new IllegalStateException("Begin time already set");
		}
		if(getPlannedBeginTime() == null) {
			throw new IllegalStateException("Task doesn't have a planned start time yet");
		}
		this.beginTime = beginTime;
	}
	
	/**
	 * Retrieve the time when the task finished (or failed)
	 * 
	 * @return the time the task ended
	 */
	public LocalDateTime getEndTime() {
		return endTime;
	}
	
	/**
	 * Set the end time to a given timestamp. Can only be set once.
	 * 
	 * @param endTime
	 *            | The new end time
	 * @throws IllegalArgumentException
	 * 			| if endTime == null
	 * @throws IllegalStateException 
	 * 			| if there is no begin time set
	 * 			| if the end time is already set
	 * 
	 */
	public void setEndTime(LocalDateTime endTime) 
			throws IllegalArgumentException, IllegalStateException {
		if(endTime == null) {
			throw new IllegalArgumentException("endTime can not be null."); 
		}
//		if(getBeginTime() == null) {
//			throw new IllegalStateException("There isn't even a begin time yet!");
//		}
		if(getEndTime() != null) {
			throw new IllegalStateException("There is already an endTime set.");
		}
		this.endTime = endTime;
		
	}
	
	/**
	 * Get the estimated duration of the task, defined at task creation
	 * 
	 * @return the task's estimated duration
	 */
	public TimeSpan getEstimatedDuration() {
		return estimatedDuration;
	}
	
	/**
	 * Get the acceptable deviation to the estimated duration of the task,
	 * defined at task creation
	 * 
	 * @return the task's acceptable deviation to the estimated duration
	 */
	public int getAcceptableDeviation() {
		return acceptableDeviation;
	}
	
	/**
	 * Returns the time spent on the task. If the task is still being worked on,
	 * this method will return the time in working time elapsed between the
	 * start time and current time. If the task has concluded (hasEnded() =
	 * true), this method will return the time in working time elapsed between
	 * the start- and end time of the task.
	 * 
	 * @param currentTime
	 *            | The current time
	 * @param availabilityStart
	 *            | A timestamp to indicate the presence of an availability
	 *            period. Time calculations will be limited by this timestamp
	 * @param availabilityEnd
	 *            | A timestamp to indicate the presence of an availability
	 *            period. Time calculations will be limited by this timestamp
	 * @return time spent on this task in working time
	 * @throws IllegalArgumentException
	 *             When the start time of the task is after the time given.
	 */
	public TimeSpan getTimeSpent(LocalDateTime currentTime,
			LocalTime availabilityStart, LocalTime availabilityEnd) {
		if(currentTime == null) {
			return new TimeSpan(0);
		}
		if(beginTime == null) {
			return new TimeSpan(0);
		}
		if(beginTime.isAfter(currentTime)) {
			return new TimeSpan(0);
		}
		if(endTime != null) {
			return new TimeSpan(TimeSpan.getDifferenceWorkingMinutes(beginTime, endTime, availabilityStart, availabilityEnd));
		}
		
		int currentTimeSpent = TimeSpan.getDifferenceWorkingMinutes(
				beginTime, 
				currentTime, null, null);
		
		return new TimeSpan(currentTimeSpent);
		
	}
	
	/**
	 * Based on the planned begin time and the estimated duration, this method
	 * will calculate the timestamp when this task is planned to end
	 * 
	 * @param availabilityStart
	 *            | A timestamp to indicate the presence of an availability
	 *            period. Time calculations will be limited by this timestamp
	 * @param availabilityEnd
	 *            | A timestamp to indicate the presence of an availability
	 *            period. Time calculations will be limited by this timestamp
	 * @return the planned end time
	 */
	public LocalDateTime getPlannedEndTime(LocalTime availabilityStart, LocalTime availabilityEnd) {
		if(plannedBeginTime == null) {
			return null;
		}
		if(beginTime == null) {
			return TimeSpan.addSpanToLDT(plannedBeginTime, estimatedDuration, availabilityStart, availabilityEnd);
		}
		return TimeSpan.addSpanToLDT(beginTime, estimatedDuration, availabilityStart, availabilityEnd);
	}
	
	/**
	 * A method to calculate the last possible timestamp when the task should
	 * still be considered "on time"
	 * 
	 * @param availabilityStart
	 *            | A timestamp to indicate the presence of an availability
	 *            period. Time calculations will be limited by this timestamp
	 * @param availabilityEnd
	 *            | A timestamp to indicate the presence of an availability
	 *            period. Time calculations will be limited by this timestamp
	 * @return the last possible acceptable end time
	 */
	public LocalDateTime getAcceptableEndTime(LocalTime availabilityStart, LocalTime availabilityEnd) {
		if(getBeginTime() == null) {
			return null;
		}
		return TimeSpan.addSpanToLDT(beginTime, estimatedDuration.getAcceptableSpan(acceptableDeviation), availabilityStart, availabilityEnd);
	}

	/**
	 * Plan a list of developers for the task that corresponds to this planning
	 * 
	 * @param plannedDevelopers
	 *            | The developers to assign
	 */
	public void setDevelopers(List<User> plannedDevelopers) {
		this.plannedDevelopers = plannedDevelopers;
	}
	
	/**
	 * Return an immutable list of the developers assigned to the task that
	 * corresponds to this planning
	 * 
	 * @return an immutable list of developers (wrapped in views)
	 */
	public List<User> getPlannedDevelopers(){
		return plannedDevelopers;
	}

}
