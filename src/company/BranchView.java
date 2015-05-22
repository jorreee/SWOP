package company;

import java.util.List;

import company.taskMan.Branch;
import company.taskMan.ProjectView;

/**
 * A BranchView is a wrapper for Branches. The BranchView only has limited
 * access to the branch and is thus safe to send out to the UI.
 * 
 * @author Tim Van den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class BranchView {

	private final Branch branch;
	
	/**
	 * Wrap a branch in a new view
	 * 
	 * @param newBranch
	 *            | The branch to wrap
	 */
	public BranchView(Branch newBranch) {
		this.branch = newBranch;
	}
	
	/**
	 * Retrieve the geographical location of the branch
	 * 
	 * @return The location of the branch
	 */
	public String getGeographicLocation() {
		return branch.getGeographicLocation();
	}
	
	/**
	 * @return the wrapped branch
	 */
	protected Branch unwrap() {
		return branch;
	}
	
	/**
	 * @return the projects of the branch
	 */
	public List<ProjectView> getProjects() {
		return branch.getProjects();
	}
}
