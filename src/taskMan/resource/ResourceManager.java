package taskMan.resource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import taskMan.Task;
import taskMan.resource.user.Developer;
import taskMan.resource.user.ProjectManager;
import taskMan.resource.user.User;
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
	
	// controls indices of resources, prototypes and developers in the system at init 
	private int prototypeIndex, resourceIndex, developerIndex;
	
	public ResourceManager() {
		this.resPools = new ArrayList<>();
//		this.availabilityPeriodList = new ArrayList<>();
		this.userList = new ArrayList<>();
		userList.add(new ProjectManager("admin"));
		prototypeIndex = 0;
		resourceIndex = 0;
		developerIndex = 0;
	}
	
	//TODO kunnen ook lijsten van Strings zijn of zelfs lijsten van ResourceViews
	public boolean createResourcePrototype(String resourceName,
			List<Integer> requirements, List<Integer> conflicts,
			Optional<LocalTime> availabilityStart, Optional<LocalTime> availabilityEnd) {
		
		if(!isValidPeriod(availabilityStart, availabilityEnd)) {
			return false;
		}
		// No nulls
		if(resourceName == null) {
			return false;
		}
		// Requirement cannot be a conflict
		for(Integer requirement : requirements) {
			if(conflicts.contains(requirement)) {
				return false;
			}
		}
		// If daily available, availabilityPeriod must exist
//		if(availabilityIndex != null && (availabilityIndex < 0 || availabilityIndex > availabilityPeriodList.size())) {
//			return false;
//		}
		
		// Create resprot (should happen before applying conflicting resources,
		// since a resource can conflict with itself
		boolean success = false;
		ResourcePrototype resprot = null;
		if(!availabilityStart.isPresent() && !availabilityEnd.isPresent()) {
			resprot = new ResourcePrototype(prototypeIndex, resourceName, null);
//			resprot = new ResourcePrototype(resourceName, new ArrayList<ResourcePrototype>(), new ArrayList<ResourcePrototype>(), null);
		} else {
			resprot = new ResourcePrototype(prototypeIndex, resourceName, new AvailabilityPeriod(availabilityStart.get(), availabilityEnd.get()));
//			resprot = new ResourcePrototype(resourceName, new ArrayList<ResourcePrototype>(), new ArrayList<ResourcePrototype>(), availabilityPeriodList.get(availabilityIndex));
		}
		success = addResourceType(resprot);
		if(!success) {
			return false;
		}
		
//		List<ResourcePrototype> reqList = new ArrayList<ResourcePrototype>();
//		List<ResourcePrototype> conList = new ArrayList<ResourcePrototype>();
		for(Integer requirement : requirements) {
			for(ResourcePool resPool : resPools) {
				if(resPool.getPrototype().isCreationIndex(requirement)) {
					resprot.addRequiredResource(resPool.getPrototype());
				}
			}
		}
		for(Integer conflict : conflicts) {
			for(ResourcePool resPool : resPools) {
				if(resPool.getPrototype().isCreationIndex(conflict)) {
					resprot.addConflictingResource(resPool.getPrototype());
				}
			}
		}
//		resprot.putConflictingResources(conList);
//		resprot.putRequiredResources(reqList);
		prototypeIndex++;
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
	
	public boolean declareConcreteResource(String resName, int typeIndex) {
		ResourcePool resPool = null;
		for(ResourcePool rp : resPools) {
			if(rp.getPrototype().isCreationIndex(typeIndex)) {
				resPool = rp;
			}
		}
		if(resPool == null) {
			return false;
		}
		boolean success = resPool.createResourceInstance(resourceIndex, resName);
		if(success) {
			resourceIndex++;
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
		boolean success = userList.add(new Developer(developerIndex, name));
		if(success) {
			developerIndex++;
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

}