package taskMan.resource.user;
import java.util.ArrayList;
import java.util.List;

import taskMan.resource.Resource;
import taskMan.resource.ResourcePrototype;


public abstract class User extends Resource {
	
	private List<UserCredential> userCredentials;
	
	public User(String name){
		super(name,new ArrayList<ResourcePrototype>(),new ArrayList<ResourcePrototype>(),null);
		userCredentials = new ArrayList<>();
	}
	
	public boolean hasAsCredential(UserCredential credential) {
		return userCredentials.contains(credential);
	}
	
	public boolean addCredential(UserCredential credential) {
		return userCredentials.add(credential);
	}
	
}
