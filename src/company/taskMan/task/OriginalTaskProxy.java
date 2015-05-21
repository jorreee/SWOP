package company.taskMan.task;

import java.util.Optional;

import company.taskMan.Branch;
import company.taskMan.util.TimeSpan;

public class OriginalTaskProxy implements Dependant {
	
	private DelegatingTask delegatingTask;
	private DelegatingTaskProxy other;
	private Branch delegatingBranch;
	private boolean hasUnfinishedPrereqs;
	
	public OriginalTaskProxy(DelegatingTask t, Branch delegatingBranch) {
		this.delegatingTask = t;
		this.delegatingBranch = delegatingBranch;
		delegatingTask.register(this);
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

	@Override
	public TimeSpan getMaxDelayChain() {
		return new TimeSpan(0);
	}

	public Task getTask() {
		return delegatingTask;
	}

	public Optional<Branch> getOriginalBranch() {
		if(other == null) {
			return Optional.empty();
		}
		return Optional.of(other.getBranch());
	}
	
	protected Branch getBranch() { return delegatingBranch; }

}
