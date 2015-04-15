package taskMan.resource;

import java.util.ArrayList;
import java.util.List;

import taskMan.view.ResourceView;

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

	public boolean createResourceInstance(int creationIndex, String resName) {
		ConcreteResource conRes = resProt.instantiate(creationIndex, resName);
		return concreteResList.add(conRes);
	}

	public Resource getConcreteResourceByIndex(int concreteResourceIndex) {
		if(concreteResourceIndex < 0 || concreteResourceIndex > concreteResList.size()) {
			return null;
		}
		return concreteResList.get(concreteResourceIndex);
	}
	
	public List<? extends Resource> getConcreteResourceList(){
		return concreteResList;
	}
	
	public List<ResourceView> getConcreteResourceViewList(){
		ArrayList<ResourceView> list = new ArrayList<ResourceView>();
		for (Resource res : concreteResList){
			list.add(new ResourceView(res));
		}
		return list;
	}

}
