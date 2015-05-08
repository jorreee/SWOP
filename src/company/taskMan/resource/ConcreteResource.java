package company.taskMan.resource;


/**
 * This class represents "concrete" resources. These resource are specific
 * instances of the more general resourceType. Of every ResourceType there can
 * be multiple concrete resources.
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class ConcreteResource extends Resource {

	private ResourcePrototype prototype;
	
	/**
	 * Construct an instance of a resourceType. This resourceType is contained
	 * within the resourcePrototype. A concrete resource has a name to identify
	 * it with, a list of other resourceTypes it requires, a list of
	 * resourceType it conflicts with and an optional availability period. (it
	 * finds the last three from the supplied prototype)
	 * During this availability period, the resource is available for use.
	 * 
	 * @param resourceName
	 *            | The name of this instance
	 * @param prototype
	 *            | The resourceType this concrete resource instantiates
	 */
	public ConcreteResource(String resourceName,
			ResourcePrototype prototype) {
		super(resourceName, prototype.getRequiredResources(), prototype.getConflictingResources(),
				new AvailabilityPeriod(prototype.getDailyAvailabilityStartTime(), prototype.getDailyAvailabilityEndTime()));
		this.prototype = prototype;
	}
	
	/**
	 * Return the resourceType this concrete resource instantiates
	 * 
	 * @return The ResourcePrototype that made this instance
	 */
	public ResourcePrototype getPrototype(){
		return prototype;
	}
	
}
