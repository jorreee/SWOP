package taskMan.resource;

import java.time.LocalTime;

/**
 * The AvailabilityPeriod of a resource indicates when that resource is available during the day.
 *  
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class AvailabilityPeriod {
	private LocalTime startTime;
	private LocalTime endTime;
	
	/**
	 * Construct a new Availability Period that begins with the start time and
	 * ends with the end time.
	 * 
	 * @param startTime
	 *            | The earliest moment in the day that the resource should be
	 *            available
	 * @param endTime
	 *            | The moment of the day when the resource ceases to be
	 *            available
	 */
	public AvailabilityPeriod(LocalTime startTime, LocalTime endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	/**
	 * Return the time of day when the resource should be available
	 * 
	 * @return the time of day when the resource should be available
	 */
	public LocalTime getStartTime() { 
		return startTime;
	}

	/**
	 * Return the time of day when the resource should cease to be available
	 * 
	 * @return the time of day when the resource cease to be available
	 */
	public LocalTime getEndTime() { 
		return endTime;
	}
	
	/**
	 * Check whether or not this availability period is the same as another
	 * 
	 * @param otherPeriod
	 *            | The other period to compare with
	 * @return True is the other period has the same start and end timestamps,
	 *         false otherwise
	 */
	public boolean equals(AvailabilityPeriod otherPeriod) {
		if (this == otherPeriod)
			return true;
		if (otherPeriod == null)
			return false;
		if (getClass() != otherPeriod.getClass())
			return false;
		if (endTime == null) {
			if (otherPeriod.endTime != null)
				return false;
		} else if (!endTime.equals(otherPeriod.endTime))
			return false;
		if (startTime == null) {
			if (otherPeriod.startTime != null)
				return false;
		} else if (!startTime.equals(otherPeriod.startTime))
			return false;
		return true;
	}

}
