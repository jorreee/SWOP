package company.taskMan.task;

import java.time.LocalDateTime;
import java.util.Optional;

import company.taskMan.Branch;
import company.taskMan.util.TimeSpan;

/**
 * A DelegatingTaskProxy is a local placeholder for a remote delegating task. It has a reference to the 
 * task that is being delegated (originalTask) to update it about remote changes. The only connection
 * to the remote branch happens through the OriginalTaskProxy which is located in the remote branch.
 * All communication between this proxy and the actual delegating task runs through the remote 
 * OriginalTaskProxy.
 * 
 * @author Tim Van Den Broecke, Vincent Van Gestel, Joran Van de Woestijne, Eli Vangrieken
 *
 */
public class DelegatingTaskProxy implements Dependant {
	
	private Task originalTask;
	private OriginalTaskProxy other;
	private Branch originalBranch;
	
	/**
	 * Creates a new DelegatingTaskProxy for originalTask that resides
	 * in originalBranch
	 * 
	 * @param originalTask the task that is being delegating remotely
	 * @param originalBranch the branch that holds the original task
	 */
	public DelegatingTaskProxy(Task originalTask, Branch originalBranch) {
		this.originalTask = originalTask;
		for(Task p : originalTask.getPrerequisites()) {
			p.register(this);
		}
		this.originalBranch = originalBranch;
	}
	
	/**
	 * Links this DelegatingTaskProxy to a remote OriginalTaskProxy. 
	 * 
	 * @param other the originalTaskProxy
	 */
	public void link(OriginalTaskProxy other) {
		this.other = other;
		other.link(this);
		updateDependencyFinished(null);
	}
	/**
	 * Informs this DelegatingTaskProxy that the remote delegating task
	 * has finished. The delegated task will be informed to finish. 
	 * 
	 * @param sender the OriginalTaskProxy that sends the update
	 * @param endTime the end time of the delegating task
	 * @throws IllegalStateException 
	 * 			| if sender isn't the linked OriginalTaskProxy
	 */
	protected void updateProxyTaskFinished(OriginalTaskProxy sender, LocalDateTime endTime) 
			throws IllegalStateException {
		if(sender == other) {
			originalTask.finish(endTime);
		} else {
			throw new IllegalStateException("An unlinked OriginalTaskProxy has tried to notify you");
		}
	}
	
	/**
	 * Return whether the original task can be planned or not
	 * @return 
	 * 			| true if it can be planned
	 * 			 false if it cannot be planned
	 */
	public boolean allowsToBePlanned() {
		return originalTask.canBePlanned();
	}

	/**
	 * Updates the dependency on preTask. This update will result in a 
	 * hasFinishedEndPoint() check on all the original task's prerequisites
	 * If all prerequisites have a finished endpoint this method will call
	 * updatePrereqsFinished() on the OriginalTaskProxy.
	 * 
	 * Ignores the parameter preTask.
	 * 
	 * @param preTask the task that has finished
	 */
	@Override
	public void updateDependencyFinished(Task preTask) {
		// geval proxy in originele branch krijg te horen dat een prereq
		// is gefinished. Als alle prereqs vervuld zijn laat hij aan de
		// andere proxy weten dat hij kan gepland worden.
		for(Task t : originalTask.getPrerequisites()) {
			if(!t.hasFinishedEndpoint()) {
				return;
			}
		}
		other.updatePrereqsFinished(this);
	}

	/**
	 * A proxy has no effect on the timechain so this method will
	 * always return a TimeSpan of 0.
	 */
	@Override
	public TimeSpan getMaxDelayChain() {
		return new TimeSpan(0);
	}

	/**
	 * Returns the Branch that is delegating the original Task.
	 * @return 
	 * 			| the delegating branch
	 * 			| an empty Optional if there is no linked OriginalTaskProxy
	 */
	public Optional<Branch> getDelegatingBranch() {
		if(other == null) {
			return Optional.empty();
		}
		return Optional.of(other.getBranch());
	}

	/**
	 * The original (delegated) task
	 * @return originalTask
	 */
	public Task getTask() {
		return originalTask;
	}
	
	/**
	 * The task that is delegating for originalTask
	 * @return the delegating Task
	 */
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
