package taskMan.resource;

import java.util.ArrayList;
import java.util.List;

public class ResourceManager {
	
	// The resource manager has a list of resource pools
	private List<ResourcePool> resPools;
	
	public ResourceManager() {
		this.resPools = new ArrayList<>();
	}
	
	public boolean createNewResourceType(String resourceName, ) {
		
	}
	
	private boolean addResourceType(ResourcePrototype resProt) {
		return resPools.add(new ResourcePool(resProt));
	}
	
}
