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
	
	// A list of availabilities
	private List<AvailabilityPeriod> availabilityPeriodList;
	
	// A list of (active) reservations
	private List<Reservation> activeReservations;
	private List<Reservation> allReservations;
	
	public ResourceManager() {
		this.resPools = new ArrayList<>();
		this.availabilityPeriodList = new ArrayList<>();
		this.userList = new ArrayList<>();
		userList.add(new ProjectManager("admin"));
	}
	
	public boolean createResourcePrototype(String resourceName,
			List<Integer> requirements, List<Integer> conflicts,
			Integer availabilityIndex) {
		// No nulls
		if(resourceName == null || requirements == null
				|| conflicts == null) {
			return false;
		}
		// Requirement cannot be a conflict
		for(Integer requirement : requirements) {
			if(conflicts.contains(requirement)) {
				return false;
			}
		}
		// If daily available, DailyAvailability must exist
		if(availabilityIndex != null && (availabilityIndex < 0 || availabilityIndex > availabilityPeriodList.size())) {
			return false;
		}
		
		// Create resprot (should happen before applying conflicting resources,
		// since a resource can conflict with itself
		boolean success = false;
		ResourcePrototype resprot = null;
		if(availabilityIndex == null) {
			resprot = new ResourcePrototype(resourceName, new ArrayList<ResourcePrototype>(), new ArrayList<ResourcePrototype>(), null);
		} else {
			resprot = new ResourcePrototype(resourceName, new ArrayList<ResourcePrototype>(), new ArrayList<ResourcePrototype>(), availabilityPeriodList.get(availabilityIndex));
		}
		success = addResourceType(resprot);
		if(!success) { return false; }
		
		// Build reqList and conList
		List<ResourcePrototype> reqList = new ArrayList<>();
		List<ResourcePrototype> conList = new ArrayList<>();
		for(Integer requirement : requirements) {
			reqList.add(resPools.get(requirement).getPrototype());
		}
		for(Integer conflict : conflicts) {
			conList.add(resPools.get(conflict).getPrototype());
		}
		resprot.putConflictingResources(conList);
		resprot.putRequiredResources(reqList);
		return true;
	}
	
	public boolean declareAvailabilityPeriod(LocalTime startTime, LocalTime endTime) {
		if(startTime == null || endTime == null) {
			return false;
		}
		if(endTime.isBefore(startTime)) {
			return false;
		}
		return availabilityPeriodList.add(new AvailabilityPeriod(startTime, endTime));
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
	
	//TODO idem zie onder, maar RAW request
	private boolean createRawReservation(int resourceTypeIndex, int concreteResourceIndex, Task reservingTask,
			LocalDateTime startTime, LocalDateTime endTime, LocalDateTime currentTime) {
		return createNewReservation((ConcreteResource) resPools.get(resourceTypeIndex).getConcreteResourceByIndex(concreteResourceIndex), reservingTask, startTime, endTime, currentTime);
	}
	
	//TODO enkel resMan gaat dit kunnen doen. Er wordt een REQUEST naar hem gestuurd en hij CREATE een reservatie
	private boolean createNewReservation(ConcreteResource reservedResource, Task reservingTask, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime currentTime) {
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

	public boolean createRawReservation(int resource, int project, int task,
			LocalDateTime startTime, LocalDateTime endTime) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public ImmutableList<ResourceView> getPossibleResourceInstances(ResourceView resourceType){
		return null; //TODO implement
	}

}