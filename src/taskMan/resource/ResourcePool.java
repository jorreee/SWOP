package taskMan.resource;

import java.util.ArrayList;
import java.util.List;

import taskMan.view.ResourceView;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

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
		ConcreteResource conRes = resProt.instantiate(resName);
		return concreteResList.add(conRes);
	}

	public Resource getConcreteResourceByIndex(int concreteResourceIndex) {
		if(concreteResourceIndex < 0 || concreteResourceIndex > concreteResList.size()) {
			return null;
		}
		return concreteResList.get(concreteResourceIndex);
	}
	

	public List<ConcreteResource> getConcreteResourceList(){
		return concreteResList;
	}
	
	public List<ResourceView> getConcreteResourceViewList(){
		Builder<ResourceView> list = ImmutableList.builder();
		for (Resource res : concreteResList){
			list.add(new ResourceView(res));
		}
		return list.build();
	}

	
	public boolean hasAsPrototype(ResourcePrototype prototype) {
		return resProt.equals(prototype);
	}
	
	public int size(){
		return concreteResList.size();
	}


}
