package taskMan.resource;

import java.util.List;

public class ResourcePrototype extends Resource implements Cloneable {
	
	public ResourcePrototype(String name, List<ResourcePrototype> requiredResources, List<ResourcePrototype> conflictingResources, DailyAvailability dailyAvailability) {
		super(name, requiredResources, conflictingResources, dailyAvailability);
	}

	@Override
	public ConcreteResource clone() {
		return new ConcreteResource(resName, reqResourcesList, conResourcesList, dailyAvailable);
	}
}
