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
import company.taskMan.Branch;
import company.taskMan.ProjectView;
import company.taskMan.project.TaskView;
import company.taskMan.resource.PrototypeManager;
import company.taskMan.resource.Reservation;
import company.taskMan.resource.Resource;
import company.taskMan.resource.ResourcePrototype;
import company.taskMan.resource.ResourceView;
import company.taskMan.resource.user.User;
import company.taskMan.resource.user.UserPermission;
import company.taskMan.task.DelegatingTaskProxy;
import company.taskMan.task.OriginalTaskProxy;
import company.taskMan.task.Task;

import exceptions.ResourceUnavailableException;
import exceptions.UnexpectedViewContentException;

/**
 * The Branch manager is the independent organ linking to the outside world (the
 * UI). The BranchManger is responsible for implementing all methods required by
 * the User Interface. Additionally, the BranchManager will have links to all
 * branches in the system, making it possible for the user to navigate to the
 * correct branch. A User will be able to log into any branch using his
 * credentials. The current system time will also be kept in the BranchManager,
 * but this is rather for testing purposes than functionality. If the system
 * would be published, it would probably use the actual time. The
 * TaskManCaretaker will be responsible for a save and restore functionality as
 * required by the simulation Use-Case. Finally the PrototypeManager will keep
 * all data concerning resource types. All Branches in the system are forced to
 * use the same prototypes, in order to remove any inconsistencies that could
 * arise concerning resource types.
 * 
 * @author Tim Van Den Broecke, Joran Van de Woestijne, Vincent Van Gestel and
 *         Eli Vangrieken
 */
public class BranchManager implements IFacade {
	private List<Branch> branches;
	private Branch currentBranch;
	private LocalDateTime currentTime;
	private User currentUser;
	private PrototypeManager protMan;
	private final TaskManCaretaker caretaker;

	/**
	 * Construct a new BranchManager at a certain time
	 * 
	 * @param time
	 *            | The system time
	 */
	public BranchManager(LocalDateTime time) {
		branches = new ArrayList<>();
		caretaker = new TaskManCaretaker(this);
		currentUser = null;
		protMan = new PrototypeManager();
		currentTime = time;
	}
	
	/**
	 * declares a Branch
	 * 
	 * @param	location
	 * 			The location of the branch.
	 * @throws  IllegalArgumentException 
	 */
	private void declareBranch(String location, List<ResourcePrototype> prototypes) throws IllegalArgumentException{
		Branch newBranch = new Branch(location, prototypes);
		branches.add(newBranch);
		currentBranch = newBranch;
		currentUser = newBranch.getSuperUser();
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
		try {
		currentBranch.createProject(name, description, creationTime, dueTime);
		} catch (Exception e){
			throw new TaskManException(e);
		}
	}
	
	@Override
	public void createProject(String name, String description,
			LocalDateTime dueTime) throws TaskManException {
		if(!currentUserHasPermission(UserPermission.CREATE_PROJECT)) {
			throw new TaskManException(new CredentialException("User has no permission to create projects"));
		}
		try {
		currentBranch.createProject(name, description, this.currentTime, dueTime);
		} catch (Exception e){
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
			Map<ResourceView, Integer> requiredResources,
			TaskView alternativeFor) throws TaskManException {
		
		if(!currentUserHasPermission(UserPermission.CREATE_TASK)) {
			throw new TaskManException(new CredentialException("User has no permission ro create tasks"));
		}
		
		try {
			currentBranch.createTask(project, 
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
			currentBranch.createTask(project, 
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
		try{
			currentBranch.setTaskFinished(project, task, endTime);
		} catch (Exception e){
			throw new TaskManException(e);
		}
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
		try{
				currentBranch.setTaskFailed(project, task, endTime);
		} catch(Exception e){
			throw new TaskManException(e);
		}
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
		try{
		currentBranch.setTaskExecuting(project,task,startTime);
		} catch (Exception e){
			throw new TaskManException(e);
		}
	}

	@Override
	public List<ProjectView> getProjects() {
		return currentBranch.getProjects();
	}
	
	@Override
	public List<ProjectView> getAllProjects() {
		return currentBranch.getAllProjects();
	}

	@Override
	public boolean storeInMemento() {
		if (!currentUserHasPermission(UserPermission.SIMULATE)) {
			return false;
		}
		caretaker.storeInMemento();
		currentBranch.setBufferMode(true);
		return true;
	}

	@Override
	public void revertFromMemento() {
		caretaker.revertFromMemento();
//		currentBranch.clearBuffer();
		currentBranch.setBufferMode(false);
	}

	@Override
	public void discardMemento() {
		caretaker.discardMemento();
		currentBranch.setBufferMode(false);
	}

	@Override
	public ResourceView getCurrentUser() {
		return new ResourceView(currentUser);
	}

	@Override
	public List<ResourceView> getPossibleUsers() {
		return currentBranch.getPossibleUsernames();
	}

	@Override
	public void changeToUser(ResourceView user) throws TaskManException {
		if (user == null) {
			throw new TaskManException(new IllegalArgumentException("Invalid user"));
		}
		try {
		User newUser = currentBranch.getUser(user);
		currentUser = newUser;
		} catch (Exception e) {
			throw new TaskManException(e);
		}
		
	}

	@Override
	public void createResourcePrototype(String name,
			Optional<LocalTime> availabilityStart,
			Optional<LocalTime> availabilityEnd) throws TaskManException{
		try{
			protMan.createResourcePrototype(name,availabilityStart,availabilityEnd);
		} catch(IllegalArgumentException e) {
			throw new TaskManException(e);
		}
	}

	/**
	 * declares a concrete resource for the current branch
	 */
	@Override
	public void declareConcreteResource(String name, ResourceView fromPrototype) {
		try {
		currentBranch.declareConcreteResource(name,fromPrototype);
		}
		catch (Exception e) {
			throw new TaskManException(e);
		}
	}

	@Override
	public void createDeveloper(String name) {
		currentBranch.createDeveloper(name);
	}
	
	@Override
	public void reserveResource(ResourceView resource, ProjectView project, TaskView task, LocalDateTime startTime, LocalDateTime endTime) {
		try {
			currentBranch.reserveResource(resource, project, task, startTime, endTime);
		} catch (Exception e) {
			throw new TaskManException(e);
		}
	}

	@Override
	public List<ResourceView> getDeveloperList() {
		return currentBranch.getDeveloperList();
	}

	@Override
	public HashMap<ProjectView, List<TaskView>> findConflictingPlannings(TaskView task) {
		return currentBranch.findConflictingPlannings(task);
	}

	@Override
	public List<ResourceView> getResourcePrototypes() {
		ImmutableList.Builder<ResourceView> prots = ImmutableList.builder();
		for(Resource prot : protMan.getPrototypes()) {
			prots.add(new ResourceView(prot));
		}
		return prots.build();
	}

	public ResourceView getPrototypeOf(ResourceView resource) {
		return currentBranch.getPrototypeOf(resource);
	}

	@Override
	public List<ResourceView> getConcreteResourcesForPrototype(
			ResourceView resourcePrototype) {
		return currentBranch.getConcreteResourcesForPrototype(resourcePrototype);
	}

	@Override
	public void planTask(ProjectView project, TaskView task,
			LocalDateTime plannedStartTime, List<ResourceView> concRes, 
			List<ResourceView> devs) throws TaskManException{
		if(!currentUserHasPermission(UserPermission.PLAN_TASK)) {
			throw new TaskManException(new CredentialException("User has no permission to plan a task"));
		}
		try {
			currentBranch.planTask(project, task, plannedStartTime, concRes, devs);
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
			currentBranch.planRawTask(project, task, plannedStartTime, concRes, devs);
		} catch (ResourceUnavailableException | IllegalArgumentException
				| IllegalStateException e) {
			throw new TaskManException(e);
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
		return currentBranch.getAllReservations();
	}

	@Override
	public boolean isLoggedIn() {
		return currentUser != null && currentBranch != null;
	}

	@Override
	public List<BranchView> getBranches() {
		Builder<BranchView> views = ImmutableList.builder();
		for (Branch taskMan : branches)
			views.add(new BranchView(taskMan));
		return views.build();
	}

	@Override
	public void selectBranch(BranchView branch) {
		Branch taskMan = unwrapBranchView(branch);
		currentBranch = taskMan;
	}
	
	public String getGeographicLocation() {
		return currentBranch.getGeographicLocation();
	}
	

	@Override
	public void initializeBranch(String geographicLocation) throws IllegalArgumentException {
		this.declareBranch(geographicLocation, protMan.getPrototypes());
	}

	@Override
	public void logout() {
		currentUser = null;
		currentBranch = null;
	}

	@Override
	public void delegateTask(ProjectView project, TaskView task,
			BranchView newBranch) {
		Branch branch = unwrapBranchView(newBranch);
		try {
			currentBranch.delegateTask(project, task, branch);
		}
		catch (Exception e) {
			throw new TaskManException(e);
		}
	}
	
	@Override //voor INIT
	public void delegateTask(ProjectView project, TaskView task,
			BranchView oldBranchView, BranchView newBranchView) {
		Branch oldBranch = unwrapBranchView(oldBranchView);
		Branch newBranch = unwrapBranchView(newBranchView);
		
		oldBranch.delegateTask(project, task, newBranch);
		
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
	private Branch unwrapBranchView(BranchView view) 
			throws TaskManException {
		if(view == null) {
			throw new TaskManException(new IllegalArgumentException("There was no branch to unwrap!"));
		}
		Branch taskMan = view.unwrap();
		if(!branches.contains(taskMan)) {
			throw new TaskManException(new UnexpectedViewContentException("Branch does not belong to this BranchManager!"));
		}
		return taskMan;

	}

	@Override
	public Optional<BranchView> getResponsibleBranch(ProjectView project, TaskView task, BranchView originalBranch) {
		return originalBranch.unwrap().getResponsibleBranch(project, task);
	}
	
	@Override
	public TaskView getDelegatingTask(ProjectView project, TaskView task) {
		return currentBranch.getDelegatingTask(project,task);
	}
	
	/**
	 * Reset the current Branch to an empty state, then reinitialize it using
	 * the data stored in the fileChecker
	 * 
	 * @param systemTime
	 *            | The system time to restore to
	 * @param fileChecker
	 *            | The fileChecker containing the data required for
	 *            reinitialization
	 */
	public void initializeFromMemento(LocalDateTime systemTime, TaskManInitFileChecker fileChecker) {
		currentTime = systemTime;
		
		currentBranch.flush(protMan.getPrototypes());
		
		currentUser = currentBranch.getSuperUser();
		
		Main.setupBranch(this, fileChecker, branches.indexOf(currentBranch));
	}

	@Override
	public void addRequirementsToResource(List<ResourceView> resourcesToAdd,
			ResourceView prototype) 
			throws IllegalArgumentException {
		try {
			protMan.addRequirementsToResource(resourcesToAdd, prototype);
		} catch(IllegalArgumentException e) {
			throw new TaskManException(e);
		}
		
	}

	@Override
	public void addConflictsToResource(List<ResourceView> resourcesToAdd,
			ResourceView prototype) 
			throws IllegalArgumentException {
		try {
			protMan.addConflictsToResource(resourcesToAdd, prototype);
		} catch(IllegalArgumentException e) {
			throw new TaskManException(e);
		}
		
	}
	
	/**
	 * @return the original proxies of this branch representative
	 */
	public Map<Task, OriginalTaskProxy> getOriginalProxies() {
		return currentBranch.getOriginalProxies();
	}
	
	/**
	 * @return the delegating proxies of this branch representative
	 */
	public Map<Task, DelegatingTaskProxy> getDelegatingProxies() {
		return currentBranch.getDelegatingProxies();
	}

	/**
	 * Offer new task and (original) proxy pairings to the representative
	 * 
	 * @param proxies
	 *            | The new task-proxy pairings present in the system
	 */
	public void offerOriginalTaskProxies(Map<Task, OriginalTaskProxy> proxies) {
		currentBranch.offerOriginalTaskProxies(proxies);
	}

	/**
	 * Offer new task and (delegating) proxy pairings to the representative
	 * 
	 * @param proxies
	 *            | The new task-proxy pairings present in the system
	 */
	public void offerDelegatingTaskProxies(
			Map<Task, DelegatingTaskProxy> proxies) {
		currentBranch.offerDelegatingTaskProxies(proxies);	
	}
}