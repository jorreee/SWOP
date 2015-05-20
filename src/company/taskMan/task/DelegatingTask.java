package company.taskMan.task;

import java.util.ArrayList;
import java.util.Map;

import company.taskMan.resource.ResourceManager;
import company.taskMan.resource.ResourceView;

public class DelegatingTask extends Task {
	
	private final OriginalTaskProxy proxy;

	public DelegatingTask(String taskDescription, int estimatedDuration,
			int acceptableDeviation, ResourceManager resMan,
			Map<ResourceView, Integer> requiredResources, Task alternativeFor, OriginalTaskProxy proxy)
			throws IllegalArgumentException {
		super(taskDescription, estimatedDuration, acceptableDeviation, resMan,
				new ArrayList<Task>(), requiredResources, alternativeFor);
		this.proxy = proxy;
	}
	
	@Override
	public boolean canBePlanned() {
		return proxy.canBePlanned();
	}

}
