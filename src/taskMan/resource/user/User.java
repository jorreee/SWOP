package taskMan.resource.user;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import taskMan.resource.ConcreteResource;

import com.google.common.collect.ImmutableList;

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

	private List<UserCredential> credentials;

	/**
	 * Construct a new user. No credentials are known. A user has no
	 * requirements or conflicts and is not limited by an availability period
	 * (however this could be possible in future adaptations of the system).
	 * 
	 * @param name
	 *            | The name of the new user
	 */
	public User(String name, UserPrototype prototype) {
		super(name, prototype);
		credentials = new ArrayList<>();
	}

	public boolean isDeveloper()      { return credentials.contains(UserCredential.DEVELOPER);      }
	public boolean isProjectManager() { return credentials.contains(UserCredential.PROJECTMANAGER); }
	
	/**
	 * Returns a full set of all permissions for this User. 
	 * 
	 * @return
	 * 			| a set of all permissions attached to this User's credentials
	 */
	public List<UserPermission> getPermissions() {
		ImmutableList.Builder<UserPermission> perms = ImmutableList.builder();
		Set<UserPermission> permsSet = new HashSet<UserPermission>();
		for(UserCredential c : credentials) {
			permsSet.addAll(c.getPermissions());
		}
		return perms.addAll(permsSet).build();
	}
	
	/**
	 * Give this specific user a specific credential
	 * 
	 * @param cred
	 *            | The credential to add
	 * @return True if the user now has the credential, false otherwise
	 */
	public boolean addCredential(UserCredential cred) {
		if(credentials.contains(cred)) {
			return true;
		}
		return credentials.add(cred);
	}
	
	/**
	 * Removes a specific credential from this user
	 * 
	 * @param cred
	 *            | The credential to remove
	 * @return True if the user no longer has the credential, false otherwise
	 */
	public boolean removeCredential(UserCredential cred) {
		if(!credentials.contains(cred)) {
			return true;
		}
		return credentials.remove(cred);
	}

}
