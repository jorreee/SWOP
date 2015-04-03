package taskMan.resource.user;
import java.util.ArrayList;

import taskMan.resource.Resource;
import taskMan.resource.ResourcePrototype;


public abstract class User extends Resource {
	
	
	public User(String name){
		super(name,new ArrayList<ResourcePrototype>(),new ArrayList<ResourcePrototype>(),null);
	}
	

	
}
