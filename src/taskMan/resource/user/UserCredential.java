package taskMan.resource.user;

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
			UserPermission.ADVANCE_TIME,
			UserPermission.CREATE_PROJECT,
			UserPermission.CREATE_TASK,
			UserPermission.PLAN_TASK,
			UserPermission.SIMULATE}),
	DEVELOPER(new UserPermission[]{
			UserPermission.ADVANCE_TIME,
			UserPermission.UPDATE_TASK});
	
	private List<UserPermission> permissions;
	
	private UserCredential(UserPermission[] permissions) {
		this.permissions = new ArrayList<UserPermission>();
		for(UserPermission p : permissions) {
			this.permissions.add(p);
		}
	}

	public boolean canAdvanceTime()   { return permissions.contains(UserPermission.ADVANCE_TIME);	}
	public boolean canCreateProject() { return permissions.contains(UserPermission.CREATE_PROJECT);	}
	public boolean canCreateTask()    { return permissions.contains(UserPermission.CREATE_TASK);    }
	public boolean canPlanTask()      { return permissions.contains(UserPermission.ADVANCE_TIME);   }
	public boolean canUpdateTask()    { return permissions.contains(UserPermission.ADVANCE_TIME);   }
	public boolean canSimulate()      { return permissions.contains(UserPermission.ADVANCE_TIME);   }
	
}
