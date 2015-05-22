package company.taskMan.task;

import java.time.LocalDateTime;
import java.util.Optional;

import company.taskMan.Branch;
import company.taskMan.util.TimeSpan;

public class DelegatingTaskProxy implements Dependant {
	
	private Task originalTask;
	private OriginalTaskProxy other;
	private Branch originalBranch;
	
	/**
	 * Creates a new DelegatingTaskProxy for originalTask that resides
	 * in originalBranch
	 * @param originalTask
	 * @param originalBranch
	 */
	public DelegatingTaskProxy(Task originalTask, Branch originalBranch) {
		this.originalTask = originalTask;
		for(Task p : originalTask.getPrerequisites()) {
			p.register(this);
		}
		this.originalBranch = originalBranch;
	}
	
	public void link(OriginalTaskProxy other) {
		this.other = other;
		updateDependencyFinished(null);
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

	public Optional<Branch> getDelegatingBranch() {
		if(other == null) {
			return Optional.empty();
		}
		return Optional.of(other.getBranch());
	}

	public Task getTask() {
		return originalTask;
	}
	
	public Task getDelegatingTask() {
		return other.getTask();
	}
	
	/**
	 * @return the original branch of the Proxy
	 */
	protected Branch getBranch() { return originalBranch; }

	/**
	 * Reset the task of this proxy
	 * 
	 * @param task
	 *            | The new task
	 */
	public void setTask(Task task) {
		for(Task t : originalTask.getPrerequisites()) {
			t.unregister(this);
		}
		originalTask = task;
		for(Task t : task.getPrerequisites()) {
			t.register(this);
		}
	}

}
