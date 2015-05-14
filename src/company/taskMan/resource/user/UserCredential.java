package company.taskMan.resource.user;

import java.util.ArrayList;
import java.util.List;

/**
 * Credentials that can be allocated to Users. These credentials could grant
 * elevated access rights to a user to do certain commands in the system. These
 * credentials can be allocated dynamically and at run-time for added
 * flexibility.
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public enum UserCredential {
	PROJECTMANAGER(new UserPermission[]{
			UserPermission.SHOW_OVERVIEW,
			UserPermission.ADVANCE_TIME,
			UserPermission.CREATE_PROJECT,
			UserPermission.CREATE_TASK,
			UserPermission.PLAN_TASK,
			UserPermission.SIMULATE,
			UserPermission.DELEGATE_TASK}),
	DEVELOPER(new UserPermission[]{
			UserPermission.SHOW_OVERVIEW,
			UserPermission.ADVANCE_TIME,
			UserPermission.UPDATE_TASK}),
	SUPER_USER(UserPermission.values());
	
	private List<UserPermission> permissions;
	
	private UserCredential(UserPermission[] permissions) {
		this.permissions = new ArrayList<UserPermission>();
		for(UserPermission p : permissions) {
			this.permissions.add(p);
		}
	}
	
	public List<UserPermission> getPermissions() {
		return this.permissions;
	}
	
}
