package taskMan.resource.user;

public class Developer extends User {

	public Developer(String name){
		super(name);
		addCredential(UserCredential.DEVELOPER);
	}
	
	
}
