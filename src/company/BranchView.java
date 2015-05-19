package company;

import java.util.List;

import company.taskMan.ProjectView;
import company.taskMan.Branch;

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
