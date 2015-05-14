package company;

import java.util.List;

import company.taskMan.ProjectView;
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
	
	public List<ProjectView> getProjects() {
		return branch.getProjects();
	}
}
