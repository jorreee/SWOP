package taskMan.resource.user;

public class ProjectManager extends User {

	public ProjectManager(String username){
		super(username); // There can only be one PM (admin) in the system, otherwise change constructor to allow dynamic creation index
		addCredential(UserCredential.PROJECTMANAGER);
	}
}
