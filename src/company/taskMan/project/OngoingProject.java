package company.taskMan.project;

import company.taskMan.task.Task;

/**
 * This class represents the ongoing state of a project
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class OngoingProject implements ProjectStatus {
	
	/**
	 * Construct a new ongoing status
	 * 
	 */
	public OngoingProject() {
	}

	@Override
	public void finish(Project project, Task lastTask) throws IllegalStateException {
		boolean shouldFinish = true;
		for (Task t : project.getTasks()) {
			if(!t.hasFinishedEndpoint()) {
				shouldFinish = false;
			}
		}
		if(shouldFinish) {
			project.setProjectStatus(new FinishedProject());
			project.setEndTime(lastTask.getEndTime());
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public boolean isOngoing() {
		return !isFinished();
	}
	
	@Override
	public String toString() {
		return "Ongoing";
	}

}
