package taskMan.resource.user;

public class ProjectManager extends User {

	/**
	 * Construct a new Project Manager with a given username. A project manager
	 * will always have the PROJECTMANAGER credential.
	 * 
	 * @param username
	 *            | The username for this project manager
	 */
	public ProjectManager(String username){
		super(username); // There can only be one PM (admin) in the system, otherwise change constructor to allow dynamic creation index
		addCredential(UserCredential.PROJECTMANAGER);
	}
}
