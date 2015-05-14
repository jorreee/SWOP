package company;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;





import company.taskMan.TaskMan;
import company.taskMan.task.Task;

public class Delegator {

	List<Delegation> delegations;
	LinkedList<Delegation> cache;
	boolean cacheMode;
	
	public Delegator(){
		delegations = new ArrayList<Delegation>();
		cache = new LinkedList<Delegation>();
		cacheMode = false;
	}
	
	public void delegateTask(Task task, TaskMan newB, TaskMan origB) throws IllegalArgumentException {
		if (newB == origB){
			throw new IllegalArgumentException("The branch to delegate to must be different from the original branch.");
		}
		Optional<Delegation> delegationTask = getDelegationContainingTask(task);
		if (delegationTask != null){
			throw new IllegalArgumentException("The task is already delegated.");
		}
		cache.add(new Delegation(task,newB,origB));
		if (!cacheMode){
			executeCache();
		}

	}
	
	public void executeCache(){
		while (!cache.isEmpty()){
			Delegation deleg = cache.removeLast();
			Task task = deleg.getTask();
			TaskMan newB = deleg.getBranch();
			TaskMan origB = deleg.getOriginalBranch();
			
			Task originalTask = task.getOriginalDelegatedTask();
			Optional<Delegation> originalDelegation = getDelegationContainingTask(originalTask);
			if (originalDelegation.isPresent()){
				if (originalDelegation.get().getBranch() != origB){
					throw new IllegalArgumentException("The original branch to delegate from is not equal to the one in the delegations sytem.");
				}
				if (!cache.remove(originalDelegation)){
				delegations.remove(originalDelegation);
				}
				// The task gets delegated back to its original branch
				if (originalDelegation.get().getOriginalBranch() == newB){
					origB.delegateTask(task, task);
				}
				else {
					delegations.add(new Delegation(task,newB,originalDelegation.get().getOriginalBranch()));
					Task newTask = newB.makeTaskFromDelegation(task);
					origB.delegateTask(task, newTask);

				}
			}
			else {
				delegations.add(deleg);
				Task newTask = newB.makeTaskFromDelegation(task);
				origB.delegateTask(task, newTask);
			}
		}
			
		}
		
		
	
	public Optional<Delegation> getDelegationContainingTask(Task task){
		if (task == null){
			return Optional.empty();
		}
		List<Delegation> simDelegationOfTask = cache.stream().filter(d -> d.getTask() == task).collect(Collectors.toList());
		switch (simDelegationOfTask.size()) {
		case 1 : return Optional.of(simDelegationOfTask.get(0));
		}
		List<Delegation> delegationOfTask = delegations.stream().filter(d -> d.getTask() == task).collect(Collectors.toList());
		switch (delegationOfTask.size()) {
		case 1 : return Optional.of(delegationOfTask.get(0));
		default : return Optional.empty();
		}
	}
	
	public void setCacheMode (boolean bool){
		cacheMode = bool;
	}

	private class Delegation{
		private Task delegatedTask;
		private TaskMan newBranch;
		private TaskMan originalBranch;
		
		private Delegation(Task task,TaskMan newB, TaskMan origB){
			delegatedTask = task;
			newBranch = newB;
			originalBranch = origB;
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
