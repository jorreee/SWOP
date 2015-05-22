package company.taskMan.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import company.taskMan.resource.ResourceManager;
import company.taskMan.resource.ResourceView;

/**
 * This task is a specialized task for delegating tasks
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class DelegatingTask extends Task {
	
	private OriginalTaskProxy proxy;

	public DelegatingTask(String taskDescription, int estimatedDuration,
			int acceptableDeviation, ResourceManager resMan,
			Map<ResourceView, Integer> requiredResources, Task alternativeFor)
			throws IllegalArgumentException {
		super(taskDescription, estimatedDuration, acceptableDeviation, resMan,
				new ArrayList<Task>(), requiredResources, alternativeFor);
	}
	
	/**
	 * Set the proxy this task should notify when it finished
	 * 
	 * @param newProxy
	 *            | The new proxy linked to this task
	 */
	public void setProxy(OriginalTaskProxy newProxy) {
		this.proxy = newProxy;
	}
	
	/**
	 * Notifies the OriginalTaskProxy that this delegating task has finished
	 * @throws IllegalStateException
	 * 			| if this task isn't linked to the correct OriginalTaskProxy
	 */
	@Override
	protected void notifyFinished() throws IllegalStateException {
		proxy.updateDependencyFinished(this);
	}
	
	/**
	 * Returns whether this task can be planned. This method will ask proxy
	 * for this information.
	 */
	@Override
	public boolean canBePlanned() {
		return proxy.canBePlanned();
	}

	/**
	 * Return whether this task has a finished endpoint. This method will always
	 * return false.
	 * @return false
	 */
	@Override 
	public boolean hasFinishedEndpoint() {
		return false;
	}
	
	/**
	 * Returns prerequisites based on the remote proxy ONLY for the purpose of
	 * making this task available. If the proxy has unfinished prerequisites the
	 * returned list will contain THIS, and THIS will always return false for
	 * hasFinishedEndpoint(). If the proxy has no more unfinished prerequisites
	 * this method will return an empty list, thus giving the idea that there
	 * are 'no unfinished prerequisites'.
	 * 
	 * @return a reference to this task in a list if the proxy has unfinished
	 *         prerequisites reported from the linked branch
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
