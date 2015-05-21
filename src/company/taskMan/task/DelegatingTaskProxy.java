package company.taskMan.task;

import java.time.LocalDateTime;

import company.taskMan.util.TimeSpan;

public class DelegatingTaskProxy implements Dependant {
	
	private Task originalTask;
	private OriginalTaskProxy other;
	
	public DelegatingTaskProxy(Task t) {
		this.originalTask = t;
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

}
