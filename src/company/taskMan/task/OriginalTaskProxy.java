package company.taskMan.task;

import java.util.Optional;

import company.taskMan.Branch;

public class OriginalTaskProxy {
	
	private DelegatingTask delegatingTask;
	private DelegatingTaskProxy other;
	private Branch delegatingBranch;
	private boolean hasUnfinishedPrereqs;
	
	public OriginalTaskProxy(DelegatingTask t, Branch delegatingBranch) {
		this.delegatingTask = t;
		this.delegatingBranch = delegatingBranch;
		delegatingTask.setProxy(this);
		hasUnfinishedPrereqs = true;
	}
	
	public void link(DelegatingTaskProxy other) {
		this.other = other;
		if(delegatingTask.isFinished()) {
			other.updateProxyTaskFinished(delegatingTask.getEndTime());
		}
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

	public void updateDependencyFinished(Task preTask)
			throws IllegalStateException {
		if(preTask == delegatingTask) {
			// geval proxy in delegating branch krijgt te horen dat de 
			// delegating task is gefinished. Hij laat aan de proxy in
			// de originele branch weten dat de originele task moet notifyen
			// dat hij 'klaar' is
			other.updateProxyTaskFinished(preTask.getEndTime());
		} else {
			throw new IllegalStateException("An error occured");
		}
	}

	protected Task getTask() {
		return delegatingTask;
	}
	
	public Task getOriginalTask() {
		return other.getTask();
	}

	public Optional<Branch> getOriginalBranch() {
		if(other == null) {
			return Optional.empty();
		}
		return Optional.of(other.getBranch());
	}
	
	protected Branch getBranch() { return delegatingBranch; }

	/**
	 * Dirty method for the simulation
	 * @param task
	 */
	public void setTask(Task task) {
		delegatingTask = (DelegatingTask) task;		
	}

}
