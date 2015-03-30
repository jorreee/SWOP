package taskMan.resource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import taskMan.Task;
import taskMan.user.Developer;
import taskMan.user.ProjectManager;
import taskMan.user.User;

public class ResourceManager {
	
	// The resource manager has a list of resource pools (and users)
	private List<ResourcePool> resPools;
	private List<User> userList;
	
	// A list of availabilities
	private List<DailyAvailability> dailyAvailabilityList;
	
	// A list of (active) reservations
	private List<Reservation> activeReservations;
	private List<Reservation> allReservations;
	
	public ResourceManager() {
		this.resPools = new ArrayList<>();
		this.dailyAvailabilityList = new ArrayList<>();
		this.userList = new ArrayList<>();
		userList.add(new ProjectManager());
	}
	
	public boolean createResourcePrototype(String resourceName,
			List<Integer> requirements, List<Integer> conflicts,
			Integer availabilityIndex) {
		// No nulls
		if(resourceName == null || requirements == null
				|| conflicts == null || availabilityIndex == null) {
			return false;
		}
		// Requirement cannot be a conflict
		for(Integer requirement : requirements) {
			if(conflicts.contains(requirement)) {
				return false;
			}
		}
		// DailyAvailability must exist
		if(availabilityIndex < 0 || availabilityIndex > dailyAvailabilityList.size()) {
			return false;
		}
		
		// Build reqList and conList
		List<ResourcePrototype> reqList = new ArrayList<>();
		List<ResourcePrototype> conList = new ArrayList<>();
		for(Integer requirement : requirements) {
			reqList.add(resPools.get(requirement).getPrototype());
		}
		for(Integer conflict : conflicts) {
			reqList.add(resPools.get(conflict).getPrototype());
		}
		return addResourceType(new ResourcePrototype(resourceName, reqList, conList, dailyAvailabilityList.get(availabilityIndex)));
	}
	
	public boolean declareDailyAvailability(LocalTime startTime, LocalTime endTime) {
		if(startTime == null || endTime == null) {
			return false;
		}
		if(endTime.isBefore(startTime)) {
			return false;
		}
		return dailyAvailabilityList.add(new DailyAvailability(startTime, endTime));
	}
	
	private boolean addResourceType(ResourcePrototype resProt) {
		return resPools.add(new ResourcePool(resProt));
	}
	
	public boolean createRawResource(String resName, int typeIndex) {
		return resPools.get(typeIndex).createResourceInstance(resName);
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
	
	public List<String> getPossibleUsernames() {
		List<String> usernames = new ArrayList<>();
		for(User user : userList) {
			usernames.add(user.getName());
		}
		return usernames;
	}
	
	public boolean createDeveloper(String name) {
		if(name == null) {
			return false;
		}
		return userList.add(new Developer(name));
	}
	
	public boolean createRawReservation(int resourceTypeIndex, int concreteResourceIndex, Task reservingTask,
			LocalDateTime startTime, LocalDateTime endTime, LocalDateTime currentTime) {
		return createNewReservation(resPools.get(resourceTypeIndex).getConcreteResourceByIndex(concreteResourceIndex), reservingTask, startTime, endTime, currentTime);
	}
	
	public boolean createNewReservation(Resource reservedResource, Task reservingTask, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime currentTime) {
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

}

class DailyAvailability {
	LocalTime startTime;
	LocalTime endTime;
	
	public DailyAvailability(LocalTime startTime, LocalTime endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public LocalTime getStartTime() { return startTime; }
	public LocalTime getEndTime() { return endTime; }
}