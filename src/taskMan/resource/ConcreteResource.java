package taskMan.resource;

import java.util.List;

public class ConcreteResource extends Resource {

	public ConcreteResource(int creationIndex, String resourceName,
			List<ResourcePrototype> requiredResourcesList,
			List<ResourcePrototype> conflictingResourcesList,
			AvailabilityPeriod dailyAvailble) {
		super(creationIndex, resourceName, requiredResourcesList, conflictingResourcesList,
				dailyAvailble);
	}

	public void setName(String name) {
		if(name != null) {
			this.resName = name;
		}
	}
	
}
