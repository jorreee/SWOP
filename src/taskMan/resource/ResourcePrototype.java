package taskMan.resource;

import java.util.ArrayList;
import java.util.List;

public class ResourcePrototype extends Resource {
	
	private int creationIndex;
	public boolean isCreationIndex(int i) {
		return i == creationIndex;
	}
	
	public ResourcePrototype(String name, AvailabilityPeriod availability, int creationIndex) {
		super(name, new ArrayList<ResourcePrototype>(), new ArrayList<ResourcePrototype>(), availability);
		this.creationIndex = creationIndex;
	}
	
	public ResourcePrototype(String name, List<ResourcePrototype> requiredResources, List<ResourcePrototype> conflictingResources, AvailabilityPeriod availability) {
		super(name, requiredResources, conflictingResources, availability);
	}
	
	public ConcreteResource instantiate(String instanceName) {
		return new ConcreteResource(instanceName, reqResourcesList, conResourcesList, dailyAvailable);
	}
	
	public boolean addRequiredResource(ResourcePrototype requiredResources) {
		return reqResourcesList.add(requiredResources);
	}
	
	public boolean addConflictingResource(ResourcePrototype conflictingResources) {
		return conResourcesList.add(conflictingResources);
	}
}
