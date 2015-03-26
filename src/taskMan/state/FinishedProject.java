package taskMan.state;

import java.util.List;

import taskMan.Project;
import taskMan.util.Prerequisite;

public class FinishedProject implements ProjectStatus {
	
	private Project project;
	
	public FinishedProject(Project p) {
		this.project = p;
	}

	@Override
	public boolean shouldFinish(List<Prerequisite> pre) {
		return false;
	}

	@Override
	public boolean isFinished() {
		return true;
	}

	@Override
	public boolean isOngoing() {
		return !isFinished();
	}

}
