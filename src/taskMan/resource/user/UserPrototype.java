package taskMan.resource.user;

import taskMan.resource.ResourcePrototype;

public class UserPrototype extends ResourcePrototype {

	public UserPrototype() {
		super("User", null);
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
