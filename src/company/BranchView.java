package company;

import java.util.List;

import company.taskMan.ProjectView;
import company.taskMan.Branch;

/**
 * A BranchView is a wrapper for Branches. The BranchView only has limited access to the branch and is thus safe 
 * 
 * @author Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class BranchView {

	private final Branch branch;
	
	public BranchView(Branch newBranch) {
		this.branch = newBranch;
	}
	
	public String getGeographicLocation() {
		return branch.getGeographicLocation();
	}
	
	protected Branch unwrap() {
		return branch;
	}
	
	public List<ProjectView> getProjects() {
		return branch.getProjects();
	}
}
