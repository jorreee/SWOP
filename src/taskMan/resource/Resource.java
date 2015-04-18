package taskMan.resource;

import java.time.LocalTime;
import java.util.List;

/**
 * This abstract class defines resources. A resource has a name and lists of
 * abstract resources (resourcePrototypes) that this specific resource requires
 * or conflicts with. A resource also has an optional period of the day during
 * which the resource is available for use.
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public abstract class Resource {
	
	protected String resName;
	protected List<ResourcePrototype> reqResourcesList;
	protected List<ResourcePrototype> conResourcesList;
	
	protected AvailabilityPeriod dailyAvailable;
	
	/**
	 * Construct a new resource with all the required information. The
	 * AvailabilityPeriod can be null when it is available during the entire day
	 * (i.e. not limited)
	 * 
	 * @param resourceName
	 *            | The name of the resource
	 * @param requiredResourcesList
	 *            | The list of required resources
	 * @param conflictingResourcesList
	 *            | The list of conflicting resources
	 * @param dailyAvailability
	 *            | The availability period, null if not present
	 */
	public Resource(String resourceName, List<ResourcePrototype> requiredResourcesList, List<ResourcePrototype> conflictingResourcesList, AvailabilityPeriod dailyAvailability) {
		this.resName = resourceName;
		this.reqResourcesList = requiredResourcesList;
		this.conResourcesList = conflictingResourcesList;
		this.dailyAvailable = dailyAvailability;
	}
	
	/**
	 * A method to retrieve the name of the resource
	 * 
	 * @return the name of the resource
	 */
	public String getName() { return resName; }
	
	/**
	 * A method to return the list of required resources
	 * @return the required resources
	 */
	public List<ResourcePrototype> getRequiredResources() { return reqResourcesList; }
	/**
	 * A method to return the list of conflicting resources
	 * @return the conflicting resources
	 */
	public List<ResourcePrototype> getConflictingResources() { return conResourcesList; }
	
	/**
	 * A method to check whether the availability period is defined
	 * 
	 * @return true if there is an availability period available, false if it is
	 *         not present
	 */
	public boolean isDailyAvailable() { return dailyAvailable != null; }
	
	/**
	 * A getter to retrieve the start time of the availability period
	 * 
	 * @return the hour and minutes of the day when the availability period
	 *         begins, null if there is no availability period
	 */
	public LocalTime getDailyAvailabilityStartTime() {
		if(dailyAvailable == null) {
			return null;
		}
		return dailyAvailable.getStartTime();
	}
	
	/**
	 * A getter to retrieve the end time of the availability period
	 * 
	 * @return the hour and minutes of the day when the availability period
	 *         end, null if there is no availability period
	 */
	public LocalTime getDailyAvailabilityEndTime() {
		if(dailyAvailable == null) {
			return null;
		}
		return dailyAvailable.getEndTime();
	}
}