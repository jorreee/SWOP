package taskMan.resource;

import java.util.ArrayList;
import java.util.List;

public class ResourcePool {
	
	// The prototype
	private final ResourcePrototype resProt;
	
	// A list of concreteResourceInstances
	private List<ConcreteResource> concreteResList;
	
	public ResourcePool(ResourcePrototype resPrototype) {
		this.resProt = resPrototype;
		this.concreteResList = new ArrayList<>();
	}

	public ResourcePrototype getPrototype() {
		return resProt;
	}

	public boolean createResourceInstance(String resName) {
		ConcreteResource conRes = resProt.clone();
		conRes.setName(resName);
		return concreteResList.add(conRes);
	}

	public Resource getConcreteResourceByIndex(int concreteResourceIndex) {
		if(concreteResourceIndex < 0 || concreteResourceIndex > concreteResList.size()) {
			return null;
		}
		return concreteResList.get(concreteResourceIndex);
	}

}
