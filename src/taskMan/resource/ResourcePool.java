package taskMan.resource;

import java.util.ArrayList;
import java.util.List;

import taskMan.view.ResourceView;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * A resource pool is a collection of concrete resources based on one specific
 * resource prototype
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class ResourcePool {
	
	// The prototype
	private final ResourcePrototype resProt;
	
	// A list of concreteResourceInstances
	private List<ConcreteResource> concreteResList;
	
	/**
	 * Construct a new resource pool with no concrete resources, based on a
	 * resource prototype
	 * 
	 * @param resPrototype
	 *            | The prototype that will be used to create concrete resources
	 *            for this resource type
	 */
	public ResourcePool(ResourcePrototype resPrototype) {
		this.resProt = resPrototype;
		this.concreteResList = new ArrayList<>();
	}

	/**
	 * A getter for the prototype of this resource pool
	 * 
	 * @return the resource pool's prototype
	 */
	public ResourcePrototype getPrototype() {
		return resProt;
	}

	/**
	 * Construct a new concrete resource in this pool with a given name
	 * 
	 * @param resName
	 *            | The name of the new resource instance
	 * @return True if the new resource was successfully created and added to
	 *         the pool, false otherwise
	 */
	public boolean createResourceInstance(String resName) {
		ConcreteResource conRes = resProt.instantiate(resName);
		return concreteResList.add(conRes);
	}

	/**
	 * Return a list of concrete resources in the pool
	 * 
	 * @return an immutable list of concrete resource in the pool
	 */
	public List<ConcreteResource> getConcreteResourceList(){
		return concreteResList;
	}
	
	/**
	 * Return a list of resourceViews based of all the concrete resources in the
	 * pool
	 * 
	 * @return an immutable list of resourceViews, one for each concrete
	 *         resource in the pool
	 */
	public List<ResourceView> getConcreteResourceViewList(){
		Builder<ResourceView> list = ImmutableList.builder();
		for (Resource res : concreteResList){
			list.add(new ResourceView(res));
		}
		return list.build();
	}
	
	/**
	 * Check whether this resource pool is based on the given resource prototype
	 * 
	 * @param prototype
	 *            | The prototype to compare with
	 * @return True if this is the resource pool containing the prototype, False
	 *         otherwise
	 */
	public boolean hasAsPrototype(ResourcePrototype prototype) {
		return resProt.equals(prototype);
	}
	
	/**
	 * Determine the size of the pool
	 * 
	 * @return the amount of the concrete resources in this pool
	 */
	public int size(){
		return concreteResList.size();
	}

}
