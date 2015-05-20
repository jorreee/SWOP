package company.taskMan.resource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import company.taskMan.resource.user.User;
import company.taskMan.resource.user.UserPrototype;
import company.taskMan.task.Task;
import company.taskMan.util.TimeSpan;

import exceptions.NoSuchResourceException;
import exceptions.ResourceUnavailableException;
import exceptions.UnexpectedViewContentException;

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
	private final User superUser;
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
	public ResourceManager(List<ResourcePrototype> prototypes) {
		this.resPools = new ArrayList<>();
		
		userProt = new UserPrototype();
		userList = new ArrayList<User>();
		userList.add(userProt.instantiateProjectManager("admin"));
		superUser = userProt.instantiateSuperUser("Initializer");
		
		activeReservations = new ArrayList<>();
		allReservations = new ArrayList<>();
		
		for(ResourcePrototype prot: prototypes){
			addResourceType(prot);
		}
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
	 * @throws IllegalArgumentException
	 */
	public void createResourcePrototype(String resourceName,
			Optional<LocalTime> availabilityStart, Optional<LocalTime> availabilityEnd) 
				throws IllegalArgumentException{
		
		if(!isValidPeriod(availabilityStart, availabilityEnd)) {
			throw new IllegalArgumentException("Invalid timestamps");
		}
		if(resourceName == null) {
			throw new IllegalArgumentException("Invalid resource name");
		}
		
		// Create resourcePrototype (should happen before applying conflicting resources,
		// since a resource can conflict with itself)
		ResourcePrototype resprot = null;
		if(!availabilityStart.isPresent() && !availabilityEnd.isPresent()) {
			resprot = new ResourcePrototype(resourceName, null);
		} else {
			resprot = new ResourcePrototype(resourceName, new AvailabilityPeriod(availabilityStart.get(), availabilityEnd.get()));
		}
		addResourceType(resprot);
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
	private void addResourceType(ResourcePrototype resProt) {
		resPools.add(new ResourcePool(resProt));
	}
	
	/**
	 * Construct a new concrete resource based on a resource prototype
	 * 
	 * @param resName
	 *            | The name for the new concrete resource
	 * @param ptype
	 *            | The prototype for which a new resource should be made
	 * @throws IllegalArgumentException, UnexpectedViewContentException
	 */
	public void declareConcreteResource(String resName, ResourceView ptype) 
			throws IllegalArgumentException, UnexpectedViewContentException {
		ResourcePrototype prototype = unWrapResourcePrototypeView(ptype);
		ResourcePool resPool;
		try {
			resPool = getPoolOf(prototype);
			resPool.createResourceInstance(resName);
		} catch (NoSuchResourceException e) {
			throw new UnexpectedViewContentException(e.getMessage());
		}
	}
	
//	/**
//	 * Get the user who corresponds to the given user
//	 * 
//	 * @param newUser
//	 *            | The user
//	 * @return the user who corresponds to the given user
//	 * @throws IllegalArgumentException
//	 */
//	public User getUser(ResourceView newUser)
//			throws IllegalArgumentException {
//		if(newUser == null) {
//			throw new IllegalArgumentException("newUser must not be null");
//		}
//		for(User user : userList) {
//			if(newUser.hasAsResource(user)) {
//				return user;
//			}
//		}
//		return null;
//	}
	
	/**
	 * Find the project manager
	 * 
	 * @return the project manager (default user)
	 */
	public User getSuperUser() {
		return superUser;
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
	 * Reserve a list of resources from a specific start to a specific end time.
	 * A reservation will always be made by a task.
	 * 
	 * @param resources
	 *            | The resources to reserve
	 * @param reservingTask
	 *            | The task making the reservation
	 * @param startTime
	 *            | The start time of the reservation
	 * @param endTime
	 *            | The end time of the reservation
	 * @param checkCanReserve
	 *            | This parameter will define if the method should check if the
	 *            resource is actually available to reserve from the given start
	 *            to end time
	 * @throws IllegalArgumentException, UnexpectedViewContentException, 
	 * 		   ResourceUnavailableException, NoSuchResourceException 
	 */
	// TODO make a new reservation for every day (only within working hours and taking availability period into account)
	public void reserve(
			List<ResourceView> resources, 
			Task reservingTask, 
			LocalDateTime startTime, 
			LocalDateTime endTime,
			boolean checkCanReserve) 
				throws IllegalArgumentException, 
					UnexpectedViewContentException, 
					ResourceUnavailableException {
		
		if(resources == null || reservingTask == null ||
				startTime == null || endTime == null) {
			throw new IllegalArgumentException("Invalid parameters");
		}
		if(endTime.isBefore(startTime)) {
			throw new IllegalArgumentException("Invalid timestamps");
		}
		
		List<Reservation> newReservations = new ArrayList<Reservation>();
		
		for(ResourceView resource : resources) {
			ConcreteResource toReserve = null;
			try {
				ResourcePrototype r = unWrapResourcePrototypeView(resource);

				if(checkCanReserve) { // Safe method, check for unreserved resource
					toReserve = pickUnreservedResource(r, startTime, endTime, newReservations, new ArrayList<ConcreteResource>());
				} else { // Unsafe method, get the first resource (that is not already chosen)
					List<ResourceView> crList = new ArrayList<ResourceView>(getConcreteResourcesForPrototype(new ResourceView(r)));
					List<ResourceView> invalidCrs = new ArrayList<>();
					for(ResourceView cr : crList) {
						for(Reservation res : newReservations) {
							if(res.hasAsResource(unWrapConcreteResourceView(cr))) { //FIXME hier worden nog exceptions gegooid
								invalidCrs.add(cr);
							}
						}
					}
					crList.removeAll(invalidCrs);
					if(!crList.isEmpty()) {
						toReserve = unWrapConcreteResourceView(crList.remove(0));//FIXME hier worden nog exceptions gegooid
					} //FIXME anders wat?
				}

			} catch(UnexpectedViewContentException e1) {
				try {
					User user = unWrapUserView(resource);
					toReserve = user;
				} catch(UnexpectedViewContentException e2) {
					try {
						ConcreteResource cr = unWrapConcreteResourceView(resource);
						toReserve = cr;
					} catch(UnexpectedViewContentException e3) {
						//De unwrap was invalid, gooi dus nieuwe exception
						throw new UnexpectedViewContentException("The view didn't contain a valid object");
					}
				}
			} catch(NoSuchResourceException e4) {
				throw new ResourceUnavailableException(e4.getMessage());
			}
			if(toReserve == null) {
				//bv als er geen unreserved resource meer beschikbaar was
				throw new ResourceUnavailableException("Failed to reserve resource: " + resource.getName());
			}
			newReservations.add(new Reservation(toReserve, reservingTask, startTime, endTime));
		}

		if(!reservingTask.hasEnded()) {
			activeReservations.addAll(newReservations);
		}
		allReservations.addAll(newReservations);
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
	 * @param checkCanReserve
	 *            | This parameter will define if the method should check if the
	 *            resource is actually available to reserve from the given start
	 *            to end time
	 * @return The list of users for whom the reservation was made
	 * @throws IllegalArgumentException, UnexpectedViewContentException, ResourceUnavailableException 
	 */
	//TODO hier moet niks gecatched worden, right?
	public List<User> pickDevs(List<ResourceView> devs, 
			Task reservingTask, 
			LocalDateTime start,
			LocalDateTime end, 
			boolean checkCanReserve) 
				throws IllegalArgumentException, UnexpectedViewContentException, ResourceUnavailableException{
		List<User> users = new ArrayList<>();
		for(ResourceView dev : devs) {
			User user = unWrapUserView(dev);
			users.add(user);
		}
		reserve(devs, reservingTask, start, end, checkCanReserve);
		return users;
	}

	/**
	 * Checks whether the given resource can be reserved from the given start
	 * time to the given end time.
	 * 
	 * @param resource
	 *            | The resource to check.
	 * @param start
	 *            | The start time of the reservation.
	 * @param end
	 *            | The end time of the reservation.
	 * @param alreadyReserved
	 *            | A list of new reservations (not yet present in the system)
	 *            to also take into account
	 * @return true if the resource can be reserved in the given time slot, else
	 *         false.
	 */
	private boolean canReserve(ConcreteResource resource, 
			LocalDateTime start, 
			LocalDateTime end, 
			List<Reservation> alreadyReserved) {
		List<Reservation> toCheck = new ArrayList<Reservation>();
		toCheck.addAll(activeReservations);
		toCheck.addAll(alreadyReserved);
		for(Reservation reservation : toCheck) {
			if(reservation.hasAsResource(resource)) {
				if(reservation.overlaps(start,end)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Picks the Unreserved Resource instances of a given Resource Prototype for
	 * the given time period.
	 * 
	 * @param rp
	 *            The resource prototype.
	 * @param start
	 *            The start time for the reservation.
	 * @param end
	 *            The end time for the reservation.
	 * @param alreadyReserved
	 *            Reservations that were already made
	 * @param dontReserve
	 *            Concrete resources to ignore
	 * @return A list of Unreserved Concrete Resources of the Prototype. 
	 * @throws IllegalArgumentException, ResourceUnavailableException, NoSuchResourceException 
	 */
	private ConcreteResource pickUnreservedResource(ResourcePrototype rp, 
			LocalDateTime start, 
			LocalDateTime end, 
			List<Reservation> alreadyReserved,
			List<ConcreteResource> dontReserve) 
					throws IllegalArgumentException, ResourceUnavailableException, NoSuchResourceException {
		if(start == null || end == null || alreadyReserved == null || dontReserve == null) {
			throw new IllegalArgumentException("Invalid parameters");
		}
		List<ConcreteResource> options = getPoolOf(rp).getConcreteResourceList();
		for(ConcreteResource cr : options) {
			if(!dontReserve.contains(cr) && canReserve(cr, start, end, alreadyReserved)) {
				return cr;
			}
		}
		throw new ResourceUnavailableException("There are no \"" + rp.getName() + "\"s available");
	}
	
	/**
	 * Gets the Resource Pool of a given Prototype.
	 * @param 	rp
	 * 			The resource prototype
	 * @return	If the pool exists, return the pool. 
	 * @throws IllegalArgumentException, NoSuchResourceException
	 */
	private ResourcePool getPoolOf(ResourcePrototype rp) 
			throws IllegalArgumentException, NoSuchResourceException {
		if(rp == null) {
			throw new IllegalArgumentException("rp must not be null");
		}
		for(ResourcePool pool : resPools) {
			if(pool.hasAsPrototype(rp)) {
				return pool;
			}
		}
		throw new NoSuchResourceException("Invalid resource prototype");
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
			if(user.isDeveloper()) {
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
	 * @throws NoSuchResourceException 
	 */
	public ResourceView getPrototypeOf(ResourceView view) throws NoSuchResourceException{
		for(ResourcePool pool : resPools) {
			for (Resource res : pool.getConcreteResourceList()){
				if (view.hasAsResource(res)){
					return new ResourceView(pool.getPrototype());
				}
			}
		}
		throw new NoSuchResourceException("Invalid resourceView");
	}
	
	/**
	 * This method will locate the resource pool responsible for the given
	 * prototype and return an immutable list of all its concrete resource
	 * instances (wrapped in a resourceView object)
	 * 
	 * @param resourcePrototype
	 *            | The prototype for which the concrete resources are wanted
	 * @return an immutable list of resourceView linked with the concrete
	 *         resources based on the given resource prototype, an empty list
	 *         if the prototype is not associated with any pool
	 * @throws UnexpectedViewContentException, IllegalArgumentException, NoSuchResourceException 
	 */
	public List<ResourceView> getConcreteResourcesForPrototype(ResourceView resourcePrototype) 
			throws UnexpectedViewContentException, NoSuchResourceException, IllegalArgumentException {
		ResourcePrototype rprot = unWrapResourcePrototypeView(resourcePrototype);
		if(rprot == null) {
			Builder<ResourceView> conResList = ImmutableList.builder();
			return conResList.build();
		}
		List<ConcreteResource> concreteRes = getPoolOf(rprot).getConcreteResourceList();
		Builder<ResourceView> conResList = ImmutableList.builder();
		for (ConcreteResource res : concreteRes) {
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
	 * @throws UnexpectedViewContentException, IllegalArgumentException
	 */
	private ConcreteResource unWrapConcreteResourceView(ResourceView view) 
			throws IllegalArgumentException, UnexpectedViewContentException{
		if(view == null) {
			throw new IllegalArgumentException("view must not be null");
		}
		for(ResourcePool pool : resPools) {
				for (ConcreteResource res : pool.getConcreteResourceList()){
					if (view.hasAsResource(res)){
						return res;
					}
				}
		}
		throw new UnexpectedViewContentException("View didn't contain a valid concrete resource");
	}
	
	/**
	 * Unwrap a resourceView to its resource prototype contents. This method
	 * will return null if the resource cannot be found
	 * 
	 * @param view
	 *            | The given resource prototype
	 * @return the resource prototype found in the resourceView
	 * @throws UnexpectedViewContentException, IllegalArgumentException
	 */
	private ResourcePrototype unWrapResourcePrototypeView(ResourceView view) 
			throws UnexpectedViewContentException {
		if(view == null) {
			throw new IllegalArgumentException("view must not be null");
		}
		for(ResourcePool pool : resPools) {
			if (view.hasAsResource(pool.getPrototype())) {
				return pool.getPrototype();
			}
		}
		throw new UnexpectedViewContentException("View didn't contain a valid resource Prototype");
	}
	
	/**
	 * Unwrap a resourceView to its user contents. This method
	 * will return null if the resource cannot be found
	 * 
	 * @param view
	 *            | The given user
	 * @return the user found in the resourceView
	 * @throws IllegalArgumentException, UnexpectedViewContentException
	 * 
	 */
	public User unWrapUserView(ResourceView view) 
			throws IllegalArgumentException, UnexpectedViewContentException {
		if(view == null) {
			throw new IllegalArgumentException("view must not be null");
		}
		for(User user : userList) {
			if (view.hasAsResource(user)) {
				return user;
			}
		}
		throw new UnexpectedViewContentException("View didn't contain a valid User");
	}

	/**
	 * Check whether or not the given resources and their supplied amount exist
	 * in the resource manager's resource pools. Also checks whether the required resources for each prototype are present, 
	 * and conflicting resources are not. 
	 * Returns the list of Prototypes if they are valid. Returns NULL if they aren't
	 * 
	 * @param reqRes
	 *            | A map linking resourcePrototypes with a specified amount
	 * @return The list of prototype if the resources are valid
	 * @throws NoSuchResourceException, IllegalArgumentException
	 */

	public Map<ResourcePrototype, Integer> isValidRequiredResources(Map<ResourceView,Integer> reqRes) 
			throws IllegalArgumentException, NoSuchResourceException {
		if(reqRes == null) {
			throw new IllegalArgumentException("reqRes must not be null");
		}
		try {
			Map<ResourcePrototype, Integer> resProtList = new HashMap<ResourcePrototype, Integer>();
			for(ResourceView rv : reqRes.keySet()) {
				ResourcePrototype rp = unWrapResourcePrototypeView(rv);
				int i = reqRes.get(rv);
				if(i <= 0) {
					throw new IllegalArgumentException("Entries must be strictly positive");
				}
				if(i > getPoolOf(rp).size()) {
					throw new IllegalArgumentException("Entry for \"" + rp.getName() + "\" must be <= " + getPoolOf(rp).size());
				}
				resProtList.put(rp, i);
			}
			for(ResourcePrototype prot : resProtList.keySet()){
				for (ResourcePrototype req : prot.getRequiredResources()){
					if (!resProtList.containsKey(req)) {
						throw new IllegalArgumentException("\"" + prot.getName() + "\" requires \"" + req.getName() + "\"");
					}
				}
				for (ResourcePrototype confl : prot.getConflictingResources()){
					if (resProtList.containsKey(confl)){
						throw new IllegalArgumentException("\"" + confl.getName() + "\" conflicts with \"" + prot.getName() + "\"");
					}
				}
			}
			return resProtList;
		} catch(UnexpectedViewContentException e) {
			throw new NoSuchResourceException(e.getMessage());
		}
	}

	/**
	 * Check whether a task has active reservations for all of its required
	 * resources
	 * 
	 * @param reservedTask
	 *            | The task for which the reservations should be checked
	 * @return true if every required resource has enough active reservations
	 */
	public boolean hasActiveReservations(Task reservedTask){
		Map<ResourcePrototype,Integer> checkList = new HashMap<ResourcePrototype, Integer>();
		checkList.putAll(reservedTask.getRequiredResources());
		checkList.put(userProt, reservedTask.getPlannedDevelopers().size());
		for (Reservation res: activeReservations){
			if (res.getReservingTask().equals(reservedTask)){
				for (ResourcePrototype resource : checkList.keySet()){
					if(res.getReservedResource().getPrototype().equals(resource)) {
						checkList.put(resource, checkList.get(resource) - 1);
					}
						
				}
			}
		}

		for(Integer i : checkList.values()) {
			if(!i.equals(0)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Add resource requirements to a prototype
	 * 
	 * @param reqToAdd
	 *            | The new requirements to add
	 * @param prototype
	 *            | The prototype that the new requirements should be added to
	 * @throws IllegalArgumentException
	 */
	public void addRequirementsToResource(List<ResourceView> reqToAdd, ResourceView prototype) 
			throws IllegalArgumentException {
		ResourcePrototype rprot = unWrapResourcePrototypeView(prototype);
		for (ResourceView req : reqToAdd) {
			ResourcePrototype unwrapReq = unWrapResourcePrototypeView(req);
			rprot.addRequiredResource(unwrapReq);
		}
	}
	
	/**
	 * Add resource conflicts to a prototype
	 * 
	 * @param conToAdd
	 *            | The new conflicts to add
	 * @param prototype
	 *            | The prototype that the new conflicts should be added to
	 * @throws IllegalArgumentException
	 */
	public void addConflictsToResource(List<ResourceView> conToAdd, ResourceView prototype) 
			throws IllegalArgumentException {
		ResourcePrototype rprot = unWrapResourcePrototypeView(prototype);
		for (ResourceView req : conToAdd) {
			ResourcePrototype unwrapReq = unWrapResourcePrototypeView(req);
			rprot.addConflictingResource(unwrapReq);
		}
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
	 * @param currentTime
	 *            | The current time, the method will find timestamps after this
	 *            time
	 * @param amount
	 *            | The amount of suggestions that should be calculated
	 * @return a list of timestamps when a planning could be made without
	 *         conflicts
	 * @throws IllegalArgumentException, NoSuchResourceException, ResourceUnavailableException 
	 */
	public List<LocalDateTime> getPossibleStartingTimes(Task task, List<ResourceView> allResources, LocalDateTime currentTime, int amount) throws ResourceUnavailableException, NoSuchResourceException, IllegalArgumentException {
		List<LocalDateTime> posTimes = new ArrayList<LocalDateTime>();
		// Workday timings
		LocalTime workDayStart = LocalTime.of(8,0);
		LocalTime workDayEnd = LocalTime.of(17, 0);
		
		// AvailabilityPeriod
		LocalTime[] availabilityPeriod = task.getAvailabilityPeriodBoundWorkingTimes();
		if(availabilityPeriod[0] != null) {
			workDayStart = availabilityPeriod[0];
			workDayEnd = availabilityPeriod[1];
		}
		
		// Initial time to check (last planned end time of prerequisite task)
		LocalDateTime hour = currentTime;
		if(!task.getPrerequisites().isEmpty()) {
			LocalDateTime latest = currentTime;
			for(Task prereq : task.getPrerequisites()) {
				latest = (prereq.getPlannedEndTime().isAfter(latest)) ? prereq.getPlannedEndTime() : latest;
			}
			hour = latest;
		}
		
		// Shift hour to inside workday
		if(hour.toLocalTime().isAfter(workDayEnd)) {
			hour = hour.withHour(8);
			hour = hour.plusDays(1);
		}
		if(hour.toLocalTime().isBefore(workDayStart)) {
			hour = hour.withHour(8);
		}
		switch (hour.getDayOfWeek()) {
		case SATURDAY:
			hour = hour.plusDays(2);
			break;
		case SUNDAY:
			hour = hour.plusDays(1);
			break;
		default:
			break;
		}
		if(hour.getMinute() != 0) {
			hour = hour.plusHours(1);
			hour = hour.withMinute(0);
		}
		
		// Until amount (Check hour)
		while(amount > 0) {
			boolean validTimeStamp = true;
			
			// These are already chosen
			List<ConcreteResource> alreadyReserved = new ArrayList<>();
			for(ResourceView resource : allResources) {
				try{
				ConcreteResource cr = unWrapConcreteResourceView(resource);
				alreadyReserved.add(cr);
				} catch (UnexpectedViewContentException e) {		
				}
			}
			
			// For each resource
			for(ResourceView resource : allResources) {
				try {
				ConcreteResource cr = unWrapConcreteResourceView(resource);
				// if Concrete resource
					// Available from hour until hour + task.getEstimatedDuration()
					if(!canReserve(cr, hour, TimeSpan.addSpanToLDT(hour,task.getEstimatedDuration(), workDayStart, workDayEnd), new ArrayList<Reservation>())) {
						validTimeStamp = false;
						break;
					}
					continue;
				}
				catch (UnexpectedViewContentException e){
					
				}
				try {
				ResourcePrototype rp = unWrapResourcePrototypeView(resource);
				// if Prototype
					// Find concrete resource (not yet present) which is available from ...
				 try {
					ConcreteResource cr = pickUnreservedResource(rp, hour, TimeSpan.addSpanToLDT(hour,task.getEstimatedDuration(), workDayStart, workDayEnd), new ArrayList<Reservation>(), alreadyReserved);
					// Add cr to list
					alreadyReserved.add(cr);
					continue;
				 }
				catch ( UnexpectedViewContentException e) {	// No cr to be found (Oh nooes)
						validTimeStamp = false;
						break;
					}
					
				}
				catch (UnexpectedViewContentException e){
					
				}
				// if User
				try {
				User us = unWrapUserView(resource);
					if(!alreadyReserved.contains(us) && canReserve(us,hour, TimeSpan.addSpanToLDT(hour,task.getEstimatedDuration(), workDayStart, workDayEnd),new ArrayList<Reservation>())) {
						alreadyReserved.add(us);
						continue;
					} else {
						validTimeStamp = false;
						break;
					}
				}
				catch (UnexpectedViewContentException e) {
				return null; // The resources in allResources must be concrete or prototype
				}
			}
			// If everything checks out, good job!
			if(validTimeStamp) {
				posTimes.add(hour);
				amount--;
			}
			// Add hour
			hour = TimeSpan.addSpanToLDT(hour, new TimeSpan(60), workDayStart, workDayEnd);
		} // Repeat
		return posTimes;
	}
	
	/**
	 * Any task can call this method to release all active reservations made by
	 * this task.
	 * 
	 * @param task
	 *            | The task which wants to release its resources
	 * @param currentTime
	 *            | The current time, for bookkeeping purposes, reservations
	 *            will be kept until this time
	 * @return True if the resources were successfully released, False otherwise
	 */
	public boolean releaseResources(Task task, LocalDateTime currentTime) {
		List<Reservation> toRemoveActive = new ArrayList<Reservation>();
		List<Reservation> toRemoveAll = new ArrayList<Reservation>();
		List<Reservation> toChange = new ArrayList<Reservation>();
		for(Reservation r : activeReservations) {
			if(r.getReservingTask().equals(task)) {
				toRemoveActive.add(r);
				if(!r.getStartTime().equals(currentTime)) {
					toChange.add(new Reservation(r.getReservedResource(),r.getReservingTask(),r.getStartTime(),currentTime));
				}
				toRemoveAll.add(r);
			}
		}
		if(!toRemoveActive.isEmpty() && !activeReservations.removeAll(toRemoveActive)) {
			activeReservations.addAll(toRemoveActive);
			return false;
		}
		if(!toRemoveAll.isEmpty() && !allReservations.removeAll(toRemoveAll)) {
			activeReservations.addAll(toRemoveActive);
			allReservations.addAll(toRemoveAll);
			return false;
		}
		if(!toChange.isEmpty() && !allReservations.addAll(toChange)) {
			activeReservations.addAll(toRemoveActive);
			allReservations.addAll(toRemoveAll);
			allReservations.removeAll(toChange);
			return false;
		}
//		if(!task.releaseDevelopers()) {
//			activeReservations.addAll(toRemoveActive);
//			allReservations.addAll(toRemoveAll);
//			allReservations.removeAll(toChange);
//			return false;
//		}
		task.releaseDevelopers();
		return true;
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
	
	/**
	 * Returns the reserved resources for a given task.
	 * 
	 * @param task
	 *            The task to get the reserved resources for.
	 * @return A list of resources reserved by a task
	 */
	public List<Resource> getReservedResourcesForTask(Task task){
		Builder<Resource> resources = ImmutableList.builder();
		for (Reservation res : activeReservations){
			if (res.getReservingTask().equals(task)){
				resources.add(res.getReservedResource());
			}
		}
		return resources.build();
	}

}