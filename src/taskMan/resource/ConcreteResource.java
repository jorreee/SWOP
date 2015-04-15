package taskMan.resource;

import java.util.List;


public class ConcreteResource extends Resource {

	private ResourcePrototype prototype;
	
	public ConcreteResource(String resourceName,
			List<ResourcePrototype> requiredResourcesList,
			List<ResourcePrototype> conflictingResourcesList,
			AvailabilityPeriod dailyAvailble, ResourcePrototype prototype) {
		super(resourceName, requiredResourcesList, conflictingResourcesList,
				dailyAvailble);
		this.prototype = prototype;
	}

	public void setName(String name) {
		if(name != null) {
			this.resName = name;
		}
	}
	
	public ResourcePrototype getPrototype(){
		return prototype;
	}
	
}
