package company.taskMan.task;

import java.time.LocalDateTime;

import company.taskMan.util.TimeSpan;

public class TaskDelegationProxy implements Dependant {
	
	Task task;
	TaskDelegationProxy other;
	private boolean hasUnfinishedPrereqs;
	
	public TaskDelegationProxy(Task t) {
		this.task = t;
		hasUnfinishedPrereqs = true;
	}
	
	public void link(TaskDelegationProxy other) {
		this.other = other;
		other.link(this);
	}
	
	private void updateProxyTaskFinished(LocalDateTime endTime) {
		task.finish(endTime);
	}
	
	private void updateProxyPrereqsFinished() {
		hasUnfinishedPrereqs = false;;
	}
	
	public boolean hasUnfinishedPrereqs() {
		return hasUnfinishedPrereqs;
	}

	@Override
	public void updateDependencyFinished(Task preTask)
			throws IllegalStateException {
		if(preTask == task) {
			// geval proxy in delegating branch krijgt te horen dat de 
			// delegating task is gefinished. Hij laat aan de proxy in
			// de originele branch weten dat de originele task moet notifyen
			// dat hij 'klaar' is
			other.updateProxyTaskFinished(preTask.getEndTime());
		} else {
			// geval proxy in originele branch krijg te horen dat een prereq
			// is gefinished. Als alle prereqs vervuld zijn laat hij aan de
			// andere proxy weten dat hij kan gepland worden.
			for(Task t : task.getPrerequisites()) {
				if(!t.hasFinishedEndpoint()) {
					return;
				}
			}
			hasUnfinishedPrereqs = false;
			other.updateProxyPrereqsFinished();
		}
	}

	@Override
	public TimeSpan getMaxDelayChain() {
		return new TimeSpan(0);
	}

}
