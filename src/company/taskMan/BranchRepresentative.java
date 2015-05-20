package company.taskMan;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import company.BranchView;
import company.taskMan.task.Dependant;
import company.taskMan.task.Task;
import company.taskMan.util.TimeSpan;

public class BranchRepresentative implements Dependant {

	private List<Delegation> delegations;
//	private List<Delegation> delegationsFromBranch;
	private LinkedList<Delegation> buffer;
	private Stack<Integer> bufferCheckpoints;
	boolean bufferMode;

	public BranchRepresentative(){
		delegations = new ArrayList<Delegation>();
//		delegationsToBranch = new ArrayList<Delegation>();
		buffer = new LinkedList<Delegation>();
		bufferCheckpoints = new Stack<>();
		bufferMode = false;
	}

	public void delegateTask(Task task, Branch fromBranch, Branch toBranch) throws IllegalArgumentException { // TODO allow for re-delegation of tasks
		// Delegation came back to the original branch
		if (toBranch == fromBranch)
		{
			task.delegate(task);
			delegations.remove(getToDelegationContainingTask(task).get());
		} else // A new delegation
		{
			buffer.add(new Delegation(task, fromBranch, toBranch));
			
			if (!bufferMode)
			{
				executeBuffer();
			}
		}
	}

	public void delegateAccept(Task newTask, Branch fromBranch,
			Branch toBranch) {
		delegations.add(new Delegation(newTask, fromBranch, toBranch));
	}

	public void executeBuffer(){
		// For every delegation ready in the buffer
		while (!buffer.isEmpty()){
			Delegation deleg = buffer.poll();						// Next delegation to commit (a TO request)
			Task task = deleg.getTask();							// Task to delegate
			Branch fromBranch = deleg.getOriginalBranch();			// Branch to delegate FROM
			Branch toBranch = deleg.getBranch();					// Branch to delegate TO

			Task originalTask = task.getOriginalDelegatedTask();	// The original task (this task has been delegated before) or null
			Optional<Delegation> optionalOriginalDelegation = getFromDelegationContainingTask(originalTask);	// Delegation made to this delegator (a FROM request)
			// A. If this task was already a delegated task
			if (optionalOriginalDelegation.isPresent())
			{
				Delegation originalDelegation = optionalOriginalDelegation.get();
				// A.e The previous delegation does not lead here
				if (originalDelegation.getBranch() != fromBranch){
					throw new IllegalStateException("The original branch to delegate from is not equal to the one in the delegations sytem.");
				}
				
				// A.1 Remove previous delegation information
				delegations.remove(originalDelegation);
				fromBranch.removeDelegatedTask(task);
				
				// A.2 (re-)delegate the original task in its respective delegator
				originalDelegation.getOriginalBranch().delegateTask(task, toBranch);
			}
			// B. If this task hasn't been delegated before
			else {
				delegations.add(deleg);
				Task newTask = toBranch.delegateAccept(task, fromBranch);
				task.delegate(newTask);
			}
		}

	}

	public Optional<Delegation> getToDelegationContainingTask(Task task){
		if (task == null){
			return Optional.empty();
		}
		List<Delegation> simDelegationOfTask = buffer.stream().filter(d -> d.getTask() == task).collect(Collectors.toList());
		switch (simDelegationOfTask.size()) {
		case 1 : return Optional.of(simDelegationOfTask.get(0));
		}
		List<Delegation> delegationOfTask = delegations.stream().filter(d -> d.getTask() == task).collect(Collectors.toList());
		switch (delegationOfTask.size()) {
		case 1 : return Optional.of(delegationOfTask.get(0));
		default : return Optional.empty();
		}
	}
	
	public Optional<Delegation> getFromDelegationContainingTask(Task task){
		if (task == null){
			return Optional.empty();
		}
		List<Delegation> delegationOfTask = delegations.stream().filter(d -> d.getTask() == task).collect(Collectors.toList());
		switch (delegationOfTask.size()) {
		case 1 : return Optional.of(delegationOfTask.get(0));
		default : return Optional.empty();
		}
	}

	public void setBufferMode (boolean bool){
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
		Optional<Delegation> delegation = getToDelegationContainingTask(task);
		if(delegation.isPresent()) {
			// TODO verify with toBranch (simulation), delegation.get().getBranch().verifyDelegation();
			return Optional.of(new BranchView(delegation.get().newBranch));
		} else {
			return Optional.empty();
		}
	}
	
	public Optional<Task> getDelegatingTask(Task t) {
		for(Delegation d : delegations) {
			if(d.getOriginalTask() == t) {
				return Optional.of(d.getNewTask());
			}
		}
		return Optional.empty();
	}

	private class Delegation{
		private Task delegatedTask;
		private Branch originalBranch;
		private Branch newBranch;
		private Task newTask;

		private Delegation(Task task,Branch origBranch, Branch newBranch, Task newTask){
			delegatedTask = task;
			originalBranch = origBranch;
			this.newBranch = newBranch;
			this.newTask = newTask;
		}

		public Task getOriginalTask(){
			return delegatedTask;
		}

		public Branch getOriginalBranch(){
			return originalBranch;
		}
		
		public Branch getBranch(){
			return newBranch;
		}
		
		public Task getNewTask(){
			return newTask;
		}
	}

	/**
	 * 
	 * @param plannableTask
	 * @throws IllegalStateException
	 * 			| when plannableTask isn't delegated 
	 */
	@Override
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
}
