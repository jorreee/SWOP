package company.taskMan.task;

import company.taskMan.Branch;
import company.taskMan.project.TaskView;
import company.taskMan.util.TimeSpan;

public class OriginalTaskProxy implements Dependant {
	
	private Task task;
	private DelegatingTaskProxy other;
	private Branch fromBranch;
	private boolean hasUnfinishedPrereqs;
	
	public OriginalTaskProxy(Task t, Branch fromBranch) {
		this.task = t;
		this.fromBranch = fromBranch;
		task.register(this);
		hasUnfinishedPrereqs = true;
	}
	
	public void link(DelegatingTaskProxy other) {
		this.other = other;
	}
	
	public void updatePrereqsFinished() {
		hasUnfinishedPrereqs = false;
	}
	
	public boolean hasUnfinishedPrerequisites() {
		return hasUnfinishedPrereqs;
	}
	
	public boolean canBePlanned() {
		return other.allowsToBePlanned();
	}

	@Override
	public void updateDependencyFinished(Task preTask)
			throws IllegalStateException {
		if(preTask == task) {
			// geval proxy in delegating branch krijgt te horen dat de 
			// delegating task is gefinished. Hij laat aan de proxy in
			// de originele branch weten dat de originele task moet notifyen
			// dat hij 'klaar' is
			other.updateProxyTaskFinished(preTask.getEndTime());
		} else {
			throw new IllegalStateException("An error occured");
		}
	}

	@Override
	public TimeSpan getMaxDelayChain() {
		return new TimeSpan(0);
	}

	public TaskView getTask() {
		return new TaskView(task);
	}

}
