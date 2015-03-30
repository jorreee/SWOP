package taskMan.resource;

import java.util.List;

public abstract class Resource {
	
	protected String resName;
	protected List<ResourcePrototype> reqResourcesList;
	protected List<ResourcePrototype> conResourcesList;
	
	protected boolean dailyAvailable;
	
	public Resource(String resourceName, List<ResourcePrototype> requiredResourcesList, List<ResourcePrototype> conflictingResourcesList, boolean dailyAvailable) {
		this.resName = resourceName;
		this.reqResourcesList = requiredResourcesList;
		this.conResourcesList = conflictingResourcesList;
		this.dailyAvailable = dailyAvailable;
	}
	
	public String getName() { return resName; }
	public List<ResourcePrototype> getRequiredResources() { return reqResourcesList; }
	public List<ResourcePrototype> getConflictingResources() { return conResourcesList; }
	
	public boolean isDailyAvailable() { return dailyAvailable; }
	
	
}
