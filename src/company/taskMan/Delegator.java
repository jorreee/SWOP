package company.taskMan;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

import company.BranchView;
import company.taskMan.task.Task;

public class Delegator {

	List<Delegation> delegationsToBranch;
	List<Delegation> delegationsFromBranch;
	Queue<Delegation> buffer;
	boolean bufferMode;

	public Delegator(){
		delegationsFromBranch = new ArrayList<Delegation>();
		delegationsToBranch = new ArrayList<Delegation>();
		buffer = new LinkedList<Delegation>();
		bufferMode = false;
	}

	public void delegateTask(Task task, TaskMan toBranch, TaskMan fromBranch) throws IllegalArgumentException { // TODO allow for re-delegation of tasks
		// Delegation came back to the original branch
		if (toBranch == fromBranch)
		{
			task.delegate(task);
			delegationsToBranch.remove(getToDelegationContainingTask(task).get());
		} else // A new delegation
		{
			buffer.add(new Delegation(task, toBranch, fromBranch));
			
			if (!bufferMode)
			{
				executeBuffer();
			}
		}
	}

	public void delegateAccept(Task newTask, TaskMan toBranch,
			TaskMan fromBranch) {
		delegationsFromBranch.add(new Delegation(newTask, toBranch, fromBranch));
	}

	public void executeBuffer(){
		// For every delegation ready in the buffer
		while (!buffer.isEmpty()){
			Delegation deleg = buffer.poll();						// Next delegation to commit (a TO request)
			Task task = deleg.getTask();							// Task to delegate
			TaskMan toBranch = deleg.getBranch();					// Branch to delegate TO
			TaskMan fromBranch = deleg.getOriginalBranch();			// Branch to delegate FROM

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
				delegationsFromBranch.remove(originalDelegation);
				fromBranch.removeDelegatedTask(task);
				
				// A.2 (re-)delegate the original task in its respective delegator
				originalDelegation.getOriginalBranch().delegateTask(task, toBranch);
			}
			// B. If this task hasn't been delegated before
			else {
				delegationsToBranch.add(deleg);
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
		List<Delegation> delegationOfTask = delegationsToBranch.stream().filter(d -> d.getTask() == task).collect(Collectors.toList());
		switch (delegationOfTask.size()) {
		case 1 : return Optional.of(delegationOfTask.get(0));
		default : return Optional.empty();
		}
	}
	
	public Optional<Delegation> getFromDelegationContainingTask(Task task){
		if (task == null){
			return Optional.empty();
		}
		List<Delegation> delegationOfTask = delegationsFromBranch.stream().filter(d -> d.getTask() == task).collect(Collectors.toList());
		switch (delegationOfTask.size()) {
		case 1 : return Optional.of(delegationOfTask.get(0));
		default : return Optional.empty();
		}
	}


	public void setBufferMode (boolean bool){
		bufferMode = bool;
		if(!bufferMode) {
			executeBuffer();
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

	private class Delegation{
		private Task delegatedTask;
		private TaskMan newBranch;
		private TaskMan originalBranch;

		private Delegation(Task task,TaskMan toBranch, TaskMan fromBranch){
			delegatedTask = task;
			newBranch = toBranch;
			originalBranch = fromBranch;
		}

		public Task getTask(){
			return delegatedTask;
		}

		public TaskMan getBranch(){
			return newBranch;
		}

		public TaskMan getOriginalBranch(){
			return originalBranch;
		}
	}
}
