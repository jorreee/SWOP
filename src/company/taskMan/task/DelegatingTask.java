package company.taskMan.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import company.taskMan.resource.ResourceManager;
import company.taskMan.resource.ResourceView;

public class DelegatingTask extends Task {
	
	private final TaskDelegationProxy proxy;

	public DelegatingTask(String taskDescription, int estimatedDuration,
			int acceptableDeviation, ResourceManager resMan,
			Map<ResourceView, Integer> requiredResources, Task alternativeFor, TaskDelegationProxy proxy)
			throws IllegalArgumentException {
		super(taskDescription, estimatedDuration, acceptableDeviation, resMan,
				new ArrayList<Task>(), requiredResources, alternativeFor);
		this.proxy = proxy;
	}
	
	@Override 
	public boolean hasFinishedEndpoint() {
		return false;
	}
	
	/**
	 * Returns prerequisites based on the remote proxy ONLY for the 
	 * purpose of making this task available. 
	 * If the proxy has unfinished prerequisites the returned list 
	 * will contain THIS, and THIS will always return false for 
	 * hasFinishedEndpoint().
	 * If the proxy has no more unfinished prerequisites this method
	 * will return an empty list, thus giving the idea that there are 
	 * 'no unfinished prerequisites'. 
	 */
	@Override
	public List<Task> getPrerequisites() {
		List<Task> prereq = new ArrayList<Task>();
		if(proxy.hasUnfinishedPrereqs()) {
			prereq.add(this);
		}
		return prereq;
	}

}
