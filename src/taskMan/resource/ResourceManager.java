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
	private List<User> userList;
	
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
		this.userList = new ArrayList<>();
		userList.add(new ProjectManager("admin"));
		
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
	 * Get the first user found with a given name
	 * 
	 * @param username
	 *            | The name of the user
	 * @return The first user in the user list that has the given name
	 */ // TODO wat als er meerdere users zijn met dezelfde naam?
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
	
	/**
	 * Find the project manager (the user with admin as a username)
	 * 
	 * @return the project manager (default user)
	 */ // TODO deze methode is verre van veilig (wat als er meerdere admins zijn, wat als een developer de naam admin heeft?)
	public User getDefaultUser() {
		return getUser("admin");
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
		boolean success = userList.add(new Developer(name));
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
				ConcreteResource cr = unWrapConcreteResourceView(resource);
				if(cr != null) {
					toReserve = cr;
				}
			}
			if(toReserve == null || canReserve(toReserve,startTime,endTime)) {
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
	
	private ConcreteResource pickUnreservedResource(ResourcePrototype rp, LocalDateTime start, LocalDateTime end) {
		List<ConcreteResource> options = getPoolOf(rp).getConcreteResourceList();
		for(ConcreteResource cr : options) {
			if(canReserve(cr, start, end)) {
				return cr;
			}
		}
		return null;
	}
	
	private ResourcePool getPoolOf(ResourcePrototype rp) {
		for(ResourcePool pool : resPools) {
			if(pool.hasAsPrototype(rp)) {
				return pool;
			}
		}
		return null;
	}
	
//	public ImmutableList<ResourceView> getPossibleResourceInstances(ResourceView resourceType){
//		return null; //TODO implement (is het wel nodig?)
//	}
	
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
		getPoolOf(rprot).getConcreteResourceViewList();
		return null;
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
	 * Check whether or not the given resources and their supplied amount exist
	 * in the resource manager's resource pools
	 * 
	 * @param reqRes
	 *            | A map linking resourcePrototypes with a specified amount
	 * @return True if enough resources exist, false otherwise
	 */
	public boolean isValidRequiredResources(Map<ResourceView,Integer> reqRes) {
		for(ResourceView rv : reqRes.keySet()) {
			ResourcePrototype rp = unWrapResourcePrototypeView(rv);
			if(rp == null) {
				return false;
			}
			int i = reqRes.get(rv);
			if(i <= 0) {
				return false;
			}
//			for(ResourcePool pool : resPools) {
//				if(pool.hasAsPrototype(rp)) {
					if(i > getPoolOf(rp).size()) {
						return false;
					}
//					break;
//				}
//			}
		}
		return true;
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
	
	/**
	 * This method will remove all future reservations for a given
	 * (finished/failed) task. If there are any active reservations that have
	 * already started, but not yet finished, a reservation until this point
	 * will be maintained, but the resources will be made available again (i.e.
	 * the reservation will end now).
	 * 
	 * @param task
	 *            | The finished or failed task
	 * @param currentTime
	 *            | The current time in the system
	 * @return true if all required changes were successfully made
	 */
	public boolean flushFutureReservations(Task task, LocalDateTime currentTime) {
		boolean succesful = false;
		for (Reservation res : activeReservations){
			if (res.getReservingTask().equals(task)){
				activeReservations.remove(res);
				succesful = true;
			}
		}
		for (Reservation res : allReservations){
			if (res.getReservingTask().equals(task)){
				if(res.getEndTime().isAfter(currentTime)){
					ConcreteResource reserved = res.getReservedResource();
					Task resTask = res.getReservingTask();
					LocalDateTime start = res.getStartTime();
					allReservations.remove(res);
					allReservations.add(new Reservation(reserved,resTask,start,currentTime));
				}
			}
		}
		return succesful;
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