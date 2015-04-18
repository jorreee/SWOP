package taskMan.resource;

import java.util.List;

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
	 * resourceType it conflicts with and an optional availability period.
	 * During this availability period, the resource is available for use.
	 * 
	 * @param resourceName
	 *            | The name of this instance
	 * @param requiredResourcesList
	 *            | The list of resources this resource requires
	 * @param conflictingResourcesList
	 *            | The list of resources this resource conflicts with
	 * @param dailyAvailble
	 *            | The period during which this resource should be available,
	 *            if null, this resource is available for use during the entire
	 *            work day
	 * @param prototype
	 *            | The resourceType this concrete resource instantiates
	 */
	public ConcreteResource(String resourceName,
			List<ResourcePrototype> requiredResourcesList,
			List<ResourcePrototype> conflictingResourcesList,
			AvailabilityPeriod dailyAvailble, ResourcePrototype prototype) {
		super(resourceName, requiredResourcesList, conflictingResourcesList,
				dailyAvailble);
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
