package taskMan.resource.user;

import taskMan.resource.AvailabilityPeriod;
import taskMan.resource.ResourcePrototype;

public class UserPrototype extends ResourcePrototype {

	public UserPrototype(String name, AvailabilityPeriod availability) {
		super(name, availability);
	}
	
	public User instantiateDeveloper(String name){
		User user =  new User(name,this);
		user.addCredential(UserCredential.DEVELOPER);
		return user;
		
	}
	
	public User instantiateProjectManager(String name){
		User user =  new User(name,this);
		user.addCredential(UserCredential.PROJECTMANAGER);
		return user;
	}

}
