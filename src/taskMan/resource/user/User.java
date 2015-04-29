package taskMan.resource.user;
import java.util.ArrayList;
import java.util.List;

import taskMan.resource.AvailabilityPeriod;
import taskMan.resource.ConcreteResource;

/**
 * Users in our system are the people using it. The current user using the
 * system should be logged in (since his credentials will be checked when
 * accessing the system).
 * 
 * User extends Resource, this way a user can be handled as a resource. A user
 * can for example be reserved by a task.
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class User extends ConcreteResource {
	
	private List<UserCredential> userCredentials;
	
	/**
	 * Construct a new user. No credentials are known. A user has no
	 * requirements or conflicts and is not limited by an availability period
	 * (however this could be possible in future adaptations of the system).
	 * 
	 * @param name
	 *            | The name of the new user
	 */
	public User(String name, UserPrototype prototype){
		super(name, prototype);
//		super(name,new ArrayList<ResourcePrototype>(),new ArrayList<ResourcePrototype>(),null);
		userCredentials = new ArrayList<>();
	}
	
	/**
	 * Check whether or not this user has a specific credential
	 * 
	 * @param credential
	 *            | The credential to check
	 * @return True if the user has the credential, false otherwise
	 */
	public boolean hasAsCredential(UserCredential credential) {
		return userCredentials.contains(credential);
	}
	
	/**
	 * Give this specific user a specific credential
	 * 
	 * @param credential
	 *            | The credential to add
	 * @return True if the credential was added, false otherwise
	 */
	public boolean addCredential(UserCredential credential) {
		return userCredentials.add(credential);
	}
	
}
