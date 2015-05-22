package company.taskMan.task;

import java.util.Optional;

import company.taskMan.Branch;

/**
 * An OriginalTaskProxy is a local placeholder for a remotely delegated task. It has a reference to the 
 * task that is delegating (delegatingTask) to update it about remote changes. The only connection
 * to the remote branch happens through the DelegatingTaskProxy which is located in the remote branch.
 * All communication between this proxy and the actual delegated task runs through the remote 
 * DelegatedTaskProxy.
 * 
 * @author Tim Van Den Broecke, Vincent Van Gestel, Joran Van de Woestijne, Eli Vangrieken
 *
 */
public class OriginalTaskProxy {
	
	private DelegatingTask delegatingTask;
	private DelegatingTaskProxy other;
	private Branch delegatingBranch;
	private boolean hasUnfinishedPrereqs;

	/**
	 * Creates a new OriginalTaskProxy for delegatingTask that resides
	 * in delegatingBranch. This constructor will call setProxy(this)
	 * on delegatingTask.
	 * 
	 * @param originalTask the task that is being delegating remotely
	 * @param originalBranch the branch that holds the original task
	 */
	public OriginalTaskProxy(DelegatingTask delegatingTask, Branch delegatingBranch) {
		this.delegatingTask = delegatingTask;
		this.delegatingBranch = delegatingBranch;
		this.delegatingTask.setProxy(this);
		hasUnfinishedPrereqs = true;
	}
	
	/**
	 * Links this DelegatingTaskProxy to a remote OriginalTaskProxy. Should only 
	 * be called by the DelegatingTaskProxy with itself as parameter
	 * 
	 * @param other the originalTaskProxy
	 */
	protected void link(DelegatingTaskProxy other) {
		this.other = other;
		if(delegatingTask.isFinished()) {
			other.updateProxyTaskFinished(this, delegatingTask.getEndTime());
		}
	}
	
	/**
	 * Informs this OriginalTaskProxy that all prerequisites for the original task
	 * are fulfilled. When this method is called, all following calls of
	 * hasUnfinishedPrerequisites will return false.
	 * 
	 * @throws IllegalStateException
	 * 			| if sender isn't the linked DelegatingTaskProxy
	 */
	public void updatePrereqsFinished(DelegatingTaskProxy sender) 
			throws IllegalStateException {
		if(sender == other) {
			hasUnfinishedPrereqs = false;
		} else {
			throw new IllegalStateException("An unlinked DelegatingTaskProxy has tried to notify you");
		}
	}
	
	/**
	 * Returns whether the remote original task has unfinished prerequisites
	 * @return
	 * 			| true if the original task has unfinished prerequisites
	 * 			| false if the original task doesn't have unfinished prerequisites
	 */
	public boolean hasUnfinishedPrerequisites() {
		return hasUnfinishedPrereqs;
	}
	
	/**
	 * Returns whether the delegating task can be planned. This method will ask
	 * the original task whether it can be planned or not.
	 * @return
	 * 			| true if the delegating task can be planned
	 * 			| false if the delegating task cannot be planned
	 */
	public boolean canBePlanned() {
		return other.allowsToBePlanned();
	}

	/**
	 * Informs this OriginalTaskProxy that preTask has finished. It will call
	 * updateProxyTaskFinished(this, preTask.getEndTime()) on the linked
	 * DelegatingTaskProxy
	 * 
	 * @param preTask the task that finished
	 * @throws IllegalStateException
	 * 			| If preTask isn't the linked DelegatingTask
	 */
	public void updateDependencyFinished(Task preTask)
			throws IllegalStateException {
		if(preTask == delegatingTask) {
			// geval proxy in delegating branch krijgt te horen dat de 
			// delegating task is gefinished. Hij laat aan de proxy in
			// de originele branch weten dat de originele task moet notifyen
			// dat hij 'klaar' is
			other.updateProxyTaskFinished(this, preTask.getEndTime());
		} else {
			throw new IllegalStateException("An uknown DelegatingTask has tried to notify you");
		}
	}

	/**
	 * Returns the local delegating task
	 * @return delegatingTask
	 */
	protected Task getTask() {
		return delegatingTask;
	}
	
	/**
	 * Returns the (original) task that delegatingTask is delegating
	 * @return the original delegated task
	 */
	public Task getOriginalTask() {
		return other.getTask();
	}

	/**
	 * Returns the Branch of the original Task.
	 * @return 
	 * 			| the original branch
	 * 			| an empty Optional if there is no linked DelegatingTaskProxy
	 */
	public Optional<Branch> getOriginalBranch() {
		if(other == null) {
			return Optional.empty();
		}
		return Optional.of(other.getBranch());
	}
	
	/**
	 * @return the branch of the delegating task
	 */
	protected Branch getBranch() { return delegatingBranch; }

	/**
	 * Reset the task of this proxy
	 * 
	 * @param task
	 *            | The new task
	 */
	public void setTask(Task task) {
		delegatingTask = (DelegatingTask) task;		
	}

}
