package taskMan.resource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
	private int nextCreationIndex;
	
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
		nextCreationIndex = 0;
	}
	
	//TODO kunnen ook lijsten van Strings zijn of zelfs lijsten van ResourceViews
	public boolean createResourcePrototype(String resourceName,
			List<Integer> requirements, List<Integer> conflicts,
			LocalTime availabilityStart, LocalTime availabilityEnd) {
		
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
		if(availabilityStart == null && availabilityEnd == null) {
			resprot = new ResourcePrototype(resourceName, null, nextCreationIndex);
//			resprot = new ResourcePrototype(resourceName, new ArrayList<ResourcePrototype>(), new ArrayList<ResourcePrototype>(), null);
		} else {
			resprot = new ResourcePrototype(resourceName, new AvailabilityPeriod(availabilityStart, availabilityEnd), nextCreationIndex);
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
		nextCreationIndex++;
		return true;
	}
//	
//	public boolean declareAvailabilityPeriod(LocalTime startTime, LocalTime endTime) {
//		if(!isValidPeriod(startTime,endTime)) {
//			return false;
//		}
//		return availabilityPeriodList.add(new AvailabilityPeriod(startTime, endTime));
//	}
	
	private boolean isValidPeriod(LocalTime start, LocalTime end) {
		if(start == null && end != null) {
			return false;
		}
		if(start != null && end == null) {
			return false;
		}
		if(start == null && end == null) {
			return true;
		}
		return !end.isBefore(start);
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
		return resPool.createResourceInstance(resName);
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
		return userList.add(new Developer(name));
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