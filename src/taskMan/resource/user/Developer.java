package taskMan.resource.user;

public class Developer extends User {

	public Developer(int creationIndex, String name){
		super(name);
		addCredential(UserCredential.DEVELOPER);
	}
	
	
}
