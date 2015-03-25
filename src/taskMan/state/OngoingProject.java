package taskMan.state;

import java.util.List;

import taskMan.Project;
import taskMan.util.Prerequisite;

public class OngoingProject implements ProjectStatus {
	
	private Project project;
	
	public OngoingProject(Project p) {
		this.project = p;
	}

	@Override
	public boolean shouldFinish(List<Prerequisite> pre) {
		if(pre.size() == 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public boolean isOngoing() {
		return !isFinished();
	}

}
