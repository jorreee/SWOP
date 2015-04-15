package taskMan.resource.user;

public class Developer extends User {

	public Developer(int creationIndex, String name){
		super(creationIndex, name);
		addCredential(UserCredential.DEVELOPER);
	}
	
	
}
