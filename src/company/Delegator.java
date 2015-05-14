package company;

import java.security.cert.PKIXRevocationChecker.Option;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;




import company.taskMan.TaskMan;
import company.taskMan.task.Task;

public class Delegator {

	List<Delegation> delegations;
	List<Delegation> simulateQueue; 
	
	public Delegator(){
		delegations = new ArrayList<Delegation>();
		simulateQueue = new LinkedList<Delegation>();
	}
	
	public void delegateTask(Task task, TaskMan newB, TaskMan origB) throws IllegalArgumentException {
		if (newB == origB){
			throw new IllegalArgumentException("The branch to delegate to must be different from the original branch.");
		}
		Task originalTask = task.getOriginalDelegatedTask();
		Optional<Delegation> originalDelegation = getDelegationContainingTask(originalTask);
		if (originalDelegation.isPresent()){
			if (originalDelegation.get().getBranch() != origB){
				throw new IllegalArgumentException("The original branch to delegate from is not equal to the one in the delegations sytem.");
			}
			delegations.remove(originalDelegation);
			// The task gets delegated back to it's original branch
			if (originalDelegation.get().getOriginalBranch() == newB){
				task.unDelegate();
			}
			else {
				delegations.add(new Delegation(task,newB,originalDelegation.get().getOriginalBranch()));
				origB.delegateTask(task);
				newB.makeTaskFromDelegation(task);
			}
		}
		else {
			delegations.add(new Delegation(task,newB,origB));
			origB.delegateTask(task);
			newB.makeTaskFromDelegation(task);
		}
		
		
	}
	
	public Optional<Delegation> getDelegationContainingTask(Task task){
		List<Delegation> delegationOfTask = delegations.stream().filter(d -> d.getTask() == task).collect(Collectors.toList());
		switch (delegationOfTask.size()) {
		case 1 : return Optional.of(delegationOfTask.get(0));
		default : return Optional.empty();
		}
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
