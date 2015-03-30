package taskMan.resource;

import java.util.ArrayList;
import java.util.List;

public class ResourceManager {
	
	// The resource manager has a list of resource pools
	private List<ResourcePool> resPools;
	
	public ResourceManager() {
		this.resPools = new ArrayList<>();
	}
	
	public boolean addResourceType() {
		return resPools.add(new ResourcePool());
	}
	
}
