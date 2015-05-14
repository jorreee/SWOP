package company;

import company.taskMan.TaskMan;

public class BranchView {

	private final TaskMan branch;
	
	public BranchView(TaskMan newBranch) {
		this.branch = newBranch;
	}
	
	public String getGeographicLocation() {
		return branch.getGeographicLocation();
	}
	
	protected TaskMan unwrap() {
		return branch;
	}
}
