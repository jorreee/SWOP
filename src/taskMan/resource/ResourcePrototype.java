package taskMan.resource;

import java.util.List;

public class ResourcePrototype implements Resource {
	
	public ResourcePrototype(String name, List<ResourcePrototype> requiredResources, List<ResourcePrototype> conflictingResources) {
		
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ResourcePrototype> getRequiredResources() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ResourcePrototype> getConflictingResources() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDailyAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

}
