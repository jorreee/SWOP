package taskMan.state;

import java.util.List;

import taskMan.util.Prerequisite;

public interface ProjectStatus {
	
	public boolean shouldFinish(List<Prerequisite> pre);
	public boolean isFinished();
	public boolean isOngoing();
	
}
