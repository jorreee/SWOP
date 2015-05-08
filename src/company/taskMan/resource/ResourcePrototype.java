package company.taskMan.resource;

import java.util.ArrayList;

/**
 * A resource prototype is an abstract resource that defines a specific type of
 * resources. The data within this prototype will be used to define new concrete
 * resources
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class ResourcePrototype extends Resource {
	
	/**
	 * Create a new resource prototype (without the requirements or conflicts
	 * information, these should be added later) with a name and an optional
	 * AvailabilityPeriod
	 * 
	 * @param name
	 *            | The name of the new prototype
	 * @param availability
	 *            | The period in which the resource should be available, null
	 *            if it is not applicable
	 */
	public ResourcePrototype(String name, AvailabilityPeriod availability) {
		super(name, new ArrayList<ResourcePrototype>(), new ArrayList<ResourcePrototype>(), availability);
	}
	
	/**
	 * Instantiate a concrete resource based on this prototype
	 * 
	 * @param instanceName
	 *            | The name of the new instance
	 * @return the concrete resource with the given name based on this prototype
	 */
	public ConcreteResource instantiate(String instanceName) {
		return new ConcreteResource(instanceName, this);
	}
	
	/**
	 * Add a required resource to a prototype
	 * 
	 * @param requiredResources
	 *            | The new requirement to add
	 * @return True if the new requirement was successfully added to the
	 *         prototype
	 */
	public boolean addRequiredResource(ResourcePrototype requiredResources) {
		return reqResourcesList.add(requiredResources);
	}
	
	/**
	 * Add a conflicting resource to a prototype
	 * 
	 * @param conflictingResource
	 *            | The new conflict to add
	 * @return True if the new conflicts was successfully added to the
	 *         prototype
	 */
	public boolean addConflictingResource(ResourcePrototype conflictingResource) {
		return conResourcesList.add(conflictingResource);
	}
	
}
