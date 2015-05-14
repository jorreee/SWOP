package company;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.security.auth.login.CredentialException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import userInterface.IFacade;
import company.caretaker.TaskManCaretaker;
import company.taskMan.ProjectView;
import company.taskMan.TaskMan;
import company.taskMan.project.TaskView;
import company.taskMan.resource.Reservation;
import company.taskMan.resource.ResourceView;
import company.taskMan.resource.user.User;
import company.taskMan.resource.user.UserPermission;

//TODO list of prototypes in branchMa
public class BranchManager implements IFacade {
	private List<TaskMan> taskMen;
	private TaskMan currentTaskMan;
	private Delegator delegator;
	private LocalDateTime currentTime;
	private User currentUser;
	private final TaskManCaretaker caretaker;

	
	public BranchManager(LocalDateTime time) {
		taskMen = new ArrayList<>();
		delegator = new Delegator();
		caretaker = new TaskManCaretaker(this);
		currentUser = null;
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
	 */
	private void declareTaskMan(String location){
		TaskMan newTaskMan = new TaskMan(location);
		taskMen.add(newTaskMan);
		currentTaskMan = newTaskMan;
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
	public void createProject(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime) throws CredentialException {
		if(!currentUserHasPermission(UserPermission.CREATE_PROJECT)) {
			throw new CredentialException("User has no permission to create projects");
		}
		currentTaskMan.createProject(name, description, creationTime, dueTime);
	}
	
	@Override
	public void createProject(String name, String description,
			LocalDateTime dueTime) throws CredentialException {
		if(!currentUserHasPermission(UserPermission.CREATE_PROJECT)) {
			throw new CredentialException("User has no permission to create projects");
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
			TaskView alternativeFor) throws CredentialException{
		
		if(!currentUserHasPermission(UserPermission.CREATE_TASK)) {
			throw new CredentialException("User has no permission ro create tasks");
		}
		
		currentTaskMan.createTask(project, 
				description, 
				estimatedDuration, 
				acceptableDeviation, 
				prerequisiteTasks, 
				requiredResources,
				alternativeFor);
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
			List<ResourceView> plannedDevelopers)  throws CredentialException{
		
		if(!currentUserHasPermission(UserPermission.CREATE_TASK)) {
			throw new CredentialException("User has no permission to create tasks");
		}
		
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
			throws IllegalArgumentException, CredentialException{
		if(!currentUserHasPermission(UserPermission.ADVANCE_TIME))
			throw new CredentialException("The user has no permission to advance the time");
		if (time == null)
			throw new IllegalArgumentException("Time is null");
		if (time.isAfter(currentTime)) {
			currentTime = time;
		} else
			throw new IllegalArgumentException("The given time is before the currentTime");

	}

	@Override
	public void setTaskFinished(ProjectView project, TaskView task,
			LocalDateTime endTime) throws CredentialException, IllegalArgumentException{
		if(!currentUserHasPermission(UserPermission.UPDATE_TASK)) {
			throw new CredentialException("The user has no permission to update the taskstatus");
		}
		if (endTime == null) { // || startTime == null) {
			throw new IllegalArgumentException("The endtime is null");
		}
		if (endTime.isAfter(currentTime)) {
			throw new IllegalArgumentException("The endtime is after the currentTime");
		}
		currentTaskMan.setTaskFinished(project, task, endTime);
	}

	@Override
	public void setTaskFailed(ProjectView project, TaskView task,
			LocalDateTime endTime) throws CredentialException, IllegalArgumentException {
		if(!currentUserHasPermission(UserPermission.UPDATE_TASK)) {
			throw new CredentialException("The user has no permission to update the taskstatus");
		}
		if (endTime == null) { // || startTime == null) {
			throw new IllegalArgumentException("The endtime is null");
		}
		if (endTime.isAfter(currentTime)) {
			throw new IllegalArgumentException("The endtime is after the currentTime");
		}
		currentTaskMan.setTaskFailed(project, task, endTime);
	}
	
	@Override
	public void setTaskExecuting(ProjectView project, TaskView task,
			LocalDateTime startTime)throws CredentialException, IllegalArgumentException{
		if(!currentUserHasPermission(UserPermission.UPDATE_TASK)) {
			throw new CredentialException("The user has no permission to update the taskstatus");
		}
		if (startTime == null) {
			throw new IllegalArgumentException("The startTime is null");
		}
		if (startTime.isAfter(currentTime)) {
			throw new IllegalArgumentException("The startTime is after the currentTime");
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
	public boolean revertFromMemento() {
		return  caretaker.revertFromMemento();
	}

	@Override
	public boolean discardMemento() {
		return caretaker.discardMemento();
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
	public void changeToUser(ResourceView user) throws IllegalArgumentException{
		User newUser = currentTaskMan.getUser(user);
		if (user == null) {
			throw new IllegalArgumentException("Invalid user");
		} else {
			currentUser = newUser;
		}
	}

	@Override
	public void createResourcePrototype(String name,
			Optional<LocalTime> availabilityStart,
			Optional<LocalTime> availabilityEnd) {
		currentTaskMan.createResourcePrototype(name,availabilityStart,availabilityEnd);
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
		currentTaskMan.reserveResource(resource, project, task, startTime, endTime);
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
		return currentTaskMan.getResourcePrototypes();
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
			List<ResourceView> devs) throws CredentialException{
		if(!currentUserHasPermission(UserPermission.PLAN_TASK)) {
			throw new CredentialException("User has no permission to plan a task");
		}
		currentTaskMan.planTask(project, task, plannedStartTime, concRes, devs);
	}
	
	@Override
	public void planRawTask(ProjectView project, TaskView task,
			LocalDateTime plannedStartTime, List<ResourceView> concRes, 
			List<ResourceView> devs) throws CredentialException{
		if(!currentUserHasPermission(UserPermission.PLAN_TASK)) {
			throw new CredentialException("User has no permission to plan a task");
		}
		currentTaskMan.planRawTask(project, task, plannedStartTime, concRes, devs);
	}

	@Override
	public void addRequirementsToResource(List<ResourceView> resourcesToAdd,
			ResourceView prototype) {
		currentTaskMan.addRequirementsToResource(resourcesToAdd, prototype);
	}

	@Override
	public void addConflictsToResource(List<ResourceView> resourcesToAdd,
			ResourceView prototype) {
		currentTaskMan.addConflictsToResource(resourcesToAdd, prototype);
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
		return currentUser == null && currentTaskMan == null;
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
	public void initializeBranch(String geographicLocation) {
		this.declareTaskMan(geographicLocation);
	}

	@Override
	public void logout() {
		currentUser = null;
		currentTaskMan = null;
	}

	@Override
	public void delegateTask(ProjectView project, TaskView task,
			BranchView newBranch) {
		// TODO Auto-generated method stub
		
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
			throws NullPointerException, IllegalArgumentException{
		if(view == null) {
			throw new NullPointerException("There was no branch to unwrap!");
		}
		TaskMan taskMan = view.unwrap();
		if(!taskMen.contains(taskMan))
			throw new IllegalArgumentException("Branch does not belong to this BranchManager!");
		return taskMan;

	}

	@Override
	public BranchView getResponsibleBranch(TaskView task) {
		// TODO Auto-generated method stub
		return null;
	}
	
}