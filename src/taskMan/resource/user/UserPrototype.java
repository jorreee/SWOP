package taskMan.resource.user;

import taskMan.resource.AvailabilityPeriod;
import taskMan.resource.ResourcePrototype;

public class UserPrototype extends ResourcePrototype {

	public UserPrototype(String name, AvailabilityPeriod availability) {
		super(name, availability);
	}
	
	public User instantiateDeveloper(String name){
		return new Developer(name,this);
	}
	
	public User instantiateProjectManager(String name){
		return new ProjectManager(name,this);
	}

}
