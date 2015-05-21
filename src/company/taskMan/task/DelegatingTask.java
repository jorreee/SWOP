package company.taskMan.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import company.taskMan.resource.ResourceManager;
import company.taskMan.resource.ResourceView;

public class DelegatingTask extends Task {
	
	private OriginalTaskProxy proxy;

	public DelegatingTask(String taskDescription, int estimatedDuration,
			int acceptableDeviation, ResourceManager resMan,
			Map<ResourceView, Integer> requiredResources, Task alternativeFor)
			throws IllegalArgumentException {
		super(taskDescription, estimatedDuration, acceptableDeviation, resMan,
				new ArrayList<Task>(), requiredResources, alternativeFor);
	}
	
	public void setProxy(OriginalTaskProxy newProxy) {
		this.proxy = newProxy;
	}
	
	@Override
	protected void notifyFinished() throws IllegalStateException {
		proxy.updateDependencyFinished(this);
	}
	
	@Override
	public boolean canBePlanned() {
		return proxy.canBePlanned();
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
		if(proxy.hasUnfinishedPrerequisites()) {
			prereq.add(this);
		}
		return prereq;
	}

}
