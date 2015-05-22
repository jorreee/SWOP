package company.taskMan;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import company.BranchView;
import company.taskMan.project.TaskView;
import company.taskMan.task.DelegatingTask;
import company.taskMan.task.DelegatingTaskProxy;
import company.taskMan.task.OriginalTaskProxy;
import company.taskMan.task.Task;

/**
 * The Branch Representative is responsible for communication between branches.
 * When a branch tries to do something that would impact the consistency of
 * another branch, their representatives will negotiate the terms and
 * synchronize the necessary information.
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 *
 */
public class BranchRepresentative {

	/**
	 * <TASK, PROXY>:
	 * TASK is being delegated remotely. It thinks PROXY is delegating him
	 */
	private Map<Task,DelegatingTaskProxy> delegationProxies;
	
	/**
	 * <TASK, PROXY>
	 * TASK is delegating a remote task. It thinks PROXY is the original task
	 */
	private Map<Task,OriginalTaskProxy> originalProxies;	
	
	private LinkedList<DelegationData> buffer;
	private boolean bufferMode;

	public BranchRepresentative() {
		
		delegationProxies = new HashMap<>();
		originalProxies = new HashMap<>();
		
		buffer = new LinkedList<DelegationData>();
		bufferMode = false;
	}

	/**
	 * Schedules a delegation for task to the branch toBranch. FromBranch 
	 * must be this representative's own branch. 
	 * If the delegation happens to its own branch, the task will end in
	 * the Unavailable state. 
	 * If the representative is in buffer mode, the delegation will be
	 * scheduled for later execution. If it isn't in buffer mode, the
	 * delegation will be executed immediately. 
	 * A delegation typically happens in three steps:
	 * 		1) A local DelegatingTaskProxy is made and the task-to-delegate
	 * 				is informed that it is being delegated
	 * 				Note: If the delegation is to its own branch, the task
	 * 					  will be informed that it is no longer being delegated
	 * 		2) The remote branch creates a Delegating Task and links it to
	 * 				a local OriginalTaskProxy
	 * 		3) The two proxies are linked to each other
	 * @param task
	 * @param fromBranch
	 * @param toBranch
	 * @throws IllegalArgumentException
	 * 			| If null arguments are supplied
	 */
	public void delegateTask(Task task, Branch fromBranch, Branch toBranch) 
			throws IllegalArgumentException {
		if(task == null || fromBranch == null || toBranch == null) {
			throw new IllegalArgumentException("Parameters cannot be null");
		}
		// Delegation came back to the original branch
		if (toBranch == fromBranch)	{
			delegationProxies.remove(task);
			task.delegate(false);
		} else {
		// Add new delegation to buffer
			buffer.add(new DelegationData(task, fromBranch, toBranch));
			if (!bufferMode) {
				executeBuffer();
			}
		}
	}

	/**
	 * Accepts the delegation of a task from the branch delegatingBranch. It will
	 * use newTask as a delegator that has delProxy as a remote proxy (in the
	 * original branch). It will create a local Original Task Proxy that is linked
	 * with delProxy so that communication is synchronized and ready for use.
	 * @param delProxy
	 * @param newTask
	 * @param delegatingBranch
	 * @throws IllegalArgumentException
	 * 			| If the supplied parameters are null
	 */
	public void delegateAccept(DelegatingTaskProxy delProxy, DelegatingTask newTask, Branch delegatingBranch) 
			throws IllegalArgumentException {
		if(delProxy == null || newTask == null || delegatingBranch == null) {
			throw new IllegalArgumentException("Parameters cannot be null");
		}
		OriginalTaskProxy origiProxy = new OriginalTaskProxy(newTask, delegatingBranch);
		delProxy.link(origiProxy);
		origiProxy.link(delProxy);
		originalProxies.put(newTask, origiProxy);
	}

	private void executeBuffer(){
		// For every delegation ready in the buffer
		while (!buffer.isEmpty()) {
			DelegationData deleg = buffer.poll();						// Next delegation to commit (a TO request)
			Task task = deleg.delegatedTask;							// Task to delegate
			Branch fromBranch = deleg.originalBranch;				    // Branch to delegate FROM
			Branch toBranch = deleg.newBranch;						    // Branch to delegate TO

			if (originalProxies.containsKey(task)) {
				// A. this task is already delegating another task
				OriginalTaskProxy origiProxy = originalProxies.get(task);
				Branch origiFromBranch = origiProxy.getOriginalBranch().get();

				// A.1 Remove previous delegation information
				originalProxies.remove(task);
				fromBranch.removeDelegatedTask(task);

				// A.2 (re-)delegate the original task in its respective delegator
				origiFromBranch.delegateTask(origiProxy.getOriginalTask(), toBranch);

				
			} else if(delegationProxies.containsKey(task)) { 
				// B. The original task is being re-delegated: remove its previous delegation and schedule a new one
				DelegatingTaskProxy delProxy = delegationProxies.get(task);
				Task origiTask = delProxy.getTask();
				origiTask.delegate(false);
				delegationProxies.remove(task);
				buffer.add(new DelegationData(task, fromBranch, toBranch));
				
			} else {
				// C. The task is delegated for the first time
				DelegatingTaskProxy delProxy = new DelegatingTaskProxy(task, fromBranch);
				toBranch.delegateAccept(delProxy);
				delegationProxies.put(task, delProxy);
				task.delegate(true);
			}
		}

	}

	/**
	 * Sets the buffer mode of this representative. If set to false, it will
	 * execute its scheduled delegations.
	 * @param bool
	 */
	public void setBufferMode(boolean bool) {
		bufferMode = bool;
		if(!bufferMode) {
			executeBuffer();
		} 
	}

	/**
	 * Returns the BranchView of the Branch that is repsonsible for task, 
	 * IF it is being delegated
	 * @param task
	 * @return 
	 * 			| BranchView of the task's responsible branch
	 * 			| empty Optional otherwise
	 */
	public Optional<BranchView> getResponsibleBranch(Task task) {
		Optional<DelegatingTaskProxy> delProxy = Optional.ofNullable(delegationProxies.get(task));

		if(delProxy.isPresent()) {
			if(delProxy.get().getDelegatingBranch().isPresent()) {
				return Optional.of(new BranchView(delProxy.get().getDelegatingBranch().get()));
			} else {
				return Optional.empty();
			}
		} else {
			//kijk eens in de buffer
			Iterator<DelegationData> i = buffer.iterator();
			DelegationData d;
			while(i.hasNext()) {
				d = i.next();
				if(d.delegatedTask == task) {
					return Optional.of(new BranchView(d.newBranch));
				}
			}
			return Optional.empty();
		}
	}

	/**
	 * Returns the TaskView of the Task that is delegating task, 
	 * IF it is being delegated
	 * @param task
	 * @return 
	 * 			| BranchView of the task's responsible branch
	 * 			| empty Optional otherwise
	 */
	public Optional<TaskView> getDelegatingTask(Task t) {
		Optional<DelegatingTaskProxy> delProxy = Optional.ofNullable(delegationProxies.get(t));
		
		if(delProxy.isPresent()) {
			return Optional.of(new TaskView(delProxy.get().getDelegatingTask()));
		} else {
			return Optional.empty();
		}
	}

	private class DelegationData {
		private Task delegatedTask;
		private Branch originalBranch, newBranch;

		private DelegationData(Task task,Branch origBranch, Branch newBranch){
			delegatedTask = task;
			originalBranch = origBranch;
			this.newBranch = newBranch;
		}
	}

	/**
	 * @return the current Task-Proxy pairings (original)
	 */
	protected Map<Task, OriginalTaskProxy> getOriginalProxies() {
		return originalProxies;
	}

	/**
	 * @return the current Task-Proxy pairings (delegating)
	 */
	protected Map<Task, DelegatingTaskProxy> getDelegatingProxies() {
		return delegationProxies;
	}

	/**
	 * A method to allow the Branch to offer new task and (original) proxy
	 * pairings to the representative
	 * 
	 * @param proxies
	 *            | The new task-proxy pairings present in the system
	 */
	protected void offerOriginalTaskProxies(Map<Task, OriginalTaskProxy> proxies) {
		originalProxies = proxies;
	}

	/**
	 * A method to allow the Branch to offer new task and (delegating) proxy
	 * pairings to the representative
	 * 
	 * @param proxies
	 *            | The new task-proxy pairings present in the system
	 */
	protected void offerDelegatingTaskProxies(
			Map<Task, DelegatingTaskProxy> proxies) {
		delegationProxies = proxies;
	}
}
