package company;

import initialization.TaskManInitFileChecker;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.security.auth.login.CredentialException;

import userInterface.IFacade;
import userInterface.Main;
import userInterface.TaskManException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import company.caretaker.TaskManCaretaker;
import company.taskMan.ProjectView;
import company.taskMan.TaskMan;
import company.taskMan.project.TaskView;
import company.taskMan.resource.AvailabilityPeriod;
import company.taskMan.resource.Reservation;
import company.taskMan.resource.Resource;
import company.taskMan.resource.ResourcePrototype;
import company.taskMan.resource.ResourceView;
import company.taskMan.resource.user.User;
import company.taskMan.resource.user.UserPermission;

import exceptions.ResourceUnavailableException;
import exceptions.UnexpectedViewContentException;

//TODO list of prototypes in branchMa
public class BranchManager implements IFacade {
	private List<TaskMan> taskMen;
	private TaskMan currentTaskMan;
	private Delegator delegator;
	private LocalDateTime currentTime;
	private User currentUser;
	private List<ResourcePrototype> prototypes; // FIXME DIT MOET BETER, PLEASE!
	private final TaskManCaretaker caretaker;

	
	public BranchManager(LocalDateTime time) {
		taskMen = new ArrayList<>();
		delegator = new Delegator();
		caretaker = new TaskManCaretaker(this);
		currentUser = null;
		this.prototypes = new ArrayList<>();
		currentTime = time;
	}
	
//	public void declareBranch(LocalDateTime branchTime, String geographicLocation) {
//		taskMen.add(new Branch(branchTime, geographicLocation));
//	}
//	
//	public void initializeFromMemento() {
//		this.taskMan = new TaskMan();
//	}
	
	/**
	 * declares a TaskMan
	 * 
	 * @param	location
	 * 			The location of the branch.
	 * @throws  IllegalArgumentException 
	 */
	private void declareTaskMan(String location, List<ResourcePrototype> prototypes) throws IllegalArgumentException{
		TaskMan newTaskMan = new TaskMan(location, prototypes);
		taskMen.add(newTaskMan);
		currentTaskMan = newTaskMan;
		currentUser = newTaskMan.getSuperUser();
	}
	
	/**
	 * Gets the current time
	 * 
	 * @return the current time
	 */
	@Override
	public LocalDateTime getCurrentTime() {
		return currentTime;
	}
	
	@Override
	public void createProject(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime) throws TaskManException {
		if(!currentUserHasPermission(UserPermission.CREATE_PROJECT)) {
			throw new TaskManException(new CredentialException("User has no permission to create projects"));
		}
		currentTaskMan.createProject(name, description, creationTime, dueTime);
	}
	
	@Override
	public void createProject(String name, String description,
			LocalDateTime dueTime) throws TaskManException {
		if(!currentUserHasPermission(UserPermission.CREATE_PROJECT)) {
			throw new TaskManException(new CredentialException("User has no permission to create projects"));
		}
		currentTaskMan.createProject(name, description, this.currentTime, dueTime);
	}

	@Override
	public void createTask(
			ProjectView project, 
			String description,
			int estimatedDuration, 
			int acceptableDeviation,
			List<TaskView> prerequisiteTasks, 
			Map<ResourceView, Integer> requiredResources,
			TaskView alternativeFor) throws TaskManException {
		
		if(!currentUserHasPermission(UserPermission.CREATE_TASK)) {
			throw new TaskManException(new CredentialException("User has no permission ro create tasks"));
		}
		
		try {
			currentTaskMan.createTask(project, 
					description, 
					estimatedDuration, 
					acceptableDeviation, 
					prerequisiteTasks, 
					requiredResources,
					alternativeFor);
		} catch (Exception e) {
			throw new TaskManException(e);
		}
	}

	@Override
	public void createTask(
			ProjectView project,
			String description,
			int estimatedDuration, 
			int acceptableDeviation,
			List<TaskView> prerequisiteTasks, 
			TaskView alternativeFor,
			Map<ResourceView, Integer> requiredResources, 
			String taskStatus,
			LocalDateTime startTime, 
			LocalDateTime endTime,
			LocalDateTime plannedStartTime, 
			List<ResourceView> plannedDevelopers)  throws TaskManException {
			if(!currentUserHasPermission(UserPermission.CREATE_TASK)) {
				throw new TaskManException(new CredentialException("User has no permission to create tasks"));
			}
			try {
			currentTaskMan.createTask(project, 
					description, 
					estimatedDuration, 
					acceptableDeviation, 
					prerequisiteTasks, 
					alternativeFor,
					requiredResources,
					taskStatus,
					startTime,
					endTime,
					plannedStartTime,
					plannedDevelopers);
		} catch(Exception e) { throw new TaskManException(e); }
	}

	/**
	 * Advances the current time to the given time.
	 * 
	 * @param time
	 *            The time to which the system should advance
	 * @return True if the advance time was successful. False if the time
	 *         parameter is earlier than the current time.
	 */
	@Override
	public void advanceTimeTo(LocalDateTime time) 
			throws TaskManException {
		if(!currentUserHasPermission(UserPermission.ADVANCE_TIME))
			throw new TaskManException(new CredentialException("The user has no permission to advance the time"));
		if (time == null)
			throw new TaskManException(new IllegalArgumentException("Time is null"));
		if (time.isAfter(currentTime)) {
			currentTime = time;
		} else
			throw new TaskManException(new IllegalArgumentException("The given time is before the currentTime"));

	}

	@Override
	public void setTaskFinished(ProjectView project, TaskView task,
			LocalDateTime endTime) throws TaskManException{
		if(!currentUserHasPermission(UserPermission.UPDATE_TASK)) {
			throw new TaskManException(new CredentialException("The user has no permission to update the taskstatus"));
		}
		if (endTime == null) { // || startTime == null) {
			throw new TaskManException(new IllegalArgumentException("The endtime is null"));
		}
		if (endTime.isAfter(currentTime)) {
			throw new TaskManException(new IllegalArgumentException("The endtime is after the currentTime"));
		}
		currentTaskMan.setTaskFinished(project, task, endTime);
	}

	@Override
	public void setTaskFailed(ProjectView project, TaskView task,
			LocalDateTime endTime) throws TaskManException{
		if(!currentUserHasPermission(UserPermission.UPDATE_TASK)) {
			throw new TaskManException(new CredentialException("The user has no permission to update the taskstatus"));
		}
		if (endTime == null) { // || startTime == null) {
			throw new TaskManException(new IllegalArgumentException("The endtime is null"));
		}
		if (endTime.isAfter(currentTime)) {
			throw new TaskManException(new IllegalArgumentException("The endtime is after the currentTime"));
		}
		currentTaskMan.setTaskFailed(project, task, endTime);
	}
	
	@Override
	public void setTaskExecuting(ProjectView project, TaskView task,
			LocalDateTime startTime) throws TaskManException {
		if(!currentUserHasPermission(UserPermission.UPDATE_TASK)) {
			throw new TaskManException(new CredentialException("The user has no permission to update the taskstatus"));
		}
		if (startTime == null) {
			throw new TaskManException(new IllegalArgumentException("The startTime is null"));
		}
		if (startTime.isAfter(currentTime)) {
			throw new TaskManException(new IllegalArgumentException("The startTime is after the currentTime"));
		}
		currentTaskMan.setTaskExecuting(project,task,startTime);
	}

	@Override
	public List<ProjectView> getProjects() {
		return currentTaskMan.getProjects();
	}

	@Override
	public boolean storeInMemento() {
		if (!currentUserHasPermission(UserPermission.SIMULATE)) {
			return false;
		}
		caretaker.storeInMemento();
		return true;
	}

	@Override
	public void revertFromMemento() {
		caretaker.revertFromMemento();
	}

	@Override
	public void discardMemento() {
		caretaker.discardMemento();
	}

	@Override
	public ResourceView getCurrentUser() {
		return new ResourceView(currentUser);
	}

	@Override
	public List<ResourceView> getPossibleUsers() {
		return currentTaskMan.getPossibleUsernames();
	}

	@Override
	public void changeToUser(ResourceView user) throws TaskManException{
		User newUser = currentTaskMan.getUser(user);
		if (user == null) {
			throw new TaskManException(new IllegalArgumentException("Invalid user"));
		} else {
			currentUser = newUser;
		}
	}

	@Override
	public void createResourcePrototype(String name,
			Optional<LocalTime> availabilityStart,
			Optional<LocalTime> availabilityEnd) throws TaskManException{
//		currentTaskMan.createResourcePrototype(name,availabilityStart,availabilityEnd);
		if(!isValidPeriod(availabilityStart, availabilityEnd)) {
			throw new TaskManException(new IllegalArgumentException("Invalod time period"));
		}
		if(name == null) {
			throw new TaskManException(new IllegalArgumentException("Null is not allowed as name"));
		}
		
		// Create resourcePrototype (should happen before applying conflicting resources,
		// since a resource can conflict with itself)
		ResourcePrototype resprot = null;
		if(!availabilityStart.isPresent() && !availabilityEnd.isPresent()) {
			resprot = new ResourcePrototype(name, null);
		} else {
			resprot = new ResourcePrototype(name, new AvailabilityPeriod(availabilityStart.get(), availabilityEnd.get()));
		}
		prototypes.add(resprot);
	}

	@Override
	public void declareConcreteResource(String name, ResourceView fromPrototype) {
		currentTaskMan.declareConcreteResource(name,fromPrototype);
	}

	@Override
	public void createDeveloper(String name) {
		currentTaskMan.createDeveloper(name);
	}
	
	@Override
	public void reserveResource(ResourceView resource, ProjectView project, TaskView task, LocalDateTime startTime, LocalDateTime endTime) {
		try {
			currentTaskMan.reserveResource(resource, project, task, startTime, endTime);
		} catch (Exception e) {
			throw new TaskManException(e);
		}
	}

	@Override
	public List<ResourceView> getDeveloperList() {
		return currentTaskMan.getDeveloperList();
	}

	@Override
	public HashMap<ProjectView, List<TaskView>> findConflictingPlannings(TaskView task) {
		return currentTaskMan.findConflictingPlannings(task);
	}

	@Override
	public List<ResourceView> getResourcePrototypes() {
		ImmutableList.Builder<ResourceView> prots = ImmutableList.builder();
		for(Resource prot : prototypes) {
			prots.add(new ResourceView(prot));
		}
		return prots.build();
	}

	public ResourceView getPrototypeOf(ResourceView resource) {
		return currentTaskMan.getPrototypeOf(resource);
	}

	@Override
	public List<ResourceView> getConcreteResourcesForPrototype(
			ResourceView resourcePrototype) {
		return currentTaskMan.getConcreteResourcesForPrototype(resourcePrototype);
	}

	@Override
	public void planTask(ProjectView project, TaskView task,
			LocalDateTime plannedStartTime, List<ResourceView> concRes, 
			List<ResourceView> devs) throws TaskManException{
		if(!currentUserHasPermission(UserPermission.PLAN_TASK)) {
			throw new TaskManException(new CredentialException("User has no permission to plan a task"));
		}
		try {
			currentTaskMan.planTask(project, task, plannedStartTime, concRes, devs);
		} catch (ResourceUnavailableException | IllegalArgumentException
				| IllegalStateException e) {
			throw new TaskManException(e);
		}
	}
	
	@Override
	public void planRawTask(ProjectView project, TaskView task,
			LocalDateTime plannedStartTime, List<ResourceView> concRes, 
			List<ResourceView> devs){
		if(!currentUserHasPermission(UserPermission.PLAN_TASK)) {
			throw new TaskManException(new CredentialException("User has no permission to plan a task"));
		}
		try {
			currentTaskMan.planRawTask(project, task, plannedStartTime, concRes, devs);
		} catch (ResourceUnavailableException | IllegalArgumentException
				| IllegalStateException e) {
			throw new TaskManException(e);
		}
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
			throws TaskManException {
		if(view == null) {
			throw new TaskManException(new IllegalArgumentException("view must not be null"));
		}
		for(ResourcePrototype prototype : prototypes) {
			if (view.hasAsResource(prototype)) {
				return prototype;
			}
		}
		throw new TaskManException(new UnexpectedViewContentException("View didn't contain a valid resource Prototype"));
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
	 * Check whether or not this user has a specific credential
	 * 
	 * @param permission
	 *            | The permission to check
	 * @return True if the user has the credential, false otherwise
	 */
	@Override
	public boolean currentUserHasPermission(UserPermission permission) {
		return currentUser.getPermissions().contains(permission);
	}

	public List<Reservation> getAllReservations() {
		return currentTaskMan.getAllReservations();
	}

	@Override
	public boolean isLoggedIn() {
		return currentUser != null && currentTaskMan != null;
	}

	@Override
	public List<BranchView> getBranches() {
		Builder<BranchView> views = ImmutableList.builder();
		for (TaskMan taskMan : taskMen)
			views.add(new BranchView(taskMan));
		return views.build();
	}

	@Override
	public void selectBranch(BranchView branch) {
		TaskMan taskMan = unwrapBranchView(branch);
		currentTaskMan = taskMan;
	}

	@Override
	public void initializeBranch(String geographicLocation) throws IllegalArgumentException {
		this.declareTaskMan(geographicLocation, prototypes);
	}

	@Override
	public void logout() {
		currentUser = null;
		currentTaskMan = null;
	}

	@Override
	public void delegateTask(ProjectView project, TaskView task,
			BranchView newBranch) {
//		currentTaskMan.delegateTask(project,task); TODO
		
	}
	
	/**
	 * Unwraps the BranchView to a TaskMan object
	 * 
	 * @param 	view
	 * 			The view to unwrap
	 * @return	The TaskMan if if belongs to this BranchManager
	 * @throws 	NullPointerException
	 * 			View can't be null
	 * @throws 	IllegalArgumentException
	 * 			TaskMan must belong to this manager
	 */
	private TaskMan unwrapBranchView(BranchView view) 
			throws TaskManException {
		if(view == null) {
			throw new TaskManException(new NullPointerException("There was no branch to unwrap!"));
		}
		TaskMan taskMan = view.unwrap();
		if(!taskMen.contains(taskMan))
			throw new TaskManException(new IllegalArgumentException("Branch does not belong to this BranchManager!"));
		return taskMan;

	}

	@Override
	public BranchView getResponsibleBranch(TaskView task) {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public void delegateTask(ProjectView project, TaskView task,
			BranchView oldBranch, BranchView newBranch) {
		// TODO Auto-generated method stub
		
	}

	public void initializeFromMemento(LocalDateTime systemTime, TaskManInitFileChecker fileChecker) {
		currentTime = systemTime;
		
		currentTaskMan = new TaskMan(fileChecker.getGeographicLocation(), prototypes);
		
		Main.initializeBranch(this, fileChecker, taskMen.indexOf(currentTaskMan));
	}
	
}