package taskMan.resource.user;

public class Developer extends User {

	/**
	 * Construct a new developer with a given name. Every developer will have
	 * the DEVELOPER credential.
	 * 
	 * @param name
	 *            | The name this developer will have
	 */
	public Developer(String name, UserPrototype prototype){
		super(name, prototype);
		addCredential(UserCredential.DEVELOPER);
	}
	
	
}
