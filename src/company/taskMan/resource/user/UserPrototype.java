package company.taskMan.resource.user;

import company.taskMan.resource.ResourcePrototype;

public class UserPrototype extends ResourcePrototype {

	public UserPrototype() {
		super("User", null);
	}
	
	/**
	 * Returns a new User object with the supplied name and typical 
	 * DEVELOPER permissions:
	 *  - show overview
	 *  - advance time
	 * 	- update task status
	 * 
	 * @param name
	 * 			| the name of the new User
	 * @return
	 * 			| a new User with Developer permissions
	 */
	public User instantiateDeveloper(String name){
		User user =  new User(name,this);
		user.addCredential(UserCredential.DEVELOPER);
		return user;
		
	}

	/**
	 * Returns a new User object with the supplied name and typical 
	 * PROJECT MANAGER permissions:
	 *  - show overview
	 *  - advance time
	 *  - create project
	 *  - create task
	 *  - plan task
	 *  - run simulation
	 * 
	 * @param name
	 * 			| the name of the new User
	 * @return
	 * 			| a new User with Project Manager permissions
	 */
	public User instantiateProjectManager(String name){
		User user =  new User(name,this);
		user.addCredential(UserCredential.PROJECTMANAGER);
		return user;
	}

	/**
	 * Returns a new User object with the supplied name and ALL
	 * permissions:
	 *  - show overview
	 *  - advance time
	 *  - create project
	 *  - create task
	 *  - plan task
	 *  - update task
	 *  - run simulation
	 * 
	 * @param name
	 * 			| the name of the new User
	 * @return
	 * 			| a new User with all system permissions
	 */
	public User instantiateSuperUser(String name) {
		User user = new User(name,this);
		user.addCredential(UserCredential.SUPER_USER);
		return user;
	}

}
