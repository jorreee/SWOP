package taskMan.resource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import taskMan.Task;
import taskMan.resource.user.Developer;
import taskMan.resource.user.ProjectManager;
import taskMan.resource.user.User;
import taskMan.resource.user.UserCredential;
import taskMan.view.ResourceView;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class ResourceManager {
	
	// The resource manager has a list of resource pools (and users)
	private List<ResourcePool> resPools;
	private List<User> userList;
	
//	// A list of availabilities
//	private List<AvailabilityPeriod> availabilityPeriodList;
	
	// A list of (active) reservations
	private List<Reservation> activeReservations;
	private List<Reservation> allReservations;
	
	public ResourceManager() {
		this.resPools = new ArrayList<>();
//		this.availabilityPeriodList = new ArrayList<>();
		this.userList = new ArrayList<>();
		userList.add(new ProjectManager("admin"));
	}
	
	//TODO kunnen ook lijsten van Strings zijn of zelfs lijsten van ResourceViews
	public boolean createResourcePrototype(String resourceName,
			Optional<LocalTime> availabilityStart, Optional<LocalTime> availabilityEnd) {
		
		if(!isValidPeriod(availabilityStart, availabilityEnd)) {
			return false;
		}
		// No nulls
		if(resourceName == null) {
			return false;
		}
		// Requirement cannot be a conflict
//		for(Integer requirement : requirements) {
//			if(conflicts.contains(requirement)) {
//				return false;
//			}
//		}
		// If daily available, availabilityPeriod must exist
//		if(availabilityIndex != null && (availabilityIndex < 0 || availabilityIndex > availabilityPeriodList.size())) {
//			return false;
//		}
		
		// Create resprot (should happen before applying conflicting resources,
		// since a resource can conflict with itself
		boolean success = false;
		ResourcePrototype resprot = null;
		if(!availabilityStart.isPresent() && !availabilityEnd.isPresent()) {
			resprot = new ResourcePrototype(resourceName, null);
//			resprot = new ResourcePrototype(resourceName, new ArrayList<ResourcePrototype>(), new ArrayList<ResourcePrototype>(), null);
		} else {
			resprot = new ResourcePrototype(resourceName, new AvailabilityPeriod(availabilityStart.get(), availabilityEnd.get()));
//			resprot = new ResourcePrototype(resourceName, new ArrayList<ResourcePrototype>(), new ArrayList<ResourcePrototype>(), availabilityPeriodList.get(availabilityIndex));
		}
		success = addResourceType(resprot);
		if(!success) {
			return false;
		}
		
//		List<ResourcePrototype> reqList = new ArrayList<ResourcePrototype>();
//		List<ResourcePrototype> conList = new ArrayList<ResourcePrototype>();
//		for(Integer requirement : requirements) {
//			for(ResourcePool resPool : resPools) {
//				if(resPool.getPrototype().isCreationIndex(requirement)) {
//					resprot.addRequiredResource(resPool.getPrototype());
//				}
//			}
//		}
//		for(Integer conflict : conflicts) {
//			for(ResourcePool resPool : resPools) {
//				if(resPool.getPrototype().isCreationIndex(conflict)) {
//					resprot.addConflictingResource(resPool.getPrototype());
//				}
//			}
//		}
//		resprot.putConflictingResources(conList);
//		resprot.putRequiredResources(reqList);
//		prototypeIndex++;
		return true;
	}
//	
//	public boolean declareAvailabilityPeriod(LocalTime startTime, LocalTime endTime) {
//		if(!isValidPeriod(startTime,endTime)) {
//			return false;
//		}
//		return availabilityPeriodList.add(new AvailabilityPeriod(startTime, endTime));
//	}
	
	private boolean isValidPeriod(Optional<LocalTime> start, Optional<LocalTime> end) {
		if(start.isPresent() && !end.isPresent()) {
			return false;
		}
		if(!start.isPresent() && end.isPresent()) {
			return false;
		}
		if(!start.isPresent() && !end.isPresent()) {
			return true;
		}
		return !end.get().isBefore(start.get());
	}
	
	private boolean addResourceType(ResourcePrototype resProt) {
		return resPools.add(new ResourcePool(resProt));
	}
	
	public boolean declareConcreteResource(String resName, ResourceView prototype) {
		ResourcePool resPool = null;
		for(ResourcePool rp : resPools) {
			if(rp.hasAsPrototype(prototype)) {
				resPool = rp;
			}
		}
		if(resPool == null) {
			return false;
		}
		boolean success = resPool.createResourceInstance(resName);
		if(success) {
			return true;
		} else {
			return false;
		}
	}
	
	public User getUser(String username) {
		if(username == null) {
			return null;
		}
		for(User user : userList) {
			if(user.getName().equalsIgnoreCase(username)) {
				return user;
			}
		}
		return null;
	}
	
	public User getDefaultUser() {
		return getUser("admin");
	}
	
	public ImmutableList<ResourceView> getPossibleUsernames() {
		Builder<ResourceView> usernames = ImmutableList.builder();
		for(User user : userList) {
			usernames.add(new ResourceView(user));
		}
		return usernames.build();
	}
	
	public boolean createDeveloper(String name) {
		if(name == null) {
			return false;
		}
		boolean success = userList.add(new Developer(name));
		if(success) {
			return true;
		} else {
			return false;
		}
	}
	
//	private boolean createRawReservation(
//			int resourceTypeIndex, 
//			int concreteResourceIndex, 
//			Task reservingTask,
//			LocalDateTime startTime, 
//			LocalDateTime endTime, 
//			LocalDateTime currentTime) {
//		return reserve(resPools.get(resourceTypeIndex).getConcreteResourceByIndex(concreteResourceIndex), reservingTask, startTime, endTime, currentTime);
//	}
	
	public boolean reserve(
			ConcreteResource reservedResource, 
			Task reservingTask, 
			LocalDateTime startTime, 
			LocalDateTime endTime, 
			LocalDateTime currentTime) {
		
		if(reservedResource == null || reservingTask == null ||
				startTime == null || endTime == null || currentTime == null) {
			return false;
		}
		if(endTime.isBefore(startTime)) {
			return false;
		}
		// Reservation is no longer active
		if(currentTime.isAfter(endTime) || reservingTask.hasEnded()) {
			return allReservations.add(new Reservation(reservedResource, reservingTask, startTime, endTime));
		}
		// Active task
		else {
			Reservation newReservation = new Reservation(reservedResource, reservingTask, startTime, endTime);
			boolean firstAddSuccess = activeReservations.add(newReservation);
			if(!firstAddSuccess) { return false; }
			boolean secondAddSuccess = allReservations.add(newReservation);
			if(!secondAddSuccess) { activeReservations.remove(newReservation); return false; }
			else return true;
		}
	}

//	public boolean createRawReservation(int resource, int project, int task,
//			LocalDateTime startTime, LocalDateTime endTime) {
//		// TODO Auto-generated method stub
//		return false;
//	}
	
	
	public ImmutableList<ResourceView> getPossibleResourceInstances(ResourceView resourceType){
		return null; //TODO implement
	}

	public List<ResourceView> getResourcePrototypes() {
		Builder<ResourceView> prototypes = ImmutableList.builder();
		for(ResourcePool pool : resPools) {
			prototypes.add(new ResourceView(pool.getPrototype()));
		}
		return prototypes.build();
	}

	public List<ResourceView> getDeveloperList() {
		Builder<ResourceView> usernames = ImmutableList.builder();
		for(User user : userList) {
			if(user.hasAsCredential(UserCredential.DEVELOPER)) {
				usernames.add(new ResourceView(user));
			}
		}
		return usernames.build();
	}
	
	public ResourceView getPrototypeOf(ResourceView view){
		for(ResourcePool pool : resPools) {
			for (Resource res : pool.getConcreteResourceList()){
				if (view.hasAsResource(res)){
					return new ResourceView(pool.getPrototype());
				}
			}
		}
		return null;
	}
	
	public List<ResourceView> getConcreteResourcesForPrototype(ResourceView resourcePrototype) {
		for(ResourcePool pool : resPools) {
			if (resourcePrototype.hasAsResource(pool.getPrototype())) {
				return pool.getConcreteResourceViewList();
			}
		}
		return null;
	}
	
	@Deprecated
	private Resource unwrapResourceView(ResourceView view) {
		if(view == null) {
			return null;
		}
		for(ResourcePool pool : resPools) {
			if (view.hasAsResource(pool.getPrototype())) {
				return pool.getPrototype();
			}
			else {
				for (Resource res : pool.getConcreteResourceList()){
					if (view.hasAsResource(res)){
						return res;
					}
				}
			}
		}
		return null;
	}
	
	private ConcreteResource unWrapConcreteResourceView(ResourceView view){
		if(view == null) {
			return null;
		}
		for(ResourcePool pool : resPools) {
				for (ConcreteResource res : pool.getConcreteResourceList()){
					if (view.hasAsResource(res)){
						return res;
					}
				}
		}
		return null;
	}
	
	private ResourcePrototype unWrapResourcePrototypeView(ResourceView view){
		if(view == null) {
			return null;
		}
		for(ResourcePool pool : resPools) {
			if (view.hasAsResource(pool.getPrototype())) {
				return pool.getPrototype();
			}
		}
		return null;
	}
	
	public boolean hasReservations(Task reservedTask, Map<Resource,Integer> requiredResources){
		// TODO nog te maken
		return false;
	}
	
	public boolean addRequirementsToResource(List<ResourceView> reqToAdd, ResourceView prototype){
		for(ResourcePool pool : resPools) {
			ResourcePrototype prot = pool.getPrototype();
			if (prototype.hasAsResource(prot)) {
				for (ResourceView req : reqToAdd ){
					prot.addRequiredResource(unWrapResourcePrototypeView(req));
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean addConflictsToResource(List<ResourceView> conToAdd, ResourceView prototype){
		for(ResourcePool pool : resPools) {
			ResourcePrototype prot = pool.getPrototype();
			if (prototype.hasAsResource(prot)) {
				for (ResourceView conflict : conToAdd ){
					prot.addConflictingResource(unWrapResourcePrototypeView(conflict));
				}
				return true;
			}
		}
		return false;
	}

}