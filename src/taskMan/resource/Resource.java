package taskMan.resource;

import java.util.List;

public abstract class Resource {
	
	private String resName;
	private List<ResourcePrototype> reqResourcesList;
	private List<ResourcePrototype> conResourcesList;
	
	private boolean dailyAvailable;
	
	public Resource(String resourceName, List<ResourcePrototype> requiredResourcesList, List<ResourcePrototype> conflictingResourcesList) {
		this.resName = resourceName;
		this.reqResourcesList = requiredResourcesList;
		this.conResourcesList = conflictingResourcesList;
	}
	
	public String getName() { return resName; }
	public List<ResourcePrototype> getRequiredResources() { return reqResourcesList; }
	public List<ResourcePrototype> getConflictingResources() { return conResourcesList; }
	
	public boolean isDailyAvailable() { return dailyAvailable; }
	
	
}
