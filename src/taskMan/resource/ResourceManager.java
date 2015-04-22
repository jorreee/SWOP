package taskMan.resource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import taskMan.Task;
import taskMan.resource.user.User;
import taskMan.resource.user.UserCredential;
import taskMan.resource.user.UserPrototype;
import taskMan.view.ResourceView;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * The resource manager is the part in the system that will keep the data about
 * resources, users and reservations. The resource manager is capable of
 * unwrapping resourceViews and linking them with specific resource prototypes
 * or concrete resources. The resource manager will make the reservations and
 * keep them for bookkeeping purposes. The resource manager is also responsible
 * for determining when a task is capable of reserving its resources during
 * planning.
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class ResourceManager {
	
	// The resource manager has a list of resource pools (and users)
	private List<ResourcePool> resPools;
//	private ResourcePool userPool; // TODO REFACTOR TO USE THIS
	private List<User> userList;
	//The prototype for Users
	private UserPrototype userProt;
	
	// A list of (active) reservations
	private List<Reservation> activeReservations;
	private List<Reservation> allReservations;
	
	/**
	 * Instantiate a new ResourceManager. This manager will have no resources
	 * nor reservations. One user will be present in the system, the project
	 * manager (admin).
	 */
	public ResourceManager() {
		this.resPools = new ArrayList<>();
		
		userProt = new UserPrototype("User",null);
		userList.add(userProt.instantiateProjectManager("admin"));
		
//		userPool = new ResourcePool(new UserPrototype("User",null));
//		userPool.createResourceInstance("admin");
		
		activeReservations = new ArrayList<>();
		allReservations = new ArrayList<>();
	}

	/**
	 * Define a new resource type. This will create a new (empty) resource pool
	 * based on the new resource type.
	 * 
	 * @param resourceName
	 *            | the name of the new abstract resource
	 * @param availabilityStart
	 *            | the optional start time of the availability period
	 * @param availabilityEnd
	 *            | the optional end time of the availability period
	 * @return True when the prototype has been successfully initiated, false
	 *         otherwise
	 */
	public boolean createResourcePrototype(String resourceName,
			Optional<LocalTime> availabilityStart, Optional<LocalTime> availabilityEnd) {
		
		if(!isValidPeriod(availabilityStart, availabilityEnd)) {
			return false;
		}
		if(resourceName == null) {
			return false;
		}
		
		// Create resourcePrototype (should happen before applying conflicting resources,
		// since a resource can conflict with itself)
		boolean success = false;
		ResourcePrototype resprot = null;
		if(!availabilityStart.isPresent() && !availabilityEnd.isPresent()) {
			resprot = new ResourcePrototype(resourceName, null);
		} else {
			resprot = new ResourcePrototype(resourceName, new AvailabilityPeriod(availabilityStart.get(), availabilityEnd.get()));
		}
		success = addResourceType(resprot);
		if(!success) {
			return false;
		}
		return true;
	}
	
	/**
	 * Check whether the given availability period start and end times are valid
	 * 
	 * @param start
	 *            | the optional startTime that should be valid
	 * @param end
	 *            | the optional endTime that should be valid
	 * @return True if the given timestamps define a valid availability period
	 */
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
	
	/**
	 * Create a new resourcePool based on a resource prototype
	 * 
	 * @param resProt
	 *            | The resource prototype for which a resource pool should be
	 *            defined
	 * @return true if the new resource pool was successfully added to the
	 *         system
	 */
	private boolean addResourceType(ResourcePrototype resProt) {
		return resPools.add(new ResourcePool(resProt));
	}
	
	/**
	 * Construct a new concrete resource based on a resource prototype
	 * 
	 * @param resName
	 *            | The name for the new concrete resource
	 * @param ptype
	 *            | The prototype for which a new resource should be made
	 * @return True if and only if the new concrete resource was made and added
	 *         to its correct pool
	 */
	public boolean declareConcreteResource(String resName, ResourceView ptype) {
		ResourcePrototype prototype = unWrapResourcePrototypeView(ptype);
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
	
	/**
	 * Get the user who corresponds to the given user
	 * 
	 * @param newUser
	 *            | The user
	 * @return the user who corresponds to the given user
	 */
	public User getUser(ResourceView newUser) {
		if(newUser == null) {
			return null;
		}
		for(User user : userList) {
			if(newUser.hasAsResource(user)) {
				return user;
			}
		}
		return null;
	}
	
	/**
	 * Find the project manager
	 * 
	 * @return the project manager (default user)
	 */
	public User getDefaultUser() {
		for(User user : userList) {
			if(user.hasAsCredential(UserCredential.PROJECTMANAGER)) {
				return user;
			}
		}
		return null;
	}
	
	/**
	 * Retrieve all possible users. This will be a list of every user in the
	 * system.
	 * 
	 * @return an immutable list containing every user in the system
	 */
	public ImmutableList<ResourceView> getPossibleUsernames() {
		Builder<ResourceView> usernames = ImmutableList.builder();
		for(User user : userList) {
			usernames.add(new ResourceView(user));
		}
		return usernames.build();
	}
	
	/**
	 * Define a new developer with a given name in the system.
	 * 
	 * @param name
	 *            | The name of the new developer
	 * @return true if the new developer was added to the system
	 */
	public boolean createDeveloper(String name) {
		if(name == null) {
			return false;
		}
		boolean success = userList.add(userProt.instantiateDeveloper(name));
		if(success) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Reserve a resource from a specific start to a specific end time. A
	 * reservation will always be made by a task.
	 * 
	 * @param resource
	 *            | The resource to reserver
	 * @param reservingTask
	 *            | The task making the reservation
	 * @param startTime
	 *            | The start time of the reservation
	 * @param endTime
	 *            | The end time of the reservation
	 * @return True if the new reservation was made and added to the system
	 */
	public boolean reserve(
			List<ResourceView> resources, 
			Task reservingTask, 
			LocalDateTime startTime, 
			LocalDateTime endTime
			) {
		
		if(resources == null || reservingTask == null ||
				startTime == null || endTime == null) { // || currentTime == null
			return false;
		}
		if(endTime.isBefore(startTime)) {
			return false;
		}
		
		//TODO check of task juiste hoeveelheid van juiste dingen reserveert
		
		List<Reservation> newReservations = new ArrayList<Reservation>();
		
		boolean error = false;
		for(ResourceView resource : resources) {
			ConcreteResource toReserve = null;
			ResourcePrototype r = unWrapResourcePrototypeView(resource);
			if(r != null) {
				toReserve = pickUnreservedResource(r, startTime, endTime);
			} else {
				User user = unWrapUserView(resource);
				if(user != null) {
					toReserve = user;
				} else {
					ConcreteResource cr = unWrapConcreteResourceView(resource);
					if(cr != null) {
						toReserve = cr;
					}
				}
			}
			if(toReserve == null || !canReserve(toReserve,startTime,endTime)) {
				error = true;
				break;
			} else {
				newReservations.add(new Reservation(toReserve, reservingTask, startTime, endTime));
			}
		}
		if(error) {
			return false;
		}
		
		if(!reservingTask.hasEnded()) {
			activeReservations.addAll(newReservations);
		}
		allReservations.addAll(newReservations);
		
		return true;
	}

	/**
	 * A method to find the assigned developers and make a reservation for each
	 * one. The reservation(s) will be made by the reserving task from the given
	 * start time and the given end time.
	 * 
	 * @param devs
	 *            | The developers to assign
	 * @param reservingTask
	 *            | The task reserving the developers
	 * @param start
	 *            | The start time of the new reservations
	 * @param end
	 *            | The end time of the new reservations
	 * @return The list of users for whom the reservation was made, null if there was an error
	 */
	public List<User> pickDevs(List<ResourceView> devs, Task reservingTask, LocalDateTime start, LocalDateTime end) {
		List<User> users = new ArrayList<>();
		for(ResourceView dev : devs) {
			User user = unWrapUserView(dev);
			if(user == null) {
				return null;
			}
			users.add(user);
		}
		boolean success = reserve(devs, reservingTask, start, end);
		if(!success) {
			return null;
		} else {
			return users;
		}
	}
	
	/**
	 * Checks whether the given resource can be reserved from the given start time to the given end time.
	 * 
	 * @param 	resource
	 * 			The resource to check.
	 * @param 	start
	 * 			The start time of the reservation.
	 * @param 	end
	 * 			The end time of the reservation.
	 * @return 	true if the resource can be reserved in the given time slot, else false.
	 */
	private boolean canReserve(ConcreteResource resource, LocalDateTime start, LocalDateTime end) {
		for(Reservation reservation : activeReservations) {
			if(reservation.getReservedResource().equals(resource)) {
				if(reservation.overlaps(start,end)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Picks the Unreserved Resource instances of a given Resource Prototype for the given time period.
	 * @param 	rp
	 * 			The resource prototype.
	 * @param 	start
	 * 			The start time for the reservation.
	 * @param 	end
	 * 			The end time for the reservation.
	 * @return 	A list of Unreserved Concrete Resources of the Prototype. If none were found, return null.
	 */
	private ConcreteResource pickUnreservedResource(ResourcePrototype rp, LocalDateTime start, LocalDateTime end) {
		List<ConcreteResource> options = getPoolOf(rp).getConcreteResourceList();
		for(ConcreteResource cr : options) {
			if(canReserve(cr, start, end)) {
				return cr;
			}
		}
		return null;
	}
	
	/**
	 * Gets the Resource Pool of a given Prototype.
	 * @param 	rp
	 * 			The resource prototype
	 * @return	If the pool exists, return the pool. Else return null.
	 */
	private ResourcePool getPoolOf(ResourcePrototype rp) {
		for(ResourcePool pool : resPools) {
			if(pool.hasAsPrototype(rp)) {
				return pool;
			}
		}
		return null;
	}
	
	/**
	 * This method will return a list of all prototypes present in the resource
	 * pools in this resource manager.
	 * 
	 * @return an immutable list of resource prototypes. For every resource pool
	 *         present in the system, one resource prototype will be added to
	 *         this list
	 */
	public List<ResourceView> getResourcePrototypes() {
		Builder<ResourceView> prototypes = ImmutableList.builder();
		for(ResourcePool pool : resPools) {
			prototypes.add(new ResourceView(pool.getPrototype()));
		}
		return prototypes.build();
	}
	
	/**
	 * This method will return an immutable list of every user managed by the
	 * resource manager that has the DEVELOPER credential
	 * 
	 * @return a list of the developers in the system
	 */
	public List<ResourceView> getDeveloperList() {
		Builder<ResourceView> usernames = ImmutableList.builder();
		for(User user : userList) {
			if(user.hasAsCredential(UserCredential.DEVELOPER)) {
				usernames.add(new ResourceView(user));
			}
		}
		return usernames.build();
	}
	
	/**
	 * This method will find the prototype corresponding to the given
	 * resourceView and return a resourceView of that prototype. If there is no
	 * prototype associated with the given resource, this method will return
	 * null.
	 * 
	 * @param view
	 *            | The resource to find the prototype of
	 * @return a resourceView of the prototype associated with the given
	 *         resource or null if no corresponding prototype was found
	 */
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
	
	/**
	 * This method will locate the resource pool responsible for the given
	 * prototype and return an immutable list of all its concrete resource
	 * instances (wrapped in a resourceView object)
	 * 
	 * @param resourcePrototype
	 *            | The prototype for which the concrete resources are wanted
	 * @return an immutable list of resourceView linked with the concrete
	 *         resources based on the given resource prototype, null if the
	 *         prototype is not associated with any pool
	 */
	public List<ResourceView> getConcreteResourcesForPrototype(ResourceView resourcePrototype) {
		ResourcePrototype rprot = unWrapResourcePrototypeView(resourcePrototype);
		if(rprot == null) {
			Builder<ResourceView> conResList = ImmutableList.builder();
			return conResList.build();
		}

		List<ConcreteResource> concreteRes = getPoolOf(rprot).getConcreteResourceList();
		Builder<ResourceView> conResList = ImmutableList.builder();
		for (ConcreteResource res : concreteRes  ){
			conResList.add(new ResourceView(res));
		}
		return conResList.build();
	}
	
	/**
	 * Unwrap a resourceView to its concrete resource contents. This method will
	 * return null if the resource cannot be found
	 * 
	 * @param view
	 *            | The given concrete resource
	 * @return the concrete resource found in the resourceView, null if it
	 *         cannot be found in the resource manager's resource pools
	 */
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
	
	/**
	 * Unwrap a resourceView to its resource prototype contents. This method
	 * will return null if the resource cannot be found
	 * 
	 * @param view
	 *            | The given resource prototype
	 * @return the resource prototype found in the resourceView, null if it
	 *         cannot be found in the resource manager's resource pools
	 */
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
	
	/**
	 * Unwrap a resourceView to its user contents. This method
	 * will return null if the resource cannot be found
	 * 
	 * @param view
	 *            | The given user
	 * @return the user found in the resourceView, null if it
	 *         cannot be found in the resource manager's user list
	 */
	private User unWrapUserView(ResourceView view){
		if(view == null) {
			return null;
		}
		for(User user : userList) {
			if (view.hasAsResource(user)) {
				return user;
			}
		}
		return null;
	}

	/**
	 * Check whether or not the given resources and their supplied amount exist
	 * in the resource manager's resource pools. Returns the list of Prototypes 
	 * if they are valid. Returns NULL if they aren't
	 * 
	 * @param reqRes
	 *            | A map linking resourcePrototypes with a specified amount
	 * @return True if enough resources exist, false otherwise
	 */

	public Map<ResourcePrototype, Integer> isValidRequiredResources(Map<ResourceView,Integer> reqRes) {
		if(reqRes == null)
			return null;
		Map<ResourcePrototype, Integer> resProtList = new HashMap<ResourcePrototype, Integer>();
		for(ResourceView rv : reqRes.keySet()) {
			ResourcePrototype rp = unWrapResourcePrototypeView(rv);
			if(rp == null) {
				return null;
			}
			int i = reqRes.get(rv);
			if(i <= 0) {
				return null;
			}
			if(i > getPoolOf(rp).size()) {
				return null;
			}
			resProtList.put(rp, reqRes.get(rv));
		}
		return resProtList;
	}

	/**
	 * Check whether a task has active reservations for all of its required
	 * resources
	 * 
	 * @param reservedTask
	 *            | The task for which the reservations should be checked
	 * @param requiredResources
	 *            | The resources and their amounts that should all have enough
	 *            reservations
	 * @return true if every required resource has enough active reservations
	 */
	public boolean hasActiveReservations(Task reservedTask, Map<ResourceView,Integer> requiredResources){
		Map<ResourceView,Integer> checkList = requiredResources;
		for (Reservation res: activeReservations){
			if (res.getReservingTask().equals(reservedTask)){
				for (ResourceView resource : checkList.keySet()){
					if(resource.hasAsResource(res.getReservedResource().getPrototype())){
						checkList.put(resource, checkList.get(resource) - 1);
					}
						
				}
			}
		}
		boolean largerZero = false;
		for (ResourceView resource : checkList.keySet()){
			if(checkList.get(resource) > 0){
				largerZero = true;
			}
		}
		return (!largerZero);
	}
	
	/**
	 * Add resource requirements to a prototype
	 * 
	 * @param reqToAdd
	 *            | The new requirements to add
	 * @param prototype
	 *            | The prototype that the new requirements should be added to
	 * @return True if the new requirements were successfully added to the
	 *         prototype
	 */
	// TODO deze nieuwe reqs zouden ook aan alle concrete resources van het
	// prototype moeten toegevoegd worden
//TODO verwijder activeResources wanneer de task end
	public boolean addRequirementsToResource(List<ResourceView> reqToAdd, ResourceView prototype){
		ResourcePrototype rprot = unWrapResourcePrototypeView(prototype);
		if(rprot == null) {
			return false;
		}
//		for(ResourcePool pool : resPools) {
//			ResourcePrototype prot = pool.getPrototype();
//			if (prototype.hasAsResource(prot)) {
		for (ResourceView req : reqToAdd ){
			ResourcePrototype unwrapReq = unWrapResourcePrototypeView(req);
			if (unwrapReq == null){
				return false;
			} else { 
				rprot.addRequiredResource(unwrapReq);
			}
		}
//				return true;
//			}
//		}
		return false;
	}
	
	/**
	 * Add resource conflicts to a prototype
	 * 
	 * @param conToAdd
	 *            | The new conflicts to add
	 * @param prototype
	 *            | The prototype that the new conflicts should be added to
	 * @return True if the new conflicts were successfully added to the
	 *         prototype
	 */
	// TODO deze nieuwe cons zouden ook aan alle concrete resources van het
	// prototype moeten toegevoegd worden
	public boolean addConflictsToResource(List<ResourceView> conToAdd, ResourceView prototype){
		ResourcePrototype rprot = unWrapResourcePrototypeView(prototype);
		if(rprot == null) {
			return false;
		}
//		for(ResourcePool pool : resPools) {
//			ResourcePrototype prot = pool.getPrototype();
//			if (prototype.hasAsResource(prot)) {
		for (ResourceView req : conToAdd ){
			ResourcePrototype unwrapReq = unWrapResourcePrototypeView(req);
			if (unwrapReq == null){
				return false;
			} else { 
				rprot.addConflictingResource(unwrapReq);
			}
		}
//				return true;
//			}
//		}
		return false;
	}
	
	/**
	 * Return a specific amount of possible starting times for a task. A task
	 * can be planned to start when enough resources are available for the
	 * entire estimated duration of the task.
	 * 
	 * @param task
	 *            | The task wants to be planned
	 * @param allResources
	 *            | The resources that should be available
	 * @param amount
	 *            | The amount of suggestions that should be calculated
	 * @return a list of timestamps when a planning could be made without
	 *         conflicts
	 */
	public List<LocalDateTime> getPossibleStartingTimes(Task task, List<ResourceView> allResources, int amount) {
		List<LocalDateTime> posTimes = new ArrayList<LocalDateTime>();
		//TODO het zware werk
		return posTimes;
	}
	
//	/**
//	 * This method will remove all future reservations for a given
//	 * (finished/failed) task. If there are any active reservations that have
//	 * already started, but not yet finished, a reservation until this point
//	 * will be maintained, but the resources will be made available again (i.e.
//	 * the reservation will end now).
//	 * 
//	 * @param task
//	 *            | The finished or failed task
//	 * @param currentTime
//	 *            | The current time in the system
//	 * @return true if all required changes were successfully made
//	 */
//	public boolean flushFutureReservations(Task task, LocalDateTime currentTime) {
//		boolean succesful = false;
//		for (Reservation res : activeReservations){
//			if (res.getReservingTask().equals(task)){
//				activeReservations.remove(res);
//				succesful = true;
//			}
//		}
//		for (Reservation res : allReservations){
//			if (res.getReservingTask().equals(task)){
//				if(res.getEndTime().isAfter(currentTime)){
//					ConcreteResource reserved = res.getReservedResource();
//					Task resTask = res.getReservingTask();
//					LocalDateTime start = res.getStartTime();
//					allReservations.remove(res);
//					allReservations.add(new Reservation(reserved,resTask,start,currentTime));
//				}
//			}
//		}
//		return succesful;
//	}
	
	/**
	 * An ended task can call this method to release all active reservations made
	 * by this task. 
	 * 
	 * @param task
	 * @return
	 */
	public boolean releaseResources(Task task) {
		if(!task.hasEnded()) {
			return false;
		}
		List<Reservation> toRemove = new ArrayList<Reservation>();
		for(Reservation r : activeReservations) {
			if(r.getReservingTask().equals(task)) {
				toRemove.add(r);
			}
		}
		return activeReservations.removeAll(toRemove);
	}




	/**
	 * Return an immutable list of all the reservations present in the resource
	 * manager
	 * 
	 * @return all reservations
	 */

	public List<Reservation> getAllReservations() {
		Builder<Reservation> reservations = ImmutableList.builder();
		reservations.addAll(allReservations);
		return reservations.build();
	}

}