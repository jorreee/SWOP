package company.taskMan.task;

import java.time.LocalDateTime;
import java.util.Optional;

import company.BranchView;
import company.taskMan.Branch;
import company.taskMan.util.TimeSpan;

public class DelegatingTaskProxy implements Dependant {
	
	private DelegatingTask originalTask;
	private OriginalTaskProxy other;
	private Branch toBranch;
	
	public DelegatingTaskProxy(DelegatingTask t, Branch toBranch) {
		this.originalTask = t;
		this.toBranch = toBranch;
	}
	
	public void link(OriginalTaskProxy other) {
		this.other = other;
	}
	
	public void updateProxyTaskFinished(LocalDateTime endTime) {
		originalTask.finish(endTime);
	}
	
	public boolean allowsToBePlanned() {
		return originalTask.canBePlanned();
	}

	@Override
	public void updateDependencyFinished(Task preTask)
			throws IllegalStateException {
		// geval proxy in originele branch krijg te horen dat een prereq
		// is gefinished. Als alle prereqs vervuld zijn laat hij aan de
		// andere proxy weten dat hij kan gepland worden.
		for(Task t : originalTask.getPrerequisites()) {
			if(!t.hasFinishedEndpoint()) {
				return;
			}
		}
		other.updatePrereqsFinished();
	}

	@Override
	public TimeSpan getMaxDelayChain() {
		return new TimeSpan(0);
	}

	public Optional<BranchView> getToBranch() {
		return Optional.of(new BranchView(toBranch));
	}

	public Task getTask() {
		return originalTask;
	}

}
