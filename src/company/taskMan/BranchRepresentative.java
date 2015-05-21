package company.taskMan;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

import company.BranchView;
import company.taskMan.task.DelegatingTask;
import company.taskMan.task.DelegatingTaskProxy;
import company.taskMan.task.Dependant;
import company.taskMan.task.OriginalTaskProxy;
import company.taskMan.task.Task;
import company.taskMan.util.TimeSpan;

public class BranchRepresentative implements Dependant {

	//	private List<DelegationData> delegations;
	private Map<Task,DelegatingTaskProxy> taskToProxy;
	private Map<Task,OriginalTaskProxy> taskFromProxy;	
	//	private List<Delegation> delegationsFromBranch;
	private LinkedList<DelegationData> buffer;
	private Stack<Integer> bufferCheckpoints;
	private boolean bufferMode;

	public BranchRepresentative(){
		//		delegations = new ArrayList<DelegationData>();
		//		delegationsToBranch = new ArrayList<Delegation>();
		taskToProxy = new HashMap<>();
		taskFromProxy = new HashMap<>();
		buffer = new LinkedList<DelegationData>();
		bufferCheckpoints = new Stack<>();
		bufferMode = false;
	}

	public void delegateTask(Task task, Branch fromBranch, Branch toBranch) throws IllegalArgumentException {
		// Delegation came back to the original branch
		if (toBranch == fromBranch)	{
			task.delegate(new DelegatingTaskProxy(null, fromBranch));
			taskToProxy.remove(task);
			//			delegations.remove(getToDelegationContainingTask(task).get());
		} else { 
			if(taskToProxy.containsKey(task)) { // re-delegation
				// TODO unregister stuff
				taskToProxy.remove(task);
			}
			// Add new delegation to buffer
			buffer.add(new DelegationData(task, fromBranch, toBranch));

			if (!bufferMode) {
				executeBuffer();
			}
		}
	}

	public DelegatingTaskProxy delegateAccept(OriginalTaskProxy fromProxy, DelegatingTask newTask, Branch toBranch) {
		taskFromProxy.put(newTask, fromProxy);
		DelegatingTaskProxy toProxy = new DelegatingTaskProxy(newTask, toBranch);
		fromProxy.link(toProxy);
		toProxy.link(fromProxy);
		return toProxy;
		//		delegations.add(new DelegationData(newTask, fromBranch, toBranch));
	}

	public void executeBuffer(){
		// For every delegation ready in the buffer
		while (!buffer.isEmpty()) {
			DelegationData deleg = buffer.poll();						// Next delegation to commit (a TO request)
			Task task = deleg.delegatedTask;							// Task to delegate
			Branch fromBranch = deleg.originalBranch;				    // Branch to delegate FROM
			Branch toBranch = deleg.newBranch;						    // Branch to delegate TO

			//TODO algemeen plan voor delegations (zie figuur facebook): 
			//		1) maak Delegating Task, TObranch
			//		2) maak proxies in Reps, juiste interdependencies
			//		3) link proxies
			// NEW STUFF BELOW
			// A. If this task was already a delegated task
			if (taskFromProxy.containsKey(task)) {
				OriginalTaskProxy fromProxy = taskFromProxy.get(task); 

				// A.1 Remove previous delegation information
				fromBranch.removeDelegatedTask(task);
				taskFromProxy.remove(task);
				fromBranch = fromProxy.getFromBranch();

				// A.2 (re-)delegate the original task in its respective delegator
				fromBranch.delegateTask(task, toBranch);

				// B. Else if this task hasn't been delegated before
			} else {
				OriginalTaskProxy fromProxy = new OriginalTaskProxy(task, fromBranch);
				DelegatingTaskProxy toProxy = toBranch.delegateAccept(fromProxy);
				taskToProxy.put(task, toProxy);
				task.delegate(toProxy); // TODO hoe moet het registeren tussen T1 en P1 nu precies?
			}
			// OLD STUFF BELOW
			//			Task originalTask = task.getOriginalDelegatedTask();	// The original task (this task has been delegated before) or null
			//			Optional<DelegationData> optionalOriginalDelegation = getFromDelegationContainingTask(originalTask);	// Delegation made to this delegator (a FROM request)
			//			// A. If this task was already a delegated task
			//			if (optionalOriginalDelegation.isPresent()) {
			//				DelegationData originalDelegation = optionalOriginalDelegation.get();
			//				// A.e The previous delegation does not lead here
			//				if (originalDelegation.getBranch() != fromBranch){
			//					throw new IllegalStateException("The original branch to delegate from is not equal to the one in the delegations sytem.");
			//				}
			//				
			//				// A.1 Remove previous delegation information
			//				delegations.remove(originalDelegation);
			//				fromBranch.removeDelegatedTask(task);
			//				
			//				// A.2 (re-)delegate the original task in its respective delegator
			//				originalDelegation.getOriginalBranch().delegateTask(task, toBranch);
			//			}
			//			// B. If this task hasn't been delegated before
			//			else {
			//				delegations.add(deleg);
			//				Task newTask = toBranch.delegateAccept(task, fromBranch);
			//				task.delegate(newTask);
			//			}
		}

	}

	// TODO still needed?
	//	public Optional<DelegationData> getToDelegationContainingTask(Task task){
	//		if (task == null){
	//			return Optional.empty();
	//		}
	//		List<DelegationData> simDelegationOfTask = buffer.stream().filter(d -> d.getTask() == task).collect(Collectors.toList());
	//		switch (simDelegationOfTask.size()) {
	//		case 1 : return Optional.of(simDelegationOfTask.get(0));
	//		}
	//		List<DelegationData> delegationOfTask = delegations.stream().filter(d -> d.getTask() == task).collect(Collectors.toList());
	//		switch (delegationOfTask.size()) {
	//		case 1 : return Optional.of(delegationOfTask.get(0));
	//		default : return Optional.empty();
	//		}
	//	}
	//	
	//	public Optional<DelegationData> getFromDelegationContainingTask(Task task){
	//		if (task == null){
	//			return Optional.empty();
	//		}
	//		List<DelegationData> delegationOfTask = delegations.stream().filter(d -> d.getTask() == task).collect(Collectors.toList());
	//		switch (delegationOfTask.size()) {
	//		case 1 : return Optional.of(delegationOfTask.get(0));
	//		default : return Optional.empty();
	//		}
	//	}

	public void setBufferMode(boolean bool){
		bufferMode = bool;
		if(!bufferMode) {
			executeBuffer();
			bufferCheckpoints.pop();
		} else {
			bufferCheckpoints.push(buffer.size());
		}
	}

	public void clearBuffer() {
		Integer checkpoint = bufferCheckpoints.pop();
		while(buffer.size() > checkpoint) {
			buffer.removeLast();
		}
	}

	public Optional<BranchView> getResponsibleBranch(Task task) {
		Optional<DelegatingTaskProxy> toProxy = Optional.ofNullable(taskToProxy.get(task));

		if(toProxy.isPresent()) {
			return toProxy.get().getToBranch();
		} else {
			return Optional.empty();
		}
	}

	public Optional<Task> getDelegatingTask(Task t) {
		Optional<DelegatingTaskProxy> toProxy = Optional.ofNullable(taskToProxy.get(t));

		if(toProxy.isPresent()) {
			return Optional.of(toProxy.get().getTask());
		} else {
			return Optional.empty();
		}
	}

	/**
	 * 
	 * @param plannableTask
	 * @throws IllegalStateException
	 * 			| when plannableTask isn't delegated 
	 */
	//	@Override
	public void updateDependencyPlannable(Task plannableTask) throws IllegalStateException {
		// TODO stuur een bericht naar delegating task dat hij kan gepland worden?

	}

	@Override
	public void updateDependencyFinished(Task preTask)
			throws IllegalStateException {
		// TODO gross.

	}

	@Override
	public TimeSpan getMaxDelayChain() {
		return new TimeSpan(0); //TODO gross.
	}

	private class DelegationData {
		private Task delegatedTask;//, newTask;
		private Branch originalBranch, newBranch;

		private DelegationData(Task task,Branch origBranch, Branch newBranch){
			delegatedTask = task;
			originalBranch = origBranch;
			this.newBranch = newBranch;
		}
	}
}
